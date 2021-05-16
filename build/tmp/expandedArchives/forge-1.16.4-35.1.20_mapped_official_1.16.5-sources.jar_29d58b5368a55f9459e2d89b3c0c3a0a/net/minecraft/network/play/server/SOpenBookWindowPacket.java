package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenBookWindowPacket implements IPacket<IClientPlayNetHandler> {
   private Hand hand;

   public SOpenBookWindowPacket() {
   }

   public SOpenBookWindowPacket(Hand p_i50770_1_) {
      this.hand = p_i50770_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.hand = p_148837_1_.readEnum(Hand.class);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.hand);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleOpenBook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Hand getHand() {
      return this.hand;
   }
}
