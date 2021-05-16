package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.BlockState;

public interface ISurfaceBuilderConfig {
   BlockState getTopMaterial();

   BlockState getUnderMaterial();
}
