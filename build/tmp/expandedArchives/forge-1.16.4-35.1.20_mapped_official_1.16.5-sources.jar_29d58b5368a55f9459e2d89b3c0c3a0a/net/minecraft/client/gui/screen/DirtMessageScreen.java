package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DirtMessageScreen extends Screen {
   public DirtMessageScreen(ITextComponent p_i51114_1_) {
      super(p_i51114_1_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderDirtBackground(0);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 70, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
