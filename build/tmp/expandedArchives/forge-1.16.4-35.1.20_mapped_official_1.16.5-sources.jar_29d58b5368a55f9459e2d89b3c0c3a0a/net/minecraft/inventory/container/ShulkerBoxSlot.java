package net.minecraft.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxSlot extends Slot {
   public ShulkerBoxSlot(IInventory p_i47265_1_, int p_i47265_2_, int p_i47265_3_, int p_i47265_4_) {
      super(p_i47265_1_, p_i47265_2_, p_i47265_3_, p_i47265_4_);
   }

   public boolean mayPlace(ItemStack p_75214_1_) {
      return !(Block.byItem(p_75214_1_.getItem()) instanceof ShulkerBoxBlock);
   }
}
