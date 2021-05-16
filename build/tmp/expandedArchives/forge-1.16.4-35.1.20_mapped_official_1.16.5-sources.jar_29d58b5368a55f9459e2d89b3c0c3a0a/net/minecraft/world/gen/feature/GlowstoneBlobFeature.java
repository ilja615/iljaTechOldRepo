package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class GlowstoneBlobFeature extends Feature<NoFeatureConfig> {
   public GlowstoneBlobFeature(Codec<NoFeatureConfig> p_i231956_1_) {
      super(p_i231956_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
      if (!p_241855_1_.isEmptyBlock(p_241855_4_)) {
         return false;
      } else {
         BlockState blockstate = p_241855_1_.getBlockState(p_241855_4_.above());
         if (!blockstate.is(Blocks.NETHERRACK) && !blockstate.is(Blocks.BASALT) && !blockstate.is(Blocks.BLACKSTONE)) {
            return false;
         } else {
            p_241855_1_.setBlock(p_241855_4_, Blocks.GLOWSTONE.defaultBlockState(), 2);

            for(int i = 0; i < 1500; ++i) {
               BlockPos blockpos = p_241855_4_.offset(p_241855_3_.nextInt(8) - p_241855_3_.nextInt(8), -p_241855_3_.nextInt(12), p_241855_3_.nextInt(8) - p_241855_3_.nextInt(8));
               if (p_241855_1_.getBlockState(blockpos).isAir(p_241855_1_, blockpos)) {
                  int j = 0;

                  for(Direction direction : Direction.values()) {
                     if (p_241855_1_.getBlockState(blockpos.relative(direction)).is(Blocks.GLOWSTONE)) {
                        ++j;
                     }

                     if (j > 1) {
                        break;
                     }
                  }

                  if (j == 1) {
                     p_241855_1_.setBlock(blockpos, Blocks.GLOWSTONE.defaultBlockState(), 2);
                  }
               }
            }

            return true;
         }
      }
   }
}
