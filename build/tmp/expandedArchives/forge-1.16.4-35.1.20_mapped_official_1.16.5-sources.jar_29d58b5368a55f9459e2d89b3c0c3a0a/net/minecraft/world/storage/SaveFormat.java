package net.minecraft.world.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.server.SessionLockManager;
import net.minecraft.util.FileUtil;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateTimeFormatter FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
   private final Path baseDir;
   private final Path backupDir;
   private final DataFixer fixerUpper;

   public SaveFormat(Path p_i51277_1_, Path p_i51277_2_, DataFixer p_i51277_3_) {
      this.fixerUpper = p_i51277_3_;

      try {
         Files.createDirectories(Files.exists(p_i51277_1_) ? p_i51277_1_.toRealPath() : p_i51277_1_);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      this.baseDir = p_i51277_1_;
      this.backupDir = p_i51277_2_;
   }

   public static SaveFormat createDefault(Path p_237269_0_) {
      return new SaveFormat(p_237269_0_, p_237269_0_.resolve("../backups"), DataFixesManager.getDataFixer());
   }

   private static <T> Pair<DimensionGeneratorSettings, Lifecycle> readWorldGenSettings(Dynamic<T> p_237259_0_, DataFixer p_237259_1_, int p_237259_2_) {
      Dynamic<T> dynamic = p_237259_0_.get("WorldGenSettings").orElseEmptyMap();

      for(String s : OLD_SETTINGS_KEYS) {
         Optional<? extends Dynamic<?>> optional = p_237259_0_.get(s).result();
         if (optional.isPresent()) {
            dynamic = dynamic.set(s, optional.get());
         }
      }

      Dynamic<T> dynamic1 = p_237259_1_.update(TypeReferences.WORLD_GEN_SETTINGS, dynamic, p_237259_2_, SharedConstants.getCurrentVersion().getWorldVersion());
      DataResult<DimensionGeneratorSettings> dataresult = DimensionGeneratorSettings.CODEC.parse(dynamic1);
      return Pair.of(dataresult.resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error)).orElseGet(() -> {
         Registry<DimensionType> registry = RegistryLookupCodec.create(Registry.DIMENSION_TYPE_REGISTRY).codec().parse(dynamic1).resultOrPartial(Util.prefix("Dimension type registry: ", LOGGER::error)).orElseThrow(() -> {
            return new IllegalStateException("Failed to get dimension registry");
         });
         Registry<Biome> registry1 = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).codec().parse(dynamic1).resultOrPartial(Util.prefix("Biome registry: ", LOGGER::error)).orElseThrow(() -> {
            return new IllegalStateException("Failed to get biome registry");
         });
         Registry<DimensionSettings> registry2 = RegistryLookupCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).codec().parse(dynamic1).resultOrPartial(Util.prefix("Noise settings registry: ", LOGGER::error)).orElseThrow(() -> {
            return new IllegalStateException("Failed to get noise settings registry");
         });
         return DimensionGeneratorSettings.makeDefault(registry, registry1, registry2);
      }), dataresult.lifecycle());
   }

   private static DatapackCodec readDataPackConfig(Dynamic<?> p_237258_0_) {
      return DatapackCodec.CODEC.parse(p_237258_0_).resultOrPartial(LOGGER::error).orElse(DatapackCodec.DEFAULT);
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getLevelList() throws AnvilConverterException {
      if (!Files.isDirectory(this.baseDir)) {
         throw new AnvilConverterException((new TranslationTextComponent("selectWorld.load_folder_access")).getString());
      } else {
         List<WorldSummary> list = Lists.newArrayList();
         File[] afile = this.baseDir.toFile().listFiles();

         for(File file1 : afile) {
            if (file1.isDirectory()) {
               boolean flag;
               try {
                  flag = SessionLockManager.isLocked(file1.toPath());
               } catch (Exception exception) {
                  LOGGER.warn("Failed to read {} lock", file1, exception);
                  continue;
               }

               WorldSummary worldsummary = this.readLevelData(file1, this.levelSummaryReader(file1, flag));
               if (worldsummary != null) {
                  list.add(worldsummary);
               }
            }
         }

         return list;
      }
   }

   private int getStorageVersion() {
      return 19133;
   }

   @Nullable
   private <T> T readLevelData(File p_237266_1_, BiFunction<File, DataFixer, T> p_237266_2_) {
      if (!p_237266_1_.exists()) {
         return (T)null;
      } else {
         File file1 = new File(p_237266_1_, "level.dat");
         if (file1.exists()) {
            T t = p_237266_2_.apply(file1, this.fixerUpper);
            if (t != null) {
               return t;
            }
         }

         file1 = new File(p_237266_1_, "level.dat_old");
         return (T)(file1.exists() ? p_237266_2_.apply(file1, this.fixerUpper) : null);
      }
   }

   @Nullable
   private static DatapackCodec getDataPacks(File p_237272_0_, DataFixer p_237272_1_) {
      try {
         CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(p_237272_0_);
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
         compoundnbt1.remove("Player");
         int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
         Dynamic<INBT> dynamic = p_237272_1_.update(DefaultTypeReferences.LEVEL.getType(), new Dynamic<>(NBTDynamicOps.INSTANCE, compoundnbt1), i, SharedConstants.getCurrentVersion().getWorldVersion());
         return dynamic.get("DataPacks").result().map(SaveFormat::readDataPackConfig).orElse(DatapackCodec.DEFAULT);
      } catch (Exception exception) {
         LOGGER.error("Exception reading {}", p_237272_0_, exception);
         return null;
      }
   }

   private static BiFunction<File, DataFixer, ServerWorldInfo> getLevelData(DynamicOps<INBT> p_237270_0_, DatapackCodec p_237270_1_) {
       return getReader(p_237270_0_, p_237270_1_, null);
   }

   private static BiFunction<File, DataFixer, ServerWorldInfo> getReader(DynamicOps<INBT> p_237270_0_, DatapackCodec p_237270_1_, @Nullable LevelSave levelSave) {
      return (p_242976_2_, p_242976_3_) -> {
         try {
            CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(p_242976_2_);
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
            CompoundNBT compoundnbt2 = compoundnbt1.contains("Player", 10) ? compoundnbt1.getCompound("Player") : null;
            compoundnbt1.remove("Player");
            int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
            Dynamic<INBT> dynamic = p_242976_3_.update(DefaultTypeReferences.LEVEL.getType(), new Dynamic<>(p_237270_0_, compoundnbt1), i, SharedConstants.getCurrentVersion().getWorldVersion());
            Pair<DimensionGeneratorSettings, Lifecycle> pair = readWorldGenSettings(dynamic, p_242976_3_, i);
            VersionData versiondata = VersionData.parse(dynamic);
            WorldSettings worldsettings = WorldSettings.parse(dynamic, p_237270_1_);
            ServerWorldInfo info = ServerWorldInfo.parse(dynamic, p_242976_3_, i, compoundnbt2, worldsettings, versiondata, pair.getFirst(), pair.getSecond());
            if (levelSave != null)
                net.minecraftforge.fml.WorldPersistenceHooks.handleWorldDataLoad(levelSave, info, compoundnbt);
            return info;
         } catch (Exception exception) {
            LOGGER.error("Exception reading {}", p_242976_2_, exception);
            return null;
         }
      };
   }

   private BiFunction<File, DataFixer, WorldSummary> levelSummaryReader(File p_237267_1_, boolean p_237267_2_) {
      return (p_242977_3_, p_242977_4_) -> {
         try {
            CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(p_242977_3_);
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
            compoundnbt1.remove("Player");
            int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
            Dynamic<INBT> dynamic = p_242977_4_.update(DefaultTypeReferences.LEVEL.getType(), new Dynamic<>(NBTDynamicOps.INSTANCE, compoundnbt1), i, SharedConstants.getCurrentVersion().getWorldVersion());
            VersionData versiondata = VersionData.parse(dynamic);
            int j = versiondata.levelDataVersion();
            if (j != 19132 && j != 19133) {
               return null;
            } else {
               boolean flag = j != this.getStorageVersion();
               File file1 = new File(p_237267_1_, "icon.png");
               DatapackCodec datapackcodec = dynamic.get("DataPacks").result().map(SaveFormat::readDataPackConfig).orElse(DatapackCodec.DEFAULT);
               WorldSettings worldsettings = WorldSettings.parse(dynamic, datapackcodec);
               return new WorldSummary(worldsettings, versiondata, p_237267_1_.getName(), flag, p_237267_2_, file1);
            }
         } catch (Exception exception) {
            LOGGER.error("Exception reading {}", p_242977_3_, exception);
            return null;
         }
      };
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNewLevelIdAcceptable(String p_207742_1_) {
      try {
         Path path = this.baseDir.resolve(p_207742_1_);
         Files.createDirectory(path);
         Files.deleteIfExists(path);
         return true;
      } catch (IOException ioexception) {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean levelExists(String p_90033_1_) {
      return Files.isDirectory(this.baseDir.resolve(p_90033_1_));
   }

   @OnlyIn(Dist.CLIENT)
   public Path getBaseDir() {
      return this.baseDir;
   }

   @OnlyIn(Dist.CLIENT)
   public Path getBackupPath() {
      return this.backupDir;
   }

   public SaveFormat.LevelSave createAccess(String p_237274_1_) throws IOException {
      return new SaveFormat.LevelSave(p_237274_1_);
   }

   public class LevelSave implements AutoCloseable {
      private final SessionLockManager lock;
      private final Path levelPath;
      private final String levelId;
      private final Map<FolderName, Path> resources = Maps.newHashMap();

      public LevelSave(String p_i232152_2_) throws IOException {
         this.levelId = p_i232152_2_;
         this.levelPath = SaveFormat.this.baseDir.resolve(p_i232152_2_);
         this.lock = SessionLockManager.create(this.levelPath);
      }

      public String getLevelId() {
         return this.levelId;
      }

      public Path getLevelPath(FolderName p_237285_1_) {
         return this.resources.computeIfAbsent(p_237285_1_, (p_237293_1_) -> {
            return this.levelPath.resolve(p_237293_1_.getId());
         });
      }

      public File getDimensionPath(RegistryKey<World> p_237291_1_) {
         return DimensionType.getStorageFolder(p_237291_1_, this.levelPath.toFile());
      }

      private void checkLock() {
         if (!this.lock.isValid()) {
            throw new IllegalStateException("Lock is no longer valid");
         }
      }

      public PlayerData createPlayerStorage() {
         this.checkLock();
         return new PlayerData(this, SaveFormat.this.fixerUpper);
      }

      public boolean requiresConversion() {
         WorldSummary worldsummary = this.getSummary();
         return worldsummary != null && worldsummary.levelVersion().levelDataVersion() != SaveFormat.this.getStorageVersion();
      }

      public boolean convertLevel(IProgressUpdate p_237283_1_) {
         this.checkLock();
         return AnvilSaveConverter.convertLevel(this, p_237283_1_);
      }

      @Nullable
      public WorldSummary getSummary() {
         this.checkLock();
         return SaveFormat.this.readLevelData(this.levelPath.toFile(), SaveFormat.this.levelSummaryReader(this.levelPath.toFile(), false));
      }

      @Nullable
      public IServerConfiguration getDataTag(DynamicOps<INBT> p_237284_1_, DatapackCodec p_237284_2_) {
         this.checkLock();
         return SaveFormat.this.readLevelData(this.levelPath.toFile(), SaveFormat.getReader(p_237284_1_, p_237284_2_, this));
      }

      @Nullable
      public DatapackCodec getDataPacks() {
         this.checkLock();
         return SaveFormat.this.readLevelData(this.levelPath.toFile(), (p_237289_0_, p_237289_1_) -> {
            return SaveFormat.getDataPacks(p_237289_0_, p_237289_1_);
         });
      }

      public void saveDataTag(DynamicRegistries p_237287_1_, IServerConfiguration p_237287_2_) {
         this.saveDataTag(p_237287_1_, p_237287_2_, (CompoundNBT)null);
      }

      public void saveDataTag(DynamicRegistries p_237288_1_, IServerConfiguration p_237288_2_, @Nullable CompoundNBT p_237288_3_) {
         File file1 = this.levelPath.toFile();
         CompoundNBT compoundnbt = p_237288_2_.createTag(p_237288_1_, p_237288_3_);
         CompoundNBT compoundnbt1 = new CompoundNBT();
         compoundnbt1.put("Data", compoundnbt);

         net.minecraftforge.fml.WorldPersistenceHooks.handleWorldDataSave(this, p_237288_2_, compoundnbt1);

         try {
            File file2 = File.createTempFile("level", ".dat", file1);
            CompressedStreamTools.writeCompressed(compoundnbt1, file2);
            File file3 = new File(file1, "level.dat_old");
            File file4 = new File(file1, "level.dat");
            Util.safeReplaceFile(file4, file2, file3);
         } catch (Exception exception) {
            SaveFormat.LOGGER.error("Failed to save level {}", file1, exception);
         }

      }

      public File getIconFile() {
         this.checkLock();
         return this.levelPath.resolve("icon.png").toFile();
      }

      public Path getWorldDir() {
          return levelPath;
      }

      @OnlyIn(Dist.CLIENT)
      public void deleteLevel() throws IOException {
         this.checkLock();
         final Path path = this.levelPath.resolve("session.lock");

         for(int i = 1; i <= 5; ++i) {
            SaveFormat.LOGGER.info("Attempt {}...", (int)i);

            try {
               Files.walkFileTree(this.levelPath, new SimpleFileVisitor<Path>() {
                  public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException {
                     if (!p_visitFile_1_.equals(path)) {
                        SaveFormat.LOGGER.debug("Deleting {}", (Object)p_visitFile_1_);
                        Files.delete(p_visitFile_1_);
                     }

                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult postVisitDirectory(Path p_postVisitDirectory_1_, IOException p_postVisitDirectory_2_) throws IOException {
                     if (p_postVisitDirectory_2_ != null) {
                        throw p_postVisitDirectory_2_;
                     } else {
                        if (p_postVisitDirectory_1_.equals(LevelSave.this.levelPath)) {
                           LevelSave.this.lock.close();
                           Files.deleteIfExists(path);
                        }

                        Files.delete(p_postVisitDirectory_1_);
                        return FileVisitResult.CONTINUE;
                     }
                  }
               });
               break;
            } catch (IOException ioexception) {
               if (i >= 5) {
                  throw ioexception;
               }

               SaveFormat.LOGGER.warn("Failed to delete {}", this.levelPath, ioexception);

               try {
                  Thread.sleep(500L);
               } catch (InterruptedException interruptedexception) {
               }
            }
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void renameLevel(String p_237290_1_) throws IOException {
         this.checkLock();
         File file1 = new File(SaveFormat.this.baseDir.toFile(), this.levelId);
         if (file1.exists()) {
            File file2 = new File(file1, "level.dat");
            if (file2.exists()) {
               CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(file2);
               CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
               compoundnbt1.putString("LevelName", p_237290_1_);
               CompressedStreamTools.writeCompressed(compoundnbt, file2);
            }

         }
      }

      @OnlyIn(Dist.CLIENT)
      public long makeWorldBackup() throws IOException {
         this.checkLock();
         String s = LocalDateTime.now().format(SaveFormat.FORMATTER) + "_" + this.levelId;
         Path path = SaveFormat.this.getBackupPath();

         try {
            Files.createDirectories(Files.exists(path) ? path.toRealPath() : path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Path path1 = path.resolve(FileUtil.findAvailableName(path, s, ".zip"));

         try (final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path1)))) {
            final Path path2 = Paths.get(this.levelId);
            Files.walkFileTree(this.levelPath, new SimpleFileVisitor<Path>() {
               public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException {
                  if (p_visitFile_1_.endsWith("session.lock")) {
                     return FileVisitResult.CONTINUE;
                  } else {
                     String s1 = path2.resolve(LevelSave.this.levelPath.relativize(p_visitFile_1_)).toString().replace('\\', '/');
                     ZipEntry zipentry = new ZipEntry(s1);
                     zipoutputstream.putNextEntry(zipentry);
                     com.google.common.io.Files.asByteSource(p_visitFile_1_.toFile()).copyTo(zipoutputstream);
                     zipoutputstream.closeEntry();
                     return FileVisitResult.CONTINUE;
                  }
               }
            });
         }

         return Files.size(path1);
      }

      public void close() throws IOException {
         this.lock.close();
      }
   }
}
