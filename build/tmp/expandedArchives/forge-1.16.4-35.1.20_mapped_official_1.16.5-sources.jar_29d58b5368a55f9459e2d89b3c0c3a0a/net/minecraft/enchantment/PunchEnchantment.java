package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class PunchEnchantment extends Enchantment {
   public PunchEnchantment(Enchantment.Rarity p_i46735_1_, EquipmentSlotType... p_i46735_2_) {
      super(p_i46735_1_, EnchantmentType.BOW, p_i46735_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 12 + (p_77321_1_ - 1) * 20;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 25;
   }

   public int getMaxLevel() {
      return 2;
   }
}
