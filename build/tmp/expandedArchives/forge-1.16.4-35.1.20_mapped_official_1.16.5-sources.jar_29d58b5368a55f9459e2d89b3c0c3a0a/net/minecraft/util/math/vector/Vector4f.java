package net.minecraft.util.math.vector;

import net.minecraft.util.math.MathHelper;

public class Vector4f {
   private float x;
   private float y;
   private float z;
   private float w;

   public Vector4f() {
   }

   public Vector4f(float p_i48096_1_, float p_i48096_2_, float p_i48096_3_, float p_i48096_4_) {
      this.x = p_i48096_1_;
      this.y = p_i48096_2_;
      this.z = p_i48096_3_;
      this.w = p_i48096_4_;
   }

   public Vector4f(Vector3f p_i226061_1_) {
      this(p_i226061_1_.x(), p_i226061_1_.y(), p_i226061_1_.z(), 1.0F);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector4f vector4f = (Vector4f)p_equals_1_;
         if (Float.compare(vector4f.x, this.x) != 0) {
            return false;
         } else if (Float.compare(vector4f.y, this.y) != 0) {
            return false;
         } else if (Float.compare(vector4f.z, this.z) != 0) {
            return false;
         } else {
            return Float.compare(vector4f.w, this.w) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      i = 31 * i + Float.floatToIntBits(this.z);
      return 31 * i + Float.floatToIntBits(this.w);
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float z() {
      return this.z;
   }

   public float w() {
      return this.w;
   }

   public void mul(Vector3f p_195909_1_) {
      this.x *= p_195909_1_.x();
      this.y *= p_195909_1_.y();
      this.z *= p_195909_1_.z();
   }

   public void set(float p_195911_1_, float p_195911_2_, float p_195911_3_, float p_195911_4_) {
      this.x = p_195911_1_;
      this.y = p_195911_2_;
      this.z = p_195911_3_;
      this.w = p_195911_4_;
   }

   public float dot(Vector4f p_229373_1_) {
      return this.x * p_229373_1_.x + this.y * p_229373_1_.y + this.z * p_229373_1_.z + this.w * p_229373_1_.w;
   }

   public boolean normalize() {
      float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
      if ((double)f < 1.0E-5D) {
         return false;
      } else {
         float f1 = MathHelper.fastInvSqrt(f);
         this.x *= f1;
         this.y *= f1;
         this.z *= f1;
         this.w *= f1;
         return true;
      }
   }

   public void transform(Matrix4f p_229372_1_) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      float f3 = this.w;
      this.x = p_229372_1_.m00 * f + p_229372_1_.m01 * f1 + p_229372_1_.m02 * f2 + p_229372_1_.m03 * f3;
      this.y = p_229372_1_.m10 * f + p_229372_1_.m11 * f1 + p_229372_1_.m12 * f2 + p_229372_1_.m13 * f3;
      this.z = p_229372_1_.m20 * f + p_229372_1_.m21 * f1 + p_229372_1_.m22 * f2 + p_229372_1_.m23 * f3;
      this.w = p_229372_1_.m30 * f + p_229372_1_.m31 * f1 + p_229372_1_.m32 * f2 + p_229372_1_.m33 * f3;
   }

   public void transform(Quaternion p_195912_1_) {
      Quaternion quaternion = new Quaternion(p_195912_1_);
      quaternion.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion quaternion1 = new Quaternion(p_195912_1_);
      quaternion1.conj();
      quaternion.mul(quaternion1);
      this.set(quaternion.i(), quaternion.j(), quaternion.k(), this.w());
   }

   public void perspectiveDivide() {
      this.x /= this.w;
      this.y /= this.w;
      this.z /= this.w;
      this.w = 1.0F;
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
   }

    // Forge start
    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
        this.w = values[3];
    }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(float z) { this.z = z; }
    public void setW(float z) { this.w = z; }
}
