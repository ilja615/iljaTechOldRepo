package net.minecraft.inventory.container;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BrewingStandContainer extends Container {
   private final IInventory brewingStand;
   private final IIntArray brewingStandData;
   private final Slot ingredientSlot;

   public BrewingStandContainer(int p_i50095_1_, PlayerInventory p_i50095_2_) {
      this(p_i50095_1_, p_i50095_2_, new Inventory(5), new IntArray(2));
   }

   public BrewingStandContainer(int p_i50096_1_, PlayerInventory p_i50096_2_, IInventory p_i50096_3_, IIntArray p_i50096_4_) {
      super(ContainerType.BREWING_STAND, p_i50096_1_);
      checkContainerSize(p_i50096_3_, 5);
      checkContainerDataCount(p_i50096_4_, 2);
      this.brewingStand = p_i50096_3_;
      this.brewingStandData = p_i50096_4_;
      this.addSlot(new BrewingStandContainer.PotionSlot(p_i50096_3_, 0, 56, 51));
      this.addSlot(new BrewingStandContainer.PotionSlot(p_i50096_3_, 1, 79, 58));
      this.addSlot(new BrewingStandContainer.PotionSlot(p_i50096_3_, 2, 102, 51));
      this.ingredientSlot = this.addSlot(new BrewingStandContainer.IngredientSlot(p_i50096_3_, 3, 79, 17));
      this.addSlot(new BrewingStandContainer.FuelSlot(p_i50096_3_, 4, 17, 17));
      this.addDataSlots(p_i50096_4_);

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50096_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50096_2_, k, 8 + k * 18, 142));
      }

   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return this.brewingStand.stillValid(p_75145_1_);
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if ((p_82846_2_ < 0 || p_82846_2_ > 2) && p_82846_2_ != 3 && p_82846_2_ != 4) {
            if (BrewingStandContainer.FuelSlot.mayPlaceItem(itemstack)) {
               if (this.moveItemStackTo(itemstack1, 4, 5, false) || this.ingredientSlot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.ingredientSlot.mayPlace(itemstack1)) {
               if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (BrewingStandContainer.PotionSlot.mayPlaceItem(itemstack) && itemstack.getCount() == 1) {
               if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 5 && p_82846_2_ < 32) {
               if (!this.moveItemStackTo(itemstack1, 32, 41, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 32 && p_82846_2_ < 41) {
               if (!this.moveItemStackTo(itemstack1, 5, 32, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(itemstack1, 5, 41, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(itemstack1, 5, 41, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
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
   public int getFuel() {
      return this.brewingStandData.get(1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrewingTicks() {
      return this.brewingStandData.get(0);
   }

   static class FuelSlot extends Slot {
      public FuelSlot(IInventory p_i47070_1_, int p_i47070_2_, int p_i47070_3_, int p_i47070_4_) {
         super(p_i47070_1_, p_i47070_2_, p_i47070_3_, p_i47070_4_);
      }

      public boolean mayPlace(ItemStack p_75214_1_) {
         return mayPlaceItem(p_75214_1_);
      }

      public static boolean mayPlaceItem(ItemStack p_185004_0_) {
         return p_185004_0_.getItem() == Items.BLAZE_POWDER;
      }

      public int getMaxStackSize() {
         return 64;
      }
   }

   static class IngredientSlot extends Slot {
      public IngredientSlot(IInventory p_i47069_1_, int p_i47069_2_, int p_i47069_3_, int p_i47069_4_) {
         super(p_i47069_1_, p_i47069_2_, p_i47069_3_, p_i47069_4_);
      }

      public boolean mayPlace(ItemStack p_75214_1_) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(p_75214_1_);
      }

      public int getMaxStackSize() {
         return 64;
      }
   }

   static class PotionSlot extends Slot {
      public PotionSlot(IInventory p_i47598_1_, int p_i47598_2_, int p_i47598_3_, int p_i47598_4_) {
         super(p_i47598_1_, p_i47598_2_, p_i47598_3_, p_i47598_4_);
      }

      public boolean mayPlace(ItemStack p_75214_1_) {
         return mayPlaceItem(p_75214_1_);
      }

      public int getMaxStackSize() {
         return 1;
      }

      public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
         Potion potion = PotionUtils.getPotion(p_190901_2_);
         if (p_190901_1_ instanceof ServerPlayerEntity) {
            net.minecraftforge.event.ForgeEventFactory.onPlayerBrewedPotion(p_190901_1_, p_190901_2_);
            CriteriaTriggers.BREWED_POTION.trigger((ServerPlayerEntity)p_190901_1_, potion);
         }

         super.onTake(p_190901_1_, p_190901_2_);
         return p_190901_2_;
      }

      public static boolean mayPlaceItem(ItemStack p_75243_0_) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(p_75243_0_);
      }
   }
}
