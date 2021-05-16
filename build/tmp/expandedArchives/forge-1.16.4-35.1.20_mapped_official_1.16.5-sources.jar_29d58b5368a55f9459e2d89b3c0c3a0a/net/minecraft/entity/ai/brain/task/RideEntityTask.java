package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class RideEntityTask<E extends LivingEntity> extends Task<E> {
   private final float speedModifier;

   public RideEntityTask(float p_i231524_1_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.RIDE_TARGET, MemoryModuleStatus.VALUE_PRESENT));
      this.speedModifier = p_i231524_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      return !p_212832_2_.isPassenger();
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      if (this.isCloseEnoughToStartRiding(p_212831_2_)) {
         p_212831_2_.startRiding(this.getRidableEntity(p_212831_2_));
      } else {
         BrainUtil.setWalkAndLookTargetMemories(p_212831_2_, this.getRidableEntity(p_212831_2_), this.speedModifier, 1);
      }

   }

   private boolean isCloseEnoughToStartRiding(E p_233925_1_) {
      return this.getRidableEntity(p_233925_1_).closerThan(p_233925_1_, 1.0D);
   }

   private Entity getRidableEntity(E p_233926_1_) {
      return p_233926_1_.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).get();
   }
}
