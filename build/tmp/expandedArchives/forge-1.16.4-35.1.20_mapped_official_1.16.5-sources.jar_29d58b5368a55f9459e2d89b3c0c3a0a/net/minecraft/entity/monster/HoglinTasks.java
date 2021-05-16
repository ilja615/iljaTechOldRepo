package net.minecraft.entity.monster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AnimalBreedTask;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.ChildFollowNearestAdultTask;
import net.minecraft.entity.ai.brain.task.DummyTask;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.LookAtEntityTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.PredicateTask;
import net.minecraft.entity.ai.brain.task.RandomlyStopAttackingTask;
import net.minecraft.entity.ai.brain.task.RunAwayTask;
import net.minecraft.entity.ai.brain.task.RunSometimesTask;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.ai.brain.task.WalkRandomlyTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;

public class HoglinTasks {
   private static final RangedInteger RETREAT_DURATION = TickRangeConverter.rangeOfSeconds(5, 20);
   private static final RangedInteger ADULT_FOLLOW_RANGE = RangedInteger.of(5, 16);

   protected static Brain<?> makeBrain(Brain<HoglinEntity> p_234376_0_) {
      initCoreActivity(p_234376_0_);
      initIdleActivity(p_234376_0_);
      initFightActivity(p_234376_0_);
      initRetreatActivity(p_234376_0_);
      p_234376_0_.setCoreActivities(ImmutableSet.of(Activity.CORE));
      p_234376_0_.setDefaultActivity(Activity.IDLE);
      p_234376_0_.useDefaultActivity();
      return p_234376_0_;
   }

   private static void initCoreActivity(Brain<HoglinEntity> p_234382_0_) {
      p_234382_0_.addActivity(Activity.CORE, 0, ImmutableList.of(new LookTask(45, 90), new WalkToTargetTask()));
   }

   private static void initIdleActivity(Brain<HoglinEntity> p_234385_0_) {
      p_234385_0_.addActivity(Activity.IDLE, 10, ImmutableList.<net.minecraft.entity.ai.brain.task.Task<? super HoglinEntity>>of(new RandomlyStopAttackingTask(MemoryModuleType.NEAREST_REPELLENT, 200), new AnimalBreedTask(EntityType.HOGLIN, 0.6F), RunAwayTask.pos(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true), new ForgetAttackTargetTask<HoglinEntity>(HoglinTasks::findNearestValidAttackTarget), new SupplementedTask<HoglinEntity>(HoglinEntity::isAdult, RunAwayTask.entity(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4F, 8, false)), new RunSometimesTask<LivingEntity>(new LookAtEntityTask(8.0F), RangedInteger.of(30, 60)), new ChildFollowNearestAdultTask(ADULT_FOLLOW_RANGE, 0.6F), createIdleMovementBehaviors()));
   }

   private static void initFightActivity(Brain<HoglinEntity> p_234388_0_) {
      p_234388_0_.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.<net.minecraft.entity.ai.brain.task.Task<? super HoglinEntity>>of(new RandomlyStopAttackingTask(MemoryModuleType.NEAREST_REPELLENT, 200), new AnimalBreedTask(EntityType.HOGLIN, 0.6F), new MoveToTargetTask(1.0F), new SupplementedTask<>(HoglinEntity::isAdult, new AttackTargetTask(40)), new SupplementedTask<>(AgeableEntity::isBaby, new AttackTargetTask(15)), new FindNewAttackTargetTask(), new PredicateTask<>(HoglinTasks::isBreeding, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
   }

   private static void initRetreatActivity(Brain<HoglinEntity> p_234391_0_) {
      p_234391_0_.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.<net.minecraft.entity.ai.brain.task.Task<? super HoglinEntity>>of(RunAwayTask.entity(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false), createIdleMovementBehaviors(), new RunSometimesTask<LivingEntity>(new LookAtEntityTask(8.0F), RangedInteger.of(30, 60)), new PredicateTask<HoglinEntity>(HoglinTasks::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
   }

   private static FirstShuffledTask<HoglinEntity> createIdleMovementBehaviors() {
      return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkRandomlyTask(0.4F), 2), Pair.of(new WalkTowardsLookTargetTask(0.4F, 3), 2), Pair.of(new DummyTask(30, 60), 1)));
   }

   protected static void updateActivity(HoglinEntity p_234377_0_) {
      Brain<HoglinEntity> brain = p_234377_0_.getBrain();
      Activity activity = brain.getActiveNonCoreActivity().orElse((Activity)null);
      brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.AVOID, Activity.IDLE));
      Activity activity1 = brain.getActiveNonCoreActivity().orElse((Activity)null);
      if (activity != activity1) {
         getSoundForCurrentActivity(p_234377_0_).ifPresent(p_234377_0_::playSound);
      }

      p_234377_0_.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
   }

   protected static void onHitTarget(HoglinEntity p_234378_0_, LivingEntity p_234378_1_) {
      if (!p_234378_0_.isBaby()) {
         if (p_234378_1_.getType() == EntityType.PIGLIN && piglinsOutnumberHoglins(p_234378_0_)) {
            setAvoidTarget(p_234378_0_, p_234378_1_);
            broadcastRetreat(p_234378_0_, p_234378_1_);
         } else {
            broadcastAttackTarget(p_234378_0_, p_234378_1_);
         }
      }
   }

   private static void broadcastRetreat(HoglinEntity p_234387_0_, LivingEntity p_234387_1_) {
      getVisibleAdultHoglins(p_234387_0_).forEach((p_234381_1_) -> {
         retreatFromNearestTarget(p_234381_1_, p_234387_1_);
      });
   }

   private static void retreatFromNearestTarget(HoglinEntity p_234390_0_, LivingEntity p_234390_1_) {
      Brain<HoglinEntity> brain = p_234390_0_.getBrain();
      LivingEntity lvt_2_1_ = BrainUtil.getNearestTarget(p_234390_0_, brain.getMemory(MemoryModuleType.AVOID_TARGET), p_234390_1_);
      lvt_2_1_ = BrainUtil.getNearestTarget(p_234390_0_, brain.getMemory(MemoryModuleType.ATTACK_TARGET), lvt_2_1_);
      setAvoidTarget(p_234390_0_, lvt_2_1_);
   }

   private static void setAvoidTarget(HoglinEntity p_234393_0_, LivingEntity p_234393_1_) {
      p_234393_0_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      p_234393_0_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_234393_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, p_234393_1_, (long)RETREAT_DURATION.randomValue(p_234393_0_.level.random));
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(HoglinEntity p_234392_0_) {
      return !isPacified(p_234392_0_) && !isBreeding(p_234392_0_) ? p_234392_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
   }

   static boolean isPosNearNearestRepellent(HoglinEntity p_234380_0_, BlockPos p_234380_1_) {
      Optional<BlockPos> optional = p_234380_0_.getBrain().getMemory(MemoryModuleType.NEAREST_REPELLENT);
      return optional.isPresent() && optional.get().closerThan(p_234380_1_, 8.0D);
   }

   private static boolean wantsToStopFleeing(HoglinEntity p_234394_0_) {
      return p_234394_0_.isAdult() && !piglinsOutnumberHoglins(p_234394_0_);
   }

   private static boolean piglinsOutnumberHoglins(HoglinEntity p_234396_0_) {
      if (p_234396_0_.isBaby()) {
         return false;
      } else {
         int i = p_234396_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
         int j = p_234396_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;
         return i > j;
      }
   }

   protected static void wasHurtBy(HoglinEntity p_234384_0_, LivingEntity p_234384_1_) {
      Brain<HoglinEntity> brain = p_234384_0_.getBrain();
      brain.eraseMemory(MemoryModuleType.PACIFIED);
      brain.eraseMemory(MemoryModuleType.BREED_TARGET);
      if (p_234384_0_.isBaby()) {
         retreatFromNearestTarget(p_234384_0_, p_234384_1_);
      } else {
         maybeRetaliate(p_234384_0_, p_234384_1_);
      }
   }

   private static void maybeRetaliate(HoglinEntity p_234395_0_, LivingEntity p_234395_1_) {
      if (!p_234395_0_.getBrain().isActive(Activity.AVOID) || p_234395_1_.getType() != EntityType.PIGLIN) {
         if (EntityPredicates.ATTACK_ALLOWED.test(p_234395_1_)) {
            if (p_234395_1_.getType() != EntityType.HOGLIN) {
               if (!BrainUtil.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(p_234395_0_, p_234395_1_, 4.0D)) {
                  setAttackTarget(p_234395_0_, p_234395_1_);
                  broadcastAttackTarget(p_234395_0_, p_234395_1_);
               }
            }
         }
      }
   }

   private static void setAttackTarget(HoglinEntity p_234397_0_, LivingEntity p_234397_1_) {
      Brain<HoglinEntity> brain = p_234397_0_.getBrain();
      brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      brain.eraseMemory(MemoryModuleType.BREED_TARGET);
      brain.setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, p_234397_1_, 200L);
   }

   private static void broadcastAttackTarget(HoglinEntity p_234399_0_, LivingEntity p_234399_1_) {
      getVisibleAdultHoglins(p_234399_0_).forEach((p_234375_1_) -> {
         setAttackTargetIfCloserThanCurrent(p_234375_1_, p_234399_1_);
      });
   }

   private static void setAttackTargetIfCloserThanCurrent(HoglinEntity p_234401_0_, LivingEntity p_234401_1_) {
      if (!isPacified(p_234401_0_)) {
         Optional<LivingEntity> optional = p_234401_0_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
         LivingEntity livingentity = BrainUtil.getNearestTarget(p_234401_0_, optional, p_234401_1_);
         setAttackTarget(p_234401_0_, livingentity);
      }
   }

   public static Optional<SoundEvent> getSoundForCurrentActivity(HoglinEntity p_234398_0_) {
      return p_234398_0_.getBrain().getActiveNonCoreActivity().map((p_234379_1_) -> {
         return getSoundForActivity(p_234398_0_, p_234379_1_);
      });
   }

   private static SoundEvent getSoundForActivity(HoglinEntity p_241413_0_, Activity p_241413_1_) {
      if (p_241413_1_ != Activity.AVOID && !p_241413_0_.isConverting()) {
         if (p_241413_1_ == Activity.FIGHT) {
            return SoundEvents.HOGLIN_ANGRY;
         } else {
            return isNearRepellent(p_241413_0_) ? SoundEvents.HOGLIN_RETREAT : SoundEvents.HOGLIN_AMBIENT;
         }
      } else {
         return SoundEvents.HOGLIN_RETREAT;
      }
   }

   private static List<HoglinEntity> getVisibleAdultHoglins(HoglinEntity p_234400_0_) {
      return p_234400_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse(ImmutableList.of());
   }

   private static boolean isNearRepellent(HoglinEntity p_241416_0_) {
      return p_241416_0_.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
   }

   private static boolean isBreeding(HoglinEntity p_234402_0_) {
      return p_234402_0_.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
   }

   protected static boolean isPacified(HoglinEntity p_234386_0_) {
      return p_234386_0_.getBrain().hasMemoryValue(MemoryModuleType.PACIFIED);
   }
}
