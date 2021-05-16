package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Monitor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class VirtualScreen implements AutoCloseable {
   private final Minecraft minecraft;
   private final MonitorHandler screenManager;

   public VirtualScreen(Minecraft p_i47668_1_) {
      this.minecraft = p_i47668_1_;
      this.screenManager = new MonitorHandler(Monitor::new);
   }

   public MainWindow newWindow(ScreenSize p_217626_1_, @Nullable String p_217626_2_, String p_217626_3_) {
      return new MainWindow(this.minecraft, this.screenManager, p_217626_1_, p_217626_2_, p_217626_3_);
   }

   public void close() {
      this.screenManager.shutdown();
   }
}
