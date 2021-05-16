package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;

public class NetherForestsSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
   protected long seed;
   private OctavesNoiseGenerator decorationNoise;

   public NetherForestsSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232131_1_) {
      super(p_i232131_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int i = p_205610_11_;
      int j = p_205610_4_ & 15;
      int k = p_205610_5_ & 15;
      double d0 = this.decorationNoise.getValue((double)p_205610_4_ * 0.1D, (double)p_205610_11_, (double)p_205610_5_ * 0.1D);
      boolean flag = d0 > 0.15D + p_205610_1_.nextDouble() * 0.35D;
      double d1 = this.decorationNoise.getValue((double)p_205610_4_ * 0.1D, 109.0D, (double)p_205610_5_ * 0.1D);
      boolean flag1 = d1 > 0.25D + p_205610_1_.nextDouble() * 0.9D;
      int l = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      int i1 = -1;
      BlockState blockstate = p_205610_14_.getUnderMaterial();

      for(int j1 = 127; j1 >= 0; --j1) {
         blockpos$mutable.set(j, j1, k);
         BlockState blockstate1 = p_205610_14_.getTopMaterial();
         BlockState blockstate2 = p_205610_2_.getBlockState(blockpos$mutable);
         if (blockstate2.isAir()) {
            i1 = -1;
         } else if (blockstate2.is(p_205610_9_.getBlock())) {
            if (i1 == -1) {
               boolean flag2 = false;
               if (l <= 0) {
                  flag2 = true;
                  blockstate = p_205610_14_.getUnderMaterial();
               }

               if (flag) {
                  blockstate1 = p_205610_14_.getUnderMaterial();
               } else if (flag1) {
                  blockstate1 = p_205610_14_.getUnderwaterMaterial();
               }

               if (j1 < i && flag2) {
                  blockstate1 = p_205610_10_;
               }

               i1 = l;
               if (j1 >= i - 1) {
                  p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
               } else {
                  p_205610_2_.setBlockState(blockpos$mutable, blockstate, false);
               }
            } else if (i1 > 0) {
               --i1;
               p_205610_2_.setBlockState(blockpos$mutable, blockstate, false);
            }
         }
      }

   }

   public void initNoise(long p_205548_1_) {
      if (this.seed != p_205548_1_ || this.decorationNoise == null) {
         this.decorationNoise = new OctavesNoiseGenerator(new SharedSeedRandom(p_205548_1_), ImmutableList.of(0));
      }

      this.seed = p_205548_1_;
   }
}
