package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class GetAngryTask<E extends MobEntity> extends Task<E> {
   public GetAngryTask() {
      super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      BrainUtil.getLivingEntityFromUUIDMemory(p_212831_2_, MemoryModuleType.ANGRY_AT).ifPresent((p_233988_2_) -> {
         if (p_233988_2_.isDeadOrDying() && (p_233988_2_.getType() != EntityType.PLAYER || p_212831_1_.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))) {
            p_212831_2_.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
         }

      });
   }
}
