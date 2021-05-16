package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SnowyDirtBlock extends Block {
   public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

   public SnowyDirtBlock(AbstractBlock.Properties p_i48327_1_) {
      super(p_i48327_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, Boolean.valueOf(false)));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ != Direction.UP ? super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_) : p_196271_1_.setValue(SNOWY, Boolean.valueOf(p_196271_3_.is(Blocks.SNOW_BLOCK) || p_196271_3_.is(Blocks.SNOW)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos().above());
      return this.defaultBlockState().setValue(SNOWY, Boolean.valueOf(blockstate.is(Blocks.SNOW_BLOCK) || blockstate.is(Blocks.SNOW)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(SNOWY);
   }
}
