package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CPlayerAbilitiesPacket implements IPacket<IServerPlayNetHandler> {
   private boolean isFlying;

   public CPlayerAbilitiesPacket() {
   }

   public CPlayerAbilitiesPacket(PlayerAbilities p_i46872_1_) {
      this.isFlying = p_i46872_1_.flying;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      byte b0 = p_148837_1_.readByte();
      this.isFlying = (b0 & 2) != 0;
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      byte b0 = 0;
      if (this.isFlying) {
         b0 = (byte)(b0 | 2);
      }

      p_148840_1_.writeByte(b0);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerAbilities(this);
   }

   public boolean isFlying() {
      return this.isFlying;
   }
}
