package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TrapDoorBlock extends HorizontalBlock implements IWaterLoggable {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape TOP_AABB = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   public TrapDoorBlock(AbstractBlock.Properties p_i48307_1_) {
      super(p_i48307_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)).setValue(HALF, Half.BOTTOM).setValue(POWERED, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if (!p_220053_1_.getValue(OPEN)) {
         return p_220053_1_.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch((Direction)p_220053_1_.getValue(FACING)) {
         case NORTH:
         default:
            return NORTH_OPEN_AABB;
         case SOUTH:
            return SOUTH_OPEN_AABB;
         case WEST:
            return WEST_OPEN_AABB;
         case EAST:
            return EAST_OPEN_AABB;
         }
      }
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return p_196266_1_.getValue(OPEN);
      case WATER:
         return p_196266_1_.getValue(WATERLOGGED);
      case AIR:
         return p_196266_1_.getValue(OPEN);
      default:
         return false;
      }
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (this.material == Material.METAL) {
         return ActionResultType.PASS;
      } else {
         p_225533_1_ = p_225533_1_.cycle(OPEN);
         p_225533_2_.setBlock(p_225533_3_, p_225533_1_, 2);
         if (p_225533_1_.getValue(WATERLOGGED)) {
            p_225533_2_.getLiquidTicks().scheduleTick(p_225533_3_, Fluids.WATER, Fluids.WATER.getTickDelay(p_225533_2_));
         }

         this.playSound(p_225533_4_, p_225533_2_, p_225533_3_, p_225533_1_.getValue(OPEN));
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      }
   }

   protected void playSound(@Nullable PlayerEntity p_185731_1_, World p_185731_2_, BlockPos p_185731_3_, boolean p_185731_4_) {
      if (p_185731_4_) {
         int i = this.material == Material.METAL ? 1037 : 1007;
         p_185731_2_.levelEvent(p_185731_1_, i, p_185731_3_, 0);
      } else {
         int j = this.material == Material.METAL ? 1036 : 1013;
         p_185731_2_.levelEvent(p_185731_1_, j, p_185731_3_, 0);
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_);
         if (flag != p_220069_1_.getValue(POWERED)) {
            if (p_220069_1_.getValue(OPEN) != flag) {
               p_220069_1_ = p_220069_1_.setValue(OPEN, Boolean.valueOf(flag));
               this.playSound((PlayerEntity)null, p_220069_2_, p_220069_3_, flag);
            }

            p_220069_2_.setBlock(p_220069_3_, p_220069_1_.setValue(POWERED, Boolean.valueOf(flag)), 2);
            if (p_220069_1_.getValue(WATERLOGGED)) {
               p_220069_2_.getLiquidTicks().scheduleTick(p_220069_3_, Fluids.WATER, Fluids.WATER.getTickDelay(p_220069_2_));
            }
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = this.defaultBlockState();
      FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
      Direction direction = p_196258_1_.getClickedFace();
      if (!p_196258_1_.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
         blockstate = blockstate.setValue(FACING, direction).setValue(HALF, p_196258_1_.getClickLocation().y - (double)p_196258_1_.getClickedPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
      } else {
         blockstate = blockstate.setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite()).setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
      }

      if (p_196258_1_.getLevel().hasNeighborSignal(p_196258_1_.getClickedPos())) {
         blockstate = blockstate.setValue(OPEN, Boolean.valueOf(true)).setValue(POWERED, Boolean.valueOf(true));
      }

      return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getValue(WATERLOGGED)) {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   //Forge Start
   @Override
   public boolean isLadder(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, net.minecraft.entity.LivingEntity entity) {
      if (state.getValue(OPEN)) {
         BlockState down = world.getBlockState(pos.below());
         if (down.getBlock() == net.minecraft.block.Blocks.LADDER)
            return down.getValue(LadderBlock.FACING) == state.getValue(FACING);
      }
      return false;
   }
   //Forge End

}
