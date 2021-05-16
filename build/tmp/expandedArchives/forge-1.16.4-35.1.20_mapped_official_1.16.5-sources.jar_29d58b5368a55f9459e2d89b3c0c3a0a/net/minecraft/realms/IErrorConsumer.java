package net.minecraft.realms;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IErrorConsumer {
   void error(ITextComponent p_230434_1_);

   default void error(String p_237703_1_) {
      this.error(new StringTextComponent(p_237703_1_));
   }
}
