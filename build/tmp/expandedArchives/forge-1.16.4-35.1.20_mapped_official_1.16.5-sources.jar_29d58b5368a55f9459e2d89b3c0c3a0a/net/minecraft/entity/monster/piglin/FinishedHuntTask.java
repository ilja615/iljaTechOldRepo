package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class FinishedHuntTask<E extends PiglinEntity> extends Task<E> {
   public FinishedHuntTask() {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleStatus.REGISTERED));
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      if (this.isAttackTargetDeadHoglin(p_212831_2_)) {
         PiglinTasks.dontKillAnyMoreHoglinsForAWhile(p_212831_2_);
      }

   }

   private boolean isAttackTargetDeadHoglin(E p_234539_1_) {
      LivingEntity livingentity = p_234539_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      return livingentity.getType() == EntityType.HOGLIN && livingentity.isDeadOrDying();
   }
}
