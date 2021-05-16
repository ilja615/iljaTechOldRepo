package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.SwordItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BambooBlock extends Block implements IGrowable {
   protected static final VoxelShape SMALL_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
   protected static final VoxelShape LARGE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
   protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
   public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
   public static final EnumProperty<BambooLeaves> LEAVES = BlockStateProperties.BAMBOO_LEAVES;
   public static final IntegerProperty STAGE = BlockStateProperties.STAGE;

   public BambooBlock(AbstractBlock.Properties p_i49998_1_) {
      super(p_i49998_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(LEAVES, BambooLeaves.NONE).setValue(STAGE, Integer.valueOf(0)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE, LEAVES, STAGE);
   }

   public AbstractBlock.OffsetType getOffsetType() {
      return AbstractBlock.OffsetType.XZ;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      VoxelShape voxelshape = p_220053_1_.getValue(LEAVES) == BambooLeaves.LARGE ? LARGE_SHAPE : SMALL_SHAPE;
      Vector3d vector3d = p_220053_1_.getOffset(p_220053_2_, p_220053_3_);
      return voxelshape.move(vector3d.x, vector3d.y, vector3d.z);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      Vector3d vector3d = p_220071_1_.getOffset(p_220071_2_, p_220071_3_);
      return COLLISION_SHAPE.move(vector3d.x, vector3d.y, vector3d.z);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
      if (!fluidstate.isEmpty()) {
         return null;
      } else {
         BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos().below());
         if (blockstate.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (blockstate.is(Blocks.BAMBOO_SAPLING)) {
               return this.defaultBlockState().setValue(AGE, Integer.valueOf(0));
            } else if (blockstate.is(Blocks.BAMBOO)) {
               int i = blockstate.getValue(AGE) > 0 ? 1 : 0;
               return this.defaultBlockState().setValue(AGE, Integer.valueOf(i));
            } else {
               BlockState blockstate1 = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos().above());
               return !blockstate1.is(Blocks.BAMBOO) && !blockstate1.is(Blocks.BAMBOO_SAPLING) ? Blocks.BAMBOO_SAPLING.defaultBlockState() : this.defaultBlockState().setValue(AGE, blockstate1.getValue(AGE));
            }
         } else {
            return null;
         }
      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      }

   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return p_149653_1_.getValue(STAGE) == 0;
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (p_225542_1_.getValue(STAGE) == 0) {
         if (p_225542_2_.isEmptyBlock(p_225542_3_.above()) && p_225542_2_.getRawBrightness(p_225542_3_.above(), 0) >= 9) {
            int i = this.getHeightBelowUpToMax(p_225542_2_, p_225542_3_) + 1;
            if (i < 16 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225542_2_, p_225542_3_, p_225542_1_, p_225542_4_.nextInt(3) == 0)) {
               this.growBamboo(p_225542_1_, p_225542_2_, p_225542_3_, p_225542_4_, i);
               net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_225542_2_, p_225542_3_, p_225542_1_);
            }
         }

      }
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      if (p_196271_2_ == Direction.UP && p_196271_3_.is(Blocks.BAMBOO) && p_196271_3_.getValue(AGE) > p_196271_1_.getValue(AGE)) {
         p_196271_4_.setBlock(p_196271_5_, p_196271_1_.cycle(AGE), 2);
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      int i = this.getHeightAboveUpToMax(p_176473_1_, p_176473_2_);
      int j = this.getHeightBelowUpToMax(p_176473_1_, p_176473_2_);
      return i + j + 1 < 16 && p_176473_1_.getBlockState(p_176473_2_.above(i)).getValue(STAGE) != 1;
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      int i = this.getHeightAboveUpToMax(p_225535_1_, p_225535_3_);
      int j = this.getHeightBelowUpToMax(p_225535_1_, p_225535_3_);
      int k = i + j + 1;
      int l = 1 + p_225535_2_.nextInt(2);

      for(int i1 = 0; i1 < l; ++i1) {
         BlockPos blockpos = p_225535_3_.above(i);
         BlockState blockstate = p_225535_1_.getBlockState(blockpos);
         if (k >= 16 || blockstate.getValue(STAGE) == 1 || !p_225535_1_.isEmptyBlock(blockpos.above())) {
            return;
         }

         this.growBamboo(blockstate, p_225535_1_, blockpos, p_225535_2_, k);
         ++i;
         ++k;
      }

   }

   public float getDestroyProgress(BlockState p_180647_1_, PlayerEntity p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
      return p_180647_2_.getMainHandItem().getItem() instanceof SwordItem ? 1.0F : super.getDestroyProgress(p_180647_1_, p_180647_2_, p_180647_3_, p_180647_4_);
   }

   protected void growBamboo(BlockState p_220258_1_, World p_220258_2_, BlockPos p_220258_3_, Random p_220258_4_, int p_220258_5_) {
      BlockState blockstate = p_220258_2_.getBlockState(p_220258_3_.below());
      BlockPos blockpos = p_220258_3_.below(2);
      BlockState blockstate1 = p_220258_2_.getBlockState(blockpos);
      BambooLeaves bambooleaves = BambooLeaves.NONE;
      if (p_220258_5_ >= 1) {
         if (blockstate.is(Blocks.BAMBOO) && blockstate.getValue(LEAVES) != BambooLeaves.NONE) {
            if (blockstate.is(Blocks.BAMBOO) && blockstate.getValue(LEAVES) != BambooLeaves.NONE) {
               bambooleaves = BambooLeaves.LARGE;
               if (blockstate1.is(Blocks.BAMBOO)) {
                  p_220258_2_.setBlock(p_220258_3_.below(), blockstate.setValue(LEAVES, BambooLeaves.SMALL), 3);
                  p_220258_2_.setBlock(blockpos, blockstate1.setValue(LEAVES, BambooLeaves.NONE), 3);
               }
            }
         } else {
            bambooleaves = BambooLeaves.SMALL;
         }
      }

      int i = p_220258_1_.getValue(AGE) != 1 && !blockstate1.is(Blocks.BAMBOO) ? 0 : 1;
      int j = (p_220258_5_ < 11 || !(p_220258_4_.nextFloat() < 0.25F)) && p_220258_5_ != 15 ? 0 : 1;
      p_220258_2_.setBlock(p_220258_3_.above(), this.defaultBlockState().setValue(AGE, Integer.valueOf(i)).setValue(LEAVES, bambooleaves).setValue(STAGE, Integer.valueOf(j)), 3);
   }

   protected int getHeightAboveUpToMax(IBlockReader p_220259_1_, BlockPos p_220259_2_) {
      int i;
      for(i = 0; i < 16 && p_220259_1_.getBlockState(p_220259_2_.above(i + 1)).is(Blocks.BAMBOO); ++i) {
      }

      return i;
   }

   protected int getHeightBelowUpToMax(IBlockReader p_220260_1_, BlockPos p_220260_2_) {
      int i;
      for(i = 0; i < 16 && p_220260_1_.getBlockState(p_220260_2_.below(i + 1)).is(Blocks.BAMBOO); ++i) {
      }

      return i;
   }
}
