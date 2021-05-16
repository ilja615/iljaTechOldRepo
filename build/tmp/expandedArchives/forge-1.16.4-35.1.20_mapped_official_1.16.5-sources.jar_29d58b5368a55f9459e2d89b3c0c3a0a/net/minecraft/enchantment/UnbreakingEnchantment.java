package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class UnbreakingEnchantment extends Enchantment {
   protected UnbreakingEnchantment(Enchantment.Rarity p_i46733_1_, EquipmentSlotType... p_i46733_2_) {
      super(p_i46733_1_, EnchantmentType.BREAKABLE, p_i46733_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 5 + (p_77321_1_ - 1) * 8;
   }

   public int getMaxCost(int p_223551_1_) {
      return super.getMinCost(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack p_92089_1_) {
      return p_92089_1_.isDamageableItem() ? true : super.canEnchant(p_92089_1_);
   }

   public static boolean shouldIgnoreDurabilityDrop(ItemStack p_92097_0_, int p_92097_1_, Random p_92097_2_) {
      if (p_92097_0_.getItem() instanceof ArmorItem && p_92097_2_.nextFloat() < 0.6F) {
         return false;
      } else {
         return p_92097_2_.nextInt(p_92097_1_ + 1) > 0;
      }
   }
}
