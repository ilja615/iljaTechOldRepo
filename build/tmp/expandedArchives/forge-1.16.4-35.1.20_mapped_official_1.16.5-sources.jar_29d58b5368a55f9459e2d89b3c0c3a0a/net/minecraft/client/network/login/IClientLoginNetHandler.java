package net.minecraft.client.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;

public interface IClientLoginNetHandler extends INetHandler {
   void handleHello(SEncryptionRequestPacket p_147389_1_);

   void handleGameProfile(SLoginSuccessPacket p_147390_1_);

   void handleDisconnect(SDisconnectLoginPacket p_147388_1_);

   void handleCompression(SEnableCompressionPacket p_180464_1_);

   void handleCustomQuery(SCustomPayloadLoginPacket p_209521_1_);
}
