package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsServerSlotButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.action.OpeningWorldRealmsAction;
import net.minecraft.realms.action.SwitchMinigameRealmsAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   private RealmsServer serverData;
   private final long serverId;
   private final ITextComponent header;
   private final ITextComponent[] message = new ITextComponent[]{new TranslationTextComponent("mco.brokenworld.message.line1"), new TranslationTextComponent("mco.brokenworld.message.line2")};
   private int leftX;
   private int rightX;
   private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
   private int animTick;

   public RealmsBrokenWorldScreen(Screen p_i232200_1_, RealmsMainScreen p_i232200_2_, long p_i232200_3_, boolean p_i232200_5_) {
      this.lastScreen = p_i232200_1_;
      this.mainScreen = p_i232200_2_;
      this.serverId = p_i232200_3_;
      this.header = p_i232200_5_ ? new TranslationTextComponent("mco.brokenworld.minigame.title") : new TranslationTextComponent("mco.brokenworld.title");
   }

   public void init() {
      this.leftX = this.width / 2 - 150;
      this.rightX = this.width / 2 + 190;
      this.addButton(new Button(this.rightX - 80 + 8, row(13) - 5, 70, 20, DialogTexts.GUI_BACK, (p_237776_1_) -> {
         this.backButtonClicked();
      }));
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      } else {
         this.addButtons();
      }

      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      RealmsNarratorHelper.now(Stream.concat(Stream.of(this.header), Stream.of(this.message)).map(ITextComponent::getString).collect(Collectors.joining(" ")));
   }

   private void addButtons() {
      for(Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
         int i = entry.getKey();
         boolean flag = i != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.ServerType.MINIGAME;
         Button button;
         if (flag) {
            button = new Button(this.getFramePositionX(i), row(8), 80, 20, new TranslationTextComponent("mco.brokenworld.play"), (p_237780_2_) -> {
               if ((this.serverData.slots.get(i)).empty) {
                  RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, this.serverData, new TranslationTextComponent("mco.configure.world.switch.slot"), new TranslationTextComponent("mco.configure.world.switch.slot.subtitle"), 10526880, DialogTexts.GUI_CANCEL, this::doSwitchOrReset, () -> {
                     this.minecraft.setScreen(this);
                     this.doSwitchOrReset();
                  });
                  realmsresetworldscreen.setSlot(i);
                  realmsresetworldscreen.setResetTitle(new TranslationTextComponent("mco.create.world.reset.title"));
                  this.minecraft.setScreen(realmsresetworldscreen);
               } else {
                  this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameRealmsAction(this.serverData.id, i, this::doSwitchOrReset)));
               }

            });
         } else {
            button = new Button(this.getFramePositionX(i), row(8), 80, 20, new TranslationTextComponent("mco.brokenworld.download"), (p_237777_2_) -> {
               ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.restore.download.question.line1");
               ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.restore.download.question.line2");
               this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_237778_2_) -> {
                  if (p_237778_2_) {
                     this.downloadWorld(i);
                  } else {
                     this.minecraft.setScreen(this);
                  }

               }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
            });
         }

         if (this.slotsThatHasBeenDownloaded.contains(i)) {
            button.active = false;
            button.setMessage(new TranslationTextComponent("mco.brokenworld.downloaded"));
         }

         this.addButton(button);
         this.addButton(new Button(this.getFramePositionX(i), row(10), 80, 20, new TranslationTextComponent("mco.brokenworld.reset"), (p_237773_2_) -> {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, this.serverData, this::doSwitchOrReset, () -> {
               this.minecraft.setScreen(this);
               this.doSwitchOrReset();
            });
            if (i != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.ServerType.MINIGAME) {
               realmsresetworldscreen.setSlot(i);
            }

            this.minecraft.setScreen(realmsresetworldscreen);
         }));
      }

   }

   public void tick() {
      ++this.animTick;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.header, this.width / 2, 17, 16777215);

      for(int i = 0; i < this.message.length; ++i) {
         drawCenteredString(p_230430_1_, this.font, this.message[i], this.width / 2, row(-1) + 3 + i * 12, 10526880);
      }

      if (this.serverData != null) {
         for(Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
            if ((entry.getValue()).templateImage != null && (entry.getValue()).templateId != -1L) {
               this.drawSlotFrame(p_230430_1_, this.getFramePositionX(entry.getKey()), row(1) + 5, p_230430_2_, p_230430_3_, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), (entry.getValue()).templateId, (entry.getValue()).templateImage, (entry.getValue()).empty);
            } else {
               this.drawSlotFrame(p_230430_1_, this.getFramePositionX(entry.getKey()), row(1) + 5, p_230430_2_, p_230430_3_, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, (String)null, (entry.getValue()).empty);
            }
         }

      }
   }

   private int getFramePositionX(int p_224065_1_) {
      return this.leftX + (p_224065_1_ - 1) * 110;
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
      this.minecraft.setScreen(this.lastScreen);
   }

   private void fetchServerData(long p_224068_1_) {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.create();

         try {
            this.serverData = realmsclient.getOwnWorld(p_224068_1_);
            this.addButtons();
         } catch (RealmsServiceException realmsserviceexception) {
            LOGGER.error("Couldn't get own world");
            this.minecraft.setScreen(new RealmsGenericErrorScreen(ITextComponent.nullToEmpty(realmsserviceexception.getMessage()), this.lastScreen));
         }

      })).start();
   }

   public void doSwitchOrReset() {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.create();
         if (this.serverData.state == RealmsServer.Status.CLOSED) {
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpeningWorldRealmsAction(this.serverData, this, this.mainScreen, true)));
            });
         } else {
            try {
               this.mainScreen.newScreen().play(realmsclient.getOwnWorld(this.serverId), this);
            } catch (RealmsServiceException realmsserviceexception) {
               LOGGER.error("Couldn't get own world");
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(this.lastScreen);
               });
            }
         }

      })).start();
   }

   private void downloadWorld(int p_224066_1_) {
      RealmsClient realmsclient = RealmsClient.create();

      try {
         WorldDownload worlddownload = realmsclient.requestDownloadInfo(this.serverData.id, p_224066_1_);
         RealmsDownloadLatestWorldScreen realmsdownloadlatestworldscreen = new RealmsDownloadLatestWorldScreen(this, worlddownload, this.serverData.getWorldName(p_224066_1_), (p_237774_2_) -> {
            if (p_237774_2_) {
               this.slotsThatHasBeenDownloaded.add(p_224066_1_);
               this.children.clear();
               this.addButtons();
            } else {
               this.minecraft.setScreen(this);
            }

         });
         this.minecraft.setScreen(realmsdownloadlatestworldscreen);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't download world data");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType == RealmsServer.ServerType.MINIGAME;
   }

   private void drawSlotFrame(MatrixStack p_237775_1_, int p_237775_2_, int p_237775_3_, int p_237775_4_, int p_237775_5_, boolean p_237775_6_, String p_237775_7_, int p_237775_8_, long p_237775_9_, String p_237775_11_, boolean p_237775_12_) {
      if (p_237775_12_) {
         this.minecraft.getTextureManager().bind(RealmsServerSlotButton.EMPTY_SLOT_LOCATION);
      } else if (p_237775_11_ != null && p_237775_9_ != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(p_237775_9_), p_237775_11_);
      } else if (p_237775_8_ == 1) {
         this.minecraft.getTextureManager().bind(RealmsServerSlotButton.DEFAULT_WORLD_SLOT_1);
      } else if (p_237775_8_ == 2) {
         this.minecraft.getTextureManager().bind(RealmsServerSlotButton.DEFAULT_WORLD_SLOT_2);
      } else if (p_237775_8_ == 3) {
         this.minecraft.getTextureManager().bind(RealmsServerSlotButton.DEFAULT_WORLD_SLOT_3);
      } else {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
      }

      if (!p_237775_6_) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else if (p_237775_6_) {
         float f = 0.9F + 0.1F * MathHelper.cos((float)this.animTick * 0.2F);
         RenderSystem.color4f(f, f, f, 1.0F);
      }

      AbstractGui.blit(p_237775_1_, p_237775_2_ + 3, p_237775_3_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      this.minecraft.getTextureManager().bind(RealmsServerSlotButton.SLOT_FRAME_LOCATION);
      if (p_237775_6_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      AbstractGui.blit(p_237775_1_, p_237775_2_, p_237775_3_, 0.0F, 0.0F, 80, 80, 80, 80);
      drawCenteredString(p_237775_1_, this.font, p_237775_7_, p_237775_2_ + 40, p_237775_3_ + 66, 16777215);
   }
}
