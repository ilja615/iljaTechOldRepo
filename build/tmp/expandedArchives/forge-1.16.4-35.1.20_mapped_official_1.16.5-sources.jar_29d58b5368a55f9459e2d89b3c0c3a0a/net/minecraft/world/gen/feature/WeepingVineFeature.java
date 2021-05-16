package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class WeepingVineFeature extends Feature<NoFeatureConfig> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public WeepingVineFeature(Codec<NoFeatureConfig> p_i232004_1_) {
      super(p_i232004_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
      if (!p_241855_1_.isEmptyBlock(p_241855_4_)) {
         return false;
      } else {
         BlockState blockstate = p_241855_1_.getBlockState(p_241855_4_.above());
         if (!blockstate.is(Blocks.NETHERRACK) && !blockstate.is(Blocks.NETHER_WART_BLOCK)) {
            return false;
         } else {
            this.placeRoofNetherWart(p_241855_1_, p_241855_3_, p_241855_4_);
            this.placeRoofWeepingVines(p_241855_1_, p_241855_3_, p_241855_4_);
            return true;
         }
      }
   }

   private void placeRoofNetherWart(IWorld p_236428_1_, Random p_236428_2_, BlockPos p_236428_3_) {
      p_236428_1_.setBlock(p_236428_3_, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();

      for(int i = 0; i < 200; ++i) {
         blockpos$mutable.setWithOffset(p_236428_3_, p_236428_2_.nextInt(6) - p_236428_2_.nextInt(6), p_236428_2_.nextInt(2) - p_236428_2_.nextInt(5), p_236428_2_.nextInt(6) - p_236428_2_.nextInt(6));
         if (p_236428_1_.isEmptyBlock(blockpos$mutable)) {
            int j = 0;

            for(Direction direction : DIRECTIONS) {
               BlockState blockstate = p_236428_1_.getBlockState(blockpos$mutable1.setWithOffset(blockpos$mutable, direction));
               if (blockstate.is(Blocks.NETHERRACK) || blockstate.is(Blocks.NETHER_WART_BLOCK)) {
                  ++j;
               }

               if (j > 1) {
                  break;
               }
            }

            if (j == 1) {
               p_236428_1_.setBlock(blockpos$mutable, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
            }
         }
      }

   }

   private void placeRoofWeepingVines(IWorld p_236429_1_, Random p_236429_2_, BlockPos p_236429_3_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < 100; ++i) {
         blockpos$mutable.setWithOffset(p_236429_3_, p_236429_2_.nextInt(8) - p_236429_2_.nextInt(8), p_236429_2_.nextInt(2) - p_236429_2_.nextInt(7), p_236429_2_.nextInt(8) - p_236429_2_.nextInt(8));
         if (p_236429_1_.isEmptyBlock(blockpos$mutable)) {
            BlockState blockstate = p_236429_1_.getBlockState(blockpos$mutable.above());
            if (blockstate.is(Blocks.NETHERRACK) || blockstate.is(Blocks.NETHER_WART_BLOCK)) {
               int j = MathHelper.nextInt(p_236429_2_, 1, 8);
               if (p_236429_2_.nextInt(6) == 0) {
                  j *= 2;
               }

               if (p_236429_2_.nextInt(5) == 0) {
                  j = 1;
               }

               int k = 17;
               int l = 25;
               placeWeepingVinesColumn(p_236429_1_, p_236429_2_, blockpos$mutable, j, 17, 25);
            }
         }
      }

   }

   public static void placeWeepingVinesColumn(IWorld p_236427_0_, Random p_236427_1_, BlockPos.Mutable p_236427_2_, int p_236427_3_, int p_236427_4_, int p_236427_5_) {
      for(int i = 0; i <= p_236427_3_; ++i) {
         if (p_236427_0_.isEmptyBlock(p_236427_2_)) {
            if (i == p_236427_3_ || !p_236427_0_.isEmptyBlock(p_236427_2_.below())) {
               p_236427_0_.setBlock(p_236427_2_, Blocks.WEEPING_VINES.defaultBlockState().setValue(AbstractTopPlantBlock.AGE, Integer.valueOf(MathHelper.nextInt(p_236427_1_, p_236427_4_, p_236427_5_))), 2);
               break;
            }

            p_236427_0_.setBlock(p_236427_2_, Blocks.WEEPING_VINES_PLANT.defaultBlockState(), 2);
         }

         p_236427_2_.move(Direction.DOWN);
      }

   }
}
