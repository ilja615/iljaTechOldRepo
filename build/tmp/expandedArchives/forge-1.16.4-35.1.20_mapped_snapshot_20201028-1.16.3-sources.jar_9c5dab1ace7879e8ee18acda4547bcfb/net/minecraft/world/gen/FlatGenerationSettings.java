package net.minecraft.world.gen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.FillLayerConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatGenerationSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Codec<FlatGenerationSettings> field_236932_a_ = RecordCodecBuilder.<FlatGenerationSettings>create((p_236938_0_) -> {
      return p_236938_0_.group(RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter((p_242874_0_) -> {
         return p_242874_0_.field_242867_d;
      }), DimensionStructuresSettings.field_236190_a_.fieldOf("structures").forGetter(FlatGenerationSettings::func_236943_d_), FlatLayerInfo.field_236929_a_.listOf().fieldOf("layers").forGetter(FlatGenerationSettings::getFlatLayers), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((p_241528_0_) -> {
         return p_241528_0_.field_236935_l_;
      }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((p_242871_0_) -> {
         return p_242871_0_.field_236934_k_;
      }), Biome.BIOME_CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((p_242868_0_) -> {
         return Optional.of(p_242868_0_.biomeToUse);
      })).apply(p_236938_0_, FlatGenerationSettings::new);
   }).stable();
   private static final Map<Structure<?>, StructureFeature<?, ?>> STRUCTURES = Util.make(Maps.newHashMap(), (p_236940_0_) -> {
      p_236940_0_.put(Structure.MINESHAFT, StructureFeatures.MINESHAFT);
      p_236940_0_.put(Structure.VILLAGE, StructureFeatures.VILLAGE_PLAINS);
      p_236940_0_.put(Structure.STRONGHOLD, StructureFeatures.STRONGHOLD);
      p_236940_0_.put(Structure.SWAMP_HUT, StructureFeatures.SWAMP_HUT);
      p_236940_0_.put(Structure.DESERT_PYRAMID, StructureFeatures.DESERT_PYRAMID);
      p_236940_0_.put(Structure.JUNGLE_PYRAMID, StructureFeatures.JUNGLE_PYRAMID);
      p_236940_0_.put(Structure.IGLOO, StructureFeatures.IGLOO);
      p_236940_0_.put(Structure.OCEAN_RUIN, StructureFeatures.OCEAN_RUIN_COLD);
      p_236940_0_.put(Structure.SHIPWRECK, StructureFeatures.SHIPWRECK);
      p_236940_0_.put(Structure.MONUMENT, StructureFeatures.MONUMENT);
      p_236940_0_.put(Structure.END_CITY, StructureFeatures.END_CITY);
      p_236940_0_.put(Structure.WOODLAND_MANSION, StructureFeatures.MANSION);
      p_236940_0_.put(Structure.FORTRESS, StructureFeatures.FORTRESS);
      p_236940_0_.put(Structure.PILLAGER_OUTPOST, StructureFeatures.PILLAGER_OUTPOST);
      p_236940_0_.put(Structure.RUINED_PORTAL, StructureFeatures.RUINED_PORTAL);
      p_236940_0_.put(Structure.BASTION_REMNANT, StructureFeatures.BASTION_REMNANT);
   });
   private final Registry<Biome> field_242867_d;
   private final DimensionStructuresSettings field_236933_f_;
   private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   private Supplier<Biome> biomeToUse;
   private final BlockState[] states = new BlockState[256];
   private boolean allAir;
   private boolean field_236934_k_ = false;
   private boolean field_236935_l_ = false;

   public FlatGenerationSettings(Registry<Biome> p_i242012_1_, DimensionStructuresSettings p_i242012_2_, List<FlatLayerInfo> p_i242012_3_, boolean p_i242012_4_, boolean p_i242012_5_, Optional<Supplier<Biome>> p_i242012_6_) {
      this(p_i242012_2_, p_i242012_1_);
      if (p_i242012_4_) {
         this.func_236941_b_();
      }

      if (p_i242012_5_) {
         this.func_236936_a_();
      }

      this.flatLayers.addAll(p_i242012_3_);
      this.updateLayers();
      if (!p_i242012_6_.isPresent()) {
         LOGGER.error("Unknown biome, defaulting to plains");
         this.biomeToUse = () -> {
            return p_i242012_1_.getOrThrow(Biomes.PLAINS);
         };
      } else {
         this.biomeToUse = p_i242012_6_.get();
      }

   }

   public FlatGenerationSettings(DimensionStructuresSettings p_i242011_1_, Registry<Biome> p_i242011_2_) {
      this.field_242867_d = p_i242011_2_;
      this.field_236933_f_ = p_i242011_1_;
      this.biomeToUse = () -> {
         return p_i242011_2_.getOrThrow(Biomes.PLAINS);
      };
   }

   @OnlyIn(Dist.CLIENT)
   public FlatGenerationSettings func_236937_a_(DimensionStructuresSettings p_236937_1_) {
      return this.func_241527_a_(this.flatLayers, p_236937_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public FlatGenerationSettings func_241527_a_(List<FlatLayerInfo> p_241527_1_, DimensionStructuresSettings p_241527_2_) {
      FlatGenerationSettings flatgenerationsettings = new FlatGenerationSettings(p_241527_2_, this.field_242867_d);

      for(FlatLayerInfo flatlayerinfo : p_241527_1_) {
         flatgenerationsettings.flatLayers.add(new FlatLayerInfo(flatlayerinfo.getLayerCount(), flatlayerinfo.getLayerMaterial().getBlock()));
         flatgenerationsettings.updateLayers();
      }

      flatgenerationsettings.func_242870_a(this.biomeToUse);
      if (this.field_236934_k_) {
         flatgenerationsettings.func_236936_a_();
      }

      if (this.field_236935_l_) {
         flatgenerationsettings.func_236941_b_();
      }

      return flatgenerationsettings;
   }

   public void func_236936_a_() {
      this.field_236934_k_ = true;
   }

   public void func_236941_b_() {
      this.field_236935_l_ = true;
   }

   public Biome func_236942_c_() {
      Biome biome = this.getBiome();
      BiomeGenerationSettings biomegenerationsettings = biome.getGenerationSettings();
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(biomegenerationsettings.getSurfaceBuilder());
      if (this.field_236935_l_) {
         biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_WATER);
         biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
      }

      for(Entry<Structure<?>, StructureSeparationSettings> entry : this.field_236933_f_.func_236195_a_().entrySet()) {
         biomegenerationsettings$builder.withStructure(biomegenerationsettings.getStructure(STRUCTURES.get(entry.getKey())));
      }

      boolean flag = (!this.allAir || this.field_242867_d.getOptionalKey(biome).equals(Optional.of(Biomes.THE_VOID))) && this.field_236934_k_;
      if (flag) {
         List<List<Supplier<ConfiguredFeature<?, ?>>>> list = biomegenerationsettings.getFeatures();

         for(int i = 0; i < list.size(); ++i) {
            if (i != GenerationStage.Decoration.UNDERGROUND_STRUCTURES.ordinal() && i != GenerationStage.Decoration.SURFACE_STRUCTURES.ordinal()) {
               for(Supplier<ConfiguredFeature<?, ?>> supplier : list.get(i)) {
                  biomegenerationsettings$builder.withFeature(i, supplier);
               }
            }
         }
      }

      BlockState[] ablockstate = this.getStates();

      for(int j = 0; j < ablockstate.length; ++j) {
         BlockState blockstate = ablockstate[j];
         if (blockstate != null && !Heightmap.Type.MOTION_BLOCKING.getHeightLimitPredicate().test(blockstate)) {
            this.states[j] = null;
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.withConfiguration(new FillLayerConfig(j, blockstate)));
         }
      }

      return (new Biome.Builder()).precipitation(biome.getPrecipitation()).category(biome.getCategory()).depth(biome.getDepth()).scale(biome.getScale()).temperature(biome.getTemperature()).downfall(biome.getDownfall()).setEffects(biome.getAmbience()).withGenerationSettings(biomegenerationsettings$builder.build()).withMobSpawnSettings(biome.getMobSpawnInfo()).build().setRegistryName(biome.getRegistryName());
   }

   public DimensionStructuresSettings func_236943_d_() {
      return this.field_236933_f_;
   }

   /**
    * Return the biome used on this preset.
    */
   public Biome getBiome() {
      return this.biomeToUse.get();
   }

   @OnlyIn(Dist.CLIENT)
   public void func_242870_a(Supplier<Biome> p_242870_1_) {
      this.biomeToUse = p_242870_1_;
   }

   /**
    * Return the list of layers on this preset.
    */
   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public BlockState[] getStates() {
      return this.states;
   }

   public void updateLayers() {
      Arrays.fill(this.states, 0, this.states.length, (Object)null);
      int i = 0;

      for(FlatLayerInfo flatlayerinfo : this.flatLayers) {
         flatlayerinfo.setMinY(i);
         i += flatlayerinfo.getLayerCount();
      }

      this.allAir = true;

      for(FlatLayerInfo flatlayerinfo1 : this.flatLayers) {
         for(int j = flatlayerinfo1.getMinY(); j < flatlayerinfo1.getMinY() + flatlayerinfo1.getLayerCount(); ++j) {
            BlockState blockstate = flatlayerinfo1.getLayerMaterial();
            if (!blockstate.isIn(Blocks.AIR)) {
               this.allAir = false;
               this.states[j] = blockstate;
            }
         }
      }

   }

   public static FlatGenerationSettings func_242869_a(Registry<Biome> p_242869_0_) {
      DimensionStructuresSettings dimensionstructuressettings = new DimensionStructuresSettings(Optional.of(DimensionStructuresSettings.field_236192_c_), Maps.newHashMap(ImmutableMap.of(Structure.VILLAGE, DimensionStructuresSettings.field_236191_b_.get(Structure.VILLAGE))));
      FlatGenerationSettings flatgenerationsettings = new FlatGenerationSettings(dimensionstructuressettings, p_242869_0_);
      flatgenerationsettings.biomeToUse = () -> {
         return p_242869_0_.getOrThrow(Biomes.PLAINS);
      };
      flatgenerationsettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      flatgenerationsettings.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
      flatgenerationsettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      flatgenerationsettings.updateLayers();
      return flatgenerationsettings;
   }
}
