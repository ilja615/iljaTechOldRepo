package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LilyPadBlock extends BushBlock {
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

   public LilyPadBlock(AbstractBlock.Properties p_i48297_1_) {
      super(p_i48297_1_);
   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      super.entityInside(p_196262_1_, p_196262_2_, p_196262_3_, p_196262_4_);
      if (p_196262_2_ instanceof ServerWorld && p_196262_4_ instanceof BoatEntity) {
         p_196262_2_.destroyBlock(new BlockPos(p_196262_3_), true, p_196262_4_);
      }

   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return AABB;
   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      FluidState fluidstate = p_200014_2_.getFluidState(p_200014_3_);
      FluidState fluidstate1 = p_200014_2_.getFluidState(p_200014_3_.above());
      return (fluidstate.getType() == Fluids.WATER || p_200014_1_.getMaterial() == Material.ICE) && fluidstate1.getType() == Fluids.EMPTY;
   }
}
