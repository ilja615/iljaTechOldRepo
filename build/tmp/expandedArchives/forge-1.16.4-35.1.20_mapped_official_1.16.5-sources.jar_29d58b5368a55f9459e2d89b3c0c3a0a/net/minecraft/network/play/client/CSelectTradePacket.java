package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CSelectTradePacket implements IPacket<IServerPlayNetHandler> {
   private int item;

   public CSelectTradePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CSelectTradePacket(int p_i49545_1_) {
      this.item = p_i49545_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.item = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.item);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSelectTrade(this);
   }

   public int getItem() {
      return this.item;
   }
}
