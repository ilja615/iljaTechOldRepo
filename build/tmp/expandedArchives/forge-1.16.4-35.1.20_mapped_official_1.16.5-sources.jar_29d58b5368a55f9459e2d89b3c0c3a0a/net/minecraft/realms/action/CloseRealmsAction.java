package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloseRealmsAction extends LongRunningTask {
   private final RealmsServer serverData;
   private final RealmsConfigureWorldScreen configureScreen;

   public CloseRealmsAction(RealmsServer p_i232228_1_, RealmsConfigureWorldScreen p_i232228_2_) {
      this.serverData = p_i232228_1_;
      this.configureScreen = p_i232228_2_;
   }

   public void run() {
      this.setTitle(new TranslationTextComponent("mco.configure.world.closing"));
      RealmsClient realmsclient = RealmsClient.create();

      for(int i = 0; i < 25; ++i) {
         if (this.aborted()) {
            return;
         }

         try {
            boolean flag = realmsclient.close(this.serverData.id);
            if (flag) {
               this.configureScreen.stateChanged();
               this.serverData.state = RealmsServer.Status.CLOSED;
               setScreen(this.configureScreen);
               break;
            }
         } catch (RetryCallException retrycallexception) {
            if (this.aborted()) {
               return;
            }

            pause(retrycallexception.delaySeconds);
         } catch (Exception exception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Failed to close server", (Throwable)exception);
            this.error("Failed to close the server");
         }
      }

   }
}
