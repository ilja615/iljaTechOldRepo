package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;

public class CPlayerTryUseItemPacket implements IPacket<IServerPlayNetHandler> {
   private Hand hand;

   public CPlayerTryUseItemPacket() {
   }

   public CPlayerTryUseItemPacket(Hand p_i46857_1_) {
      this.hand = p_i46857_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.hand = p_148837_1_.readEnum(Hand.class);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.hand);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUseItem(this);
   }

   public Hand getHand() {
      return this.hand;
   }
}
