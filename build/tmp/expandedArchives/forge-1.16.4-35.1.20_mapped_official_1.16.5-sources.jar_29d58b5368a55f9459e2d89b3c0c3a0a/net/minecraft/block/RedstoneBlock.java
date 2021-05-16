package net.minecraft.block;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class RedstoneBlock extends Block {
   public RedstoneBlock(AbstractBlock.Properties p_i48350_1_) {
      super(p_i48350_1_);
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return 15;
   }
}
