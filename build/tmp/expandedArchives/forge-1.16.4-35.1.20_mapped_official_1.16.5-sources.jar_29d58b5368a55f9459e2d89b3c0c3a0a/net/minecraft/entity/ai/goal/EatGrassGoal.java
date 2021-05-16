package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EatGrassGoal extends Goal {
   private static final Predicate<BlockState> IS_TALL_GRASS = BlockStateMatcher.forBlock(Blocks.GRASS);
   private final MobEntity mob;
   private final World level;
   private int eatAnimationTick;

   public EatGrassGoal(MobEntity p_i45314_1_) {
      this.mob = p_i45314_1_;
      this.level = p_i45314_1_.level;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
   }

   public boolean canUse() {
      if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
         return false;
      } else {
         BlockPos blockpos = this.mob.blockPosition();
         if (IS_TALL_GRASS.test(this.level.getBlockState(blockpos))) {
            return true;
         } else {
            return this.level.getBlockState(blockpos.below()).is(Blocks.GRASS_BLOCK);
         }
      }
   }

   public void start() {
      this.eatAnimationTick = 40;
      this.level.broadcastEntityEvent(this.mob, (byte)10);
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.eatAnimationTick = 0;
   }

   public boolean canContinueToUse() {
      return this.eatAnimationTick > 0;
   }

   public int getEatAnimationTick() {
      return this.eatAnimationTick;
   }

   public void tick() {
      this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      if (this.eatAnimationTick == 4) {
         BlockPos blockpos = this.mob.blockPosition();
         if (IS_TALL_GRASS.test(this.level.getBlockState(blockpos))) {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.mob)) {
               this.level.destroyBlock(blockpos, false);
            }

            this.mob.ate();
         } else {
            BlockPos blockpos1 = blockpos.below();
            if (this.level.getBlockState(blockpos1).is(Blocks.GRASS_BLOCK)) {
               if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.mob)) {
                  this.level.levelEvent(2001, blockpos1, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                  this.level.setBlock(blockpos1, Blocks.DIRT.defaultBlockState(), 2);
               }

               this.mob.ate();
            }
         }

      }
   }
}
