package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorkingScreen extends Screen implements IProgressUpdate {
   @Nullable
   private ITextComponent header;
   @Nullable
   private ITextComponent stage;
   private int progress;
   private boolean stop;

   public WorkingScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void progressStartNoAbort(ITextComponent p_200210_1_) {
      this.progressStart(p_200210_1_);
   }

   public void progressStart(ITextComponent p_200211_1_) {
      this.header = p_200211_1_;
      this.progressStage(new TranslationTextComponent("progress.working"));
   }

   public void progressStage(ITextComponent p_200209_1_) {
      this.stage = p_200209_1_;
      this.progressStagePercentage(0);
   }

   public void progressStagePercentage(int p_73718_1_) {
      this.progress = p_73718_1_;
   }

   public void stop() {
      this.stop = true;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      if (this.stop) {
         if (!this.minecraft.isConnectedToRealms()) {
            this.minecraft.setScreen((Screen)null);
         }

      } else {
         this.renderBackground(p_230430_1_);
         if (this.header != null) {
            drawCenteredString(p_230430_1_, this.font, this.header, this.width / 2, 70, 16777215);
         }

         if (this.stage != null && this.progress != 0) {
            drawCenteredString(p_230430_1_, this.font, (new StringTextComponent("")).append(this.stage).append(" " + this.progress + "%"), this.width / 2, 90, 16777215);
         }

         super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      }
   }
}
