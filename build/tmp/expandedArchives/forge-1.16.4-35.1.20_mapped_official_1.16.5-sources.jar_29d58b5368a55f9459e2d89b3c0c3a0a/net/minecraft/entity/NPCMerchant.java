package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NPCMerchant implements IMerchant {
   private final MerchantInventory container;
   private final PlayerEntity source;
   private MerchantOffers offers = new MerchantOffers();
   private int xp;

   public NPCMerchant(PlayerEntity p_i50184_1_) {
      this.source = p_i50184_1_;
      this.container = new MerchantInventory(this);
   }

   @Nullable
   public PlayerEntity getTradingPlayer() {
      return this.source;
   }

   public void setTradingPlayer(@Nullable PlayerEntity p_70932_1_) {
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   @OnlyIn(Dist.CLIENT)
   public void overrideOffers(@Nullable MerchantOffers p_213703_1_) {
      this.offers = p_213703_1_;
   }

   public void notifyTrade(MerchantOffer p_213704_1_) {
      p_213704_1_.increaseUses();
   }

   public void notifyTradeUpdated(ItemStack p_110297_1_) {
   }

   public World getLevel() {
      return this.source.level;
   }

   public int getVillagerXp() {
      return this.xp;
   }

   public void overrideXp(int p_213702_1_) {
      this.xp = p_213702_1_;
   }

   public boolean showProgressBar() {
      return true;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }
}
