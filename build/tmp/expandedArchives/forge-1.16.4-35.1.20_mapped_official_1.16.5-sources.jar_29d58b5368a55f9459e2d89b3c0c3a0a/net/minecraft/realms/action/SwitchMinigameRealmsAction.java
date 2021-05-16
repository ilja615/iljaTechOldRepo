package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SwitchMinigameRealmsAction extends LongRunningTask {
   private final long worldId;
   private final int slot;
   private final Runnable callback;

   public SwitchMinigameRealmsAction(long p_i232236_1_, int p_i232236_3_, Runnable p_i232236_4_) {
      this.worldId = p_i232236_1_;
      this.slot = p_i232236_3_;
      this.callback = p_i232236_4_;
   }

   public void run() {
      RealmsClient realmsclient = RealmsClient.create();
      this.setTitle(new TranslationTextComponent("mco.minigame.world.slot.screen.title"));

      for(int i = 0; i < 25; ++i) {
         try {
            if (this.aborted()) {
               return;
            }

            if (realmsclient.switchSlot(this.worldId, this.slot)) {
               this.callback.run();
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

            LOGGER.error("Couldn't switch world!");
            this.error(exception.toString());
         }
      }

   }
}
