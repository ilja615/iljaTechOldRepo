package net.minecraft.network;

import java.io.IOException;

public interface IPacket<T extends INetHandler> {
   void read(PacketBuffer p_148837_1_) throws IOException;

   void write(PacketBuffer p_148840_1_) throws IOException;

   void handle(T p_148833_1_);

   default boolean isSkippable() {
      return false;
   }
}
