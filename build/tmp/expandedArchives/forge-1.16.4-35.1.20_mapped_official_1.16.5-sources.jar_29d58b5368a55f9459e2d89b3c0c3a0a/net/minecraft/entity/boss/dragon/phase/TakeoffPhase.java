package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class TakeoffPhase extends Phase {
   private boolean firstTick;
   private Path currentPath;
   private Vector3d targetLocation;

   public TakeoffPhase(EnderDragonEntity p_i46783_1_) {
      super(p_i46783_1_);
   }

   public void doServerTick() {
      if (!this.firstTick && this.currentPath != null) {
         BlockPos blockpos = this.dragon.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         if (!blockpos.closerThan(this.dragon.position(), 10.0D)) {
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
         }
      } else {
         this.firstTick = false;
         this.findNewTarget();
      }

   }

   public void begin() {
      this.firstTick = true;
      this.currentPath = null;
      this.targetLocation = null;
   }

   private void findNewTarget() {
      int i = this.dragon.findClosestNode();
      Vector3d vector3d = this.dragon.getHeadLookVector(1.0F);
      int j = this.dragon.findClosestNode(-vector3d.x * 40.0D, 105.0D, -vector3d.z * 40.0D);
      if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() > 0) {
         j = j % 12;
         if (j < 0) {
            j += 12;
         }
      } else {
         j = j - 12;
         j = j & 7;
         j = j + 12;
      }

      this.currentPath = this.dragon.findPath(i, j, (PathPoint)null);
      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null) {
         this.currentPath.advance();
         if (!this.currentPath.isDone()) {
            Vector3i vector3i = this.currentPath.getNextNodePos();
            this.currentPath.advance();

            double d0;
            do {
               d0 = (double)((float)vector3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
            } while(d0 < (double)vector3i.getY());

            this.targetLocation = new Vector3d((double)vector3i.getX(), d0, (double)vector3i.getZ());
         }
      }

   }

   @Nullable
   public Vector3d getFlyTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<TakeoffPhase> getPhase() {
      return PhaseType.TAKEOFF;
   }
}
