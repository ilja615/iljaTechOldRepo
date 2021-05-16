package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class DispenserContainer extends Container {
   private final IInventory dispenser;

   public DispenserContainer(int p_i50087_1_, PlayerInventory p_i50087_2_) {
      this(p_i50087_1_, p_i50087_2_, new Inventory(9));
   }

   public DispenserContainer(int p_i50088_1_, PlayerInventory p_i50088_2_, IInventory p_i50088_3_) {
      super(ContainerType.GENERIC_3x3, p_i50088_1_);
      checkContainerSize(p_i50088_3_, 9);
      this.dispenser = p_i50088_3_;
      p_i50088_3_.startOpen(p_i50088_2_.player);

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlot(new Slot(p_i50088_3_, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for(int k = 0; k < 3; ++k) {
         for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(p_i50088_2_, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
         }
      }

      for(int l = 0; l < 9; ++l) {
         this.addSlot(new Slot(p_i50088_2_, l, 8 + l * 18, 142));
      }

   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return this.dispenser.stillValid(p_75145_1_);
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_82846_2_ < 9) {
            if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_82846_1_, itemstack1);
      }

      return itemstack;
   }

   public void removed(PlayerEntity p_75134_1_) {
      super.removed(p_75134_1_);
      this.dispenser.stopOpen(p_75134_1_);
   }
}
