package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class MultishotEnchantment extends Enchantment {
   public MultishotEnchantment(Enchantment.Rarity p_i50017_1_, EquipmentSlotType... p_i50017_2_) {
      super(p_i50017_1_, EnchantmentType.CROSSBOW, p_i50017_2_);
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

   public boolean checkCompatibility(Enchantment p_77326_1_) {
      return super.checkCompatibility(p_77326_1_) && p_77326_1_ != Enchantments.PIERCING;
   }
}
