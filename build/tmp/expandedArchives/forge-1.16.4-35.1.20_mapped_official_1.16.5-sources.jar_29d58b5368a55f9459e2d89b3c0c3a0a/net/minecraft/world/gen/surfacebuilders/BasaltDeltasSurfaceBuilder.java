package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BasaltDeltasSurfaceBuilder extends ValleySurfaceBuilder {
   private static final BlockState BASALT = Blocks.BASALT.defaultBlockState();
   private static final BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final ImmutableList<BlockState> FLOOR_BLOCK_STATES = ImmutableList.of(BASALT, BLACKSTONE);
   private static final ImmutableList<BlockState> CEILING_BLOCK_STATES = ImmutableList.of(BASALT);

   public BasaltDeltasSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232123_1_) {
      super(p_i232123_1_);
   }

   protected ImmutableList<BlockState> getFloorBlockStates() {
      return FLOOR_BLOCK_STATES;
   }

   protected ImmutableList<BlockState> getCeilingBlockStates() {
      return CEILING_BLOCK_STATES;
   }

   protected BlockState getPatchBlockState() {
      return GRAVEL;
   }
}
