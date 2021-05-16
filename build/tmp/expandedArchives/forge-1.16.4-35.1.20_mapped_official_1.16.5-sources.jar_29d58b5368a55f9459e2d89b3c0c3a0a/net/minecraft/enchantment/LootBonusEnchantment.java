package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class LootBonusEnchantment extends Enchantment {
   protected LootBonusEnchantment(Enchantment.Rarity p_i46726_1_, EnchantmentType p_i46726_2_, EquipmentSlotType... p_i46726_3_) {
      super(p_i46726_1_, p_i46726_2_, p_i46726_3_);
   }

   public int getMinCost(int p_77321_1_) {
      return 15 + (p_77321_1_ - 1) * 9;
   }

   public int getMaxCost(int p_223551_1_) {
      return super.getMinCost(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean checkCompatibility(Enchantment p_77326_1_) {
      return super.checkCompatibility(p_77326_1_) && p_77326_1_ != Enchantments.SILK_TOUCH;
   }
}
