package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class StopRidingEntityTask<E extends LivingEntity, T extends Entity> extends Task<E> {
   private final int maxWalkDistToRideTarget;
   private final BiPredicate<E, Entity> dontRideIf;

   public StopRidingEntityTask(int p_i231515_1_, BiPredicate<E, Entity> p_i231515_2_) {
      super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryModuleStatus.REGISTERED));
      this.maxWalkDistToRideTarget = p_i231515_1_;
      this.dontRideIf = p_i231515_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      Entity entity = p_212832_2_.getVehicle();
      Entity entity1 = p_212832_2_.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).orElse((Entity)null);
      if (entity == null && entity1 == null) {
         return false;
      } else {
         Entity entity2 = entity == null ? entity1 : entity;
         return !this.isVehicleValid(p_212832_2_, entity2) || this.dontRideIf.test(p_212832_2_, entity2);
      }
   }

   private boolean isVehicleValid(E p_233892_1_, Entity p_233892_2_) {
      return p_233892_2_.isAlive() && p_233892_2_.closerThan(p_233892_1_, (double)this.maxWalkDistToRideTarget) && p_233892_2_.level == p_233892_1_.level;
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      p_212831_2_.stopRiding();
      p_212831_2_.getBrain().eraseMemory(MemoryModuleType.RIDE_TARGET);
   }
}
