package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;

public class FollowMobGoal extends Goal {
   private final MobEntity mob;
   private final Predicate<MobEntity> followPredicate;
   private MobEntity followingMob;
   private final double speedModifier;
   private final PathNavigator navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private float oldWaterCost;
   private final float areaSize;

   public FollowMobGoal(MobEntity p_i47417_1_, double p_i47417_2_, float p_i47417_4_, float p_i47417_5_) {
      this.mob = p_i47417_1_;
      this.followPredicate = (p_210291_1_) -> {
         return p_210291_1_ != null && p_i47417_1_.getClass() != p_210291_1_.getClass();
      };
      this.speedModifier = p_i47417_2_;
      this.navigation = p_i47417_1_.getNavigation();
      this.stopDistance = p_i47417_4_;
      this.areaSize = p_i47417_5_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(p_i47417_1_.getNavigation() instanceof GroundPathNavigator) && !(p_i47417_1_.getNavigation() instanceof FlyingPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
      }
   }

   public boolean canUse() {
      List<MobEntity> list = this.mob.level.getEntitiesOfClass(MobEntity.class, this.mob.getBoundingBox().inflate((double)this.areaSize), this.followPredicate);
      if (!list.isEmpty()) {
         for(MobEntity mobentity : list) {
            if (!mobentity.isInvisible()) {
               this.followingMob = mobentity;
               return true;
            }
         }
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > (double)(this.stopDistance * this.stopDistance);
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.mob.getPathfindingMalus(PathNodeType.WATER);
      this.mob.setPathfindingMalus(PathNodeType.WATER, 0.0F);
   }

   public void stop() {
      this.followingMob = null;
      this.navigation.stop();
      this.mob.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
   }

   public void tick() {
      if (this.followingMob != null && !this.mob.isLeashed()) {
         this.mob.getLookControl().setLookAt(this.followingMob, 10.0F, (float)this.mob.getMaxHeadXRot());
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            double d0 = this.mob.getX() - this.followingMob.getX();
            double d1 = this.mob.getY() - this.followingMob.getY();
            double d2 = this.mob.getZ() - this.followingMob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (!(d3 <= (double)(this.stopDistance * this.stopDistance))) {
               this.navigation.moveTo(this.followingMob, this.speedModifier);
            } else {
               this.navigation.stop();
               LookController lookcontroller = this.followingMob.getLookControl();
               if (d3 <= (double)this.stopDistance || lookcontroller.getWantedX() == this.mob.getX() && lookcontroller.getWantedY() == this.mob.getY() && lookcontroller.getWantedZ() == this.mob.getZ()) {
                  double d4 = this.followingMob.getX() - this.mob.getX();
                  double d5 = this.followingMob.getZ() - this.mob.getZ();
                  this.navigation.moveTo(this.mob.getX() - d4, this.mob.getY(), this.mob.getZ() - d5, this.speedModifier);
               }

            }
         }
      }
   }
}
