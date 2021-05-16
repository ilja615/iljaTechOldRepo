package net.minecraft.entity;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class CreatureEntity extends MobEntity {
   protected CreatureEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
      super(p_i48575_1_, p_i48575_2_);
   }

   public float getWalkTargetValue(BlockPos p_180484_1_) {
      return this.getWalkTargetValue(p_180484_1_, this.level);
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return 0.0F;
   }

   public boolean checkSpawnRules(IWorld p_213380_1_, SpawnReason p_213380_2_) {
      return this.getWalkTargetValue(this.blockPosition(), p_213380_1_) >= 0.0F;
   }

   public boolean isPathFinding() {
      return !this.getNavigation().isDone();
   }

   protected void tickLeash() {
      super.tickLeash();
      Entity entity = this.getLeashHolder();
      if (entity != null && entity.level == this.level) {
         this.restrictTo(entity.blockPosition(), 5);
         float f = this.distanceTo(entity);
         if (this instanceof TameableEntity && ((TameableEntity)this).isInSittingPose()) {
            if (f > 10.0F) {
               this.dropLeash(true, true);
            }

            return;
         }

         this.onLeashDistance(f);
         if (f > 10.0F) {
            this.dropLeash(true, true);
            this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
         } else if (f > 6.0F) {
            double d0 = (entity.getX() - this.getX()) / (double)f;
            double d1 = (entity.getY() - this.getY()) / (double)f;
            double d2 = (entity.getZ() - this.getZ()) / (double)f;
            this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
         } else {
            this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
            float f1 = 2.0F;
            Vector3d vector3d = (new Vector3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())).normalize().scale((double)Math.max(f - 2.0F, 0.0F));
            this.getNavigation().moveTo(this.getX() + vector3d.x, this.getY() + vector3d.y, this.getZ() + vector3d.z, this.followLeashSpeed());
         }
      }

   }

   protected double followLeashSpeed() {
      return 1.0D;
   }

   protected void onLeashDistance(float p_142017_1_) {
   }
}
