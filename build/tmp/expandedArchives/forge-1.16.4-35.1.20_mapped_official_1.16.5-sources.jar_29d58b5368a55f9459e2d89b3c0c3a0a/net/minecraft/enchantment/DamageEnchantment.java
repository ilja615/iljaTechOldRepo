package net.minecraft.enchantment;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class DamageEnchantment extends Enchantment {
   private static final String[] NAMES = new String[]{"all", "undead", "arthropods"};
   private static final int[] MIN_COST = new int[]{1, 5, 5};
   private static final int[] LEVEL_COST = new int[]{11, 8, 8};
   private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
   public final int type;

   public DamageEnchantment(Enchantment.Rarity p_i46734_1_, int p_i46734_2_, EquipmentSlotType... p_i46734_3_) {
      super(p_i46734_1_, EnchantmentType.WEAPON, p_i46734_3_);
      this.type = p_i46734_2_;
   }

   public int getMinCost(int p_77321_1_) {
      return MIN_COST[this.type] + (p_77321_1_ - 1) * LEVEL_COST[this.type];
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + LEVEL_COST_SPAN[this.type];
   }

   public int getMaxLevel() {
      return 5;
   }

   public float getDamageBonus(int p_152376_1_, CreatureAttribute p_152376_2_) {
      if (this.type == 0) {
         return 1.0F + (float)Math.max(0, p_152376_1_ - 1) * 0.5F;
      } else if (this.type == 1 && p_152376_2_ == CreatureAttribute.UNDEAD) {
         return (float)p_152376_1_ * 2.5F;
      } else {
         return this.type == 2 && p_152376_2_ == CreatureAttribute.ARTHROPOD ? (float)p_152376_1_ * 2.5F : 0.0F;
      }
   }

   public boolean checkCompatibility(Enchantment p_77326_1_) {
      return !(p_77326_1_ instanceof DamageEnchantment);
   }

   public boolean canEnchant(ItemStack p_92089_1_) {
      return p_92089_1_.getItem() instanceof AxeItem ? true : super.canEnchant(p_92089_1_);
   }

   public void doPostAttack(LivingEntity p_151368_1_, Entity p_151368_2_, int p_151368_3_) {
      if (p_151368_2_ instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)p_151368_2_;
         if (this.type == 2 && livingentity.getMobType() == CreatureAttribute.ARTHROPOD) {
            int i = 20 + p_151368_1_.getRandom().nextInt(10 * p_151368_3_);
            livingentity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, i, 3));
         }
      }

   }
}
