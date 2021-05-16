package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;

public class DolphinLookController extends LookController {
   private final int maxYRotFromCenter;

   public DolphinLookController(MobEntity p_i48942_1_, int p_i48942_2_) {
      super(p_i48942_1_);
      this.maxYRotFromCenter = p_i48942_2_;
   }

   public void tick() {
      if (this.hasWanted) {
         this.hasWanted = false;
         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.getYRotD() + 20.0F, this.yMaxRotSpeed);
         this.mob.xRot = this.rotateTowards(this.mob.xRot, this.getXRotD() + 10.0F, this.xMaxRotAngle);
      } else {
         if (this.mob.getNavigation().isDone()) {
            this.mob.xRot = this.rotateTowards(this.mob.xRot, 0.0F, 5.0F);
         }

         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
      }

      float f = MathHelper.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
      if (f < (float)(-this.maxYRotFromCenter)) {
         this.mob.yBodyRot -= 4.0F;
      } else if (f > (float)this.maxYRotFromCenter) {
         this.mob.yBodyRot += 4.0F;
      }

   }
}
