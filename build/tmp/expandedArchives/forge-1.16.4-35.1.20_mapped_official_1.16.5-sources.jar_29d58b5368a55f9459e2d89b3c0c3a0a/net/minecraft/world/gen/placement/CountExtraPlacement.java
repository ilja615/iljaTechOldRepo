package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class CountExtraPlacement extends SimplePlacement<AtSurfaceWithExtraConfig> {
   public CountExtraPlacement(Codec<AtSurfaceWithExtraConfig> p_i242018_1_) {
      super(p_i242018_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, AtSurfaceWithExtraConfig p_212852_2_, BlockPos p_212852_3_) {
      int i = p_212852_2_.count + (p_212852_1_.nextFloat() < p_212852_2_.extraChance ? p_212852_2_.extraCount : 0);
      return IntStream.range(0, i).mapToObj((p_242880_1_) -> {
         return p_212852_3_;
      });
   }
}
