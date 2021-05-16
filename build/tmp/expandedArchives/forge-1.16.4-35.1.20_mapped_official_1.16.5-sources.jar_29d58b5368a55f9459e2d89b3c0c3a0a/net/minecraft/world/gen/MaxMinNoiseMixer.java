package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.util.SharedSeedRandom;

public class MaxMinNoiseMixer {
   private final double valueFactor;
   private final OctavesNoiseGenerator first;
   private final OctavesNoiseGenerator second;

   public static MaxMinNoiseMixer create(SharedSeedRandom p_242930_0_, int p_242930_1_, DoubleList p_242930_2_) {
      return new MaxMinNoiseMixer(p_242930_0_, p_242930_1_, p_242930_2_);
   }

   private MaxMinNoiseMixer(SharedSeedRandom p_i242039_1_, int p_i242039_2_, DoubleList p_i242039_3_) {
      this.first = OctavesNoiseGenerator.create(p_i242039_1_, p_i242039_2_, p_i242039_3_);
      this.second = OctavesNoiseGenerator.create(p_i242039_1_, p_i242039_2_, p_i242039_3_);
      int i = Integer.MAX_VALUE;
      int j = Integer.MIN_VALUE;
      DoubleListIterator doublelistiterator = p_i242039_3_.iterator();

      while(doublelistiterator.hasNext()) {
         int k = doublelistiterator.nextIndex();
         double d0 = doublelistiterator.nextDouble();
         if (d0 != 0.0D) {
            i = Math.min(i, k);
            j = Math.max(j, k);
         }
      }

      this.valueFactor = 0.16666666666666666D / expectedDeviation(j - i);
   }

   private static double expectedDeviation(int p_237212_0_) {
      return 0.1D * (1.0D + 1.0D / (double)(p_237212_0_ + 1));
   }

   public double getValue(double p_237211_1_, double p_237211_3_, double p_237211_5_) {
      double d0 = p_237211_1_ * 1.0181268882175227D;
      double d1 = p_237211_3_ * 1.0181268882175227D;
      double d2 = p_237211_5_ * 1.0181268882175227D;
      return (this.first.getValue(p_237211_1_, p_237211_3_, p_237211_5_) + this.second.getValue(d0, d1, d2)) * this.valueFactor;
   }
}
