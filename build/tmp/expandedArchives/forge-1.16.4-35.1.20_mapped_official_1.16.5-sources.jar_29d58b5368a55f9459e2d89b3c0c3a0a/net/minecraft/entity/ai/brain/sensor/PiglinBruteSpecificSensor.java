package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.world.server.ServerWorld;

public class PiglinBruteSpecificSensor extends Sensor<LivingEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
   }

   protected void doTick(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      Brain<?> brain = p_212872_2_.getBrain();
      Optional<MobEntity> optional = Optional.empty();
      List<AbstractPiglinEntity> list = Lists.newArrayList();

      for(LivingEntity livingentity : brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(ImmutableList.of())) {
         if (livingentity instanceof WitherSkeletonEntity || livingentity instanceof WitherEntity) {
            optional = Optional.of((MobEntity)livingentity);
            break;
         }
      }

      for(LivingEntity livingentity1 : brain.getMemory(MemoryModuleType.LIVING_ENTITIES).orElse(ImmutableList.of())) {
         if (livingentity1 instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)livingentity1).isAdult()) {
            list.add((AbstractPiglinEntity)livingentity1);
         }
      }

      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
      brain.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, list);
   }
}
