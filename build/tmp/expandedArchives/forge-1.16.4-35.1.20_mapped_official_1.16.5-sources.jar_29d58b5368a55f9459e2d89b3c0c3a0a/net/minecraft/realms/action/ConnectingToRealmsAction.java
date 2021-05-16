package net.minecraft.realms.action;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConnectingToRealmsAction extends LongRunningTask {
   private final RealmsServer server;
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   private final ReentrantLock connectLock;

   public ConnectingToRealmsAction(RealmsMainScreen p_i232231_1_, Screen p_i232231_2_, RealmsServer p_i232231_3_, ReentrantLock p_i232231_4_) {
      this.lastScreen = p_i232231_2_;
      this.mainScreen = p_i232231_1_;
      this.server = p_i232231_3_;
      this.connectLock = p_i232231_4_;
   }

   public void run() {
      this.setTitle(new TranslationTextComponent("mco.connect.connecting"));
      RealmsClient realmsclient = RealmsClient.create();
      boolean flag = false;
      boolean flag1 = false;
      int i = 5;
      RealmsServerAddress realmsserveraddress = null;
      boolean flag2 = false;
      boolean flag3 = false;

      for(int j = 0; j < 40 && !this.aborted(); ++j) {
         try {
            realmsserveraddress = realmsclient.join(this.server.id);
            flag = true;
         } catch (RetryCallException retrycallexception) {
            i = retrycallexception.delaySeconds;
         } catch (RealmsServiceException realmsserviceexception) {
            if (realmsserviceexception.errorCode == 6002) {
               flag2 = true;
            } else if (realmsserviceexception.errorCode == 6006) {
               flag3 = true;
            } else {
               flag1 = true;
               this.error(realmsserviceexception.toString());
               LOGGER.error("Couldn't connect to world", (Throwable)realmsserviceexception);
            }
            break;
         } catch (Exception exception) {
            flag1 = true;
            LOGGER.error("Couldn't connect to world", (Throwable)exception);
            this.error(exception.getLocalizedMessage());
            break;
         }

         if (flag) {
            break;
         }

         this.sleep(i);
      }

      if (flag2) {
         setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
      } else if (flag3) {
         if (this.server.ownerUUID.equals(Minecraft.getInstance().getUser().getUuid())) {
            setScreen(new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.ServerType.MINIGAME));
         } else {
            setScreen(new RealmsGenericErrorScreen(new TranslationTextComponent("mco.brokenworld.nonowner.title"), new TranslationTextComponent("mco.brokenworld.nonowner.error"), this.lastScreen));
         }
      } else if (!this.aborted() && !flag1) {
         if (flag) {
            RealmsServerAddress realmsserveraddress1 = realmsserveraddress;
            if (realmsserveraddress1.resourcePackUrl != null && realmsserveraddress1.resourcePackHash != null) {
               ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.resourcepack.question.line1");
               ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.resourcepack.question.line2");
               setScreen(new RealmsLongConfirmationScreen((p_238121_2_) -> {
                  try {
                     if (p_238121_2_) {
                        Function<Throwable, Void> function = (p_238122_1_) -> {
                           Minecraft.getInstance().getClientPackSource().clearServerPack();
                           LOGGER.error(p_238122_1_);
                           setScreen(new RealmsGenericErrorScreen(new StringTextComponent("Failed to download resource pack!"), this.lastScreen));
                           return null;
                        };

                        try {
                           Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(realmsserveraddress1.resourcePackUrl, realmsserveraddress1.resourcePackHash).thenRun(() -> {
                              this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectedToRealmsAction(this.lastScreen, this.server, realmsserveraddress1)));
                           }).exceptionally(function);
                        } catch (Exception exception1) {
                           function.apply(exception1);
                        }
                     } else {
                        setScreen(this.lastScreen);
                     }
                  } finally {
                     if (this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
                        this.connectLock.unlock();
                     }

                  }

               }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
            } else {
               this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectedToRealmsAction(this.lastScreen, this.server, realmsserveraddress1)));
            }
         } else {
            this.error(new TranslationTextComponent("mco.errorMessage.connectionFailure"));
         }
      }

   }

   private void sleep(int p_238123_1_) {
      try {
         Thread.sleep((long)(p_238123_1_ * 1000));
      } catch (InterruptedException interruptedexception) {
         LOGGER.warn(interruptedexception.getLocalizedMessage());
      }

   }
}
