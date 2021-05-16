package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SHeldItemChangePacket implements IPacket<IClientPlayNetHandler> {
   private int slot;

   public SHeldItemChangePacket() {
   }

   public SHeldItemChangePacket(int p_i46919_1_) {
      this.slot = p_i46919_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.slot = p_148837_1_.readByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.slot);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetCarriedItem(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlot() {
      return this.slot;
   }
}
