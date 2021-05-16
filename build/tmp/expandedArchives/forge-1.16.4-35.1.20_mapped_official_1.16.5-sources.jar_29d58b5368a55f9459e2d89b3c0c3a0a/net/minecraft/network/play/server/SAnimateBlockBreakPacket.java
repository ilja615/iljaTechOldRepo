package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SAnimateBlockBreakPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private BlockPos pos;
   private int progress;

   public SAnimateBlockBreakPacket() {
   }

   public SAnimateBlockBreakPacket(int p_i46968_1_, BlockPos p_i46968_2_, int p_i46968_3_) {
      this.id = p_i46968_1_;
      this.pos = p_i46968_2_;
      this.progress = p_i46968_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.pos = p_148837_1_.readBlockPos();
      this.progress = p_148837_1_.readUnsignedByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeByte(this.progress);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleBlockDestruction(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @OnlyIn(Dist.CLIENT)
   public int getProgress() {
      return this.progress;
   }
}
