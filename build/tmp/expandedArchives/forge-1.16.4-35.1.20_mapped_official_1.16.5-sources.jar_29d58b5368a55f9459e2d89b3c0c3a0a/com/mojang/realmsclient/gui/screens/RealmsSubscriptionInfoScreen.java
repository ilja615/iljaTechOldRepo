package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSubscriptionInfoScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ITextComponent SUBSCRIPTION_TITLE = new TranslationTextComponent("mco.configure.world.subscription.title");
   private static final ITextComponent SUBSCRIPTION_START_LABEL = new TranslationTextComponent("mco.configure.world.subscription.start");
   private static final ITextComponent TIME_LEFT_LABEL = new TranslationTextComponent("mco.configure.world.subscription.timeleft");
   private static final ITextComponent DAYS_LEFT_LABEL = new TranslationTextComponent("mco.configure.world.subscription.recurring.daysleft");
   private static final ITextComponent SUBSCRIPTION_EXPIRED_TEXT = new TranslationTextComponent("mco.configure.world.subscription.expired");
   private static final ITextComponent SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = new TranslationTextComponent("mco.configure.world.subscription.less_than_a_day");
   private static final ITextComponent MONTH_SUFFIX = new TranslationTextComponent("mco.configure.world.subscription.month");
   private static final ITextComponent MONTHS_SUFFIX = new TranslationTextComponent("mco.configure.world.subscription.months");
   private static final ITextComponent DAY_SUFFIX = new TranslationTextComponent("mco.configure.world.subscription.day");
   private static final ITextComponent DAYS_SUFFIX = new TranslationTextComponent("mco.configure.world.subscription.days");
   private final Screen lastScreen;
   private final RealmsServer serverData;
   private final Screen mainScreen;
   private ITextComponent daysLeft;
   private String startDate;
   private Subscription.Type type;

   public RealmsSubscriptionInfoScreen(Screen p_i232223_1_, RealmsServer p_i232223_2_, Screen p_i232223_3_) {
      this.lastScreen = p_i232223_1_;
      this.serverData = p_i232223_2_;
      this.mainScreen = p_i232223_3_;
   }

   public void init() {
      this.getSubscription(this.serverData.id);
      RealmsNarratorHelper.now(SUBSCRIPTION_TITLE.getString(), SUBSCRIPTION_START_LABEL.getString(), this.startDate, TIME_LEFT_LABEL.getString(), this.daysLeft.getString());
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addButton(new Button(this.width / 2 - 100, row(6), 200, 20, new TranslationTextComponent("mco.configure.world.subscription.extend"), (p_238073_1_) -> {
         String s = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + this.serverData.remoteSubscriptionId + "&profileId=" + this.minecraft.getUser().getUuid();
         this.minecraft.keyboardHandler.setClipboard(s);
         Util.getPlatform().openUri(s);
      }));
      this.addButton(new Button(this.width / 2 - 100, row(12), 200, 20, DialogTexts.GUI_BACK, (p_238071_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      if (this.serverData.expired) {
         this.addButton(new Button(this.width / 2 - 100, row(10), 200, 20, new TranslationTextComponent("mco.configure.world.delete.button"), (p_238069_1_) -> {
            ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.delete.question.line1");
            ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.delete.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::deleteRealm, RealmsLongConfirmationScreen.Type.Warning, itextcomponent, itextcomponent1, true));
         }));
      }

   }

   private void deleteRealm(boolean p_238074_1_) {
      if (p_238074_1_) {
         (new Thread("Realms-delete-realm") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
                  RealmsSubscriptionInfoScreen.LOGGER.error(realmsserviceexception);
               }

               RealmsSubscriptionInfoScreen.this.minecraft.execute(() -> {
                  RealmsSubscriptionInfoScreen.this.minecraft.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen);
               });
            }
         }).start();
      }

      this.minecraft.setScreen(this);
   }

   private void getSubscription(long p_224573_1_) {
      RealmsClient realmsclient = RealmsClient.create();

      try {
         Subscription subscription = realmsclient.subscriptionFor(p_224573_1_);
         this.daysLeft = this.daysLeftPresentation(subscription.daysLeft);
         this.startDate = localPresentation(subscription.startDate);
         this.type = subscription.type;
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't get subscription");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this.lastScreen));
      }

   }

   private static String localPresentation(long p_224574_0_) {
      Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
      calendar.setTimeInMillis(p_224574_0_);
      return DateFormat.getDateTimeInstance().format(calendar.getTime());
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
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
      int i = this.width / 2 - 100;
      drawCenteredString(p_230430_1_, this.font, SUBSCRIPTION_TITLE, this.width / 2, 17, 16777215);
      this.font.draw(p_230430_1_, SUBSCRIPTION_START_LABEL, (float)i, (float)row(0), 10526880);
      this.font.draw(p_230430_1_, this.startDate, (float)i, (float)row(1), 16777215);
      if (this.type == Subscription.Type.NORMAL) {
         this.font.draw(p_230430_1_, TIME_LEFT_LABEL, (float)i, (float)row(3), 10526880);
      } else if (this.type == Subscription.Type.RECURRING) {
         this.font.draw(p_230430_1_, DAYS_LEFT_LABEL, (float)i, (float)row(3), 10526880);
      }

      this.font.draw(p_230430_1_, this.daysLeft, (float)i, (float)row(4), 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private ITextComponent daysLeftPresentation(int p_224576_1_) {
      if (p_224576_1_ < 0 && this.serverData.expired) {
         return SUBSCRIPTION_EXPIRED_TEXT;
      } else if (p_224576_1_ <= 1) {
         return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
      } else {
         int i = p_224576_1_ / 30;
         int j = p_224576_1_ % 30;
         IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("");
         if (i > 0) {
            iformattabletextcomponent.append(Integer.toString(i)).append(" ");
            if (i == 1) {
               iformattabletextcomponent.append(MONTH_SUFFIX);
            } else {
               iformattabletextcomponent.append(MONTHS_SUFFIX);
            }
         }

         if (j > 0) {
            if (i > 0) {
               iformattabletextcomponent.append(", ");
            }

            iformattabletextcomponent.append(Integer.toString(j)).append(" ");
            if (j == 1) {
               iformattabletextcomponent.append(DAY_SUFFIX);
            } else {
               iformattabletextcomponent.append(DAYS_SUFFIX);
            }
         }

         return iformattabletextcomponent;
      }
   }
}
