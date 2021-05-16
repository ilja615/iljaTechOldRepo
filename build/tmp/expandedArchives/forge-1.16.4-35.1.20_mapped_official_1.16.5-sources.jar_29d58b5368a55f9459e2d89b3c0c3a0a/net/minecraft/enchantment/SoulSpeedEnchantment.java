package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class SoulSpeedEnchantment extends Enchantment {
   public SoulSpeedEnchantment(Enchantment.Rarity p_i231601_1_, EquipmentSlotType... p_i231601_2_) {
      super(p_i231601_1_, EnchantmentType.ARMOR_FEET, p_i231601_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return p_77321_1_ * 10;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 15;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public boolean isTradeable() {
      return false;
   }

   public boolean isDiscoverable() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }
}
