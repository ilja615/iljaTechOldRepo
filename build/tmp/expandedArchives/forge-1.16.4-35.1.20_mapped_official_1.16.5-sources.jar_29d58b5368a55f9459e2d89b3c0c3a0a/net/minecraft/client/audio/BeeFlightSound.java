package net.minecraft.client.audio;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeFlightSound extends BeeSound {
   public BeeFlightSound(BeeEntity p_i226059_1_) {
      super(p_i226059_1_, SoundEvents.BEE_LOOP, SoundCategory.NEUTRAL);
   }

   protected TickableSound getAlternativeSoundInstance() {
      return new BeeAngrySound(this.bee);
   }

   protected boolean shouldSwitchSounds() {
      return this.bee.isAngry();
   }
}
