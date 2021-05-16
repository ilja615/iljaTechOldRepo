package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPredicate {
   public static final EntityPredicate DEFAULT = new EntityPredicate();
   private double range = -1.0D;
   private boolean allowInvulnerable;
   private boolean allowSameTeam;
   private boolean allowUnseeable;
   private boolean allowNonAttackable;
   private boolean testInvisible = true;
   private Predicate<LivingEntity> selector;

   public EntityPredicate range(double p_221013_1_) {
      this.range = p_221013_1_;
      return this;
   }

   public EntityPredicate allowInvulnerable() {
      this.allowInvulnerable = true;
      return this;
   }

   public EntityPredicate allowSameTeam() {
      this.allowSameTeam = true;
      return this;
   }

   public EntityPredicate allowUnseeable() {
      this.allowUnseeable = true;
      return this;
   }

   public EntityPredicate allowNonAttackable() {
      this.allowNonAttackable = true;
      return this;
   }

   public EntityPredicate ignoreInvisibilityTesting() {
      this.testInvisible = false;
      return this;
   }

   public EntityPredicate selector(@Nullable Predicate<LivingEntity> p_221012_1_) {
      this.selector = p_221012_1_;
      return this;
   }

   public boolean test(@Nullable LivingEntity p_221015_1_, LivingEntity p_221015_2_) {
      if (p_221015_1_ == p_221015_2_) {
         return false;
      } else if (p_221015_2_.isSpectator()) {
         return false;
      } else if (!p_221015_2_.isAlive()) {
         return false;
      } else if (!this.allowInvulnerable && p_221015_2_.isInvulnerable()) {
         return false;
      } else if (this.selector != null && !this.selector.test(p_221015_2_)) {
         return false;
      } else {
         if (p_221015_1_ != null) {
            if (!this.allowNonAttackable) {
               if (!p_221015_1_.canAttack(p_221015_2_)) {
                  return false;
               }

               if (!p_221015_1_.canAttackType(p_221015_2_.getType())) {
                  return false;
               }
            }

            if (!this.allowSameTeam && p_221015_1_.isAlliedTo(p_221015_2_)) {
               return false;
            }

            if (this.range > 0.0D) {
               double d0 = this.testInvisible ? p_221015_2_.getVisibilityPercent(p_221015_1_) : 1.0D;
               double d1 = Math.max(this.range * d0, 2.0D);
               double d2 = p_221015_1_.distanceToSqr(p_221015_2_.getX(), p_221015_2_.getY(), p_221015_2_.getZ());
               if (d2 > d1 * d1) {
                  return false;
               }
            }

            if (!this.allowUnseeable && p_221015_1_ instanceof MobEntity && !((MobEntity)p_221015_1_).getSensing().canSee(p_221015_2_)) {
               return false;
            }
         }

         return true;
      }
   }
}
