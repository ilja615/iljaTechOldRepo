package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TradeWithPlayerGoal extends Goal {
   private final AbstractVillagerEntity mob;

   public TradeWithPlayerGoal(AbstractVillagerEntity p_i50320_1_) {
      this.mob = p_i50320_1_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.mob.isAlive()) {
         return false;
      } else if (this.mob.isInWater()) {
         return false;
      } else if (!this.mob.isOnGround()) {
         return false;
      } else if (this.mob.hurtMarked) {
         return false;
      } else {
         PlayerEntity playerentity = this.mob.getTradingPlayer();
         if (playerentity == null) {
            return false;
         } else if (this.mob.distanceToSqr(playerentity) > 16.0D) {
            return false;
         } else {
            return playerentity.containerMenu != null;
         }
      }
   }

   public void start() {
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.mob.setTradingPlayer((PlayerEntity)null);
   }
}
