package net.minecraft.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityTickableSound extends TickableSound {
   private final Entity entity;

   public EntityTickableSound(SoundEvent p_i50898_1_, SoundCategory p_i50898_2_, Entity p_i50898_3_) {
      this(p_i50898_1_, p_i50898_2_, 1.0F, 1.0F, p_i50898_3_);
   }

   public EntityTickableSound(SoundEvent p_i50899_1_, SoundCategory p_i50899_2_, float p_i50899_3_, float p_i50899_4_, Entity p_i50899_5_) {
      super(p_i50899_1_, p_i50899_2_);
      this.volume = p_i50899_3_;
      this.pitch = p_i50899_4_;
      this.entity = p_i50899_5_;
      this.x = (double)((float)this.entity.getX());
      this.y = (double)((float)this.entity.getY());
      this.z = (double)((float)this.entity.getZ());
   }

   public boolean canPlaySound() {
      return !this.entity.isSilent();
   }

   public void tick() {
      if (this.entity.removed) {
         this.stop();
      } else {
         this.x = (double)((float)this.entity.getX());
         this.y = (double)((float)this.entity.getY());
         this.z = (double)((float)this.entity.getZ());
      }
   }
}
