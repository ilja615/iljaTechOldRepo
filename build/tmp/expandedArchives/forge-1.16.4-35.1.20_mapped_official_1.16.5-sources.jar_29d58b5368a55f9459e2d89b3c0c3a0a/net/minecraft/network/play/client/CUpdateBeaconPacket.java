package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateBeaconPacket implements IPacket<IServerPlayNetHandler> {
   private int primary;
   private int secondary;

   public CUpdateBeaconPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateBeaconPacket(int p_i49544_1_, int p_i49544_2_) {
      this.primary = p_i49544_1_;
      this.secondary = p_i49544_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.primary = p_148837_1_.readVarInt();
      this.secondary = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.primary);
      p_148840_1_.writeVarInt(this.secondary);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetBeaconPacket(this);
   }

   public int getPrimary() {
      return this.primary;
   }

   public int getSecondary() {
      return this.secondary;
   }
}
