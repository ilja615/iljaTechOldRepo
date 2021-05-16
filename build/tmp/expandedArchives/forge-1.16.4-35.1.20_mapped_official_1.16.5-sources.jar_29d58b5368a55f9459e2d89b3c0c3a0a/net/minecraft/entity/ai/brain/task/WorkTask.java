package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class WorkTask extends Task<CreatureEntity> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private long nextOkStartTime;
   private final int maxDistanceFromPoi;
   private float speedModifier;

   public WorkTask(MemoryModuleType<GlobalPos> p_i241909_1_, float p_i241909_2_, int p_i241909_3_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i241909_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.memoryType = p_i241909_1_;
      this.speedModifier = p_i241909_2_;
      this.maxDistanceFromPoi = p_i241909_3_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      Optional<GlobalPos> optional = p_212832_2_.getBrain().getMemory(this.memoryType);
      return optional.isPresent() && p_212832_1_.dimension() == optional.get().dimension() && optional.get().pos().closerThan(p_212832_2_.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.nextOkStartTime) {
         Optional<Vector3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(p_212831_2_, 8, 6));
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220564_1_) -> {
            return new WalkTarget(p_220564_1_, this.speedModifier, 1);
         }));
         this.nextOkStartTime = p_212831_3_ + 180L;
      }

   }
}
