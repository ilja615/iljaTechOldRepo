package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedstoneLampBlock extends Block {
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedstoneLampBlock(AbstractBlock.Properties p_i48343_1_) {
      super(p_i48343_1_);
      this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(LIT, Boolean.valueOf(p_196258_1_.getLevel().hasNeighborSignal(p_196258_1_.getClickedPos())));
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         boolean flag = p_220069_1_.getValue(LIT);
         if (flag != p_220069_2_.hasNeighborSignal(p_220069_3_)) {
            if (flag) {
               p_220069_2_.getBlockTicks().scheduleTick(p_220069_3_, this, 4);
            } else {
               p_220069_2_.setBlock(p_220069_3_, p_220069_1_.cycle(LIT), 2);
            }
         }

      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_1_.getValue(LIT) && !p_225534_2_.hasNeighborSignal(p_225534_3_)) {
         p_225534_2_.setBlock(p_225534_3_, p_225534_1_.cycle(LIT), 2);
      }

   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }
}
