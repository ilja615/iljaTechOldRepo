package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public abstract class CoralFeature extends Feature<NoFeatureConfig> {
   public CoralFeature(Codec<NoFeatureConfig> p_i231940_1_) {
      super(p_i231940_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
      BlockState blockstate = BlockTags.CORAL_BLOCKS.getRandomElement(p_241855_3_).defaultBlockState();
      return this.placeFeature(p_241855_1_, p_241855_3_, p_241855_4_, blockstate);
   }

   protected abstract boolean placeFeature(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_);

   protected boolean placeCoralBlock(IWorld p_204624_1_, Random p_204624_2_, BlockPos p_204624_3_, BlockState p_204624_4_) {
      BlockPos blockpos = p_204624_3_.above();
      BlockState blockstate = p_204624_1_.getBlockState(p_204624_3_);
      if ((blockstate.is(Blocks.WATER) || blockstate.is(BlockTags.CORALS)) && p_204624_1_.getBlockState(blockpos).is(Blocks.WATER)) {
         p_204624_1_.setBlock(p_204624_3_, p_204624_4_, 3);
         if (p_204624_2_.nextFloat() < 0.25F) {
            p_204624_1_.setBlock(blockpos, BlockTags.CORALS.getRandomElement(p_204624_2_).defaultBlockState(), 2);
         } else if (p_204624_2_.nextFloat() < 0.05F) {
            p_204624_1_.setBlock(blockpos, Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, Integer.valueOf(p_204624_2_.nextInt(4) + 1)), 2);
         }

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (p_204624_2_.nextFloat() < 0.2F) {
               BlockPos blockpos1 = p_204624_3_.relative(direction);
               if (p_204624_1_.getBlockState(blockpos1).is(Blocks.WATER)) {
                  BlockState blockstate1 = BlockTags.WALL_CORALS.getRandomElement(p_204624_2_).defaultBlockState().setValue(DeadCoralWallFanBlock.FACING, direction);
                  p_204624_1_.setBlock(blockpos1, blockstate1, 2);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
