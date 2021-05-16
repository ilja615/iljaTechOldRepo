package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChangeBlockPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos pos;
   private BlockState blockState;

   public SChangeBlockPacket() {
   }

   public SChangeBlockPacket(BlockPos p_i242080_1_, BlockState p_i242080_2_) {
      this.pos = p_i242080_1_;
      this.blockState = p_i242080_2_;
   }

   public SChangeBlockPacket(IBlockReader p_i48982_1_, BlockPos p_i48982_2_) {
      this(p_i48982_2_, p_i48982_1_.getBlockState(p_i48982_2_));
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.blockState = Block.BLOCK_STATE_REGISTRY.byId(p_148837_1_.readVarInt());
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeVarInt(Block.getId(this.blockState));
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleBlockUpdate(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockState getBlockState() {
      return this.blockState;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
