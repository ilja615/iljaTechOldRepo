package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Random;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class HuntCelebrationTask<E extends MobEntity> extends Task<E> {
   private final int closeEnoughDist;
   private final float speedModifier;

   public HuntCelebrationTask(int p_i231518_1_, float p_i231518_2_) {
      super(ImmutableMap.of(MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED));
      this.closeEnoughDist = p_i231518_1_;
      this.speedModifier = p_i231518_2_;
   }

   protected void start(ServerWorld p_212831_1_, MobEntity p_212831_2_, long p_212831_3_) {
      BlockPos blockpos = getCelebrateLocation(p_212831_2_);
      boolean flag = blockpos.closerThan(p_212831_2_.blockPosition(), (double)this.closeEnoughDist);
      if (!flag) {
         BrainUtil.setWalkAndLookTargetMemories(p_212831_2_, getNearbyPos(p_212831_2_, blockpos), this.speedModifier, this.closeEnoughDist);
      }

   }

   private static BlockPos getNearbyPos(MobEntity p_233900_0_, BlockPos p_233900_1_) {
      Random random = p_233900_0_.level.random;
      return p_233900_1_.offset(getRandomOffset(random), 0, getRandomOffset(random));
   }

   private static int getRandomOffset(Random p_233901_0_) {
      return p_233901_0_.nextInt(3) - 1;
   }

   private static BlockPos getCelebrateLocation(MobEntity p_233899_0_) {
      return p_233899_0_.getBrain().getMemory(MemoryModuleType.CELEBRATE_LOCATION).get();
   }
}
