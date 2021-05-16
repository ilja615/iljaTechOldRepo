package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class EndAttackTask extends Task<LivingEntity> {
   private final int celebrateDuration;
   private final BiPredicate<LivingEntity, LivingEntity> dancePredicate;

   public EndAttackTask(int p_i231538_1_, BiPredicate<LivingEntity, LivingEntity> p_i231538_2_) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryModuleStatus.REGISTERED, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.DANCING, MemoryModuleStatus.REGISTERED));
      this.celebrateDuration = p_i231538_1_;
      this.dancePredicate = p_i231538_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return this.getAttackTarget(p_212832_2_).isDeadOrDying();
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      LivingEntity livingentity = this.getAttackTarget(p_212831_2_);
      if (this.dancePredicate.test(p_212831_2_, livingentity)) {
         p_212831_2_.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, (long)this.celebrateDuration);
      }

      p_212831_2_.getBrain().setMemoryWithExpiry(MemoryModuleType.CELEBRATE_LOCATION, livingentity.blockPosition(), (long)this.celebrateDuration);
      if (livingentity.getType() != EntityType.PLAYER || p_212831_1_.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         p_212831_2_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         p_212831_2_.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
      }

   }

   private LivingEntity getAttackTarget(LivingEntity p_233980_1_) {
      return p_233980_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}
