package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.MobEntity;
import net.minecraft.tags.FluidTags;

public class SwimGoal extends Goal {
   private final MobEntity mob;

   public SwimGoal(MobEntity p_i1624_1_) {
      this.mob = p_i1624_1_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP));
      p_i1624_1_.getNavigation().setCanFloat(true);
   }

   public boolean canUse() {
      return this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava();
   }

   public void tick() {
      if (this.mob.getRandom().nextFloat() < 0.8F) {
         this.mob.getJumpControl().jump();
      }

   }
}
