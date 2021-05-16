package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class FishingRodItem extends Item implements IVanishable {
   public FishingRodItem(Item.Properties p_i48494_1_) {
      super(p_i48494_1_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      if (p_77659_2_.fishing != null) {
         if (!p_77659_1_.isClientSide) {
            int i = p_77659_2_.fishing.retrieve(itemstack);
            itemstack.hurtAndBreak(i, p_77659_2_, (p_220000_1_) -> {
               p_220000_1_.broadcastBreakEvent(p_77659_3_);
            });
         }

         p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.getX(), p_77659_2_.getY(), p_77659_2_.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      } else {
         p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.getX(), p_77659_2_.getY(), p_77659_2_.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!p_77659_1_.isClientSide) {
            int k = EnchantmentHelper.getFishingSpeedBonus(itemstack);
            int j = EnchantmentHelper.getFishingLuckBonus(itemstack);
            p_77659_1_.addFreshEntity(new FishingBobberEntity(p_77659_2_, p_77659_1_, j, k));
         }

         p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
      }

      return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
