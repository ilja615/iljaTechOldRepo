package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGuiEventListener {
   default void mouseMoved(double p_212927_1_, double p_212927_3_) {
   }

   default boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      return false;
   }

   default boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      return false;
   }

   default boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
      return false;
   }

   default boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
      return false;
   }

   default boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      return false;
   }

   default boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
      return false;
   }

   default boolean charTyped(char p_231042_1_, int p_231042_2_) {
      return false;
   }

   default boolean changeFocus(boolean p_231049_1_) {
      return false;
   }

   default boolean isMouseOver(double p_231047_1_, double p_231047_3_) {
      return false;
   }
}
