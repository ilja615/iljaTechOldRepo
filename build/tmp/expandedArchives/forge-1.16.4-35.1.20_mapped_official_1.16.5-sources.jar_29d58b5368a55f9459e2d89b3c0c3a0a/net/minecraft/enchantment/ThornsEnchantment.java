package net.minecraft.enchantment;

import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ThornsEnchantment extends Enchantment {
   public ThornsEnchantment(Enchantment.Rarity p_i46722_1_, EquipmentSlotType... p_i46722_2_) {
      super(p_i46722_1_, EnchantmentType.ARMOR_CHEST, p_i46722_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 10 + 20 * (p_77321_1_ - 1);
   }

   public int getMaxCost(int p_223551_1_) {
      return super.getMinCost(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack p_92089_1_) {
      return p_92089_1_.getItem() instanceof ArmorItem ? true : super.canEnchant(p_92089_1_);
   }

   public void doPostHurt(LivingEntity p_151367_1_, Entity p_151367_2_, int p_151367_3_) {
      Random random = p_151367_1_.getRandom();
      Entry<EquipmentSlotType, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, p_151367_1_);
      if (shouldHit(p_151367_3_, random)) {
         if (p_151367_2_ != null) {
            p_151367_2_.hurt(DamageSource.thorns(p_151367_1_), (float)getDamage(p_151367_3_, random));
         }

         if (entry != null) {
            entry.getValue().hurtAndBreak(2, p_151367_1_, (p_222183_1_) -> {
               p_222183_1_.broadcastBreakEvent(entry.getKey());
            });
         }
      }

   }

   public static boolean shouldHit(int p_92094_0_, Random p_92094_1_) {
      if (p_92094_0_ <= 0) {
         return false;
      } else {
         return p_92094_1_.nextFloat() < 0.15F * (float)p_92094_0_;
      }
   }

   public static int getDamage(int p_92095_0_, Random p_92095_1_) {
      return p_92095_0_ > 10 ? p_92095_0_ - 10 : 1 + p_92095_1_.nextInt(4);
   }
}
