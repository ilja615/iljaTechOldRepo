package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class WalkRandomlyInsideTask extends Task<CreatureEntity> {
   private final float speedModifier;

   public WalkRandomlyInsideTask(float p_i50364_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.speedModifier = p_i50364_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      return !p_212832_1_.canSeeSky(p_212832_2_.blockPosition());
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      BlockPos blockpos = p_212831_2_.blockPosition();
      List<BlockPos> list = BlockPos.betweenClosedStream(blockpos.offset(-1, -1, -1), blockpos.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
      Collections.shuffle(list);
      Optional<BlockPos> optional = list.stream().filter((p_220428_1_) -> {
         return !p_212831_1_.canSeeSky(p_220428_1_);
      }).filter((p_220427_2_) -> {
         return p_212831_1_.loadedAndEntityCanStandOn(p_220427_2_, p_212831_2_);
      }).filter((p_220429_2_) -> {
         return p_212831_1_.noCollision(p_212831_2_);
      }).findFirst();
      optional.ifPresent((p_220430_2_) -> {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220430_2_, this.speedModifier, 0));
      });
   }
}
