package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class ReturnToVillageGoal extends RandomWalkingGoal {
   public ReturnToVillageGoal(CreatureEntity p_i231548_1_, double p_i231548_2_, boolean p_i231548_4_) {
      super(p_i231548_1_, p_i231548_2_, 10, p_i231548_4_);
   }

   public boolean canUse() {
      ServerWorld serverworld = (ServerWorld)this.mob.level;
      BlockPos blockpos = this.mob.blockPosition();
      return serverworld.isVillage(blockpos) ? false : super.canUse();
   }

   @Nullable
   protected Vector3d getPosition() {
      ServerWorld serverworld = (ServerWorld)this.mob.level;
      BlockPos blockpos = this.mob.blockPosition();
      SectionPos sectionpos = SectionPos.of(blockpos);
      SectionPos sectionpos1 = BrainUtil.findSectionClosestToVillage(serverworld, sectionpos, 2);
      return sectionpos1 != sectionpos ? RandomPositionGenerator.getPosTowards(this.mob, 10, 7, Vector3d.atBottomCenterOf(sectionpos1.center())) : null;
   }
}
