package net.minecraft.entity.monster;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IFlinging {
   @OnlyIn(Dist.CLIENT)
   int getAttackAnimationRemainingTicks();

   static boolean hurtAndThrowTarget(LivingEntity p_234403_0_, LivingEntity p_234403_1_) {
      float f1 = (float)p_234403_0_.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float f;
      if (!p_234403_0_.isBaby() && (int)f1 > 0) {
         f = f1 / 2.0F + (float)p_234403_0_.level.random.nextInt((int)f1);
      } else {
         f = f1;
      }

      boolean flag = p_234403_1_.hurt(DamageSource.mobAttack(p_234403_0_), f);
      if (flag) {
         p_234403_0_.doEnchantDamageEffects(p_234403_0_, p_234403_1_);
         if (!p_234403_0_.isBaby()) {
            throwTarget(p_234403_0_, p_234403_1_);
         }
      }

      return flag;
   }

   static void throwTarget(LivingEntity p_234404_0_, LivingEntity p_234404_1_) {
      double d0 = p_234404_0_.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      double d1 = p_234404_1_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
      double d2 = d0 - d1;
      if (!(d2 <= 0.0D)) {
         double d3 = p_234404_1_.getX() - p_234404_0_.getX();
         double d4 = p_234404_1_.getZ() - p_234404_0_.getZ();
         float f = (float)(p_234404_0_.level.random.nextInt(21) - 10);
         double d5 = d2 * (double)(p_234404_0_.level.random.nextFloat() * 0.5F + 0.2F);
         Vector3d vector3d = (new Vector3d(d3, 0.0D, d4)).normalize().scale(d5).yRot(f);
         double d6 = d2 * (double)p_234404_0_.level.random.nextFloat() * 0.5D;
         p_234404_1_.push(vector3d.x, d6, vector3d.z);
         p_234404_1_.hurtMarked = true;
      }
   }
}
