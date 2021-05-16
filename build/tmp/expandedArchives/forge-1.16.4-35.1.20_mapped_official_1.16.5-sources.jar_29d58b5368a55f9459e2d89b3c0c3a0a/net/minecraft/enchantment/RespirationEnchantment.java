package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class RespirationEnchantment extends Enchantment {
   public RespirationEnchantment(Enchantment.Rarity p_i46724_1_, EquipmentSlotType... p_i46724_2_) {
      super(p_i46724_1_, EnchantmentType.ARMOR_HEAD, p_i46724_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 10 * p_77321_1_;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 30;
   }

   public int getMaxLevel() {
      return 3;
   }
}
