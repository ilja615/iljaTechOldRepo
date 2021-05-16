package net.minecraft.inventory.container;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.stats.Stats;

public class MerchantResultSlot extends Slot {
   private final MerchantInventory slots;
   private final PlayerEntity player;
   private int removeCount;
   private final IMerchant merchant;

   public MerchantResultSlot(PlayerEntity p_i1822_1_, IMerchant p_i1822_2_, MerchantInventory p_i1822_3_, int p_i1822_4_, int p_i1822_5_, int p_i1822_6_) {
      super(p_i1822_3_, p_i1822_4_, p_i1822_5_, p_i1822_6_);
      this.player = p_i1822_1_;
      this.merchant = p_i1822_2_;
      this.slots = p_i1822_3_;
   }

   public boolean mayPlace(ItemStack p_75214_1_) {
      return false;
   }

   public ItemStack remove(int p_75209_1_) {
      if (this.hasItem()) {
         this.removeCount += Math.min(p_75209_1_, this.getItem().getCount());
      }

      return super.remove(p_75209_1_);
   }

   protected void onQuickCraft(ItemStack p_75210_1_, int p_75210_2_) {
      this.removeCount += p_75210_2_;
      this.checkTakeAchievements(p_75210_1_);
   }

   protected void checkTakeAchievements(ItemStack p_75208_1_) {
      p_75208_1_.onCraftedBy(this.player.level, this.player, this.removeCount);
      this.removeCount = 0;
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.checkTakeAchievements(p_190901_2_);
      MerchantOffer merchantoffer = this.slots.getActiveOffer();
      if (merchantoffer != null) {
         ItemStack itemstack = this.slots.getItem(0);
         ItemStack itemstack1 = this.slots.getItem(1);
         if (merchantoffer.take(itemstack, itemstack1) || merchantoffer.take(itemstack1, itemstack)) {
            this.merchant.notifyTrade(merchantoffer);
            p_190901_1_.awardStat(Stats.TRADED_WITH_VILLAGER);
            this.slots.setItem(0, itemstack);
            this.slots.setItem(1, itemstack1);
         }

         this.merchant.overrideXp(this.merchant.getVillagerXp() + merchantoffer.getXp());
      }

      return p_190901_2_;
   }
}
