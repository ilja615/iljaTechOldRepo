package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class AxisRotatingBlockStateProvider extends BlockStateProvider {
   public static final Codec<AxisRotatingBlockStateProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(AbstractBlock.AbstractBlockState::getBlock, Block::defaultBlockState).xmap(AxisRotatingBlockStateProvider::new, (p_236808_0_) -> {
      return p_236808_0_.block;
   }).codec();
   private final Block block;

   public AxisRotatingBlockStateProvider(Block p_i225858_1_) {
      this.block = p_i225858_1_;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.ROTATED_BLOCK_PROVIDER;
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      Direction.Axis direction$axis = Direction.Axis.getRandom(p_225574_1_);
      return this.block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction$axis);
   }
}
