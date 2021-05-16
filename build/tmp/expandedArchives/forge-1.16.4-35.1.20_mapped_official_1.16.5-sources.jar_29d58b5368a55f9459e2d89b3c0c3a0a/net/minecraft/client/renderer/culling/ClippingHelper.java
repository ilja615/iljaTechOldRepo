package net.minecraft.client.renderer.culling;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClippingHelper {
   private final Vector4f[] frustumData = new Vector4f[6];
   private double camX;
   private double camY;
   private double camZ;

   public ClippingHelper(Matrix4f p_i226026_1_, Matrix4f p_i226026_2_) {
      this.calculateFrustum(p_i226026_1_, p_i226026_2_);
   }

   public void prepare(double p_228952_1_, double p_228952_3_, double p_228952_5_) {
      this.camX = p_228952_1_;
      this.camY = p_228952_3_;
      this.camZ = p_228952_5_;
   }

   private void calculateFrustum(Matrix4f p_228956_1_, Matrix4f p_228956_2_) {
      Matrix4f matrix4f = p_228956_2_.copy();
      matrix4f.multiply(p_228956_1_);
      matrix4f.transpose();
      this.getPlane(matrix4f, -1, 0, 0, 0);
      this.getPlane(matrix4f, 1, 0, 0, 1);
      this.getPlane(matrix4f, 0, -1, 0, 2);
      this.getPlane(matrix4f, 0, 1, 0, 3);
      this.getPlane(matrix4f, 0, 0, -1, 4);
      this.getPlane(matrix4f, 0, 0, 1, 5);
   }

   private void getPlane(Matrix4f p_228955_1_, int p_228955_2_, int p_228955_3_, int p_228955_4_, int p_228955_5_) {
      Vector4f vector4f = new Vector4f((float)p_228955_2_, (float)p_228955_3_, (float)p_228955_4_, 1.0F);
      vector4f.transform(p_228955_1_);
      vector4f.normalize();
      this.frustumData[p_228955_5_] = vector4f;
   }

   public boolean isVisible(AxisAlignedBB p_228957_1_) {
      return this.cubeInFrustum(p_228957_1_.minX, p_228957_1_.minY, p_228957_1_.minZ, p_228957_1_.maxX, p_228957_1_.maxY, p_228957_1_.maxZ);
   }

   private boolean cubeInFrustum(double p_228953_1_, double p_228953_3_, double p_228953_5_, double p_228953_7_, double p_228953_9_, double p_228953_11_) {
      float f = (float)(p_228953_1_ - this.camX);
      float f1 = (float)(p_228953_3_ - this.camY);
      float f2 = (float)(p_228953_5_ - this.camZ);
      float f3 = (float)(p_228953_7_ - this.camX);
      float f4 = (float)(p_228953_9_ - this.camY);
      float f5 = (float)(p_228953_11_ - this.camZ);
      return this.cubeInFrustum(f, f1, f2, f3, f4, f5);
   }

   private boolean cubeInFrustum(float p_228954_1_, float p_228954_2_, float p_228954_3_, float p_228954_4_, float p_228954_5_, float p_228954_6_) {
      for(int i = 0; i < 6; ++i) {
         Vector4f vector4f = this.frustumData[i];
         if (!(vector4f.dot(new Vector4f(p_228954_1_, p_228954_2_, p_228954_3_, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(p_228954_4_, p_228954_2_, p_228954_3_, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(p_228954_1_, p_228954_5_, p_228954_3_, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(p_228954_4_, p_228954_5_, p_228954_3_, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(p_228954_1_, p_228954_2_, p_228954_6_, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(p_228954_4_, p_228954_2_, p_228954_6_, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(p_228954_1_, p_228954_5_, p_228954_6_, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(p_228954_4_, p_228954_5_, p_228954_6_, 1.0F)) > 0.0F)) {
            return false;
         }
      }

      return true;
   }
}
