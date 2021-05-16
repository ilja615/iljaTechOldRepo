package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class BlockBlobFeature extends Feature<BlockStateFeatureConfig> {
   public BlockBlobFeature(Codec<BlockStateFeatureConfig> p_i231931_1_) {
      super(p_i231931_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlockStateFeatureConfig p_241855_5_) {
      while(true) {
         label46: {
            if (p_241855_4_.getY() > 3) {
               if (p_241855_1_.isEmptyBlock(p_241855_4_.below())) {
                  break label46;
               }

               Block block = p_241855_1_.getBlockState(p_241855_4_.below()).getBlock();
               if (!isDirt(block) && !isStone(block)) {
                  break label46;
               }
            }

            if (p_241855_4_.getY() <= 3) {
               return false;
            }

            for(int l = 0; l < 3; ++l) {
               int i = p_241855_3_.nextInt(2);
               int j = p_241855_3_.nextInt(2);
               int k = p_241855_3_.nextInt(2);
               float f = (float)(i + j + k) * 0.333F + 0.5F;

               for(BlockPos blockpos : BlockPos.betweenClosed(p_241855_4_.offset(-i, -j, -k), p_241855_4_.offset(i, j, k))) {
                  if (blockpos.distSqr(p_241855_4_) <= (double)(f * f)) {
                     p_241855_1_.setBlock(blockpos, p_241855_5_.state, 4);
                  }
               }

               p_241855_4_ = p_241855_4_.offset(-1 + p_241855_3_.nextInt(2), -p_241855_3_.nextInt(2), -1 + p_241855_3_.nextInt(2));
            }

            return true;
         }

         p_241855_4_ = p_241855_4_.below();
      }
   }
}
