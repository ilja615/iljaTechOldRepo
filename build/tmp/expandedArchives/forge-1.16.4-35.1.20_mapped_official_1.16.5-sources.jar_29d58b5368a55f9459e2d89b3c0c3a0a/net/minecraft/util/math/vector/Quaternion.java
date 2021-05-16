package net.minecraft.util.math.vector;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class Quaternion {
   public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float i;
   private float j;
   private float k;
   private float r;

   public Quaternion(float p_i48100_1_, float p_i48100_2_, float p_i48100_3_, float p_i48100_4_) {
      this.i = p_i48100_1_;
      this.j = p_i48100_2_;
      this.k = p_i48100_3_;
      this.r = p_i48100_4_;
   }

   public Quaternion(Vector3f p_i48101_1_, float p_i48101_2_, boolean p_i48101_3_) {
      if (p_i48101_3_) {
         p_i48101_2_ *= ((float)Math.PI / 180F);
      }

      float f = sin(p_i48101_2_ / 2.0F);
      this.i = p_i48101_1_.x() * f;
      this.j = p_i48101_1_.y() * f;
      this.k = p_i48101_1_.z() * f;
      this.r = cos(p_i48101_2_ / 2.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion(float p_i48102_1_, float p_i48102_2_, float p_i48102_3_, boolean p_i48102_4_) {
      if (p_i48102_4_) {
         p_i48102_1_ *= ((float)Math.PI / 180F);
         p_i48102_2_ *= ((float)Math.PI / 180F);
         p_i48102_3_ *= ((float)Math.PI / 180F);
      }

      float f = sin(0.5F * p_i48102_1_);
      float f1 = cos(0.5F * p_i48102_1_);
      float f2 = sin(0.5F * p_i48102_2_);
      float f3 = cos(0.5F * p_i48102_2_);
      float f4 = sin(0.5F * p_i48102_3_);
      float f5 = cos(0.5F * p_i48102_3_);
      this.i = f * f3 * f5 + f1 * f2 * f4;
      this.j = f1 * f2 * f5 - f * f3 * f4;
      this.k = f * f2 * f5 + f1 * f3 * f4;
      this.r = f1 * f3 * f5 - f * f2 * f4;
   }

   public Quaternion(Quaternion p_i48103_1_) {
      this.i = p_i48103_1_.i;
      this.j = p_i48103_1_.j;
      this.k = p_i48103_1_.k;
      this.r = p_i48103_1_.r;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Quaternion quaternion = (Quaternion)p_equals_1_;
         if (Float.compare(quaternion.i, this.i) != 0) {
            return false;
         } else if (Float.compare(quaternion.j, this.j) != 0) {
            return false;
         } else if (Float.compare(quaternion.k, this.k) != 0) {
            return false;
         } else {
            return Float.compare(quaternion.r, this.r) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.i);
      i = 31 * i + Float.floatToIntBits(this.j);
      i = 31 * i + Float.floatToIntBits(this.k);
      return 31 * i + Float.floatToIntBits(this.r);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Quaternion[").append(this.r()).append(" + ");
      stringbuilder.append(this.i()).append("i + ");
      stringbuilder.append(this.j()).append("j + ");
      stringbuilder.append(this.k()).append("k]");
      return stringbuilder.toString();
   }

   public float i() {
      return this.i;
   }

   public float j() {
      return this.j;
   }

   public float k() {
      return this.k;
   }

   public float r() {
      return this.r;
   }

   public void mul(Quaternion p_195890_1_) {
      float f = this.i();
      float f1 = this.j();
      float f2 = this.k();
      float f3 = this.r();
      float f4 = p_195890_1_.i();
      float f5 = p_195890_1_.j();
      float f6 = p_195890_1_.k();
      float f7 = p_195890_1_.r();
      this.i = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
      this.j = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
      this.k = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
      this.r = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
   }

   @OnlyIn(Dist.CLIENT)
   public void mul(float p_227065_1_) {
      this.i *= p_227065_1_;
      this.j *= p_227065_1_;
      this.k *= p_227065_1_;
      this.r *= p_227065_1_;
   }

   public void conj() {
      this.i = -this.i;
      this.j = -this.j;
      this.k = -this.k;
   }

   @OnlyIn(Dist.CLIENT)
   public void set(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
      this.i = p_227066_1_;
      this.j = p_227066_2_;
      this.k = p_227066_3_;
      this.r = p_227066_4_;
   }

   private static float cos(float p_214904_0_) {
      return (float)Math.cos((double)p_214904_0_);
   }

   private static float sin(float p_214903_0_) {
      return (float)Math.sin((double)p_214903_0_);
   }

   @OnlyIn(Dist.CLIENT)
   public void normalize() {
      float f = this.i() * this.i() + this.j() * this.j() + this.k() * this.k() + this.r() * this.r();
      if (f > 1.0E-6F) {
         float f1 = MathHelper.fastInvSqrt(f);
         this.i *= f1;
         this.j *= f1;
         this.k *= f1;
         this.r *= f1;
      } else {
         this.i = 0.0F;
         this.j = 0.0F;
         this.k = 0.0F;
         this.r = 0.0F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion copy() {
      return new Quaternion(this);
   }
}
