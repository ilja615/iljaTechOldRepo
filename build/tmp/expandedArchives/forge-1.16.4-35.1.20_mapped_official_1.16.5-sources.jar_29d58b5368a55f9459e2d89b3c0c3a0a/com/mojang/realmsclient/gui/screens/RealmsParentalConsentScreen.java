package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
   private static final ITextComponent MESSAGE = new TranslationTextComponent("mco.account.privacyinfo");
   private final Screen nextScreen;
   private IBidiRenderer messageLines = IBidiRenderer.EMPTY;

   public RealmsParentalConsentScreen(Screen p_i232210_1_) {
      this.nextScreen = p_i232210_1_;
   }

   public void init() {
      RealmsNarratorHelper.now(MESSAGE.getString());
      ITextComponent itextcomponent = new TranslationTextComponent("mco.account.update");
      ITextComponent itextcomponent1 = DialogTexts.GUI_BACK;
      int i = Math.max(this.font.width(itextcomponent), this.font.width(itextcomponent1)) + 30;
      ITextComponent itextcomponent2 = new TranslationTextComponent("mco.account.privacy.info");
      int j = (int)((double)this.font.width(itextcomponent2) * 1.2D);
      this.addButton(new Button(this.width / 2 - j / 2, row(11), j, 20, itextcomponent2, (p_237862_0_) -> {
         Util.getPlatform().openUri("https://aka.ms/MinecraftGDPR");
      }));
      this.addButton(new Button(this.width / 2 - (i + 5), row(13), i, 20, itextcomponent, (p_237861_0_) -> {
         Util.getPlatform().openUri("https://aka.ms/UpdateMojangAccount");
      }));
      this.addButton(new Button(this.width / 2 + 5, row(13), i, 20, itextcomponent1, (p_237860_1_) -> {
         this.minecraft.setScreen(this.nextScreen);
      }));
      this.messageLines = IBidiRenderer.create(this.font, MESSAGE, (int)Math.round((double)this.width * 0.9D));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.messageLines.renderCentered(p_230430_1_, this.width / 2, 15, 15, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
