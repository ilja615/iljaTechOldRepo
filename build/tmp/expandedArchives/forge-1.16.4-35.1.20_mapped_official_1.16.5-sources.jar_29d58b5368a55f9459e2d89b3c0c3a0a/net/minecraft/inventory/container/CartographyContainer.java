package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.storage.MapData;

public class CartographyContainer extends Container {
   private final IWorldPosCallable access;
   private long lastSoundTime;
   public final IInventory container = new Inventory(2) {
      public void setChanged() {
         CartographyContainer.this.slotsChanged(this);
         super.setChanged();
      }
   };
   private final CraftResultInventory resultContainer = new CraftResultInventory() {
      public void setChanged() {
         CartographyContainer.this.slotsChanged(this);
         super.setChanged();
      }
   };

   public CartographyContainer(int p_i50093_1_, PlayerInventory p_i50093_2_) {
      this(p_i50093_1_, p_i50093_2_, IWorldPosCallable.NULL);
   }

   public CartographyContainer(int p_i50094_1_, PlayerInventory p_i50094_2_, final IWorldPosCallable p_i50094_3_) {
      super(ContainerType.CARTOGRAPHY_TABLE, p_i50094_1_);
      this.access = p_i50094_3_;
      this.addSlot(new Slot(this.container, 0, 15, 15) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() == Items.FILLED_MAP;
         }
      });
      this.addSlot(new Slot(this.container, 1, 15, 52) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            Item item = p_75214_1_.getItem();
            return item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE;
         }
      });
      this.addSlot(new Slot(this.resultContainer, 2, 145, 39) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            return false;
         }

         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
            CartographyContainer.this.slots.get(0).remove(1);
            CartographyContainer.this.slots.get(1).remove(1);
            p_190901_2_.getItem().onCraftedBy(p_190901_2_, p_190901_1_.level, p_190901_1_);
            p_i50094_3_.execute((p_242385_1_, p_242385_2_) -> {
               long l = p_242385_1_.getGameTime();
               if (CartographyContainer.this.lastSoundTime != l) {
                  p_242385_1_.playSound((PlayerEntity)null, p_242385_2_, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  CartographyContainer.this.lastSoundTime = l;
               }

            });
            return super.onTake(p_190901_1_, p_190901_2_);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50094_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50094_2_, k, 8 + k * 18, 142));
      }

   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return stillValid(this.access, p_75145_1_, Blocks.CARTOGRAPHY_TABLE);
   }

   public void slotsChanged(IInventory p_75130_1_) {
      ItemStack itemstack = this.container.getItem(0);
      ItemStack itemstack1 = this.container.getItem(1);
      ItemStack itemstack2 = this.resultContainer.getItem(2);
      if (itemstack2.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty()) {
         if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            this.setupResultSlot(itemstack, itemstack1, itemstack2);
         }
      } else {
         this.resultContainer.removeItemNoUpdate(2);
      }

   }

   private void setupResultSlot(ItemStack p_216993_1_, ItemStack p_216993_2_, ItemStack p_216993_3_) {
      this.access.execute((p_216996_4_, p_216996_5_) -> {
         Item item = p_216993_2_.getItem();
         MapData mapdata = FilledMapItem.getSavedData(p_216993_1_, p_216996_4_);
         if (mapdata != null) {
            ItemStack itemstack;
            if (item == Items.PAPER && !mapdata.locked && mapdata.scale < 4) {
               itemstack = p_216993_1_.copy();
               itemstack.setCount(1);
               itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
               this.broadcastChanges();
            } else if (item == Items.GLASS_PANE && !mapdata.locked) {
               itemstack = p_216993_1_.copy();
               itemstack.setCount(1);
               itemstack.getOrCreateTag().putBoolean("map_to_lock", true);
               this.broadcastChanges();
            } else {
               if (item != Items.MAP) {
                  this.resultContainer.removeItemNoUpdate(2);
                  this.broadcastChanges();
                  return;
               }

               itemstack = p_216993_1_.copy();
               itemstack.setCount(2);
               this.broadcastChanges();
            }

            if (!ItemStack.matches(itemstack, p_216993_3_)) {
               this.resultContainer.setItem(2, itemstack);
               this.broadcastChanges();
            }

         }
      });
   }

   public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.container != this.resultContainer && super.canTakeItemForPickAll(p_94530_1_, p_94530_2_);
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         Item item = itemstack1.getItem();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 2) {
            item.onCraftedBy(itemstack1, p_82846_1_.level, p_82846_1_);
            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_82846_2_ != 1 && p_82846_2_ != 0) {
            if (item == Items.FILLED_MAP) {
               if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (item != Items.PAPER && item != Items.MAP && item != Items.GLASS_PANE) {
               if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
                  if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         }

         slot.setChanged();
         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_82846_1_, itemstack1);
         this.broadcastChanges();
      }

      return itemstack;
   }

   public void removed(PlayerEntity p_75134_1_) {
      super.removed(p_75134_1_);
      this.resultContainer.removeItemNoUpdate(2);
      this.access.execute((p_216995_2_, p_216995_3_) -> {
         this.clearContainer(p_75134_1_, p_75134_1_.level, this.container);
      });
   }
}
