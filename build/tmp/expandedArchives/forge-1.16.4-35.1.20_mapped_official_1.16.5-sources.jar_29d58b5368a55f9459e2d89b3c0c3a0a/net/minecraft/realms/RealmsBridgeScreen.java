package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBridgeScreen extends RealmsScreen {
   private Screen previousScreen;

   public void switchToRealms(Screen p_231394_1_) {
      this.previousScreen = p_231394_1_;
      Minecraft.getInstance().setScreen(new RealmsMainScreen(this));
   }

   @Nullable
   public RealmsScreen getNotificationScreen(Screen p_239555_1_) {
      this.previousScreen = p_239555_1_;
      return new RealmsNotificationsScreen();
   }

   public void init() {
      Minecraft.getInstance().setScreen(this.previousScreen);
   }
}
