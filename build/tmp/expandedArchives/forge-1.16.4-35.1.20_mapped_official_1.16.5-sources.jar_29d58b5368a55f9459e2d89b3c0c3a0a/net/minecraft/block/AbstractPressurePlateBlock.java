package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractPressurePlateBlock extends Block {
   protected static final VoxelShape PRESSED_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
   protected static final AxisAlignedBB TOUCH_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

   protected AbstractPressurePlateBlock(AbstractBlock.Properties p_i48445_1_) {
      super(p_i48445_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.getSignalForState(p_220053_1_) > 0 ? PRESSED_AABB : AABB;
   }

   protected int getPressedTime() {
      return 20;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.below();
      return canSupportRigidBlock(p_196260_2_, blockpos) || canSupportCenter(p_196260_2_, blockpos, Direction.UP);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      int i = this.getSignalForState(p_225534_1_);
      if (i > 0) {
         this.checkPressed(p_225534_2_, p_225534_3_, p_225534_1_, i);
      }

   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isClientSide) {
         int i = this.getSignalForState(p_196262_1_);
         if (i == 0) {
            this.checkPressed(p_196262_2_, p_196262_3_, p_196262_1_, i);
         }

      }
   }

   protected void checkPressed(World p_180666_1_, BlockPos p_180666_2_, BlockState p_180666_3_, int p_180666_4_) {
      int i = this.getSignalStrength(p_180666_1_, p_180666_2_);
      boolean flag = p_180666_4_ > 0;
      boolean flag1 = i > 0;
      if (p_180666_4_ != i) {
         BlockState blockstate = this.setSignalForState(p_180666_3_, i);
         p_180666_1_.setBlock(p_180666_2_, blockstate, 2);
         this.updateNeighbours(p_180666_1_, p_180666_2_);
         p_180666_1_.setBlocksDirty(p_180666_2_, p_180666_3_, blockstate);
      }

      if (!flag1 && flag) {
         this.playOffSound(p_180666_1_, p_180666_2_);
      } else if (flag1 && !flag) {
         this.playOnSound(p_180666_1_, p_180666_2_);
      }

      if (flag1) {
         p_180666_1_.getBlockTicks().scheduleTick(new BlockPos(p_180666_2_), this, this.getPressedTime());
      }

   }

   protected abstract void playOnSound(IWorld p_185507_1_, BlockPos p_185507_2_);

   protected abstract void playOffSound(IWorld p_185508_1_, BlockPos p_185508_2_);

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && !p_196243_1_.is(p_196243_4_.getBlock())) {
         if (this.getSignalForState(p_196243_1_) > 0) {
            this.updateNeighbours(p_196243_2_, p_196243_3_);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   protected void updateNeighbours(World p_176578_1_, BlockPos p_176578_2_) {
      p_176578_1_.updateNeighborsAt(p_176578_2_, this);
      p_176578_1_.updateNeighborsAt(p_176578_2_.below(), this);
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return this.getSignalForState(p_180656_1_);
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_4_ == Direction.UP ? this.getSignalForState(p_176211_1_) : 0;
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   protected abstract int getSignalStrength(World p_180669_1_, BlockPos p_180669_2_);

   protected abstract int getSignalForState(BlockState p_176576_1_);

   protected abstract BlockState setSignalForState(BlockState p_176575_1_, int p_176575_2_);
}
