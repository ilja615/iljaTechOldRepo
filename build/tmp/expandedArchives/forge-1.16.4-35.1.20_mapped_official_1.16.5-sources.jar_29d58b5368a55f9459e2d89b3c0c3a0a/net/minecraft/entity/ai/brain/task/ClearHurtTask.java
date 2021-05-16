package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.server.ServerWorld;

public class ClearHurtTask extends Task<VillagerEntity> {
   public ClearHurtTask() {
      super(ImmutableMap.of());
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      boolean flag = PanicTask.isHurt(p_212831_2_) || PanicTask.hasHostile(p_212831_2_) || isCloseToEntityThatHurtMe(p_212831_2_);
      if (!flag) {
         p_212831_2_.getBrain().eraseMemory(MemoryModuleType.HURT_BY);
         p_212831_2_.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         p_212831_2_.getBrain().updateActivityFromSchedule(p_212831_1_.getDayTime(), p_212831_1_.getGameTime());
      }

   }

   private static boolean isCloseToEntityThatHurtMe(VillagerEntity p_220394_0_) {
      return p_220394_0_.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((p_223523_1_) -> {
         return p_223523_1_.distanceToSqr(p_220394_0_) <= 36.0D;
      }).isPresent();
   }
}
