package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class IcebergPlacement extends SimplePlacement<NoPlacementConfig> {
   public IcebergPlacement(Codec<NoPlacementConfig> p_i232088_1_) {
      super(p_i232088_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, NoPlacementConfig p_212852_2_, BlockPos p_212852_3_) {
      int i = p_212852_1_.nextInt(8) + 4 + p_212852_3_.getX();
      int j = p_212852_1_.nextInt(8) + 4 + p_212852_3_.getZ();
      return Stream.of(new BlockPos(i, p_212852_3_.getY(), j));
   }
}
