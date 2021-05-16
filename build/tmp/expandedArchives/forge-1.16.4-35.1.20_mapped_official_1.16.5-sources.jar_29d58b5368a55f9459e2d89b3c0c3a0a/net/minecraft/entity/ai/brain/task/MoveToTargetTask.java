package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class MoveToTargetTask extends Task<MobEntity> {
   private final float speedModifier;

   public MoveToTargetTask(float p_i231534_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.REGISTERED));
      this.speedModifier = p_i231534_1_;
   }

   protected void start(ServerWorld p_212831_1_, MobEntity p_212831_2_, long p_212831_3_) {
      LivingEntity livingentity = p_212831_2_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      if (BrainUtil.canSee(p_212831_2_, livingentity) && BrainUtil.isWithinAttackRange(p_212831_2_, livingentity, 1)) {
         this.clearWalkTarget(p_212831_2_);
      } else {
         this.setWalkAndLookTarget(p_212831_2_, livingentity);
      }

   }

   private void setWalkAndLookTarget(LivingEntity p_233968_1_, LivingEntity p_233968_2_) {
      Brain brain = p_233968_1_.getBrain();
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_233968_2_, true));
      WalkTarget walktarget = new WalkTarget(new EntityPosWrapper(p_233968_2_, false), this.speedModifier, 0);
      brain.setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   private void clearWalkTarget(LivingEntity p_233967_1_) {
      p_233967_1_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }
}
