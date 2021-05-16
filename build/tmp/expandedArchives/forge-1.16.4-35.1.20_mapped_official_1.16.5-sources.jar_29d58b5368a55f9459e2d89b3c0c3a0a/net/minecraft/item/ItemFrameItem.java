package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFrameItem extends HangingEntityItem {
   public ItemFrameItem(Item.Properties p_i48486_1_) {
      super(EntityType.ITEM_FRAME, p_i48486_1_);
   }

   protected boolean mayPlace(PlayerEntity p_200127_1_, Direction p_200127_2_, ItemStack p_200127_3_, BlockPos p_200127_4_) {
      return !World.isOutsideBuildHeight(p_200127_4_) && p_200127_1_.mayUseItemAt(p_200127_4_, p_200127_2_, p_200127_3_);
   }
}
