package net.minecraft.block;

import java.util.Random;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public abstract class SpreadableSnowyDirtBlock extends SnowyDirtBlock {
   protected SpreadableSnowyDirtBlock(AbstractBlock.Properties p_i48324_1_) {
      super(p_i48324_1_);
   }

   private static boolean canBeGrass(BlockState p_220257_0_, IWorldReader p_220257_1_, BlockPos p_220257_2_) {
      BlockPos blockpos = p_220257_2_.above();
      BlockState blockstate = p_220257_1_.getBlockState(blockpos);
      if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowBlock.LAYERS) == 1) {
         return true;
      } else if (blockstate.getFluidState().getAmount() == 8) {
         return false;
      } else {
         int i = LightEngine.getLightBlockInto(p_220257_1_, p_220257_0_, p_220257_2_, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(p_220257_1_, blockpos));
         return i < p_220257_1_.getMaxLightLevel();
      }
   }

   private static boolean canPropagate(BlockState p_220256_0_, IWorldReader p_220256_1_, BlockPos p_220256_2_) {
      BlockPos blockpos = p_220256_2_.above();
      return canBeGrass(p_220256_0_, p_220256_1_, p_220256_2_) && !p_220256_1_.getFluidState(blockpos).is(FluidTags.WATER);
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (!canBeGrass(p_225542_1_, p_225542_2_, p_225542_3_)) {
         if (!p_225542_2_.isAreaLoaded(p_225542_3_, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
         p_225542_2_.setBlockAndUpdate(p_225542_3_, Blocks.DIRT.defaultBlockState());
      } else {
         if (p_225542_2_.getMaxLocalRawBrightness(p_225542_3_.above()) >= 9) {
            BlockState blockstate = this.defaultBlockState();

            for(int i = 0; i < 4; ++i) {
               BlockPos blockpos = p_225542_3_.offset(p_225542_4_.nextInt(3) - 1, p_225542_4_.nextInt(5) - 3, p_225542_4_.nextInt(3) - 1);
               if (p_225542_2_.getBlockState(blockpos).is(Blocks.DIRT) && canPropagate(blockstate, p_225542_2_, blockpos)) {
                  p_225542_2_.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, Boolean.valueOf(p_225542_2_.getBlockState(blockpos.above()).is(Blocks.SNOW))));
               }
            }
         }

      }
   }
}
