package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();

   public ErodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232125_1_) {
      super(p_i232125_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = 0.0D;
      double d1 = Math.min(Math.abs(p_205610_7_), this.pillarNoise.getValue((double)p_205610_4_ * 0.25D, (double)p_205610_5_ * 0.25D, false) * 15.0D);
      if (d1 > 0.0D) {
         double d2 = 0.001953125D;
         double d3 = Math.abs(this.pillarRoofNoise.getValue((double)p_205610_4_ * 0.001953125D, (double)p_205610_5_ * 0.001953125D, false));
         d0 = d1 * d1 * 2.5D;
         double d4 = Math.ceil(d3 * 50.0D) + 14.0D;
         if (d0 > d4) {
            d0 = d4;
         }

         d0 = d0 + 64.0D;
      }

      int i1 = p_205610_4_ & 15;
      int i = p_205610_5_ & 15;
      BlockState blockstate3 = WHITE_TERRACOTTA;
      ISurfaceBuilderConfig isurfacebuilderconfig = p_205610_3_.getGenerationSettings().getSurfaceBuilderConfig();
      BlockState blockstate4 = isurfacebuilderconfig.getUnderMaterial();
      BlockState blockstate = isurfacebuilderconfig.getTopMaterial();
      BlockState blockstate1 = blockstate4;
      int j = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean flag = Math.cos(p_205610_7_ / 3.0D * Math.PI) > 0.0D;
      int k = -1;
      boolean flag1 = false;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int l = Math.max(p_205610_6_, (int)d0 + 1); l >= 0; --l) {
         blockpos$mutable.set(i1, l, i);
         if (p_205610_2_.getBlockState(blockpos$mutable).isAir() && l < (int)d0) {
            p_205610_2_.setBlockState(blockpos$mutable, p_205610_9_, false);
         }

         BlockState blockstate2 = p_205610_2_.getBlockState(blockpos$mutable);
         if (blockstate2.isAir()) {
            k = -1;
         } else if (blockstate2.is(p_205610_9_.getBlock())) {
            if (k == -1) {
               flag1 = false;
               if (j <= 0) {
                  blockstate3 = Blocks.AIR.defaultBlockState();
                  blockstate1 = p_205610_9_;
               } else if (l >= p_205610_11_ - 4 && l <= p_205610_11_ + 1) {
                  blockstate3 = WHITE_TERRACOTTA;
                  blockstate1 = blockstate4;
               }

               if (l < p_205610_11_ && (blockstate3 == null || blockstate3.isAir())) {
                  blockstate3 = p_205610_10_;
               }

               k = j + Math.max(0, l - p_205610_11_);
               if (l >= p_205610_11_ - 1) {
                  if (l <= p_205610_11_ + 3 + j) {
                     p_205610_2_.setBlockState(blockpos$mutable, blockstate, false);
                     flag1 = true;
                  } else {
                     BlockState blockstate5;
                     if (l >= 64 && l <= 127) {
                        if (flag) {
                           blockstate5 = TERRACOTTA;
                        } else {
                           blockstate5 = this.getBand(p_205610_4_, l, p_205610_5_);
                        }
                     } else {
                        blockstate5 = ORANGE_TERRACOTTA;
                     }

                     p_205610_2_.setBlockState(blockpos$mutable, blockstate5, false);
                  }
               } else {
                  p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
                  Block block = blockstate1.getBlock();
                  if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
                     p_205610_2_.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                  }
               }
            } else if (k > 0) {
               --k;
               if (flag1) {
                  p_205610_2_.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
               } else {
                  p_205610_2_.setBlockState(blockpos$mutable, this.getBand(p_205610_4_, l, p_205610_5_), false);
               }
            }
         }
      }

   }
}
