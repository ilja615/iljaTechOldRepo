package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSlider extends Widget {
   protected double value;

   public AbstractSlider(int p_i232253_1_, int p_i232253_2_, int p_i232253_3_, int p_i232253_4_, ITextComponent p_i232253_5_, double p_i232253_6_) {
      super(p_i232253_1_, p_i232253_2_, p_i232253_3_, p_i232253_4_, p_i232253_5_);
      this.value = p_i232253_6_;
   }

   protected int getYImage(boolean p_230989_1_) {
      return 0;
   }

   protected IFormattableTextComponent createNarrationMessage() {
      return new TranslationTextComponent("gui.narrate.slider", this.getMessage());
   }

   protected void renderBg(MatrixStack p_230441_1_, Minecraft p_230441_2_, int p_230441_3_, int p_230441_4_) {
      p_230441_2_.getTextureManager().bind(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = (this.isHovered() ? 2 : 1) * 20;
      this.blit(p_230441_1_, this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
      this.blit(p_230441_1_, this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
   }

   public void onClick(double p_230982_1_, double p_230982_3_) {
      this.setValueFromMouse(p_230982_1_);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      boolean flag = p_231046_1_ == 263;
      if (flag || p_231046_1_ == 262) {
         float f = flag ? -1.0F : 1.0F;
         this.setValue(this.value + (double)(f / (float)(this.width - 8)));
      }

      return false;
   }

   private void setValueFromMouse(double p_230973_1_) {
      this.setValue((p_230973_1_ - (double)(this.x + 4)) / (double)(this.width - 8));
   }

   private void setValue(double p_230980_1_) {
      double d0 = this.value;
      this.value = MathHelper.clamp(p_230980_1_, 0.0D, 1.0D);
      if (d0 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void onDrag(double p_230983_1_, double p_230983_3_, double p_230983_5_, double p_230983_7_) {
      this.setValueFromMouse(p_230983_1_);
      super.onDrag(p_230983_1_, p_230983_3_, p_230983_5_, p_230983_7_);
   }

   public void playDownSound(SoundHandler p_230988_1_) {
   }

   public void onRelease(double p_231000_1_, double p_231000_3_) {
      super.playDownSound(Minecraft.getInstance().getSoundManager());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}
