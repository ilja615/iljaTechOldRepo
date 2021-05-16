package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class BiomeLayer implements IC0Transformer {
   private static final int[] LEGACY_WARM_BIOMES = new int[]{2, 4, 3, 6, 1, 5};
   private static final int[] WARM_BIOMES = new int[]{2, 2, 2, 35, 35, 1};
   private static final int[] MEDIUM_BIOMES = new int[]{4, 29, 3, 1, 27, 6};
   private static final int[] COLD_BIOMES = new int[]{4, 3, 5, 1};
   private static final int[] ICE_BIOMES = new int[]{12, 12, 12, 30};
   private int[] warmBiomes = WARM_BIOMES;
   private final boolean legacyDesert;
   private java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry>[] biomes = new java.util.ArrayList[net.minecraftforge.common.BiomeManager.BiomeType.values().length];

   public BiomeLayer(boolean p_i232147_1_) {
      this.legacyDesert = p_i232147_1_;
      for (net.minecraftforge.common.BiomeManager.BiomeType type : net.minecraftforge.common.BiomeManager.BiomeType.values())
         biomes[type.ordinal()] = new java.util.ArrayList<>(net.minecraftforge.common.BiomeManager.getBiomes(type));
   }

   public int apply(INoiseRandom p_202726_1_, int p_202726_2_) {
      int i = (p_202726_2_ & 3840) >> 8;
      p_202726_2_ = p_202726_2_ & -3841;
      if (!LayerUtil.isOcean(p_202726_2_) && p_202726_2_ != 14) {
         switch(p_202726_2_) {
         case 1:
            if (i > 0) {
               return p_202726_1_.nextRandom(3) == 0 ? 39 : 38;
            }

            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.DESERT, p_202726_1_);
         case 2:
            if (i > 0) {
               return 21;
            }

            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.WARM, p_202726_1_);
         case 3:
            if (i > 0) {
               return 32;
            }

            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.COOL, p_202726_1_);
         case 4:
            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.ICY, p_202726_1_);
         default:
            return 14;
         }
      } else {
         return p_202726_2_;
      }
   }

   private int getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType type, INoiseRandom context) {
      return net.minecraft.util.registry.WorldGenRegistries.BIOME.getId(
         net.minecraft.util.registry.WorldGenRegistries.BIOME.get(getBiome(type, context)));
   }
   protected net.minecraft.util.RegistryKey<net.minecraft.world.biome.Biome> getBiome(net.minecraftforge.common.BiomeManager.BiomeType type, INoiseRandom context) {
      if (type == net.minecraftforge.common.BiomeManager.BiomeType.DESERT && this.legacyDesert)
         type = net.minecraftforge.common.BiomeManager.BiomeType.DESERT_LEGACY;
      java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry> biomeList = biomes[type.ordinal()];
      int totalWeight = net.minecraft.util.WeightedRandom.getTotalWeight(biomeList);
      int weight = net.minecraftforge.common.BiomeManager.isTypeListModded(type) ? context.nextRandom(totalWeight) : context.nextRandom(totalWeight / 10) * 10;
      return net.minecraft.util.WeightedRandom.getWeightedItem(biomeList, weight).getKey();
   }
}
