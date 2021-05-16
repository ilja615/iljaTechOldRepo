package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CClientStatusPacket implements IPacket<IServerPlayNetHandler> {
   private CClientStatusPacket.State action;

   public CClientStatusPacket() {
   }

   public CClientStatusPacket(CClientStatusPacket.State p_i46886_1_) {
      this.action = p_i46886_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnum(CClientStatusPacket.State.class);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.action);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleClientCommand(this);
   }

   public CClientStatusPacket.State getAction() {
      return this.action;
   }

   public static enum State {
      PERFORM_RESPAWN,
      REQUEST_STATS;
   }
}
