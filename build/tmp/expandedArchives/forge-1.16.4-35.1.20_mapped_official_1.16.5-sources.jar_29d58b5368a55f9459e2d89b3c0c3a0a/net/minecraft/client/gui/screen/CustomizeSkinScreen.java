package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomizeSkinScreen extends SettingsScreen {
   public CustomizeSkinScreen(Screen p_i225931_1_, GameSettings p_i225931_2_) {
      super(p_i225931_1_, p_i225931_2_, new TranslationTextComponent("options.skinCustomisation.title"));
   }

   protected void init() {
      int i = 0;

      for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
         this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.getMessage(playermodelpart), (p_213080_2_) -> {
            this.options.toggleModelPart(playermodelpart);
            p_213080_2_.setMessage(this.getMessage(playermodelpart));
         }));
         ++i;
      }

      this.addButton(new OptionButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, AbstractOption.MAIN_HAND, AbstractOption.MAIN_HAND.getMessage(this.options), (p_213081_1_) -> {
         AbstractOption.MAIN_HAND.toggle(this.options, 1);
         this.options.save();
         p_213081_1_.setMessage(AbstractOption.MAIN_HAND.getMessage(this.options));
         this.options.broadcastOptions();
      }));
      ++i;
      if (i % 2 == 1) {
         ++i;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, DialogTexts.GUI_DONE, (p_213079_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private ITextComponent getMessage(PlayerModelPart p_238655_1_) {
      return DialogTexts.optionStatus(p_238655_1_.getName(), this.options.getModelParts().contains(p_238655_1_));
   }
}
