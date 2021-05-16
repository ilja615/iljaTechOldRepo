package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class Passthrough extends SimplePlacement<NoPlacementConfig> {
   public Passthrough(Codec<NoPlacementConfig> p_i232094_1_) {
      super(p_i232094_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, NoPlacementConfig p_212852_2_, BlockPos p_212852_3_) {
      return Stream.of(p_212852_3_);
   }
}
