package net.minecraft.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeaconContainer extends Container {
   private final IInventory beacon = new Inventory(1) {
      public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
         return p_94041_2_.getItem().is(ItemTags.BEACON_PAYMENT_ITEMS);
      }

      public int getMaxStackSize() {
         return 1;
      }
   };
   private final BeaconContainer.BeaconSlot paymentSlot;
   private final IWorldPosCallable access;
   private final IIntArray beaconData;

   public BeaconContainer(int p_i50099_1_, IInventory p_i50099_2_) {
      this(p_i50099_1_, p_i50099_2_, new IntArray(3), IWorldPosCallable.NULL);
   }

   public BeaconContainer(int p_i50100_1_, IInventory p_i50100_2_, IIntArray p_i50100_3_, IWorldPosCallable p_i50100_4_) {
      super(ContainerType.BEACON, p_i50100_1_);
      checkContainerDataCount(p_i50100_3_, 3);
      this.beaconData = p_i50100_3_;
      this.access = p_i50100_4_;
      this.paymentSlot = new BeaconContainer.BeaconSlot(this.beacon, 0, 136, 110);
      this.addSlot(this.paymentSlot);
      this.addDataSlots(p_i50100_3_);
      int i = 36;
      int j = 137;

      for(int k = 0; k < 3; ++k) {
         for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(p_i50100_2_, l + k * 9 + 9, 36 + l * 18, 137 + k * 18));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(p_i50100_2_, i1, 36 + i1 * 18, 195));
      }

   }

   public void removed(PlayerEntity p_75134_1_) {
      super.removed(p_75134_1_);
      if (!p_75134_1_.level.isClientSide) {
         ItemStack itemstack = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
         if (!itemstack.isEmpty()) {
            p_75134_1_.drop(itemstack, false);
         }

      }
   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return stillValid(this.access, p_75145_1_, Blocks.BEACON);
   }

   public void setData(int p_75137_1_, int p_75137_2_) {
      super.setData(p_75137_1_, p_75137_2_);
      this.broadcastChanges();
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 0) {
            if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (this.moveItemStackTo(itemstack1, 0, 1, false)) { //Forge Fix Shift Clicking in beacons with stacks larger then 1.
            return ItemStack.EMPTY;
         } else if (p_82846_2_ >= 1 && p_82846_2_ < 28) {
            if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 28 && p_82846_2_ < 37) {
            if (!this.moveItemStackTo(itemstack1, 1, 28, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 1, 37, false)) {
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

   @OnlyIn(Dist.CLIENT)
   public int getLevels() {
      return this.beaconData.get(0);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Effect getPrimaryEffect() {
      return Effect.byId(this.beaconData.get(1));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Effect getSecondaryEffect() {
      return Effect.byId(this.beaconData.get(2));
   }

   public void updateEffects(int p_216966_1_, int p_216966_2_) {
      if (this.paymentSlot.hasItem()) {
         this.beaconData.set(1, p_216966_1_);
         this.beaconData.set(2, p_216966_2_);
         this.paymentSlot.remove(1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasPayment() {
      return !this.beacon.getItem(0).isEmpty();
   }

   class BeaconSlot extends Slot {
      public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {
         super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
      }

      public boolean mayPlace(ItemStack p_75214_1_) {
         return p_75214_1_.getItem().is(ItemTags.BEACON_PAYMENT_ITEMS);
      }

      public int getMaxStackSize() {
         return 1;
      }
   }
}
