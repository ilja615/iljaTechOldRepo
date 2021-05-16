package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int p_i48338_1_, AbstractBlock.Properties p_i48338_2_) {
      super(p_i48338_2_);
      this.dustColor = p_i48338_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState p_189876_1_, IBlockReader p_189876_2_, BlockPos p_189876_3_) {
      return this.dustColor;
   }
}
