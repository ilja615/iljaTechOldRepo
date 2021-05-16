package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.IServerStatusNetHandler;

public class CServerQueryPacket implements IPacket<IServerStatusNetHandler> {
   public void read(PacketBuffer p_148837_1_) throws IOException {
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
   }

   public void handle(IServerStatusNetHandler p_148833_1_) {
      p_148833_1_.handleStatusRequest(this);
   }
}
