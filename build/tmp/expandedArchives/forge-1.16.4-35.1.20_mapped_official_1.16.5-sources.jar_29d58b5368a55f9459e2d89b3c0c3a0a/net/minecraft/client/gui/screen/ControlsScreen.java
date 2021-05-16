package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends SettingsScreen {
   public KeyBinding selectedKey;
   public long lastKeySelection;
   private KeyBindingList controlList;
   private Button resetButton;

   public ControlsScreen(Screen p_i1027_1_, GameSettings p_i1027_2_) {
      super(p_i1027_1_, p_i1027_2_, new TranslationTextComponent("controls.title"));
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, new TranslationTextComponent("options.mouse_settings"), (p_213126_1_) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this, this.options));
      }));
      this.addButton(AbstractOption.AUTO_JUMP.createButton(this.options, this.width / 2 - 155 + 160, 18, 150));
      this.controlList = new KeyBindingList(this, this.minecraft);
      this.children.add(this.controlList);
      this.resetButton = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, new TranslationTextComponent("controls.resetAll"), (p_213125_1_) -> {
         for(KeyBinding keybinding : this.options.keyMappings) {
            keybinding.setToDefault();
         }

         KeyBinding.resetMapping();
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, DialogTexts.GUI_DONE, (p_213124_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (this.selectedKey != null) {
         this.options.setKey(this.selectedKey, InputMappings.Type.MOUSE.getOrCreate(p_231044_5_));
         this.selectedKey = null;
         KeyBinding.resetMapping();
         return true;
      } else {
         return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
      }
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (this.selectedKey != null) {
         if (p_231046_1_ == 256) {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.UNKNOWN);
            this.options.setKey(this.selectedKey, InputMappings.UNKNOWN);
         } else {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.getKey(p_231046_1_, p_231046_2_));
            this.options.setKey(this.selectedKey, InputMappings.getKey(p_231046_1_, p_231046_2_));
         }

         if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedKey.getKey()))
         this.selectedKey = null;
         this.lastKeySelection = Util.getMillis();
         KeyBinding.resetMapping();
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.controlList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 8, 16777215);
      boolean flag = false;

      for(KeyBinding keybinding : this.options.keyMappings) {
         if (!keybinding.isDefault()) {
            flag = true;
            break;
         }
      }

      this.resetButton.active = flag;
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
