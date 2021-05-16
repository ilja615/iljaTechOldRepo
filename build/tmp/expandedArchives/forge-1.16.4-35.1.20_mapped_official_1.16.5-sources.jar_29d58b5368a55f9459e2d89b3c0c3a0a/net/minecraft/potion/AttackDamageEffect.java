package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class AttackDamageEffect extends Effect {
   protected final double multiplier;

   protected AttackDamageEffect(EffectType p_i50394_1_, int p_i50394_2_, double p_i50394_3_) {
      super(p_i50394_1_, p_i50394_2_);
      this.multiplier = p_i50394_3_;
   }

   public double getAttributeModifierValue(int p_111183_1_, AttributeModifier p_111183_2_) {
      return this.multiplier * (double)(p_111183_1_ + 1);
   }
}
