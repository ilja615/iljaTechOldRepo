package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CactusBlock extends Block implements net.minecraftforge.common.IPlantable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
   protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public CactusBlock(AbstractBlock.Properties p_i48435_1_) {
      super(p_i48435_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_2_.isAreaLoaded(p_225534_3_, 1)) return; // Forge: prevent growing cactus from loading unloaded chunks with block update
      if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      }

   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      BlockPos blockpos = p_225542_3_.above();
      if (p_225542_2_.isEmptyBlock(blockpos)) {
         int i;
         for(i = 1; p_225542_2_.getBlockState(p_225542_3_.below(i)).is(this); ++i) {
         }

         if (i < 3) {
            int j = p_225542_1_.getValue(AGE);
            if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225542_2_, blockpos, p_225542_1_, true)) {
            if (j == 15) {
               p_225542_2_.setBlockAndUpdate(blockpos, this.defaultBlockState());
               BlockState blockstate = p_225542_1_.setValue(AGE, Integer.valueOf(0));
               p_225542_2_.setBlock(p_225542_3_, blockstate, 4);
               blockstate.neighborChanged(p_225542_2_, blockpos, this, p_225542_3_, false);
            } else {
               p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(AGE, Integer.valueOf(j + 1)), 4);
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_225542_2_, p_225542_3_, p_225542_1_);
            }
         }
      }
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return COLLISION_SHAPE;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return OUTLINE_SHAPE;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.relative(direction));
         Material material = blockstate.getMaterial();
         if (material.isSolid() || p_196260_2_.getFluidState(p_196260_3_.relative(direction)).is(FluidTags.LAVA)) {
            return false;
         }
      }

      BlockState blockstate1 = p_196260_2_.getBlockState(p_196260_3_.below());
      return blockstate1.canSustainPlant(p_196260_2_, p_196260_3_, Direction.UP, this) && !p_196260_2_.getBlockState(p_196260_3_.above()).getMaterial().isLiquid();
   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      p_196262_4_.hurt(DamageSource.CACTUS, 1.0F);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   @Override
   public net.minecraftforge.common.PlantType getPlantType(IBlockReader world, BlockPos pos) {
      return net.minecraftforge.common.PlantType.DESERT;
   }

   @Override
   public BlockState getPlant(IBlockReader world, BlockPos pos) {
      return defaultBlockState();
   }
}
