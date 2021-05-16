package net.minecraft.entity.ai.goal;

import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtCustomerGoal extends LookAtGoal {
   private final AbstractVillagerEntity villager;

   public LookAtCustomerGoal(AbstractVillagerEntity p_i50326_1_) {
      super(p_i50326_1_, PlayerEntity.class, 8.0F);
      this.villager = p_i50326_1_;
   }

   public boolean canUse() {
      if (this.villager.isTrading()) {
         this.lookAt = this.villager.getTradingPlayer();
         return true;
      } else {
         return false;
      }
   }
}
