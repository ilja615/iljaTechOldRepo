package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SKeepAlivePacket implements IPacket<IClientPlayNetHandler> {
   private long id;

   public SKeepAlivePacket() {
   }

   public SKeepAlivePacket(long p_i46942_1_) {
      this.id = p_i46942_1_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleKeepAlive(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readLong();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.id);
   }

   @OnlyIn(Dist.CLIENT)
   public long getId() {
      return this.id;
   }
}
