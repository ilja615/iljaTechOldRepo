package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.EnderChestTileEntity;

public class EnderChestInventory extends Inventory {
   private EnderChestTileEntity activeChest;

   public EnderChestInventory() {
      super(27);
   }

   public void setActiveChest(EnderChestTileEntity p_146031_1_) {
      this.activeChest = p_146031_1_;
   }

   public void fromTag(ListNBT p_70486_1_) {
      for(int i = 0; i < this.getContainerSize(); ++i) {
         this.setItem(i, ItemStack.EMPTY);
      }

      for(int k = 0; k < p_70486_1_.size(); ++k) {
         CompoundNBT compoundnbt = p_70486_1_.getCompound(k);
         int j = compoundnbt.getByte("Slot") & 255;
         if (j >= 0 && j < this.getContainerSize()) {
            this.setItem(j, ItemStack.of(compoundnbt));
         }
      }

   }

   public ListNBT createTag() {
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemstack = this.getItem(i);
         if (!itemstack.isEmpty()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Slot", (byte)i);
            itemstack.save(compoundnbt);
            listnbt.add(compoundnbt);
         }
      }

      return listnbt;
   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      return this.activeChest != null && !this.activeChest.stillValid(p_70300_1_) ? false : super.stillValid(p_70300_1_);
   }

   public void startOpen(PlayerEntity p_174889_1_) {
      if (this.activeChest != null) {
         this.activeChest.startOpen();
      }

      super.startOpen(p_174889_1_);
   }

   public void stopOpen(PlayerEntity p_174886_1_) {
      if (this.activeChest != null) {
         this.activeChest.stopOpen();
      }

      super.stopOpen(p_174886_1_);
      this.activeChest = null;
   }
}
