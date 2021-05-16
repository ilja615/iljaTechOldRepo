package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEncryptionRequestPacket implements IPacket<IClientLoginNetHandler> {
   private String serverId;
   private byte[] publicKey;
   private byte[] nonce;

   public SEncryptionRequestPacket() {
   }

   public SEncryptionRequestPacket(String p_i244727_1_, byte[] p_i244727_2_, byte[] p_i244727_3_) {
      this.serverId = p_i244727_1_;
      this.publicKey = p_i244727_2_;
      this.nonce = p_i244727_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.serverId = p_148837_1_.readUtf(20);
      this.publicKey = p_148837_1_.readByteArray();
      this.nonce = p_148837_1_.readByteArray();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.serverId);
      p_148840_1_.writeByteArray(this.publicKey);
      p_148840_1_.writeByteArray(this.nonce);
   }

   public void handle(IClientLoginNetHandler p_148833_1_) {
      p_148833_1_.handleHello(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getServerId() {
      return this.serverId;
   }

   @OnlyIn(Dist.CLIENT)
   public PublicKey getPublicKey() throws CryptException {
      return CryptManager.byteToPublicKey(this.publicKey);
   }

   @OnlyIn(Dist.CLIENT)
   public byte[] getNonce() {
      return this.nonce;
   }
}
