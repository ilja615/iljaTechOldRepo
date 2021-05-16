package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackStrafingTask;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.DummyTask;
import net.minecraft.entity.ai.brain.task.EndAttackTask;
import net.minecraft.entity.ai.brain.task.FindInteractionAndLookTargetTask;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GetAngryTask;
import net.minecraft.entity.ai.brain.task.HuntCelebrationTask;
import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.InteractWithEntityTask;
import net.minecraft.entity.ai.brain.task.LookAtEntityTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.PickupWantedItemTask;
import net.minecraft.entity.ai.brain.task.PiglinIdleActivityTask;
import net.minecraft.entity.ai.brain.task.PredicateTask;
import net.minecraft.entity.ai.brain.task.RideEntityTask;
import net.minecraft.entity.ai.brain.task.RunAwayTask;
import net.minecraft.entity.ai.brain.task.RunSometimesTask;
import net.minecraft.entity.ai.brain.task.ShootTargetTask;
import net.minecraft.entity.ai.brain.task.StopRidingEntityTask;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.ai.brain.task.WalkRandomlyTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class PiglinTasks {
   public static final Item BARTERING_ITEM = Items.GOLD_INGOT;
   private static final RangedInteger TIME_BETWEEN_HUNTS = TickRangeConverter.rangeOfSeconds(30, 120);
   private static final RangedInteger RIDE_START_INTERVAL = TickRangeConverter.rangeOfSeconds(10, 40);
   private static final RangedInteger RIDE_DURATION = TickRangeConverter.rangeOfSeconds(10, 30);
   private static final RangedInteger RETREAT_DURATION = TickRangeConverter.rangeOfSeconds(5, 20);
   private static final RangedInteger AVOID_ZOMBIFIED_DURATION = TickRangeConverter.rangeOfSeconds(5, 7);
   private static final RangedInteger BABY_AVOID_NEMESIS_DURATION = TickRangeConverter.rangeOfSeconds(5, 7);
   private static final Set<Item> FOOD_ITEMS = ImmutableSet.of(Items.PORKCHOP, Items.COOKED_PORKCHOP);

   protected static Brain<?> makeBrain(PiglinEntity p_234469_0_, Brain<PiglinEntity> p_234469_1_) {
      initCoreActivity(p_234469_1_);
      initIdleActivity(p_234469_1_);
      initAdmireItemActivity(p_234469_1_);
      initFightActivity(p_234469_0_, p_234469_1_);
      initCelebrateActivity(p_234469_1_);
      initRetreatActivity(p_234469_1_);
      initRideHoglinActivity(p_234469_1_);
      p_234469_1_.setCoreActivities(ImmutableSet.of(Activity.CORE));
      p_234469_1_.setDefaultActivity(Activity.IDLE);
      p_234469_1_.useDefaultActivity();
      return p_234469_1_;
   }

   protected static void initMemories(PiglinEntity p_234466_0_) {
      int i = TIME_BETWEEN_HUNTS.randomValue(p_234466_0_.level.random);
      p_234466_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, (long)i);
   }

   private static void initCoreActivity(Brain<PiglinEntity> p_234464_0_) {
      p_234464_0_.addActivity(Activity.CORE, 0, ImmutableList.<net.minecraft.entity.ai.brain.task.Task<? super PiglinEntity>>of(new LookTask(45, 90), new WalkToTargetTask(), new InteractWithDoorTask(), babyAvoidNemesis(), avoidZombified(), new StartAdmiringItemTask(), new AdmireItemTask(120), new EndAttackTask(300, PiglinTasks::wantsToDance), new GetAngryTask()));
   }

   private static void initIdleActivity(Brain<PiglinEntity> p_234485_0_) {
      p_234485_0_.addActivity(Activity.IDLE, 10, ImmutableList.of(new LookAtEntityTask(PiglinTasks::isPlayerHoldingLovedItem, 14.0F), new ForgetAttackTargetTask<>(AbstractPiglinEntity::isAdult, PiglinTasks::findNearestValidAttackTarget), new SupplementedTask<>(PiglinEntity::canHunt, new StartHuntTask<>()), avoidRepellent(), babySometimesRideBabyHoglin(), createIdleLookBehaviors(), createIdleMovementBehaviors(), new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)));
   }

   private static void initFightActivity(PiglinEntity p_234488_0_, Brain<PiglinEntity> p_234488_1_) {
      p_234488_1_.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.<net.minecraft.entity.ai.brain.task.Task<? super PiglinEntity>>of(new FindNewAttackTargetTask<>((p_234523_1_) -> {
         return !isNearestValidAttackTarget(p_234488_0_, p_234523_1_);
      }), new SupplementedTask<>(PiglinTasks::hasCrossbow, new AttackStrafingTask<>(5, 0.75F)), new MoveToTargetTask(1.0F), new AttackTargetTask(20), new ShootTargetTask(), new FinishedHuntTask(), new PredicateTask<>(PiglinTasks::isNearZombified, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
   }

   private static void initCelebrateActivity(Brain<PiglinEntity> p_234495_0_) {
      p_234495_0_.addActivityAndRemoveMemoryWhenStopped(Activity.CELEBRATE, 10, ImmutableList.<net.minecraft.entity.ai.brain.task.Task<? super PiglinEntity>>of(avoidRepellent(), new LookAtEntityTask(PiglinTasks::isPlayerHoldingLovedItem, 14.0F), new ForgetAttackTargetTask<PiglinEntity>(AbstractPiglinEntity::isAdult, PiglinTasks::findNearestValidAttackTarget), new SupplementedTask<PiglinEntity>((p_234457_0_) -> {
         return !p_234457_0_.isDancing();
      }, new HuntCelebrationTask<>(2, 1.0F)), new SupplementedTask<PiglinEntity>(PiglinEntity::isDancing, new HuntCelebrationTask<>(4, 0.6F)), new FirstShuffledTask(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.PIGLIN, 8.0F), 1), Pair.of(new WalkRandomlyTask(0.6F, 2, 1), 1), Pair.of(new DummyTask(10, 20), 1)))), MemoryModuleType.CELEBRATE_LOCATION);
   }

   private static void initAdmireItemActivity(Brain<PiglinEntity> p_234502_0_) {
      p_234502_0_.addActivityAndRemoveMemoryWhenStopped(Activity.ADMIRE_ITEM, 10, ImmutableList.<net.minecraft.entity.ai.brain.task.Task<? super PiglinEntity>>of(new PickupWantedItemTask<>(PiglinTasks::isNotHoldingLovedItemInOffHand, 1.0F, true, 9), new ForgetAdmiredItemTask(9), new StopReachingItemTask(200, 200)), MemoryModuleType.ADMIRING_ITEM);
   }

   private static void initRetreatActivity(Brain<PiglinEntity> p_234507_0_) {
      p_234507_0_.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(RunAwayTask.entity(MemoryModuleType.AVOID_TARGET, 1.0F, 12, true), createIdleLookBehaviors(), createIdleMovementBehaviors(), new PredicateTask<PiglinEntity>(PiglinTasks::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
   }

   private static void initRideHoglinActivity(Brain<PiglinEntity> p_234511_0_) {
      p_234511_0_.addActivityAndRemoveMemoryWhenStopped(Activity.RIDE, 10, ImmutableList.of(new RideEntityTask<>(0.8F), new LookAtEntityTask(PiglinTasks::isPlayerHoldingLovedItem, 8.0F), new SupplementedTask<>(Entity::isPassenger, createIdleLookBehaviors()), new StopRidingEntityTask<>(8, PiglinTasks::wantsToStopRiding)), MemoryModuleType.RIDE_TARGET);
   }

   private static FirstShuffledTask<PiglinEntity> createIdleLookBehaviors() {
      return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityType.PIGLIN, 8.0F), 1), Pair.of(new LookAtEntityTask(8.0F), 1), Pair.of(new DummyTask(30, 60), 1)));
   }

   private static FirstShuffledTask<PiglinEntity> createIdleMovementBehaviors() {
      return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkRandomlyTask(0.6F), 2), Pair.of(InteractWithEntityTask.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new SupplementedTask<>(PiglinTasks::doesntSeeAnyPlayerHoldingLovedItem, new WalkTowardsLookTargetTask(0.6F, 3)), 2), Pair.of(new DummyTask(30, 60), 1)));
   }

   private static RunAwayTask<BlockPos> avoidRepellent() {
      return RunAwayTask.pos(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, false);
   }

   private static PiglinIdleActivityTask<PiglinEntity, LivingEntity> babyAvoidNemesis() {
      return new PiglinIdleActivityTask<>(PiglinEntity::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, BABY_AVOID_NEMESIS_DURATION);
   }

   private static PiglinIdleActivityTask<PiglinEntity, LivingEntity> avoidZombified() {
      return new PiglinIdleActivityTask<>(PiglinTasks::isNearZombified, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, AVOID_ZOMBIFIED_DURATION);
   }

   protected static void updateActivity(PiglinEntity p_234486_0_) {
      Brain<PiglinEntity> brain = p_234486_0_.getBrain();
      Activity activity = brain.getActiveNonCoreActivity().orElse((Activity)null);
      brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
      Activity activity1 = brain.getActiveNonCoreActivity().orElse((Activity)null);
      if (activity != activity1) {
         getSoundForCurrentActivity(p_234486_0_).ifPresent(p_234486_0_::playSound);
      }

      p_234486_0_.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
      if (!brain.hasMemoryValue(MemoryModuleType.RIDE_TARGET) && isBabyRidingBaby(p_234486_0_)) {
         p_234486_0_.stopRiding();
      }

      if (!brain.hasMemoryValue(MemoryModuleType.CELEBRATE_LOCATION)) {
         brain.eraseMemory(MemoryModuleType.DANCING);
      }

      p_234486_0_.setDancing(brain.hasMemoryValue(MemoryModuleType.DANCING));
   }

   private static boolean isBabyRidingBaby(PiglinEntity p_234522_0_) {
      if (!p_234522_0_.isBaby()) {
         return false;
      } else {
         Entity entity = p_234522_0_.getVehicle();
         return entity instanceof PiglinEntity && ((PiglinEntity)entity).isBaby() || entity instanceof HoglinEntity && ((HoglinEntity)entity).isBaby();
      }
   }

   protected static void pickUpItem(PiglinEntity p_234470_0_, ItemEntity p_234470_1_) {
      stopWalking(p_234470_0_);
      ItemStack itemstack;
      if (p_234470_1_.getItem().getItem() == Items.GOLD_NUGGET) {
         p_234470_0_.take(p_234470_1_, p_234470_1_.getItem().getCount());
         itemstack = p_234470_1_.getItem();
         p_234470_1_.remove();
      } else {
         p_234470_0_.take(p_234470_1_, 1);
         itemstack = removeOneItemFromItemEntity(p_234470_1_);
      }

      Item item = itemstack.getItem();
      if (isLovedItem(item)) {
         p_234470_0_.getBrain().eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
         holdInOffhand(p_234470_0_, itemstack);
         admireGoldItem(p_234470_0_);
      } else if (isFood(item) && !hasEatenRecently(p_234470_0_)) {
         eat(p_234470_0_);
      } else {
         boolean flag = p_234470_0_.equipItemIfPossible(itemstack);
         if (!flag) {
            putInInventory(p_234470_0_, itemstack);
         }
      }
   }

   private static void holdInOffhand(PiglinEntity p_241427_0_, ItemStack p_241427_1_) {
      if (isHoldingItemInOffHand(p_241427_0_)) {
         p_241427_0_.spawnAtLocation(p_241427_0_.getItemInHand(Hand.OFF_HAND));
      }

      p_241427_0_.holdInOffHand(p_241427_1_);
   }

   private static ItemStack removeOneItemFromItemEntity(ItemEntity p_234465_0_) {
      ItemStack itemstack = p_234465_0_.getItem();
      ItemStack itemstack1 = itemstack.split(1);
      if (itemstack.isEmpty()) {
         p_234465_0_.remove();
      } else {
         p_234465_0_.setItem(itemstack);
      }

      return itemstack1;
   }

   protected static void stopHoldingOffHandItem(PiglinEntity p_234477_0_, boolean p_234477_1_) {
      ItemStack itemstack = p_234477_0_.getItemInHand(Hand.OFF_HAND);
      p_234477_0_.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
      if (p_234477_0_.isAdult()) {
         boolean flag = itemstack.isPiglinCurrency();
         if (p_234477_1_ && flag) {
            throwItems(p_234477_0_, getBarterResponseItems(p_234477_0_));
         } else if (!flag) {
            boolean flag1 = p_234477_0_.equipItemIfPossible(itemstack);
            if (!flag1) {
               putInInventory(p_234477_0_, itemstack);
            }
         }
      } else {
         boolean flag2 = p_234477_0_.equipItemIfPossible(itemstack);
         if (!flag2) {
            ItemStack itemstack1 = p_234477_0_.getMainHandItem();
            if (isLovedItem(itemstack1.getItem())) {
               putInInventory(p_234477_0_, itemstack1);
            } else {
               throwItems(p_234477_0_, Collections.singletonList(itemstack1));
            }

            p_234477_0_.holdInMainHand(itemstack);
         }
      }

   }

   protected static void cancelAdmiring(PiglinEntity p_234496_0_) {
      if (isAdmiringItem(p_234496_0_) && !p_234496_0_.getOffhandItem().isEmpty()) {
         p_234496_0_.spawnAtLocation(p_234496_0_.getOffhandItem());
         p_234496_0_.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
      }

   }

   private static void putInInventory(PiglinEntity p_234498_0_, ItemStack p_234498_1_) {
      ItemStack itemstack = p_234498_0_.addToInventory(p_234498_1_);
      throwItemsTowardRandomPos(p_234498_0_, Collections.singletonList(itemstack));
   }

   private static void throwItems(PiglinEntity p_234475_0_, List<ItemStack> p_234475_1_) {
      Optional<PlayerEntity> optional = p_234475_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
      if (optional.isPresent()) {
         throwItemsTowardPlayer(p_234475_0_, optional.get(), p_234475_1_);
      } else {
         throwItemsTowardRandomPos(p_234475_0_, p_234475_1_);
      }

   }

   private static void throwItemsTowardRandomPos(PiglinEntity p_234490_0_, List<ItemStack> p_234490_1_) {
      throwItemsTowardPos(p_234490_0_, p_234490_1_, getRandomNearbyPos(p_234490_0_));
   }

   private static void throwItemsTowardPlayer(PiglinEntity p_234472_0_, PlayerEntity p_234472_1_, List<ItemStack> p_234472_2_) {
      throwItemsTowardPos(p_234472_0_, p_234472_2_, p_234472_1_.position());
   }

   private static void throwItemsTowardPos(PiglinEntity p_234476_0_, List<ItemStack> p_234476_1_, Vector3d p_234476_2_) {
      if (!p_234476_1_.isEmpty()) {
         p_234476_0_.swing(Hand.OFF_HAND);

         for(ItemStack itemstack : p_234476_1_) {
            BrainUtil.throwItem(p_234476_0_, itemstack, p_234476_2_.add(0.0D, 1.0D, 0.0D));
         }
      }

   }

   private static List<ItemStack> getBarterResponseItems(PiglinEntity p_234524_0_) {
      LootTable loottable = p_234524_0_.level.getServer().getLootTables().get(LootTables.PIGLIN_BARTERING);
      return loottable.getRandomItems((new LootContext.Builder((ServerWorld)p_234524_0_.level)).withParameter(LootParameters.THIS_ENTITY, p_234524_0_).withRandom(p_234524_0_.level.random).create(LootParameterSets.PIGLIN_BARTER));
   }

   private static boolean wantsToDance(LivingEntity p_234461_0_, LivingEntity p_234461_1_) {
      if (p_234461_1_.getType() != EntityType.HOGLIN) {
         return false;
      } else {
         return (new Random(p_234461_0_.level.getGameTime())).nextFloat() < 0.1F;
      }
   }

   protected static boolean wantsToPickup(PiglinEntity p_234474_0_, ItemStack p_234474_1_) {
      Item item = p_234474_1_.getItem();
      if (item.is(ItemTags.PIGLIN_REPELLENTS)) {
         return false;
      } else if (isAdmiringDisabled(p_234474_0_) && p_234474_0_.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
         return false;
      } else if (p_234474_1_.isPiglinCurrency()) {
         return isNotHoldingLovedItemInOffHand(p_234474_0_);
      } else {
         boolean flag = p_234474_0_.canAddToInventory(p_234474_1_);
         if (item == Items.GOLD_NUGGET) {
            return flag;
         } else if (isFood(item)) {
            return !hasEatenRecently(p_234474_0_) && flag;
         } else if (!isLovedItem(item)) {
            return p_234474_0_.canReplaceCurrentItem(p_234474_1_);
         } else {
            return isNotHoldingLovedItemInOffHand(p_234474_0_) && flag;
         }
      }
   }

   protected static boolean isLovedItem(Item p_234480_0_) {
      return p_234480_0_.is(ItemTags.PIGLIN_LOVED);
   }

   private static boolean wantsToStopRiding(PiglinEntity p_234467_0_, Entity p_234467_1_) {
      if (!(p_234467_1_ instanceof MobEntity)) {
         return false;
      } else {
         MobEntity mobentity = (MobEntity)p_234467_1_;
         return !mobentity.isBaby() || !mobentity.isAlive() || wasHurtRecently(p_234467_0_) || wasHurtRecently(mobentity) || mobentity instanceof PiglinEntity && mobentity.getVehicle() == null;
      }
   }

   private static boolean isNearestValidAttackTarget(PiglinEntity p_234504_0_, LivingEntity p_234504_1_) {
      return findNearestValidAttackTarget(p_234504_0_).filter((p_234483_1_) -> {
         return p_234483_1_ == p_234504_1_;
      }).isPresent();
   }

   private static boolean isNearZombified(PiglinEntity p_234525_0_) {
      Brain<PiglinEntity> brain = p_234525_0_.getBrain();
      if (brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
         LivingEntity livingentity = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
         return p_234525_0_.closerThan(livingentity, 6.0D);
      } else {
         return false;
      }
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(PiglinEntity p_234526_0_) {
      Brain<PiglinEntity> brain = p_234526_0_.getBrain();
      if (isNearZombified(p_234526_0_)) {
         return Optional.empty();
      } else {
         Optional<LivingEntity> optional = BrainUtil.getLivingEntityFromUUIDMemory(p_234526_0_, MemoryModuleType.ANGRY_AT);
         if (optional.isPresent() && isAttackAllowed(optional.get())) {
            return optional;
         } else {
            if (brain.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
               Optional<PlayerEntity> optional1 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
               if (optional1.isPresent()) {
                  return optional1;
               }
            }

            Optional<MobEntity> optional3 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
            if (optional3.isPresent()) {
               return optional3;
            } else {
               Optional<PlayerEntity> optional2 = brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
               return optional2.isPresent() && isAttackAllowed(optional2.get()) ? optional2 : Optional.empty();
            }
         }
      }
   }

   public static void angerNearbyPiglins(PlayerEntity p_234478_0_, boolean p_234478_1_) {
      List<PiglinEntity> list = p_234478_0_.level.getEntitiesOfClass(PiglinEntity.class, p_234478_0_.getBoundingBox().inflate(16.0D));
      list.stream().filter(PiglinTasks::isIdle).filter((p_234491_2_) -> {
         return !p_234478_1_ || BrainUtil.canSee(p_234491_2_, p_234478_0_);
      }).forEach((p_234479_1_) -> {
         if (p_234479_1_.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            setAngerTargetToNearestTargetablePlayerIfFound(p_234479_1_, p_234478_0_);
         } else {
            setAngerTarget(p_234479_1_, p_234478_0_);
         }

      });
   }

   public static ActionResultType mobInteract(PiglinEntity p_234471_0_, PlayerEntity p_234471_1_, Hand p_234471_2_) {
      ItemStack itemstack = p_234471_1_.getItemInHand(p_234471_2_);
      if (canAdmire(p_234471_0_, itemstack)) {
         ItemStack itemstack1 = itemstack.split(1);
         holdInOffhand(p_234471_0_, itemstack1);
         admireGoldItem(p_234471_0_);
         stopWalking(p_234471_0_);
         return ActionResultType.CONSUME;
      } else {
         return ActionResultType.PASS;
      }
   }

   protected static boolean canAdmire(PiglinEntity p_234489_0_, ItemStack p_234489_1_) {
      return !isAdmiringDisabled(p_234489_0_) && !isAdmiringItem(p_234489_0_) && p_234489_0_.isAdult() && p_234489_1_.isPiglinCurrency();
   }

   protected static void wasHurtBy(PiglinEntity p_234468_0_, LivingEntity p_234468_1_) {
      if (!(p_234468_1_ instanceof PiglinEntity)) {
         if (isHoldingItemInOffHand(p_234468_0_)) {
            stopHoldingOffHandItem(p_234468_0_, false);
         }

         Brain<PiglinEntity> brain = p_234468_0_.getBrain();
         brain.eraseMemory(MemoryModuleType.CELEBRATE_LOCATION);
         brain.eraseMemory(MemoryModuleType.DANCING);
         brain.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
         if (p_234468_1_ instanceof PlayerEntity) {
            brain.setMemoryWithExpiry(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
         }

         getAvoidTarget(p_234468_0_).ifPresent((p_234462_2_) -> {
            if (p_234462_2_.getType() != p_234468_1_.getType()) {
               brain.eraseMemory(MemoryModuleType.AVOID_TARGET);
            }

         });
         if (p_234468_0_.isBaby()) {
            brain.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, p_234468_1_, 100L);
            if (isAttackAllowed(p_234468_1_)) {
               broadcastAngerTarget(p_234468_0_, p_234468_1_);
            }

         } else if (p_234468_1_.getType() == EntityType.HOGLIN && hoglinsOutnumberPiglins(p_234468_0_)) {
            setAvoidTargetAndDontHuntForAWhile(p_234468_0_, p_234468_1_);
            broadcastRetreat(p_234468_0_, p_234468_1_);
         } else {
            maybeRetaliate(p_234468_0_, p_234468_1_);
         }
      }
   }

   protected static void maybeRetaliate(AbstractPiglinEntity p_234509_0_, LivingEntity p_234509_1_) {
      if (!p_234509_0_.getBrain().isActive(Activity.AVOID)) {
         if (isAttackAllowed(p_234509_1_)) {
            if (!BrainUtil.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(p_234509_0_, p_234509_1_, 4.0D)) {
               if (p_234509_1_.getType() == EntityType.PLAYER && p_234509_0_.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                  setAngerTargetToNearestTargetablePlayerIfFound(p_234509_0_, p_234509_1_);
                  broadcastUniversalAnger(p_234509_0_);
               } else {
                  setAngerTarget(p_234509_0_, p_234509_1_);
                  broadcastAngerTarget(p_234509_0_, p_234509_1_);
               }

            }
         }
      }
   }

   public static Optional<SoundEvent> getSoundForCurrentActivity(PiglinEntity p_241429_0_) {
      return p_241429_0_.getBrain().getActiveNonCoreActivity().map((p_241426_1_) -> {
         return getSoundForActivity(p_241429_0_, p_241426_1_);
      });
   }

   private static SoundEvent getSoundForActivity(PiglinEntity p_241422_0_, Activity p_241422_1_) {
      if (p_241422_1_ == Activity.FIGHT) {
         return SoundEvents.PIGLIN_ANGRY;
      } else if (p_241422_0_.isConverting()) {
         return SoundEvents.PIGLIN_RETREAT;
      } else if (p_241422_1_ == Activity.AVOID && isNearAvoidTarget(p_241422_0_)) {
         return SoundEvents.PIGLIN_RETREAT;
      } else if (p_241422_1_ == Activity.ADMIRE_ITEM) {
         return SoundEvents.PIGLIN_ADMIRING_ITEM;
      } else if (p_241422_1_ == Activity.CELEBRATE) {
         return SoundEvents.PIGLIN_CELEBRATE;
      } else if (seesPlayerHoldingLovedItem(p_241422_0_)) {
         return SoundEvents.PIGLIN_JEALOUS;
      } else {
         return isNearRepellent(p_241422_0_) ? SoundEvents.PIGLIN_RETREAT : SoundEvents.PIGLIN_AMBIENT;
      }
   }

   private static boolean isNearAvoidTarget(PiglinEntity p_234528_0_) {
      Brain<PiglinEntity> brain = p_234528_0_.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.AVOID_TARGET) ? false : brain.getMemory(MemoryModuleType.AVOID_TARGET).get().closerThan(p_234528_0_, 12.0D);
   }

   protected static boolean hasAnyoneNearbyHuntedRecently(PiglinEntity p_234508_0_) {
      return p_234508_0_.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY) || getVisibleAdultPiglins(p_234508_0_).stream().anyMatch((p_234456_0_) -> {
         return p_234456_0_.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
      });
   }

   private static List<AbstractPiglinEntity> getVisibleAdultPiglins(PiglinEntity p_234529_0_) {
      return p_234529_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
   }

   private static List<AbstractPiglinEntity> getAdultPiglins(AbstractPiglinEntity p_234530_0_) {
      return p_234530_0_.getBrain().getMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
   }

   public static boolean isWearingGold(LivingEntity p_234460_0_) {
      for(ItemStack itemstack : p_234460_0_.getArmorSlots()) {
         Item item = itemstack.getItem();
         if (itemstack.makesPiglinsNeutral(p_234460_0_)) {
            return true;
         }
      }

      return false;
   }

   private static void stopWalking(PiglinEntity p_234531_0_) {
      p_234531_0_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_234531_0_.getNavigation().stop();
   }

   private static RunSometimesTask<PiglinEntity> babySometimesRideBabyHoglin() {
      return new RunSometimesTask<>(new PiglinIdleActivityTask<>(PiglinEntity::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, RIDE_DURATION), RIDE_START_INTERVAL);
   }

   protected static void broadcastAngerTarget(AbstractPiglinEntity p_234487_0_, LivingEntity p_234487_1_) {
      getAdultPiglins(p_234487_0_).forEach((p_234484_1_) -> {
         if (p_234487_1_.getType() != EntityType.HOGLIN || p_234484_1_.canHunt() && ((HoglinEntity)p_234487_1_).canBeHunted()) {
            setAngerTargetIfCloserThanCurrent(p_234484_1_, p_234487_1_);
         }
      });
   }

   protected static void broadcastUniversalAnger(AbstractPiglinEntity p_241430_0_) {
      getAdultPiglins(p_241430_0_).forEach((p_241419_0_) -> {
         getNearestVisibleTargetablePlayer(p_241419_0_).ifPresent((p_241421_1_) -> {
            setAngerTarget(p_241419_0_, p_241421_1_);
         });
      });
   }

   protected static void broadcastDontKillAnyMoreHoglinsForAWhile(PiglinEntity p_234512_0_) {
      getVisibleAdultPiglins(p_234512_0_).forEach(PiglinTasks::dontKillAnyMoreHoglinsForAWhile);
   }

   protected static void setAngerTarget(AbstractPiglinEntity p_234497_0_, LivingEntity p_234497_1_) {
      if (isAttackAllowed(p_234497_1_)) {
         p_234497_0_.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         p_234497_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, p_234497_1_.getUUID(), 600L);
         if (p_234497_1_.getType() == EntityType.HOGLIN && p_234497_0_.canHunt()) {
            dontKillAnyMoreHoglinsForAWhile(p_234497_0_);
         }

         if (p_234497_1_.getType() == EntityType.PLAYER && p_234497_0_.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            p_234497_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
         }

      }
   }

   private static void setAngerTargetToNearestTargetablePlayerIfFound(AbstractPiglinEntity p_241431_0_, LivingEntity p_241431_1_) {
      Optional<PlayerEntity> optional = getNearestVisibleTargetablePlayer(p_241431_0_);
      if (optional.isPresent()) {
         setAngerTarget(p_241431_0_, optional.get());
      } else {
         setAngerTarget(p_241431_0_, p_241431_1_);
      }

   }

   private static void setAngerTargetIfCloserThanCurrent(AbstractPiglinEntity p_234513_0_, LivingEntity p_234513_1_) {
      Optional<LivingEntity> optional = getAngerTarget(p_234513_0_);
      LivingEntity livingentity = BrainUtil.getNearestTarget(p_234513_0_, optional, p_234513_1_);
      if (!optional.isPresent() || optional.get() != livingentity) {
         setAngerTarget(p_234513_0_, livingentity);
      }
   }

   private static Optional<LivingEntity> getAngerTarget(AbstractPiglinEntity p_234532_0_) {
      return BrainUtil.getLivingEntityFromUUIDMemory(p_234532_0_, MemoryModuleType.ANGRY_AT);
   }

   public static Optional<LivingEntity> getAvoidTarget(PiglinEntity p_234515_0_) {
      return p_234515_0_.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) ? p_234515_0_.getBrain().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
   }

   public static Optional<PlayerEntity> getNearestVisibleTargetablePlayer(AbstractPiglinEntity p_241432_0_) {
      return p_241432_0_.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) ? p_241432_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
   }

   private static void broadcastRetreat(PiglinEntity p_234516_0_, LivingEntity p_234516_1_) {
      getVisibleAdultPiglins(p_234516_0_).stream().filter((p_242341_0_) -> {
         return p_242341_0_ instanceof PiglinEntity;
      }).forEach((p_234463_1_) -> {
         retreatFromNearestTarget((PiglinEntity)p_234463_1_, p_234516_1_);
      });
   }

   private static void retreatFromNearestTarget(PiglinEntity p_234519_0_, LivingEntity p_234519_1_) {
      Brain<PiglinEntity> brain = p_234519_0_.getBrain();
      LivingEntity lvt_3_1_ = BrainUtil.getNearestTarget(p_234519_0_, brain.getMemory(MemoryModuleType.AVOID_TARGET), p_234519_1_);
      lvt_3_1_ = BrainUtil.getNearestTarget(p_234519_0_, brain.getMemory(MemoryModuleType.ATTACK_TARGET), lvt_3_1_);
      setAvoidTargetAndDontHuntForAWhile(p_234519_0_, lvt_3_1_);
   }

   private static boolean wantsToStopFleeing(PiglinEntity p_234533_0_) {
      Brain<PiglinEntity> brain = p_234533_0_.getBrain();
      if (!brain.hasMemoryValue(MemoryModuleType.AVOID_TARGET)) {
         return true;
      } else {
         LivingEntity livingentity = brain.getMemory(MemoryModuleType.AVOID_TARGET).get();
         EntityType<?> entitytype = livingentity.getType();
         if (entitytype == EntityType.HOGLIN) {
            return piglinsEqualOrOutnumberHoglins(p_234533_0_);
         } else if (isZombified(entitytype)) {
            return !brain.isMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, livingentity);
         } else {
            return false;
         }
      }
   }

   private static boolean piglinsEqualOrOutnumberHoglins(PiglinEntity p_234534_0_) {
      return !hoglinsOutnumberPiglins(p_234534_0_);
   }

   private static boolean hoglinsOutnumberPiglins(PiglinEntity p_234535_0_) {
      int i = p_234535_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
      int j = p_234535_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
      return j > i;
   }

   private static void setAvoidTargetAndDontHuntForAWhile(PiglinEntity p_234521_0_, LivingEntity p_234521_1_) {
      p_234521_0_.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
      p_234521_0_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      p_234521_0_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_234521_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, p_234521_1_, (long)RETREAT_DURATION.randomValue(p_234521_0_.level.random));
      dontKillAnyMoreHoglinsForAWhile(p_234521_0_);
   }

   protected static void dontKillAnyMoreHoglinsForAWhile(AbstractPiglinEntity p_234518_0_) {
      p_234518_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, (long)TIME_BETWEEN_HUNTS.randomValue(p_234518_0_.level.random));
   }

   private static void eat(PiglinEntity p_234536_0_) {
      p_234536_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.ATE_RECENTLY, true, 200L);
   }

   private static Vector3d getRandomNearbyPos(PiglinEntity p_234537_0_) {
      Vector3d vector3d = RandomPositionGenerator.getLandPos(p_234537_0_, 4, 2);
      return vector3d == null ? p_234537_0_.position() : vector3d;
   }

   private static boolean hasEatenRecently(PiglinEntity p_234538_0_) {
      return p_234538_0_.getBrain().hasMemoryValue(MemoryModuleType.ATE_RECENTLY);
   }

   protected static boolean isIdle(AbstractPiglinEntity p_234520_0_) {
      return p_234520_0_.getBrain().isActive(Activity.IDLE);
   }

   private static boolean hasCrossbow(LivingEntity p_234494_0_) {
      return p_234494_0_.isHolding(Items.CROSSBOW);
   }

   private static void admireGoldItem(LivingEntity p_234501_0_) {
      p_234501_0_.getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, 120L);
   }

   private static boolean isAdmiringItem(PiglinEntity p_234451_0_) {
      return p_234451_0_.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_ITEM);
   }

   private static boolean isBarterCurrency(Item p_234492_0_) {
      return p_234492_0_ == BARTERING_ITEM;
   }

   private static boolean isFood(Item p_234499_0_) {
      return FOOD_ITEMS.contains(p_234499_0_);
   }

   private static boolean isAttackAllowed(LivingEntity p_234506_0_) {
      return EntityPredicates.ATTACK_ALLOWED.test(p_234506_0_);
   }

   private static boolean isNearRepellent(PiglinEntity p_234452_0_) {
      return p_234452_0_.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
   }

   private static boolean seesPlayerHoldingLovedItem(LivingEntity p_234510_0_) {
      return p_234510_0_.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
   }

   private static boolean doesntSeeAnyPlayerHoldingLovedItem(LivingEntity p_234514_0_) {
      return !seesPlayerHoldingLovedItem(p_234514_0_);
   }

   public static boolean isPlayerHoldingLovedItem(LivingEntity p_234482_0_) {
      return p_234482_0_.getType() == EntityType.PLAYER && p_234482_0_.isHolding(PiglinTasks::isLovedItem);
   }

   private static boolean isAdmiringDisabled(PiglinEntity p_234453_0_) {
      return p_234453_0_.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_DISABLED);
   }

   private static boolean wasHurtRecently(LivingEntity p_234517_0_) {
      return p_234517_0_.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
   }

   private static boolean isHoldingItemInOffHand(PiglinEntity p_234454_0_) {
      return !p_234454_0_.getOffhandItem().isEmpty();
   }

   private static boolean isNotHoldingLovedItemInOffHand(PiglinEntity p_234455_0_) {
      return p_234455_0_.getOffhandItem().isEmpty() || !isLovedItem(p_234455_0_.getOffhandItem().getItem());
   }

   public static boolean isZombified(EntityType p_234459_0_) {
      return p_234459_0_ == EntityType.ZOMBIFIED_PIGLIN || p_234459_0_ == EntityType.ZOGLIN;
   }
}
