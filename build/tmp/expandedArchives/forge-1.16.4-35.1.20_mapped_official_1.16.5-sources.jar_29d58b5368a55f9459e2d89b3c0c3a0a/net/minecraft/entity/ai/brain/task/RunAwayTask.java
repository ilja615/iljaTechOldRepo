package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class RunAwayTask<T> extends Task<CreatureEntity> {
   private final MemoryModuleType<T> walkAwayFromMemory;
   private final float speedModifier;
   private final int desiredDistance;
   private final Function<T, Vector3d> toPosition;

   public RunAwayTask(MemoryModuleType<T> p_i231533_1_, float p_i231533_2_, int p_i231533_3_, boolean p_i231533_4_, Function<T, Vector3d> p_i231533_5_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, p_i231533_4_ ? MemoryModuleStatus.REGISTERED : MemoryModuleStatus.VALUE_ABSENT, p_i231533_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.walkAwayFromMemory = p_i231533_1_;
      this.speedModifier = p_i231533_2_;
      this.desiredDistance = p_i231533_3_;
      this.toPosition = p_i231533_5_;
   }

   public static RunAwayTask<BlockPos> pos(MemoryModuleType<BlockPos> p_233963_0_, float p_233963_1_, int p_233963_2_, boolean p_233963_3_) {
      return new RunAwayTask<>(p_233963_0_, p_233963_1_, p_233963_2_, p_233963_3_, Vector3d::atBottomCenterOf);
   }

   public static RunAwayTask<? extends Entity> entity(MemoryModuleType<? extends Entity> p_233965_0_, float p_233965_1_, int p_233965_2_, boolean p_233965_3_) {
      return new RunAwayTask<>(p_233965_0_, p_233965_1_, p_233965_2_, p_233965_3_, Entity::position);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      return this.alreadyWalkingAwayFromPosWithSameSpeed(p_212832_2_) ? false : p_212832_2_.position().closerThan(this.getPosToAvoid(p_212832_2_), (double)this.desiredDistance);
   }

   private Vector3d getPosToAvoid(CreatureEntity p_233961_1_) {
      return this.toPosition.apply(p_233961_1_.getBrain().getMemory(this.walkAwayFromMemory).get());
   }

   private boolean alreadyWalkingAwayFromPosWithSameSpeed(CreatureEntity p_233964_1_) {
      if (!p_233964_1_.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
         return false;
      } else {
         WalkTarget walktarget = p_233964_1_.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get();
         if (walktarget.getSpeedModifier() != this.speedModifier) {
            return false;
         } else {
            Vector3d vector3d = walktarget.getTarget().currentPosition().subtract(p_233964_1_.position());
            Vector3d vector3d1 = this.getPosToAvoid(p_233964_1_).subtract(p_233964_1_.position());
            return vector3d.dot(vector3d1) < 0.0D;
         }
      }
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      moveAwayFrom(p_212831_2_, this.getPosToAvoid(p_212831_2_), this.speedModifier);
   }

   private static void moveAwayFrom(CreatureEntity p_233962_0_, Vector3d p_233962_1_, float p_233962_2_) {
      for(int i = 0; i < 10; ++i) {
         Vector3d vector3d = RandomPositionGenerator.getLandPosAvoid(p_233962_0_, 16, 7, p_233962_1_);
         if (vector3d != null) {
            p_233962_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vector3d, p_233962_2_, 0));
            return;
         }
      }

   }
}
