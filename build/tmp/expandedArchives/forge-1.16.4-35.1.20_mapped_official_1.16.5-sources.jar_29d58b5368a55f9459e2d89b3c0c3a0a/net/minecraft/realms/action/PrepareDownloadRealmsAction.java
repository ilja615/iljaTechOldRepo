package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PrepareDownloadRealmsAction extends LongRunningTask {
   private final long worldId;
   private final int slot;
   private final Screen lastScreen;
   private final String downloadName;

   public PrepareDownloadRealmsAction(long p_i232230_1_, int p_i232230_3_, String p_i232230_4_, Screen p_i232230_5_) {
      this.worldId = p_i232230_1_;
      this.slot = p_i232230_3_;
      this.lastScreen = p_i232230_5_;
      this.downloadName = p_i232230_4_;
   }

   public void run() {
      this.setTitle(new TranslationTextComponent("mco.download.preparing"));
      RealmsClient realmsclient = RealmsClient.create();
      int i = 0;

      while(i < 25) {
         try {
            if (this.aborted()) {
               return;
            }

            WorldDownload worlddownload = realmsclient.requestDownloadInfo(this.worldId, this.slot);
            pause(1);
            if (this.aborted()) {
               return;
            }

            setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, worlddownload, this.downloadName, (p_238115_0_) -> {
            }));
            return;
         } catch (RetryCallException retrycallexception) {
            if (this.aborted()) {
               return;
            }

            pause(retrycallexception.delaySeconds);
            ++i;
         } catch (RealmsServiceException realmsserviceexception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't download world data");
            setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this.lastScreen));
            return;
         } catch (Exception exception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't download world data", (Throwable)exception);
            this.error(exception.getLocalizedMessage());
            return;
         }
      }

   }
}
