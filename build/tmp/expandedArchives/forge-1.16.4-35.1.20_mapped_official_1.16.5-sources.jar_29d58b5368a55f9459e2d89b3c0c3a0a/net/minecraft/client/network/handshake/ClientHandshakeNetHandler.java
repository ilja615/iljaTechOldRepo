package net.minecraft.client.network.handshake;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHandshakeNetHandler implements IHandshakeNetHandler {
   private final MinecraftServer server;
   private final NetworkManager connection;

   public ClientHandshakeNetHandler(MinecraftServer p_i45287_1_, NetworkManager p_i45287_2_) {
      this.server = p_i45287_1_;
      this.connection = p_i45287_2_;
   }

   public void handleIntention(CHandshakePacket p_147383_1_) {
      if (!net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerLogin(p_147383_1_, this.connection)) return;
      this.connection.setProtocol(p_147383_1_.getIntention());
      this.connection.setListener(new ServerLoginNetHandler(this.server, this.connection));
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
   }

   public NetworkManager getConnection() {
      return this.connection;
   }
}
