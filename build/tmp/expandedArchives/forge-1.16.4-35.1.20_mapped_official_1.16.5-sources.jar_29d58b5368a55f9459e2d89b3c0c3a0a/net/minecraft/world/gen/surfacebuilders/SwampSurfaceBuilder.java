package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class SwampSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   public SwampSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232137_1_) {
      super(p_i232137_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = Biome.BIOME_INFO_NOISE.getValue((double)p_205610_4_ * 0.25D, (double)p_205610_5_ * 0.25D, false);
      if (d0 > 0.0D) {
         int i = p_205610_4_ & 15;
         int j = p_205610_5_ & 15;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = p_205610_6_; k >= 0; --k) {
            blockpos$mutable.set(i, k, j);
            if (!p_205610_2_.getBlockState(blockpos$mutable).isAir()) {
               if (k == 62 && !p_205610_2_.getBlockState(blockpos$mutable).is(p_205610_10_.getBlock())) {
                  p_205610_2_.setBlockState(blockpos$mutable, p_205610_10_, false);
               }
               break;
            }
         }
      }

      SurfaceBuilder.DEFAULT.apply(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, p_205610_14_);
   }
}
