package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmOpenLinkScreen extends ConfirmScreen {
   private final ITextComponent warning;
   private final ITextComponent copyButton;
   private final String url;
   private final boolean showWarning;

   public ConfirmOpenLinkScreen(BooleanConsumer p_i51121_1_, String p_i51121_2_, boolean p_i51121_3_) {
      super(p_i51121_1_, new TranslationTextComponent(p_i51121_3_ ? "chat.link.confirmTrusted" : "chat.link.confirm"), new StringTextComponent(p_i51121_2_));
      this.yesButton = (ITextComponent)(p_i51121_3_ ? new TranslationTextComponent("chat.link.open") : DialogTexts.GUI_YES);
      this.noButton = p_i51121_3_ ? DialogTexts.GUI_CANCEL : DialogTexts.GUI_NO;
      this.copyButton = new TranslationTextComponent("chat.copy");
      this.warning = new TranslationTextComponent("chat.link.warning");
      this.showWarning = !p_i51121_3_;
      this.url = p_i51121_2_;
   }

   protected void init() {
      super.init();
      this.buttons.clear();
      this.children.clear();
      this.addButton(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesButton, (p_213006_1_) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyButton, (p_213005_1_) -> {
         this.copyToClipboard();
         this.callback.accept(false);
      }));
      this.addButton(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noButton, (p_213004_1_) -> {
         this.callback.accept(false);
      }));
   }

   public void copyToClipboard() {
      this.minecraft.keyboardHandler.setClipboard(this.url);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.showWarning) {
         drawCenteredString(p_230430_1_, this.font, this.warning, this.width / 2, 110, 16764108);
      }

   }
}
