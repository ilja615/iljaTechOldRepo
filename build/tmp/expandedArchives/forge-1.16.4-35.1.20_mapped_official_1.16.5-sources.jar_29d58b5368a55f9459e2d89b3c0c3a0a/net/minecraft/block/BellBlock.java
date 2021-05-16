package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BellAttachment;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BellBlock extends ContainerBlock {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final EnumProperty<BellAttachment> ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
   private static final VoxelShape EAST_WEST_FLOOR_SHAPE = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   private static final VoxelShape BELL_TOP_SHAPE = Block.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
   private static final VoxelShape BELL_BOTTOM_SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
   private static final VoxelShape BELL_SHAPE = VoxelShapes.or(BELL_BOTTOM_SHAPE, BELL_TOP_SHAPE);
   private static final VoxelShape NORTH_SOUTH_BETWEEN = VoxelShapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
   private static final VoxelShape EAST_WEST_BETWEEN = VoxelShapes.or(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
   private static final VoxelShape TO_WEST = VoxelShapes.or(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
   private static final VoxelShape TO_EAST = VoxelShapes.or(BELL_SHAPE, Block.box(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
   private static final VoxelShape TO_NORTH = VoxelShapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
   private static final VoxelShape TO_SOUTH = VoxelShapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
   private static final VoxelShape CEILING_SHAPE = VoxelShapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));

   public BellBlock(AbstractBlock.Properties p_i49993_1_) {
      super(p_i49993_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACHMENT, BellAttachment.FLOOR).setValue(POWERED, Boolean.valueOf(false)));
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_);
      if (flag != p_220069_1_.getValue(POWERED)) {
         if (flag) {
            this.attemptToRing(p_220069_2_, p_220069_3_, (Direction)null);
         }

         p_220069_2_.setBlock(p_220069_3_, p_220069_1_.setValue(POWERED, Boolean.valueOf(flag)), 3);
      }

   }

   public void onProjectileHit(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, ProjectileEntity p_220066_4_) {
      Entity entity = p_220066_4_.getOwner();
      PlayerEntity playerentity = entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
      this.onHit(p_220066_1_, p_220066_2_, p_220066_3_, playerentity, true);
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      return this.onHit(p_225533_2_, p_225533_1_, p_225533_6_, p_225533_4_, true) ? ActionResultType.sidedSuccess(p_225533_2_.isClientSide) : ActionResultType.PASS;
   }

   public boolean onHit(World p_226884_1_, BlockState p_226884_2_, BlockRayTraceResult p_226884_3_, @Nullable PlayerEntity p_226884_4_, boolean p_226884_5_) {
      Direction direction = p_226884_3_.getDirection();
      BlockPos blockpos = p_226884_3_.getBlockPos();
      boolean flag = !p_226884_5_ || this.isProperHit(p_226884_2_, direction, p_226884_3_.getLocation().y - (double)blockpos.getY());
      if (flag) {
         boolean flag1 = this.attemptToRing(p_226884_1_, blockpos, direction);
         if (flag1 && p_226884_4_ != null) {
            p_226884_4_.awardStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isProperHit(BlockState p_220129_1_, Direction p_220129_2_, double p_220129_3_) {
      if (p_220129_2_.getAxis() != Direction.Axis.Y && !(p_220129_3_ > (double)0.8124F)) {
         Direction direction = p_220129_1_.getValue(FACING);
         BellAttachment bellattachment = p_220129_1_.getValue(ATTACHMENT);
         switch(bellattachment) {
         case FLOOR:
            return direction.getAxis() == p_220129_2_.getAxis();
         case SINGLE_WALL:
         case DOUBLE_WALL:
            return direction.getAxis() != p_220129_2_.getAxis();
         case CEILING:
            return true;
         default:
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean attemptToRing(World p_226885_1_, BlockPos p_226885_2_, @Nullable Direction p_226885_3_) {
      TileEntity tileentity = p_226885_1_.getBlockEntity(p_226885_2_);
      if (!p_226885_1_.isClientSide && tileentity instanceof BellTileEntity) {
         if (p_226885_3_ == null) {
            p_226885_3_ = p_226885_1_.getBlockState(p_226885_2_).getValue(FACING);
         }

         ((BellTileEntity)tileentity).onHit(p_226885_3_);
         p_226885_1_.playSound((PlayerEntity)null, p_226885_2_, SoundEvents.BELL_BLOCK, SoundCategory.BLOCKS, 2.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShape getVoxelShape(BlockState p_220128_1_) {
      Direction direction = p_220128_1_.getValue(FACING);
      BellAttachment bellattachment = p_220128_1_.getValue(ATTACHMENT);
      if (bellattachment == BellAttachment.FLOOR) {
         return direction != Direction.NORTH && direction != Direction.SOUTH ? EAST_WEST_FLOOR_SHAPE : NORTH_SOUTH_FLOOR_SHAPE;
      } else if (bellattachment == BellAttachment.CEILING) {
         return CEILING_SHAPE;
      } else if (bellattachment == BellAttachment.DOUBLE_WALL) {
         return direction != Direction.NORTH && direction != Direction.SOUTH ? EAST_WEST_BETWEEN : NORTH_SOUTH_BETWEEN;
      } else if (direction == Direction.NORTH) {
         return TO_NORTH;
      } else if (direction == Direction.SOUTH) {
         return TO_SOUTH;
      } else {
         return direction == Direction.EAST ? TO_EAST : TO_WEST;
      }
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.getVoxelShape(p_220071_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.getVoxelShape(p_220053_1_);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction direction = p_196258_1_.getClickedFace();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      World world = p_196258_1_.getLevel();
      Direction.Axis direction$axis = direction.getAxis();
      if (direction$axis == Direction.Axis.Y) {
         BlockState blockstate = this.defaultBlockState().setValue(ATTACHMENT, direction == Direction.DOWN ? BellAttachment.CEILING : BellAttachment.FLOOR).setValue(FACING, p_196258_1_.getHorizontalDirection());
         if (blockstate.canSurvive(p_196258_1_.getLevel(), blockpos)) {
            return blockstate;
         }
      } else {
         boolean flag = direction$axis == Direction.Axis.X && world.getBlockState(blockpos.west()).isFaceSturdy(world, blockpos.west(), Direction.EAST) && world.getBlockState(blockpos.east()).isFaceSturdy(world, blockpos.east(), Direction.WEST) || direction$axis == Direction.Axis.Z && world.getBlockState(blockpos.north()).isFaceSturdy(world, blockpos.north(), Direction.SOUTH) && world.getBlockState(blockpos.south()).isFaceSturdy(world, blockpos.south(), Direction.NORTH);
         BlockState blockstate1 = this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(ATTACHMENT, flag ? BellAttachment.DOUBLE_WALL : BellAttachment.SINGLE_WALL);
         if (blockstate1.canSurvive(p_196258_1_.getLevel(), p_196258_1_.getClickedPos())) {
            return blockstate1;
         }

         boolean flag1 = world.getBlockState(blockpos.below()).isFaceSturdy(world, blockpos.below(), Direction.UP);
         blockstate1 = blockstate1.setValue(ATTACHMENT, flag1 ? BellAttachment.FLOOR : BellAttachment.CEILING);
         if (blockstate1.canSurvive(p_196258_1_.getLevel(), p_196258_1_.getClickedPos())) {
            return blockstate1;
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      BellAttachment bellattachment = p_196271_1_.getValue(ATTACHMENT);
      Direction direction = getConnectedDirection(p_196271_1_).getOpposite();
      if (direction == p_196271_2_ && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) && bellattachment != BellAttachment.DOUBLE_WALL) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_196271_2_.getAxis() == p_196271_1_.getValue(FACING).getAxis()) {
            if (bellattachment == BellAttachment.DOUBLE_WALL && !p_196271_3_.isFaceSturdy(p_196271_4_, p_196271_6_, p_196271_2_)) {
               return p_196271_1_.setValue(ATTACHMENT, BellAttachment.SINGLE_WALL).setValue(FACING, p_196271_2_.getOpposite());
            }

            if (bellattachment == BellAttachment.SINGLE_WALL && direction.getOpposite() == p_196271_2_ && p_196271_3_.isFaceSturdy(p_196271_4_, p_196271_6_, p_196271_1_.getValue(FACING))) {
               return p_196271_1_.setValue(ATTACHMENT, BellAttachment.DOUBLE_WALL);
            }
         }

         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Direction direction = getConnectedDirection(p_196260_1_).getOpposite();
      return direction == Direction.UP ? Block.canSupportCenter(p_196260_2_, p_196260_3_.above(), Direction.DOWN) : HorizontalFaceBlock.canAttach(p_196260_2_, p_196260_3_, direction);
   }

   private static Direction getConnectedDirection(BlockState p_220131_0_) {
      switch((BellAttachment)p_220131_0_.getValue(ATTACHMENT)) {
      case FLOOR:
         return Direction.UP;
      case CEILING:
         return Direction.DOWN;
      default:
         return p_220131_0_.getValue(FACING).getOpposite();
      }
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, ATTACHMENT, POWERED);
   }

   @Nullable
   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new BellTileEntity();
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
