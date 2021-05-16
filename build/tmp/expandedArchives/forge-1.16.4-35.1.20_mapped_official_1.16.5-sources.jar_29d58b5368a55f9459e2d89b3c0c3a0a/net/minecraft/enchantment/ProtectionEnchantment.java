package net.minecraft.enchantment;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
   public final ProtectionEnchantment.Type type;

   public ProtectionEnchantment(Enchantment.Rarity p_i46723_1_, ProtectionEnchantment.Type p_i46723_2_, EquipmentSlotType... p_i46723_3_) {
      super(p_i46723_1_, p_i46723_2_ == ProtectionEnchantment.Type.FALL ? EnchantmentType.ARMOR_FEET : EnchantmentType.ARMOR, p_i46723_3_);
      this.type = p_i46723_2_;
   }

   public int getMinCost(int p_77321_1_) {
      return this.type.getMinCost() + (p_77321_1_ - 1) * this.type.getLevelCost();
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + this.type.getLevelCost();
   }

   public int getMaxLevel() {
      return 4;
   }

   public int getDamageProtection(int p_77318_1_, DamageSource p_77318_2_) {
      if (p_77318_2_.isBypassInvul()) {
         return 0;
      } else if (this.type == ProtectionEnchantment.Type.ALL) {
         return p_77318_1_;
      } else if (this.type == ProtectionEnchantment.Type.FIRE && p_77318_2_.isFire()) {
         return p_77318_1_ * 2;
      } else if (this.type == ProtectionEnchantment.Type.FALL && p_77318_2_ == DamageSource.FALL) {
         return p_77318_1_ * 3;
      } else if (this.type == ProtectionEnchantment.Type.EXPLOSION && p_77318_2_.isExplosion()) {
         return p_77318_1_ * 2;
      } else {
         return this.type == ProtectionEnchantment.Type.PROJECTILE && p_77318_2_.isProjectile() ? p_77318_1_ * 2 : 0;
      }
   }

   public boolean checkCompatibility(Enchantment p_77326_1_) {
      if (p_77326_1_ instanceof ProtectionEnchantment) {
         ProtectionEnchantment protectionenchantment = (ProtectionEnchantment)p_77326_1_;
         if (this.type == protectionenchantment.type) {
            return false;
         } else {
            return this.type == ProtectionEnchantment.Type.FALL || protectionenchantment.type == ProtectionEnchantment.Type.FALL;
         }
      } else {
         return super.checkCompatibility(p_77326_1_);
      }
   }

   public static int getFireAfterDampener(LivingEntity p_92093_0_, int p_92093_1_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, p_92093_0_);
      if (i > 0) {
         p_92093_1_ -= MathHelper.floor((float)p_92093_1_ * (float)i * 0.15F);
      }

      return p_92093_1_;
   }

   public static double getExplosionKnockbackAfterDampener(LivingEntity p_92092_0_, double p_92092_1_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, p_92092_0_);
      if (i > 0) {
         p_92092_1_ -= (double)MathHelper.floor(p_92092_1_ * (double)((float)i * 0.15F));
      }

      return p_92092_1_;
   }

   public static enum Type {
      ALL("all", 1, 11),
      FIRE("fire", 10, 8),
      FALL("fall", 5, 6),
      EXPLOSION("explosion", 5, 8),
      PROJECTILE("projectile", 3, 6);

      private final String name;
      private final int minCost;
      private final int levelCost;

      private Type(String p_i48839_3_, int p_i48839_4_, int p_i48839_5_) {
         this.name = p_i48839_3_;
         this.minCost = p_i48839_4_;
         this.levelCost = p_i48839_5_;
      }

      public int getMinCost() {
         return this.minCost;
      }

      public int getLevelCost() {
         return this.levelCost;
      }
   }
}
