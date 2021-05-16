package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Biome extends net.minecraftforge.registries.ForgeRegistryEntry.UncheckedRegistryEntry<Biome> {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create((p_235064_0_) -> {
      return p_235064_0_.group(Biome.Climate.CODEC.forGetter((p_242446_0_) -> {
         return p_242446_0_.climateSettings;
      }), Biome.Category.CODEC.fieldOf("category").forGetter((p_235087_0_) -> {
         return p_235087_0_.biomeCategory;
      }), Codec.FLOAT.fieldOf("depth").forGetter((p_235086_0_) -> {
         return p_235086_0_.depth;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_235085_0_) -> {
         return p_235085_0_.scale;
      }), BiomeAmbience.CODEC.fieldOf("effects").forGetter((p_242444_0_) -> {
         return p_242444_0_.specialEffects;
      }), BiomeGenerationSettings.CODEC.forGetter((p_242443_0_) -> {
         return p_242443_0_.generationSettings;
      }), MobSpawnInfo.CODEC.forGetter((p_242442_0_) -> {
         return p_242442_0_.mobSettings;
      }), ResourceLocation.CODEC.optionalFieldOf("forge:registry_name").forGetter(b -> Optional.ofNullable(b.getRegistryName())))
      .apply(p_235064_0_, (climate, category, depth, scale, effects, gen, spawns, name) ->
          net.minecraftforge.common.ForgeHooks.enhanceBiome(name.orElse(null), climate, category, depth, scale, effects, gen, spawns, p_235064_0_, Biome::new));
   });
   public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create((p_242432_0_) -> {
      return p_242432_0_.group(Biome.Climate.CODEC.forGetter((p_242441_0_) -> {
         return p_242441_0_.climateSettings;
      }), Biome.Category.CODEC.fieldOf("category").forGetter((p_242439_0_) -> {
         return p_242439_0_.biomeCategory;
      }), Codec.FLOAT.fieldOf("depth").forGetter((p_242438_0_) -> {
         return p_242438_0_.depth;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_242434_0_) -> {
         return p_242434_0_.scale;
      }), BiomeAmbience.CODEC.fieldOf("effects").forGetter((p_242429_0_) -> {
         return p_242429_0_.specialEffects;
      })).apply(p_242432_0_, (p_242428_0_, p_242428_1_, p_242428_2_, p_242428_3_, p_242428_4_) -> {
         return new Biome(p_242428_0_, p_242428_1_, p_242428_2_, p_242428_3_, p_242428_4_, BiomeGenerationSettings.EMPTY, MobSpawnInfo.EMPTY);
      });
   });
   public static final Codec<Supplier<Biome>> CODEC = RegistryKeyCodec.create(Registry.BIOME_REGISTRY, DIRECT_CODEC);
   public static final Codec<List<Supplier<Biome>>> LIST_CODEC = RegistryKeyCodec.homogeneousList(Registry.BIOME_REGISTRY, DIRECT_CODEC);
   private final Map<Integer, List<Structure<?>>> structuresByStep = Registry.STRUCTURE_FEATURE.stream().collect(Collectors.groupingBy((p_242435_0_) -> {
      return p_242435_0_.step().ordinal();
   }));
   private static final PerlinNoiseGenerator TEMPERATURE_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(1234L), ImmutableList.of(0));
   private static final PerlinNoiseGenerator FROZEN_TEMPERATURE_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(3456L), ImmutableList.of(-2, -1, 0));
   public static final PerlinNoiseGenerator BIOME_INFO_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(2345L), ImmutableList.of(0));
   private final Biome.Climate climateSettings;
   private final BiomeGenerationSettings generationSettings;
   private final MobSpawnInfo mobSettings;
   private final float depth;
   private final float scale;
   private final Biome.Category biomeCategory;
   private final BiomeAmbience specialEffects;
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> {
      return Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
         return long2floatlinkedopenhashmap;
      });
   });

   private Biome(Biome.Climate p_i241927_1_, Biome.Category p_i241927_2_, float p_i241927_3_, float p_i241927_4_, BiomeAmbience p_i241927_5_, BiomeGenerationSettings p_i241927_6_, MobSpawnInfo p_i241927_7_) {
      this.climateSettings = p_i241927_1_;
      this.generationSettings = p_i241927_6_;
      this.mobSettings = p_i241927_7_;
      this.biomeCategory = p_i241927_2_;
      this.depth = p_i241927_3_;
      this.scale = p_i241927_4_;
      this.specialEffects = p_i241927_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyColor() {
      return this.specialEffects.getSkyColor();
   }

   public MobSpawnInfo getMobSettings() {
      return this.mobSettings;
   }

   public Biome.RainType getPrecipitation() {
      return this.climateSettings.precipitation;
   }

   public boolean isHumid() {
      return this.getDownfall() > 0.85F;
   }

   private float getHeightAdjustedTemperature(BlockPos p_242437_1_) {
      float f = this.climateSettings.temperatureModifier.modifyTemperature(p_242437_1_, this.getBaseTemperature());
      if (p_242437_1_.getY() > 64) {
         float f1 = (float)(TEMPERATURE_NOISE.getValue((double)((float)p_242437_1_.getX() / 8.0F), (double)((float)p_242437_1_.getZ() / 8.0F), false) * 4.0D);
         return f - (f1 + (float)p_242437_1_.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return f;
      }
   }

   public final float getTemperature(BlockPos p_225486_1_) {
      long i = p_225486_1_.asLong();
      Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = this.temperatureCache.get();
      float f = long2floatlinkedopenhashmap.get(i);
      if (!Float.isNaN(f)) {
         return f;
      } else {
         float f1 = this.getHeightAdjustedTemperature(p_225486_1_);
         if (long2floatlinkedopenhashmap.size() == 1024) {
            long2floatlinkedopenhashmap.removeFirstFloat();
         }

         long2floatlinkedopenhashmap.put(i, f1);
         return f1;
      }
   }

   public boolean shouldFreeze(IWorldReader p_201848_1_, BlockPos p_201848_2_) {
      return this.shouldFreeze(p_201848_1_, p_201848_2_, true);
   }

   public boolean shouldFreeze(IWorldReader p_201854_1_, BlockPos p_201854_2_, boolean p_201854_3_) {
      if (this.getTemperature(p_201854_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201854_2_.getY() >= 0 && p_201854_2_.getY() < 256 && p_201854_1_.getBrightness(LightType.BLOCK, p_201854_2_) < 10) {
            BlockState blockstate = p_201854_1_.getBlockState(p_201854_2_);
            FluidState fluidstate = p_201854_1_.getFluidState(p_201854_2_);
            if (fluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof FlowingFluidBlock) {
               if (!p_201854_3_) {
                  return true;
               }

               boolean flag = p_201854_1_.isWaterAt(p_201854_2_.west()) && p_201854_1_.isWaterAt(p_201854_2_.east()) && p_201854_1_.isWaterAt(p_201854_2_.north()) && p_201854_1_.isWaterAt(p_201854_2_.south());
               if (!flag) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean shouldSnow(IWorldReader p_201850_1_, BlockPos p_201850_2_) {
      if (this.getTemperature(p_201850_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201850_2_.getY() >= 0 && p_201850_2_.getY() < 256 && p_201850_1_.getBrightness(LightType.BLOCK, p_201850_2_) < 10) {
            BlockState blockstate = p_201850_1_.getBlockState(p_201850_2_);
            if (blockstate.isAir(p_201850_1_, p_201850_2_) && Blocks.SNOW.defaultBlockState().canSurvive(p_201850_1_, p_201850_2_)) {
               return true;
            }
         }

         return false;
      }
   }

   public BiomeGenerationSettings getGenerationSettings() {
      return this.generationSettings;
   }

   public void generate(StructureManager p_242427_1_, ChunkGenerator p_242427_2_, WorldGenRegion p_242427_3_, long p_242427_4_, SharedSeedRandom p_242427_6_, BlockPos p_242427_7_) {
      List<List<Supplier<ConfiguredFeature<?, ?>>>> list = this.generationSettings.features();
      int i = GenerationStage.Decoration.values().length;

      for(int j = 0; j < i; ++j) {
         int k = 0;
         if (p_242427_1_.shouldGenerateFeatures()) {
            for(Structure<?> structure : this.structuresByStep.getOrDefault(j, Collections.emptyList())) {
               p_242427_6_.setFeatureSeed(p_242427_4_, k, j);
               int l = p_242427_7_.getX() >> 4;
               int i1 = p_242427_7_.getZ() >> 4;
               int j1 = l << 4;
               int k1 = i1 << 4;

               try {
                  p_242427_1_.startsForFeature(SectionPos.of(p_242427_7_), structure).forEach((p_242426_8_) -> {
                     p_242426_8_.placeInChunk(p_242427_3_, p_242427_1_, p_242427_2_, p_242427_6_, new MutableBoundingBox(j1, k1, j1 + 15, k1 + 15), new ChunkPos(l, i1));
                  });
               } catch (Exception exception) {
                  CrashReport crashreport = CrashReport.forThrowable(exception, "Feature placement");
                  crashreport.addCategory("Feature").setDetail("Id", Registry.STRUCTURE_FEATURE.getKey(structure)).setDetail("Description", () -> {
                     return structure.toString();
                  });
                  throw new ReportedException(crashreport);
               }

               ++k;
            }
         }

         if (list.size() > j) {
            for(Supplier<ConfiguredFeature<?, ?>> supplier : list.get(j)) {
               ConfiguredFeature<?, ?> configuredfeature = supplier.get();
               p_242427_6_.setFeatureSeed(p_242427_4_, k, j);

               try {
                  configuredfeature.place(p_242427_3_, p_242427_2_, p_242427_6_, p_242427_7_);
               } catch (Exception exception1) {
                  CrashReport crashreport1 = CrashReport.forThrowable(exception1, "Feature placement");
                  crashreport1.addCategory("Feature").setDetail("Id", Registry.FEATURE.getKey(configuredfeature.feature)).setDetail("Config", configuredfeature.config).setDetail("Description", () -> {
                     return configuredfeature.feature.toString();
                  });
                  throw new ReportedException(crashreport1);
               }

               ++k;
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getFogColor() {
      return this.specialEffects.getFogColor();
   }

   @OnlyIn(Dist.CLIENT)
   public int getGrassColor(double p_225528_1_, double p_225528_3_) {
      int i = this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);
      return this.specialEffects.getGrassColorModifier().modifyColor(p_225528_1_, p_225528_3_, i);
   }

   @OnlyIn(Dist.CLIENT)
   private int getGrassColorFromTexture() {
      double d0 = (double)MathHelper.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return GrassColors.get(d0, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoliageColor() {
      return this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
   }

   @OnlyIn(Dist.CLIENT)
   private int getFoliageColorFromTexture() {
      double d0 = (double)MathHelper.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return FoliageColors.get(d0, d1);
   }

   public void buildSurfaceAt(Random p_206854_1_, IChunk p_206854_2_, int p_206854_3_, int p_206854_4_, int p_206854_5_, double p_206854_6_, BlockState p_206854_8_, BlockState p_206854_9_, int p_206854_10_, long p_206854_11_) {
      ConfiguredSurfaceBuilder<?> configuredsurfacebuilder = this.generationSettings.getSurfaceBuilder().get();
      configuredsurfacebuilder.initNoise(p_206854_11_);
      configuredsurfacebuilder.apply(p_206854_1_, p_206854_2_, this, p_206854_3_, p_206854_4_, p_206854_5_, p_206854_6_, p_206854_8_, p_206854_9_, p_206854_10_, p_206854_11_);
   }

   public final float getDepth() {
      return this.depth;
   }

   public final float getDownfall() {
      return this.climateSettings.downfall;
   }

   public final float getScale() {
      return this.scale;
   }

   public final float getBaseTemperature() {
      return this.climateSettings.temperature;
   }

   public BiomeAmbience getSpecialEffects() {
      return this.specialEffects;
   }

   @OnlyIn(Dist.CLIENT)
   public final int getWaterColor() {
      return this.specialEffects.getWaterColor();
   }

   @OnlyIn(Dist.CLIENT)
   public final int getWaterFogColor() {
      return this.specialEffects.getWaterFogColor();
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<ParticleEffectAmbience> getAmbientParticle() {
      return this.specialEffects.getAmbientParticleSettings();
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<SoundEvent> getAmbientLoop() {
      return this.specialEffects.getAmbientLoopSoundEvent();
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<MoodSoundAmbience> getAmbientMood() {
      return this.specialEffects.getAmbientMoodSettings();
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<SoundAdditionsAmbience> getAmbientAdditions() {
      return this.specialEffects.getAmbientAdditionsSettings();
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<BackgroundMusicSelector> getBackgroundMusic() {
      return this.specialEffects.getBackgroundMusic();
   }

   public final Biome.Category getBiomeCategory() {
      return this.biomeCategory;
   }

   public String toString() {
      ResourceLocation resourcelocation = WorldGenRegistries.BIOME.getKey(this);
      return resourcelocation == null ? super.toString() : resourcelocation.toString();
   }

   public static class Attributes {
      public static final Codec<Biome.Attributes> CODEC = RecordCodecBuilder.create((p_235111_0_) -> {
         return p_235111_0_.group(Codec.floatRange(-2.0F, 2.0F).fieldOf("temperature").forGetter((p_235116_0_) -> {
            return p_235116_0_.temperature;
         }), Codec.floatRange(-2.0F, 2.0F).fieldOf("humidity").forGetter((p_235115_0_) -> {
            return p_235115_0_.humidity;
         }), Codec.floatRange(-2.0F, 2.0F).fieldOf("altitude").forGetter((p_235114_0_) -> {
            return p_235114_0_.altitude;
         }), Codec.floatRange(-2.0F, 2.0F).fieldOf("weirdness").forGetter((p_235113_0_) -> {
            return p_235113_0_.weirdness;
         }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").forGetter((p_235112_0_) -> {
            return p_235112_0_.offset;
         })).apply(p_235111_0_, Biome.Attributes::new);
      });
      private final float temperature;
      private final float humidity;
      private final float altitude;
      private final float weirdness;
      private final float offset;

      public Attributes(float p_i231632_1_, float p_i231632_2_, float p_i231632_3_, float p_i231632_4_, float p_i231632_5_) {
         this.temperature = p_i231632_1_;
         this.humidity = p_i231632_2_;
         this.altitude = p_i231632_3_;
         this.weirdness = p_i231632_4_;
         this.offset = p_i231632_5_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            Biome.Attributes biome$attributes = (Biome.Attributes)p_equals_1_;
            if (Float.compare(biome$attributes.temperature, this.temperature) != 0) {
               return false;
            } else if (Float.compare(biome$attributes.humidity, this.humidity) != 0) {
               return false;
            } else if (Float.compare(biome$attributes.altitude, this.altitude) != 0) {
               return false;
            } else {
               return Float.compare(biome$attributes.weirdness, this.weirdness) == 0;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int i = this.temperature != 0.0F ? Float.floatToIntBits(this.temperature) : 0;
         i = 31 * i + (this.humidity != 0.0F ? Float.floatToIntBits(this.humidity) : 0);
         i = 31 * i + (this.altitude != 0.0F ? Float.floatToIntBits(this.altitude) : 0);
         return 31 * i + (this.weirdness != 0.0F ? Float.floatToIntBits(this.weirdness) : 0);
      }

      public float fitness(Biome.Attributes p_235110_1_) {
         return (this.temperature - p_235110_1_.temperature) * (this.temperature - p_235110_1_.temperature) + (this.humidity - p_235110_1_.humidity) * (this.humidity - p_235110_1_.humidity) + (this.altitude - p_235110_1_.altitude) * (this.altitude - p_235110_1_.altitude) + (this.weirdness - p_235110_1_.weirdness) * (this.weirdness - p_235110_1_.weirdness) + (this.offset - p_235110_1_.offset) * (this.offset - p_235110_1_.offset);
      }
   }

   public static class Builder {
      @Nullable
      private Biome.RainType precipitation;
      @Nullable
      private Biome.Category biomeCategory;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
      @Nullable
      private Float temperature;
      private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
      @Nullable
      private Float downfall;
      @Nullable
      private BiomeAmbience specialEffects;
      @Nullable
      private MobSpawnInfo mobSpawnSettings;
      @Nullable
      private BiomeGenerationSettings generationSettings;

      public Biome.Builder precipitation(Biome.RainType p_205415_1_) {
         this.precipitation = p_205415_1_;
         return this;
      }

      public Biome.Builder biomeCategory(Biome.Category p_205419_1_) {
         this.biomeCategory = p_205419_1_;
         return this;
      }

      public Biome.Builder depth(float p_205421_1_) {
         this.depth = p_205421_1_;
         return this;
      }

      public Biome.Builder scale(float p_205420_1_) {
         this.scale = p_205420_1_;
         return this;
      }

      public Biome.Builder temperature(float p_205414_1_) {
         this.temperature = p_205414_1_;
         return this;
      }

      public Biome.Builder downfall(float p_205417_1_) {
         this.downfall = p_205417_1_;
         return this;
      }

      public Biome.Builder specialEffects(BiomeAmbience p_235097_1_) {
         this.specialEffects = p_235097_1_;
         return this;
      }

      public Biome.Builder mobSpawnSettings(MobSpawnInfo p_242458_1_) {
         this.mobSpawnSettings = p_242458_1_;
         return this;
      }

      public Biome.Builder generationSettings(BiomeGenerationSettings p_242457_1_) {
         this.generationSettings = p_242457_1_;
         return this;
      }

      public Biome.Builder temperatureAdjustment(Biome.TemperatureModifier p_242456_1_) {
         this.temperatureModifier = p_242456_1_;
         return this;
      }

      public Biome build() {
         if (this.precipitation != null && this.biomeCategory != null && this.depth != null && this.scale != null && this.temperature != null && this.downfall != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
            return new Biome(new Biome.Climate(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.biomeCategory, this.depth, this.scale, this.specialEffects, this.generationSettings, this.mobSpawnSettings);
         } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
         }
      }

      public String toString() {
         return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n" + '}';
      }
   }

   public static enum Category implements IStringSerializable {
      NONE("none"),
      TAIGA("taiga"),
      EXTREME_HILLS("extreme_hills"),
      JUNGLE("jungle"),
      MESA("mesa"),
      PLAINS("plains"),
      SAVANNA("savanna"),
      ICY("icy"),
      THEEND("the_end"),
      BEACH("beach"),
      FOREST("forest"),
      OCEAN("ocean"),
      DESERT("desert"),
      RIVER("river"),
      SWAMP("swamp"),
      MUSHROOM("mushroom"),
      NETHER("nether");

      public static final Codec<Biome.Category> CODEC = IStringSerializable.fromEnum(Biome.Category::values, Biome.Category::byName);
      private static final Map<String, Biome.Category> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.Category::getName, (p_222353_0_) -> {
         return p_222353_0_;
      }));
      private final String name;

      private Category(String p_i50595_3_) {
         this.name = p_i50595_3_;
      }

      public String getName() {
         return this.name;
      }

      public static Biome.Category byName(String p_235103_0_) {
         return BY_NAME.get(p_235103_0_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static class Climate {
      public static final MapCodec<Biome.Climate> CODEC = RecordCodecBuilder.mapCodec((p_242465_0_) -> {
         return p_242465_0_.group(Biome.RainType.CODEC.fieldOf("precipitation").forGetter((p_242472_0_) -> {
            return p_242472_0_.precipitation;
         }), Codec.FLOAT.fieldOf("temperature").forGetter((p_242471_0_) -> {
            return p_242471_0_.temperature;
         }), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((p_242470_0_) -> {
            return p_242470_0_.temperatureModifier;
         }), Codec.FLOAT.fieldOf("downfall").forGetter((p_242469_0_) -> {
            return p_242469_0_.downfall;
         })).apply(p_242465_0_, Biome.Climate::new);
      });
      public final Biome.RainType precipitation;
      public final float temperature;
      public final Biome.TemperatureModifier temperatureModifier;
      public final float downfall;

      public Climate(Biome.RainType p_i241929_1_, float p_i241929_2_, Biome.TemperatureModifier p_i241929_3_, float p_i241929_4_) {
         this.precipitation = p_i241929_1_;
         this.temperature = p_i241929_2_;
         this.temperatureModifier = p_i241929_3_;
         this.downfall = p_i241929_4_;
      }
   }

   public static enum RainType implements IStringSerializable {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      public static final Codec<Biome.RainType> CODEC = IStringSerializable.fromEnum(Biome.RainType::values, Biome.RainType::byName);
      private static final Map<String, Biome.RainType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.RainType::getName, (p_222360_0_) -> {
         return p_222360_0_;
      }));
      private final String name;

      private RainType(String p_i50593_3_) {
         this.name = p_i50593_3_;
      }

      public String getName() {
         return this.name;
      }

      public static Biome.RainType byName(String p_235122_0_) {
         return BY_NAME.get(p_235122_0_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum TemperatureModifier implements IStringSerializable {
      NONE("none") {
         public float modifyTemperature(BlockPos p_241852_1_, float p_241852_2_) {
            return p_241852_2_;
         }
      },
      FROZEN("frozen") {
         public float modifyTemperature(BlockPos p_241852_1_, float p_241852_2_) {
            double d0 = Biome.FROZEN_TEMPERATURE_NOISE.getValue((double)p_241852_1_.getX() * 0.05D, (double)p_241852_1_.getZ() * 0.05D, false) * 7.0D;
            double d1 = Biome.BIOME_INFO_NOISE.getValue((double)p_241852_1_.getX() * 0.2D, (double)p_241852_1_.getZ() * 0.2D, false);
            double d2 = d0 + d1;
            if (d2 < 0.3D) {
               double d3 = Biome.BIOME_INFO_NOISE.getValue((double)p_241852_1_.getX() * 0.09D, (double)p_241852_1_.getZ() * 0.09D, false);
               if (d3 < 0.8D) {
                  return 0.2F;
               }
            }

            return p_241852_2_;
         }
      };

      private final String name;
      public static final Codec<Biome.TemperatureModifier> CODEC = IStringSerializable.fromEnum(Biome.TemperatureModifier::values, Biome.TemperatureModifier::byName);
      private static final Map<String, Biome.TemperatureModifier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.TemperatureModifier::getName, (p_242476_0_) -> {
         return p_242476_0_;
      }));

      public abstract float modifyTemperature(BlockPos p_241852_1_, float p_241852_2_);

      private TemperatureModifier(String p_i241931_3_) {
         this.name = p_i241931_3_;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Biome.TemperatureModifier byName(String p_242477_0_) {
         return BY_NAME.get(p_242477_0_);
      }
   }
}
