package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedScreen extends Screen {
   private final ITextComponent reason;
   private IBidiRenderer message = IBidiRenderer.EMPTY;
   private final Screen parent;
   private int textHeight;

   public DisconnectedScreen(Screen p_i242056_1_, ITextComponent p_i242056_2_, ITextComponent p_i242056_3_) {
      super(p_i242056_2_);
      this.parent = p_i242056_1_;
      this.reason = p_i242056_3_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.message = IBidiRenderer.create(this.font, this.reason, this.width - 50);
      this.textHeight = this.message.getLineCount() * 9;
      this.addButton(new Button(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20, new TranslationTextComponent("gui.toMenu"), (p_213033_1_) -> {
         this.minecraft.setScreen(this.parent);
      }));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
      this.message.renderCentered(p_230430_1_, this.width / 2, this.height / 2 - this.textHeight / 2);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
