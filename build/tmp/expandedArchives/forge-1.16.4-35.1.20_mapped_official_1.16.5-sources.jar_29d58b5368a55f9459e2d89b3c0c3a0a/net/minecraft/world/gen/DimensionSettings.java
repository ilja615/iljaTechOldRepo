package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

public final class DimensionSettings {
   public static final Codec<DimensionSettings> DIRECT_CODEC = RecordCodecBuilder.create((p_236112_0_) -> {
      return p_236112_0_.group(DimensionStructuresSettings.CODEC.fieldOf("structures").forGetter(DimensionSettings::structureSettings), NoiseSettings.CODEC.fieldOf("noise").forGetter(DimensionSettings::noiseSettings), BlockState.CODEC.fieldOf("default_block").forGetter(DimensionSettings::getDefaultBlock), BlockState.CODEC.fieldOf("default_fluid").forGetter(DimensionSettings::getDefaultFluid), Codec.intRange(-20, 276).fieldOf("bedrock_roof_position").forGetter(DimensionSettings::getBedrockRoofPosition), Codec.intRange(-20, 276).fieldOf("bedrock_floor_position").forGetter(DimensionSettings::getBedrockFloorPosition), Codec.intRange(0, 255).fieldOf("sea_level").forGetter(DimensionSettings::seaLevel), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(DimensionSettings::disableMobGeneration)).apply(p_236112_0_, DimensionSettings::new);
   });
   public static final Codec<Supplier<DimensionSettings>> CODEC = RegistryKeyCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, DIRECT_CODEC);
   private final DimensionStructuresSettings structureSettings;
   private final NoiseSettings noiseSettings;
   private final BlockState defaultBlock;
   private final BlockState defaultFluid;
   private final int bedrockRoofPosition;
   private final int bedrockFloorPosition;
   private final int seaLevel;
   private final boolean disableMobGeneration;
   public static final RegistryKey<DimensionSettings> OVERWORLD = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("overworld"));
   public static final RegistryKey<DimensionSettings> AMPLIFIED = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("amplified"));
   public static final RegistryKey<DimensionSettings> NETHER = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("nether"));
   public static final RegistryKey<DimensionSettings> END = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("end"));
   public static final RegistryKey<DimensionSettings> CAVES = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("caves"));
   public static final RegistryKey<DimensionSettings> FLOATING_ISLANDS = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("floating_islands"));
   private static final DimensionSettings BUILTIN_OVERWORLD = register(OVERWORLD, overworld(new DimensionStructuresSettings(true), false, OVERWORLD.location()));

   private DimensionSettings(DimensionStructuresSettings p_i231905_1_, NoiseSettings p_i231905_2_, BlockState p_i231905_3_, BlockState p_i231905_4_, int p_i231905_5_, int p_i231905_6_, int p_i231905_7_, boolean p_i231905_8_) {
      this.structureSettings = p_i231905_1_;
      this.noiseSettings = p_i231905_2_;
      this.defaultBlock = p_i231905_3_;
      this.defaultFluid = p_i231905_4_;
      this.bedrockRoofPosition = p_i231905_5_;
      this.bedrockFloorPosition = p_i231905_6_;
      this.seaLevel = p_i231905_7_;
      this.disableMobGeneration = p_i231905_8_;
   }

   public DimensionStructuresSettings structureSettings() {
      return this.structureSettings;
   }

   public NoiseSettings noiseSettings() {
      return this.noiseSettings;
   }

   public BlockState getDefaultBlock() {
      return this.defaultBlock;
   }

   public BlockState getDefaultFluid() {
      return this.defaultFluid;
   }

   public int getBedrockRoofPosition() {
      return this.bedrockRoofPosition;
   }

   public int getBedrockFloorPosition() {
      return this.bedrockFloorPosition;
   }

   public int seaLevel() {
      return this.seaLevel;
   }

   @Deprecated
   protected boolean disableMobGeneration() {
      return this.disableMobGeneration;
   }

   public boolean stable(RegistryKey<DimensionSettings> p_242744_1_) {
      return Objects.equals(this, WorldGenRegistries.NOISE_GENERATOR_SETTINGS.get(p_242744_1_));
   }

   private static DimensionSettings register(RegistryKey<DimensionSettings> p_242745_0_, DimensionSettings p_242745_1_) {
      WorldGenRegistries.register(WorldGenRegistries.NOISE_GENERATOR_SETTINGS, p_242745_0_.location(), p_242745_1_);
      return p_242745_1_;
   }

   public static DimensionSettings bootstrap() {
      return BUILTIN_OVERWORLD;
   }

   private static DimensionSettings end(DimensionStructuresSettings p_242742_0_, BlockState p_242742_1_, BlockState p_242742_2_, ResourceLocation p_242742_3_, boolean p_242742_4_, boolean p_242742_5_) {
      return new DimensionSettings(p_242742_0_, new NoiseSettings(128, new ScalingSettings(2.0D, 1.0D, 80.0D, 160.0D), new SlideSettings(-3000, 64, -46), new SlideSettings(-30, 7, 1), 2, 1, 0.0D, 0.0D, true, false, p_242742_5_, false), p_242742_1_, p_242742_2_, -10, -10, 0, p_242742_4_);
   }

   private static DimensionSettings nether(DimensionStructuresSettings p_242741_0_, BlockState p_242741_1_, BlockState p_242741_2_, ResourceLocation p_242741_3_) {
      Map<Structure<?>, StructureSeparationSettings> map = Maps.newHashMap(DimensionStructuresSettings.DEFAULTS);
      map.put(Structure.RUINED_PORTAL, new StructureSeparationSettings(25, 10, 34222645));
      return new DimensionSettings(new DimensionStructuresSettings(Optional.ofNullable(p_242741_0_.stronghold()), map), new NoiseSettings(128, new ScalingSettings(1.0D, 3.0D, 80.0D, 60.0D), new SlideSettings(120, 3, 0), new SlideSettings(320, 4, -1), 1, 2, 0.0D, 0.019921875D, false, false, false, false), p_242741_1_, p_242741_2_, 0, 0, 32, false);
   }

   private static DimensionSettings overworld(DimensionStructuresSettings p_242743_0_, boolean p_242743_1_, ResourceLocation p_242743_2_) {
      double d0 = 0.9999999814507745D;
      return new DimensionSettings(p_242743_0_, new NoiseSettings(256, new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D), new SlideSettings(-10, 3, 0), new SlideSettings(-30, 0, 0), 1, 2, 1.0D, -0.46875D, true, true, false, p_242743_1_), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), -10, 0, 63, false);
   }

   static {
      register(AMPLIFIED, overworld(new DimensionStructuresSettings(true), true, AMPLIFIED.location()));
      register(NETHER, nether(new DimensionStructuresSettings(false), Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), NETHER.location()));
      register(END, end(new DimensionStructuresSettings(false), Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), END.location(), true, true));
      register(CAVES, nether(new DimensionStructuresSettings(true), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), CAVES.location()));
      register(FLOATING_ISLANDS, end(new DimensionStructuresSettings(true), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), FLOATING_ISLANDS.location(), false, false));
   }
}
