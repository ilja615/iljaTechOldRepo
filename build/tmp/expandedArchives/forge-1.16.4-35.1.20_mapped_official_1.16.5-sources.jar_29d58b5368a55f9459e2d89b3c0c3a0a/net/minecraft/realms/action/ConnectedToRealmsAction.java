package net.minecraft.realms.action;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConnectedToRealmsAction extends LongRunningTask {
   private final RealmsConnect realmsConnect;
   private final RealmsServer server;
   private final RealmsServerAddress address;

   public ConnectedToRealmsAction(Screen p_i244788_1_, RealmsServer p_i244788_2_, RealmsServerAddress p_i244788_3_) {
      this.server = p_i244788_2_;
      this.address = p_i244788_3_;
      this.realmsConnect = new RealmsConnect(p_i244788_1_);
   }

   public void run() {
      this.setTitle(new TranslationTextComponent("mco.connect.connecting"));
      net.minecraft.realms.RealmsServerAddress realmsserveraddress = net.minecraft.realms.RealmsServerAddress.parseString(this.address.address);
      this.realmsConnect.connect(this.server, realmsserveraddress.getHost(), realmsserveraddress.getPort());
   }

   public void abortTask() {
      this.realmsConnect.abort();
      Minecraft.getInstance().getClientPackSource().clearServerPack();
   }

   public void tick() {
      this.realmsConnect.tick();
   }
}
