package net.minecraft.client.audio;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterAmbientSoundHandler implements IAmbientSoundHandler {
   private final ClientPlayerEntity player;
   private final SoundHandler soundManager;
   private int tickDelay = 0;

   public UnderwaterAmbientSoundHandler(ClientPlayerEntity p_i48885_1_, SoundHandler p_i48885_2_) {
      this.player = p_i48885_1_;
      this.soundManager = p_i48885_2_;
   }

   public void tick() {
      --this.tickDelay;
      if (this.tickDelay <= 0 && this.player.isUnderWater()) {
         float f = this.player.level.random.nextFloat();
         if (f < 1.0E-4F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSounds.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
         } else if (f < 0.001F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSounds.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
         } else if (f < 0.01F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSounds.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
         }
      }

   }
}
