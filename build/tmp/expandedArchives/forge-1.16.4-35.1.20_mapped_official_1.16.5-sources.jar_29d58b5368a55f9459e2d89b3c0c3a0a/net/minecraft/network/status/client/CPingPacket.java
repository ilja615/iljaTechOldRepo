package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.IServerStatusNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPingPacket implements IPacket<IServerStatusNetHandler> {
   private long time;

   public CPingPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPingPacket(long p_i46842_1_) {
      this.time = p_i46842_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.time = p_148837_1_.readLong();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.time);
   }

   public void handle(IServerStatusNetHandler p_148833_1_) {
      p_148833_1_.handlePingRequest(this);
   }

   public long getTime() {
      return this.time;
   }
}
