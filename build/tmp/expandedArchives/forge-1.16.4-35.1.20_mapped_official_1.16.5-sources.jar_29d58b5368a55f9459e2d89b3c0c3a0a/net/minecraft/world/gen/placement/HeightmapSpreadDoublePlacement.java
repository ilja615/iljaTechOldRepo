package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class HeightmapSpreadDoublePlacement<DC extends IPlacementConfig> extends HeightmapBasedPlacement<DC> {
   public HeightmapSpreadDoublePlacement(Codec<DC> p_i242027_1_) {
      super(p_i242027_1_);
   }

   protected Heightmap.Type type(DC p_241858_1_) {
      return Heightmap.Type.MOTION_BLOCKING;
   }

   public Stream<BlockPos> getPositions(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, DC p_241857_3_, BlockPos p_241857_4_) {
      int i = p_241857_4_.getX();
      int j = p_241857_4_.getZ();
      int k = p_241857_1_.getHeight(this.type(p_241857_3_), i, j);
      return k == 0 ? Stream.of() : Stream.of(new BlockPos(i, p_241857_2_.nextInt(k * 2), j));
   }
}
