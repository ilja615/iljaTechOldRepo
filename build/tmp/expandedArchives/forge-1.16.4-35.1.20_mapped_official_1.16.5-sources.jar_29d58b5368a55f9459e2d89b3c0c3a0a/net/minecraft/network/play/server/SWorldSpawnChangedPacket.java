package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SWorldSpawnChangedPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos pos;
   private float angle;

   public SWorldSpawnChangedPacket() {
   }

   public SWorldSpawnChangedPacket(BlockPos p_i242086_1_, float p_i242086_2_) {
      this.pos = p_i242086_1_;
      this.angle = p_i242086_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetSpawn(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAngle() {
      return this.angle;
   }
}
