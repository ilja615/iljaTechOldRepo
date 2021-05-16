package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class VineBlock extends Block implements net.minecraftforge.common.IForgeShearable {
   public static final BooleanProperty UP = SixWayBlock.UP;
   public static final BooleanProperty NORTH = SixWayBlock.NORTH;
   public static final BooleanProperty EAST = SixWayBlock.EAST;
   public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
   public static final BooleanProperty WEST = SixWayBlock.WEST;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_199782_0_) -> {
      return p_199782_0_.getKey() != Direction.DOWN;
   }).collect(Util.toMap());
   private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   private final Map<BlockState, VoxelShape> shapesCache;

   public VineBlock(AbstractBlock.Properties p_i48303_1_) {
      super(p_i48303_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(UP, Boolean.valueOf(false)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)));
      this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), VineBlock::calculateShape)));
   }

   private static VoxelShape calculateShape(BlockState p_242685_0_) {
      VoxelShape voxelshape = VoxelShapes.empty();
      if (p_242685_0_.getValue(UP)) {
         voxelshape = UP_AABB;
      }

      if (p_242685_0_.getValue(NORTH)) {
         voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
      }

      if (p_242685_0_.getValue(SOUTH)) {
         voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
      }

      if (p_242685_0_.getValue(EAST)) {
         voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
      }

      if (p_242685_0_.getValue(WEST)) {
         voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
      }

      return voxelshape;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.shapesCache.get(p_220053_1_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return this.hasFaces(this.getUpdatedState(p_196260_1_, p_196260_2_, p_196260_3_));
   }

   private boolean hasFaces(BlockState p_196543_1_) {
      return this.countFaces(p_196543_1_) > 0;
   }

   private int countFaces(BlockState p_208496_1_) {
      int i = 0;

      for(BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
         if (p_208496_1_.getValue(booleanproperty)) {
            ++i;
         }
      }

      return i;
   }

   private boolean canSupportAtFace(IBlockReader p_196541_1_, BlockPos p_196541_2_, Direction p_196541_3_) {
      if (p_196541_3_ == Direction.DOWN) {
         return false;
      } else {
         BlockPos blockpos = p_196541_2_.relative(p_196541_3_);
         if (isAcceptableNeighbour(p_196541_1_, blockpos, p_196541_3_)) {
            return true;
         } else if (p_196541_3_.getAxis() == Direction.Axis.Y) {
            return false;
         } else {
            BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(p_196541_3_);
            BlockState blockstate = p_196541_1_.getBlockState(p_196541_2_.above());
            return blockstate.is(this) && blockstate.getValue(booleanproperty);
         }
      }
   }

   public static boolean isAcceptableNeighbour(IBlockReader p_196542_0_, BlockPos p_196542_1_, Direction p_196542_2_) {
      BlockState blockstate = p_196542_0_.getBlockState(p_196542_1_);
      return Block.isFaceFull(blockstate.getCollisionShape(p_196542_0_, p_196542_1_), p_196542_2_.getOpposite());
   }

   private BlockState getUpdatedState(BlockState p_196545_1_, IBlockReader p_196545_2_, BlockPos p_196545_3_) {
      BlockPos blockpos = p_196545_3_.above();
      if (p_196545_1_.getValue(UP)) {
         p_196545_1_ = p_196545_1_.setValue(UP, Boolean.valueOf(isAcceptableNeighbour(p_196545_2_, blockpos, Direction.DOWN)));
      }

      BlockState blockstate = null;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BooleanProperty booleanproperty = getPropertyForFace(direction);
         if (p_196545_1_.getValue(booleanproperty)) {
            boolean flag = this.canSupportAtFace(p_196545_2_, p_196545_3_, direction);
            if (!flag) {
               if (blockstate == null) {
                  blockstate = p_196545_2_.getBlockState(blockpos);
               }

               flag = blockstate.is(this) && blockstate.getValue(booleanproperty);
            }

            p_196545_1_ = p_196545_1_.setValue(booleanproperty, Boolean.valueOf(flag));
         }
      }

      return p_196545_1_;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.DOWN) {
         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         BlockState blockstate = this.getUpdatedState(p_196271_1_, p_196271_4_, p_196271_5_);
         return !this.hasFaces(blockstate) ? Blocks.AIR.defaultBlockState() : blockstate;
      }
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (p_225542_2_.random.nextInt(4) == 0 && p_225542_2_.isAreaLoaded(p_225542_3_, 4)) { // Forge: check area to prevent loading unloaded chunks
         Direction direction = Direction.getRandom(p_225542_4_);
         BlockPos blockpos = p_225542_3_.above();
         if (direction.getAxis().isHorizontal() && !p_225542_1_.getValue(getPropertyForFace(direction))) {
            if (this.canSpread(p_225542_2_, p_225542_3_)) {
               BlockPos blockpos4 = p_225542_3_.relative(direction);
               BlockState blockstate4 = p_225542_2_.getBlockState(blockpos4);
               if (blockstate4.isAir(p_225542_2_, blockpos4)) {
                  Direction direction3 = direction.getClockWise();
                  Direction direction4 = direction.getCounterClockWise();
                  boolean flag = p_225542_1_.getValue(getPropertyForFace(direction3));
                  boolean flag1 = p_225542_1_.getValue(getPropertyForFace(direction4));
                  BlockPos blockpos2 = blockpos4.relative(direction3);
                  BlockPos blockpos3 = blockpos4.relative(direction4);
                  if (flag && isAcceptableNeighbour(p_225542_2_, blockpos2, direction3)) {
                     p_225542_2_.setBlock(blockpos4, this.defaultBlockState().setValue(getPropertyForFace(direction3), Boolean.valueOf(true)), 2);
                  } else if (flag1 && isAcceptableNeighbour(p_225542_2_, blockpos3, direction4)) {
                     p_225542_2_.setBlock(blockpos4, this.defaultBlockState().setValue(getPropertyForFace(direction4), Boolean.valueOf(true)), 2);
                  } else {
                     Direction direction1 = direction.getOpposite();
                     if (flag && p_225542_2_.isEmptyBlock(blockpos2) && isAcceptableNeighbour(p_225542_2_, p_225542_3_.relative(direction3), direction1)) {
                        p_225542_2_.setBlock(blockpos2, this.defaultBlockState().setValue(getPropertyForFace(direction1), Boolean.valueOf(true)), 2);
                     } else if (flag1 && p_225542_2_.isEmptyBlock(blockpos3) && isAcceptableNeighbour(p_225542_2_, p_225542_3_.relative(direction4), direction1)) {
                        p_225542_2_.setBlock(blockpos3, this.defaultBlockState().setValue(getPropertyForFace(direction1), Boolean.valueOf(true)), 2);
                     } else if ((double)p_225542_2_.random.nextFloat() < 0.05D && isAcceptableNeighbour(p_225542_2_, blockpos4.above(), Direction.UP)) {
                        p_225542_2_.setBlock(blockpos4, this.defaultBlockState().setValue(UP, Boolean.valueOf(true)), 2);
                     }
                  }
               } else if (isAcceptableNeighbour(p_225542_2_, blockpos4, direction)) {
                  p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(getPropertyForFace(direction), Boolean.valueOf(true)), 2);
               }

            }
         } else {
            if (direction == Direction.UP && p_225542_3_.getY() < 255) {
               if (this.canSupportAtFace(p_225542_2_, p_225542_3_, direction)) {
                  p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(UP, Boolean.valueOf(true)), 2);
                  return;
               }

               if (p_225542_2_.isEmptyBlock(blockpos)) {
                  if (!this.canSpread(p_225542_2_, p_225542_3_)) {
                     return;
                  }

                  BlockState blockstate3 = p_225542_1_;

                  for(Direction direction2 : Direction.Plane.HORIZONTAL) {
                     if (p_225542_4_.nextBoolean() || !isAcceptableNeighbour(p_225542_2_, blockpos.relative(direction2), Direction.UP)) {
                        blockstate3 = blockstate3.setValue(getPropertyForFace(direction2), Boolean.valueOf(false));
                     }
                  }

                  if (this.hasHorizontalConnection(blockstate3)) {
                     p_225542_2_.setBlock(blockpos, blockstate3, 2);
                  }

                  return;
               }
            }

            if (p_225542_3_.getY() > 0) {
               BlockPos blockpos1 = p_225542_3_.below();
               BlockState blockstate = p_225542_2_.getBlockState(blockpos1);
               boolean isAir = blockstate.isAir(p_225542_2_, blockpos1);
               if (isAir || blockstate.is(this)) {
                  BlockState blockstate1 = isAir ? this.defaultBlockState() : blockstate;
                  BlockState blockstate2 = this.copyRandomFaces(p_225542_1_, blockstate1, p_225542_4_);
                  if (blockstate1 != blockstate2 && this.hasHorizontalConnection(blockstate2)) {
                     p_225542_2_.setBlock(blockpos1, blockstate2, 2);
                  }
               }
            }

         }
      }
   }

   private BlockState copyRandomFaces(BlockState p_196544_1_, BlockState p_196544_2_, Random p_196544_3_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (p_196544_3_.nextBoolean()) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            if (p_196544_1_.getValue(booleanproperty)) {
               p_196544_2_ = p_196544_2_.setValue(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return p_196544_2_;
   }

   private boolean hasHorizontalConnection(BlockState p_196540_1_) {
      return p_196540_1_.getValue(NORTH) || p_196540_1_.getValue(EAST) || p_196540_1_.getValue(SOUTH) || p_196540_1_.getValue(WEST);
   }

   private boolean canSpread(IBlockReader p_196539_1_, BlockPos p_196539_2_) {
      int i = 4;
      Iterable<BlockPos> iterable = BlockPos.betweenClosed(p_196539_2_.getX() - 4, p_196539_2_.getY() - 1, p_196539_2_.getZ() - 4, p_196539_2_.getX() + 4, p_196539_2_.getY() + 1, p_196539_2_.getZ() + 4);
      int j = 5;

      for(BlockPos blockpos : iterable) {
         if (p_196539_1_.getBlockState(blockpos).is(this)) {
            --j;
            if (j <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      BlockState blockstate = p_196253_2_.getLevel().getBlockState(p_196253_2_.getClickedPos());
      if (blockstate.is(this)) {
         return this.countFaces(blockstate) < PROPERTY_BY_DIRECTION.size();
      } else {
         return super.canBeReplaced(p_196253_1_, p_196253_2_);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos());
      boolean flag = blockstate.is(this);
      BlockState blockstate1 = flag ? blockstate : this.defaultBlockState();

      for(Direction direction : p_196258_1_.getNearestLookingDirections()) {
         if (direction != Direction.DOWN) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            boolean flag1 = flag && blockstate.getValue(booleanproperty);
            if (!flag1 && this.canSupportAtFace(p_196258_1_.getLevel(), p_196258_1_.getClickedPos(), direction)) {
               return blockstate1.setValue(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return flag ? blockstate1 : null;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UP, NORTH, EAST, SOUTH, WEST);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(SOUTH)).setValue(EAST, p_185499_1_.getValue(WEST)).setValue(SOUTH, p_185499_1_.getValue(NORTH)).setValue(WEST, p_185499_1_.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(EAST)).setValue(EAST, p_185499_1_.getValue(SOUTH)).setValue(SOUTH, p_185499_1_.getValue(WEST)).setValue(WEST, p_185499_1_.getValue(NORTH));
      case CLOCKWISE_90:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(WEST)).setValue(EAST, p_185499_1_.getValue(NORTH)).setValue(SOUTH, p_185499_1_.getValue(EAST)).setValue(WEST, p_185499_1_.getValue(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return p_185471_1_.setValue(NORTH, p_185471_1_.getValue(SOUTH)).setValue(SOUTH, p_185471_1_.getValue(NORTH));
      case FRONT_BACK:
         return p_185471_1_.setValue(EAST, p_185471_1_.getValue(WEST)).setValue(WEST, p_185471_1_.getValue(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   public static BooleanProperty getPropertyForFace(Direction p_176267_0_) {
      return PROPERTY_BY_DIRECTION.get(p_176267_0_);
   }

   @Override
   public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, net.minecraft.entity.LivingEntity entity) { return true; }
}
