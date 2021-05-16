package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
   public static final float SQRT_OF_TWO = sqrt(2.0F);
   private static final float[] SIN = Util.make(new float[65536], (p_203445_0_) -> {
      for(int i = 0; i < p_203445_0_.length; ++i) {
         p_203445_0_[i] = (float)Math.sin((double)i * Math.PI * 2.0D / 65536.0D);
      }

   });
   private static final Random RANDOM = new Random();
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
   private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
   private static final double[] ASIN_TAB = new double[257];
   private static final double[] COS_TAB = new double[257];

   public static float sin(float p_76126_0_) {
      return SIN[(int)(p_76126_0_ * 10430.378F) & '\uffff'];
   }

   public static float cos(float p_76134_0_) {
      return SIN[(int)(p_76134_0_ * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static float sqrt(float p_76129_0_) {
      return (float)Math.sqrt((double)p_76129_0_);
   }

   public static float sqrt(double p_76133_0_) {
      return (float)Math.sqrt(p_76133_0_);
   }

   public static int floor(float p_76141_0_) {
      int i = (int)p_76141_0_;
      return p_76141_0_ < (float)i ? i - 1 : i;
   }

   @OnlyIn(Dist.CLIENT)
   public static int fastFloor(double p_76140_0_) {
      return (int)(p_76140_0_ + 1024.0D) - 1024;
   }

   public static int floor(double p_76128_0_) {
      int i = (int)p_76128_0_;
      return p_76128_0_ < (double)i ? i - 1 : i;
   }

   public static long lfloor(double p_76124_0_) {
      long i = (long)p_76124_0_;
      return p_76124_0_ < (double)i ? i - 1L : i;
   }

   public static float abs(float p_76135_0_) {
      return Math.abs(p_76135_0_);
   }

   public static int abs(int p_76130_0_) {
      return Math.abs(p_76130_0_);
   }

   public static int ceil(float p_76123_0_) {
      int i = (int)p_76123_0_;
      return p_76123_0_ > (float)i ? i + 1 : i;
   }

   public static int ceil(double p_76143_0_) {
      int i = (int)p_76143_0_;
      return p_76143_0_ > (double)i ? i + 1 : i;
   }

   public static int clamp(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
      if (p_76125_0_ < p_76125_1_) {
         return p_76125_1_;
      } else {
         return p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static long clamp(long p_226163_0_, long p_226163_2_, long p_226163_4_) {
      if (p_226163_0_ < p_226163_2_) {
         return p_226163_2_;
      } else {
         return p_226163_0_ > p_226163_4_ ? p_226163_4_ : p_226163_0_;
      }
   }

   public static float clamp(float p_76131_0_, float p_76131_1_, float p_76131_2_) {
      if (p_76131_0_ < p_76131_1_) {
         return p_76131_1_;
      } else {
         return p_76131_0_ > p_76131_2_ ? p_76131_2_ : p_76131_0_;
      }
   }

   public static double clamp(double p_151237_0_, double p_151237_2_, double p_151237_4_) {
      if (p_151237_0_ < p_151237_2_) {
         return p_151237_2_;
      } else {
         return p_151237_0_ > p_151237_4_ ? p_151237_4_ : p_151237_0_;
      }
   }

   public static double clampedLerp(double p_151238_0_, double p_151238_2_, double p_151238_4_) {
      if (p_151238_4_ < 0.0D) {
         return p_151238_0_;
      } else {
         return p_151238_4_ > 1.0D ? p_151238_2_ : lerp(p_151238_4_, p_151238_0_, p_151238_2_);
      }
   }

   public static double absMax(double p_76132_0_, double p_76132_2_) {
      if (p_76132_0_ < 0.0D) {
         p_76132_0_ = -p_76132_0_;
      }

      if (p_76132_2_ < 0.0D) {
         p_76132_2_ = -p_76132_2_;
      }

      return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
   }

   public static int intFloorDiv(int p_76137_0_, int p_76137_1_) {
      return Math.floorDiv(p_76137_0_, p_76137_1_);
   }

   public static int nextInt(Random p_76136_0_, int p_76136_1_, int p_76136_2_) {
      return p_76136_1_ >= p_76136_2_ ? p_76136_1_ : p_76136_0_.nextInt(p_76136_2_ - p_76136_1_ + 1) + p_76136_1_;
   }

   public static float nextFloat(Random p_151240_0_, float p_151240_1_, float p_151240_2_) {
      return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
   }

   public static double nextDouble(Random p_82716_0_, double p_82716_1_, double p_82716_3_) {
      return p_82716_1_ >= p_82716_3_ ? p_82716_1_ : p_82716_0_.nextDouble() * (p_82716_3_ - p_82716_1_) + p_82716_1_;
   }

   public static double average(long[] p_76127_0_) {
      long i = 0L;

      for(long j : p_76127_0_) {
         i += j;
      }

      return (double)i / (double)p_76127_0_.length;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean equal(float p_180185_0_, float p_180185_1_) {
      return Math.abs(p_180185_1_ - p_180185_0_) < 1.0E-5F;
   }

   public static boolean equal(double p_219806_0_, double p_219806_2_) {
      return Math.abs(p_219806_2_ - p_219806_0_) < (double)1.0E-5F;
   }

   public static int positiveModulo(int p_180184_0_, int p_180184_1_) {
      return Math.floorMod(p_180184_0_, p_180184_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static float positiveModulo(float p_188207_0_, float p_188207_1_) {
      return (p_188207_0_ % p_188207_1_ + p_188207_1_) % p_188207_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static double positiveModulo(double p_191273_0_, double p_191273_2_) {
      return (p_191273_0_ % p_191273_2_ + p_191273_2_) % p_191273_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static int wrapDegrees(int p_188209_0_) {
      int i = p_188209_0_ % 360;
      if (i >= 180) {
         i -= 360;
      }

      if (i < -180) {
         i += 360;
      }

      return i;
   }

   public static float wrapDegrees(float p_76142_0_) {
      float f = p_76142_0_ % 360.0F;
      if (f >= 180.0F) {
         f -= 360.0F;
      }

      if (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   public static double wrapDegrees(double p_76138_0_) {
      double d0 = p_76138_0_ % 360.0D;
      if (d0 >= 180.0D) {
         d0 -= 360.0D;
      }

      if (d0 < -180.0D) {
         d0 += 360.0D;
      }

      return d0;
   }

   public static float degreesDifference(float p_203302_0_, float p_203302_1_) {
      return wrapDegrees(p_203302_1_ - p_203302_0_);
   }

   public static float degreesDifferenceAbs(float p_203301_0_, float p_203301_1_) {
      return abs(degreesDifference(p_203301_0_, p_203301_1_));
   }

   public static float rotateIfNecessary(float p_219800_0_, float p_219800_1_, float p_219800_2_) {
      float f = degreesDifference(p_219800_0_, p_219800_1_);
      float f1 = clamp(f, -p_219800_2_, p_219800_2_);
      return p_219800_1_ - f1;
   }

   public static float approach(float p_203300_0_, float p_203300_1_, float p_203300_2_) {
      p_203300_2_ = abs(p_203300_2_);
      return p_203300_0_ < p_203300_1_ ? clamp(p_203300_0_ + p_203300_2_, p_203300_0_, p_203300_1_) : clamp(p_203300_0_ - p_203300_2_, p_203300_1_, p_203300_0_);
   }

   public static float approachDegrees(float p_203303_0_, float p_203303_1_, float p_203303_2_) {
      float f = degreesDifference(p_203303_0_, p_203303_1_);
      return approach(p_203303_0_, p_203303_0_ + f, p_203303_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static int getInt(String p_82715_0_, int p_82715_1_) {
      return NumberUtils.toInt(p_82715_0_, p_82715_1_);
   }

   public static int smallestEncompassingPowerOfTwo(int p_151236_0_) {
      int i = p_151236_0_ - 1;
      i = i | i >> 1;
      i = i | i >> 2;
      i = i | i >> 4;
      i = i | i >> 8;
      i = i | i >> 16;
      return i + 1;
   }

   public static boolean isPowerOfTwo(int p_151235_0_) {
      return p_151235_0_ != 0 && (p_151235_0_ & p_151235_0_ - 1) == 0;
   }

   public static int ceillog2(int p_151241_0_) {
      p_151241_0_ = isPowerOfTwo(p_151241_0_) ? p_151241_0_ : smallestEncompassingPowerOfTwo(p_151241_0_);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)p_151241_0_ * 125613361L >> 27) & 31];
   }

   public static int log2(int p_151239_0_) {
      return ceillog2(p_151239_0_) - (isPowerOfTwo(p_151239_0_) ? 0 : 1);
   }

   public static int roundUp(int p_154354_0_, int p_154354_1_) {
      if (p_154354_1_ == 0) {
         return 0;
      } else if (p_154354_0_ == 0) {
         return p_154354_1_;
      } else {
         if (p_154354_0_ < 0) {
            p_154354_1_ *= -1;
         }

         int i = p_154354_0_ % p_154354_1_;
         return i == 0 ? p_154354_0_ : p_154354_0_ + p_154354_1_ - i;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static int color(float p_180183_0_, float p_180183_1_, float p_180183_2_) {
      return color(floor(p_180183_0_ * 255.0F), floor(p_180183_1_ * 255.0F), floor(p_180183_2_ * 255.0F));
   }

   @OnlyIn(Dist.CLIENT)
   public static int color(int p_180181_0_, int p_180181_1_, int p_180181_2_) {
      int lvt_3_1_ = (p_180181_0_ << 8) + p_180181_1_;
      return (lvt_3_1_ << 8) + p_180181_2_;
   }

   public static float frac(float p_226164_0_) {
      return p_226164_0_ - (float)floor(p_226164_0_);
   }

   public static double frac(double p_181162_0_) {
      return p_181162_0_ - (double)lfloor(p_181162_0_);
   }

   public static long getSeed(Vector3i p_180186_0_) {
      return getSeed(p_180186_0_.getX(), p_180186_0_.getY(), p_180186_0_.getZ());
   }

   public static long getSeed(int p_180187_0_, int p_180187_1_, int p_180187_2_) {
      long i = (long)(p_180187_0_ * 3129871) ^ (long)p_180187_2_ * 116129781L ^ (long)p_180187_1_;
      i = i * i * 42317861L + i * 11L;
      return i >> 16;
   }

   public static UUID createInsecureUUID(Random p_180182_0_) {
      long i = p_180182_0_.nextLong() & -61441L | 16384L;
      long j = p_180182_0_.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(i, j);
   }

   public static UUID createInsecureUUID() {
      return createInsecureUUID(RANDOM);
   }

   public static double inverseLerp(double p_233020_0_, double p_233020_2_, double p_233020_4_) {
      return (p_233020_0_ - p_233020_2_) / (p_233020_4_ - p_233020_2_);
   }

   public static double atan2(double p_181159_0_, double p_181159_2_) {
      double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
      if (Double.isNaN(d0)) {
         return Double.NaN;
      } else {
         boolean flag = p_181159_0_ < 0.0D;
         if (flag) {
            p_181159_0_ = -p_181159_0_;
         }

         boolean flag1 = p_181159_2_ < 0.0D;
         if (flag1) {
            p_181159_2_ = -p_181159_2_;
         }

         boolean flag2 = p_181159_0_ > p_181159_2_;
         if (flag2) {
            double d1 = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = d1;
         }

         double d9 = fastInvSqrt(d0);
         p_181159_2_ = p_181159_2_ * d9;
         p_181159_0_ = p_181159_0_ * d9;
         double d2 = FRAC_BIAS + p_181159_0_;
         int i = (int)Double.doubleToRawLongBits(d2);
         double d3 = ASIN_TAB[i];
         double d4 = COS_TAB[i];
         double d5 = d2 - FRAC_BIAS;
         double d6 = p_181159_0_ * d4 - p_181159_2_ * d5;
         double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
         double d8 = d3 + d7;
         if (flag2) {
            d8 = (Math.PI / 2D) - d8;
         }

         if (flag1) {
            d8 = Math.PI - d8;
         }

         if (flag) {
            d8 = -d8;
         }

         return d8;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float fastInvSqrt(float p_226165_0_) {
      float f = 0.5F * p_226165_0_;
      int i = Float.floatToIntBits(p_226165_0_);
      i = 1597463007 - (i >> 1);
      p_226165_0_ = Float.intBitsToFloat(i);
      return p_226165_0_ * (1.5F - f * p_226165_0_ * p_226165_0_);
   }

   public static double fastInvSqrt(double p_181161_0_) {
      double d0 = 0.5D * p_181161_0_;
      long i = Double.doubleToRawLongBits(p_181161_0_);
      i = 6910469410427058090L - (i >> 1);
      p_181161_0_ = Double.longBitsToDouble(i);
      return p_181161_0_ * (1.5D - d0 * p_181161_0_ * p_181161_0_);
   }

   @OnlyIn(Dist.CLIENT)
   public static float fastInvCubeRoot(float p_226166_0_) {
      int i = Float.floatToIntBits(p_226166_0_);
      i = 1419967116 - i / 3;
      float f = Float.intBitsToFloat(i);
      f = 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
      return 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
   }

   public static int hsvToRgb(float p_181758_0_, float p_181758_1_, float p_181758_2_) {
      int i = (int)(p_181758_0_ * 6.0F) % 6;
      float f = p_181758_0_ * 6.0F - (float)i;
      float f1 = p_181758_2_ * (1.0F - p_181758_1_);
      float f2 = p_181758_2_ * (1.0F - f * p_181758_1_);
      float f3 = p_181758_2_ * (1.0F - (1.0F - f) * p_181758_1_);
      float f4;
      float f5;
      float f6;
      switch(i) {
      case 0:
         f4 = p_181758_2_;
         f5 = f3;
         f6 = f1;
         break;
      case 1:
         f4 = f2;
         f5 = p_181758_2_;
         f6 = f1;
         break;
      case 2:
         f4 = f1;
         f5 = p_181758_2_;
         f6 = f3;
         break;
      case 3:
         f4 = f1;
         f5 = f2;
         f6 = p_181758_2_;
         break;
      case 4:
         f4 = f3;
         f5 = f1;
         f6 = p_181758_2_;
         break;
      case 5:
         f4 = p_181758_2_;
         f5 = f1;
         f6 = f2;
         break;
      default:
         throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_181758_0_ + ", " + p_181758_1_ + ", " + p_181758_2_);
      }

      int j = clamp((int)(f4 * 255.0F), 0, 255);
      int k = clamp((int)(f5 * 255.0F), 0, 255);
      int l = clamp((int)(f6 * 255.0F), 0, 255);
      return j << 16 | k << 8 | l;
   }

   public static int murmurHash3Mixer(int p_188208_0_) {
      p_188208_0_ = p_188208_0_ ^ p_188208_0_ >>> 16;
      p_188208_0_ = p_188208_0_ * -2048144789;
      p_188208_0_ = p_188208_0_ ^ p_188208_0_ >>> 13;
      p_188208_0_ = p_188208_0_ * -1028477387;
      return p_188208_0_ ^ p_188208_0_ >>> 16;
   }

   public static int binarySearch(int p_199093_0_, int p_199093_1_, IntPredicate p_199093_2_) {
      int i = p_199093_1_ - p_199093_0_;

      while(i > 0) {
         int j = i / 2;
         int k = p_199093_0_ + j;
         if (p_199093_2_.test(k)) {
            i = j;
         } else {
            p_199093_0_ = k + 1;
            i -= j + 1;
         }
      }

      return p_199093_0_;
   }

   public static float lerp(float p_219799_0_, float p_219799_1_, float p_219799_2_) {
      return p_219799_1_ + p_219799_0_ * (p_219799_2_ - p_219799_1_);
   }

   public static double lerp(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
      return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
   }

   public static double lerp2(double p_219804_0_, double p_219804_2_, double p_219804_4_, double p_219804_6_, double p_219804_8_, double p_219804_10_) {
      return lerp(p_219804_2_, lerp(p_219804_0_, p_219804_4_, p_219804_6_), lerp(p_219804_0_, p_219804_8_, p_219804_10_));
   }

   public static double lerp3(double p_219807_0_, double p_219807_2_, double p_219807_4_, double p_219807_6_, double p_219807_8_, double p_219807_10_, double p_219807_12_, double p_219807_14_, double p_219807_16_, double p_219807_18_, double p_219807_20_) {
      return lerp(p_219807_4_, lerp2(p_219807_0_, p_219807_2_, p_219807_6_, p_219807_8_, p_219807_10_, p_219807_12_), lerp2(p_219807_0_, p_219807_2_, p_219807_14_, p_219807_16_, p_219807_18_, p_219807_20_));
   }

   public static double smoothstep(double p_219801_0_) {
      return p_219801_0_ * p_219801_0_ * p_219801_0_ * (p_219801_0_ * (p_219801_0_ * 6.0D - 15.0D) + 10.0D);
   }

   public static int sign(double p_219802_0_) {
      if (p_219802_0_ == 0.0D) {
         return 0;
      } else {
         return p_219802_0_ > 0.0D ? 1 : -1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float rotLerp(float p_219805_0_, float p_219805_1_, float p_219805_2_) {
      return p_219805_1_ + p_219805_0_ * wrapDegrees(p_219805_2_ - p_219805_1_);
   }

   @Deprecated
   public static float rotlerp(float p_226167_0_, float p_226167_1_, float p_226167_2_) {
      float f;
      for(f = p_226167_1_ - p_226167_0_; f < -180.0F; f += 360.0F) {
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_226167_0_ + p_226167_2_ * f;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public static float rotWrap(double p_226168_0_) {
      while(p_226168_0_ >= 180.0D) {
         p_226168_0_ -= 360.0D;
      }

      while(p_226168_0_ < -180.0D) {
         p_226168_0_ += 360.0D;
      }

      return (float)p_226168_0_;
   }

   @OnlyIn(Dist.CLIENT)
   public static float triangleWave(float p_233021_0_, float p_233021_1_) {
      return (Math.abs(p_233021_0_ % p_233021_1_ - p_233021_1_ * 0.5F) - p_233021_1_ * 0.25F) / (p_233021_1_ * 0.25F);
   }

   public static float square(float p_233022_0_) {
      return p_233022_0_ * p_233022_0_;
   }

   static {
      for(int i = 0; i < 257; ++i) {
         double d0 = (double)i / 256.0D;
         double d1 = Math.asin(d0);
         COS_TAB[i] = Math.cos(d1);
         ASIN_TAB[i] = d1;
      }

   }
}
