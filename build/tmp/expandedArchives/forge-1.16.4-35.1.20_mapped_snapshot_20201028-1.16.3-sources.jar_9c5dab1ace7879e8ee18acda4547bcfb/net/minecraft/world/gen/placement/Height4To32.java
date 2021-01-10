package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class Height4To32 extends SimplePlacement<NoPlacementConfig> {
   public Height4To32(Codec<NoPlacementConfig> codec) {
      super(codec);
   }

   public Stream<BlockPos> getPositions(Random random, NoPlacementConfig config, BlockPos pos) {
      int i = 3 + random.nextInt(6);
      return IntStream.range(0, i).mapToObj((count) -> {
         int j = random.nextInt(16) + pos.getX();
         int k = random.nextInt(16) + pos.getZ();
         int l = random.nextInt(28) + 4;
         return new BlockPos(j, l, k);
      });
   }
}
