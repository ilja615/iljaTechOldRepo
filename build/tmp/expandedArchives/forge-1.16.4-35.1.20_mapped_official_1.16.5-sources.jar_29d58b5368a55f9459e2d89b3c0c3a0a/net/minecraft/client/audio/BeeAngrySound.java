package net.minecraft.client.audio;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeAngrySound extends BeeSound {
   public BeeAngrySound(BeeEntity p_i226058_1_) {
      super(p_i226058_1_, SoundEvents.BEE_LOOP_AGGRESSIVE, SoundCategory.NEUTRAL);
      this.delay = 0;
   }

   protected TickableSound getAlternativeSoundInstance() {
      return new BeeFlightSound(this.bee);
   }

   protected boolean shouldSwitchSounds() {
      return !this.bee.isAngry();
   }
}
