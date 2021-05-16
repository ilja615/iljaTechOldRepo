package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IWindowEventListener {
   void setWindowActive(boolean p_213228_1_);

   void resizeDisplay();

   void cursorEntered();
}
