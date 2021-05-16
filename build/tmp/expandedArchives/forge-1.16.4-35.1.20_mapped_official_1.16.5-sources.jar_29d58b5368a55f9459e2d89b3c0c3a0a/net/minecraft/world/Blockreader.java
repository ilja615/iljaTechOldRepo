package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public final class Blockreader implements IBlockReader {
   private final BlockState[] column;

   public Blockreader(BlockState[] p_i231623_1_) {
      this.column = p_i231623_1_;
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      return null;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      int i = p_180495_1_.getY();
      return i >= 0 && i < this.column.length ? this.column[i] : Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      return this.getBlockState(p_204610_1_).getFluidState();
   }
}
