package net.minecraft.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public abstract class AbstractRepairContainer extends Container {
   protected final CraftResultInventory resultSlots = new CraftResultInventory();
   protected final IInventory inputSlots = new Inventory(2) {
      public void setChanged() {
         super.setChanged();
         AbstractRepairContainer.this.slotsChanged(this);
      }
   };
   protected final IWorldPosCallable access;
   protected final PlayerEntity player;

   protected abstract boolean mayPickup(PlayerEntity p_230303_1_, boolean p_230303_2_);

   protected abstract ItemStack onTake(PlayerEntity p_230301_1_, ItemStack p_230301_2_);

   protected abstract boolean isValidBlock(BlockState p_230302_1_);

   public AbstractRepairContainer(@Nullable ContainerType<?> p_i231587_1_, int p_i231587_2_, PlayerInventory p_i231587_3_, IWorldPosCallable p_i231587_4_) {
      super(p_i231587_1_, p_i231587_2_);
      this.access = p_i231587_4_;
      this.player = p_i231587_3_.player;
      this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlot(new Slot(this.resultSlots, 2, 134, 47) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            return false;
         }

         public boolean mayPickup(PlayerEntity p_82869_1_) {
            return AbstractRepairContainer.this.mayPickup(p_82869_1_, this.hasItem());
         }

         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
            return AbstractRepairContainer.this.onTake(p_190901_1_, p_190901_2_);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i231587_3_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i231587_3_, k, 8 + k * 18, 142));
      }

   }

   public abstract void createResult();

   public void slotsChanged(IInventory p_75130_1_) {
      super.slotsChanged(p_75130_1_);
      if (p_75130_1_ == this.inputSlots) {
         this.createResult();
      }

   }

   public void removed(PlayerEntity p_75134_1_) {
      super.removed(p_75134_1_);
      this.access.execute((p_234647_2_, p_234647_3_) -> {
         this.clearContainer(p_75134_1_, p_234647_2_, this.inputSlots);
      });
   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return this.access.evaluate((p_234646_2_, p_234646_3_) -> {
         return !this.isValidBlock(p_234646_2_.getBlockState(p_234646_3_)) ? false : p_75145_1_.distanceToSqr((double)p_234646_3_.getX() + 0.5D, (double)p_234646_3_.getY() + 0.5D, (double)p_234646_3_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   protected boolean shouldQuickMoveToAdditionalSlot(ItemStack p_241210_1_) {
      return false;
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 2) {
            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
            if (p_82846_2_ >= 3 && p_82846_2_ < 39) {
               int i = this.shouldQuickMoveToAdditionalSlot(itemstack) ? 1 : 0;
               if (!this.moveItemStackTo(itemstack1, i, 2, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
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
}
