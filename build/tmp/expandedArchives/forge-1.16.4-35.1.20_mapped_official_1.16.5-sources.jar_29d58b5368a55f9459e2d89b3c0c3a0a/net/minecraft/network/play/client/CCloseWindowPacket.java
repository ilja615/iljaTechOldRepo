package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCloseWindowPacket implements IPacket<IServerPlayNetHandler> {
   private int containerId;

   public CCloseWindowPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCloseWindowPacket(int p_i46881_1_) {
      this.containerId = p_i46881_1_;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerClose(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
   }
}
