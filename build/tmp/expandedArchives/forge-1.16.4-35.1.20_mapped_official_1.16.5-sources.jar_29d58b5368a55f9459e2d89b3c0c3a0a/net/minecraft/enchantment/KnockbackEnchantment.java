package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class KnockbackEnchantment extends Enchantment {
   protected KnockbackEnchantment(Enchantment.Rarity p_i46727_1_, EquipmentSlotType... p_i46727_2_) {
      super(p_i46727_1_, EnchantmentType.WEAPON, p_i46727_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 5 + 20 * (p_77321_1_ - 1);
   }

   public int getMaxCost(int p_223551_1_) {
      return super.getMinCost(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 2;
   }
}
