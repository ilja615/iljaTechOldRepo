package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;

public class AbsorptionEffect extends Effect {
   protected AbsorptionEffect(EffectType p_i50395_1_, int p_i50395_2_) {
      super(p_i50395_1_, p_i50395_2_);
   }

   public void removeAttributeModifiers(LivingEntity p_111187_1_, AttributeModifierManager p_111187_2_, int p_111187_3_) {
      p_111187_1_.setAbsorptionAmount(p_111187_1_.getAbsorptionAmount() - (float)(4 * (p_111187_3_ + 1)));
      super.removeAttributeModifiers(p_111187_1_, p_111187_2_, p_111187_3_);
   }

   public void addAttributeModifiers(LivingEntity p_111185_1_, AttributeModifierManager p_111185_2_, int p_111185_3_) {
      p_111185_1_.setAbsorptionAmount(p_111185_1_.getAbsorptionAmount() + (float)(4 * (p_111185_3_ + 1)));
      super.addAttributeModifiers(p_111185_1_, p_111185_2_, p_111185_3_);
   }
}
