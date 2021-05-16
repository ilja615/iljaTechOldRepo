package net.minecraft.network.status;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerStatusNetHandler implements IServerStatusNetHandler {
   private static final ITextComponent DISCONNECT_REASON = new TranslationTextComponent("multiplayer.status.request_handled");
   private final MinecraftServer server;
   private final NetworkManager connection;
   private boolean hasRequestedStatus;

   public ServerStatusNetHandler(MinecraftServer p_i45299_1_, NetworkManager p_i45299_2_) {
      this.server = p_i45299_1_;
      this.connection = p_i45299_2_;
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
   }

   public NetworkManager getConnection() {
      return this.connection;
   }

   public void handleStatusRequest(CServerQueryPacket p_147312_1_) {
      if (this.hasRequestedStatus) {
         this.connection.disconnect(DISCONNECT_REASON);
      } else {
         this.hasRequestedStatus = true;
         this.connection.send(new SServerInfoPacket(this.server.getStatus()));
      }
   }

   public void handlePingRequest(CPingPacket p_147311_1_) {
      this.connection.send(new SPongPacket(p_147311_1_.getTime()));
      this.connection.disconnect(DISCONNECT_REASON);
   }
}
