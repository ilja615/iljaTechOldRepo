package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class RedstoneDiodeBlock extends HorizontalBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected RedstoneDiodeBlock(AbstractBlock.Properties p_i48416_1_) {
      super(p_i48416_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return canSupportRigidBlock(p_196260_2_, p_196260_3_.below());
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!this.isLocked(p_225534_2_, p_225534_3_, p_225534_1_)) {
         boolean flag = p_225534_1_.getValue(POWERED);
         boolean flag1 = this.shouldTurnOn(p_225534_2_, p_225534_3_, p_225534_1_);
         if (flag && !flag1) {
            p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag) {
            p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(POWERED, Boolean.valueOf(true)), 2);
            if (!flag1) {
               p_225534_2_.getBlockTicks().scheduleTick(p_225534_3_, this, this.getDelay(p_225534_1_), TickPriority.VERY_HIGH);
            }
         }

      }
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_1_.getSignal(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      if (!p_180656_1_.getValue(POWERED)) {
         return 0;
      } else {
         return p_180656_1_.getValue(FACING) == p_180656_4_ ? this.getOutputSignal(p_180656_2_, p_180656_3_, p_180656_1_) : 0;
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_1_.canSurvive(p_220069_2_, p_220069_3_)) {
         this.checkTickOnNeighbor(p_220069_2_, p_220069_3_, p_220069_1_);
      } else {
         TileEntity tileentity = p_220069_1_.hasTileEntity() ? p_220069_2_.getBlockEntity(p_220069_3_) : null;
         dropResources(p_220069_1_, p_220069_2_, p_220069_3_, tileentity);
         p_220069_2_.removeBlock(p_220069_3_, false);

         for(Direction direction : Direction.values()) {
            p_220069_2_.updateNeighborsAt(p_220069_3_.relative(direction), this);
         }

      }
   }

   protected void checkTickOnNeighbor(World p_176398_1_, BlockPos p_176398_2_, BlockState p_176398_3_) {
      if (!this.isLocked(p_176398_1_, p_176398_2_, p_176398_3_)) {
         boolean flag = p_176398_3_.getValue(POWERED);
         boolean flag1 = this.shouldTurnOn(p_176398_1_, p_176398_2_, p_176398_3_);
         if (flag != flag1 && !p_176398_1_.getBlockTicks().willTickThisTick(p_176398_2_, this)) {
            TickPriority tickpriority = TickPriority.HIGH;
            if (this.shouldPrioritize(p_176398_1_, p_176398_2_, p_176398_3_)) {
               tickpriority = TickPriority.EXTREMELY_HIGH;
            } else if (flag) {
               tickpriority = TickPriority.VERY_HIGH;
            }

            p_176398_1_.getBlockTicks().scheduleTick(p_176398_2_, this, this.getDelay(p_176398_3_), tickpriority);
         }

      }
   }

   public boolean isLocked(IWorldReader p_176405_1_, BlockPos p_176405_2_, BlockState p_176405_3_) {
      return false;
   }

   protected boolean shouldTurnOn(World p_176404_1_, BlockPos p_176404_2_, BlockState p_176404_3_) {
      return this.getInputSignal(p_176404_1_, p_176404_2_, p_176404_3_) > 0;
   }

   protected int getInputSignal(World p_176397_1_, BlockPos p_176397_2_, BlockState p_176397_3_) {
      Direction direction = p_176397_3_.getValue(FACING);
      BlockPos blockpos = p_176397_2_.relative(direction);
      int i = p_176397_1_.getSignal(blockpos, direction);
      if (i >= 15) {
         return i;
      } else {
         BlockState blockstate = p_176397_1_.getBlockState(blockpos);
         return Math.max(i, blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedstoneWireBlock.POWER) : 0);
      }
   }

   protected int getAlternateSignal(IWorldReader p_176407_1_, BlockPos p_176407_2_, BlockState p_176407_3_) {
      Direction direction = p_176407_3_.getValue(FACING);
      Direction direction1 = direction.getClockWise();
      Direction direction2 = direction.getCounterClockWise();
      return Math.max(this.getAlternateSignalAt(p_176407_1_, p_176407_2_.relative(direction1), direction1), this.getAlternateSignalAt(p_176407_1_, p_176407_2_.relative(direction2), direction2));
   }

   protected int getAlternateSignalAt(IWorldReader p_176401_1_, BlockPos p_176401_2_, Direction p_176401_3_) {
      BlockState blockstate = p_176401_1_.getBlockState(p_176401_2_);
      if (this.isAlternateInput(blockstate)) {
         if (blockstate.is(Blocks.REDSTONE_BLOCK)) {
            return 15;
         } else {
            return blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedstoneWireBlock.POWER) : p_176401_1_.getDirectSignal(p_176401_2_, p_176401_3_);
         }
      } else {
         return 0;
      }
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (this.shouldTurnOn(p_180633_1_, p_180633_2_, p_180633_3_)) {
         p_180633_1_.getBlockTicks().scheduleTick(p_180633_2_, this, 1);
      }

   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      this.updateNeighborsInFront(p_220082_2_, p_220082_3_, p_220082_1_);
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && !p_196243_1_.is(p_196243_4_.getBlock())) {
         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         this.updateNeighborsInFront(p_196243_2_, p_196243_3_, p_196243_1_);
      }
   }

   protected void updateNeighborsInFront(World p_176400_1_, BlockPos p_176400_2_, BlockState p_176400_3_) {
      Direction direction = p_176400_3_.getValue(FACING);
      BlockPos blockpos = p_176400_2_.relative(direction.getOpposite());
      if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(p_176400_1_, p_176400_2_, p_176400_1_.getBlockState(p_176400_2_), java.util.EnumSet.of(direction.getOpposite()), false).isCanceled())
         return;
      p_176400_1_.neighborChanged(blockpos, this, p_176400_2_);
      p_176400_1_.updateNeighborsAtExceptFromFacing(blockpos, this, direction);
   }

   protected boolean isAlternateInput(BlockState p_185545_1_) {
      return p_185545_1_.isSignalSource();
   }

   protected int getOutputSignal(IBlockReader p_176408_1_, BlockPos p_176408_2_, BlockState p_176408_3_) {
      return 15;
   }

   public static boolean isDiode(BlockState p_185546_0_) {
      return p_185546_0_.getBlock() instanceof RedstoneDiodeBlock;
   }

   public boolean shouldPrioritize(IBlockReader p_176402_1_, BlockPos p_176402_2_, BlockState p_176402_3_) {
      Direction direction = p_176402_3_.getValue(FACING).getOpposite();
      BlockState blockstate = p_176402_1_.getBlockState(p_176402_2_.relative(direction));
      return isDiode(blockstate) && blockstate.getValue(FACING) != direction;
   }

   protected abstract int getDelay(BlockState p_196346_1_);
}
