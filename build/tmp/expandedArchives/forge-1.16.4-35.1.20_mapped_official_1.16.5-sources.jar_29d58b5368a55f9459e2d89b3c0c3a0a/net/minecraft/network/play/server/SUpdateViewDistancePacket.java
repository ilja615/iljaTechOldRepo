package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateViewDistancePacket implements IPacket<IClientPlayNetHandler> {
   private int radius;

   public SUpdateViewDistancePacket() {
   }

   public SUpdateViewDistancePacket(int p_i50765_1_) {
      this.radius = p_i50765_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.radius = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.radius);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetChunkCacheRadius(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getRadius() {
      return this.radius;
   }
}
