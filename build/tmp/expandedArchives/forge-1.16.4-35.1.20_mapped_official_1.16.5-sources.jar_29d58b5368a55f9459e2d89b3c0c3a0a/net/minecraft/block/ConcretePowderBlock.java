package net.minecraft.block;

import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ConcretePowderBlock extends FallingBlock {
   private final BlockState concrete;

   public ConcretePowderBlock(Block p_i48423_1_, AbstractBlock.Properties p_i48423_2_) {
      super(p_i48423_2_);
      this.concrete = p_i48423_1_.defaultBlockState();
   }

   public void onLand(World p_176502_1_, BlockPos p_176502_2_, BlockState p_176502_3_, BlockState p_176502_4_, FallingBlockEntity p_176502_5_) {
      if (shouldSolidify(p_176502_1_, p_176502_2_, p_176502_4_)) {
         p_176502_1_.setBlock(p_176502_2_, this.concrete, 3);
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getLevel();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      BlockState blockstate = iblockreader.getBlockState(blockpos);
      return shouldSolidify(iblockreader, blockpos, blockstate) ? this.concrete : super.getStateForPlacement(p_196258_1_);
   }

   private static boolean shouldSolidify(IBlockReader p_230137_0_, BlockPos p_230137_1_, BlockState p_230137_2_) {
      return canSolidify(p_230137_2_) || touchesLiquid(p_230137_0_, p_230137_1_);
   }

   private static boolean touchesLiquid(IBlockReader p_196441_0_, BlockPos p_196441_1_) {
      boolean flag = false;
      BlockPos.Mutable blockpos$mutable = p_196441_1_.mutable();

      for(Direction direction : Direction.values()) {
         BlockState blockstate = p_196441_0_.getBlockState(blockpos$mutable);
         if (direction != Direction.DOWN || canSolidify(blockstate)) {
            blockpos$mutable.setWithOffset(p_196441_1_, direction);
            blockstate = p_196441_0_.getBlockState(blockpos$mutable);
            if (canSolidify(blockstate) && !blockstate.isFaceSturdy(p_196441_0_, p_196441_1_, direction.getOpposite())) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private static boolean canSolidify(BlockState p_212566_0_) {
      return p_212566_0_.getFluidState().is(FluidTags.WATER);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return touchesLiquid(p_196271_4_, p_196271_5_) ? this.concrete : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState p_189876_1_, IBlockReader p_189876_2_, BlockPos p_189876_3_) {
      return p_189876_1_.getMapColor(p_189876_2_, p_189876_3_).col;
   }
}
