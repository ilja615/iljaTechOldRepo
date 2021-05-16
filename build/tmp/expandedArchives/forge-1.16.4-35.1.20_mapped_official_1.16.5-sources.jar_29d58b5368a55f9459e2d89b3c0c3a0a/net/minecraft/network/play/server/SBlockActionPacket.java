package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SBlockActionPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos pos;
   private int b0;
   private int b1;
   private Block block;

   public SBlockActionPacket() {
   }

   public SBlockActionPacket(BlockPos p_i46966_1_, Block p_i46966_2_, int p_i46966_3_, int p_i46966_4_) {
      this.pos = p_i46966_1_;
      this.block = p_i46966_2_;
      this.b0 = p_i46966_3_;
      this.b1 = p_i46966_4_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.b0 = p_148837_1_.readUnsignedByte();
      this.b1 = p_148837_1_.readUnsignedByte();
      this.block = Registry.BLOCK.byId(p_148837_1_.readVarInt());
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeByte(this.b0);
      p_148840_1_.writeByte(this.b1);
      p_148840_1_.writeVarInt(Registry.BLOCK.getId(this.block));
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleBlockEvent(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @OnlyIn(Dist.CLIENT)
   public int getB0() {
      return this.b0;
   }

   @OnlyIn(Dist.CLIENT)
   public int getB1() {
      return this.b1;
   }

   @OnlyIn(Dist.CLIENT)
   public Block getBlock() {
      return this.block;
   }
}
