package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BasaltDeltasFeature;
import net.minecraft.world.gen.feature.Feature;

public class BasaltDeltasStructure extends Feature<BasaltDeltasFeature> {
   private static final ImmutableList<Block> CANNOT_REPLACE = ImmutableList.of(Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
   private static final Direction[] DIRECTIONS = Direction.values();

   public BasaltDeltasStructure(Codec<BasaltDeltasFeature> p_i231946_1_) {
      super(p_i231946_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BasaltDeltasFeature p_241855_5_) {
      boolean flag = false;
      boolean flag1 = p_241855_3_.nextDouble() < 0.9D;
      int i = flag1 ? p_241855_5_.rimSize().sample(p_241855_3_) : 0;
      int j = flag1 ? p_241855_5_.rimSize().sample(p_241855_3_) : 0;
      boolean flag2 = flag1 && i != 0 && j != 0;
      int k = p_241855_5_.size().sample(p_241855_3_);
      int l = p_241855_5_.size().sample(p_241855_3_);
      int i1 = Math.max(k, l);

      for(BlockPos blockpos : BlockPos.withinManhattan(p_241855_4_, k, 0, l)) {
         if (blockpos.distManhattan(p_241855_4_) > i1) {
            break;
         }

         if (isClear(p_241855_1_, blockpos, p_241855_5_)) {
            if (flag2) {
               flag = true;
               this.setBlock(p_241855_1_, blockpos, p_241855_5_.rim());
            }

            BlockPos blockpos1 = blockpos.offset(i, 0, j);
            if (isClear(p_241855_1_, blockpos1, p_241855_5_)) {
               flag = true;
               this.setBlock(p_241855_1_, blockpos1, p_241855_5_.contents());
            }
         }
      }

      return flag;
   }

   private static boolean isClear(IWorld p_236277_0_, BlockPos p_236277_1_, BasaltDeltasFeature p_236277_2_) {
      BlockState blockstate = p_236277_0_.getBlockState(p_236277_1_);
      if (blockstate.is(p_236277_2_.contents().getBlock())) {
         return false;
      } else if (CANNOT_REPLACE.contains(blockstate.getBlock())) {
         return false;
      } else {
         for(Direction direction : DIRECTIONS) {
            boolean flag = p_236277_0_.getBlockState(p_236277_1_.relative(direction)).isAir();
            if (flag && direction != Direction.UP || !flag && direction == Direction.UP) {
               return false;
            }
         }

         return true;
      }
   }
}
