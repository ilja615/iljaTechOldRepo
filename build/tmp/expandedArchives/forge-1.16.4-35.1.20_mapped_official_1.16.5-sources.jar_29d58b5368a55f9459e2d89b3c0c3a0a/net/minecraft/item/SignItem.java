package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SignItem extends WallOrFloorItem {
   public SignItem(Item.Properties p_i50038_1_, Block p_i50038_2_, Block p_i50038_3_) {
      super(p_i50038_2_, p_i50038_3_, p_i50038_1_);
   }

   protected boolean updateCustomBlockEntityTag(BlockPos p_195943_1_, World p_195943_2_, @Nullable PlayerEntity p_195943_3_, ItemStack p_195943_4_, BlockState p_195943_5_) {
      boolean flag = super.updateCustomBlockEntityTag(p_195943_1_, p_195943_2_, p_195943_3_, p_195943_4_, p_195943_5_);
      if (!p_195943_2_.isClientSide && !flag && p_195943_3_ != null) {
         p_195943_3_.openTextEdit((SignTileEntity)p_195943_2_.getBlockEntity(p_195943_1_));
      }

      return flag;
   }
}
