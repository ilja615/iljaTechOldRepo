package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.Rarity p_i46725_1_, EquipmentSlotType... p_i46725_2_) {
      super(p_i46725_1_, EnchantmentType.BREAKABLE, p_i46725_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return p_77321_1_ * 25;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 50;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }
}
