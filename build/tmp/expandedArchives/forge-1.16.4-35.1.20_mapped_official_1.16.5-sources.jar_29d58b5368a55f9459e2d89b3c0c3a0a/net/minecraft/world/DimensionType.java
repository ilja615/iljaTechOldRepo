package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DimensionType {
   public static final ResourceLocation OVERWORLD_EFFECTS = new ResourceLocation("overworld");
   public static final ResourceLocation NETHER_EFFECTS = new ResourceLocation("the_nether");
   public static final ResourceLocation END_EFFECTS = new ResourceLocation("the_end");
   public static final Codec<DimensionType> DIRECT_CODEC = RecordCodecBuilder.create((p_236026_0_) -> {
      return p_236026_0_.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((p_236028_0_) -> {
         return p_236028_0_.map(OptionalLong::of).orElseGet(OptionalLong::empty);
      }, (p_236029_0_) -> {
         return p_236029_0_.isPresent() ? Optional.of(p_236029_0_.getAsLong()) : Optional.empty();
      }).forGetter((p_236044_0_) -> {
         return p_236044_0_.fixedTime;
      }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange((double)1.0E-5F, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::piglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids), Codec.intRange(0, 256).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), ResourceLocation.CODEC.fieldOf("infiniburn").forGetter((p_241508_0_) -> {
         return p_241508_0_.infiniburn;
      }), ResourceLocation.CODEC.fieldOf("effects").orElse(OVERWORLD_EFFECTS).forGetter((p_242721_0_) -> {
         return p_242721_0_.effectsLocation;
      }), Codec.FLOAT.fieldOf("ambient_light").forGetter((p_236042_0_) -> {
         return p_236042_0_.ambientLight;
      })).apply(p_236026_0_, DimensionType::new);
   });
   public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public static final RegistryKey<DimensionType> OVERWORLD_LOCATION = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld"));
   public static final RegistryKey<DimensionType> NETHER_LOCATION = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_nether"));
   public static final RegistryKey<DimensionType> END_LOCATION = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_end"));
   protected static final DimensionType DEFAULT_OVERWORLD = new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, 256, ColumnFuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0F);
   protected static final DimensionType DEFAULT_NETHER = new DimensionType(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, false, true, false, 128, FuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_NETHER.getName(), NETHER_EFFECTS, 0.1F);
   protected static final DimensionType DEFAULT_END = new DimensionType(OptionalLong.of(6000L), false, false, false, false, 1.0D, true, false, false, false, true, 256, FuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_END.getName(), END_EFFECTS, 0.0F);
   public static final RegistryKey<DimensionType> OVERWORLD_CAVES_LOCATION = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld_caves"));
   protected static final DimensionType DEFAULT_OVERWORLD_CAVES = new DimensionType(OptionalLong.empty(), true, true, false, true, 1.0D, false, false, true, false, true, 256, ColumnFuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0F);
   public static final Codec<Supplier<DimensionType>> CODEC = RegistryKeyCodec.create(Registry.DIMENSION_TYPE_REGISTRY, DIRECT_CODEC);
   private final OptionalLong fixedTime;
   private final boolean hasSkylight;
   private final boolean hasCeiling;
   private final boolean ultraWarm;
   private final boolean natural;
   private final double coordinateScale;
   private final boolean createDragonFight;
   private final boolean piglinSafe;
   private final boolean bedWorks;
   private final boolean respawnAnchorWorks;
   private final boolean hasRaids;
   private final int logicalHeight;
   private final IBiomeMagnifier biomeZoomer;
   private final ResourceLocation infiniburn;
   private final ResourceLocation effectsLocation;
   private final float ambientLight;
   private final transient float[] brightnessRamp;

   protected DimensionType(OptionalLong p_i241972_1_, boolean p_i241972_2_, boolean p_i241972_3_, boolean p_i241972_4_, boolean p_i241972_5_, double p_i241972_6_, boolean p_i241972_8_, boolean p_i241972_9_, boolean p_i241972_10_, boolean p_i241972_11_, int p_i241972_12_, ResourceLocation p_i241972_13_, ResourceLocation p_i241972_14_, float p_i241972_15_) {
      this(p_i241972_1_, p_i241972_2_, p_i241972_3_, p_i241972_4_, p_i241972_5_, p_i241972_6_, false, p_i241972_8_, p_i241972_9_, p_i241972_10_, p_i241972_11_, p_i241972_12_, FuzzedBiomeMagnifier.INSTANCE, p_i241972_13_, p_i241972_14_, p_i241972_15_);
   }

   protected DimensionType(OptionalLong p_i241973_1_, boolean p_i241973_2_, boolean p_i241973_3_, boolean p_i241973_4_, boolean p_i241973_5_, double p_i241973_6_, boolean p_i241973_8_, boolean p_i241973_9_, boolean p_i241973_10_, boolean p_i241973_11_, boolean p_i241973_12_, int p_i241973_13_, IBiomeMagnifier p_i241973_14_, ResourceLocation p_i241973_15_, ResourceLocation p_i241973_16_, float p_i241973_17_) {
      this.fixedTime = p_i241973_1_;
      this.hasSkylight = p_i241973_2_;
      this.hasCeiling = p_i241973_3_;
      this.ultraWarm = p_i241973_4_;
      this.natural = p_i241973_5_;
      this.coordinateScale = p_i241973_6_;
      this.createDragonFight = p_i241973_8_;
      this.piglinSafe = p_i241973_9_;
      this.bedWorks = p_i241973_10_;
      this.respawnAnchorWorks = p_i241973_11_;
      this.hasRaids = p_i241973_12_;
      this.logicalHeight = p_i241973_13_;
      this.biomeZoomer = p_i241973_14_;
      this.infiniburn = p_i241973_15_;
      this.effectsLocation = p_i241973_16_;
      this.ambientLight = p_i241973_17_;
      this.brightnessRamp = fillBrightnessRamp(p_i241973_17_);
   }

   private static float[] fillBrightnessRamp(float p_236020_0_) {
      float[] afloat = new float[16];

      for(int i = 0; i <= 15; ++i) {
         float f = (float)i / 15.0F;
         float f1 = f / (4.0F - 3.0F * f);
         afloat[i] = MathHelper.lerp(p_236020_0_, f1, 1.0F);
      }

      return afloat;
   }

   @Deprecated
   public static DataResult<RegistryKey<World>> parseLegacy(Dynamic<?> p_236025_0_) {
      Optional<Number> optional = p_236025_0_.asNumber().result();
      if (optional.isPresent()) {
         int i = optional.get().intValue();
         if (i == -1) {
            return DataResult.success(World.NETHER);
         }

         if (i == 0) {
            return DataResult.success(World.OVERWORLD);
         }

         if (i == 1) {
            return DataResult.success(World.END);
         }
      }

      return World.RESOURCE_KEY_CODEC.parse(p_236025_0_);
   }

   public static DynamicRegistries.Impl registerBuiltin(DynamicRegistries.Impl p_236027_0_) {
      MutableRegistry<DimensionType> mutableregistry = p_236027_0_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      mutableregistry.register(OVERWORLD_LOCATION, DEFAULT_OVERWORLD, Lifecycle.stable());
      mutableregistry.register(OVERWORLD_CAVES_LOCATION, DEFAULT_OVERWORLD_CAVES, Lifecycle.stable());
      mutableregistry.register(NETHER_LOCATION, DEFAULT_NETHER, Lifecycle.stable());
      mutableregistry.register(END_LOCATION, DEFAULT_END, Lifecycle.stable());
      return p_236027_0_;
   }

   private static ChunkGenerator defaultEndGenerator(Registry<Biome> p_242717_0_, Registry<DimensionSettings> p_242717_1_, long p_242717_2_) {
      return new NoiseChunkGenerator(new EndBiomeProvider(p_242717_0_, p_242717_2_), p_242717_2_, () -> {
         return p_242717_1_.getOrThrow(DimensionSettings.END);
      });
   }

   private static ChunkGenerator defaultNetherGenerator(Registry<Biome> p_242720_0_, Registry<DimensionSettings> p_242720_1_, long p_242720_2_) {
      return new NoiseChunkGenerator(NetherBiomeProvider.Preset.NETHER.biomeSource(p_242720_0_, p_242720_2_), p_242720_2_, () -> {
         return p_242720_1_.getOrThrow(DimensionSettings.NETHER);
      });
   }

   public static SimpleRegistry<Dimension> defaultDimensions(Registry<DimensionType> p_242718_0_, Registry<Biome> p_242718_1_, Registry<DimensionSettings> p_242718_2_, long p_242718_3_) {
      SimpleRegistry<Dimension> simpleregistry = new SimpleRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
      simpleregistry.register(Dimension.NETHER, new Dimension(() -> {
         return p_242718_0_.getOrThrow(NETHER_LOCATION);
      }, defaultNetherGenerator(p_242718_1_, p_242718_2_, p_242718_3_)), Lifecycle.stable());
      simpleregistry.register(Dimension.END, new Dimension(() -> {
         return p_242718_0_.getOrThrow(END_LOCATION);
      }, defaultEndGenerator(p_242718_1_, p_242718_2_, p_242718_3_)), Lifecycle.stable());
      return simpleregistry;
   }

   public static double getTeleportationScale(DimensionType p_242715_0_, DimensionType p_242715_1_) {
      double d0 = p_242715_0_.coordinateScale();
      double d1 = p_242715_1_.coordinateScale();
      return d0 / d1;
   }

   @Deprecated
   public String getFileSuffix() {
      return this.equalTo(DEFAULT_END) ? "_end" : "";
   }

   public static File getStorageFolder(RegistryKey<World> p_236031_0_, File p_236031_1_) {
      if (p_236031_0_ == World.OVERWORLD) {
         return p_236031_1_;
      } else if (p_236031_0_ == World.END) {
         return new File(p_236031_1_, "DIM1");
      } else {
         return p_236031_0_ == World.NETHER ? new File(p_236031_1_, "DIM-1") : new File(p_236031_1_, "dimensions/" + p_236031_0_.location().getNamespace() + "/" + p_236031_0_.location().getPath());
      }
   }

   public boolean hasSkyLight() {
      return this.hasSkylight;
   }

   public boolean hasCeiling() {
      return this.hasCeiling;
   }

   public boolean ultraWarm() {
      return this.ultraWarm;
   }

   public boolean natural() {
      return this.natural;
   }

   public double coordinateScale() {
      return this.coordinateScale;
   }

   public boolean piglinSafe() {
      return this.piglinSafe;
   }

   public boolean bedWorks() {
      return this.bedWorks;
   }

   public boolean respawnAnchorWorks() {
      return this.respawnAnchorWorks;
   }

   public boolean hasRaids() {
      return this.hasRaids;
   }

   public int logicalHeight() {
      return this.logicalHeight;
   }

   public boolean createDragonFight() {
      return this.createDragonFight;
   }

   public IBiomeMagnifier getBiomeZoomer() {
      return this.biomeZoomer;
   }

   public boolean hasFixedTime() {
      return this.fixedTime.isPresent();
   }

   public float timeOfDay(long p_236032_1_) {
      double d0 = MathHelper.frac((double)this.fixedTime.orElse(p_236032_1_) / 24000.0D - 0.25D);
      double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
      return (float)(d0 * 2.0D + d1) / 3.0F;
   }

   public int moonPhase(long p_236035_1_) {
      return (int)(p_236035_1_ / 24000L % 8L + 8L) % 8;
   }

   public float brightness(int p_236021_1_) {
      return this.brightnessRamp[p_236021_1_];
   }

   public ITag<Block> infiniburn() {
      ITag<Block> itag = BlockTags.getAllTags().getTag(this.infiniburn);
      return (ITag<Block>)(itag != null ? itag : BlockTags.INFINIBURN_OVERWORLD);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation effectsLocation() {
      return this.effectsLocation;
   }

   public boolean equalTo(DimensionType p_242714_1_) {
      if (this == p_242714_1_) {
         return true;
      } else {
         return this.hasSkylight == p_242714_1_.hasSkylight && this.hasCeiling == p_242714_1_.hasCeiling && this.ultraWarm == p_242714_1_.ultraWarm && this.natural == p_242714_1_.natural && this.coordinateScale == p_242714_1_.coordinateScale && this.createDragonFight == p_242714_1_.createDragonFight && this.piglinSafe == p_242714_1_.piglinSafe && this.bedWorks == p_242714_1_.bedWorks && this.respawnAnchorWorks == p_242714_1_.respawnAnchorWorks && this.hasRaids == p_242714_1_.hasRaids && this.logicalHeight == p_242714_1_.logicalHeight && Float.compare(p_242714_1_.ambientLight, this.ambientLight) == 0 && this.fixedTime.equals(p_242714_1_.fixedTime) && this.biomeZoomer.equals(p_242714_1_.biomeZoomer) && this.infiniburn.equals(p_242714_1_.infiniburn) && this.effectsLocation.equals(p_242714_1_.effectsLocation);
      }
   }
}
