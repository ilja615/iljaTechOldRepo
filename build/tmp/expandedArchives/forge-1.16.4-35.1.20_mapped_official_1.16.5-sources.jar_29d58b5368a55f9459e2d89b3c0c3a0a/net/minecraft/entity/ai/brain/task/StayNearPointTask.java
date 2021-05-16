package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class StayNearPointTask extends Task<VillagerEntity> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final float speedModifier;
   private final int closeEnoughDist;
   private final int tooFarDistance;
   private final int tooLongUnreachableDuration;

   public StayNearPointTask(MemoryModuleType<GlobalPos> p_i51501_1_, float p_i51501_2_, int p_i51501_3_, int p_i51501_4_, int p_i51501_5_) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, p_i51501_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.memoryType = p_i51501_1_;
      this.speedModifier = p_i51501_2_;
      this.closeEnoughDist = p_i51501_3_;
      this.tooFarDistance = p_i51501_4_;
      this.tooLongUnreachableDuration = p_i51501_5_;
   }

   private void dropPOI(VillagerEntity p_225457_1_, long p_225457_2_) {
      Brain<?> brain = p_225457_1_.getBrain();
      p_225457_1_.releasePoi(this.memoryType);
      brain.eraseMemory(this.memoryType);
      brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, p_225457_2_);
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      brain.getMemory(this.memoryType).ifPresent((p_220545_6_) -> {
         if (!this.wrongDimension(p_212831_1_, p_220545_6_) && !this.tiredOfTryingToFindTarget(p_212831_1_, p_212831_2_)) {
            if (this.tooFar(p_212831_2_, p_220545_6_)) {
               Vector3d vector3d = null;
               int i = 0;

               for(int j = 1000; i < 1000 && (vector3d == null || this.tooFar(p_212831_2_, GlobalPos.of(p_212831_1_.dimension(), new BlockPos(vector3d)))); ++i) {
                  vector3d = RandomPositionGenerator.getPosTowards(p_212831_2_, 15, 7, Vector3d.atBottomCenterOf(p_220545_6_.pos()));
               }

               if (i == 1000) {
                  this.dropPOI(p_212831_2_, p_212831_3_);
                  return;
               }

               brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vector3d, this.speedModifier, this.closeEnoughDist));
            } else if (!this.closeEnough(p_212831_1_, p_212831_2_, p_220545_6_)) {
               brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220545_6_.pos(), this.speedModifier, this.closeEnoughDist));
            }
         } else {
            this.dropPOI(p_212831_2_, p_212831_3_);
         }

      });
   }

   private boolean tiredOfTryingToFindTarget(ServerWorld p_223017_1_, VillagerEntity p_223017_2_) {
      Optional<Long> optional = p_223017_2_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (optional.isPresent()) {
         return p_223017_1_.getGameTime() - optional.get() > (long)this.tooLongUnreachableDuration;
      } else {
         return false;
      }
   }

   private boolean tooFar(VillagerEntity p_242304_1_, GlobalPos p_242304_2_) {
      return p_242304_2_.pos().distManhattan(p_242304_1_.blockPosition()) > this.tooFarDistance;
   }

   private boolean wrongDimension(ServerWorld p_242303_1_, GlobalPos p_242303_2_) {
      return p_242303_2_.dimension() != p_242303_1_.dimension();
   }

   private boolean closeEnough(ServerWorld p_220547_1_, VillagerEntity p_220547_2_, GlobalPos p_220547_3_) {
      return p_220547_3_.dimension() == p_220547_1_.dimension() && p_220547_3_.pos().distManhattan(p_220547_2_.blockPosition()) <= this.closeEnoughDist;
   }
}
