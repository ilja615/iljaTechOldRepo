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
   public static final Codec<FlatGenerationSettings> CODEC = RecordCodecBuilder.<FlatGenerationSettings>create((p_236938_0_) -> {
      return p_236938_0_.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((p_242874_0_) -> {
         return p_242874_0_.biomes;
      }), DimensionStructuresSettings.CODEC.fieldOf("structures").forGetter(FlatGenerationSettings::structureSettings), FlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(FlatGenerationSettings::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((p_241528_0_) -> {
         return p_241528_0_.addLakes;
      }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((p_242871_0_) -> {
         return p_242871_0_.decoration;
      }), Biome.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((p_242868_0_) -> {
         return Optional.of(p_242868_0_.biome);
      })).apply(p_236938_0_, FlatGenerationSettings::new);
   }).stable();
   private static final Map<Structure<?>, StructureFeature<?, ?>> STRUCTURE_FEATURES = Util.make(Maps.newHashMap(), (p_236940_0_) -> {
      p_236940_0_.put(Structure.MINESHAFT, StructureFeatures.MINESHAFT);
      p_236940_0_.put(Structure.VILLAGE, StructureFeatures.VILLAGE_PLAINS);
      p_236940_0_.put(Structure.STRONGHOLD, StructureFeatures.STRONGHOLD);
      p_236940_0_.put(Structure.SWAMP_HUT, StructureFeatures.SWAMP_HUT);
      p_236940_0_.put(Structure.DESERT_PYRAMID, StructureFeatures.DESERT_PYRAMID);
      p_236940_0_.put(Structure.JUNGLE_TEMPLE, StructureFeatures.JUNGLE_TEMPLE);
      p_236940_0_.put(Structure.IGLOO, StructureFeatures.IGLOO);
      p_236940_0_.put(Structure.OCEAN_RUIN, StructureFeatures.OCEAN_RUIN_COLD);
      p_236940_0_.put(Structure.SHIPWRECK, StructureFeatures.SHIPWRECK);
      p_236940_0_.put(Structure.OCEAN_MONUMENT, StructureFeatures.OCEAN_MONUMENT);
      p_236940_0_.put(Structure.END_CITY, StructureFeatures.END_CITY);
      p_236940_0_.put(Structure.WOODLAND_MANSION, StructureFeatures.WOODLAND_MANSION);
      p_236940_0_.put(Structure.NETHER_BRIDGE, StructureFeatures.NETHER_BRIDGE);
      p_236940_0_.put(Structure.PILLAGER_OUTPOST, StructureFeatures.PILLAGER_OUTPOST);
      p_236940_0_.put(Structure.RUINED_PORTAL, StructureFeatures.RUINED_PORTAL_STANDARD);
      p_236940_0_.put(Structure.BASTION_REMNANT, StructureFeatures.BASTION_REMNANT);
   });
   private final Registry<Biome> biomes;
   private final DimensionStructuresSettings structureSettings;
   private final List<FlatLayerInfo> layersInfo = Lists.newArrayList();
   private Supplier<Biome> biome;
   private final BlockState[] layers = new BlockState[256];
   private boolean voidGen;
   private boolean decoration = false;
   private boolean addLakes = false;

   public FlatGenerationSettings(Registry<Biome> p_i242012_1_, DimensionStructuresSettings p_i242012_2_, List<FlatLayerInfo> p_i242012_3_, boolean p_i242012_4_, boolean p_i242012_5_, Optional<Supplier<Biome>> p_i242012_6_) {
      this(p_i242012_2_, p_i242012_1_);
      if (p_i242012_4_) {
         this.setAddLakes();
      }

      if (p_i242012_5_) {
         this.setDecoration();
      }

      this.layersInfo.addAll(p_i242012_3_);
      this.updateLayers();
      if (!p_i242012_6_.isPresent()) {
         LOGGER.error("Unknown biome, defaulting to plains");
         this.biome = () -> {
            return p_i242012_1_.getOrThrow(Biomes.PLAINS);
         };
      } else {
         this.biome = p_i242012_6_.get();
      }

   }

   public FlatGenerationSettings(DimensionStructuresSettings p_i242011_1_, Registry<Biome> p_i242011_2_) {
      this.biomes = p_i242011_2_;
      this.structureSettings = p_i242011_1_;
      this.biome = () -> {
         return p_i242011_2_.getOrThrow(Biomes.PLAINS);
      };
   }

   @OnlyIn(Dist.CLIENT)
   public FlatGenerationSettings withStructureSettings(DimensionStructuresSettings p_236937_1_) {
      return this.withLayers(this.layersInfo, p_236937_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public FlatGenerationSettings withLayers(List<FlatLayerInfo> p_241527_1_, DimensionStructuresSettings p_241527_2_) {
      FlatGenerationSettings flatgenerationsettings = new FlatGenerationSettings(p_241527_2_, this.biomes);

      for(FlatLayerInfo flatlayerinfo : p_241527_1_) {
         flatgenerationsettings.layersInfo.add(new FlatLayerInfo(flatlayerinfo.getHeight(), flatlayerinfo.getBlockState().getBlock()));
         flatgenerationsettings.updateLayers();
      }

      flatgenerationsettings.setBiome(this.biome);
      if (this.decoration) {
         flatgenerationsettings.setDecoration();
      }

      if (this.addLakes) {
         flatgenerationsettings.setAddLakes();
      }

      return flatgenerationsettings;
   }

   public void setDecoration() {
      this.decoration = true;
   }

   public void setAddLakes() {
      this.addLakes = true;
   }

   public Biome getBiomeFromSettings() {
      Biome biome = this.getBiome();
      BiomeGenerationSettings biomegenerationsettings = biome.getGenerationSettings();
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).surfaceBuilder(biomegenerationsettings.getSurfaceBuilder());
      if (this.addLakes) {
         biomegenerationsettings$builder.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_WATER);
         biomegenerationsettings$builder.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
      }

      for(Entry<Structure<?>, StructureSeparationSettings> entry : this.structureSettings.structureConfig().entrySet()) {
         biomegenerationsettings$builder.addStructureStart(biomegenerationsettings.withBiomeConfig(STRUCTURE_FEATURES.get(entry.getKey())));
      }

      boolean flag = (!this.voidGen || this.biomes.getResourceKey(biome).equals(Optional.of(Biomes.THE_VOID))) && this.decoration;
      if (flag) {
         List<List<Supplier<ConfiguredFeature<?, ?>>>> list = biomegenerationsettings.features();

         for(int i = 0; i < list.size(); ++i) {
            if (i != GenerationStage.Decoration.UNDERGROUND_STRUCTURES.ordinal() && i != GenerationStage.Decoration.SURFACE_STRUCTURES.ordinal()) {
               for(Supplier<ConfiguredFeature<?, ?>> supplier : list.get(i)) {
                  biomegenerationsettings$builder.addFeature(i, supplier);
               }
            }
         }
      }

      BlockState[] ablockstate = this.getLayers();

      for(int j = 0; j < ablockstate.length; ++j) {
         BlockState blockstate = ablockstate[j];
         if (blockstate != null && !Heightmap.Type.MOTION_BLOCKING.isOpaque().test(blockstate)) {
            this.layers[j] = null;
            biomegenerationsettings$builder.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configured(new FillLayerConfig(j, blockstate)));
         }
      }

      return (new Biome.Builder()).precipitation(biome.getPrecipitation()).biomeCategory(biome.getBiomeCategory()).depth(biome.getDepth()).scale(biome.getScale()).temperature(biome.getBaseTemperature()).downfall(biome.getDownfall()).specialEffects(biome.getSpecialEffects()).generationSettings(biomegenerationsettings$builder.build()).mobSpawnSettings(biome.getMobSettings()).build().setRegistryName(biome.getRegistryName());
   }

   public DimensionStructuresSettings structureSettings() {
      return this.structureSettings;
   }

   public Biome getBiome() {
      return this.biome.get();
   }

   @OnlyIn(Dist.CLIENT)
   public void setBiome(Supplier<Biome> p_242870_1_) {
      this.biome = p_242870_1_;
   }

   public List<FlatLayerInfo> getLayersInfo() {
      return this.layersInfo;
   }

   public BlockState[] getLayers() {
      return this.layers;
   }

   public void updateLayers() {
      Arrays.fill(this.layers, 0, this.layers.length, (Object)null);
      int i = 0;

      for(FlatLayerInfo flatlayerinfo : this.layersInfo) {
         flatlayerinfo.setStart(i);
         i += flatlayerinfo.getHeight();
      }

      this.voidGen = true;

      for(FlatLayerInfo flatlayerinfo1 : this.layersInfo) {
         for(int j = flatlayerinfo1.getStart(); j < flatlayerinfo1.getStart() + flatlayerinfo1.getHeight(); ++j) {
            BlockState blockstate = flatlayerinfo1.getBlockState();
            if (!blockstate.is(Blocks.AIR)) {
               this.voidGen = false;
               this.layers[j] = blockstate;
            }
         }
      }

   }

   public static FlatGenerationSettings getDefault(Registry<Biome> p_242869_0_) {
      DimensionStructuresSettings dimensionstructuressettings = new DimensionStructuresSettings(Optional.of(DimensionStructuresSettings.DEFAULT_STRONGHOLD), Maps.newHashMap(ImmutableMap.of(Structure.VILLAGE, DimensionStructuresSettings.DEFAULTS.get(Structure.VILLAGE))));
      FlatGenerationSettings flatgenerationsettings = new FlatGenerationSettings(dimensionstructuressettings, p_242869_0_);
      flatgenerationsettings.biome = () -> {
         return p_242869_0_.getOrThrow(Biomes.PLAINS);
      };
      flatgenerationsettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      flatgenerationsettings.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
      flatgenerationsettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      flatgenerationsettings.updateLayers();
      return flatgenerationsettings;
   }
}
