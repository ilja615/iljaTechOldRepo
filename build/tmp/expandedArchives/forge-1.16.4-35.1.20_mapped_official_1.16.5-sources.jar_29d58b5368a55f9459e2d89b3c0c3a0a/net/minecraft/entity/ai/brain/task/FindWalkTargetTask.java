package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class FindWalkTargetTask extends Task<CreatureEntity> {
   private final float speedModifier;
   private final int maxXyDist;
   private final int maxYDist;

   public FindWalkTargetTask(float p_i50336_1_) {
      this(p_i50336_1_, 10, 7);
   }

   public FindWalkTargetTask(float p_i51526_1_, int p_i51526_2_, int p_i51526_3_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.speedModifier = p_i51526_1_;
      this.maxXyDist = p_i51526_2_;
      this.maxYDist = p_i51526_3_;
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      BlockPos blockpos = p_212831_2_.blockPosition();
      if (p_212831_1_.isVillage(blockpos)) {
         this.setRandomPos(p_212831_2_);
      } else {
         SectionPos sectionpos = SectionPos.of(blockpos);
         SectionPos sectionpos1 = BrainUtil.findSectionClosestToVillage(p_212831_1_, sectionpos, 2);
         if (sectionpos1 != sectionpos) {
            this.setTargetedPos(p_212831_2_, sectionpos1);
         } else {
            this.setRandomPos(p_212831_2_);
         }
      }

   }

   private void setTargetedPos(CreatureEntity p_220594_1_, SectionPos p_220594_2_) {
      Optional<Vector3d> optional = Optional.ofNullable(RandomPositionGenerator.getPosTowards(p_220594_1_, this.maxXyDist, this.maxYDist, Vector3d.atBottomCenterOf(p_220594_2_.center())));
      p_220594_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220596_1_) -> {
         return new WalkTarget(p_220596_1_, this.speedModifier, 0);
      }));
   }

   private void setRandomPos(CreatureEntity p_220593_1_) {
      Optional<Vector3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(p_220593_1_, this.maxXyDist, this.maxYDist));
      p_220593_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220595_1_) -> {
         return new WalkTarget(p_220595_1_, this.speedModifier, 0);
      }));
   }
}
