package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class FindHidingPlaceDuringRaidTask extends FindHidingPlaceTask {
   public FindHidingPlaceDuringRaidTask(int p_i50360_1_, float p_i50360_2_) {
      super(p_i50360_1_, p_i50360_2_, 1);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Raid raid = p_212832_1_.getRaidAt(p_212832_2_.blockPosition());
      return super.checkExtraStartConditions(p_212832_1_, p_212832_2_) && raid != null && raid.isActive() && !raid.isVictory() && !raid.isLoss();
   }
}
