package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;

public class CountPlacement extends SimplePlacement<FeatureSpreadConfig> {
   public CountPlacement(Codec<FeatureSpreadConfig> p_i242016_1_) {
      super(p_i242016_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, FeatureSpreadConfig p_212852_2_, BlockPos p_212852_3_) {
      return IntStream.range(0, p_212852_2_.count().sample(p_212852_1_)).mapToObj((p_242878_1_) -> {
         return p_212852_3_;
      });
   }
}
