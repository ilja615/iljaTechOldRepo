package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class OperatorOnlyItem extends BlockItem {
   public OperatorOnlyItem(Block p_i48491_1_, Item.Properties p_i48491_2_) {
      super(p_i48491_1_, p_i48491_2_);
   }

   @Nullable
   protected BlockState getPlacementState(BlockItemUseContext p_195945_1_) {
      PlayerEntity playerentity = p_195945_1_.getPlayer();
      return playerentity != null && !playerentity.canUseGameMasterBlocks() ? null : super.getPlacementState(p_195945_1_);
   }
}
