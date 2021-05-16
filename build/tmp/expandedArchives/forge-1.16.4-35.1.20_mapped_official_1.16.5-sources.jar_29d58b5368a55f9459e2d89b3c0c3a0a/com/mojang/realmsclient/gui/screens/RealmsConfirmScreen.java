package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsConfirmScreen extends RealmsScreen {
   protected BooleanConsumer callback;
   private final ITextComponent title1;
   private final ITextComponent title2;
   private int delayTicker;

   public RealmsConfirmScreen(BooleanConsumer p_i232202_1_, ITextComponent p_i232202_2_, ITextComponent p_i232202_3_) {
      this.callback = p_i232202_1_;
      this.title1 = p_i232202_2_;
      this.title2 = p_i232202_3_;
   }

   public void init() {
      this.addButton(new Button(this.width / 2 - 105, row(9), 100, 20, DialogTexts.GUI_YES, (p_237826_1_) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 + 5, row(9), 100, 20, DialogTexts.GUI_NO, (p_237825_1_) -> {
         this.callback.accept(false);
      }));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title1, this.width / 2, row(3), 16777215);
      drawCenteredString(p_230430_1_, this.font, this.title2, this.width / 2, row(5), 16777215);
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
