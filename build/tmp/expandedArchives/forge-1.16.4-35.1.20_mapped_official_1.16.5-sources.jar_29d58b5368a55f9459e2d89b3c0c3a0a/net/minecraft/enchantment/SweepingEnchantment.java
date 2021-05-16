package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class SweepingEnchantment extends Enchantment {
   public SweepingEnchantment(Enchantment.Rarity p_i47366_1_, EquipmentSlotType... p_i47366_2_) {
      super(p_i47366_1_, EnchantmentType.WEAPON, p_i47366_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 5 + (p_77321_1_ - 1) * 9;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public static float getSweepingDamageRatio(int p_191526_0_) {
      return 1.0F - 1.0F / (float)(p_191526_0_ + 1);
   }
}
