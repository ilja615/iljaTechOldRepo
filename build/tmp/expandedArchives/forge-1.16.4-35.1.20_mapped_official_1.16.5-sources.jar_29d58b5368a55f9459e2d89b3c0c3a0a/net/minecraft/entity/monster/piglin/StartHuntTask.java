package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.world.server.ServerWorld;

public class StartHuntTask<E extends PiglinEntity> extends Task<E> {
   public StartHuntTask() {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, PiglinEntity p_212832_2_) {
      return !p_212832_2_.isBaby() && !PiglinTasks.hasAnyoneNearbyHuntedRecently(p_212832_2_);
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      HoglinEntity hoglinentity = p_212831_2_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
      PiglinTasks.setAngerTarget(p_212831_2_, hoglinentity);
      PiglinTasks.dontKillAnyMoreHoglinsForAWhile(p_212831_2_);
      PiglinTasks.broadcastAngerTarget(p_212831_2_, hoglinentity);
      PiglinTasks.broadcastDontKillAnyMoreHoglinsForAWhile(p_212831_2_);
   }
}
