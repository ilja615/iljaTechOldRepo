package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class WalkTowardsPosTask extends Task<CreatureEntity> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final int closeEnoughDist;
   private final int maxDistanceFromPoi;
   private final float speedModifier;
   private long nextOkStartTime;

   public WalkTowardsPosTask(MemoryModuleType<GlobalPos> p_i241910_1_, float p_i241910_2_, int p_i241910_3_, int p_i241910_4_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i241910_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.memoryType = p_i241910_1_;
      this.speedModifier = p_i241910_2_;
      this.closeEnoughDist = p_i241910_3_;
      this.maxDistanceFromPoi = p_i241910_4_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      Optional<GlobalPos> optional = p_212832_2_.getBrain().getMemory(this.memoryType);
      return optional.isPresent() && p_212832_1_.dimension() == optional.get().dimension() && optional.get().pos().closerThan(p_212832_2_.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.nextOkStartTime) {
         Brain<?> brain = p_212831_2_.getBrain();
         Optional<GlobalPos> optional = brain.getMemory(this.memoryType);
         optional.ifPresent((p_220580_2_) -> {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220580_2_.pos(), this.speedModifier, this.closeEnoughDist));
         });
         this.nextOkStartTime = p_212831_3_ + 80L;
      }

   }
}
