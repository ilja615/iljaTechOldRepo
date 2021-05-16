package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;

public class LookAtGoal extends Goal {
   protected final MobEntity mob;
   protected Entity lookAt;
   protected final float lookDistance;
   private int lookTime;
   protected final float probability;
   protected final Class<? extends LivingEntity> lookAtType;
   protected final EntityPredicate lookAtContext;

   public LookAtGoal(MobEntity p_i1631_1_, Class<? extends LivingEntity> p_i1631_2_, float p_i1631_3_) {
      this(p_i1631_1_, p_i1631_2_, p_i1631_3_, 0.02F);
   }

   public LookAtGoal(MobEntity p_i1632_1_, Class<? extends LivingEntity> p_i1632_2_, float p_i1632_3_, float p_i1632_4_) {
      this.mob = p_i1632_1_;
      this.lookAtType = p_i1632_2_;
      this.lookDistance = p_i1632_3_;
      this.probability = p_i1632_4_;
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      if (p_i1632_2_ == PlayerEntity.class) {
         this.lookAtContext = (new EntityPredicate()).range((double)p_i1632_3_).allowSameTeam().allowInvulnerable().allowNonAttackable().selector((p_220715_1_) -> {
            return EntityPredicates.notRiding(p_i1632_1_).test(p_220715_1_);
         });
      } else {
         this.lookAtContext = (new EntityPredicate()).range((double)p_i1632_3_).allowSameTeam().allowInvulnerable().allowNonAttackable();
      }

   }

   public boolean canUse() {
      if (this.mob.getRandom().nextFloat() >= this.probability) {
         return false;
      } else {
         if (this.mob.getTarget() != null) {
            this.lookAt = this.mob.getTarget();
         }

         if (this.lookAtType == PlayerEntity.class) {
            this.lookAt = this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
         } else {
            this.lookAt = this.mob.level.getNearestLoadedEntity(this.lookAtType, this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0D, (double)this.lookDistance));
         }

         return this.lookAt != null;
      }
   }

   public boolean canContinueToUse() {
      if (!this.lookAt.isAlive()) {
         return false;
      } else if (this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)) {
         return false;
      } else {
         return this.lookTime > 0;
      }
   }

   public void start() {
      this.lookTime = 40 + this.mob.getRandom().nextInt(40);
   }

   public void stop() {
      this.lookAt = null;
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
      --this.lookTime;
   }
}
