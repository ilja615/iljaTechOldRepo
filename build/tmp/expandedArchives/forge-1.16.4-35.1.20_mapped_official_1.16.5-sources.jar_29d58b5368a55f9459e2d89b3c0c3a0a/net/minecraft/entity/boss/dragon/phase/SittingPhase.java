package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.DamageSource;

public abstract class SittingPhase extends Phase {
   public SittingPhase(EnderDragonEntity p_i46794_1_) {
      super(p_i46794_1_);
   }

   public boolean isSitting() {
      return true;
   }

   public float onHurt(DamageSource p_221113_1_, float p_221113_2_) {
      if (p_221113_1_.getDirectEntity() instanceof AbstractArrowEntity) {
         p_221113_1_.getDirectEntity().setSecondsOnFire(1);
         return 0.0F;
      } else {
         return super.onHurt(p_221113_1_, p_221113_2_);
      }
   }
}
