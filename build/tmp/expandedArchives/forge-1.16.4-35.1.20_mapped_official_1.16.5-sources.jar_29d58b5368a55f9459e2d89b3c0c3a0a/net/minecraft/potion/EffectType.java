package net.minecraft.potion;

import net.minecraft.util.text.TextFormatting;

public enum EffectType {
   BENEFICIAL(TextFormatting.BLUE),
   HARMFUL(TextFormatting.RED),
   NEUTRAL(TextFormatting.BLUE);

   private final TextFormatting tooltipFormatting;

   private EffectType(TextFormatting p_i50390_3_) {
      this.tooltipFormatting = p_i50390_3_;
   }

   public TextFormatting getTooltipFormatting() {
      return this.tooltipFormatting;
   }
}
