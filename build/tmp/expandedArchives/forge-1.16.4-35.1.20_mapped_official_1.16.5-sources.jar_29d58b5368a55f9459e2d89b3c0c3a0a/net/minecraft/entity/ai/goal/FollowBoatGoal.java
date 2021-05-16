package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FollowBoatGoal extends Goal {
   private int timeToRecalcPath;
   private final CreatureEntity mob;
   private PlayerEntity following;
   private BoatGoals currentGoal;

   public FollowBoatGoal(CreatureEntity p_i48939_1_) {
      this.mob = p_i48939_1_;
   }

   public boolean canUse() {
      List<BoatEntity> list = this.mob.level.getEntitiesOfClass(BoatEntity.class, this.mob.getBoundingBox().inflate(5.0D));
      boolean flag = false;

      for(BoatEntity boatentity : list) {
         Entity entity = boatentity.getControllingPassenger();
         if (entity instanceof PlayerEntity && (MathHelper.abs(((PlayerEntity)entity).xxa) > 0.0F || MathHelper.abs(((PlayerEntity)entity).zza) > 0.0F)) {
            flag = true;
            break;
         }
      }

      return this.following != null && (MathHelper.abs(this.following.xxa) > 0.0F || MathHelper.abs(this.following.zza) > 0.0F) || flag;
   }

   public boolean isInterruptable() {
      return true;
   }

   public boolean canContinueToUse() {
      return this.following != null && this.following.isPassenger() && (MathHelper.abs(this.following.xxa) > 0.0F || MathHelper.abs(this.following.zza) > 0.0F);
   }

   public void start() {
      for(BoatEntity boatentity : this.mob.level.getEntitiesOfClass(BoatEntity.class, this.mob.getBoundingBox().inflate(5.0D))) {
         if (boatentity.getControllingPassenger() != null && boatentity.getControllingPassenger() instanceof PlayerEntity) {
            this.following = (PlayerEntity)boatentity.getControllingPassenger();
            break;
         }
      }

      this.timeToRecalcPath = 0;
      this.currentGoal = BoatGoals.GO_TO_BOAT;
   }

   public void stop() {
      this.following = null;
   }

   public void tick() {
      boolean flag = MathHelper.abs(this.following.xxa) > 0.0F || MathHelper.abs(this.following.zza) > 0.0F;
      float f = this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION ? (flag ? 0.01F : 0.0F) : 0.015F;
      this.mob.moveRelative(f, new Vector3d((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         if (this.currentGoal == BoatGoals.GO_TO_BOAT) {
            BlockPos blockpos = this.following.blockPosition().relative(this.following.getDirection().getOpposite());
            blockpos = blockpos.offset(0, -1, 0);
            this.mob.getNavigation().moveTo((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
            if (this.mob.distanceTo(this.following) < 4.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if (this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction direction = this.following.getMotionDirection();
            BlockPos blockpos1 = this.following.blockPosition().relative(direction, 10);
            this.mob.getNavigation().moveTo((double)blockpos1.getX(), (double)(blockpos1.getY() - 1), (double)blockpos1.getZ(), 1.0D);
            if (this.mob.distanceTo(this.following) > 12.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}
