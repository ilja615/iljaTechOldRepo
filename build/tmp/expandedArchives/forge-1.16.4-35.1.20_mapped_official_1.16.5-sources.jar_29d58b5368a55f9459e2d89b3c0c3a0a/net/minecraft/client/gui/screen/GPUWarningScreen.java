package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GPUWarningScreen extends Screen {
   private final ITextProperties message;
   private final ImmutableList<GPUWarningScreen.Option> buttonOptions;
   private IBidiRenderer messageLines = IBidiRenderer.EMPTY;
   private int contentTop;
   private int buttonWidth;

   protected GPUWarningScreen(ITextComponent p_i241250_1_, List<ITextProperties> p_i241250_2_, ImmutableList<GPUWarningScreen.Option> p_i241250_3_) {
      super(p_i241250_1_);
      this.message = ITextProperties.composite(p_i241250_2_);
      this.buttonOptions = p_i241250_3_;
   }

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.message.getString();
   }

   public void init(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
      super.init(p_231158_1_, p_231158_2_, p_231158_3_);

      for(GPUWarningScreen.Option gpuwarningscreen$option : this.buttonOptions) {
         this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width(gpuwarningscreen$option.message) + 20);
      }

      int l = 5 + this.buttonWidth + 5;
      int i1 = l * this.buttonOptions.size();
      this.messageLines = IBidiRenderer.create(this.font, this.message, i1);
      int i = this.messageLines.getLineCount() * 9;
      this.contentTop = (int)((double)p_231158_3_ / 2.0D - (double)i / 2.0D);
      int j = this.contentTop + i + 9 * 2;
      int k = (int)((double)p_231158_2_ / 2.0D - (double)i1 / 2.0D);

      for(GPUWarningScreen.Option gpuwarningscreen$option1 : this.buttonOptions) {
         this.addButton(new Button(k, j, this.buttonWidth, 20, gpuwarningscreen$option1.message, gpuwarningscreen$option1.onPress));
         k += l;
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderDirtBackground(0);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, this.contentTop - 9 * 2, -1);
      this.messageLines.renderCentered(p_230430_1_, this.width / 2, this.contentTop);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Option {
      private final ITextComponent message;
      private final Button.IPressable onPress;

      public Option(ITextComponent p_i241251_1_, Button.IPressable p_i241251_2_) {
         this.message = p_i241251_1_;
         this.onPress = p_i241251_2_;
      }
   }
}
