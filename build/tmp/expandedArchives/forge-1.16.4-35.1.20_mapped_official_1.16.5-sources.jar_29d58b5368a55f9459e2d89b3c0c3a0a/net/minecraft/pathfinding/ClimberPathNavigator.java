package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClimberPathNavigator extends GroundPathNavigator {
   private BlockPos pathToPosition;

   public ClimberPathNavigator(MobEntity p_i45874_1_, World p_i45874_2_) {
      super(p_i45874_1_, p_i45874_2_);
   }

   public Path createPath(BlockPos p_179680_1_, int p_179680_2_) {
      this.pathToPosition = p_179680_1_;
      return super.createPath(p_179680_1_, p_179680_2_);
   }

   public Path createPath(Entity p_75494_1_, int p_75494_2_) {
      this.pathToPosition = p_75494_1_.blockPosition();
      return super.createPath(p_75494_1_, p_75494_2_);
   }

   public boolean moveTo(Entity p_75497_1_, double p_75497_2_) {
      Path path = this.createPath(p_75497_1_, 0);
      if (path != null) {
         return this.moveTo(path, p_75497_2_);
      } else {
         this.pathToPosition = p_75497_1_.blockPosition();
         this.speedModifier = p_75497_2_;
         return true;
      }
   }

   public void tick() {
      if (!this.isDone()) {
         super.tick();
      } else {
         if (this.pathToPosition != null) {
            // FORGE: Fix MC-94054
            if (!this.pathToPosition.closerThan(this.mob.position(), Math.max((double)this.mob.getBbWidth(), 1.0D)) && (!(this.mob.getY() > (double)this.pathToPosition.getY()) || !(new BlockPos((double)this.pathToPosition.getX(), this.mob.getY(), (double)this.pathToPosition.getZ())).closerThan(this.mob.position(), Math.max((double)this.mob.getBbWidth(), 1.0D)))) {
               this.mob.getMoveControl().setWantedPosition((double)this.pathToPosition.getX(), (double)this.pathToPosition.getY(), (double)this.pathToPosition.getZ(), this.speedModifier);
            } else {
               this.pathToPosition = null;
            }
         }

      }
   }
}
