package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public enum EmptyBlockReader implements IBlockReader {
   INSTANCE;

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      return null;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      return Fluids.EMPTY.defaultFluidState();
   }
}
