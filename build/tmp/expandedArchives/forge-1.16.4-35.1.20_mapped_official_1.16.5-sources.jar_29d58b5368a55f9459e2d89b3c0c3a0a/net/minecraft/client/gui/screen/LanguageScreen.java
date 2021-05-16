package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageScreen extends SettingsScreen {
   private static final ITextComponent WARNING_LABEL = (new StringTextComponent("(")).append(new TranslationTextComponent("options.languageWarning")).append(")").withStyle(TextFormatting.GRAY);
   private LanguageScreen.List packSelectionList;
   private final LanguageManager languageManager;
   private OptionButton forceUnicodeButton;
   private Button doneButton;

   public LanguageScreen(Screen p_i1043_1_, GameSettings p_i1043_2_, LanguageManager p_i1043_3_) {
      super(p_i1043_1_, p_i1043_2_, new TranslationTextComponent("options.language"));
      this.languageManager = p_i1043_3_;
   }

   protected void init() {
      this.packSelectionList = new LanguageScreen.List(this.minecraft);
      this.children.add(this.packSelectionList);
      this.forceUnicodeButton = this.addButton(new OptionButton(this.width / 2 - 155, this.height - 38, 150, 20, AbstractOption.FORCE_UNICODE_FONT, AbstractOption.FORCE_UNICODE_FONT.getMessage(this.options), (p_213037_1_) -> {
         AbstractOption.FORCE_UNICODE_FONT.toggle(this.options);
         this.options.save();
         p_213037_1_.setMessage(AbstractOption.FORCE_UNICODE_FONT.getMessage(this.options));
         this.minecraft.resizeDisplay();
      }));
      this.doneButton = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, DialogTexts.GUI_DONE, (p_213036_1_) -> {
         LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = this.packSelectionList.getSelected();
         if (languagescreen$list$languageentry != null && !languagescreen$list$languageentry.language.getCode().equals(this.languageManager.getSelected().getCode())) {
            this.languageManager.setSelected(languagescreen$list$languageentry.language);
            this.options.languageCode = languagescreen$list$languageentry.language.getCode();
            net.minecraftforge.client.ForgeHooksClient.refreshResources(this.minecraft, net.minecraftforge.resource.VanillaResourceType.LANGUAGES);
            this.doneButton.setMessage(DialogTexts.GUI_DONE);
            this.forceUnicodeButton.setMessage(AbstractOption.FORCE_UNICODE_FONT.getMessage(this.options));
            this.options.save();
         }

         this.minecraft.setScreen(this.lastScreen);
      }));
      super.init();
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.packSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 16, 16777215);
      drawCenteredString(p_230430_1_, this.font, WARNING_LABEL, this.width / 2, this.height - 56, 8421504);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   @OnlyIn(Dist.CLIENT)
   class List extends ExtendedList<LanguageScreen.List.LanguageEntry> {
      public List(Minecraft p_i45519_2_) {
         super(p_i45519_2_, LanguageScreen.this.width, LanguageScreen.this.height, 32, LanguageScreen.this.height - 65 + 4, 18);

         for(Language language : LanguageScreen.this.languageManager.getLanguages()) {
            LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = new LanguageScreen.List.LanguageEntry(language);
            this.addEntry(languagescreen$list$languageentry);
            if (LanguageScreen.this.languageManager.getSelected().getCode().equals(language.getCode())) {
               this.setSelected(languagescreen$list$languageentry);
            }
         }

         if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
         }

      }

      protected int getScrollbarPosition() {
         return super.getScrollbarPosition() + 20;
      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public void setSelected(@Nullable LanguageScreen.List.LanguageEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         if (p_241215_1_ != null) {
            NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", p_241215_1_.language)).getString());
         }

      }

      protected void renderBackground(MatrixStack p_230433_1_) {
         LanguageScreen.this.renderBackground(p_230433_1_);
      }

      protected boolean isFocused() {
         return LanguageScreen.this.getFocused() == this;
      }

      @OnlyIn(Dist.CLIENT)
      public class LanguageEntry extends ExtendedList.AbstractListEntry<LanguageScreen.List.LanguageEntry> {
         private final Language language;

         public LanguageEntry(Language p_i50494_2_) {
            this.language = p_i50494_2_;
         }

         public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            String s = this.language.toString();
            LanguageScreen.this.font.drawShadow(p_230432_1_, s, (float)(List.this.width / 2 - LanguageScreen.this.font.width(s) / 2), (float)(p_230432_3_ + 1), 16777215, true);
         }

         public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
            if (p_231044_5_ == 0) {
               this.select();
               return true;
            } else {
               return false;
            }
         }

         private void select() {
            List.this.setSelected(this);
         }
      }
   }
}
