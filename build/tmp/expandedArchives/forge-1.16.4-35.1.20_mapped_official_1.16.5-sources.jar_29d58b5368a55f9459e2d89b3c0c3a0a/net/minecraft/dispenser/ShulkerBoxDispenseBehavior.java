package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class ShulkerBoxDispenseBehavior extends OptionalDispenseBehavior {
   protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      this.setSuccess(false);
      Item item = p_82487_2_.getItem();
      if (item instanceof BlockItem) {
         Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
         BlockPos blockpos = p_82487_1_.getPos().relative(direction);
         Direction direction1 = p_82487_1_.getLevel().isEmptyBlock(blockpos.below()) ? direction : Direction.UP;
         this.setSuccess(((BlockItem)item).place(new DirectionalPlaceContext(p_82487_1_.getLevel(), blockpos, direction, p_82487_2_, direction1)).consumesAction());
      }

      return p_82487_2_;
   }
}
