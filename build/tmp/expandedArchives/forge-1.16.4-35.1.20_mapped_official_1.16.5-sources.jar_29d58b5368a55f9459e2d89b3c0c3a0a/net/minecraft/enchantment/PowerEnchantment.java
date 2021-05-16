package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class PowerEnchantment extends Enchantment {
   public PowerEnchantment(Enchantment.Rarity p_i46738_1_, EquipmentSlotType... p_i46738_2_) {
      super(p_i46738_1_, EnchantmentType.BOW, p_i46738_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 1 + (p_77321_1_ - 1) * 10;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 15;
   }

   public int getMaxLevel() {
      return 5;
   }
}
