package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class DepthAveragePlacement extends SimplePlacement<DepthAverageConfig> {
   public DepthAveragePlacement(Codec<DepthAverageConfig> p_i242023_1_) {
      super(p_i242023_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, DepthAverageConfig p_212852_2_, BlockPos p_212852_3_) {
      int i = p_212852_2_.baseline;
      int j = p_212852_2_.spread;
      int k = p_212852_3_.getX();
      int l = p_212852_3_.getZ();
      int i1 = p_212852_1_.nextInt(j) + p_212852_1_.nextInt(j) - j + i;
      return Stream.of(new BlockPos(k, i1, l));
   }
}
