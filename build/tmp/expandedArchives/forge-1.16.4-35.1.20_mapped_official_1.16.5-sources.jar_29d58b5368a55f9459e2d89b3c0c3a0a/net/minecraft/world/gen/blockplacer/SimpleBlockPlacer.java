package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SimpleBlockPlacer extends BlockPlacer {
   public static final Codec<SimpleBlockPlacer> CODEC;
   public static final SimpleBlockPlacer INSTANCE = new SimpleBlockPlacer();

   protected BlockPlacerType<?> type() {
      return BlockPlacerType.SIMPLE_BLOCK_PLACER;
   }

   public void place(IWorld p_225567_1_, BlockPos p_225567_2_, BlockState p_225567_3_, Random p_225567_4_) {
      p_225567_1_.setBlock(p_225567_2_, p_225567_3_, 2);
   }

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
