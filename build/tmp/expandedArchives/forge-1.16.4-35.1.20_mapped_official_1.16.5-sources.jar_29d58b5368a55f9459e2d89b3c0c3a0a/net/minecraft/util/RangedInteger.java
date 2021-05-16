package net.minecraft.util;

import java.util.Random;

public class RangedInteger {
   private final int minInclusive;
   private final int maxInclusive;

   public RangedInteger(int p_i231439_1_, int p_i231439_2_) {
      if (p_i231439_2_ < p_i231439_1_) {
         throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + p_i231439_1_ + ", Given max: " + p_i231439_2_);
      } else {
         this.minInclusive = p_i231439_1_;
         this.maxInclusive = p_i231439_2_;
      }
   }

   public static RangedInteger of(int p_233017_0_, int p_233017_1_) {
      return new RangedInteger(p_233017_0_, p_233017_1_);
   }

   public int randomValue(Random p_233018_1_) {
      return this.minInclusive == this.maxInclusive ? this.minInclusive : p_233018_1_.nextInt(this.maxInclusive - this.minInclusive + 1) + this.minInclusive;
   }

   public int getMinInclusive() {
      return this.minInclusive;
   }

   public int getMaxInclusive() {
      return this.maxInclusive;
   }

   public String toString() {
      return "IntRange[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
