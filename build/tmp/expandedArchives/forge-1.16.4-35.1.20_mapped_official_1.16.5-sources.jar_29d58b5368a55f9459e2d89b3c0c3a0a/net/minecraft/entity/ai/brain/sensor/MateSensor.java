package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class MateSensor extends Sensor<AgeableEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }

   protected void doTick(ServerWorld p_212872_1_, AgeableEntity p_212872_2_) {
      p_212872_2_.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((p_234118_2_) -> {
         this.setNearestVisibleAdult(p_212872_2_, p_234118_2_);
      });
   }

   private void setNearestVisibleAdult(AgeableEntity p_234116_1_, List<LivingEntity> p_234116_2_) {
      Optional<AgeableEntity> optional = p_234116_2_.stream().filter((p_234115_1_) -> {
         return p_234115_1_.getType() == p_234116_1_.getType();
      }).map((p_234117_0_) -> {
         return (AgeableEntity)p_234117_0_;
      }).filter((p_234114_0_) -> {
         return !p_234114_0_.isBaby();
      }).findFirst();
      p_234116_1_.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
   }
}
