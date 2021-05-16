package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatLieOnBedGoal extends MoveToBlockGoal {
   private final CatEntity cat;

   public CatLieOnBedGoal(CatEntity p_i50331_1_, double p_i50331_2_, int p_i50331_4_) {
      super(p_i50331_1_, p_i50331_2_, p_i50331_4_, 6);
      this.cat = p_i50331_1_;
      this.verticalSearchStart = -2;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isOrderedToSit() && !this.cat.isLying() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   protected int nextStartTick(CreatureEntity p_203109_1_) {
      return 40;
   }

   public void stop() {
      super.stop();
      this.cat.setLying(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(false);
      if (!this.isReachedTarget()) {
         this.cat.setLying(false);
      } else if (!this.cat.isLying()) {
         this.cat.setLying(true);
      }

   }

   protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
      return p_179488_1_.isEmptyBlock(p_179488_2_.above()) && p_179488_1_.getBlockState(p_179488_2_).getBlock().is(BlockTags.BEDS);
   }
}
