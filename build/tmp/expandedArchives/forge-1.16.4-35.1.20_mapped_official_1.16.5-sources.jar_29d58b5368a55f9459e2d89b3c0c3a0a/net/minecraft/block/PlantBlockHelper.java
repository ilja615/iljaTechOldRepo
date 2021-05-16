package net.minecraft.block;

import java.util.Random;

public class PlantBlockHelper {
   public static boolean isValidGrowthState(BlockState p_235514_0_) {
      return p_235514_0_.isAir();
   }

   public static int getBlocksToGrowWhenBonemealed(Random p_235515_0_) {
      double d0 = 1.0D;

      int i;
      for(i = 0; p_235515_0_.nextDouble() < d0; ++i) {
         d0 *= 0.826D;
      }

      return i;
   }
}
