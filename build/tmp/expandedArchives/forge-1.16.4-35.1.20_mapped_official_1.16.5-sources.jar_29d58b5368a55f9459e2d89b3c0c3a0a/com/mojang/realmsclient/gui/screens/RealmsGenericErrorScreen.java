package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
   private final Screen nextScreen;
   private ITextComponent line1;
   private ITextComponent line2;

   public RealmsGenericErrorScreen(RealmsServiceException p_i232204_1_, Screen p_i232204_2_) {
      this.nextScreen = p_i232204_2_;
      this.errorMessage(p_i232204_1_);
   }

   public RealmsGenericErrorScreen(ITextComponent p_i232205_1_, Screen p_i232205_2_) {
      this.nextScreen = p_i232205_2_;
      this.errorMessage(p_i232205_1_);
   }

   public RealmsGenericErrorScreen(ITextComponent p_i232206_1_, ITextComponent p_i232206_2_, Screen p_i232206_3_) {
      this.nextScreen = p_i232206_3_;
      this.errorMessage(p_i232206_1_, p_i232206_2_);
   }

   private void errorMessage(RealmsServiceException p_224224_1_) {
      if (p_224224_1_.errorCode == -1) {
         this.line1 = new StringTextComponent("An error occurred (" + p_224224_1_.httpResultCode + "):");
         this.line2 = new StringTextComponent(p_224224_1_.httpResponseContent);
      } else {
         this.line1 = new StringTextComponent("Realms (" + p_224224_1_.errorCode + "):");
         String s = "mco.errorMessage." + p_224224_1_.errorCode;
         this.line2 = (ITextComponent)(I18n.exists(s) ? new TranslationTextComponent(s) : ITextComponent.nullToEmpty(p_224224_1_.errorMsg));
      }

   }

   private void errorMessage(ITextComponent p_237841_1_) {
      this.line1 = new StringTextComponent("An error occurred: ");
      this.line2 = p_237841_1_;
   }

   private void errorMessage(ITextComponent p_237842_1_, ITextComponent p_237842_2_) {
      this.line1 = p_237842_1_;
      this.line2 = p_237842_2_;
   }

   public void init() {
      RealmsNarratorHelper.now(this.line1.getString() + ": " + this.line2.getString());
      this.addButton(new Button(this.width / 2 - 100, this.height - 52, 200, 20, new StringTextComponent("Ok"), (p_237840_1_) -> {
         this.minecraft.setScreen(this.nextScreen);
      }));
   }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            minecraft.setScreen(this.nextScreen);
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.line1, this.width / 2, 80, 16777215);
      drawCenteredString(p_230430_1_, this.font, this.line2, this.width / 2, 100, 16711680);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
