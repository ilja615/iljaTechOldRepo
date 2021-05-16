package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DoubleSidedInventory implements IInventory {
   private final IInventory container1;
   private final IInventory container2;

   public DoubleSidedInventory(IInventory p_i50399_1_, IInventory p_i50399_2_) {
      if (p_i50399_1_ == null) {
         p_i50399_1_ = p_i50399_2_;
      }

      if (p_i50399_2_ == null) {
         p_i50399_2_ = p_i50399_1_;
      }

      this.container1 = p_i50399_1_;
      this.container2 = p_i50399_2_;
   }

   public int getContainerSize() {
      return this.container1.getContainerSize() + this.container2.getContainerSize();
   }

   public boolean isEmpty() {
      return this.container1.isEmpty() && this.container2.isEmpty();
   }

   public boolean contains(IInventory p_90010_1_) {
      return this.container1 == p_90010_1_ || this.container2 == p_90010_1_;
   }

   public ItemStack getItem(int p_70301_1_) {
      return p_70301_1_ >= this.container1.getContainerSize() ? this.container2.getItem(p_70301_1_ - this.container1.getContainerSize()) : this.container1.getItem(p_70301_1_);
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      return p_70298_1_ >= this.container1.getContainerSize() ? this.container2.removeItem(p_70298_1_ - this.container1.getContainerSize(), p_70298_2_) : this.container1.removeItem(p_70298_1_, p_70298_2_);
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      return p_70304_1_ >= this.container1.getContainerSize() ? this.container2.removeItemNoUpdate(p_70304_1_ - this.container1.getContainerSize()) : this.container1.removeItemNoUpdate(p_70304_1_);
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      if (p_70299_1_ >= this.container1.getContainerSize()) {
         this.container2.setItem(p_70299_1_ - this.container1.getContainerSize(), p_70299_2_);
      } else {
         this.container1.setItem(p_70299_1_, p_70299_2_);
      }

   }

   public int getMaxStackSize() {
      return this.container1.getMaxStackSize();
   }

   public void setChanged() {
      this.container1.setChanged();
      this.container2.setChanged();
   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      return this.container1.stillValid(p_70300_1_) && this.container2.stillValid(p_70300_1_);
   }

   public void startOpen(PlayerEntity p_174889_1_) {
      this.container1.startOpen(p_174889_1_);
      this.container2.startOpen(p_174889_1_);
   }

   public void stopOpen(PlayerEntity p_174886_1_) {
      this.container1.stopOpen(p_174886_1_);
      this.container2.stopOpen(p_174886_1_);
   }

   public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
      return p_94041_1_ >= this.container1.getContainerSize() ? this.container2.canPlaceItem(p_94041_1_ - this.container1.getContainerSize(), p_94041_2_) : this.container1.canPlaceItem(p_94041_1_, p_94041_2_);
   }

   public void clearContent() {
      this.container1.clearContent();
      this.container2.clearContent();
   }
}
