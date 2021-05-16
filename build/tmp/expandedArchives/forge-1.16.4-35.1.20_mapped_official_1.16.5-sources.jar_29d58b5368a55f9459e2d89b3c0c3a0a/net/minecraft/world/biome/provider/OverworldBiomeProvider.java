package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OverworldBiomeProvider extends BiomeProvider {
   public static final Codec<OverworldBiomeProvider> CODEC = RecordCodecBuilder.create((p_235302_0_) -> {
      return p_235302_0_.group(Codec.LONG.fieldOf("seed").stable().forGetter((p_235304_0_) -> {
         return p_235304_0_.seed;
      }), Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", Boolean.valueOf(false), Lifecycle.stable()).forGetter((p_235303_0_) -> {
         return p_235303_0_.legacyBiomeInitLayer;
      }), Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter((p_235301_0_) -> {
         return p_235301_0_.largeBiomes;
      }), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((p_242637_0_) -> {
         return p_242637_0_.biomes;
      })).apply(p_235302_0_, p_235302_0_.stable(OverworldBiomeProvider::new));
   });
   private final Layer noiseBiomeLayer;
   private static final List<RegistryKey<Biome>> POSSIBLE_BIOMES = ImmutableList.of(Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
   private final long seed;
   private final boolean legacyBiomeInitLayer;
   private final boolean largeBiomes;
   private final Registry<Biome> biomes;

   public OverworldBiomeProvider(long p_i241958_1_, boolean p_i241958_3_, boolean p_i241958_4_, Registry<Biome> p_i241958_5_) {
      super(POSSIBLE_BIOMES.stream().map((p_242638_1_) -> {
         return () -> {
            return p_i241958_5_.getOrThrow(p_242638_1_);
         };
      }));
      this.seed = p_i241958_1_;
      this.legacyBiomeInitLayer = p_i241958_3_;
      this.largeBiomes = p_i241958_4_;
      this.biomes = p_i241958_5_;
      this.noiseBiomeLayer = LayerUtil.getDefaultLayer(p_i241958_1_, p_i241958_3_, p_i241958_4_ ? 6 : 4, 4);
   }

   protected Codec<? extends BiomeProvider> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public BiomeProvider withSeed(long p_230320_1_) {
      return new OverworldBiomeProvider(p_230320_1_, this.legacyBiomeInitLayer, this.largeBiomes, this.biomes);
   }

   public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      return this.noiseBiomeLayer.get(this.biomes, p_225526_1_, p_225526_3_);
   }
}
