package net.minecraft.client.gui.screen;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatOptionsScreen extends WithNarratorSettingsScreen {
   private static final AbstractOption[] CHAT_OPTIONS = new AbstractOption[]{AbstractOption.CHAT_VISIBILITY, AbstractOption.CHAT_COLOR, AbstractOption.CHAT_LINKS, AbstractOption.CHAT_LINKS_PROMPT, AbstractOption.CHAT_OPACITY, AbstractOption.TEXT_BACKGROUND_OPACITY, AbstractOption.CHAT_SCALE, AbstractOption.CHAT_LINE_SPACING, AbstractOption.CHAT_DELAY, AbstractOption.CHAT_WIDTH, AbstractOption.CHAT_HEIGHT_FOCUSED, AbstractOption.CHAT_HEIGHT_UNFOCUSED, AbstractOption.NARRATOR, AbstractOption.AUTO_SUGGESTIONS, AbstractOption.HIDE_MATCHED_NAMES, AbstractOption.REDUCED_DEBUG_INFO};

   public ChatOptionsScreen(Screen p_i1023_1_, GameSettings p_i1023_2_) {
      super(p_i1023_1_, p_i1023_2_, new TranslationTextComponent("options.chat.title"), CHAT_OPTIONS);
   }
}
