package net.minecraft.entity.ai.attributes;

import net.minecraft.util.math.MathHelper;

public class RangedAttribute extends Attribute {
   private final double minValue;
   private final double maxValue;

   public RangedAttribute(String p_i231504_1_, double p_i231504_2_, double p_i231504_4_, double p_i231504_6_) {
      super(p_i231504_1_, p_i231504_2_);
      this.minValue = p_i231504_4_;
      this.maxValue = p_i231504_6_;
      if (p_i231504_4_ > p_i231504_6_) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (p_i231504_2_ < p_i231504_4_) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (p_i231504_2_ > p_i231504_6_) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public double sanitizeValue(double p_111109_1_) {
      return MathHelper.clamp(p_111109_1_, this.minValue, this.maxValue);
   }
}
