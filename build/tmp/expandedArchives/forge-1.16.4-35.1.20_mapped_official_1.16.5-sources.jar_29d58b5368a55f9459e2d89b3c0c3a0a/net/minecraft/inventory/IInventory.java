package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IInventory extends IClearable {
   int getContainerSize();

   boolean isEmpty();

   ItemStack getItem(int p_70301_1_);

   ItemStack removeItem(int p_70298_1_, int p_70298_2_);

   ItemStack removeItemNoUpdate(int p_70304_1_);

   void setItem(int p_70299_1_, ItemStack p_70299_2_);

   default int getMaxStackSize() {
      return 64;
   }

   void setChanged();

   boolean stillValid(PlayerEntity p_70300_1_);

   default void startOpen(PlayerEntity p_174889_1_) {
   }

   default void stopOpen(PlayerEntity p_174886_1_) {
   }

   default boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   default int countItem(Item p_213901_1_) {
      int i = 0;

      for(int j = 0; j < this.getContainerSize(); ++j) {
         ItemStack itemstack = this.getItem(j);
         if (itemstack.getItem().equals(p_213901_1_)) {
            i += itemstack.getCount();
         }
      }

      return i;
   }

   default boolean hasAnyOf(Set<Item> p_213902_1_) {
      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemstack = this.getItem(i);
         if (p_213902_1_.contains(itemstack.getItem()) && itemstack.getCount() > 0) {
            return true;
         }
      }

      return false;
   }
}
