package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractPlantBlock extends Block {
   protected final Direction growthDirection;
   protected final boolean scheduleFluidTicks;
   protected final VoxelShape shape;

   protected AbstractPlantBlock(AbstractBlock.Properties p_i241178_1_, Direction p_i241178_2_, VoxelShape p_i241178_3_, boolean p_i241178_4_) {
      super(p_i241178_1_);
      this.growthDirection = p_i241178_2_;
      this.shape = p_i241178_3_;
      this.scheduleFluidTicks = p_i241178_4_;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos().relative(this.growthDirection));
      return !blockstate.is(this.getHeadBlock()) && !blockstate.is(this.getBodyBlock()) ? this.getStateForPlacement(p_196258_1_.getLevel()) : this.getBodyBlock().defaultBlockState();
   }

   public BlockState getStateForPlacement(IWorld p_235504_1_) {
      return this.defaultBlockState();
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.relative(this.growthDirection.getOpposite());
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (!this.canAttachToBlock(block)) {
         return false;
      } else {
         return block == this.getHeadBlock() || block == this.getBodyBlock() || blockstate.isFaceSturdy(p_196260_2_, blockpos, this.growthDirection);
      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      }

   }

   protected boolean canAttachToBlock(Block p_230333_1_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.shape;
   }

   protected abstract AbstractTopPlantBlock getHeadBlock();

   protected abstract Block getBodyBlock();
}
