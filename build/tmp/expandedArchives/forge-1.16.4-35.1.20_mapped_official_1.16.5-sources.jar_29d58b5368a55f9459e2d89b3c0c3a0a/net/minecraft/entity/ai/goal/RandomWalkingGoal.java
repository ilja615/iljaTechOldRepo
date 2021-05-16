package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.vector.Vector3d;

public class RandomWalkingGoal extends Goal {
   protected final CreatureEntity mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected final double speedModifier;
   protected int interval;
   protected boolean forceTrigger;
   private boolean checkNoActionTime;

   public RandomWalkingGoal(CreatureEntity p_i1648_1_, double p_i1648_2_) {
      this(p_i1648_1_, p_i1648_2_, 120);
   }

   public RandomWalkingGoal(CreatureEntity p_i45887_1_, double p_i45887_2_, int p_i45887_4_) {
      this(p_i45887_1_, p_i45887_2_, p_i45887_4_, true);
   }

   public RandomWalkingGoal(CreatureEntity p_i231550_1_, double p_i231550_2_, int p_i231550_4_, boolean p_i231550_5_) {
      this.mob = p_i231550_1_;
      this.speedModifier = p_i231550_2_;
      this.interval = p_i231550_4_;
      this.checkNoActionTime = p_i231550_5_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isVehicle()) {
         return false;
      } else {
         if (!this.forceTrigger) {
            if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
               return false;
            }

            if (this.mob.getRandom().nextInt(this.interval) != 0) {
               return false;
            }
         }

         Vector3d vector3d = this.getPosition();
         if (vector3d == null) {
            return false;
         } else {
            this.wantedX = vector3d.x;
            this.wantedY = vector3d.y;
            this.wantedZ = vector3d.z;
            this.forceTrigger = false;
            return true;
         }
      }
   }

   @Nullable
   protected Vector3d getPosition() {
      return RandomPositionGenerator.getPos(this.mob, 10, 7);
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }

   public void stop() {
      this.mob.getNavigation().stop();
      super.stop();
   }

   public void trigger() {
      this.forceTrigger = true;
   }

   public void setInterval(int p_179479_1_) {
      this.interval = p_179479_1_;
   }
}
