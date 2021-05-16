package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   private static final ResourceLocation NEWS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
   private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher();
   private volatile int numberOfPendingInvites;
   private static boolean checkedMcoAvailability;
   private static boolean trialAvailable;
   private static boolean validClient;
   private static boolean hasUnreadNews;

   public void init() {
      this.checkIfMcoEnabled();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
   }

   public void tick() {
      if ((!this.getRealmsNotificationsEnabled() || !this.inTitleScreen() || !validClient) && !REALMS_DATA_FETCHER.isStopped()) {
         REALMS_DATA_FETCHER.stop();
      } else if (validClient && this.getRealmsNotificationsEnabled()) {
         REALMS_DATA_FETCHER.initWithSpecificTaskList();
         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = REALMS_DATA_FETCHER.isTrialAvailable();
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
         }

         REALMS_DATA_FETCHER.markClean();
      }
   }

   private boolean getRealmsNotificationsEnabled() {
      return this.minecraft.options.realmsNotifications;
   }

   private boolean inTitleScreen() {
      return this.minecraft.screen instanceof MainMenuScreen;
   }

   private void checkIfMcoEnabled() {
      if (!checkedMcoAvailability) {
         checkedMcoAvailability = true;
         (new Thread("Realms Notification Availability checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.clientCompatible();
                  if (realmsclient$compatibleversionresponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                     return;
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  if (realmsserviceexception.httpResultCode != 401) {
                     RealmsNotificationsScreen.checkedMcoAvailability = false;
                  }

                  return;
               }

               RealmsNotificationsScreen.validClient = true;
            }
         }).start();
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      if (validClient) {
         this.drawIcons(p_230430_1_, p_230430_2_, p_230430_3_);
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private void drawIcons(MatrixStack p_237857_1_, int p_237857_2_, int p_237857_3_) {
      int i = this.numberOfPendingInvites;
      int j = 24;
      int k = this.height / 4 + 48;
      int l = this.width / 2 + 80;
      int i1 = k + 48 + 2;
      int j1 = 0;
      if (hasUnreadNews) {
         this.minecraft.getTextureManager().bind(NEWS_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.4F, 0.4F, 0.4F);
         AbstractGui.blit(p_237857_1_, (int)((double)(l + 2 - j1) * 2.5D), (int)((double)i1 * 2.5D), 0.0F, 0.0F, 40, 40, 40, 40);
         RenderSystem.popMatrix();
         j1 += 14;
      }

      if (i != 0) {
         this.minecraft.getTextureManager().bind(INVITE_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_237857_1_, l - j1, i1 - 6, 0.0F, 0.0F, 15, 25, 31, 25);
         j1 += 16;
      }

      if (trialAvailable) {
         this.minecraft.getTextureManager().bind(TRIAL_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int k1 = 0;
         if ((Util.getMillis() / 800L & 1L) == 1L) {
            k1 = 8;
         }

         AbstractGui.blit(p_237857_1_, l + 4 - j1, i1 + 4, 0.0F, (float)k1, 8, 8, 8, 16);
      }

   }

   public void removed() {
      REALMS_DATA_FETCHER.stop();
   }
}
