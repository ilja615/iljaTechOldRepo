package net.minecraft.realms.action;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OpeningWorldRealmsAction extends LongRunningTask {
   private final RealmsServer serverData;
   private final Screen returnScreen;
   private final boolean join;
   private final RealmsMainScreen mainScreen;

   public OpeningWorldRealmsAction(RealmsServer p_i232232_1_, Screen p_i232232_2_, RealmsMainScreen p_i232232_3_, boolean p_i232232_4_) {
      this.serverData = p_i232232_1_;
      this.returnScreen = p_i232232_2_;
      this.join = p_i232232_4_;
      this.mainScreen = p_i232232_3_;
   }

   public void run() {
      this.setTitle(new TranslationTextComponent("mco.configure.world.opening"));
      RealmsClient realmsclient = RealmsClient.create();

      for(int i = 0; i < 25; ++i) {
         if (this.aborted()) {
            return;
         }

         try {
            boolean flag = realmsclient.open(this.serverData.id);
            if (flag) {
               if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                  ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
               }

               this.serverData.state = RealmsServer.Status.OPEN;
               if (this.join) {
                  this.mainScreen.play(this.serverData, this.returnScreen);
               } else {
                  setScreen(this.returnScreen);
               }
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

            LOGGER.error("Failed to open server", (Throwable)exception);
            this.error("Failed to open the server");
         }
      }

   }
}
