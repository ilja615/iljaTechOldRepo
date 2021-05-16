package com.mojang.blaze3d.vertex;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MatrixApplyingVertexBuilder extends DefaultColorVertexBuilder {
   private final IVertexBuilder delegate;
   private final Matrix4f cameraInversePose;
   private final Matrix3f normalInversePose;
   private float x;
   private float y;
   private float z;
   private int overlayU;
   private int overlayV;
   private int lightCoords;
   private float nx;
   private float ny;
   private float nz;

   public MatrixApplyingVertexBuilder(IVertexBuilder p_i241245_1_, Matrix4f p_i241245_2_, Matrix3f p_i241245_3_) {
      this.delegate = p_i241245_1_;
      this.cameraInversePose = p_i241245_2_.copy();
      this.cameraInversePose.invert();
      this.normalInversePose = p_i241245_3_.copy();
      this.normalInversePose.invert();
      this.resetState();
   }

   private void resetState() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
      this.overlayU = 0;
      this.overlayV = 10;
      this.lightCoords = 15728880;
      this.nx = 0.0F;
      this.ny = 1.0F;
      this.nz = 0.0F;
   }

   public void endVertex() {
      Vector3f vector3f = new Vector3f(this.nx, this.ny, this.nz);
      vector3f.transform(this.normalInversePose);
      Direction direction = Direction.getNearest(vector3f.x(), vector3f.y(), vector3f.z());
      Vector4f vector4f = new Vector4f(this.x, this.y, this.z, 1.0F);
      vector4f.transform(this.cameraInversePose);
      vector4f.transform(Vector3f.YP.rotationDegrees(180.0F));
      vector4f.transform(Vector3f.XP.rotationDegrees(-90.0F));
      vector4f.transform(direction.getRotation());
      float f = -vector4f.x();
      float f1 = -vector4f.y();
      this.delegate.vertex((double)this.x, (double)this.y, (double)this.z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(f, f1).overlayCoords(this.overlayU, this.overlayV).uv2(this.lightCoords).normal(this.nx, this.ny, this.nz).endVertex();
      this.resetState();
   }

   public IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_) {
      this.x = (float)p_225582_1_;
      this.y = (float)p_225582_3_;
      this.z = (float)p_225582_5_;
      return this;
   }

   public IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_) {
      return this;
   }

   public IVertexBuilder uv(float p_225583_1_, float p_225583_2_) {
      return this;
   }

   public IVertexBuilder overlayCoords(int p_225585_1_, int p_225585_2_) {
      this.overlayU = p_225585_1_;
      this.overlayV = p_225585_2_;
      return this;
   }

   public IVertexBuilder uv2(int p_225587_1_, int p_225587_2_) {
      this.lightCoords = p_225587_1_ | p_225587_2_ << 16;
      return this;
   }

   public IVertexBuilder normal(float p_225584_1_, float p_225584_2_, float p_225584_3_) {
      this.nx = p_225584_1_;
      this.ny = p_225584_2_;
      this.nz = p_225584_3_;
      return this;
   }
}
