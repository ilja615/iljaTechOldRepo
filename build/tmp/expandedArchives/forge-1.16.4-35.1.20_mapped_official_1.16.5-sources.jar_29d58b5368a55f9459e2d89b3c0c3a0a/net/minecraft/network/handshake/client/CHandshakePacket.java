package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CHandshakePacket implements IPacket<IHandshakeNetHandler> {
   private int protocolVersion;
   private String hostName;
   private int port;
   private ProtocolType intention;
   private String fmlVersion = net.minecraftforge.fml.network.FMLNetworkConstants.NETVERSION;

   public CHandshakePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CHandshakePacket(String p_i47613_1_, int p_i47613_2_, ProtocolType p_i47613_3_) {
      this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
      this.hostName = p_i47613_1_;
      this.port = p_i47613_2_;
      this.intention = p_i47613_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.protocolVersion = p_148837_1_.readVarInt();
      this.hostName = p_148837_1_.readUtf(255);
      this.port = p_148837_1_.readUnsignedShort();
      this.intention = ProtocolType.getById(p_148837_1_.readVarInt());
      this.fmlVersion = net.minecraftforge.fml.network.NetworkHooks.getFMLVersion(this.hostName);
      this.hostName = this.hostName.split("\0")[0];
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.protocolVersion);
      p_148840_1_.writeUtf(this.hostName + "\0"+net.minecraftforge.fml.network.FMLNetworkConstants.NETVERSION+"\0");
      p_148840_1_.writeShort(this.port);
      p_148840_1_.writeVarInt(this.intention.getId());
   }

   public void handle(IHandshakeNetHandler p_148833_1_) {
      p_148833_1_.handleIntention(this);
   }

   public ProtocolType getIntention() {
      return this.intention;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public String getFMLVersion() {
      return this.fmlVersion;
   }
}
