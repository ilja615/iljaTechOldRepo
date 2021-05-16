package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class BasaltColumnFeature extends Feature<ColumnConfig> {
   private static final ImmutableList<Block> CANNOT_PLACE_ON = ImmutableList.of(Blocks.LAVA, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);

   public BasaltColumnFeature(Codec<ColumnConfig> p_i231925_1_) {
      super(p_i231925_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, ColumnConfig p_241855_5_) {
      int i = p_241855_2_.getSeaLevel();
      if (!canPlaceAt(p_241855_1_, i, p_241855_4_.mutable())) {
         return false;
      } else {
         int j = p_241855_5_.height().sample(p_241855_3_);
         boolean flag = p_241855_3_.nextFloat() < 0.9F;
         int k = Math.min(j, flag ? 5 : 8);
         int l = flag ? 50 : 15;
         boolean flag1 = false;

         for(BlockPos blockpos : BlockPos.randomBetweenClosed(p_241855_3_, l, p_241855_4_.getX() - k, p_241855_4_.getY(), p_241855_4_.getZ() - k, p_241855_4_.getX() + k, p_241855_4_.getY(), p_241855_4_.getZ() + k)) {
            int i1 = j - blockpos.distManhattan(p_241855_4_);
            if (i1 >= 0) {
               flag1 |= this.placeColumn(p_241855_1_, i, blockpos, i1, p_241855_5_.reach().sample(p_241855_3_));
            }
         }

         return flag1;
      }
   }

   private boolean placeColumn(IWorld p_236248_1_, int p_236248_2_, BlockPos p_236248_3_, int p_236248_4_, int p_236248_5_) {
      boolean flag = false;

      for(BlockPos blockpos : BlockPos.betweenClosed(p_236248_3_.getX() - p_236248_5_, p_236248_3_.getY(), p_236248_3_.getZ() - p_236248_5_, p_236248_3_.getX() + p_236248_5_, p_236248_3_.getY(), p_236248_3_.getZ() + p_236248_5_)) {
         int i = blockpos.distManhattan(p_236248_3_);
         BlockPos blockpos1 = isAirOrLavaOcean(p_236248_1_, p_236248_2_, blockpos) ? findSurface(p_236248_1_, p_236248_2_, blockpos.mutable(), i) : findAir(p_236248_1_, blockpos.mutable(), i);
         if (blockpos1 != null) {
            int j = p_236248_4_ - i / 2;

            for(BlockPos.Mutable blockpos$mutable = blockpos1.mutable(); j >= 0; --j) {
               if (isAirOrLavaOcean(p_236248_1_, p_236248_2_, blockpos$mutable)) {
                  this.setBlock(p_236248_1_, blockpos$mutable, Blocks.BASALT.defaultBlockState());
                  blockpos$mutable.move(Direction.UP);
                  flag = true;
               } else {
                  if (!p_236248_1_.getBlockState(blockpos$mutable).is(Blocks.BASALT)) {
                     break;
                  }

                  blockpos$mutable.move(Direction.UP);
               }
            }
         }
      }

      return flag;
   }

   @Nullable
   private static BlockPos findSurface(IWorld p_236246_0_, int p_236246_1_, BlockPos.Mutable p_236246_2_, int p_236246_3_) {
      while(p_236246_2_.getY() > 1 && p_236246_3_ > 0) {
         --p_236246_3_;
         if (canPlaceAt(p_236246_0_, p_236246_1_, p_236246_2_)) {
            return p_236246_2_;
         }

         p_236246_2_.move(Direction.DOWN);
      }

      return null;
   }

   private static boolean canPlaceAt(IWorld p_242762_0_, int p_242762_1_, BlockPos.Mutable p_242762_2_) {
      if (!isAirOrLavaOcean(p_242762_0_, p_242762_1_, p_242762_2_)) {
         return false;
      } else {
         BlockState blockstate = p_242762_0_.getBlockState(p_242762_2_.move(Direction.DOWN));
         p_242762_2_.move(Direction.UP);
         return !blockstate.isAir() && !CANNOT_PLACE_ON.contains(blockstate.getBlock());
      }
   }

   @Nullable
   private static BlockPos findAir(IWorld p_236249_0_, BlockPos.Mutable p_236249_1_, int p_236249_2_) {
      while(p_236249_1_.getY() < p_236249_0_.getMaxBuildHeight() && p_236249_2_ > 0) {
         --p_236249_2_;
         BlockState blockstate = p_236249_0_.getBlockState(p_236249_1_);
         if (CANNOT_PLACE_ON.contains(blockstate.getBlock())) {
            return null;
         }

         if (blockstate.isAir()) {
            return p_236249_1_;
         }

         p_236249_1_.move(Direction.UP);
      }

      return null;
   }

   private static boolean isAirOrLavaOcean(IWorld p_236247_0_, int p_236247_1_, BlockPos p_236247_2_) {
      BlockState blockstate = p_236247_0_.getBlockState(p_236247_2_);
      return blockstate.isAir() || blockstate.is(Blocks.LAVA) && p_236247_2_.getY() <= p_236247_1_;
   }
}
