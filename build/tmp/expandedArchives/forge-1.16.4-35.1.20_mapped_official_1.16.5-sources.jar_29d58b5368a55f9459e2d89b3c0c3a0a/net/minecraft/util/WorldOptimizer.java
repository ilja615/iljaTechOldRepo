package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldOptimizer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private final ImmutableSet<RegistryKey<World>> levels;
   private final boolean eraseCache;
   private final SaveFormat.LevelSave levelStorage;
   private final Thread thread;
   private final DataFixer dataFixer;
   private volatile boolean running = true;
   private volatile boolean finished;
   private volatile float progress;
   private volatile int totalChunks;
   private volatile int converted;
   private volatile int skipped;
   private final Object2FloatMap<RegistryKey<World>> progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(Util.identityStrategy()));
   private volatile ITextComponent status = new TranslationTextComponent("optimizeWorld.stage.counting");
   private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final DimensionSavedDataManager overworldDataStorage;

   public WorldOptimizer(SaveFormat.LevelSave p_i231486_1_, DataFixer p_i231486_2_, ImmutableSet<RegistryKey<World>> p_i231486_3_, boolean p_i231486_4_) {
      this.levels = p_i231486_3_;
      this.eraseCache = p_i231486_4_;
      this.dataFixer = p_i231486_2_;
      this.levelStorage = p_i231486_1_;
      this.overworldDataStorage = new DimensionSavedDataManager(new File(this.levelStorage.getDimensionPath(World.OVERWORLD), "data"), p_i231486_2_);
      this.thread = THREAD_FACTORY.newThread(this::work);
      this.thread.setUncaughtExceptionHandler((p_219956_1_, p_219956_2_) -> {
         LOGGER.error("Error upgrading world", p_219956_2_);
         this.status = new TranslationTextComponent("optimizeWorld.stage.failed");
         this.finished = true;
      });
      this.thread.start();
   }

   public void cancel() {
      this.running = false;

      try {
         this.thread.join();
      } catch (InterruptedException interruptedexception) {
      }

   }

   private void work() {
      this.totalChunks = 0;
      Builder<RegistryKey<World>, ListIterator<ChunkPos>> builder = ImmutableMap.builder();

      for(RegistryKey<World> registrykey : this.levels) {
         List<ChunkPos> list = this.getAllChunkPos(registrykey);
         builder.put(registrykey, list.listIterator());
         this.totalChunks += list.size();
      }

      if (this.totalChunks == 0) {
         this.finished = true;
      } else {
         float f1 = (float)this.totalChunks;
         ImmutableMap<RegistryKey<World>, ListIterator<ChunkPos>> immutablemap = builder.build();
         Builder<RegistryKey<World>, ChunkLoader> builder1 = ImmutableMap.builder();

         for(RegistryKey<World> registrykey1 : this.levels) {
            File file1 = this.levelStorage.getDimensionPath(registrykey1);
            builder1.put(registrykey1, new ChunkLoader(new File(file1, "region"), this.dataFixer, true));
         }

         ImmutableMap<RegistryKey<World>, ChunkLoader> immutablemap1 = builder1.build();
         long i = Util.getMillis();
         this.status = new TranslationTextComponent("optimizeWorld.stage.upgrading");

         while(this.running) {
            boolean flag = false;
            float f = 0.0F;

            for(RegistryKey<World> registrykey2 : this.levels) {
               ListIterator<ChunkPos> listiterator = immutablemap.get(registrykey2);
               ChunkLoader chunkloader = immutablemap1.get(registrykey2);
               if (listiterator.hasNext()) {
                  ChunkPos chunkpos = listiterator.next();
                  boolean flag1 = false;

                  try {
                     CompoundNBT compoundnbt = chunkloader.read(chunkpos);
                     if (compoundnbt != null) {
                        int j = ChunkLoader.getVersion(compoundnbt);
                        CompoundNBT compoundnbt1 = chunkloader.upgradeChunkTag(registrykey2, () -> {
                           return this.overworldDataStorage;
                        }, compoundnbt);
                        CompoundNBT compoundnbt2 = compoundnbt1.getCompound("Level");
                        ChunkPos chunkpos1 = new ChunkPos(compoundnbt2.getInt("xPos"), compoundnbt2.getInt("zPos"));
                        if (!chunkpos1.equals(chunkpos)) {
                           LOGGER.warn("Chunk {} has invalid position {}", chunkpos, chunkpos1);
                        }

                        boolean flag2 = j < SharedConstants.getCurrentVersion().getWorldVersion();
                        if (this.eraseCache) {
                           flag2 = flag2 || compoundnbt2.contains("Heightmaps");
                           compoundnbt2.remove("Heightmaps");
                           flag2 = flag2 || compoundnbt2.contains("isLightOn");
                           compoundnbt2.remove("isLightOn");
                        }

                        if (flag2) {
                           chunkloader.write(chunkpos, compoundnbt1);
                           flag1 = true;
                        }
                     }
                  } catch (ReportedException reportedexception) {
                     Throwable throwable = reportedexception.getCause();
                     if (!(throwable instanceof IOException)) {
                        throw reportedexception;
                     }

                     LOGGER.error("Error upgrading chunk {}", chunkpos, throwable);
                  } catch (IOException ioexception1) {
                     LOGGER.error("Error upgrading chunk {}", chunkpos, ioexception1);
                  }

                  if (flag1) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  flag = true;
               }

               float f2 = (float)listiterator.nextIndex() / f1;
               this.progressMap.put(registrykey2, f2);
               f += f2;
            }

            this.progress = f;
            if (!flag) {
               this.running = false;
            }
         }

         this.status = new TranslationTextComponent("optimizeWorld.stage.finished");

         for(ChunkLoader chunkloader1 : immutablemap1.values()) {
            try {
               chunkloader1.close();
            } catch (IOException ioexception) {
               LOGGER.error("Error upgrading chunk", (Throwable)ioexception);
            }
         }

         this.overworldDataStorage.save();
         i = Util.getMillis() - i;
         LOGGER.info("World optimizaton finished after {} ms", (long)i);
         this.finished = true;
      }
   }

   private List<ChunkPos> getAllChunkPos(RegistryKey<World> p_233532_1_) {
      File file1 = this.levelStorage.getDimensionPath(p_233532_1_);
      File file2 = new File(file1, "region");
      File[] afile = file2.listFiles((p_219954_0_, p_219954_1_) -> {
         return p_219954_1_.endsWith(".mca");
      });
      if (afile == null) {
         return ImmutableList.of();
      } else {
         List<ChunkPos> list = Lists.newArrayList();

         for(File file3 : afile) {
            Matcher matcher = REGEX.matcher(file3.getName());
            if (matcher.matches()) {
               int i = Integer.parseInt(matcher.group(1)) << 5;
               int j = Integer.parseInt(matcher.group(2)) << 5;

               try (RegionFile regionfile = new RegionFile(file3, file2, true)) {
                  for(int k = 0; k < 32; ++k) {
                     for(int l = 0; l < 32; ++l) {
                        ChunkPos chunkpos = new ChunkPos(k + i, l + j);
                        if (regionfile.doesChunkExist(chunkpos)) {
                           list.add(chunkpos);
                        }
                     }
                  }
               } catch (Throwable throwable) {
               }
            }
         }

         return list;
      }
   }

   public boolean isFinished() {
      return this.finished;
   }

   @OnlyIn(Dist.CLIENT)
   public ImmutableSet<RegistryKey<World>> levels() {
      return this.levels;
   }

   @OnlyIn(Dist.CLIENT)
   public float dimensionProgress(RegistryKey<World> p_233531_1_) {
      return this.progressMap.getFloat(p_233531_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getProgress() {
      return this.progress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public ITextComponent getStatus() {
      return this.status;
   }
}
