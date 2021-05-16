package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SoulSandValleySurfaceBuilder extends ValleySurfaceBuilder {
   private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
   private static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final ImmutableList<BlockState> BLOCK_STATES = ImmutableList.of(SOUL_SAND, SOUL_SOIL);

   public SoulSandValleySurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232135_1_) {
      super(p_i232135_1_);
   }

   protected ImmutableList<BlockState> getFloorBlockStates() {
      return BLOCK_STATES;
   }

   protected ImmutableList<BlockState> getCeilingBlockStates() {
      return BLOCK_STATES;
   }

   protected BlockState getPatchBlockState() {
      return GRAVEL;
   }
}
