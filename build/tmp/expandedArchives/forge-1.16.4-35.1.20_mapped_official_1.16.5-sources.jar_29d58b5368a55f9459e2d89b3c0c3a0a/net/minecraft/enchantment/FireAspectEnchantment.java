package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class FireAspectEnchantment extends Enchantment {
   protected FireAspectEnchantment(Enchantment.Rarity p_i46730_1_, EquipmentSlotType... p_i46730_2_) {
      super(p_i46730_1_, EnchantmentType.WEAPON, p_i46730_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 10 + 20 * (p_77321_1_ - 1);
   }

   public int getMaxCost(int p_223551_1_) {
      return super.getMinCost(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 2;
   }
}
