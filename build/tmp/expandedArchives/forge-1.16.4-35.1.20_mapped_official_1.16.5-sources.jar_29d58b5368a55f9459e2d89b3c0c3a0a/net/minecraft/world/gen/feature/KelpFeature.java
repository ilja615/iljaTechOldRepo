package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpTopBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class KelpFeature extends Feature<NoFeatureConfig> {
   public KelpFeature(Codec<NoFeatureConfig> p_i231967_1_) {
      super(p_i231967_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
      int i = 0;
      int j = p_241855_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_241855_4_.getX(), p_241855_4_.getZ());
      BlockPos blockpos = new BlockPos(p_241855_4_.getX(), j, p_241855_4_.getZ());
      if (p_241855_1_.getBlockState(blockpos).is(Blocks.WATER)) {
         BlockState blockstate = Blocks.KELP.defaultBlockState();
         BlockState blockstate1 = Blocks.KELP_PLANT.defaultBlockState();
         int k = 1 + p_241855_3_.nextInt(10);

         for(int l = 0; l <= k; ++l) {
            if (p_241855_1_.getBlockState(blockpos).is(Blocks.WATER) && p_241855_1_.getBlockState(blockpos.above()).is(Blocks.WATER) && blockstate1.canSurvive(p_241855_1_, blockpos)) {
               if (l == k) {
                  p_241855_1_.setBlock(blockpos, blockstate.setValue(KelpTopBlock.AGE, Integer.valueOf(p_241855_3_.nextInt(4) + 20)), 2);
                  ++i;
               } else {
                  p_241855_1_.setBlock(blockpos, blockstate1, 2);
               }
            } else if (l > 0) {
               BlockPos blockpos1 = blockpos.below();
               if (blockstate.canSurvive(p_241855_1_, blockpos1) && !p_241855_1_.getBlockState(blockpos1.below()).is(Blocks.KELP)) {
                  p_241855_1_.setBlock(blockpos1, blockstate.setValue(KelpTopBlock.AGE, Integer.valueOf(p_241855_3_.nextInt(4) + 20)), 2);
                  ++i;
               }
               break;
            }

            blockpos = blockpos.above();
         }
      }

      return i > 0;
   }
}
