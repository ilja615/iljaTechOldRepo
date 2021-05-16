package net.minecraft.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BeeSound extends TickableSound {
   protected final BeeEntity bee;
   private boolean hasSwitched;

   public BeeSound(BeeEntity p_i226060_1_, SoundEvent p_i226060_2_, SoundCategory p_i226060_3_) {
      super(p_i226060_2_, p_i226060_3_);
      this.bee = p_i226060_1_;
      this.x = (double)((float)p_i226060_1_.getX());
      this.y = (double)((float)p_i226060_1_.getY());
      this.z = (double)((float)p_i226060_1_.getZ());
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
   }

   public void tick() {
      boolean flag = this.shouldSwitchSounds();
      if (flag && !this.isStopped()) {
         Minecraft.getInstance().getSoundManager().queueTickingSound(this.getAlternativeSoundInstance());
         this.hasSwitched = true;
      }

      if (!this.bee.removed && !this.hasSwitched) {
         this.x = (double)((float)this.bee.getX());
         this.y = (double)((float)this.bee.getY());
         this.z = (double)((float)this.bee.getZ());
         float f = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(this.bee.getDeltaMovement()));
         if ((double)f >= 0.01D) {
            this.pitch = MathHelper.lerp(MathHelper.clamp(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
            this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 1.2F);
         } else {
            this.pitch = 0.0F;
            this.volume = 0.0F;
         }

      } else {
         this.stop();
      }
   }

   private float getMinPitch() {
      return this.bee.isBaby() ? 1.1F : 0.7F;
   }

   private float getMaxPitch() {
      return this.bee.isBaby() ? 1.5F : 1.1F;
   }

   public boolean canStartSilent() {
      return true;
   }

   public boolean canPlaySound() {
      return !this.bee.isSilent();
   }

   protected abstract TickableSound getAlternativeSoundInstance();

   protected abstract boolean shouldSwitchSounds();
}
