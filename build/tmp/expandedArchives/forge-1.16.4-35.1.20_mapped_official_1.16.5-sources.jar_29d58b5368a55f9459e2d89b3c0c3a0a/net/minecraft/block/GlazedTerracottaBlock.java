package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;

public class GlazedTerracottaBlock extends HorizontalBlock {
   public GlazedTerracottaBlock(AbstractBlock.Properties p_i48390_1_) {
      super(p_i48390_1_);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.PUSH_ONLY;
   }
}
