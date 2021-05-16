package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class InteractWithEntityTask<E extends LivingEntity, T extends LivingEntity> extends Task<E> {
   private final int maxDist;
   private final float speedModifier;
   private final EntityType<? extends T> type;
   private final int interactionRangeSqr;
   private final Predicate<T> targetFilter;
   private final Predicate<E> selfFilter;
   private final MemoryModuleType<T> memory;

   public InteractWithEntityTask(EntityType<? extends T> p_i50363_1_, int p_i50363_2_, Predicate<E> p_i50363_3_, Predicate<T> p_i50363_4_, MemoryModuleType<T> p_i50363_5_, float p_i50363_6_, int p_i50363_7_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT));
      this.type = p_i50363_1_;
      this.speedModifier = p_i50363_6_;
      this.interactionRangeSqr = p_i50363_2_ * p_i50363_2_;
      this.maxDist = p_i50363_7_;
      this.targetFilter = p_i50363_4_;
      this.selfFilter = p_i50363_3_;
      this.memory = p_i50363_5_;
   }

   public static <T extends LivingEntity> InteractWithEntityTask<LivingEntity, T> of(EntityType<? extends T> p_220445_0_, int p_220445_1_, MemoryModuleType<T> p_220445_2_, float p_220445_3_, int p_220445_4_) {
      return new InteractWithEntityTask<>(p_220445_0_, p_220445_1_, (p_220441_0_) -> {
         return true;
      }, (p_220442_0_) -> {
         return true;
      }, p_220445_2_, p_220445_3_, p_220445_4_);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      return this.selfFilter.test(p_212832_2_) && this.seesAtLeastOneValidTarget(p_212832_2_);
   }

   private boolean seesAtLeastOneValidTarget(E p_233913_1_) {
      List<LivingEntity> list = p_233913_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get();
      return list.stream().anyMatch(this::isTargetValid);
   }

   private boolean isTargetValid(LivingEntity p_233914_1_) {
      return this.type.equals(p_233914_1_.getType()) && this.targetFilter.test((T)p_233914_1_);
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((p_220437_3_) -> {
         p_220437_3_.stream().filter((p_220440_1_) -> {
            return this.type.equals(p_220440_1_.getType());
         }).map((p_220439_0_) -> {
            return (T)p_220439_0_;
         }).filter((p_220443_2_) -> {
            return p_220443_2_.distanceToSqr(p_212831_2_) <= (double)this.interactionRangeSqr;
         }).filter(this.targetFilter).findFirst().ifPresent((p_220438_2_) -> {
            brain.setMemory(this.memory, (T)p_220438_2_);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220438_2_, true));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(p_220438_2_, false), this.speedModifier, this.maxDist));
         });
      });
   }
}
