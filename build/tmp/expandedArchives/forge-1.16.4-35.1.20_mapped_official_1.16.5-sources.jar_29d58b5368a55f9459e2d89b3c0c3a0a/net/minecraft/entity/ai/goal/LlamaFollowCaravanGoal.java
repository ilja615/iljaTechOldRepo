package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.util.math.vector.Vector3d;

public class LlamaFollowCaravanGoal extends Goal {
   public final LlamaEntity llama;
   private double speedModifier;
   private int distCheckCounter;

   public LlamaFollowCaravanGoal(LlamaEntity p_i47305_1_, double p_i47305_2_) {
      this.llama = p_i47305_1_;
      this.speedModifier = p_i47305_2_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.llama.isLeashed() && !this.llama.inCaravan()) {
         List<Entity> list = this.llama.level.getEntities(this.llama, this.llama.getBoundingBox().inflate(9.0D, 4.0D, 9.0D), (p_220719_0_) -> {
            EntityType<?> entitytype = p_220719_0_.getType();
            return entitytype == EntityType.LLAMA || entitytype == EntityType.TRADER_LLAMA;
         });
         LlamaEntity llamaentity = null;
         double d0 = Double.MAX_VALUE;

         for(Entity entity : list) {
            LlamaEntity llamaentity1 = (LlamaEntity)entity;
            if (llamaentity1.inCaravan() && !llamaentity1.hasCaravanTail()) {
               double d1 = this.llama.distanceToSqr(llamaentity1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  llamaentity = llamaentity1;
               }
            }
         }

         if (llamaentity == null) {
            for(Entity entity1 : list) {
               LlamaEntity llamaentity2 = (LlamaEntity)entity1;
               if (llamaentity2.isLeashed() && !llamaentity2.hasCaravanTail()) {
                  double d2 = this.llama.distanceToSqr(llamaentity2);
                  if (!(d2 > d0)) {
                     d0 = d2;
                     llamaentity = llamaentity2;
                  }
               }
            }
         }

         if (llamaentity == null) {
            return false;
         } else if (d0 < 4.0D) {
            return false;
         } else if (!llamaentity.isLeashed() && !this.firstIsLeashed(llamaentity, 1)) {
            return false;
         } else {
            this.llama.joinCaravan(llamaentity);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
         double d0 = this.llama.distanceToSqr(this.llama.getCaravanHead());
         if (d0 > 676.0D) {
            if (this.speedModifier <= 3.0D) {
               this.speedModifier *= 1.2D;
               this.distCheckCounter = 40;
               return true;
            }

            if (this.distCheckCounter == 0) {
               return false;
            }
         }

         if (this.distCheckCounter > 0) {
            --this.distCheckCounter;
         }

         return true;
      } else {
         return false;
      }
   }

   public void stop() {
      this.llama.leaveCaravan();
      this.speedModifier = 2.1D;
   }

   public void tick() {
      if (this.llama.inCaravan()) {
         if (!(this.llama.getLeashHolder() instanceof LeashKnotEntity)) {
            LlamaEntity llamaentity = this.llama.getCaravanHead();
            double d0 = (double)this.llama.distanceTo(llamaentity);
            float f = 2.0F;
            Vector3d vector3d = (new Vector3d(llamaentity.getX() - this.llama.getX(), llamaentity.getY() - this.llama.getY(), llamaentity.getZ() - this.llama.getZ())).normalize().scale(Math.max(d0 - 2.0D, 0.0D));
            this.llama.getNavigation().moveTo(this.llama.getX() + vector3d.x, this.llama.getY() + vector3d.y, this.llama.getZ() + vector3d.z, this.speedModifier);
         }
      }
   }

   private boolean firstIsLeashed(LlamaEntity p_190858_1_, int p_190858_2_) {
      if (p_190858_2_ > 8) {
         return false;
      } else if (p_190858_1_.inCaravan()) {
         if (p_190858_1_.getCaravanHead().isLeashed()) {
            return true;
         } else {
            LlamaEntity llamaentity = p_190858_1_.getCaravanHead();
            ++p_190858_2_;
            return this.firstIsLeashed(llamaentity, p_190858_2_);
         }
      } else {
         return false;
      }
   }
}
