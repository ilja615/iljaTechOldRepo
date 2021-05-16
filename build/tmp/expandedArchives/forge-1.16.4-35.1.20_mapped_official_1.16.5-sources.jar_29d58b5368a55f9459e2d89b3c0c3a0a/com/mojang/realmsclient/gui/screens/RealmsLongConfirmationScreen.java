package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLongConfirmationScreen extends RealmsScreen {
   private final RealmsLongConfirmationScreen.Type type;
   private final ITextComponent line2;
   private final ITextComponent line3;
   protected final BooleanConsumer callback;
   private final boolean yesNoQuestion;

   public RealmsLongConfirmationScreen(BooleanConsumer p_i232208_1_, RealmsLongConfirmationScreen.Type p_i232208_2_, ITextComponent p_i232208_3_, ITextComponent p_i232208_4_, boolean p_i232208_5_) {
      this.callback = p_i232208_1_;
      this.type = p_i232208_2_;
      this.line2 = p_i232208_3_;
      this.line3 = p_i232208_4_;
      this.yesNoQuestion = p_i232208_5_;
   }

   public void init() {
      RealmsNarratorHelper.now(this.type.text, this.line2.getString(), this.line3.getString());
      if (this.yesNoQuestion) {
         this.addButton(new Button(this.width / 2 - 105, row(8), 100, 20, DialogTexts.GUI_YES, (p_237848_1_) -> {
            this.callback.accept(true);
         }));
         this.addButton(new Button(this.width / 2 + 5, row(8), 100, 20, DialogTexts.GUI_NO, (p_237847_1_) -> {
            this.callback.accept(false);
         }));
      } else {
         this.addButton(new Button(this.width / 2 - 50, row(8), 100, 20, new TranslationTextComponent("mco.gui.ok"), (p_237846_1_) -> {
            this.callback.accept(true);
         }));
      }

   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.type.text, this.width / 2, row(2), this.type.colorCode);
      drawCenteredString(p_230430_1_, this.font, this.line2, this.width / 2, row(4), 16777215);
      drawCenteredString(p_230430_1_, this.font, this.line3, this.width / 2, row(6), 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      Warning("Warning!", 16711680),
      Info("Info!", 8226750);

      public final int colorCode;
      public final String text;

      private Type(String p_i51697_3_, int p_i51697_4_) {
         this.text = p_i51697_3_;
         this.colorCode = p_i51697_4_;
      }
   }
}
