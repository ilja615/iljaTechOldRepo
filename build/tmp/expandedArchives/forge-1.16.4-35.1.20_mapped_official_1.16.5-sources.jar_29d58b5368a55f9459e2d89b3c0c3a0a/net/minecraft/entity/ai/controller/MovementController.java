package net.minecraft.entity.ai.controller;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;

public class MovementController {
   protected final MobEntity mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected double speedModifier;
   protected float strafeForwards;
   protected float strafeRight;
   protected MovementController.Action operation = MovementController.Action.WAIT;

   public MovementController(MobEntity p_i1614_1_) {
      this.mob = p_i1614_1_;
   }

   public boolean hasWanted() {
      return this.operation == MovementController.Action.MOVE_TO;
   }

   public double getSpeedModifier() {
      return this.speedModifier;
   }

   public void setWantedPosition(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
      this.wantedX = p_75642_1_;
      this.wantedY = p_75642_3_;
      this.wantedZ = p_75642_5_;
      this.speedModifier = p_75642_7_;
      if (this.operation != MovementController.Action.JUMPING) {
         this.operation = MovementController.Action.MOVE_TO;
      }

   }

   public void strafe(float p_188488_1_, float p_188488_2_) {
      this.operation = MovementController.Action.STRAFE;
      this.strafeForwards = p_188488_1_;
      this.strafeRight = p_188488_2_;
      this.speedModifier = 0.25D;
   }

   public void tick() {
      if (this.operation == MovementController.Action.STRAFE) {
         float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
         float f1 = (float)this.speedModifier * f;
         float f2 = this.strafeForwards;
         float f3 = this.strafeRight;
         float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
         if (f4 < 1.0F) {
            f4 = 1.0F;
         }

         f4 = f1 / f4;
         f2 = f2 * f4;
         f3 = f3 * f4;
         float f5 = MathHelper.sin(this.mob.yRot * ((float)Math.PI / 180F));
         float f6 = MathHelper.cos(this.mob.yRot * ((float)Math.PI / 180F));
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f6 + f2 * f5;
         if (!this.isWalkable(f7, f8)) {
            this.strafeForwards = 1.0F;
            this.strafeRight = 0.0F;
         }

         this.mob.setSpeed(f1);
         this.mob.setZza(this.strafeForwards);
         this.mob.setXxa(this.strafeRight);
         this.operation = MovementController.Action.WAIT;
      } else if (this.operation == MovementController.Action.MOVE_TO) {
         this.operation = MovementController.Action.WAIT;
         double d0 = this.wantedX - this.mob.getX();
         double d1 = this.wantedZ - this.mob.getZ();
         double d2 = this.wantedY - this.mob.getY();
         double d3 = d0 * d0 + d2 * d2 + d1 * d1;
         if (d3 < (double)2.5000003E-7F) {
            this.mob.setZza(0.0F);
            return;
         }

         float f9 = (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         this.mob.yRot = this.rotlerp(this.mob.yRot, f9, 90.0F);
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         BlockPos blockpos = this.mob.blockPosition();
         BlockState blockstate = this.mob.level.getBlockState(blockpos);
         Block block = blockstate.getBlock();
         VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level, blockpos);
         if (d2 > (double)this.mob.maxUpStep && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !block.is(BlockTags.DOORS) && !block.is(BlockTags.FENCES)) {
            this.mob.getJumpControl().jump();
            this.operation = MovementController.Action.JUMPING;
         }
      } else if (this.operation == MovementController.Action.JUMPING) {
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         if (this.mob.isOnGround()) {
            this.operation = MovementController.Action.WAIT;
         }
      } else {
         this.mob.setZza(0.0F);
      }

   }

   private boolean isWalkable(float p_234024_1_, float p_234024_2_) {
      PathNavigator pathnavigator = this.mob.getNavigation();
      if (pathnavigator != null) {
         NodeProcessor nodeprocessor = pathnavigator.getNodeEvaluator();
         if (nodeprocessor != null && nodeprocessor.getBlockPathType(this.mob.level, MathHelper.floor(this.mob.getX() + (double)p_234024_1_), MathHelper.floor(this.mob.getY()), MathHelper.floor(this.mob.getZ() + (double)p_234024_2_)) != PathNodeType.WALKABLE) {
            return false;
         }
      }

      return true;
   }

   protected float rotlerp(float p_75639_1_, float p_75639_2_, float p_75639_3_) {
      float f = MathHelper.wrapDegrees(p_75639_2_ - p_75639_1_);
      if (f > p_75639_3_) {
         f = p_75639_3_;
      }

      if (f < -p_75639_3_) {
         f = -p_75639_3_;
      }

      float f1 = p_75639_1_ + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   public double getWantedX() {
      return this.wantedX;
   }

   public double getWantedY() {
      return this.wantedY;
   }

   public double getWantedZ() {
      return this.wantedZ;
   }

   public static enum Action {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;
   }
}
