package net.minecraft.client.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;

public interface IClientStatusNetHandler extends INetHandler {
   void handleStatusResponse(SServerInfoPacket p_147397_1_);

   void handlePongResponse(SPongPacket p_147398_1_);
}
