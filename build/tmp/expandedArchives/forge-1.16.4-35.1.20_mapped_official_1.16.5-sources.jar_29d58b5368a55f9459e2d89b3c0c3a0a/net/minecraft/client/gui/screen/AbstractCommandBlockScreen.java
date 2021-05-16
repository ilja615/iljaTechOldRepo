package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
   private static final ITextComponent SET_COMMAND_LABEL = new TranslationTextComponent("advMode.setCommand");
   private static final ITextComponent COMMAND_LABEL = new TranslationTextComponent("advMode.command");
   private static final ITextComponent PREVIOUS_OUTPUT_LABEL = new TranslationTextComponent("advMode.previousOutput");
   protected TextFieldWidget commandEdit;
   protected TextFieldWidget previousEdit;
   protected Button doneButton;
   protected Button cancelButton;
   protected Button outputButton;
   protected boolean trackOutput;
   private CommandSuggestionHelper commandSuggestions;

   public AbstractCommandBlockScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public void tick() {
      this.commandEdit.tick();
   }

   abstract CommandBlockLogic getCommandBlock();

   abstract int getPreviousY();

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, DialogTexts.GUI_DONE, (p_214187_1_) -> {
         this.onDone();
      }));
      this.cancelButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, DialogTexts.GUI_CANCEL, (p_214186_1_) -> {
         this.onClose();
      }));
      this.outputButton = this.addButton(new Button(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, new StringTextComponent("O"), (p_214184_1_) -> {
         CommandBlockLogic commandblocklogic = this.getCommandBlock();
         commandblocklogic.setTrackOutput(!commandblocklogic.isTrackOutput());
         this.updateCommandOutput();
      }));
      this.commandEdit = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, new TranslationTextComponent("advMode.command")) {
         protected IFormattableTextComponent createNarrationMessage() {
            return super.createNarrationMessage().append(AbstractCommandBlockScreen.this.commandSuggestions.getNarrationMessage());
         }
      };
      this.commandEdit.setMaxLength(32500);
      this.commandEdit.setResponder(this::onEdited);
      this.children.add(this.commandEdit);
      this.previousEdit = new TextFieldWidget(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, new TranslationTextComponent("advMode.previousOutput"));
      this.previousEdit.setMaxLength(32500);
      this.previousEdit.setEditable(false);
      this.previousEdit.setValue("-");
      this.children.add(this.previousEdit);
      this.setInitialFocus(this.commandEdit);
      this.commandEdit.setFocus(true);
      this.commandSuggestions = new CommandSuggestionHelper(this.minecraft, this, this.commandEdit, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
      this.commandSuggestions.setAllowSuggestions(true);
      this.commandSuggestions.updateCommandInfo();
   }

   public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
      String s = this.commandEdit.getValue();
      this.init(p_231152_1_, p_231152_2_, p_231152_3_);
      this.commandEdit.setValue(s);
      this.commandSuggestions.updateCommandInfo();
   }

   protected void updateCommandOutput() {
      if (this.getCommandBlock().isTrackOutput()) {
         this.outputButton.setMessage(new StringTextComponent("O"));
         this.previousEdit.setValue(this.getCommandBlock().getLastOutput().getString());
      } else {
         this.outputButton.setMessage(new StringTextComponent("X"));
         this.previousEdit.setValue("-");
      }

   }

   protected void onDone() {
      CommandBlockLogic commandblocklogic = this.getCommandBlock();
      this.populateAndSendPacket(commandblocklogic);
      if (!commandblocklogic.isTrackOutput()) {
         commandblocklogic.setLastOutput((ITextComponent)null);
      }

      this.minecraft.setScreen((Screen)null);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   protected abstract void populateAndSendPacket(CommandBlockLogic p_195235_1_);

   public void onClose() {
      this.getCommandBlock().setTrackOutput(this.trackOutput);
      this.minecraft.setScreen((Screen)null);
   }

   private void onEdited(String p_214185_1_) {
      this.commandSuggestions.updateCommandInfo();
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (this.commandSuggestions.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
         return true;
      } else if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
         return true;
      } else if (p_231046_1_ != 257 && p_231046_1_ != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
      return this.commandSuggestions.mouseScrolled(p_231043_5_) ? true : super.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      return this.commandSuggestions.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_) ? true : super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, SET_COMMAND_LABEL, this.width / 2, 20, 16777215);
      drawString(p_230430_1_, this.font, COMMAND_LABEL, this.width / 2 - 150, 40, 10526880);
      this.commandEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      int i = 75;
      if (!this.previousEdit.getValue().isEmpty()) {
         i = i + (5 * 9 + 1 + this.getPreviousY() - 135);
         drawString(p_230430_1_, this.font, PREVIOUS_OUTPUT_LABEL, this.width / 2 - 150, i + 4, 10526880);
         this.previousEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.commandSuggestions.render(p_230430_1_, p_230430_2_, p_230430_3_);
   }
}
