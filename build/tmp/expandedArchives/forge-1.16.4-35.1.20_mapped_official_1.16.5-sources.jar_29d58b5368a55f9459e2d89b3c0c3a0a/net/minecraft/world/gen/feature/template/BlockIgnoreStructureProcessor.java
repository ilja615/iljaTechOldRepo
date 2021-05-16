package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockIgnoreStructureProcessor extends StructureProcessor {
   public static final Codec<BlockIgnoreStructureProcessor> CODEC = BlockState.CODEC.xmap(AbstractBlock.AbstractBlockState::getBlock, Block::defaultBlockState).listOf().fieldOf("blocks").xmap(BlockIgnoreStructureProcessor::new, (p_237074_0_) -> {
      return p_237074_0_.toIgnore;
   }).codec();
   public static final BlockIgnoreStructureProcessor STRUCTURE_BLOCK = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
   public static final BlockIgnoreStructureProcessor AIR = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.AIR));
   public static final BlockIgnoreStructureProcessor STRUCTURE_AND_AIR = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
   private final ImmutableList<Block> toIgnore;

   public BlockIgnoreStructureProcessor(List<Block> p_i51336_1_) {
      this.toIgnore = ImmutableList.copyOf(p_i51336_1_);
   }

   @Nullable
   public Template.BlockInfo processBlock(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_) {
      return this.toIgnore.contains(p_230386_5_.state.getBlock()) ? null : p_230386_5_;
   }

   protected IStructureProcessorType<?> getType() {
      return IStructureProcessorType.BLOCK_IGNORE;
   }
}
