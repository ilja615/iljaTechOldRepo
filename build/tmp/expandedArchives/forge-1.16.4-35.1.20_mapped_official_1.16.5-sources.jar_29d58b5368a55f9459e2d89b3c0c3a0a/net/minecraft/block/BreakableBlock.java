package net.minecraft.block;

import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BreakableBlock extends Block {
   public BreakableBlock(AbstractBlock.Properties p_i48382_1_) {
      super(p_i48382_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean skipRendering(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
      return p_200122_2_.is(this) ? true : super.skipRendering(p_200122_1_, p_200122_2_, p_200122_3_);
   }
}
