package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CConfirmTeleportPacket implements IPacket<IServerPlayNetHandler> {
   private int id;

   public CConfirmTeleportPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CConfirmTeleportPacket(int p_i46889_1_) {
      this.id = p_i46889_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAcceptTeleportPacket(this);
   }

   public int getId() {
      return this.id;
   }
}
