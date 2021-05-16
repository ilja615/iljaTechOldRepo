package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class SilkTouchEnchantment extends Enchantment {
   protected SilkTouchEnchantment(Enchantment.Rarity p_i46721_1_, EquipmentSlotType... p_i46721_2_) {
      super(p_i46721_1_, EnchantmentType.DIGGER, p_i46721_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 15;
   }

   public int getMaxCost(int p_223551_1_) {
      return super.getMinCost(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean checkCompatibility(Enchantment p_77326_1_) {
      return super.checkCompatibility(p_77326_1_) && p_77326_1_ != Enchantments.BLOCK_FORTUNE;
   }
}
