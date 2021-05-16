package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class WoodButtonBlock extends AbstractButtonBlock {
   public WoodButtonBlock(AbstractBlock.Properties p_i48291_1_) {
      super(true, p_i48291_1_);
   }

   protected SoundEvent getSound(boolean p_196369_1_) {
      return p_196369_1_ ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
   }
}
