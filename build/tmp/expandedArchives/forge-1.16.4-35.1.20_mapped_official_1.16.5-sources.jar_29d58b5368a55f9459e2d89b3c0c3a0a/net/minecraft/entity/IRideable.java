package net.minecraft.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public interface IRideable {
   boolean boost();

   void travelWithInput(Vector3d p_230267_1_);

   float getSteeringSpeed();

   default boolean travel(MobEntity p_233622_1_, BoostHelper p_233622_2_, Vector3d p_233622_3_) {
      if (!p_233622_1_.isAlive()) {
         return false;
      } else {
         Entity entity = p_233622_1_.getPassengers().isEmpty() ? null : p_233622_1_.getPassengers().get(0);
         if (p_233622_1_.isVehicle() && p_233622_1_.canBeControlledByRider() && entity instanceof PlayerEntity) {
            p_233622_1_.yRot = entity.yRot;
            p_233622_1_.yRotO = p_233622_1_.yRot;
            p_233622_1_.xRot = entity.xRot * 0.5F;
            p_233622_1_.setRot(p_233622_1_.yRot, p_233622_1_.xRot);
            p_233622_1_.yBodyRot = p_233622_1_.yRot;
            p_233622_1_.yHeadRot = p_233622_1_.yRot;
            p_233622_1_.maxUpStep = 1.0F;
            p_233622_1_.flyingSpeed = p_233622_1_.getSpeed() * 0.1F;
            if (p_233622_2_.boosting && p_233622_2_.boostTime++ > p_233622_2_.boostTimeTotal) {
               p_233622_2_.boosting = false;
            }

            if (p_233622_1_.isControlledByLocalInstance()) {
               float f = this.getSteeringSpeed();
               if (p_233622_2_.boosting) {
                  f += f * 1.15F * MathHelper.sin((float)p_233622_2_.boostTime / (float)p_233622_2_.boostTimeTotal * (float)Math.PI);
               }

               p_233622_1_.setSpeed(f);
               this.travelWithInput(new Vector3d(0.0D, 0.0D, 1.0D));
               p_233622_1_.lerpSteps = 0;
            } else {
               p_233622_1_.calculateEntityAnimation(p_233622_1_, false);
               p_233622_1_.setDeltaMovement(Vector3d.ZERO);
            }

            return true;
         } else {
            p_233622_1_.maxUpStep = 0.5F;
            p_233622_1_.flyingSpeed = 0.02F;
            this.travelWithInput(p_233622_3_);
            return false;
         }
      }
   }
}
