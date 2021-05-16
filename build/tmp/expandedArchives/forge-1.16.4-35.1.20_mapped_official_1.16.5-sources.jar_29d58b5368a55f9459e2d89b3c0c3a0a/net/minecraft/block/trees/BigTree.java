package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public abstract class BigTree extends Tree {
   public boolean growTree(ServerWorld p_230339_1_, ChunkGenerator p_230339_2_, BlockPos p_230339_3_, BlockState p_230339_4_, Random p_230339_5_) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (isTwoByTwoSapling(p_230339_4_, p_230339_1_, p_230339_3_, i, j)) {
               return this.placeMega(p_230339_1_, p_230339_2_, p_230339_3_, p_230339_4_, p_230339_5_, i, j);
            }
         }
      }

      return super.growTree(p_230339_1_, p_230339_2_, p_230339_3_, p_230339_4_, p_230339_5_);
   }

   @Nullable
   protected abstract ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredMegaFeature(Random p_225547_1_);

   public boolean placeMega(ServerWorld p_235678_1_, ChunkGenerator p_235678_2_, BlockPos p_235678_3_, BlockState p_235678_4_, Random p_235678_5_, int p_235678_6_, int p_235678_7_) {
      ConfiguredFeature<BaseTreeFeatureConfig, ?> configuredfeature = this.getConfiguredMegaFeature(p_235678_5_);
      if (configuredfeature == null) {
         return false;
      } else {
         configuredfeature.config.setFromSapling();
         BlockState blockstate = Blocks.AIR.defaultBlockState();
         p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_, 0, p_235678_7_), blockstate, 4);
         p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_ + 1, 0, p_235678_7_), blockstate, 4);
         p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_, 0, p_235678_7_ + 1), blockstate, 4);
         p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_ + 1, 0, p_235678_7_ + 1), blockstate, 4);
         if (configuredfeature.place(p_235678_1_, p_235678_2_, p_235678_5_, p_235678_3_.offset(p_235678_6_, 0, p_235678_7_))) {
            return true;
         } else {
            p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_, 0, p_235678_7_), p_235678_4_, 4);
            p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_ + 1, 0, p_235678_7_), p_235678_4_, 4);
            p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_, 0, p_235678_7_ + 1), p_235678_4_, 4);
            p_235678_1_.setBlock(p_235678_3_.offset(p_235678_6_ + 1, 0, p_235678_7_ + 1), p_235678_4_, 4);
            return false;
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState p_196937_0_, IBlockReader p_196937_1_, BlockPos p_196937_2_, int p_196937_3_, int p_196937_4_) {
      Block block = p_196937_0_.getBlock();
      return block == p_196937_1_.getBlockState(p_196937_2_.offset(p_196937_3_, 0, p_196937_4_)).getBlock() && block == p_196937_1_.getBlockState(p_196937_2_.offset(p_196937_3_ + 1, 0, p_196937_4_)).getBlock() && block == p_196937_1_.getBlockState(p_196937_2_.offset(p_196937_3_, 0, p_196937_4_ + 1)).getBlock() && block == p_196937_1_.getBlockState(p_196937_2_.offset(p_196937_3_ + 1, 0, p_196937_4_ + 1)).getBlock();
   }
}
