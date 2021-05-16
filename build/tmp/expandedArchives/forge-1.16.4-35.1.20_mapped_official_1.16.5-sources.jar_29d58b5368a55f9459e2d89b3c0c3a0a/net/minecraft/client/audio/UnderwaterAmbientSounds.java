package net.minecraft.client.audio;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterAmbientSounds {
   @OnlyIn(Dist.CLIENT)
   public static class SubSound extends TickableSound {
      private final ClientPlayerEntity player;

      protected SubSound(ClientPlayerEntity p_i48884_1_, SoundEvent p_i48884_2_) {
         super(p_i48884_2_, SoundCategory.AMBIENT);
         this.player = p_i48884_1_;
         this.looping = false;
         this.delay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.relative = true;
      }

      public void tick() {
         if (this.player.removed || !this.player.isUnderWater()) {
            this.stop();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UnderWaterSound extends TickableSound {
      private final ClientPlayerEntity player;
      private int fade;

      public UnderWaterSound(ClientPlayerEntity p_i48883_1_) {
         super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundCategory.AMBIENT);
         this.player = p_i48883_1_;
         this.looping = true;
         this.delay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.relative = true;
      }

      public void tick() {
         if (!this.player.removed && this.fade >= 0) {
            if (this.player.isUnderWater()) {
               ++this.fade;
            } else {
               this.fade -= 2;
            }

            this.fade = Math.min(this.fade, 40);
            this.volume = Math.max(0.0F, Math.min((float)this.fade / 40.0F, 1.0F));
         } else {
            this.stop();
         }
      }
   }
}
