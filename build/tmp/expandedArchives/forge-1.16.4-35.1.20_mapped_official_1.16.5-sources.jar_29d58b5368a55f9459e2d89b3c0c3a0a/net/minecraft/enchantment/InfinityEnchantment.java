package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class InfinityEnchantment extends Enchantment {
   public InfinityEnchantment(Enchantment.Rarity p_i46736_1_, EquipmentSlotType... p_i46736_2_) {
      super(p_i46736_1_, EnchantmentType.BOW, p_i46736_2_);
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
      return p_77326_1_ instanceof MendingEnchantment ? false : super.checkCompatibility(p_77326_1_);
   }
}
