package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResetWorldRealmsAction extends LongRunningTask {
   private final String seed;
   private final WorldTemplate worldTemplate;
   private final int levelType;
   private final boolean generateStructures;
   private final long serverId;
   private ITextComponent title = new TranslationTextComponent("mco.reset.world.resetting.screen.title");
   private final Runnable callback;

   public ResetWorldRealmsAction(@Nullable String p_i242048_1_, @Nullable WorldTemplate p_i242048_2_, int p_i242048_3_, boolean p_i242048_4_, long p_i242048_5_, @Nullable ITextComponent p_i242048_7_, Runnable p_i242048_8_) {
      this.seed = p_i242048_1_;
      this.worldTemplate = p_i242048_2_;
      this.levelType = p_i242048_3_;
      this.generateStructures = p_i242048_4_;
      this.serverId = p_i242048_5_;
      if (p_i242048_7_ != null) {
         this.title = p_i242048_7_;
      }

      this.callback = p_i242048_8_;
   }

   public void run() {
      RealmsClient realmsclient = RealmsClient.create();
      this.setTitle(this.title);
      int i = 0;

      while(i < 25) {
         try {
            if (this.aborted()) {
               return;
            }

            if (this.worldTemplate != null) {
               realmsclient.resetWorldWithTemplate(this.serverId, this.worldTemplate.id);
            } else {
               realmsclient.resetWorldWithSeed(this.serverId, this.seed, this.levelType, this.generateStructures);
            }

            if (this.aborted()) {
               return;
            }

            this.callback.run();
            return;
         } catch (RetryCallException retrycallexception) {
            if (this.aborted()) {
               return;
            }

            pause(retrycallexception.delaySeconds);
            ++i;
         } catch (Exception exception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't reset world");
            this.error(exception.toString());
            return;
         }
      }

   }
}
