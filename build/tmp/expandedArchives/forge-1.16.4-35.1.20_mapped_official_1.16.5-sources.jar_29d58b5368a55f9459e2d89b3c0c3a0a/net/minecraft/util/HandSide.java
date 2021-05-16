package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum HandSide {
   LEFT(new TranslationTextComponent("options.mainHand.left")),
   RIGHT(new TranslationTextComponent("options.mainHand.right"));

   private final ITextComponent name;

   private HandSide(ITextComponent p_i46806_3_) {
      this.name = p_i46806_3_;
   }

   @OnlyIn(Dist.CLIENT)
   public HandSide getOpposite() {
      return this == LEFT ? RIGHT : LEFT;
   }

   public String toString() {
      return this.name.getString();
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getName() {
      return this.name;
   }
}
