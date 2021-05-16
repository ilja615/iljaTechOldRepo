package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

public class HurtBySensor extends Sensor<LivingEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
   }

   protected void doTick(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      Brain<?> brain = p_212872_2_.getBrain();
      DamageSource damagesource = p_212872_2_.getLastDamageSource();
      if (damagesource != null) {
         brain.setMemory(MemoryModuleType.HURT_BY, p_212872_2_.getLastDamageSource());
         Entity entity = damagesource.getEntity();
         if (entity instanceof LivingEntity) {
            brain.setMemory(MemoryModuleType.HURT_BY_ENTITY, (LivingEntity)entity);
         }
      } else {
         brain.eraseMemory(MemoryModuleType.HURT_BY);
      }

      brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).ifPresent((p_234121_2_) -> {
         if (!p_234121_2_.isAlive() || p_234121_2_.level != p_212872_1_) {
            brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         }

      });
   }
}
