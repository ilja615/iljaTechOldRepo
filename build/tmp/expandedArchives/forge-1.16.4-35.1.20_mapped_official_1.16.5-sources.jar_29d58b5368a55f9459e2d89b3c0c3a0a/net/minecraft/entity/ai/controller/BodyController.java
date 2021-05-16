package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyController {
   private final MobEntity mob;
   private int headStableTime;
   private float lastStableYHeadRot;

   public BodyController(MobEntity p_i50334_1_) {
      this.mob = p_i50334_1_;
   }

   public void clientTick() {
      if (this.isMoving()) {
         this.mob.yBodyRot = this.mob.yRot;
         this.rotateHeadIfNecessary();
         this.lastStableYHeadRot = this.mob.yHeadRot;
         this.headStableTime = 0;
      } else {
         if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
               this.headStableTime = 0;
               this.lastStableYHeadRot = this.mob.yHeadRot;
               this.rotateBodyIfNecessary();
            } else {
               ++this.headStableTime;
               if (this.headStableTime > 10) {
                  this.rotateHeadTowardsFront();
               }
            }
         }

      }
   }

   private void rotateBodyIfNecessary() {
      this.mob.yBodyRot = MathHelper.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, (float)this.mob.getMaxHeadYRot());
   }

   private void rotateHeadIfNecessary() {
      this.mob.yHeadRot = MathHelper.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
   }

   private void rotateHeadTowardsFront() {
      int i = this.headStableTime - 10;
      float f = MathHelper.clamp((float)i / 10.0F, 0.0F, 1.0F);
      float f1 = (float)this.mob.getMaxHeadYRot() * (1.0F - f);
      this.mob.yBodyRot = MathHelper.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, f1);
   }

   private boolean notCarryingMobPassengers() {
      return this.mob.getPassengers().isEmpty() || !(this.mob.getPassengers().get(0) instanceof MobEntity);
   }

   private boolean isMoving() {
      double d0 = this.mob.getX() - this.mob.xo;
      double d1 = this.mob.getZ() - this.mob.zo;
      return d0 * d0 + d1 * d1 > (double)2.5000003E-7F;
   }
}
