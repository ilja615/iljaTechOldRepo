package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public class Inventory implements IInventory, IRecipeHelperPopulator {
   private final int size;
   private final NonNullList<ItemStack> items;
   private List<IInventoryChangedListener> listeners;

   public Inventory(int p_i50397_1_) {
      this.size = p_i50397_1_;
      this.items = NonNullList.withSize(p_i50397_1_, ItemStack.EMPTY);
   }

   public Inventory(ItemStack... p_i50398_1_) {
      this.size = p_i50398_1_.length;
      this.items = NonNullList.of(ItemStack.EMPTY, p_i50398_1_);
   }

   public void addListener(IInventoryChangedListener p_110134_1_) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(p_110134_1_);
   }

   public void removeListener(IInventoryChangedListener p_110132_1_) {
      this.listeners.remove(p_110132_1_);
   }

   public ItemStack getItem(int p_70301_1_) {
      return p_70301_1_ >= 0 && p_70301_1_ < this.items.size() ? this.items.get(p_70301_1_) : ItemStack.EMPTY;
   }

   public List<ItemStack> removeAllItems() {
      List<ItemStack> list = this.items.stream().filter((p_233544_0_) -> {
         return !p_233544_0_.isEmpty();
      }).collect(Collectors.toList());
      this.clearContent();
      return list;
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      ItemStack itemstack = ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemType(Item p_223374_1_, int p_223374_2_) {
      ItemStack itemstack = new ItemStack(p_223374_1_, 0);

      for(int i = this.size - 1; i >= 0; --i) {
         ItemStack itemstack1 = this.getItem(i);
         if (itemstack1.getItem().equals(p_223374_1_)) {
            int j = p_223374_2_ - itemstack.getCount();
            ItemStack itemstack2 = itemstack1.split(j);
            itemstack.grow(itemstack2.getCount());
            if (itemstack.getCount() == p_223374_2_) {
               break;
            }
         }
      }

      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack addItem(ItemStack p_174894_1_) {
      ItemStack itemstack = p_174894_1_.copy();
      this.moveItemToOccupiedSlotsWithSameType(itemstack);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.moveItemToEmptySlots(itemstack);
         return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
      }
   }

   public boolean canAddItem(ItemStack p_233541_1_) {
      boolean flag = false;

      for(ItemStack itemstack : this.items) {
         if (itemstack.isEmpty() || this.isSameItem(itemstack, p_233541_1_) && itemstack.getCount() < itemstack.getMaxStackSize()) {
            flag = true;
            break;
         }
      }

      return flag;
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      ItemStack itemstack = this.items.get(p_70304_1_);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.items.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      this.items.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getMaxStackSize()) {
         p_70299_2_.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   public int getContainerSize() {
      return this.size;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setChanged() {
      if (this.listeners != null) {
         for(IInventoryChangedListener iinventorychangedlistener : this.listeners) {
            iinventorychangedlistener.containerChanged(this);
         }
      }

   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
      this.setChanged();
   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      for(ItemStack itemstack : this.items) {
         p_194018_1_.accountStack(itemstack);
      }

   }

   public String toString() {
      return this.items.stream().filter((p_223371_0_) -> {
         return !p_223371_0_.isEmpty();
      }).collect(Collectors.toList()).toString();
   }

   private void moveItemToEmptySlots(ItemStack p_223375_1_) {
      for(int i = 0; i < this.size; ++i) {
         ItemStack itemstack = this.getItem(i);
         if (itemstack.isEmpty()) {
            this.setItem(i, p_223375_1_.copy());
            p_223375_1_.setCount(0);
            return;
         }
      }

   }

   private void moveItemToOccupiedSlotsWithSameType(ItemStack p_223372_1_) {
      for(int i = 0; i < this.size; ++i) {
         ItemStack itemstack = this.getItem(i);
         if (this.isSameItem(itemstack, p_223372_1_)) {
            this.moveItemsBetweenStacks(p_223372_1_, itemstack);
            if (p_223372_1_.isEmpty()) {
               return;
            }
         }
      }

   }

   private boolean isSameItem(ItemStack p_233540_1_, ItemStack p_233540_2_) {
      return p_233540_1_.getItem() == p_233540_2_.getItem() && ItemStack.tagMatches(p_233540_1_, p_233540_2_);
   }

   private void moveItemsBetweenStacks(ItemStack p_223373_1_, ItemStack p_223373_2_) {
      int i = Math.min(this.getMaxStackSize(), p_223373_2_.getMaxStackSize());
      int j = Math.min(p_223373_1_.getCount(), i - p_223373_2_.getCount());
      if (j > 0) {
         p_223373_2_.grow(j);
         p_223373_1_.shrink(j);
         this.setChanged();
      }

   }

   public void fromTag(ListNBT p_70486_1_) {
      for(int i = 0; i < p_70486_1_.size(); ++i) {
         ItemStack itemstack = ItemStack.of(p_70486_1_.getCompound(i));
         if (!itemstack.isEmpty()) {
            this.addItem(itemstack);
         }
      }

   }

   public ListNBT createTag() {
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemstack = this.getItem(i);
         if (!itemstack.isEmpty()) {
            listnbt.add(itemstack.save(new CompoundNBT()));
         }
      }

      return listnbt;
   }
}
