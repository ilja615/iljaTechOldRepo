package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DatapackFailureScreen extends Screen {
   private IBidiRenderer message = IBidiRenderer.EMPTY;
   private final Runnable callback;

   public DatapackFailureScreen(Runnable p_i232276_1_) {
      super(new TranslationTextComponent("datapackFailure.title"));
      this.callback = p_i232276_1_;
   }

   protected void init() {
      super.init();
      this.message = IBidiRenderer.create(this.font, this.getTitle(), this.width - 50);
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslationTextComponent("datapackFailure.safeMode"), (p_238622_1_) -> {
         this.callback.run();
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslationTextComponent("gui.toTitle"), (p_238621_1_) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.message.renderCentered(p_230430_1_, this.width / 2, 70);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
