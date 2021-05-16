package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateWorldRealmsAction extends LongRunningTask {
   private final String name;
   private final String motd;
   private final long worldId;
   private final Screen lastScreen;

   public CreateWorldRealmsAction(long p_i232237_1_, String p_i232237_3_, String p_i232237_4_, Screen p_i232237_5_) {
      this.worldId = p_i232237_1_;
      this.name = p_i232237_3_;
      this.motd = p_i232237_4_;
      this.lastScreen = p_i232237_5_;
   }

   public void run() {
      this.setTitle(new TranslationTextComponent("mco.create.world.wait"));
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.initializeWorld(this.worldId, this.name, this.motd);
         setScreen(this.lastScreen);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't create world");
         this.error(realmsserviceexception.toString());
      } catch (Exception exception) {
         LOGGER.error("Could not create world");
         this.error(exception.getLocalizedMessage());
      }

   }
}
