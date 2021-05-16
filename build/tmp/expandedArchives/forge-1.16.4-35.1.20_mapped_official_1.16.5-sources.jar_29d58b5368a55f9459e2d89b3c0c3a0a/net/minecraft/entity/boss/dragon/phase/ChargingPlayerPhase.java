package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChargingPlayerPhase extends Phase {
   private static final Logger LOGGER = LogManager.getLogger();
   private Vector3d targetLocation;
   private int timeSinceCharge;

   public ChargingPlayerPhase(EnderDragonEntity p_i46793_1_) {
      super(p_i46793_1_);
   }

   public void doServerTick() {
      if (this.targetLocation == null) {
         LOGGER.warn("Aborting charge player as no target was set.");
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else {
         double d0 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            ++this.timeSinceCharge;
         }

      }
   }

   public void begin() {
      this.targetLocation = null;
      this.timeSinceCharge = 0;
   }

   public void setTarget(Vector3d p_188668_1_) {
      this.targetLocation = p_188668_1_;
   }

   public float getFlySpeed() {
      return 3.0F;
   }

   @Nullable
   public Vector3d getFlyTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<ChargingPlayerPhase> getPhase() {
      return PhaseType.CHARGING_PLAYER;
   }
}
