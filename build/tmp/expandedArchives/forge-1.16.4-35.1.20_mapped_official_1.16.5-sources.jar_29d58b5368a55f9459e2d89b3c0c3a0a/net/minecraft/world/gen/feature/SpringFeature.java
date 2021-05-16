package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class SpringFeature extends Feature<LiquidsConfig> {
   public SpringFeature(Codec<LiquidsConfig> p_i231995_1_) {
      super(p_i231995_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, LiquidsConfig p_241855_5_) {
      if (!p_241855_5_.validBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.above()).getBlock())) {
         return false;
      } else if (p_241855_5_.requiresBlockBelow && !p_241855_5_.validBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.below()).getBlock())) {
         return false;
      } else {
         BlockState blockstate = p_241855_1_.getBlockState(p_241855_4_);
         if (!blockstate.isAir(p_241855_1_, p_241855_4_) && !p_241855_5_.validBlocks.contains(blockstate.getBlock())) {
            return false;
         } else {
            int i = 0;
            int j = 0;
            if (p_241855_5_.validBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.west()).getBlock())) {
               ++j;
            }

            if (p_241855_5_.validBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.east()).getBlock())) {
               ++j;
            }

            if (p_241855_5_.validBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.north()).getBlock())) {
               ++j;
            }

            if (p_241855_5_.validBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.south()).getBlock())) {
               ++j;
            }

            if (p_241855_5_.validBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.below()).getBlock())) {
               ++j;
            }

            int k = 0;
            if (p_241855_1_.isEmptyBlock(p_241855_4_.west())) {
               ++k;
            }

            if (p_241855_1_.isEmptyBlock(p_241855_4_.east())) {
               ++k;
            }

            if (p_241855_1_.isEmptyBlock(p_241855_4_.north())) {
               ++k;
            }

            if (p_241855_1_.isEmptyBlock(p_241855_4_.south())) {
               ++k;
            }

            if (p_241855_1_.isEmptyBlock(p_241855_4_.below())) {
               ++k;
            }

            if (j == p_241855_5_.rockCount && k == p_241855_5_.holeCount) {
               p_241855_1_.setBlock(p_241855_4_, p_241855_5_.state.createLegacyBlock(), 2);
               p_241855_1_.getLiquidTicks().scheduleTick(p_241855_4_, p_241855_5_.state.getType(), 0);
               ++i;
            }

            return i > 0;
         }
      }
   }
}
