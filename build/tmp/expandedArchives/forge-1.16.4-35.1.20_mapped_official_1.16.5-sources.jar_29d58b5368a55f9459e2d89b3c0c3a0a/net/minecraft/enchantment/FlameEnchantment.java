package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class FlameEnchantment extends Enchantment {
   public FlameEnchantment(Enchantment.Rarity p_i46737_1_, EquipmentSlotType... p_i46737_2_) {
      super(p_i46737_1_, EnchantmentType.BOW, p_i46737_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 20;
   }

   public int getMaxCost(int p_223551_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }
}
