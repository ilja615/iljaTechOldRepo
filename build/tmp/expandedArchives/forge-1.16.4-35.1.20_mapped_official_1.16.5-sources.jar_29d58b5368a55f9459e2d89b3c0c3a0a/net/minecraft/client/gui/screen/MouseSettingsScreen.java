package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MouseSettingsScreen extends SettingsScreen {
   private OptionsRowList list;
   private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.SENSITIVITY, AbstractOption.INVERT_MOUSE, AbstractOption.MOUSE_WHEEL_SENSITIVITY, AbstractOption.DISCRETE_MOUSE_SCROLL, AbstractOption.TOUCHSCREEN};

   public MouseSettingsScreen(Screen p_i225929_1_, GameSettings p_i225929_2_) {
      super(p_i225929_1_, p_i225929_2_, new TranslationTextComponent("options.mouse_settings.title"));
   }

   protected void init() {
      this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      if (InputMappings.isRawMouseInputSupported()) {
         this.list.addSmall(Stream.concat(Arrays.stream(OPTIONS), Stream.of(AbstractOption.RAW_MOUSE_INPUT)).toArray((p_223702_0_) -> {
            return new AbstractOption[p_223702_0_];
         }));
      } else {
         this.list.addSmall(OPTIONS);
      }

      this.children.add(this.list);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (p_223703_1_) -> {
         this.options.save();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 5, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
