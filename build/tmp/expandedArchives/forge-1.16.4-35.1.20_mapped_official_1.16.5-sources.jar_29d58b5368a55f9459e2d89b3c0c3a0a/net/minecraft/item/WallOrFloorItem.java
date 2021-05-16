package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IWorldReader;

public class WallOrFloorItem extends BlockItem {
   protected final Block wallBlock;

   public WallOrFloorItem(Block p_i48462_1_, Block p_i48462_2_, Item.Properties p_i48462_3_) {
      super(p_i48462_1_, p_i48462_3_);
      this.wallBlock = p_i48462_2_;
   }

   @Nullable
   protected BlockState getPlacementState(BlockItemUseContext p_195945_1_) {
      BlockState blockstate = this.wallBlock.getStateForPlacement(p_195945_1_);
      BlockState blockstate1 = null;
      IWorldReader iworldreader = p_195945_1_.getLevel();
      BlockPos blockpos = p_195945_1_.getClickedPos();

      for(Direction direction : p_195945_1_.getNearestLookingDirections()) {
         if (direction != Direction.UP) {
            BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(p_195945_1_) : blockstate;
            if (blockstate2 != null && blockstate2.canSurvive(iworldreader, blockpos)) {
               blockstate1 = blockstate2;
               break;
            }
         }
      }

      return blockstate1 != null && iworldreader.isUnobstructed(blockstate1, blockpos, ISelectionContext.empty()) ? blockstate1 : null;
   }

   public void registerBlocks(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
      super.registerBlocks(p_195946_1_, p_195946_2_);
      p_195946_1_.put(this.wallBlock, p_195946_2_);
   }

   public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
      super.removeFromBlockToItemMap(blockToItemMap, itemIn);
      blockToItemMap.remove(this.wallBlock);
   }
}
