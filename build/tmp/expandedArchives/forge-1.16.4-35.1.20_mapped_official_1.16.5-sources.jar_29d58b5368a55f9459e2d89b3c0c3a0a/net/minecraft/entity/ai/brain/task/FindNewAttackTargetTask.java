package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;

public class FindNewAttackTargetTask<E extends MobEntity> extends Task<E> {
   private final Predicate<LivingEntity> stopAttackingWhen;

   public FindNewAttackTargetTask(Predicate<LivingEntity> p_i231539_1_) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED));
      this.stopAttackingWhen = p_i231539_1_;
   }

   public FindNewAttackTargetTask() {
      this((p_233984_0_) -> {
         return false;
      });
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      if (isTiredOfTryingToReachTarget(p_212831_2_)) {
         this.clearAttackTarget(p_212831_2_);
      } else if (this.isCurrentTargetDeadOrRemoved(p_212831_2_)) {
         this.clearAttackTarget(p_212831_2_);
      } else if (this.isCurrentTargetInDifferentLevel(p_212831_2_)) {
         this.clearAttackTarget(p_212831_2_);
      } else if (!EntityPredicates.ATTACK_ALLOWED.test(this.getAttackTarget(p_212831_2_))) {
         this.clearAttackTarget(p_212831_2_);
      } else if (this.stopAttackingWhen.test(this.getAttackTarget(p_212831_2_))) {
         this.clearAttackTarget(p_212831_2_);
      }
   }

   private boolean isCurrentTargetInDifferentLevel(E p_233983_1_) {
      return this.getAttackTarget(p_233983_1_).level != p_233983_1_.level;
   }

   private LivingEntity getAttackTarget(E p_233985_1_) {
      return p_233985_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   private static <E extends LivingEntity> boolean isTiredOfTryingToReachTarget(E p_233982_0_) {
      Optional<Long> optional = p_233982_0_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      return optional.isPresent() && p_233982_0_.level.getGameTime() - optional.get() > 200L;
   }

   private boolean isCurrentTargetDeadOrRemoved(E p_233986_1_) {
      Optional<LivingEntity> optional = p_233986_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
      return optional.isPresent() && !optional.get().isAlive();
   }

   private void clearAttackTarget(E p_233987_1_) {
      p_233987_1_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
   }
}
