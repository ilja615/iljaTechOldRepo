package net.minecraft.entity.merchant;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IMerchant {
   void setTradingPlayer(@Nullable PlayerEntity p_70932_1_);

   @Nullable
   PlayerEntity getTradingPlayer();

   MerchantOffers getOffers();

   @OnlyIn(Dist.CLIENT)
   void overrideOffers(@Nullable MerchantOffers p_213703_1_);

   void notifyTrade(MerchantOffer p_213704_1_);

   void notifyTradeUpdated(ItemStack p_110297_1_);

   World getLevel();

   int getVillagerXp();

   void overrideXp(int p_213702_1_);

   boolean showProgressBar();

   SoundEvent getNotifyTradeSound();

   default boolean canRestock() {
      return false;
   }

   default void openTradingScreen(PlayerEntity p_213707_1_, ITextComponent p_213707_2_, int p_213707_3_) {
      OptionalInt optionalint = p_213707_1_.openMenu(new SimpleNamedContainerProvider((p_213701_1_, p_213701_2_, p_213701_3_) -> {
         return new MerchantContainer(p_213701_1_, p_213701_2_, this);
      }, p_213707_2_));
      if (optionalint.isPresent()) {
         MerchantOffers merchantoffers = this.getOffers();
         if (!merchantoffers.isEmpty()) {
            p_213707_1_.sendMerchantOffers(optionalint.getAsInt(), merchantoffers, p_213707_3_, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
         }
      }

   }
}
