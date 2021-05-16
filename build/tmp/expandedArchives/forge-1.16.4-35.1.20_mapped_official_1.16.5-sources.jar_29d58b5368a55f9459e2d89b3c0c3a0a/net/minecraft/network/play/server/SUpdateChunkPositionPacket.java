package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateChunkPositionPacket implements IPacket<IClientPlayNetHandler> {
   private int x;
   private int z;

   public SUpdateChunkPositionPacket() {
   }

   public SUpdateChunkPositionPacket(int p_i50766_1_, int p_i50766_2_) {
      this.x = p_i50766_1_;
      this.z = p_i50766_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readVarInt();
      this.z = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.x);
      p_148840_1_.writeVarInt(this.z);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetChunkCacheCenter(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZ() {
      return this.z;
   }
}
