package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public interface IWaterLoggable extends IBucketPickupHandler, ILiquidContainer {
   default boolean canPlaceLiquid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
      return !p_204510_3_.getValue(BlockStateProperties.WATERLOGGED) && p_204510_4_ == Fluids.WATER;
   }

   default boolean placeLiquid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, FluidState p_204509_4_) {
      if (!p_204509_3_.getValue(BlockStateProperties.WATERLOGGED) && p_204509_4_.getType() == Fluids.WATER) {
         if (!p_204509_1_.isClientSide()) {
            p_204509_1_.setBlock(p_204509_2_, p_204509_3_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
            p_204509_1_.getLiquidTicks().scheduleTick(p_204509_2_, p_204509_4_.getType(), p_204509_4_.getType().getTickDelay(p_204509_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   default Fluid takeLiquid(IWorld p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_) {
      if (p_204508_3_.getValue(BlockStateProperties.WATERLOGGED)) {
         p_204508_1_.setBlock(p_204508_2_, p_204508_3_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }
}
