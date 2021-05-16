package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.SharedSeedRandom;

public class PerlinNoiseGenerator implements INoiseGenerator {
   private final SimplexNoiseGenerator[] noiseLevels;
   private final double highestFreqValueFactor;
   private final double highestFreqInputFactor;

   public PerlinNoiseGenerator(SharedSeedRandom p_i232144_1_, IntStream p_i232144_2_) {
      this(p_i232144_1_, p_i232144_2_.boxed().collect(ImmutableList.toImmutableList()));
   }

   public PerlinNoiseGenerator(SharedSeedRandom p_i232143_1_, List<Integer> p_i232143_2_) {
      this(p_i232143_1_, new IntRBTreeSet(p_i232143_2_));
   }

   private PerlinNoiseGenerator(SharedSeedRandom p_i225881_1_, IntSortedSet p_i225881_2_) {
      if (p_i225881_2_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int i = -p_i225881_2_.firstInt();
         int j = p_i225881_2_.lastInt();
         int k = i + j + 1;
         if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            SimplexNoiseGenerator simplexnoisegenerator = new SimplexNoiseGenerator(p_i225881_1_);
            int l = j;
            this.noiseLevels = new SimplexNoiseGenerator[k];
            if (j >= 0 && j < k && p_i225881_2_.contains(0)) {
               this.noiseLevels[j] = simplexnoisegenerator;
            }

            for(int i1 = j + 1; i1 < k; ++i1) {
               if (i1 >= 0 && p_i225881_2_.contains(l - i1)) {
                  this.noiseLevels[i1] = new SimplexNoiseGenerator(p_i225881_1_);
               } else {
                  p_i225881_1_.consumeCount(262);
               }
            }

            if (j > 0) {
               long k1 = (long)(simplexnoisegenerator.getValue(simplexnoisegenerator.xo, simplexnoisegenerator.yo, simplexnoisegenerator.zo) * (double)9.223372E18F);
               SharedSeedRandom sharedseedrandom = new SharedSeedRandom(k1);

               for(int j1 = l - 1; j1 >= 0; --j1) {
                  if (j1 < k && p_i225881_2_.contains(l - j1)) {
                     this.noiseLevels[j1] = new SimplexNoiseGenerator(sharedseedrandom);
                  } else {
                     sharedseedrandom.consumeCount(262);
                  }
               }
            }

            this.highestFreqInputFactor = Math.pow(2.0D, (double)j);
            this.highestFreqValueFactor = 1.0D / (Math.pow(2.0D, (double)k) - 1.0D);
         }
      }
   }

   public double getValue(double p_215464_1_, double p_215464_3_, boolean p_215464_5_) {
      double d0 = 0.0D;
      double d1 = this.highestFreqInputFactor;
      double d2 = this.highestFreqValueFactor;

      for(SimplexNoiseGenerator simplexnoisegenerator : this.noiseLevels) {
         if (simplexnoisegenerator != null) {
            d0 += simplexnoisegenerator.getValue(p_215464_1_ * d1 + (p_215464_5_ ? simplexnoisegenerator.xo : 0.0D), p_215464_3_ * d1 + (p_215464_5_ ? simplexnoisegenerator.yo : 0.0D)) * d2;
         }

         d1 /= 2.0D;
         d2 *= 2.0D;
      }

      return d0;
   }

   public double getSurfaceNoiseValue(double p_215460_1_, double p_215460_3_, double p_215460_5_, double p_215460_7_) {
      return this.getValue(p_215460_1_, p_215460_3_, true) * 0.55D;
   }
}
