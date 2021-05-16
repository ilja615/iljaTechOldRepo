package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SCloseWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;

   public SCloseWindowPacket() {
   }

   public SCloseWindowPacket(int p_i46957_1_) {
      this.containerId = p_i46957_1_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerClose(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readUnsignedByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
   }
}
