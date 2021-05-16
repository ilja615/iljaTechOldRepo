package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;

public interface IServerLoginNetHandler extends INetHandler {
   void handleHello(CLoginStartPacket p_147316_1_);

   void handleKey(CEncryptionResponsePacket p_147315_1_);

   void handleCustomQueryPacket(CCustomPayloadLoginPacket p_209526_1_);
}
