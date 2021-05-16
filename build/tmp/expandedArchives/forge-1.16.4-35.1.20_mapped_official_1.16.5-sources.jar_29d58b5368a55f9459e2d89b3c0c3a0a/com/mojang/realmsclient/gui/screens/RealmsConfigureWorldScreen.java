package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsServerSlotButton;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.action.CloseRealmsAction;
import net.minecraft.realms.action.OpeningWorldRealmsAction;
import net.minecraft.realms.action.StartMinigameRealmsAction;
import net.minecraft.realms.action.SwitchMinigameRealmsAction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConfigureWorldScreen extends NotifableRealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
   private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
   private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
   private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
   private static final ITextComponent TITLE = new TranslationTextComponent("mco.configure.worlds.title");
   private static final ITextComponent WORLD_TITLE = new TranslationTextComponent("mco.configure.world.title");
   private static final ITextComponent MINIGAME_PREFIX = (new TranslationTextComponent("mco.configure.current.minigame")).append(": ");
   private static final ITextComponent SERVER_EXPIRED_TOOLTIP = new TranslationTextComponent("mco.selectServer.expired");
   private static final ITextComponent SERVER_EXPIRING_SOON_TOOLTIP = new TranslationTextComponent("mco.selectServer.expires.soon");
   private static final ITextComponent SERVER_EXPIRING_IN_DAY_TOOLTIP = new TranslationTextComponent("mco.selectServer.expires.day");
   private static final ITextComponent SERVER_OPEN_TOOLTIP = new TranslationTextComponent("mco.selectServer.open");
   private static final ITextComponent SERVER_CLOSED_TOOLTIP = new TranslationTextComponent("mco.selectServer.closed");
   @Nullable
   private ITextComponent toolTip;
   private final RealmsMainScreen lastScreen;
   @Nullable
   private RealmsServer serverData;
   private final long serverId;
   private int leftX;
   private int rightX;
   private Button playersButton;
   private Button settingsButton;
   private Button subscriptionButton;
   private Button optionsButton;
   private Button backupButton;
   private Button resetWorldButton;
   private Button switchMinigameButton;
   private boolean stateChanged;
   private int animTick;
   private int clicks;

   public RealmsConfigureWorldScreen(RealmsMainScreen p_i51774_1_, long p_i51774_2_) {
      this.lastScreen = p_i51774_1_;
      this.serverId = p_i51774_2_;
   }

   public void init() {
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      this.leftX = this.width / 2 - 187;
      this.rightX = this.width / 2 + 190;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.playersButton = this.addButton(new Button(this.centerButton(0, 3), row(0), 100, 20, new TranslationTextComponent("mco.configure.world.buttons.players"), (p_237818_1_) -> {
         this.minecraft.setScreen(new RealmsPlayerScreen(this, this.serverData));
      }));
      this.settingsButton = this.addButton(new Button(this.centerButton(1, 3), row(0), 100, 20, new TranslationTextComponent("mco.configure.world.buttons.settings"), (p_237817_1_) -> {
         this.minecraft.setScreen(new RealmsSettingsScreen(this, this.serverData.clone()));
      }));
      this.subscriptionButton = this.addButton(new Button(this.centerButton(2, 3), row(0), 100, 20, new TranslationTextComponent("mco.configure.world.buttons.subscription"), (p_237816_1_) -> {
         this.minecraft.setScreen(new RealmsSubscriptionInfoScreen(this, this.serverData.clone(), this.lastScreen));
      }));

      for(int i = 1; i < 5; ++i) {
         this.addSlotButton(i);
      }

      this.switchMinigameButton = this.addButton(new Button(this.leftButton(0), row(13) - 5, 100, 20, new TranslationTextComponent("mco.configure.world.buttons.switchminigame"), (p_237815_1_) -> {
         RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.MINIGAME);
         realmsselectworldtemplatescreen.setTitle(new TranslationTextComponent("mco.template.title.minigame"));
         this.minecraft.setScreen(realmsselectworldtemplatescreen);
      }));
      this.optionsButton = this.addButton(new Button(this.leftButton(0), row(13) - 5, 90, 20, new TranslationTextComponent("mco.configure.world.buttons.options"), (p_237814_1_) -> {
         this.minecraft.setScreen(new RealmsSlotOptionsScreen(this, this.serverData.slots.get(this.serverData.activeSlot).clone(), this.serverData.worldType, this.serverData.activeSlot));
      }));
      this.backupButton = this.addButton(new Button(this.leftButton(1), row(13) - 5, 90, 20, new TranslationTextComponent("mco.configure.world.backup"), (p_237812_1_) -> {
         this.minecraft.setScreen(new RealmsBackupScreen(this, this.serverData.clone(), this.serverData.activeSlot));
      }));
      this.resetWorldButton = this.addButton(new Button(this.leftButton(2), row(13) - 5, 90, 20, new TranslationTextComponent("mco.configure.world.buttons.resetworld"), (p_237810_1_) -> {
         this.minecraft.setScreen(new RealmsResetWorldScreen(this, this.serverData.clone(), () -> {
            this.minecraft.setScreen(this.getNewScreen());
         }, () -> {
            this.minecraft.setScreen(this.getNewScreen());
         }));
      }));
      this.addButton(new Button(this.rightX - 80 + 8, row(13) - 5, 70, 20, DialogTexts.GUI_BACK, (p_237808_1_) -> {
         this.backButtonClicked();
      }));
      this.backupButton.active = true;
      if (this.serverData == null) {
         this.hideMinigameButtons();
         this.hideRegularButtons();
         this.playersButton.active = false;
         this.settingsButton.active = false;
         this.subscriptionButton.active = false;
      } else {
         this.disableButtons();
         if (this.isMinigame()) {
            this.hideRegularButtons();
         } else {
            this.hideMinigameButtons();
         }
      }

   }

   private void addSlotButton(int p_224402_1_) {
      int i = this.frame(p_224402_1_);
      int j = row(5) + 5;
      RealmsServerSlotButton realmsserverslotbutton = new RealmsServerSlotButton(i, j, 80, 80, () -> {
         return this.serverData;
      }, (p_237801_1_) -> {
         this.toolTip = p_237801_1_;
      }, p_224402_1_, (p_237795_2_) -> {
         RealmsServerSlotButton.ServerData realmsserverslotbutton$serverdata = ((RealmsServerSlotButton)p_237795_2_).getState();
         if (realmsserverslotbutton$serverdata != null) {
            switch(realmsserverslotbutton$serverdata.action) {
            case NOTHING:
               break;
            case JOIN:
               this.joinRealm(this.serverData);
               break;
            case SWITCH_SLOT:
               if (realmsserverslotbutton$serverdata.minigame) {
                  this.switchToMinigame();
               } else if (realmsserverslotbutton$serverdata.empty) {
                  this.switchToEmptySlot(p_224402_1_, this.serverData);
               } else {
                  this.switchToFullSlot(p_224402_1_, this.serverData);
               }
               break;
            default:
               throw new IllegalStateException("Unknown action " + realmsserverslotbutton$serverdata.action);
            }
         }

      });
      this.addButton(realmsserverslotbutton);
   }

   private int leftButton(int p_224411_1_) {
      return this.leftX + p_224411_1_ * 95;
   }

   private int centerButton(int p_224374_1_, int p_224374_2_) {
      return this.width / 2 - (p_224374_2_ * 105 - 5) / 2 + p_224374_1_ * 105;
   }

   public void tick() {
      super.tick();
      ++this.animTick;
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.toolTip = null;
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, TITLE, this.width / 2, row(4), 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.serverData == null) {
         drawCenteredString(p_230430_1_, this.font, WORLD_TITLE, this.width / 2, 17, 16777215);
      } else {
         String s = this.serverData.getName();
         int i = this.font.width(s);
         int j = this.serverData.state == RealmsServer.Status.CLOSED ? 10526880 : 8388479;
         int k = this.font.width(WORLD_TITLE);
         drawCenteredString(p_230430_1_, this.font, WORLD_TITLE, this.width / 2, 12, 16777215);
         drawCenteredString(p_230430_1_, this.font, s, this.width / 2, 24, j);
         int l = Math.min(this.centerButton(2, 3) + 80 - 11, this.width / 2 + i / 2 + k / 2 + 10);
         this.drawServerStatus(p_230430_1_, l, 7, p_230430_2_, p_230430_3_);
         if (this.isMinigame()) {
            this.font.draw(p_230430_1_, MINIGAME_PREFIX.copy().append(this.serverData.getMinigameName()), (float)(this.leftX + 80 + 20 + 10), (float)row(13), 16777215);
         }

         if (this.toolTip != null) {
            this.renderMousehoverTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
         }

      }
   }

   private int frame(int p_224368_1_) {
      return this.leftX + (p_224368_1_ - 1) * 98;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.lastScreen.removeSelection();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   private void fetchServerData(long p_224387_1_) {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.create();

         try {
            this.serverData = realmsclient.getOwnWorld(p_224387_1_);
            this.disableButtons();
            if (this.isMinigame()) {
               this.show(this.switchMinigameButton);
            } else {
               this.show(this.optionsButton);
               this.show(this.backupButton);
               this.show(this.resetWorldButton);
            }
         } catch (RealmsServiceException realmsserviceexception) {
            LOGGER.error("Couldn't get own world");
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(new RealmsGenericErrorScreen(ITextComponent.nullToEmpty(realmsserviceexception.getMessage()), this.lastScreen));
            });
         }

      })).start();
   }

   private void disableButtons() {
      this.playersButton.active = !this.serverData.expired;
      this.settingsButton.active = !this.serverData.expired;
      this.subscriptionButton.active = true;
      this.switchMinigameButton.active = !this.serverData.expired;
      this.optionsButton.active = !this.serverData.expired;
      this.resetWorldButton.active = !this.serverData.expired;
   }

   private void joinRealm(RealmsServer p_224385_1_) {
      if (this.serverData.state == RealmsServer.Status.OPEN) {
         this.lastScreen.play(p_224385_1_, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      } else {
         this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      }

   }

   private void switchToMinigame() {
      RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.MINIGAME);
      realmsselectworldtemplatescreen.setTitle(new TranslationTextComponent("mco.template.title.minigame"));
      realmsselectworldtemplatescreen.setWarning(new TranslationTextComponent("mco.minigame.world.info.line1"), new TranslationTextComponent("mco.minigame.world.info.line2"));
      this.minecraft.setScreen(realmsselectworldtemplatescreen);
   }

   private void switchToFullSlot(int p_224403_1_, RealmsServer p_224403_2_) {
      ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.slot.switch.question.line1");
      ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.slot.switch.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_237805_3_) -> {
         if (p_237805_3_) {
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameRealmsAction(p_224403_2_.id, p_224403_1_, () -> {
               this.minecraft.setScreen(this.getNewScreen());
            })));
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
   }

   private void switchToEmptySlot(int p_224388_1_, RealmsServer p_224388_2_) {
      ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.slot.switch.question.line1");
      ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.slot.switch.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_237797_3_) -> {
         if (p_237797_3_) {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, p_224388_2_, new TranslationTextComponent("mco.configure.world.switch.slot"), new TranslationTextComponent("mco.configure.world.switch.slot.subtitle"), 10526880, DialogTexts.GUI_CANCEL, () -> {
               this.minecraft.setScreen(this.getNewScreen());
            }, () -> {
               this.minecraft.setScreen(this.getNewScreen());
            });
            realmsresetworldscreen.setSlot(p_224388_1_);
            realmsresetworldscreen.setResetTitle(new TranslationTextComponent("mco.create.world.reset.title"));
            this.minecraft.setScreen(realmsresetworldscreen);
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
   }

   protected void renderMousehoverTooltip(MatrixStack p_237796_1_, @Nullable ITextComponent p_237796_2_, int p_237796_3_, int p_237796_4_) {
      int i = p_237796_3_ + 12;
      int j = p_237796_4_ - 12;
      int k = this.font.width(p_237796_2_);
      if (i + k + 3 > this.rightX) {
         i = i - k - 20;
      }

      this.fillGradient(p_237796_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
      this.font.drawShadow(p_237796_1_, p_237796_2_, (float)i, (float)j, 16777215);
   }

   private void drawServerStatus(MatrixStack p_237807_1_, int p_237807_2_, int p_237807_3_, int p_237807_4_, int p_237807_5_) {
      if (this.serverData.expired) {
         this.drawExpired(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_);
      } else if (this.serverData.state == RealmsServer.Status.CLOSED) {
         this.drawClose(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_);
      } else if (this.serverData.state == RealmsServer.Status.OPEN) {
         if (this.serverData.daysLeft < 7) {
            this.drawExpiring(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_, this.serverData.daysLeft);
         } else {
            this.drawOpen(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_);
         }
      }

   }

   private void drawExpired(MatrixStack p_237809_1_, int p_237809_2_, int p_237809_3_, int p_237809_4_, int p_237809_5_) {
      this.minecraft.getTextureManager().bind(EXPIRED_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      AbstractGui.blit(p_237809_1_, p_237809_2_, p_237809_3_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_237809_4_ >= p_237809_2_ && p_237809_4_ <= p_237809_2_ + 9 && p_237809_5_ >= p_237809_3_ && p_237809_5_ <= p_237809_3_ + 27) {
         this.toolTip = SERVER_EXPIRED_TOOLTIP;
      }

   }

   private void drawExpiring(MatrixStack p_237804_1_, int p_237804_2_, int p_237804_3_, int p_237804_4_, int p_237804_5_, int p_237804_6_) {
      this.minecraft.getTextureManager().bind(EXPIRES_SOON_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.animTick % 20 < 10) {
         AbstractGui.blit(p_237804_1_, p_237804_2_, p_237804_3_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         AbstractGui.blit(p_237804_1_, p_237804_2_, p_237804_3_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      if (p_237804_4_ >= p_237804_2_ && p_237804_4_ <= p_237804_2_ + 9 && p_237804_5_ >= p_237804_3_ && p_237804_5_ <= p_237804_3_ + 27) {
         if (p_237804_6_ <= 0) {
            this.toolTip = SERVER_EXPIRING_SOON_TOOLTIP;
         } else if (p_237804_6_ == 1) {
            this.toolTip = SERVER_EXPIRING_IN_DAY_TOOLTIP;
         } else {
            this.toolTip = new TranslationTextComponent("mco.selectServer.expires.days", p_237804_6_);
         }
      }

   }

   private void drawOpen(MatrixStack p_237811_1_, int p_237811_2_, int p_237811_3_, int p_237811_4_, int p_237811_5_) {
      this.minecraft.getTextureManager().bind(ON_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      AbstractGui.blit(p_237811_1_, p_237811_2_, p_237811_3_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_237811_4_ >= p_237811_2_ && p_237811_4_ <= p_237811_2_ + 9 && p_237811_5_ >= p_237811_3_ && p_237811_5_ <= p_237811_3_ + 27) {
         this.toolTip = SERVER_OPEN_TOOLTIP;
      }

   }

   private void drawClose(MatrixStack p_237813_1_, int p_237813_2_, int p_237813_3_, int p_237813_4_, int p_237813_5_) {
      this.minecraft.getTextureManager().bind(OFF_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      AbstractGui.blit(p_237813_1_, p_237813_2_, p_237813_3_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_237813_4_ >= p_237813_2_ && p_237813_4_ <= p_237813_2_ + 9 && p_237813_5_ >= p_237813_3_ && p_237813_5_ <= p_237813_3_ + 27) {
         this.toolTip = SERVER_CLOSED_TOOLTIP;
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType == RealmsServer.ServerType.MINIGAME;
   }

   private void hideRegularButtons() {
      this.hide(this.optionsButton);
      this.hide(this.backupButton);
      this.hide(this.resetWorldButton);
   }

   private void hide(Button p_237799_1_) {
      p_237799_1_.visible = false;
      this.children.remove(p_237799_1_);
      this.buttons.remove(p_237799_1_);
   }

   private void show(Button p_237806_1_) {
      p_237806_1_.visible = true;
      this.addButton(p_237806_1_);
   }

   private void hideMinigameButtons() {
      this.hide(this.switchMinigameButton);
   }

   public void saveSlotSettings(RealmsWorldOptions p_224386_1_) {
      RealmsWorldOptions realmsworldoptions = this.serverData.slots.get(this.serverData.activeSlot);
      p_224386_1_.templateId = realmsworldoptions.templateId;
      p_224386_1_.templateImage = realmsworldoptions.templateImage;
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.updateSlot(this.serverData.id, this.serverData.activeSlot, p_224386_1_);
         this.serverData.slots.put(this.serverData.activeSlot, p_224386_1_);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't save slot settings");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void saveSettings(String p_224410_1_, String p_224410_2_) {
      String s = p_224410_2_.trim().isEmpty() ? null : p_224410_2_;
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.update(this.serverData.id, p_224410_1_, s);
         this.serverData.setName(p_224410_1_);
         this.serverData.setDescription(s);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't save settings");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void openTheWorld(boolean p_237802_1_, Screen p_237802_2_) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(p_237802_2_, new OpeningWorldRealmsAction(this.serverData, this, this.lastScreen, p_237802_1_)));
   }

   public void closeTheWorld(Screen p_237800_1_) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(p_237800_1_, new CloseRealmsAction(this.serverData, this)));
   }

   public void stateChanged() {
      this.stateChanged = true;
   }

   protected void callback(@Nullable WorldTemplate p_223627_1_) {
      if (p_223627_1_ != null) {
         if (WorldTemplate.Type.MINIGAME == p_223627_1_.type) {
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new StartMinigameRealmsAction(this.serverData.id, p_223627_1_, this.getNewScreen())));
         }

      }
   }

   public RealmsConfigureWorldScreen getNewScreen() {
      return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
   }
}
