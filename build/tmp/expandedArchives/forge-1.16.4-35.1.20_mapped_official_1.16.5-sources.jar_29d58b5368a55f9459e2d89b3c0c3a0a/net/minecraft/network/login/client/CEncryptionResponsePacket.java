package net.minecraft.network.login.client;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEncryptionResponsePacket implements IPacket<IServerLoginNetHandler> {
   private byte[] keybytes = new byte[0];
   private byte[] nonce = new byte[0];

   public CEncryptionResponsePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEncryptionResponsePacket(SecretKey p_i46851_1_, PublicKey p_i46851_2_, byte[] p_i46851_3_) throws CryptException {
      this.keybytes = CryptManager.encryptUsingKey(p_i46851_2_, p_i46851_1_.getEncoded());
      this.nonce = CryptManager.encryptUsingKey(p_i46851_2_, p_i46851_3_);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.keybytes = p_148837_1_.readByteArray();
      this.nonce = p_148837_1_.readByteArray();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByteArray(this.keybytes);
      p_148840_1_.writeByteArray(this.nonce);
   }

   public void handle(IServerLoginNetHandler p_148833_1_) {
      p_148833_1_.handleKey(this);
   }

   public SecretKey getSecretKey(PrivateKey p_149300_1_) throws CryptException {
      return CryptManager.decryptByteToSecretKey(p_149300_1_, this.keybytes);
   }

   public byte[] getNonce(PrivateKey p_149299_1_) throws CryptException {
      return CryptManager.decryptUsingKey(p_149299_1_, this.nonce);
   }
}
