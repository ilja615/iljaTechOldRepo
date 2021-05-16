package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSendResourcePackPacket implements IPacket<IClientPlayNetHandler> {
   private String url;
   private String hash;

   public SSendResourcePackPacket() {
   }

   public SSendResourcePackPacket(String p_i46924_1_, String p_i46924_2_) {
      this.url = p_i46924_1_;
      this.hash = p_i46924_2_;
      if (p_i46924_2_.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + p_i46924_2_.length() + ")");
      }
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.url = p_148837_1_.readUtf(32767);
      this.hash = p_148837_1_.readUtf(40);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.url);
      p_148840_1_.writeUtf(this.hash);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleResourcePack(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getUrl() {
      return this.url;
   }

   @OnlyIn(Dist.CLIENT)
   public String getHash() {
      return this.hash;
   }
}
