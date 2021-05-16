package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShareToLanScreen extends Screen {
   private static final ITextComponent ALLOW_COMMANDS_LABEL = new TranslationTextComponent("selectWorld.allowCommands");
   private static final ITextComponent GAME_MODE_LABEL = new TranslationTextComponent("selectWorld.gameMode");
   private static final ITextComponent INFO_TEXT = new TranslationTextComponent("lanServer.otherPlayers");
   private final Screen lastScreen;
   private Button commandsButton;
   private Button modeButton;
   private String gameModeName = "survival";
   private boolean commands;

   public ShareToLanScreen(Screen p_i1055_1_) {
      super(new TranslationTextComponent("lanServer.title"));
      this.lastScreen = p_i1055_1_;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslationTextComponent("lanServer.start"), (p_213082_1_) -> {
         this.minecraft.setScreen((Screen)null);
         int i = HTTPUtil.getAvailablePort();
         ITextComponent itextcomponent;
         if (this.minecraft.getSingleplayerServer().publishServer(GameType.byName(this.gameModeName), this.commands, i)) {
            itextcomponent = new TranslationTextComponent("commands.publish.started", i);
         } else {
            itextcomponent = new TranslationTextComponent("commands.publish.failed");
         }

         this.minecraft.gui.getChat().addMessage(itextcomponent);
         this.minecraft.updateTitle();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, DialogTexts.GUI_CANCEL, (p_213085_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.modeButton = this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, StringTextComponent.EMPTY, (p_213084_1_) -> {
         if ("spectator".equals(this.gameModeName)) {
            this.gameModeName = "creative";
         } else if ("creative".equals(this.gameModeName)) {
            this.gameModeName = "adventure";
         } else if ("adventure".equals(this.gameModeName)) {
            this.gameModeName = "survival";
         } else {
            this.gameModeName = "spectator";
         }

         this.updateSelectionStrings();
      }));
      this.commandsButton = this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, (p_213083_1_) -> {
         this.commands = !this.commands;
         this.updateSelectionStrings();
      }));
      this.updateSelectionStrings();
   }

   private void updateSelectionStrings() {
      this.modeButton.setMessage(new TranslationTextComponent("options.generic_value", GAME_MODE_LABEL, new TranslationTextComponent("selectWorld.gameMode." + this.gameModeName)));
      this.commandsButton.setMessage(DialogTexts.optionStatus(ALLOW_COMMANDS_LABEL, this.commands));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 50, 16777215);
      drawCenteredString(p_230430_1_, this.font, INFO_TEXT, this.width / 2, 82, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
