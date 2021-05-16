package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class PiglinMobsSensor extends Sensor<LivingEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT);
   }

   protected void doTick(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      Brain<?> brain = p_212872_2_.getBrain();
      brain.setMemory(MemoryModuleType.NEAREST_REPELLENT, findNearestRepellent(p_212872_1_, p_212872_2_));
      Optional<MobEntity> optional = Optional.empty();
      Optional<HoglinEntity> optional1 = Optional.empty();
      Optional<HoglinEntity> optional2 = Optional.empty();
      Optional<PiglinEntity> optional3 = Optional.empty();
      Optional<LivingEntity> optional4 = Optional.empty();
      Optional<PlayerEntity> optional5 = Optional.empty();
      Optional<PlayerEntity> optional6 = Optional.empty();
      int i = 0;
      List<AbstractPiglinEntity> list = Lists.newArrayList();
      List<AbstractPiglinEntity> list1 = Lists.newArrayList();

      for(LivingEntity livingentity : brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(ImmutableList.of())) {
         if (livingentity instanceof HoglinEntity) {
            HoglinEntity hoglinentity = (HoglinEntity)livingentity;
            if (hoglinentity.isBaby() && !optional2.isPresent()) {
               optional2 = Optional.of(hoglinentity);
            } else if (hoglinentity.isAdult()) {
               ++i;
               if (!optional1.isPresent() && hoglinentity.canBeHunted()) {
                  optional1 = Optional.of(hoglinentity);
               }
            }
         } else if (livingentity instanceof PiglinBruteEntity) {
            list.add((PiglinBruteEntity)livingentity);
         } else if (livingentity instanceof PiglinEntity) {
            PiglinEntity piglinentity = (PiglinEntity)livingentity;
            if (piglinentity.isBaby() && !optional3.isPresent()) {
               optional3 = Optional.of(piglinentity);
            } else if (piglinentity.isAdult()) {
               list.add(piglinentity);
            }
         } else if (livingentity instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)livingentity;
            if (!optional5.isPresent() && EntityPredicates.ATTACK_ALLOWED.test(livingentity) && !PiglinTasks.isWearingGold(playerentity)) {
               optional5 = Optional.of(playerentity);
            }

            if (!optional6.isPresent() && !playerentity.isSpectator() && PiglinTasks.isPlayerHoldingLovedItem(playerentity)) {
               optional6 = Optional.of(playerentity);
            }
         } else if (optional.isPresent() || !(livingentity instanceof WitherSkeletonEntity) && !(livingentity instanceof WitherEntity)) {
            if (!optional4.isPresent() && PiglinTasks.isZombified(livingentity.getType())) {
               optional4 = Optional.of(livingentity);
            }
         } else {
            optional = Optional.of((MobEntity)livingentity);
         }
      }

      for(LivingEntity livingentity1 : brain.getMemory(MemoryModuleType.LIVING_ENTITIES).orElse(ImmutableList.of())) {
         if (livingentity1 instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)livingentity1).isAdult()) {
            list1.add((AbstractPiglinEntity)livingentity1);
         }
      }

      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, optional1);
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional2);
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, optional4);
      brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional5);
      brain.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional6);
      brain.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, list1);
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, list);
      brain.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, list.size());
      brain.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
   }

   private static Optional<BlockPos> findNearestRepellent(ServerWorld p_234126_0_, LivingEntity p_234126_1_) {
      return BlockPos.findClosestMatch(p_234126_1_.blockPosition(), 8, 4, (p_234125_1_) -> {
         return isValidRepellent(p_234126_0_, p_234125_1_);
      });
   }

   private static boolean isValidRepellent(ServerWorld p_241391_0_, BlockPos p_241391_1_) {
      BlockState blockstate = p_241391_0_.getBlockState(p_241391_1_);
      boolean flag = blockstate.is(BlockTags.PIGLIN_REPELLENTS);
      return flag && blockstate.is(Blocks.SOUL_CAMPFIRE) ? CampfireBlock.isLitCampfire(blockstate) : flag;
   }
}
