package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class RangeBiasedPlacement extends SimplePlacement<TopSolidRangeConfig> {
   public RangeBiasedPlacement(Codec<TopSolidRangeConfig> p_i242014_1_) {
      super(p_i242014_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, TopSolidRangeConfig p_212852_2_, BlockPos p_212852_3_) {
      int i = p_212852_3_.getX();
      int j = p_212852_3_.getZ();
      int k = p_212852_1_.nextInt(p_212852_1_.nextInt(p_212852_2_.maximum - p_212852_2_.topOffset) + p_212852_2_.bottomOffset);
      return Stream.of(new BlockPos(i, k, j));
   }
}
