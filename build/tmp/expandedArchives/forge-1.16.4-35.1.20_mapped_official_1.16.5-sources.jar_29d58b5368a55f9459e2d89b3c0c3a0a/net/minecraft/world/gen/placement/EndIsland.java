package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class EndIsland extends SimplePlacement<NoPlacementConfig> {
   public EndIsland(Codec<NoPlacementConfig> p_i232085_1_) {
      super(p_i232085_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, NoPlacementConfig p_212852_2_, BlockPos p_212852_3_) {
      Stream<BlockPos> stream = Stream.empty();
      if (p_212852_1_.nextInt(14) == 0) {
         stream = Stream.concat(stream, Stream.of(p_212852_3_.offset(p_212852_1_.nextInt(16), 55 + p_212852_1_.nextInt(16), p_212852_1_.nextInt(16))));
         if (p_212852_1_.nextInt(4) == 0) {
            stream = Stream.concat(stream, Stream.of(p_212852_3_.offset(p_212852_1_.nextInt(16), 55 + p_212852_1_.nextInt(16), p_212852_1_.nextInt(16))));
         }

         return stream;
      } else {
         return Stream.empty();
      }
   }
}
