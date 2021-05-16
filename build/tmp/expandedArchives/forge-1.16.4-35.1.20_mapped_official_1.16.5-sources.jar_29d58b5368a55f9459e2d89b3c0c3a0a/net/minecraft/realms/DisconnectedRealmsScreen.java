package net.minecraft.realms;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
   private final ITextComponent title;
   private final ITextComponent reason;
   private IBidiRenderer message = IBidiRenderer.EMPTY;
   private final Screen parent;
   private int textHeight;

   public DisconnectedRealmsScreen(Screen p_i242069_1_, ITextComponent p_i242069_2_, ITextComponent p_i242069_3_) {
      this.parent = p_i242069_1_;
      this.title = p_i242069_2_;
      this.reason = p_i242069_3_;
   }

   public void init() {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.setConnectedToRealms(false);
      minecraft.getClientPackSource().clearServerPack();
      RealmsNarratorHelper.now(this.title.getString() + ": " + this.reason.getString());
      this.message = IBidiRenderer.create(this.font, this.reason, this.width - 50);
      this.textHeight = this.message.getLineCount() * 9;
      this.addButton(new Button(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20, DialogTexts.GUI_BACK, (p_239547_2_) -> {
         minecraft.setScreen(this.parent);
      }));
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
      this.message.renderCentered(p_230430_1_, this.width / 2, this.height / 2 - this.textHeight / 2);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
