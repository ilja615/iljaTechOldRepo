package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;

public class NearestLivingEntitiesSensor extends Sensor<LivingEntity> {
   protected void doTick(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      AxisAlignedBB axisalignedbb = p_212872_2_.getBoundingBox().inflate(16.0D, 16.0D, 16.0D);
      List<LivingEntity> list = p_212872_1_.getEntitiesOfClass(LivingEntity.class, axisalignedbb, (p_220980_1_) -> {
         return p_220980_1_ != p_212872_2_ && p_220980_1_.isAlive();
      });
      list.sort(Comparator.comparingDouble(p_212872_2_::distanceToSqr));
      Brain<?> brain = p_212872_2_.getBrain();
      brain.setMemory(MemoryModuleType.LIVING_ENTITIES, list);
      brain.setMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES, list.stream().filter((p_220981_1_) -> {
         return isEntityTargetable(p_212872_2_, p_220981_1_);
      }).collect(Collectors.toList()));
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }
}
