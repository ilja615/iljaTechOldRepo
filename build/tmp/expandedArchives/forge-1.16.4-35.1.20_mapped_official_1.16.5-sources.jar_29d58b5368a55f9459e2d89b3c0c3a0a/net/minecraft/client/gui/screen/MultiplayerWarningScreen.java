package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiplayerWarningScreen extends Screen {
   private final Screen previous;
   private static final ITextComponent TITLE = (new TranslationTextComponent("multiplayerWarning.header")).withStyle(TextFormatting.BOLD);
   private static final ITextComponent CONTENT = new TranslationTextComponent("multiplayerWarning.message");
   private static final ITextComponent CHECK = new TranslationTextComponent("multiplayerWarning.check");
   private static final ITextComponent NARRATION = TITLE.copy().append("\n").append(CONTENT);
   private CheckboxButton stopShowing;
   private IBidiRenderer message = IBidiRenderer.EMPTY;

   public MultiplayerWarningScreen(Screen p_i230052_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.previous = p_i230052_1_;
   }

   protected void init() {
      super.init();
      this.message = IBidiRenderer.create(this.font, CONTENT, this.width - 50);
      int i = (this.message.getLineCount() + 1) * 9 * 2;
      this.addButton(new Button(this.width / 2 - 155, 100 + i, 150, 20, DialogTexts.GUI_PROCEED, (p_230165_1_) -> {
         if (this.stopShowing.selected()) {
            this.minecraft.options.skipMultiplayerWarning = true;
            this.minecraft.options.save();
         }

         this.minecraft.setScreen(new MultiplayerScreen(this.previous));
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, DialogTexts.GUI_BACK, (p_230164_1_) -> {
         this.minecraft.setScreen(this.previous);
      }));
      this.stopShowing = new CheckboxButton(this.width / 2 - 155 + 80, 76 + i, 150, 20, CHECK, false);
      this.addButton(this.stopShowing);
   }

   public String getNarrationMessage() {
      return NARRATION.getString();
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderDirtBackground(0);
      drawString(p_230430_1_, this.font, TITLE, 25, 30, 16777215);
      this.message.renderLeftAligned(p_230430_1_, 25, 70, 9 * 2, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
