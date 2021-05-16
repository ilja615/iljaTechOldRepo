package net.minecraft.inventory.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot extends Slot {
   private final AbstractFurnaceContainer menu;

   public FurnaceFuelSlot(AbstractFurnaceContainer p_i50084_1_, IInventory p_i50084_2_, int p_i50084_3_, int p_i50084_4_, int p_i50084_5_) {
      super(p_i50084_2_, p_i50084_3_, p_i50084_4_, p_i50084_5_);
      this.menu = p_i50084_1_;
   }

   public boolean mayPlace(ItemStack p_75214_1_) {
      return this.menu.isFuel(p_75214_1_) || isBucket(p_75214_1_);
   }

   public int getMaxStackSize(ItemStack p_178170_1_) {
      return isBucket(p_178170_1_) ? 1 : super.getMaxStackSize(p_178170_1_);
   }

   public static boolean isBucket(ItemStack p_178173_0_) {
      return p_178173_0_.getItem() == Items.BUCKET;
   }
}
