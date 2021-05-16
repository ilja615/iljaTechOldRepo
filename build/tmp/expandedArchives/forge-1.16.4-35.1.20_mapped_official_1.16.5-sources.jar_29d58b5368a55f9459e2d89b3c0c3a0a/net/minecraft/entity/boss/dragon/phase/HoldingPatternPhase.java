package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class HoldingPatternPhase extends Phase {
   private static final EntityPredicate NEW_TARGET_TARGETING = (new EntityPredicate()).range(64.0D);
   private Path currentPath;
   private Vector3d targetLocation;
   private boolean clockwise;

   public HoldingPatternPhase(EnderDragonEntity p_i46791_1_) {
      super(p_i46791_1_);
   }

   public PhaseType<HoldingPatternPhase> getPhase() {
      return PhaseType.HOLDING_PATTERN;
   }

   public void doServerTick() {
      double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
         this.findNewTarget();
      }

   }

   public void begin() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   @Nullable
   public Vector3d getFlyTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath != null && this.currentPath.isDone()) {
         BlockPos blockpos = this.dragon.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
         int i = this.dragon.getDragonFight() == null ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
         if (this.dragon.getRandom().nextInt(i + 3) == 0) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
            return;
         }

         double d0 = 64.0D;
         PlayerEntity playerentity = this.dragon.level.getNearestPlayer(NEW_TARGET_TARGETING, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
         if (playerentity != null) {
            d0 = blockpos.distSqr(playerentity.position(), true) / 512.0D;
         }

         if (playerentity != null && !playerentity.abilities.invulnerable && (this.dragon.getRandom().nextInt(MathHelper.abs((int)d0) + 2) == 0 || this.dragon.getRandom().nextInt(i + 2) == 0)) {
            this.strafePlayer(playerentity);
            return;
         }
      }

      if (this.currentPath == null || this.currentPath.isDone()) {
         int j = this.dragon.findClosestNode();
         int k = j;
         if (this.dragon.getRandom().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
            k = j + 6;
         }

         if (this.clockwise) {
            ++k;
         } else {
            --k;
         }

         if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() >= 0) {
            k = k % 12;
            if (k < 0) {
               k += 12;
            }
         } else {
            k = k - 12;
            k = k & 7;
            k = k + 12;
         }

         this.currentPath = this.dragon.findPath(j, k, (PathPoint)null);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
   }

   private void strafePlayer(PlayerEntity p_188674_1_) {
      this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
      this.dragon.getPhaseManager().getPhase(PhaseType.STRAFE_PLAYER).setTarget(p_188674_1_);
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isDone()) {
         Vector3i vector3i = this.currentPath.getNextNodePos();
         this.currentPath.advance();
         double d0 = (double)vector3i.getX();
         double d1 = (double)vector3i.getZ();

         double d2;
         do {
            d2 = (double)((float)vector3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
         } while(d2 < (double)vector3i.getY());

         this.targetLocation = new Vector3d(d0, d2, d1);
      }

   }

   public void onCrystalDestroyed(EnderCrystalEntity p_188655_1_, BlockPos p_188655_2_, DamageSource p_188655_3_, @Nullable PlayerEntity p_188655_4_) {
      if (p_188655_4_ != null && !p_188655_4_.abilities.invulnerable) {
         this.strafePlayer(p_188655_4_);
      }

   }
}
