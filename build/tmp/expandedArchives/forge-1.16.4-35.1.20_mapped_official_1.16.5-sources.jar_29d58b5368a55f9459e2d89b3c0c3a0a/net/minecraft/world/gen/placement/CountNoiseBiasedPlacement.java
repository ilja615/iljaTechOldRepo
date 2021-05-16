package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class CountNoiseBiasedPlacement extends SimplePlacement<TopSolidWithNoiseConfig> {
   public CountNoiseBiasedPlacement(Codec<TopSolidWithNoiseConfig> p_i242028_1_) {
      super(p_i242028_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, TopSolidWithNoiseConfig p_212852_2_, BlockPos p_212852_3_) {
      double d0 = Biome.BIOME_INFO_NOISE.getValue((double)p_212852_3_.getX() / p_212852_2_.noiseFactor, (double)p_212852_3_.getZ() / p_212852_2_.noiseFactor, false);
      int i = (int)Math.ceil((d0 + p_212852_2_.noiseOffset) * (double)p_212852_2_.noiseToCountRatio);
      return IntStream.range(0, i).mapToObj((p_242913_1_) -> {
         return p_212852_3_;
      });
   }
}
