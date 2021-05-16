package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantInventory implements IInventory {
   private final IMerchant merchant;
   private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
   @Nullable
   private MerchantOffer activeOffer;
   private int selectionHint;
   private int futureXp;

   public MerchantInventory(IMerchant p_i50071_1_) {
      this.merchant = p_i50071_1_;
   }

   public int getContainerSize() {
      return this.itemStacks.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.itemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int p_70301_1_) {
      return this.itemStacks.get(p_70301_1_);
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      ItemStack itemstack = this.itemStacks.get(p_70298_1_);
      if (p_70298_1_ == 2 && !itemstack.isEmpty()) {
         return ItemStackHelper.removeItem(this.itemStacks, p_70298_1_, itemstack.getCount());
      } else {
         ItemStack itemstack1 = ItemStackHelper.removeItem(this.itemStacks, p_70298_1_, p_70298_2_);
         if (!itemstack1.isEmpty() && this.isPaymentSlot(p_70298_1_)) {
            this.updateSellItem();
         }

         return itemstack1;
      }
   }

   private boolean isPaymentSlot(int p_70469_1_) {
      return p_70469_1_ == 0 || p_70469_1_ == 1;
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      return ItemStackHelper.takeItem(this.itemStacks, p_70304_1_);
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      this.itemStacks.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getMaxStackSize()) {
         p_70299_2_.setCount(this.getMaxStackSize());
      }

      if (this.isPaymentSlot(p_70299_1_)) {
         this.updateSellItem();
      }

   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      return this.merchant.getTradingPlayer() == p_70300_1_;
   }

   public void setChanged() {
      this.updateSellItem();
   }

   public void updateSellItem() {
      this.activeOffer = null;
      ItemStack itemstack;
      ItemStack itemstack1;
      if (this.itemStacks.get(0).isEmpty()) {
         itemstack = this.itemStacks.get(1);
         itemstack1 = ItemStack.EMPTY;
      } else {
         itemstack = this.itemStacks.get(0);
         itemstack1 = this.itemStacks.get(1);
      }

      if (itemstack.isEmpty()) {
         this.setItem(2, ItemStack.EMPTY);
         this.futureXp = 0;
      } else {
         MerchantOffers merchantoffers = this.merchant.getOffers();
         if (!merchantoffers.isEmpty()) {
            MerchantOffer merchantoffer = merchantoffers.getRecipeFor(itemstack, itemstack1, this.selectionHint);
            if (merchantoffer == null || merchantoffer.isOutOfStock()) {
               this.activeOffer = merchantoffer;
               merchantoffer = merchantoffers.getRecipeFor(itemstack1, itemstack, this.selectionHint);
            }

            if (merchantoffer != null && !merchantoffer.isOutOfStock()) {
               this.activeOffer = merchantoffer;
               this.setItem(2, merchantoffer.assemble());
               this.futureXp = merchantoffer.getXp();
            } else {
               this.setItem(2, ItemStack.EMPTY);
               this.futureXp = 0;
            }
         }

         this.merchant.notifyTradeUpdated(this.getItem(2));
      }
   }

   @Nullable
   public MerchantOffer getActiveOffer() {
      return this.activeOffer;
   }

   public void setSelectionHint(int p_70471_1_) {
      this.selectionHint = p_70471_1_;
      this.updateSellItem();
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public int getFutureXp() {
      return this.futureXp;
   }
}
