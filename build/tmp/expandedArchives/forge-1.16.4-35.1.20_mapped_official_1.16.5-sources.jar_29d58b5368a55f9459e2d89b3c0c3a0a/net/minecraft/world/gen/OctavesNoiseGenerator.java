package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;

public class OctavesNoiseGenerator implements INoiseGenerator {
   private final ImprovedNoiseGenerator[] noiseLevels;
   private final DoubleList amplitudes;
   private final double lowestFreqValueFactor;
   private final double lowestFreqInputFactor;

   public OctavesNoiseGenerator(SharedSeedRandom p_i232142_1_, IntStream p_i232142_2_) {
      this(p_i232142_1_, p_i232142_2_.boxed().collect(ImmutableList.toImmutableList()));
   }

   public OctavesNoiseGenerator(SharedSeedRandom p_i232141_1_, List<Integer> p_i232141_2_) {
      this(p_i232141_1_, new IntRBTreeSet(p_i232141_2_));
   }

   public static OctavesNoiseGenerator create(SharedSeedRandom p_242932_0_, int p_242932_1_, DoubleList p_242932_2_) {
      return new OctavesNoiseGenerator(p_242932_0_, Pair.of(p_242932_1_, p_242932_2_));
   }

   private static Pair<Integer, DoubleList> makeAmplitudes(IntSortedSet p_242933_0_) {
      if (p_242933_0_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int i = -p_242933_0_.firstInt();
         int j = p_242933_0_.lastInt();
         int k = i + j + 1;
         if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            DoubleList doublelist = new DoubleArrayList(new double[k]);
            IntBidirectionalIterator intbidirectionaliterator = p_242933_0_.iterator();

            while(intbidirectionaliterator.hasNext()) {
               int l = intbidirectionaliterator.nextInt();
               doublelist.set(l + i, 1.0D);
            }

            return Pair.of(-i, doublelist);
         }
      }
   }

   private OctavesNoiseGenerator(SharedSeedRandom p_i225879_1_, IntSortedSet p_i225879_2_) {
      this(p_i225879_1_, makeAmplitudes(p_i225879_2_));
   }

   private OctavesNoiseGenerator(SharedSeedRandom p_i242040_1_, Pair<Integer, DoubleList> p_i242040_2_) {
      int i = p_i242040_2_.getFirst();
      this.amplitudes = p_i242040_2_.getSecond();
      ImprovedNoiseGenerator improvednoisegenerator = new ImprovedNoiseGenerator(p_i242040_1_);
      int j = this.amplitudes.size();
      int k = -i;
      this.noiseLevels = new ImprovedNoiseGenerator[j];
      if (k >= 0 && k < j) {
         double d0 = this.amplitudes.getDouble(k);
         if (d0 != 0.0D) {
            this.noiseLevels[k] = improvednoisegenerator;
         }
      }

      for(int i1 = k - 1; i1 >= 0; --i1) {
         if (i1 < j) {
            double d1 = this.amplitudes.getDouble(i1);
            if (d1 != 0.0D) {
               this.noiseLevels[i1] = new ImprovedNoiseGenerator(p_i242040_1_);
            } else {
               p_i242040_1_.consumeCount(262);
            }
         } else {
            p_i242040_1_.consumeCount(262);
         }
      }

      if (k < j - 1) {
         long j1 = (long)(improvednoisegenerator.noise(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * (double)9.223372E18F);
         SharedSeedRandom sharedseedrandom = new SharedSeedRandom(j1);

         for(int l = k + 1; l < j; ++l) {
            if (l >= 0) {
               double d2 = this.amplitudes.getDouble(l);
               if (d2 != 0.0D) {
                  this.noiseLevels[l] = new ImprovedNoiseGenerator(sharedseedrandom);
               } else {
                  sharedseedrandom.consumeCount(262);
               }
            } else {
               sharedseedrandom.consumeCount(262);
            }
         }
      }

      this.lowestFreqInputFactor = Math.pow(2.0D, (double)(-k));
      this.lowestFreqValueFactor = Math.pow(2.0D, (double)(j - 1)) / (Math.pow(2.0D, (double)j) - 1.0D);
   }

   public double getValue(double p_205563_1_, double p_205563_3_, double p_205563_5_) {
      return this.getValue(p_205563_1_, p_205563_3_, p_205563_5_, 0.0D, 0.0D, false);
   }

   public double getValue(double p_215462_1_, double p_215462_3_, double p_215462_5_, double p_215462_7_, double p_215462_9_, boolean p_215462_11_) {
      double d0 = 0.0D;
      double d1 = this.lowestFreqInputFactor;
      double d2 = this.lowestFreqValueFactor;

      for(int i = 0; i < this.noiseLevels.length; ++i) {
         ImprovedNoiseGenerator improvednoisegenerator = this.noiseLevels[i];
         if (improvednoisegenerator != null) {
            d0 += this.amplitudes.getDouble(i) * improvednoisegenerator.noise(wrap(p_215462_1_ * d1), p_215462_11_ ? -improvednoisegenerator.yo : wrap(p_215462_3_ * d1), wrap(p_215462_5_ * d1), p_215462_7_ * d1, p_215462_9_ * d1) * d2;
         }

         d1 *= 2.0D;
         d2 /= 2.0D;
      }

      return d0;
   }

   @Nullable
   public ImprovedNoiseGenerator getOctaveNoise(int p_215463_1_) {
      return this.noiseLevels[this.noiseLevels.length - 1 - p_215463_1_];
   }

   public static double wrap(double p_215461_0_) {
      return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   public double getSurfaceNoiseValue(double p_215460_1_, double p_215460_3_, double p_215460_5_, double p_215460_7_) {
      return this.getValue(p_215460_1_, p_215460_3_, 0.0D, p_215460_5_, p_215460_7_, false);
   }
}
