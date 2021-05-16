package net.minecraft.client.audio;

import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianSound extends TickableSound {
   private final GuardianEntity guardian;

   public GuardianSound(GuardianEntity p_i46071_1_) {
      super(SoundEvents.GUARDIAN_ATTACK, SoundCategory.HOSTILE);
      this.guardian = p_i46071_1_;
      this.attenuation = ISound.AttenuationType.NONE;
      this.looping = true;
      this.delay = 0;
   }

   public boolean canPlaySound() {
      return !this.guardian.isSilent();
   }

   public void tick() {
      if (!this.guardian.removed && this.guardian.getTarget() == null) {
         this.x = (double)((float)this.guardian.getX());
         this.y = (double)((float)this.guardian.getY());
         this.z = (double)((float)this.guardian.getZ());
         float f = this.guardian.getAttackAnimationScale(0.0F);
         this.volume = 0.0F + 1.0F * f * f;
         this.pitch = 0.7F + 0.5F * f;
      } else {
         this.stop();
      }
   }
}
