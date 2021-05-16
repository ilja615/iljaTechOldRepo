package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class CongregateTask extends Task<LivingEntity> {
   public CongregateTask() {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Brain<?> brain = p_212832_2_.getBrain();
      Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.MEETING_POINT);
      return p_212832_1_.getRandom().nextInt(100) == 0 && optional.isPresent() && p_212832_1_.dimension() == optional.get().dimension() && optional.get().pos().closerThan(p_212832_2_.position(), 4.0D) && brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().stream().anyMatch((p_220570_0_) -> {
         return EntityType.VILLAGER.equals(p_220570_0_.getType());
      });
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((p_220568_2_) -> {
         p_220568_2_.stream().filter((p_220572_0_) -> {
            return EntityType.VILLAGER.equals(p_220572_0_.getType());
         }).filter((p_220571_1_) -> {
            return p_220571_1_.distanceToSqr(p_212831_2_) <= 32.0D;
         }).findFirst().ifPresent((p_220569_1_) -> {
            brain.setMemory(MemoryModuleType.INTERACTION_TARGET, p_220569_1_);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220569_1_, true));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(p_220569_1_, false), 0.3F, 1));
         });
      });
   }
}
