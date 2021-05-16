package net.minecraft.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public enum BlockVoxelShape {
   FULL {
      public boolean isSupporting(BlockState p_241854_1_, IBlockReader p_241854_2_, BlockPos p_241854_3_, Direction p_241854_4_) {
         return Block.isFaceFull(p_241854_1_.getBlockSupportShape(p_241854_2_, p_241854_3_), p_241854_4_);
      }
   },
   CENTER {
      private final int CENTER_SUPPORT_WIDTH = 1;
      private final VoxelShape CENTER_SUPPORT_SHAPE = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);

      public boolean isSupporting(BlockState p_241854_1_, IBlockReader p_241854_2_, BlockPos p_241854_3_, Direction p_241854_4_) {
         return !VoxelShapes.joinIsNotEmpty(p_241854_1_.getBlockSupportShape(p_241854_2_, p_241854_3_).getFaceShape(p_241854_4_), this.CENTER_SUPPORT_SHAPE, IBooleanFunction.ONLY_SECOND);
      }
   },
   RIGID {
      private final int RIGID_SUPPORT_WIDTH = 2;
      private final VoxelShape RIGID_SUPPORT_SHAPE = VoxelShapes.join(VoxelShapes.block(), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);

      public boolean isSupporting(BlockState p_241854_1_, IBlockReader p_241854_2_, BlockPos p_241854_3_, Direction p_241854_4_) {
         return !VoxelShapes.joinIsNotEmpty(p_241854_1_.getBlockSupportShape(p_241854_2_, p_241854_3_).getFaceShape(p_241854_4_), this.RIGID_SUPPORT_SHAPE, IBooleanFunction.ONLY_SECOND);
      }
   };

   private BlockVoxelShape() {
   }

   public abstract boolean isSupporting(BlockState p_241854_1_, IBlockReader p_241854_2_, BlockPos p_241854_3_, Direction p_241854_4_);
}
