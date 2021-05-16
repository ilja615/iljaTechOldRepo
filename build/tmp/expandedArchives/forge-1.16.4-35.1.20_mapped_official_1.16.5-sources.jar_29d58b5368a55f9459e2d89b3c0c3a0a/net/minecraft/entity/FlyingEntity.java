package net.minecraft.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class FlyingEntity extends MobEntity {
   protected FlyingEntity(EntityType<? extends FlyingEntity> p_i48578_1_, World p_i48578_2_) {
      super(p_i48578_1_, p_i48578_2_);
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   public void travel(Vector3d p_213352_1_) {
      if (this.isInWater()) {
         this.moveRelative(0.02F, p_213352_1_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
      } else if (this.isInLava()) {
         this.moveRelative(0.02F, p_213352_1_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
      } else {
         BlockPos ground = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
         float f = 0.91F;
         if (this.onGround) {
            f = this.level.getBlockState(ground).getSlipperiness(this.level, ground, this) * 0.91F;
         }

         float f1 = 0.16277137F / (f * f * f);
         f = 0.91F;
         if (this.onGround) {
            f = this.level.getBlockState(ground).getSlipperiness(this.level, ground, this) * 0.91F;
         }

         this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, p_213352_1_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale((double)f));
      }

      this.calculateEntityAnimation(this, false);
   }

   public boolean onClimbable() {
      return false;
   }
}
