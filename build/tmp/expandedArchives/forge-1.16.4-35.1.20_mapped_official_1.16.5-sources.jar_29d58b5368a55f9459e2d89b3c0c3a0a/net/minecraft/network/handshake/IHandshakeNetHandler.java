package net.minecraft.network.handshake;

import net.minecraft.network.INetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;

public interface IHandshakeNetHandler extends INetHandler {
   void handleIntention(CHandshakePacket p_147383_1_);
}
