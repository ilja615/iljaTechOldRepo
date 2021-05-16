package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsInviteScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ITextComponent NAME_LABEL = new TranslationTextComponent("mco.configure.world.invite.profile.name");
   private static final ITextComponent NO_SUCH_PLAYER_ERROR_TEXT = new TranslationTextComponent("mco.configure.world.players.error");
   private TextFieldWidget profileName;
   private final RealmsServer serverData;
   private final RealmsConfigureWorldScreen configureScreen;
   private final Screen lastScreen;
   @Nullable
   private ITextComponent errorMsg;

   public RealmsInviteScreen(RealmsConfigureWorldScreen p_i232207_1_, Screen p_i232207_2_, RealmsServer p_i232207_3_) {
      this.configureScreen = p_i232207_1_;
      this.lastScreen = p_i232207_2_;
      this.serverData = p_i232207_3_;
   }

   public void tick() {
      this.profileName.tick();
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.profileName = new TextFieldWidget(this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.configure.world.invite.profile.name"));
      this.addWidget(this.profileName);
      this.setInitialFocus(this.profileName);
      this.addButton(new Button(this.width / 2 - 100, row(10), 200, 20, new TranslationTextComponent("mco.configure.world.buttons.invite"), (p_237844_1_) -> {
         this.onInvite();
      }));
      this.addButton(new Button(this.width / 2 - 100, row(12), 200, 20, DialogTexts.GUI_CANCEL, (p_237843_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onInvite() {
      RealmsClient realmsclient = RealmsClient.create();
      if (this.profileName.getValue() != null && !this.profileName.getValue().isEmpty()) {
         try {
            RealmsServer realmsserver = realmsclient.invite(this.serverData.id, this.profileName.getValue().trim());
            if (realmsserver != null) {
               this.serverData.players = realmsserver.players;
               this.minecraft.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            } else {
               this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
            }
         } catch (Exception exception) {
            LOGGER.error("Couldn't invite user");
            this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
         }

      } else {
         this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
      }
   }

   private void showError(ITextComponent p_224209_1_) {
      this.errorMsg = p_224209_1_;
      RealmsNarratorHelper.now(p_224209_1_.getString());
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.font.draw(p_230430_1_, NAME_LABEL, (float)(this.width / 2 - 100), (float)row(1), 10526880);
      if (this.errorMsg != null) {
         drawCenteredString(p_230430_1_, this.font, this.errorMsg, this.width / 2, row(5), 16711680);
      }

      this.profileName.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
