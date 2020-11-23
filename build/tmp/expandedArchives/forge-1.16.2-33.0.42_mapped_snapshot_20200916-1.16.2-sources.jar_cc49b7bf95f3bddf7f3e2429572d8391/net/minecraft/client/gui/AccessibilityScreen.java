package net.minecraft.client.gui;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WithNarratorSettingsScreen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AccessibilityScreen extends WithNarratorSettingsScreen {
   private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.NARRATOR, AbstractOption.SHOW_SUBTITLES, AbstractOption.ACCESSIBILITY_TEXT_BACKGROUND_OPACITY, AbstractOption.ACCESSIBILITY_TEXT_BACKGROUND, AbstractOption.CHAT_OPACITY, AbstractOption.LINE_SPACING, AbstractOption.DELAY_INSTANT, AbstractOption.AUTO_JUMP, AbstractOption.SNEAK, AbstractOption.SPRINT, AbstractOption.SCREEN_EFFECT_SCALE_SLIDER, AbstractOption.FOV_EFFECT_SCALE_SLIDER};

   public AccessibilityScreen(Screen parentScreen, GameSettings settings) {
      super(parentScreen, settings, new TranslationTextComponent("options.accessibility.title"), OPTIONS);
   }
}