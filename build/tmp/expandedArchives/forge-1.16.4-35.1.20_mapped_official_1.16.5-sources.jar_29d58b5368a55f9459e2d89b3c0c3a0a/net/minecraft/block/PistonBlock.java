package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PistonBlock extends DirectionalBlock {
   public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private final boolean isSticky;

   public PistonBlock(boolean p_i48281_1_, AbstractBlock.Properties p_i48281_2_) {
      super(p_i48281_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, Boolean.valueOf(false)));
      this.isSticky = p_i48281_1_;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if (p_220053_1_.getValue(EXTENDED)) {
         switch((Direction)p_220053_1_.getValue(FACING)) {
         case DOWN:
            return DOWN_AABB;
         case UP:
         default:
            return UP_AABB;
         case NORTH:
            return NORTH_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case WEST:
            return WEST_AABB;
         case EAST:
            return EAST_AABB;
         }
      } else {
         return VoxelShapes.block();
      }
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (!p_180633_1_.isClientSide) {
         this.checkIfExtend(p_180633_1_, p_180633_2_, p_180633_3_);
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         this.checkIfExtend(p_220069_2_, p_220069_3_, p_220069_1_);
      }

   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock())) {
         if (!p_220082_2_.isClientSide && p_220082_2_.getBlockEntity(p_220082_3_) == null) {
            this.checkIfExtend(p_220082_2_, p_220082_3_, p_220082_1_);
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getNearestLookingDirection().getOpposite()).setValue(EXTENDED, Boolean.valueOf(false));
   }

   private void checkIfExtend(World p_176316_1_, BlockPos p_176316_2_, BlockState p_176316_3_) {
      Direction direction = p_176316_3_.getValue(FACING);
      boolean flag = this.getNeighborSignal(p_176316_1_, p_176316_2_, direction);
      if (flag && !p_176316_3_.getValue(EXTENDED)) {
         if ((new PistonBlockStructureHelper(p_176316_1_, p_176316_2_, direction, true)).resolve()) {
            p_176316_1_.blockEvent(p_176316_2_, this, 0, direction.get3DDataValue());
         }
      } else if (!flag && p_176316_3_.getValue(EXTENDED)) {
         BlockPos blockpos = p_176316_2_.relative(direction, 2);
         BlockState blockstate = p_176316_1_.getBlockState(blockpos);
         int i = 1;
         if (blockstate.is(Blocks.MOVING_PISTON) && blockstate.getValue(FACING) == direction) {
            TileEntity tileentity = p_176316_1_.getBlockEntity(blockpos);
            if (tileentity instanceof PistonTileEntity) {
               PistonTileEntity pistontileentity = (PistonTileEntity)tileentity;
               if (pistontileentity.isExtending() && (pistontileentity.getProgress(0.0F) < 0.5F || p_176316_1_.getGameTime() == pistontileentity.getLastTicked() || ((ServerWorld)p_176316_1_).isHandlingTick())) {
                  i = 2;
               }
            }
         }

         p_176316_1_.blockEvent(p_176316_2_, this, i, direction.get3DDataValue());
      }

   }

   private boolean getNeighborSignal(World p_176318_1_, BlockPos p_176318_2_, Direction p_176318_3_) {
      for(Direction direction : Direction.values()) {
         if (direction != p_176318_3_ && p_176318_1_.hasSignal(p_176318_2_.relative(direction), direction)) {
            return true;
         }
      }

      if (p_176318_1_.hasSignal(p_176318_2_, Direction.DOWN)) {
         return true;
      } else {
         BlockPos blockpos = p_176318_2_.above();

         for(Direction direction1 : Direction.values()) {
            if (direction1 != Direction.DOWN && p_176318_1_.hasSignal(blockpos.relative(direction1), direction1)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean triggerEvent(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      Direction direction = p_189539_1_.getValue(FACING);
      if (!p_189539_2_.isClientSide) {
         boolean flag = this.getNeighborSignal(p_189539_2_, p_189539_3_, direction);
         if (flag && (p_189539_4_ == 1 || p_189539_4_ == 2)) {
            p_189539_2_.setBlock(p_189539_3_, p_189539_1_.setValue(EXTENDED, Boolean.valueOf(true)), 2);
            return false;
         }

         if (!flag && p_189539_4_ == 0) {
            return false;
         }
      }

      if (p_189539_4_ == 0) {
         if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(p_189539_2_, p_189539_3_, direction, true)) return false;
         if (!this.moveBlocks(p_189539_2_, p_189539_3_, direction, true)) {
            return false;
         }

         p_189539_2_.setBlock(p_189539_3_, p_189539_1_.setValue(EXTENDED, Boolean.valueOf(true)), 67);
         p_189539_2_.playSound((PlayerEntity)null, p_189539_3_, SoundEvents.PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, p_189539_2_.random.nextFloat() * 0.25F + 0.6F);
      } else if (p_189539_4_ == 1 || p_189539_4_ == 2) {
         if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(p_189539_2_, p_189539_3_, direction, false)) return false;
         TileEntity tileentity1 = p_189539_2_.getBlockEntity(p_189539_3_.relative(direction));
         if (tileentity1 instanceof PistonTileEntity) {
            ((PistonTileEntity)tileentity1).finalTick();
         }

         BlockState blockstate = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
         p_189539_2_.setBlock(p_189539_3_, blockstate, 20);
         p_189539_2_.setBlockEntity(p_189539_3_, MovingPistonBlock.newMovingBlockEntity(this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(p_189539_5_ & 7)), direction, false, true));
         p_189539_2_.blockUpdated(p_189539_3_, blockstate.getBlock());
         blockstate.updateNeighbourShapes(p_189539_2_, p_189539_3_, 2);
         if (this.isSticky) {
            BlockPos blockpos = p_189539_3_.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
            BlockState blockstate1 = p_189539_2_.getBlockState(blockpos);
            boolean flag1 = false;
            if (blockstate1.is(Blocks.MOVING_PISTON)) {
               TileEntity tileentity = p_189539_2_.getBlockEntity(blockpos);
               if (tileentity instanceof PistonTileEntity) {
                  PistonTileEntity pistontileentity = (PistonTileEntity)tileentity;
                  if (pistontileentity.getDirection() == direction && pistontileentity.isExtending()) {
                     pistontileentity.finalTick();
                     flag1 = true;
                  }
               }
            }

            if (!flag1) {
               if (p_189539_4_ != 1 || blockstate1.isAir() || !isPushable(blockstate1, p_189539_2_, blockpos, direction.getOpposite(), false, direction) || blockstate1.getPistonPushReaction() != PushReaction.NORMAL && !blockstate1.is(Blocks.PISTON) && !blockstate1.is(Blocks.STICKY_PISTON)) {
                  p_189539_2_.removeBlock(p_189539_3_.relative(direction), false);
               } else {
                  this.moveBlocks(p_189539_2_, p_189539_3_, direction, false);
               }
            }
         } else {
            p_189539_2_.removeBlock(p_189539_3_.relative(direction), false);
         }

         p_189539_2_.playSound((PlayerEntity)null, p_189539_3_, SoundEvents.PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, p_189539_2_.random.nextFloat() * 0.15F + 0.6F);
      }

      net.minecraftforge.event.ForgeEventFactory.onPistonMovePost(p_189539_2_, p_189539_3_, direction, (p_189539_4_ == 0));
      return true;
   }

   public static boolean isPushable(BlockState p_185646_0_, World p_185646_1_, BlockPos p_185646_2_, Direction p_185646_3_, boolean p_185646_4_, Direction p_185646_5_) {
      if (p_185646_2_.getY() >= 0 && p_185646_2_.getY() <= p_185646_1_.getMaxBuildHeight() - 1 && p_185646_1_.getWorldBorder().isWithinBounds(p_185646_2_)) {
         if (p_185646_0_.isAir()) {
            return true;
         } else if (!p_185646_0_.is(Blocks.OBSIDIAN) && !p_185646_0_.is(Blocks.CRYING_OBSIDIAN) && !p_185646_0_.is(Blocks.RESPAWN_ANCHOR)) {
            if (p_185646_3_ == Direction.DOWN && p_185646_2_.getY() == 0) {
               return false;
            } else if (p_185646_3_ == Direction.UP && p_185646_2_.getY() == p_185646_1_.getMaxBuildHeight() - 1) {
               return false;
            } else {
               if (!p_185646_0_.is(Blocks.PISTON) && !p_185646_0_.is(Blocks.STICKY_PISTON)) {
                  if (p_185646_0_.getDestroySpeed(p_185646_1_, p_185646_2_) == -1.0F) {
                     return false;
                  }

                  switch(p_185646_0_.getPistonPushReaction()) {
                  case BLOCK:
                     return false;
                  case DESTROY:
                     return p_185646_4_;
                  case PUSH_ONLY:
                     return p_185646_3_ == p_185646_5_;
                  }
               } else if (p_185646_0_.getValue(EXTENDED)) {
                  return false;
               }

               return !p_185646_0_.hasTileEntity();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean moveBlocks(World p_176319_1_, BlockPos p_176319_2_, Direction p_176319_3_, boolean p_176319_4_) {
      BlockPos blockpos = p_176319_2_.relative(p_176319_3_);
      if (!p_176319_4_ && p_176319_1_.getBlockState(blockpos).is(Blocks.PISTON_HEAD)) {
         p_176319_1_.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 20);
      }

      PistonBlockStructureHelper pistonblockstructurehelper = new PistonBlockStructureHelper(p_176319_1_, p_176319_2_, p_176319_3_, p_176319_4_);
      if (!pistonblockstructurehelper.resolve()) {
         return false;
      } else {
         Map<BlockPos, BlockState> map = Maps.newHashMap();
         List<BlockPos> list = pistonblockstructurehelper.getToPush();
         List<BlockState> list1 = Lists.newArrayList();

         for(int i = 0; i < list.size(); ++i) {
            BlockPos blockpos1 = list.get(i);
            BlockState blockstate = p_176319_1_.getBlockState(blockpos1);
            list1.add(blockstate);
            map.put(blockpos1, blockstate);
         }

         List<BlockPos> list2 = pistonblockstructurehelper.getToDestroy();
         BlockState[] ablockstate = new BlockState[list.size() + list2.size()];
         Direction direction = p_176319_4_ ? p_176319_3_ : p_176319_3_.getOpposite();
         int j = 0;

         for(int k = list2.size() - 1; k >= 0; --k) {
            BlockPos blockpos2 = list2.get(k);
            BlockState blockstate1 = p_176319_1_.getBlockState(blockpos2);
            TileEntity tileentity = blockstate1.hasTileEntity() ? p_176319_1_.getBlockEntity(blockpos2) : null;
            dropResources(blockstate1, p_176319_1_, blockpos2, tileentity);
            p_176319_1_.setBlock(blockpos2, Blocks.AIR.defaultBlockState(), 18);
            ablockstate[j++] = blockstate1;
         }

         for(int l = list.size() - 1; l >= 0; --l) {
            BlockPos blockpos3 = list.get(l);
            BlockState blockstate5 = p_176319_1_.getBlockState(blockpos3);
            blockpos3 = blockpos3.relative(direction);
            map.remove(blockpos3);
            p_176319_1_.setBlock(blockpos3, Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, p_176319_3_), 68);
            p_176319_1_.setBlockEntity(blockpos3, MovingPistonBlock.newMovingBlockEntity(list1.get(l), p_176319_3_, p_176319_4_, false));
            ablockstate[j++] = blockstate5;
         }

         if (p_176319_4_) {
            PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockstate4 = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, p_176319_3_).setValue(PistonHeadBlock.TYPE, pistontype);
            BlockState blockstate6 = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, p_176319_3_).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockpos);
            p_176319_1_.setBlock(blockpos, blockstate6, 68);
            p_176319_1_.setBlockEntity(blockpos, MovingPistonBlock.newMovingBlockEntity(blockstate4, p_176319_3_, true, true));
         }

         BlockState blockstate3 = Blocks.AIR.defaultBlockState();

         for(BlockPos blockpos4 : map.keySet()) {
            p_176319_1_.setBlock(blockpos4, blockstate3, 82);
         }

         for(Entry<BlockPos, BlockState> entry : map.entrySet()) {
            BlockPos blockpos5 = entry.getKey();
            BlockState blockstate2 = entry.getValue();
            blockstate2.updateIndirectNeighbourShapes(p_176319_1_, blockpos5, 2);
            blockstate3.updateNeighbourShapes(p_176319_1_, blockpos5, 2);
            blockstate3.updateIndirectNeighbourShapes(p_176319_1_, blockpos5, 2);
         }

         j = 0;

         for(int i1 = list2.size() - 1; i1 >= 0; --i1) {
            BlockState blockstate7 = ablockstate[j++];
            BlockPos blockpos6 = list2.get(i1);
            blockstate7.updateIndirectNeighbourShapes(p_176319_1_, blockpos6, 2);
            p_176319_1_.updateNeighborsAt(blockpos6, blockstate7.getBlock());
         }

         for(int j1 = list.size() - 1; j1 >= 0; --j1) {
            p_176319_1_.updateNeighborsAt(list.get(j1), ablockstate[j++].getBlock());
         }

         if (p_176319_4_) {
            p_176319_1_.updateNeighborsAt(blockpos, Blocks.PISTON_HEAD);
         }

         return true;
      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState rotate(BlockState state, net.minecraft.world.IWorld world, BlockPos pos, Rotation direction) {
       return state.getValue(EXTENDED) ? state : super.rotate(state, world, pos, direction);
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, EXTENDED);
   }

   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return p_220074_1_.getValue(EXTENDED);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
