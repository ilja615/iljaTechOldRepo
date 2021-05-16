package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.ComparatorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ComparatorBlock extends RedstoneDiodeBlock implements ITileEntityProvider {
   public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.MODE_COMPARATOR;

   public ComparatorBlock(AbstractBlock.Properties p_i48424_1_) {
      super(p_i48424_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(BlockState p_196346_1_) {
      return 2;
   }

   protected int getOutputSignal(IBlockReader p_176408_1_, BlockPos p_176408_2_, BlockState p_176408_3_) {
      TileEntity tileentity = p_176408_1_.getBlockEntity(p_176408_2_);
      return tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
   }

   private int calculateOutputSignal(World p_176460_1_, BlockPos p_176460_2_, BlockState p_176460_3_) {
      return p_176460_3_.getValue(MODE) == ComparatorMode.SUBTRACT ? Math.max(this.getInputSignal(p_176460_1_, p_176460_2_, p_176460_3_) - this.getAlternateSignal(p_176460_1_, p_176460_2_, p_176460_3_), 0) : this.getInputSignal(p_176460_1_, p_176460_2_, p_176460_3_);
   }

   protected boolean shouldTurnOn(World p_176404_1_, BlockPos p_176404_2_, BlockState p_176404_3_) {
      int i = this.getInputSignal(p_176404_1_, p_176404_2_, p_176404_3_);
      if (i == 0) {
         return false;
      } else {
         int j = this.getAlternateSignal(p_176404_1_, p_176404_2_, p_176404_3_);
         if (i > j) {
            return true;
         } else {
            return i == j && p_176404_3_.getValue(MODE) == ComparatorMode.COMPARE;
         }
      }
   }

   protected int getInputSignal(World p_176397_1_, BlockPos p_176397_2_, BlockState p_176397_3_) {
      int i = super.getInputSignal(p_176397_1_, p_176397_2_, p_176397_3_);
      Direction direction = p_176397_3_.getValue(FACING);
      BlockPos blockpos = p_176397_2_.relative(direction);
      BlockState blockstate = p_176397_1_.getBlockState(blockpos);
      if (blockstate.hasAnalogOutputSignal()) {
         i = blockstate.getAnalogOutputSignal(p_176397_1_, blockpos);
      } else if (i < 15 && blockstate.isRedstoneConductor(p_176397_1_, blockpos)) {
         blockpos = blockpos.relative(direction);
         blockstate = p_176397_1_.getBlockState(blockpos);
         ItemFrameEntity itemframeentity = this.getItemFrame(p_176397_1_, direction, blockpos);
         int j = Math.max(itemframeentity == null ? Integer.MIN_VALUE : itemframeentity.getAnalogOutput(), blockstate.hasAnalogOutputSignal() ? blockstate.getAnalogOutputSignal(p_176397_1_, blockpos) : Integer.MIN_VALUE);
         if (j != Integer.MIN_VALUE) {
            i = j;
         }
      }

      return i;
   }

   @Nullable
   private ItemFrameEntity getItemFrame(World p_176461_1_, Direction p_176461_2_, BlockPos p_176461_3_) {
      List<ItemFrameEntity> list = p_176461_1_.getEntitiesOfClass(ItemFrameEntity.class, new AxisAlignedBB((double)p_176461_3_.getX(), (double)p_176461_3_.getY(), (double)p_176461_3_.getZ(), (double)(p_176461_3_.getX() + 1), (double)(p_176461_3_.getY() + 1), (double)(p_176461_3_.getZ() + 1)), (p_210304_1_) -> {
         return p_210304_1_ != null && p_210304_1_.getDirection() == p_176461_2_;
      });
      return list.size() == 1 ? list.get(0) : null;
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (!p_225533_4_.abilities.mayBuild) {
         return ActionResultType.PASS;
      } else {
         p_225533_1_ = p_225533_1_.cycle(MODE);
         float f = p_225533_1_.getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         p_225533_2_.playSound(p_225533_4_, p_225533_3_, SoundEvents.COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         p_225533_2_.setBlock(p_225533_3_, p_225533_1_, 2);
         this.refreshOutputState(p_225533_2_, p_225533_3_, p_225533_1_);
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      }
   }

   protected void checkTickOnNeighbor(World p_176398_1_, BlockPos p_176398_2_, BlockState p_176398_3_) {
      if (!p_176398_1_.getBlockTicks().willTickThisTick(p_176398_2_, this)) {
         int i = this.calculateOutputSignal(p_176398_1_, p_176398_2_, p_176398_3_);
         TileEntity tileentity = p_176398_1_.getBlockEntity(p_176398_2_);
         int j = tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
         if (i != j || p_176398_3_.getValue(POWERED) != this.shouldTurnOn(p_176398_1_, p_176398_2_, p_176398_3_)) {
            TickPriority tickpriority = this.shouldPrioritize(p_176398_1_, p_176398_2_, p_176398_3_) ? TickPriority.HIGH : TickPriority.NORMAL;
            p_176398_1_.getBlockTicks().scheduleTick(p_176398_2_, this, 2, tickpriority);
         }

      }
   }

   private void refreshOutputState(World p_176462_1_, BlockPos p_176462_2_, BlockState p_176462_3_) {
      int i = this.calculateOutputSignal(p_176462_1_, p_176462_2_, p_176462_3_);
      TileEntity tileentity = p_176462_1_.getBlockEntity(p_176462_2_);
      int j = 0;
      if (tileentity instanceof ComparatorTileEntity) {
         ComparatorTileEntity comparatortileentity = (ComparatorTileEntity)tileentity;
         j = comparatortileentity.getOutputSignal();
         comparatortileentity.setOutputSignal(i);
      }

      if (j != i || p_176462_3_.getValue(MODE) == ComparatorMode.COMPARE) {
         boolean flag1 = this.shouldTurnOn(p_176462_1_, p_176462_2_, p_176462_3_);
         boolean flag = p_176462_3_.getValue(POWERED);
         if (flag && !flag1) {
            p_176462_1_.setBlock(p_176462_2_, p_176462_3_.setValue(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag && flag1) {
            p_176462_1_.setBlock(p_176462_2_, p_176462_3_.setValue(POWERED, Boolean.valueOf(true)), 2);
         }

         this.updateNeighborsInFront(p_176462_1_, p_176462_2_, p_176462_3_);
      }

   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      this.refreshOutputState(p_225534_2_, p_225534_3_, p_225534_1_);
   }

   public boolean triggerEvent(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      super.triggerEvent(p_189539_1_, p_189539_2_, p_189539_3_, p_189539_4_, p_189539_5_);
      TileEntity tileentity = p_189539_2_.getBlockEntity(p_189539_3_);
      return tileentity != null && tileentity.triggerEvent(p_189539_4_, p_189539_5_);
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new ComparatorTileEntity();
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, MODE, POWERED);
   }

   @Override
   public boolean getWeakChanges(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos) {
      return state.is(Blocks.COMPARATOR);
   }

   @Override
   public void onNeighborChange(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, BlockPos neighbor) {
      if (pos.getY() == neighbor.getY() && world instanceof World && !((World)world).isClientSide()) {
         state.neighborChanged((World)world, pos, world.getBlockState(neighbor).getBlock(), neighbor, false);
      }
   }
}
