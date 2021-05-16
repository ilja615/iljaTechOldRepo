package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public abstract class Tree {
   @Nullable
   protected abstract ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_);

   public boolean growTree(ServerWorld p_230339_1_, ChunkGenerator p_230339_2_, BlockPos p_230339_3_, BlockState p_230339_4_, Random p_230339_5_) {
      ConfiguredFeature<BaseTreeFeatureConfig, ?> configuredfeature = this.getConfiguredFeature(p_230339_5_, this.hasFlowers(p_230339_1_, p_230339_3_));
      if (configuredfeature == null) {
         return false;
      } else {
         p_230339_1_.setBlock(p_230339_3_, Blocks.AIR.defaultBlockState(), 4);
         configuredfeature.config.setFromSapling();
         if (configuredfeature.place(p_230339_1_, p_230339_2_, p_230339_5_, p_230339_3_)) {
            return true;
         } else {
            p_230339_1_.setBlock(p_230339_3_, p_230339_4_, 4);
            return false;
         }
      }
   }

   private boolean hasFlowers(IWorld p_230140_1_, BlockPos p_230140_2_) {
      for(BlockPos blockpos : BlockPos.Mutable.betweenClosed(p_230140_2_.below().north(2).west(2), p_230140_2_.above().south(2).east(2))) {
         if (p_230140_1_.getBlockState(blockpos).is(BlockTags.FLOWERS)) {
            return true;
         }
      }

      return false;
   }
}
