package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
   protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState ICE = Blocks.ICE.defaultBlockState();
   private PerlinNoiseGenerator icebergNoise;
   private PerlinNoiseGenerator icebergRoofNoise;
   private long seed;

   public FrozenOceanSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232126_1_) {
      super(p_i232126_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      float f = p_205610_3_.getTemperature(blockpos$mutable.set(p_205610_4_, 63, p_205610_5_));
      double d2 = Math.min(Math.abs(p_205610_7_), this.icebergNoise.getValue((double)p_205610_4_ * 0.1D, (double)p_205610_5_ * 0.1D, false) * 15.0D);
      if (d2 > 1.8D) {
         double d3 = 0.09765625D;
         double d4 = Math.abs(this.icebergRoofNoise.getValue((double)p_205610_4_ * 0.09765625D, (double)p_205610_5_ * 0.09765625D, false));
         d0 = d2 * d2 * 1.2D;
         double d5 = Math.ceil(d4 * 40.0D) + 14.0D;
         if (d0 > d5) {
            d0 = d5;
         }

         if (f > 0.1F) {
            d0 -= 2.0D;
         }

         if (d0 > 2.0D) {
            d1 = (double)p_205610_11_ - d0 - 7.0D;
            d0 = d0 + (double)p_205610_11_;
         } else {
            d0 = 0.0D;
         }
      }

      int l1 = p_205610_4_ & 15;
      int i = p_205610_5_ & 15;
      ISurfaceBuilderConfig isurfacebuilderconfig = p_205610_3_.getGenerationSettings().getSurfaceBuilderConfig();
      BlockState blockstate = isurfacebuilderconfig.getUnderMaterial();
      BlockState blockstate4 = isurfacebuilderconfig.getTopMaterial();
      BlockState blockstate1 = blockstate;
      BlockState blockstate2 = blockstate4;
      int j = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      int k = -1;
      int l = 0;
      int i1 = 2 + p_205610_1_.nextInt(4);
      int j1 = p_205610_11_ + 18 + p_205610_1_.nextInt(10);

      for(int k1 = Math.max(p_205610_6_, (int)d0 + 1); k1 >= 0; --k1) {
         blockpos$mutable.set(l1, k1, i);
         if (p_205610_2_.getBlockState(blockpos$mutable).isAir() && k1 < (int)d0 && p_205610_1_.nextDouble() > 0.01D) {
            p_205610_2_.setBlockState(blockpos$mutable, PACKED_ICE, false);
         } else if (p_205610_2_.getBlockState(blockpos$mutable).getMaterial() == Material.WATER && k1 > (int)d1 && k1 < p_205610_11_ && d1 != 0.0D && p_205610_1_.nextDouble() > 0.15D) {
            p_205610_2_.setBlockState(blockpos$mutable, PACKED_ICE, false);
         }

         BlockState blockstate3 = p_205610_2_.getBlockState(blockpos$mutable);
         if (blockstate3.isAir()) {
            k = -1;
         } else if (!blockstate3.is(p_205610_9_.getBlock())) {
            if (blockstate3.is(Blocks.PACKED_ICE) && l <= i1 && k1 > j1) {
               p_205610_2_.setBlockState(blockpos$mutable, SNOW_BLOCK, false);
               ++l;
            }
         } else if (k == -1) {
            if (j <= 0) {
               blockstate2 = AIR;
               blockstate1 = p_205610_9_;
            } else if (k1 >= p_205610_11_ - 4 && k1 <= p_205610_11_ + 1) {
               blockstate2 = blockstate4;
               blockstate1 = blockstate;
            }

            if (k1 < p_205610_11_ && (blockstate2 == null || blockstate2.isAir())) {
               if (p_205610_3_.getTemperature(blockpos$mutable.set(p_205610_4_, k1, p_205610_5_)) < 0.15F) {
                  blockstate2 = ICE;
               } else {
                  blockstate2 = p_205610_10_;
               }
            }

            k = j;
            if (k1 >= p_205610_11_ - 1) {
               p_205610_2_.setBlockState(blockpos$mutable, blockstate2, false);
            } else if (k1 < p_205610_11_ - 7 - j) {
               blockstate2 = AIR;
               blockstate1 = p_205610_9_;
               p_205610_2_.setBlockState(blockpos$mutable, GRAVEL, false);
            } else {
               p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
            }
         } else if (k > 0) {
            --k;
            p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
            if (k == 0 && blockstate1.is(Blocks.SAND) && j > 1) {
               k = p_205610_1_.nextInt(4) + Math.max(0, k1 - 63);
               blockstate1 = blockstate1.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
            }
         }
      }

   }

   public void initNoise(long p_205548_1_) {
      if (this.seed != p_205548_1_ || this.icebergNoise == null || this.icebergRoofNoise == null) {
         SharedSeedRandom sharedseedrandom = new SharedSeedRandom(p_205548_1_);
         this.icebergNoise = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
         this.icebergRoofNoise = new PerlinNoiseGenerator(sharedseedrandom, ImmutableList.of(0));
      }

      this.seed = p_205548_1_;
   }
}
