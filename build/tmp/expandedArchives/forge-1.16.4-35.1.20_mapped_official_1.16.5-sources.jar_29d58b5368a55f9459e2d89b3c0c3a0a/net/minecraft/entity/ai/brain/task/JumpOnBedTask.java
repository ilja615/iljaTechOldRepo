package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class JumpOnBedTask extends Task<MobEntity> {
   private final float speedModifier;
   @Nullable
   private BlockPos targetBed;
   private int remainingTimeToReachBed;
   private int remainingJumps;
   private int remainingCooldownUntilNextJump;

   public JumpOnBedTask(float p_i50362_1_) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.speedModifier = p_i50362_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, MobEntity p_212832_2_) {
      return p_212832_2_.isBaby() && this.nearBed(p_212832_1_, p_212832_2_);
   }

   protected void start(ServerWorld p_212831_1_, MobEntity p_212831_2_, long p_212831_3_) {
      super.start(p_212831_1_, p_212831_2_, p_212831_3_);
      this.getNearestBed(p_212831_2_).ifPresent((p_220461_3_) -> {
         this.targetBed = p_220461_3_;
         this.remainingTimeToReachBed = 100;
         this.remainingJumps = 3 + p_212831_1_.random.nextInt(4);
         this.remainingCooldownUntilNextJump = 0;
         this.startWalkingTowardsBed(p_212831_2_, p_220461_3_);
      });
   }

   protected void stop(ServerWorld p_212835_1_, MobEntity p_212835_2_, long p_212835_3_) {
      super.stop(p_212835_1_, p_212835_2_, p_212835_3_);
      this.targetBed = null;
      this.remainingTimeToReachBed = 0;
      this.remainingJumps = 0;
      this.remainingCooldownUntilNextJump = 0;
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, MobEntity p_212834_2_, long p_212834_3_) {
      return p_212834_2_.isBaby() && this.targetBed != null && this.isBed(p_212834_1_, this.targetBed) && !this.tiredOfWalking(p_212834_1_, p_212834_2_) && !this.tiredOfJumping(p_212834_1_, p_212834_2_);
   }

   protected boolean timedOut(long p_220383_1_) {
      return false;
   }

   protected void tick(ServerWorld p_212833_1_, MobEntity p_212833_2_, long p_212833_3_) {
      if (!this.onOrOverBed(p_212833_1_, p_212833_2_)) {
         --this.remainingTimeToReachBed;
      } else if (this.remainingCooldownUntilNextJump > 0) {
         --this.remainingCooldownUntilNextJump;
      } else {
         if (this.onBedSurface(p_212833_1_, p_212833_2_)) {
            p_212833_2_.getJumpControl().jump();
            --this.remainingJumps;
            this.remainingCooldownUntilNextJump = 5;
         }

      }
   }

   private void startWalkingTowardsBed(MobEntity p_220467_1_, BlockPos p_220467_2_) {
      p_220467_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220467_2_, this.speedModifier, 0));
   }

   private boolean nearBed(ServerWorld p_220469_1_, MobEntity p_220469_2_) {
      return this.onOrOverBed(p_220469_1_, p_220469_2_) || this.getNearestBed(p_220469_2_).isPresent();
   }

   private boolean onOrOverBed(ServerWorld p_220468_1_, MobEntity p_220468_2_) {
      BlockPos blockpos = p_220468_2_.blockPosition();
      BlockPos blockpos1 = blockpos.below();
      return this.isBed(p_220468_1_, blockpos) || this.isBed(p_220468_1_, blockpos1);
   }

   private boolean onBedSurface(ServerWorld p_220465_1_, MobEntity p_220465_2_) {
      return this.isBed(p_220465_1_, p_220465_2_.blockPosition());
   }

   private boolean isBed(ServerWorld p_220466_1_, BlockPos p_220466_2_) {
      return p_220466_1_.getBlockState(p_220466_2_).is(BlockTags.BEDS);
   }

   private Optional<BlockPos> getNearestBed(MobEntity p_220463_1_) {
      return p_220463_1_.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
   }

   private boolean tiredOfWalking(ServerWorld p_220464_1_, MobEntity p_220464_2_) {
      return !this.onOrOverBed(p_220464_1_, p_220464_2_) && this.remainingTimeToReachBed <= 0;
   }

   private boolean tiredOfJumping(ServerWorld p_220462_1_, MobEntity p_220462_2_) {
      return this.onOrOverBed(p_220462_1_, p_220462_2_) && this.remainingJumps <= 0;
   }
}
