package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class WalkTowardsLookTargetTask extends Task<LivingEntity> {
   private final float speedModifier;
   private final int closeEnoughDistance;

   public WalkTowardsLookTargetTask(float p_i50344_1_, int p_i50344_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT));
      this.speedModifier = p_i50344_1_;
      this.closeEnoughDistance = p_i50344_2_;
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      IPosWrapper iposwrapper = brain.getMemory(MemoryModuleType.LOOK_TARGET).get();
      brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(iposwrapper, this.speedModifier, this.closeEnoughDistance));
   }
}
