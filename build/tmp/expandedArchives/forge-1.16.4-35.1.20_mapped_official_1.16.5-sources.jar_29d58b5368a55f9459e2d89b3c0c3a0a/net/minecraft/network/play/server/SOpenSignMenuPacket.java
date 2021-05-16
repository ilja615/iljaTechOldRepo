package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenSignMenuPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos pos;

   public SOpenSignMenuPacket() {
   }

   public SOpenSignMenuPacket(BlockPos p_i46934_1_) {
      this.pos = p_i46934_1_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleOpenSignEditor(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
