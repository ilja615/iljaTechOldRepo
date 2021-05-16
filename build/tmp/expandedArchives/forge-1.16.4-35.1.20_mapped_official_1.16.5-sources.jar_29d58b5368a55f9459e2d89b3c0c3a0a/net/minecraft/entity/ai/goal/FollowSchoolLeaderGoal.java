package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;

public class FollowSchoolLeaderGoal extends Goal {
   private final AbstractGroupFishEntity mob;
   private int timeToRecalcPath;
   private int nextStartTick;

   public FollowSchoolLeaderGoal(AbstractGroupFishEntity p_i49857_1_) {
      this.mob = p_i49857_1_;
      this.nextStartTick = this.nextStartTick(p_i49857_1_);
   }

   protected int nextStartTick(AbstractGroupFishEntity p_212825_1_) {
      return 200 + p_212825_1_.getRandom().nextInt(200) % 20;
   }

   public boolean canUse() {
      if (this.mob.hasFollowers()) {
         return false;
      } else if (this.mob.isFollower()) {
         return true;
      } else if (this.nextStartTick > 0) {
         --this.nextStartTick;
         return false;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         Predicate<AbstractGroupFishEntity> predicate = (p_212824_0_) -> {
            return p_212824_0_.canBeFollowed() || !p_212824_0_.isFollower();
         };
         List<AbstractGroupFishEntity> list = this.mob.level.getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), predicate);
         AbstractGroupFishEntity abstractgroupfishentity = list.stream().filter(AbstractGroupFishEntity::canBeFollowed).findAny().orElse(this.mob);
         abstractgroupfishentity.addFollowers(list.stream().filter((p_212823_0_) -> {
            return !p_212823_0_.isFollower();
         }));
         return this.mob.isFollower();
      }
   }

   public boolean canContinueToUse() {
      return this.mob.isFollower() && this.mob.inRangeOfLeader();
   }

   public void start() {
      this.timeToRecalcPath = 0;
   }

   public void stop() {
      this.mob.stopFollowing();
   }

   public void tick() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         this.mob.pathToLeader();
      }
   }
}
