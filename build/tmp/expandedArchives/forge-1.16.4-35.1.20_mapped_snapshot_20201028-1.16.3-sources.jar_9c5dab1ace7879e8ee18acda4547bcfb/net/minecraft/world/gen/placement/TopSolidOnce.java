package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.Heightmap;

public class TopSolidOnce extends SimpleHeightmapBasedPlacement<NoPlacementConfig> {
   public TopSolidOnce(Codec<NoPlacementConfig> codec) {
      super(codec);
   }

   protected Heightmap.Type func_241858_a(NoPlacementConfig config) {
      return Heightmap.Type.OCEAN_FLOOR_WG;
   }
}
