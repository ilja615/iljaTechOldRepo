package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public interface ICrossbowUser extends IRangedAttackMob {
   void setChargingCrossbow(boolean p_213671_1_);

   void shootCrossbowProjectile(LivingEntity p_230284_1_, ItemStack p_230284_2_, ProjectileEntity p_230284_3_, float p_230284_4_);

   @Nullable
   LivingEntity getTarget();

   void onCrossbowAttackPerformed();

   default void performCrossbowAttack(LivingEntity p_234281_1_, float p_234281_2_) {
      Hand hand = ProjectileHelper.getWeaponHoldingHand(p_234281_1_, Items.CROSSBOW);
      ItemStack itemstack = p_234281_1_.getItemInHand(hand);
      if (p_234281_1_.isHolding(Items.CROSSBOW)) {
         CrossbowItem.performShooting(p_234281_1_.level, p_234281_1_, hand, itemstack, p_234281_2_, (float)(14 - p_234281_1_.level.getDifficulty().getId() * 4));
      }

      this.onCrossbowAttackPerformed();
   }

   default void shootCrossbowProjectile(LivingEntity p_234279_1_, LivingEntity p_234279_2_, ProjectileEntity p_234279_3_, float p_234279_4_, float p_234279_5_) {
      double d0 = p_234279_2_.getX() - p_234279_1_.getX();
      double d1 = p_234279_2_.getZ() - p_234279_1_.getZ();
      double d2 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
      double d3 = p_234279_2_.getY(0.3333333333333333D) - p_234279_3_.getY() + d2 * (double)0.2F;
      Vector3f vector3f = this.getProjectileShotVector(p_234279_1_, new Vector3d(d0, d3, d1), p_234279_4_);
      p_234279_3_.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), p_234279_5_, (float)(14 - p_234279_1_.level.getDifficulty().getId() * 4));
      p_234279_1_.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (p_234279_1_.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   default Vector3f getProjectileShotVector(LivingEntity p_234280_1_, Vector3d p_234280_2_, float p_234280_3_) {
      Vector3d vector3d = p_234280_2_.normalize();
      Vector3d vector3d1 = vector3d.cross(new Vector3d(0.0D, 1.0D, 0.0D));
      if (vector3d1.lengthSqr() <= 1.0E-7D) {
         vector3d1 = vector3d.cross(p_234280_1_.getUpVector(1.0F));
      }

      Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), 90.0F, true);
      Vector3f vector3f = new Vector3f(vector3d);
      vector3f.transform(quaternion);
      Quaternion quaternion1 = new Quaternion(vector3f, p_234280_3_, true);
      Vector3f vector3f1 = new Vector3f(vector3d);
      vector3f1.transform(quaternion1);
      return vector3f1;
   }
}
