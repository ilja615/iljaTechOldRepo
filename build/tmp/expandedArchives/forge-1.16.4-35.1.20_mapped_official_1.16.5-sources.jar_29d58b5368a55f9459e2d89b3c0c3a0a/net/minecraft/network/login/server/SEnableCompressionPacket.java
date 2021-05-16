package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEnableCompressionPacket implements IPacket<IClientLoginNetHandler> {
   private int compressionThreshold;

   public SEnableCompressionPacket() {
   }

   public SEnableCompressionPacket(int p_i46854_1_) {
      this.compressionThreshold = p_i46854_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.compressionThreshold = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.compressionThreshold);
   }

   public void handle(IClientLoginNetHandler p_148833_1_) {
      p_148833_1_.handleCompression(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
