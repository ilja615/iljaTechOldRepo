package net.minecraft.block;

import java.util.Random;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class NetherrackBlock extends Block implements IGrowable {
   public NetherrackBlock(AbstractBlock.Properties p_i241183_1_) {
      super(p_i241183_1_);
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      if (!p_176473_1_.getBlockState(p_176473_2_.above()).propagatesSkylightDown(p_176473_1_, p_176473_2_)) {
         return false;
      } else {
         for(BlockPos blockpos : BlockPos.betweenClosed(p_176473_2_.offset(-1, -1, -1), p_176473_2_.offset(1, 1, 1))) {
            if (p_176473_1_.getBlockState(blockpos).is(BlockTags.NYLIUM)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      boolean flag = false;
      boolean flag1 = false;

      for(BlockPos blockpos : BlockPos.betweenClosed(p_225535_3_.offset(-1, -1, -1), p_225535_3_.offset(1, 1, 1))) {
         BlockState blockstate = p_225535_1_.getBlockState(blockpos);
         if (blockstate.is(Blocks.WARPED_NYLIUM)) {
            flag1 = true;
         }

         if (blockstate.is(Blocks.CRIMSON_NYLIUM)) {
            flag = true;
         }

         if (flag1 && flag) {
            break;
         }
      }

      if (flag1 && flag) {
         p_225535_1_.setBlock(p_225535_3_, p_225535_2_.nextBoolean() ? Blocks.WARPED_NYLIUM.defaultBlockState() : Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
      } else if (flag1) {
         p_225535_1_.setBlock(p_225535_3_, Blocks.WARPED_NYLIUM.defaultBlockState(), 3);
      } else if (flag) {
         p_225535_1_.setBlock(p_225535_3_, Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
      }

   }
}
