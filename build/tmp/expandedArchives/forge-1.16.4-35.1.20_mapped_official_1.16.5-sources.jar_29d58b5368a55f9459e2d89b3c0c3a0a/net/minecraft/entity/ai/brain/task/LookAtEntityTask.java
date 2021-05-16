package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class LookAtEntityTask extends Task<LivingEntity> {
   private final Predicate<LivingEntity> predicate;
   private final float maxDistSqr;

   public LookAtEntityTask(EntityClassification p_i50350_1_, float p_i50350_2_) {
      this((p_220514_1_) -> {
         return p_i50350_1_.equals(p_220514_1_.getType().getCategory());
      }, p_i50350_2_);
   }

   public LookAtEntityTask(EntityType<?> p_i50351_1_, float p_i50351_2_) {
      this((p_220518_1_) -> {
         return p_i50351_1_.equals(p_220518_1_.getType());
      }, p_i50351_2_);
   }

   public LookAtEntityTask(float p_i231532_1_) {
      this((p_233953_0_) -> {
         return true;
      }, p_i231532_1_);
   }

   public LookAtEntityTask(Predicate<LivingEntity> p_i50352_1_, float p_i50352_2_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT));
      this.predicate = p_i50352_1_;
      this.maxDistSqr = p_i50352_2_ * p_i50352_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return p_212832_2_.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().stream().anyMatch(this.predicate);
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((p_220515_3_) -> {
         p_220515_3_.stream().filter(this.predicate).filter((p_220517_2_) -> {
            return p_220517_2_.distanceToSqr(p_212831_2_) <= (double)this.maxDistSqr;
         }).findFirst().ifPresent((p_220516_1_) -> {
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220516_1_, true));
         });
      });
   }
}
