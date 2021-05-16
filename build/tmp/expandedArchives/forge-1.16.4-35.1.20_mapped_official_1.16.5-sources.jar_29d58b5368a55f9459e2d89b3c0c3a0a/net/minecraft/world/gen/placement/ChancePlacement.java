package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChancePlacement extends SimplePlacement<ChanceConfig> {
   public ChancePlacement(Codec<ChanceConfig> p_i242015_1_) {
      super(p_i242015_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, ChanceConfig p_212852_2_, BlockPos p_212852_3_) {
      return p_212852_1_.nextFloat() < 1.0F / (float)p_212852_2_.chance ? Stream.of(p_212852_3_) : Stream.empty();
   }
}
