package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class SugarCaneBlock extends Block implements net.minecraftforge.common.IPlantable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public SugarCaneBlock(AbstractBlock.Properties p_i48312_1_) {
      super(p_i48312_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      }

   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (p_225542_2_.isEmptyBlock(p_225542_3_.above())) {
         int i;
         for(i = 1; p_225542_2_.getBlockState(p_225542_3_.below(i)).is(this); ++i) {
         }

         if (i < 3) {
            int j = p_225542_1_.getValue(AGE);
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225542_2_, p_225542_3_, p_225542_1_, true)) {
            if (j == 15) {
               p_225542_2_.setBlockAndUpdate(p_225542_3_.above(), this.defaultBlockState());
               p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(AGE, Integer.valueOf(0)), 4);
            } else {
               p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(AGE, Integer.valueOf(j + 1)), 4);
            }
            }
         }
      }

   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState soil = p_196260_2_.getBlockState(p_196260_3_.below());
      if (soil.canSustainPlant(p_196260_2_, p_196260_3_.below(), Direction.UP, this)) return true;
      BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.below());
      if (blockstate.getBlock() == this) {
         return true;
      } else {
         if (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.DIRT) || blockstate.is(Blocks.COARSE_DIRT) || blockstate.is(Blocks.PODZOL) || blockstate.is(Blocks.SAND) || blockstate.is(Blocks.RED_SAND)) {
            BlockPos blockpos = p_196260_3_.below();

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               BlockState blockstate1 = p_196260_2_.getBlockState(blockpos.relative(direction));
               FluidState fluidstate = p_196260_2_.getFluidState(blockpos.relative(direction));
               if (fluidstate.is(FluidTags.WATER) || blockstate1.is(Blocks.FROSTED_ICE)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   @Override
   public net.minecraftforge.common.PlantType getPlantType(IBlockReader world, BlockPos pos) {
       return net.minecraftforge.common.PlantType.BEACH;
   }

   @Override
   public BlockState getPlant(IBlockReader world, BlockPos pos) {
      return defaultBlockState();
   }
}
