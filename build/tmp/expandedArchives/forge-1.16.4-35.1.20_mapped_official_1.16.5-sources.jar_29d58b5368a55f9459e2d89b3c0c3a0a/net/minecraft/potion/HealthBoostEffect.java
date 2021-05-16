package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;

public class HealthBoostEffect extends Effect {
   public HealthBoostEffect(EffectType p_i50393_1_, int p_i50393_2_) {
      super(p_i50393_1_, p_i50393_2_);
   }

   public void removeAttributeModifiers(LivingEntity p_111187_1_, AttributeModifierManager p_111187_2_, int p_111187_3_) {
      super.removeAttributeModifiers(p_111187_1_, p_111187_2_, p_111187_3_);
      if (p_111187_1_.getHealth() > p_111187_1_.getMaxHealth()) {
         p_111187_1_.setHealth(p_111187_1_.getMaxHealth());
      }

   }
}
