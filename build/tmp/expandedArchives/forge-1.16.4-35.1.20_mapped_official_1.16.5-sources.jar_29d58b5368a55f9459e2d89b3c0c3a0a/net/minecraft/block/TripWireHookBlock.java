package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TripWireHookBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
   protected static final VoxelShape WEST_AABB = Block.box(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);

   public TripWireHookBlock(AbstractBlock.Properties p_i48304_1_) {
      super(p_i48304_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction)p_220053_1_.getValue(FACING)) {
      case EAST:
      default:
         return EAST_AABB;
      case WEST:
         return WEST_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case NORTH:
         return NORTH_AABB;
      }
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Direction direction = p_196260_1_.getValue(FACING);
      BlockPos blockpos = p_196260_3_.relative(direction.getOpposite());
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      return direction.getAxis().isHorizontal() && blockstate.isFaceSturdy(p_196260_2_, blockpos, direction);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getOpposite() == p_196271_1_.getValue(FACING) && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false));
      IWorldReader iworldreader = p_196258_1_.getLevel();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      Direction[] adirection = p_196258_1_.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(iworldreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      this.calculateState(p_180633_1_, p_180633_2_, p_180633_3_, false, false, -1, (BlockState)null);
   }

   public void calculateState(World p_176260_1_, BlockPos p_176260_2_, BlockState p_176260_3_, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, @Nullable BlockState p_176260_7_) {
      Direction direction = p_176260_3_.getValue(FACING);
      boolean flag = p_176260_3_.getValue(ATTACHED);
      boolean flag1 = p_176260_3_.getValue(POWERED);
      boolean flag2 = !p_176260_4_;
      boolean flag3 = false;
      int i = 0;
      BlockState[] ablockstate = new BlockState[42];

      for(int j = 1; j < 42; ++j) {
         BlockPos blockpos = p_176260_2_.relative(direction, j);
         BlockState blockstate = p_176260_1_.getBlockState(blockpos);
         if (blockstate.is(Blocks.TRIPWIRE_HOOK)) {
            if (blockstate.getValue(FACING) == direction.getOpposite()) {
               i = j;
            }
            break;
         }

         if (!blockstate.is(Blocks.TRIPWIRE) && j != p_176260_6_) {
            ablockstate[j] = null;
            flag2 = false;
         } else {
            if (j == p_176260_6_) {
               blockstate = MoreObjects.firstNonNull(p_176260_7_, blockstate);
            }

            boolean flag4 = !blockstate.getValue(TripWireBlock.DISARMED);
            boolean flag5 = blockstate.getValue(TripWireBlock.POWERED);
            flag3 |= flag4 && flag5;
            ablockstate[j] = blockstate;
            if (j == p_176260_6_) {
               p_176260_1_.getBlockTicks().scheduleTick(p_176260_2_, this, 10);
               flag2 &= flag4;
            }
         }
      }

      flag2 = flag2 & i > 1;
      flag3 = flag3 & flag2;
      BlockState blockstate1 = this.defaultBlockState().setValue(ATTACHED, Boolean.valueOf(flag2)).setValue(POWERED, Boolean.valueOf(flag3));
      if (i > 0) {
         BlockPos blockpos1 = p_176260_2_.relative(direction, i);
         Direction direction1 = direction.getOpposite();
         p_176260_1_.setBlock(blockpos1, blockstate1.setValue(FACING, direction1), 3);
         this.notifyNeighbors(p_176260_1_, blockpos1, direction1);
         this.playSound(p_176260_1_, blockpos1, flag2, flag3, flag, flag1);
      }

      this.playSound(p_176260_1_, p_176260_2_, flag2, flag3, flag, flag1);
      if (!p_176260_4_) {
         p_176260_1_.setBlock(p_176260_2_, blockstate1.setValue(FACING, direction), 3);
         if (p_176260_5_) {
            this.notifyNeighbors(p_176260_1_, p_176260_2_, direction);
         }
      }

      if (flag != flag2) {
         for(int k = 1; k < i; ++k) {
            BlockPos blockpos2 = p_176260_2_.relative(direction, k);
            BlockState blockstate2 = ablockstate[k];
            if (blockstate2 != null) {
               p_176260_1_.setBlock(blockpos2, blockstate2.setValue(ATTACHED, Boolean.valueOf(flag2)), 3);
               if (!p_176260_1_.getBlockState(blockpos2).isAir()) {
               }
            }
         }
      }

   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      this.calculateState(p_225534_2_, p_225534_3_, p_225534_1_, false, true, -1, (BlockState)null);
   }

   private void playSound(World p_180694_1_, BlockPos p_180694_2_, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_) {
      if (p_180694_4_ && !p_180694_6_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
      } else if (!p_180694_4_ && p_180694_6_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
      } else if (p_180694_3_ && !p_180694_5_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
      } else if (!p_180694_3_ && p_180694_5_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (p_180694_1_.random.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void notifyNeighbors(World p_176262_1_, BlockPos p_176262_2_, Direction p_176262_3_) {
      p_176262_1_.updateNeighborsAt(p_176262_2_, this);
      p_176262_1_.updateNeighborsAt(p_176262_2_.relative(p_176262_3_.getOpposite()), this);
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && !p_196243_1_.is(p_196243_4_.getBlock())) {
         boolean flag = p_196243_1_.getValue(ATTACHED);
         boolean flag1 = p_196243_1_.getValue(POWERED);
         if (flag || flag1) {
            this.calculateState(p_196243_2_, p_196243_3_, p_196243_1_, true, false, -1, (BlockState)null);
         }

         if (flag1) {
            p_196243_2_.updateNeighborsAt(p_196243_3_, this);
            p_196243_2_.updateNeighborsAt(p_196243_3_.relative(p_196243_1_.getValue(FACING).getOpposite()), this);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      if (!p_176211_1_.getValue(POWERED)) {
         return 0;
      } else {
         return p_176211_1_.getValue(FACING) == p_176211_4_ ? 15 : 0;
      }
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED, ATTACHED);
   }
}
