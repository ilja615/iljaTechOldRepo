package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class NonTamedTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private final TameableEntity tamableMob;

   public NonTamedTargetGoal(TameableEntity p_i48571_1_, Class<T> p_i48571_2_, boolean p_i48571_3_, @Nullable Predicate<LivingEntity> p_i48571_4_) {
      super(p_i48571_1_, p_i48571_2_, 10, p_i48571_3_, false, p_i48571_4_);
      this.tamableMob = p_i48571_1_;
   }

   public boolean canUse() {
      return !this.tamableMob.isTame() && super.canUse();
   }

   public boolean canContinueToUse() {
      return this.targetConditions != null ? this.targetConditions.test(this.mob, this.target) : super.canContinueToUse();
   }
}
