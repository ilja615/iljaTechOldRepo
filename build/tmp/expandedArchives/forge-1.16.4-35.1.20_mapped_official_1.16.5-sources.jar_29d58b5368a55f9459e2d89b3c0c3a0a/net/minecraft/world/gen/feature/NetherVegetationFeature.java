package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class NetherVegetationFeature extends Feature<BlockStateProvidingFeatureConfig> {
   public NetherVegetationFeature(Codec<BlockStateProvidingFeatureConfig> p_i231971_1_) {
      super(p_i231971_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlockStateProvidingFeatureConfig p_241855_5_) {
      return place(p_241855_1_, p_241855_3_, p_241855_4_, p_241855_5_, 8, 4);
   }

   public static boolean place(IWorld p_236325_0_, Random p_236325_1_, BlockPos p_236325_2_, BlockStateProvidingFeatureConfig p_236325_3_, int p_236325_4_, int p_236325_5_) {
      Block block = p_236325_0_.getBlockState(p_236325_2_.below()).getBlock();
      if (!block.is(BlockTags.NYLIUM)) {
         return false;
      } else {
         int i = p_236325_2_.getY();
         if (i >= 1 && i + 1 < 256) {
            int j = 0;

            for(int k = 0; k < p_236325_4_ * p_236325_4_; ++k) {
               BlockPos blockpos = p_236325_2_.offset(p_236325_1_.nextInt(p_236325_4_) - p_236325_1_.nextInt(p_236325_4_), p_236325_1_.nextInt(p_236325_5_) - p_236325_1_.nextInt(p_236325_5_), p_236325_1_.nextInt(p_236325_4_) - p_236325_1_.nextInt(p_236325_4_));
               BlockState blockstate = p_236325_3_.stateProvider.getState(p_236325_1_, blockpos);
               if (p_236325_0_.isEmptyBlock(blockpos) && blockpos.getY() > 0 && blockstate.canSurvive(p_236325_0_, blockpos)) {
                  p_236325_0_.setBlock(blockpos, blockstate, 2);
                  ++j;
               }
            }

            return j > 0;
         } else {
            return false;
         }
      }
   }
}
