package net.minecraft.world.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionSectionCache<R> implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IOWorker worker;
   private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap<>();
   private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
   private final Function<Runnable, Codec<R>> codec;
   private final Function<Runnable, R> factory;
   private final DataFixer fixerUpper;
   private final DefaultTypeReferences type;

   public RegionSectionCache(File p_i231897_1_, Function<Runnable, Codec<R>> p_i231897_2_, Function<Runnable, R> p_i231897_3_, DataFixer p_i231897_4_, DefaultTypeReferences p_i231897_5_, boolean p_i231897_6_) {
      this.codec = p_i231897_2_;
      this.factory = p_i231897_3_;
      this.fixerUpper = p_i231897_4_;
      this.type = p_i231897_5_;
      this.worker = new IOWorker(p_i231897_1_, p_i231897_6_, p_i231897_1_.getName());
   }

   protected void tick(BooleanSupplier p_219115_1_) {
      while(!this.dirty.isEmpty() && p_219115_1_.getAsBoolean()) {
         ChunkPos chunkpos = SectionPos.of(this.dirty.firstLong()).chunk();
         this.writeColumn(chunkpos);
      }

   }

   @Nullable
   protected Optional<R> get(long p_219106_1_) {
      return this.storage.get(p_219106_1_);
   }

   protected Optional<R> getOrLoad(long p_219113_1_) {
      SectionPos sectionpos = SectionPos.of(p_219113_1_);
      if (this.outsideStoredRange(sectionpos)) {
         return Optional.empty();
      } else {
         Optional<R> optional = this.get(p_219113_1_);
         if (optional != null) {
            return optional;
         } else {
            this.readColumn(sectionpos.chunk());
            optional = this.get(p_219113_1_);
            if (optional == null) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            } else {
               return optional;
            }
         }
      }
   }

   protected boolean outsideStoredRange(SectionPos p_219114_1_) {
      return World.isOutsideBuildHeight(SectionPos.sectionToBlockCoord(p_219114_1_.y()));
   }

   protected R getOrCreate(long p_235995_1_) {
      Optional<R> optional = this.getOrLoad(p_235995_1_);
      if (optional.isPresent()) {
         return optional.get();
      } else {
         R r = this.factory.apply(() -> {
            this.setDirty(p_235995_1_);
         });
         this.storage.put(p_235995_1_, Optional.of(r));
         return r;
      }
   }

   private void readColumn(ChunkPos p_219107_1_) {
      this.readColumn(p_219107_1_, NBTDynamicOps.INSTANCE, this.tryRead(p_219107_1_));
   }

   @Nullable
   private CompoundNBT tryRead(ChunkPos p_223138_1_) {
      try {
         return this.worker.load(p_223138_1_);
      } catch (IOException ioexception) {
         LOGGER.error("Error reading chunk {} data from disk", p_223138_1_, ioexception);
         return null;
      }
   }

   private <T> void readColumn(ChunkPos p_235992_1_, DynamicOps<T> p_235992_2_, @Nullable T p_235992_3_) {
      if (p_235992_3_ == null) {
         for(int i = 0; i < 16; ++i) {
            this.storage.put(SectionPos.of(p_235992_1_, i).asLong(), Optional.empty());
         }
      } else {
         Dynamic<T> dynamic1 = new Dynamic<>(p_235992_2_, p_235992_3_);
         int j = getVersion(dynamic1);
         int k = SharedConstants.getCurrentVersion().getWorldVersion();
         boolean flag = j != k;
         Dynamic<T> dynamic = this.fixerUpper.update(this.type.getType(), dynamic1, j, k);
         OptionalDynamic<T> optionaldynamic = dynamic.get("Sections");

         for(int l = 0; l < 16; ++l) {
            long i1 = SectionPos.of(p_235992_1_, l).asLong();
            Optional<R> optional = optionaldynamic.get(Integer.toString(l)).result().flatMap((p_235989_3_) -> {
               return this.codec.apply(() -> {
                  this.setDirty(i1);
               }).parse(p_235989_3_).resultOrPartial(LOGGER::error);
            });
            this.storage.put(i1, optional);
            optional.ifPresent((p_235990_4_) -> {
               this.onSectionLoad(i1);
               if (flag) {
                  this.setDirty(i1);
               }

            });
         }
      }

   }

   private void writeColumn(ChunkPos p_219117_1_) {
      Dynamic<INBT> dynamic = this.writeColumn(p_219117_1_, NBTDynamicOps.INSTANCE);
      INBT inbt = dynamic.getValue();
      if (inbt instanceof CompoundNBT) {
         this.worker.store(p_219117_1_, (CompoundNBT)inbt);
      } else {
         LOGGER.error("Expected compound tag, got {}", (Object)inbt);
      }

   }

   private <T> Dynamic<T> writeColumn(ChunkPos p_235991_1_, DynamicOps<T> p_235991_2_) {
      Map<T, T> map = Maps.newHashMap();

      for(int i = 0; i < 16; ++i) {
         long j = SectionPos.of(p_235991_1_, i).asLong();
         this.dirty.remove(j);
         Optional<R> optional = this.storage.get(j);
         if (optional != null && optional.isPresent()) {
            DataResult<T> dataresult = this.codec.apply(() -> {
               this.setDirty(j);
            }).encodeStart(p_235991_2_, optional.get());
            String s = Integer.toString(i);
            dataresult.resultOrPartial(LOGGER::error).ifPresent((p_235994_3_) -> {
               map.put(p_235991_2_.createString(s), p_235994_3_);
            });
         }
      }

      return new Dynamic<>(p_235991_2_, p_235991_2_.createMap(ImmutableMap.of(p_235991_2_.createString("Sections"), p_235991_2_.createMap(map), p_235991_2_.createString("DataVersion"), p_235991_2_.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
   }

   protected void onSectionLoad(long p_219111_1_) {
   }

   protected void setDirty(long p_219116_1_) {
      Optional<R> optional = this.storage.get(p_219116_1_);
      if (optional != null && optional.isPresent()) {
         this.dirty.add(p_219116_1_);
      } else {
         LOGGER.warn("No data for position: {}", (Object)SectionPos.of(p_219116_1_));
      }
   }

   private static int getVersion(Dynamic<?> p_235993_0_) {
      return p_235993_0_.get("DataVersion").asInt(1945);
   }

   public void flush(ChunkPos p_219112_1_) {
      if (!this.dirty.isEmpty()) {
         for(int i = 0; i < 16; ++i) {
            long j = SectionPos.of(p_219112_1_, i).asLong();
            if (this.dirty.contains(j)) {
               this.writeColumn(p_219112_1_);
               return;
            }
         }
      }

   }

   public void close() throws IOException {
      this.worker.close();
   }
}
