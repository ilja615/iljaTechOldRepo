package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class ExpireHidingTask extends Task<LivingEntity> {
   private final int closeEnoughDist;
   private final int stayHiddenTicks;
   private int ticksHidden;

   public ExpireHidingTask(int p_i50349_1_, int p_i50349_2_) {
      super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleStatus.VALUE_PRESENT));
      this.stayHiddenTicks = p_i50349_1_ * 20;
      this.ticksHidden = 0;
      this.closeEnoughDist = p_i50349_2_;
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      Optional<Long> optional = brain.getMemory(MemoryModuleType.HEARD_BELL_TIME);
      boolean flag = optional.get() + 300L <= p_212831_3_;
      if (this.ticksHidden <= this.stayHiddenTicks && !flag) {
         BlockPos blockpos = brain.getMemory(MemoryModuleType.HIDING_PLACE).get().pos();
         if (blockpos.closerThan(p_212831_2_.blockPosition(), (double)this.closeEnoughDist)) {
            ++this.ticksHidden;
         }

      } else {
         brain.eraseMemory(MemoryModuleType.HEARD_BELL_TIME);
         brain.eraseMemory(MemoryModuleType.HIDING_PLACE);
         brain.updateActivityFromSchedule(p_212831_1_.getDayTime(), p_212831_1_.getGameTime());
         this.ticksHidden = 0;
      }
   }
}
