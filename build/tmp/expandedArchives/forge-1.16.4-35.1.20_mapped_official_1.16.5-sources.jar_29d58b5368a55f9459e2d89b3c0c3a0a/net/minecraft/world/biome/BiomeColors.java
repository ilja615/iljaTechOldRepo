package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeColors {
   public static final ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;
   public static final ColorResolver FOLIAGE_COLOR_RESOLVER = (p_228362_0_, p_228362_1_, p_228362_3_) -> {
      return p_228362_0_.getFoliageColor();
   };
   public static final ColorResolver WATER_COLOR_RESOLVER = (p_228360_0_, p_228360_1_, p_228360_3_) -> {
      return p_228360_0_.getWaterColor();
   };

   private static int getAverageColor(IBlockDisplayReader p_228359_0_, BlockPos p_228359_1_, ColorResolver p_228359_2_) {
      return p_228359_0_.getBlockTint(p_228359_1_, p_228359_2_);
   }

   public static int getAverageGrassColor(IBlockDisplayReader p_228358_0_, BlockPos p_228358_1_) {
      return getAverageColor(p_228358_0_, p_228358_1_, GRASS_COLOR_RESOLVER);
   }

   public static int getAverageFoliageColor(IBlockDisplayReader p_228361_0_, BlockPos p_228361_1_) {
      return getAverageColor(p_228361_0_, p_228361_1_, FOLIAGE_COLOR_RESOLVER);
   }

   public static int getAverageWaterColor(IBlockDisplayReader p_228363_0_, BlockPos p_228363_1_) {
      return getAverageColor(p_228363_0_, p_228363_1_, WATER_COLOR_RESOLVER);
   }
}
