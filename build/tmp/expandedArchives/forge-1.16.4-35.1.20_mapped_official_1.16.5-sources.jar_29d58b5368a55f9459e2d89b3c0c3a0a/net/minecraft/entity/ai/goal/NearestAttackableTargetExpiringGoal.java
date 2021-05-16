package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;

public class NearestAttackableTargetExpiringGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private int cooldown = 0;

   public NearestAttackableTargetExpiringGoal(AbstractRaiderEntity p_i50311_1_, Class<T> p_i50311_2_, boolean p_i50311_3_, @Nullable Predicate<LivingEntity> p_i50311_4_) {
      super(p_i50311_1_, p_i50311_2_, 500, p_i50311_3_, false, p_i50311_4_);
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void decrementCooldown() {
      --this.cooldown;
   }

   public boolean canUse() {
      if (this.cooldown <= 0 && this.mob.getRandom().nextBoolean()) {
         if (!((AbstractRaiderEntity)this.mob).hasActiveRaid()) {
            return false;
         } else {
            this.findTarget();
            return this.target != null;
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.cooldown = 200;
      super.start();
   }
}
