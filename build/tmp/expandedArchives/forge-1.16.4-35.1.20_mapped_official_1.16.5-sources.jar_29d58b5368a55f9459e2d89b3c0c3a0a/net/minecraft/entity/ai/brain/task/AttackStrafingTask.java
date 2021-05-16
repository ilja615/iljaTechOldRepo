package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class AttackStrafingTask<E extends MobEntity> extends Task<E> {
   private final int tooCloseDistance;
   private final float strafeSpeed;

   public AttackStrafingTask(int p_i231509_1_, float p_i231509_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT));
      this.tooCloseDistance = p_i231509_1_;
      this.strafeSpeed = p_i231509_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      return this.isTargetVisible(p_212832_2_) && this.isTargetTooClose(p_212832_2_);
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      p_212831_2_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(this.getTarget(p_212831_2_), true));
      p_212831_2_.getMoveControl().strafe(-this.strafeSpeed, 0.0F);
      p_212831_2_.yRot = MathHelper.rotateIfNecessary(p_212831_2_.yRot, p_212831_2_.yHeadRot, 0.0F);
   }

   private boolean isTargetVisible(E p_233855_1_) {
      return p_233855_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().contains(this.getTarget(p_233855_1_));
   }

   private boolean isTargetTooClose(E p_233856_1_) {
      return this.getTarget(p_233856_1_).closerThan(p_233856_1_, (double)this.tooCloseDistance);
   }

   private LivingEntity getTarget(E p_233857_1_) {
      return p_233857_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}
