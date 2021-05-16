package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;

public class AttackTargetTask extends Task<MobEntity> {
   private final int cooldownBetweenAttacks;

   public AttackTargetTask(int p_i231523_1_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleStatus.VALUE_ABSENT));
      this.cooldownBetweenAttacks = p_i231523_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, MobEntity p_212832_2_) {
      LivingEntity livingentity = this.getAttackTarget(p_212832_2_);
      return !this.isHoldingUsableProjectileWeapon(p_212832_2_) && BrainUtil.canSee(p_212832_2_, livingentity) && BrainUtil.isWithinMeleeAttackRange(p_212832_2_, livingentity);
   }

   private boolean isHoldingUsableProjectileWeapon(MobEntity p_233921_1_) {
      return p_233921_1_.isHolding((p_233922_1_) -> {
         return p_233922_1_ instanceof ShootableItem && p_233921_1_.canFireProjectileWeapon((ShootableItem)p_233922_1_);
      });
   }

   protected void start(ServerWorld p_212831_1_, MobEntity p_212831_2_, long p_212831_3_) {
      LivingEntity livingentity = this.getAttackTarget(p_212831_2_);
      BrainUtil.lookAtEntity(p_212831_2_, livingentity);
      p_212831_2_.swing(Hand.MAIN_HAND);
      p_212831_2_.doHurtTarget(livingentity);
      p_212831_2_.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.cooldownBetweenAttacks);
   }

   private LivingEntity getAttackTarget(MobEntity p_233923_1_) {
      return p_233923_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}
