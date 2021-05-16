package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AlertScreen extends Screen {
   private final Runnable callback;
   protected final ITextComponent text;
   private IBidiRenderer message = IBidiRenderer.EMPTY;
   protected final ITextComponent okButton;
   private int delayTicker;

   public AlertScreen(Runnable p_i48623_1_, ITextComponent p_i48623_2_, ITextComponent p_i48623_3_) {
      this(p_i48623_1_, p_i48623_2_, p_i48623_3_, DialogTexts.GUI_BACK);
   }

   public AlertScreen(Runnable p_i232268_1_, ITextComponent p_i232268_2_, ITextComponent p_i232268_3_, ITextComponent p_i232268_4_) {
      super(p_i232268_2_);
      this.callback = p_i232268_1_;
      this.text = p_i232268_3_;
      this.okButton = p_i232268_4_;
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.okButton, (p_212983_1_) -> {
         this.callback.run();
      }));
      this.message = IBidiRenderer.create(this.font, this.text, this.width - 50);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 70, 16777215);
      this.message.renderCentered(p_230430_1_, this.width / 2, 90);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   public void tick() {
      super.tick();
      if (--this.delayTicker == 0) {
         for(Widget widget : this.buttons) {
            widget.active = true;
         }
      }

   }
}
