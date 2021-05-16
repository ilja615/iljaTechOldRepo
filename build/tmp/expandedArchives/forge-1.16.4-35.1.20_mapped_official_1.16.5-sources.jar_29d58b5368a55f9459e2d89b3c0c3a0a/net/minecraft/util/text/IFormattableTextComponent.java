package net.minecraft.util.text;

import java.util.function.UnaryOperator;

public interface IFormattableTextComponent extends ITextComponent {
   IFormattableTextComponent setStyle(Style p_230530_1_);

   default IFormattableTextComponent append(String p_240702_1_) {
      return this.append(new StringTextComponent(p_240702_1_));
   }

   IFormattableTextComponent append(ITextComponent p_230529_1_);

   default IFormattableTextComponent withStyle(UnaryOperator<Style> p_240700_1_) {
      this.setStyle(p_240700_1_.apply(this.getStyle()));
      return this;
   }

   default IFormattableTextComponent withStyle(Style p_240703_1_) {
      this.setStyle(p_240703_1_.applyTo(this.getStyle()));
      return this;
   }

   default IFormattableTextComponent withStyle(TextFormatting... p_240701_1_) {
      this.setStyle(this.getStyle().applyFormats(p_240701_1_));
      return this;
   }

   default IFormattableTextComponent withStyle(TextFormatting p_240699_1_) {
      this.setStyle(this.getStyle().applyFormat(p_240699_1_));
      return this;
   }
}
