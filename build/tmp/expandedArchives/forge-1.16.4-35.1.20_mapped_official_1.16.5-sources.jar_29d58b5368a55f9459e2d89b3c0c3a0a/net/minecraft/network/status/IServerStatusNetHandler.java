package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;

public interface IServerStatusNetHandler extends INetHandler {
   void handlePingRequest(CPingPacket p_147311_1_);

   void handleStatusRequest(CServerQueryPacket p_147312_1_);
}
