package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class GoOutsideAfterRaidTask extends MoveToSkylightTask {
   public GoOutsideAfterRaidTask(float p_i50365_1_) {
      super(p_i50365_1_);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Raid raid = p_212832_1_.getRaidAt(p_212832_2_.blockPosition());
      return raid != null && raid.isVictory() && super.checkExtraStartConditions(p_212832_1_, p_212832_2_);
   }
}
