package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class AquaAffinityEnchantment extends Enchantment {
   public AquaAffinityEnchantment(Enchantment.Rarity p_i46719_1_, EquipmentSlotType... p_i46719_2_) {
      super(p_i46719_1_, EnchantmentType.ARMOR_HEAD, p_i46719_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 1;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 40;
   }

   public int getMaxLevel() {
      return 1;
   }
}
