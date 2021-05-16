package net.minecraft.entity.ai.controller;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class LookController {
   protected final MobEntity mob;
   protected float yMaxRotSpeed;
   protected float xMaxRotAngle;
   protected boolean hasWanted;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;

   public LookController(MobEntity p_i1613_1_) {
      this.mob = p_i1613_1_;
   }

   public void setLookAt(Vector3d p_220674_1_) {
      this.setLookAt(p_220674_1_.x, p_220674_1_.y, p_220674_1_.z);
   }

   public void setLookAt(Entity p_75651_1_, float p_75651_2_, float p_75651_3_) {
      this.setLookAt(p_75651_1_.getX(), getWantedY(p_75651_1_), p_75651_1_.getZ(), p_75651_2_, p_75651_3_);
   }

   public void setLookAt(double p_220679_1_, double p_220679_3_, double p_220679_5_) {
      this.setLookAt(p_220679_1_, p_220679_3_, p_220679_5_, (float)this.mob.getHeadRotSpeed(), (float)this.mob.getMaxHeadXRot());
   }

   public void setLookAt(double p_75650_1_, double p_75650_3_, double p_75650_5_, float p_75650_7_, float p_75650_8_) {
      this.wantedX = p_75650_1_;
      this.wantedY = p_75650_3_;
      this.wantedZ = p_75650_5_;
      this.yMaxRotSpeed = p_75650_7_;
      this.xMaxRotAngle = p_75650_8_;
      this.hasWanted = true;
   }

   public void tick() {
      if (this.resetXRotOnTick()) {
         this.mob.xRot = 0.0F;
      }

      if (this.hasWanted) {
         this.hasWanted = false;
         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.getYRotD(), this.yMaxRotSpeed);
         this.mob.xRot = this.rotateTowards(this.mob.xRot, this.getXRotD(), this.xMaxRotAngle);
      } else {
         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, 10.0F);
      }

      if (!this.mob.getNavigation().isDone()) {
         this.mob.yHeadRot = MathHelper.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
      }

   }

   protected boolean resetXRotOnTick() {
      return true;
   }

   public boolean isHasWanted() {
      return this.hasWanted;
   }

   public double getWantedX() {
      return this.wantedX;
   }

   public double getWantedY() {
      return this.wantedY;
   }

   public double getWantedZ() {
      return this.wantedZ;
   }

   protected float getXRotD() {
      double d0 = this.wantedX - this.mob.getX();
      double d1 = this.wantedY - this.mob.getEyeY();
      double d2 = this.wantedZ - this.mob.getZ();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      return (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
   }

   protected float getYRotD() {
      double d0 = this.wantedX - this.mob.getX();
      double d1 = this.wantedZ - this.mob.getZ();
      return (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
   }

   protected float rotateTowards(float p_220675_1_, float p_220675_2_, float p_220675_3_) {
      float f = MathHelper.degreesDifference(p_220675_1_, p_220675_2_);
      float f1 = MathHelper.clamp(f, -p_220675_3_, p_220675_3_);
      return p_220675_1_ + f1;
   }

   private static double getWantedY(Entity p_220676_0_) {
      return p_220676_0_ instanceof LivingEntity ? p_220676_0_.getEyeY() : (p_220676_0_.getBoundingBox().minY + p_220676_0_.getBoundingBox().maxY) / 2.0D;
   }
}
