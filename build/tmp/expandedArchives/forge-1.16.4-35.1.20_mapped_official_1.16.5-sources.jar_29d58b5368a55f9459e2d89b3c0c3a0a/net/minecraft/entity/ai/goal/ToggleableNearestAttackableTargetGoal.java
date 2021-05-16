package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;

public class ToggleableNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private boolean canAttack = true;

   public ToggleableNearestAttackableTargetGoal(AbstractRaiderEntity p_i50312_1_, Class<T> p_i50312_2_, int p_i50312_3_, boolean p_i50312_4_, boolean p_i50312_5_, @Nullable Predicate<LivingEntity> p_i50312_6_) {
      super(p_i50312_1_, p_i50312_2_, p_i50312_3_, p_i50312_4_, p_i50312_5_, p_i50312_6_);
   }

   public void setCanAttack(boolean p_220783_1_) {
      this.canAttack = p_220783_1_;
   }

   public boolean canUse() {
      return this.canAttack && super.canUse();
   }
}
