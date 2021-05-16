package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class TallSeaGrassBlock extends DoublePlantBlock implements ILiquidContainer {
   public static final EnumProperty<DoubleBlockHalf> HALF = DoublePlantBlock.HALF;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public TallSeaGrassBlock(AbstractBlock.Properties p_i49970_1_) {
      super(p_i49970_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.isFaceSturdy(p_200014_2_, p_200014_3_, Direction.UP) && !p_200014_1_.is(Blocks.MAGMA_BLOCK);
   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(Blocks.SEAGRASS);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = super.getStateForPlacement(p_196258_1_);
      if (blockstate != null) {
         FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos().above());
         if (fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8) {
            return blockstate;
         }
      }

      return null;
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      if (p_196260_1_.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.below());
         return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
      } else {
         FluidState fluidstate = p_196260_2_.getFluidState(p_196260_3_);
         return super.canSurvive(p_196260_1_, p_196260_2_, p_196260_3_) && fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8;
      }
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.WATER.getSource(false);
   }

   public boolean canPlaceLiquid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
      return false;
   }

   public boolean placeLiquid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, FluidState p_204509_4_) {
      return false;
   }
}
