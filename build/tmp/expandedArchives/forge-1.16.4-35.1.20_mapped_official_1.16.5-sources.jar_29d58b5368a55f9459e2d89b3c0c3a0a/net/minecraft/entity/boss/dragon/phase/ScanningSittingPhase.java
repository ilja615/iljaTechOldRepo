package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class ScanningSittingPhase extends SittingPhase {
   private static final EntityPredicate CHARGE_TARGETING = (new EntityPredicate()).range(150.0D);
   private final EntityPredicate scanTargeting;
   private int scanningTime;

   public ScanningSittingPhase(EnderDragonEntity p_i46785_1_) {
      super(p_i46785_1_);
      this.scanTargeting = (new EntityPredicate()).range(20.0D).selector((p_221114_1_) -> {
         return Math.abs(p_221114_1_.getY() - p_i46785_1_.getY()) <= 10.0D;
      });
   }

   public void doServerTick() {
      ++this.scanningTime;
      LivingEntity livingentity = this.dragon.level.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (livingentity != null) {
         if (this.scanningTime > 25) {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
         } else {
            Vector3d vector3d = (new Vector3d(livingentity.getX() - this.dragon.getX(), 0.0D, livingentity.getZ() - this.dragon.getZ())).normalize();
            Vector3d vector3d1 = (new Vector3d((double)MathHelper.sin(this.dragon.yRot * ((float)Math.PI / 180F)), 0.0D, (double)(-MathHelper.cos(this.dragon.yRot * ((float)Math.PI / 180F))))).normalize();
            float f = (float)vector3d1.dot(vector3d);
            float f1 = (float)(Math.acos((double)f) * (double)(180F / (float)Math.PI)) + 0.5F;
            if (f1 < 0.0F || f1 > 10.0F) {
               double d0 = livingentity.getX() - this.dragon.head.getX();
               double d1 = livingentity.getZ() - this.dragon.head.getZ();
               double d2 = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)this.dragon.yRot), -100.0D, 100.0D);
               this.dragon.yRotA *= 0.8F;
               float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) + 1.0F;
               float f3 = f2;
               if (f2 > 40.0F) {
                  f2 = 40.0F;
               }

               this.dragon.yRotA = (float)((double)this.dragon.yRotA + d2 * (double)(0.7F / f2 / f3));
               this.dragon.yRot += this.dragon.yRotA;
            }
         }
      } else if (this.scanningTime >= 100) {
         livingentity = this.dragon.level.getNearestPlayer(CHARGE_TARGETING, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         if (livingentity != null) {
            this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
            this.dragon.getPhaseManager().getPhase(PhaseType.CHARGING_PLAYER).setTarget(new Vector3d(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
         }
      }

   }

   public void begin() {
      this.scanningTime = 0;
   }

   public PhaseType<ScanningSittingPhase> getPhase() {
      return PhaseType.SITTING_SCANNING;
   }
}
