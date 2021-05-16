package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public final class ImprovedNoiseGenerator {
   private final byte[] p;
   public final double xo;
   public final double yo;
   public final double zo;

   public ImprovedNoiseGenerator(Random p_i45469_1_) {
      this.xo = p_i45469_1_.nextDouble() * 256.0D;
      this.yo = p_i45469_1_.nextDouble() * 256.0D;
      this.zo = p_i45469_1_.nextDouble() * 256.0D;
      this.p = new byte[256];

      for(int i = 0; i < 256; ++i) {
         this.p[i] = (byte)i;
      }

      for(int k = 0; k < 256; ++k) {
         int j = p_i45469_1_.nextInt(256 - k);
         byte b0 = this.p[k];
         this.p[k] = this.p[k + j];
         this.p[k + j] = b0;
      }

   }

   public double noise(double p_215456_1_, double p_215456_3_, double p_215456_5_, double p_215456_7_, double p_215456_9_) {
      double d0 = p_215456_1_ + this.xo;
      double d1 = p_215456_3_ + this.yo;
      double d2 = p_215456_5_ + this.zo;
      int i = MathHelper.floor(d0);
      int j = MathHelper.floor(d1);
      int k = MathHelper.floor(d2);
      double d3 = d0 - (double)i;
      double d4 = d1 - (double)j;
      double d5 = d2 - (double)k;
      double d6 = MathHelper.smoothstep(d3);
      double d7 = MathHelper.smoothstep(d4);
      double d8 = MathHelper.smoothstep(d5);
      double d9;
      if (p_215456_7_ != 0.0D) {
         double d10 = Math.min(p_215456_9_, d4);
         d9 = (double)MathHelper.floor(d10 / p_215456_7_) * p_215456_7_;
      } else {
         d9 = 0.0D;
      }

      return this.sampleAndLerp(i, j, k, d3, d4 - d9, d5, d6, d7, d8);
   }

   private static double gradDot(int p_215457_0_, double p_215457_1_, double p_215457_3_, double p_215457_5_) {
      int i = p_215457_0_ & 15;
      return SimplexNoiseGenerator.dot(SimplexNoiseGenerator.GRADIENT[i], p_215457_1_, p_215457_3_, p_215457_5_);
   }

   private int p(int p_215458_1_) {
      return this.p[p_215458_1_ & 255] & 255;
   }

   public double sampleAndLerp(int p_215459_1_, int p_215459_2_, int p_215459_3_, double p_215459_4_, double p_215459_6_, double p_215459_8_, double p_215459_10_, double p_215459_12_, double p_215459_14_) {
      int i = this.p(p_215459_1_) + p_215459_2_;
      int j = this.p(i) + p_215459_3_;
      int k = this.p(i + 1) + p_215459_3_;
      int l = this.p(p_215459_1_ + 1) + p_215459_2_;
      int i1 = this.p(l) + p_215459_3_;
      int j1 = this.p(l + 1) + p_215459_3_;
      double d0 = gradDot(this.p(j), p_215459_4_, p_215459_6_, p_215459_8_);
      double d1 = gradDot(this.p(i1), p_215459_4_ - 1.0D, p_215459_6_, p_215459_8_);
      double d2 = gradDot(this.p(k), p_215459_4_, p_215459_6_ - 1.0D, p_215459_8_);
      double d3 = gradDot(this.p(j1), p_215459_4_ - 1.0D, p_215459_6_ - 1.0D, p_215459_8_);
      double d4 = gradDot(this.p(j + 1), p_215459_4_, p_215459_6_, p_215459_8_ - 1.0D);
      double d5 = gradDot(this.p(i1 + 1), p_215459_4_ - 1.0D, p_215459_6_, p_215459_8_ - 1.0D);
      double d6 = gradDot(this.p(k + 1), p_215459_4_, p_215459_6_ - 1.0D, p_215459_8_ - 1.0D);
      double d7 = gradDot(this.p(j1 + 1), p_215459_4_ - 1.0D, p_215459_6_ - 1.0D, p_215459_8_ - 1.0D);
      return MathHelper.lerp3(p_215459_10_, p_215459_12_, p_215459_14_, d0, d1, d2, d3, d4, d5, d6, d7);
   }
}
