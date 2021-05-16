package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractBodyPlantBlock extends AbstractPlantBlock implements IGrowable {
   protected AbstractBodyPlantBlock(AbstractBlock.Properties p_i241179_1_, Direction p_i241179_2_, VoxelShape p_i241179_3_, boolean p_i241179_4_) {
      super(p_i241179_1_, p_i241179_2_, p_i241179_3_, p_i241179_4_);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == this.growthDirection.getOpposite() && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      AbstractTopPlantBlock abstracttopplantblock = this.getHeadBlock();
      if (p_196271_2_ == this.growthDirection) {
         Block block = p_196271_3_.getBlock();
         if (block != this && block != abstracttopplantblock) {
            return abstracttopplantblock.getStateForPlacement(p_196271_4_);
         }
      }

      if (this.scheduleFluidTicks) {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(this.getHeadBlock());
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      Optional<BlockPos> optional = this.getHeadPos(p_176473_1_, p_176473_2_, p_176473_3_);
      return optional.isPresent() && this.getHeadBlock().canGrowInto(p_176473_1_.getBlockState(optional.get().relative(this.growthDirection)));
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      Optional<BlockPos> optional = this.getHeadPos(p_225535_1_, p_225535_3_, p_225535_4_);
      if (optional.isPresent()) {
         BlockState blockstate = p_225535_1_.getBlockState(optional.get());
         ((AbstractTopPlantBlock)blockstate.getBlock()).performBonemeal(p_225535_1_, p_225535_2_, optional.get(), blockstate);
      }

   }

   private Optional<BlockPos> getHeadPos(IBlockReader p_235501_1_, BlockPos p_235501_2_, BlockState p_235501_3_) {
      BlockPos blockpos = p_235501_2_;

      Block block;
      do {
         blockpos = blockpos.relative(this.growthDirection);
         block = p_235501_1_.getBlockState(blockpos).getBlock();
      } while(block == p_235501_3_.getBlock());

      return block == this.getHeadBlock() ? Optional.of(blockpos) : Optional.empty();
   }

   public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      boolean flag = super.canBeReplaced(p_196253_1_, p_196253_2_);
      return flag && p_196253_2_.getItemInHand().getItem() == this.getHeadBlock().asItem() ? false : flag;
   }

   protected Block getBodyBlock() {
      return this;
   }
}
