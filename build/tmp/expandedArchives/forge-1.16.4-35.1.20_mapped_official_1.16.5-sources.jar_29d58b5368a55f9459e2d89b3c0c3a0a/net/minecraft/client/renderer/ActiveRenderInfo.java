package net.minecraft.client.renderer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ActiveRenderInfo {
   private boolean initialized;
   private IBlockReader level;
   private Entity entity;
   private Vector3d position = Vector3d.ZERO;
   private final BlockPos.Mutable blockPosition = new BlockPos.Mutable();
   private final Vector3f forwards = new Vector3f(0.0F, 0.0F, 1.0F);
   private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
   private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
   private float xRot;
   private float yRot;
   private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private boolean detached;
   private boolean mirror;
   private float eyeHeight;
   private float eyeHeightOld;

   public void setup(IBlockReader p_216772_1_, Entity p_216772_2_, boolean p_216772_3_, boolean p_216772_4_, float p_216772_5_) {
      this.initialized = true;
      this.level = p_216772_1_;
      this.entity = p_216772_2_;
      this.detached = p_216772_3_;
      this.mirror = p_216772_4_;
      this.setRotation(p_216772_2_.getViewYRot(p_216772_5_), p_216772_2_.getViewXRot(p_216772_5_));
      this.setPosition(MathHelper.lerp((double)p_216772_5_, p_216772_2_.xo, p_216772_2_.getX()), MathHelper.lerp((double)p_216772_5_, p_216772_2_.yo, p_216772_2_.getY()) + (double)MathHelper.lerp(p_216772_5_, this.eyeHeightOld, this.eyeHeight), MathHelper.lerp((double)p_216772_5_, p_216772_2_.zo, p_216772_2_.getZ()));
      if (p_216772_3_) {
         if (p_216772_4_) {
            this.setRotation(this.yRot + 180.0F, -this.xRot);
         }

         this.move(-this.getMaxZoom(4.0D), 0.0D, 0.0D);
      } else if (p_216772_2_ instanceof LivingEntity && ((LivingEntity)p_216772_2_).isSleeping()) {
         Direction direction = ((LivingEntity)p_216772_2_).getBedOrientation();
         this.setRotation(direction != null ? direction.toYRot() - 180.0F : 0.0F, 0.0F);
         this.move(0.0D, 0.3D, 0.0D);
      }

   }

   public void tick() {
      if (this.entity != null) {
         this.eyeHeightOld = this.eyeHeight;
         this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5F;
      }

   }

   private double getMaxZoom(double p_216779_1_) {
      for(int i = 0; i < 8; ++i) {
         float f = (float)((i & 1) * 2 - 1);
         float f1 = (float)((i >> 1 & 1) * 2 - 1);
         float f2 = (float)((i >> 2 & 1) * 2 - 1);
         f = f * 0.1F;
         f1 = f1 * 0.1F;
         f2 = f2 * 0.1F;
         Vector3d vector3d = this.position.add((double)f, (double)f1, (double)f2);
         Vector3d vector3d1 = new Vector3d(this.position.x - (double)this.forwards.x() * p_216779_1_ + (double)f + (double)f2, this.position.y - (double)this.forwards.y() * p_216779_1_ + (double)f1, this.position.z - (double)this.forwards.z() * p_216779_1_ + (double)f2);
         RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, this.entity));
         if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            double d0 = raytraceresult.getLocation().distanceTo(this.position);
            if (d0 < p_216779_1_) {
               p_216779_1_ = d0;
            }
         }
      }

      return p_216779_1_;
   }

   protected void move(double p_216782_1_, double p_216782_3_, double p_216782_5_) {
      double d0 = (double)this.forwards.x() * p_216782_1_ + (double)this.up.x() * p_216782_3_ + (double)this.left.x() * p_216782_5_;
      double d1 = (double)this.forwards.y() * p_216782_1_ + (double)this.up.y() * p_216782_3_ + (double)this.left.y() * p_216782_5_;
      double d2 = (double)this.forwards.z() * p_216782_1_ + (double)this.up.z() * p_216782_3_ + (double)this.left.z() * p_216782_5_;
      this.setPosition(new Vector3d(this.position.x + d0, this.position.y + d1, this.position.z + d2));
   }

   protected void setRotation(float p_216776_1_, float p_216776_2_) {
      this.xRot = p_216776_2_;
      this.yRot = p_216776_1_;
      this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
      this.rotation.mul(Vector3f.YP.rotationDegrees(-p_216776_1_));
      this.rotation.mul(Vector3f.XP.rotationDegrees(p_216776_2_));
      this.forwards.set(0.0F, 0.0F, 1.0F);
      this.forwards.transform(this.rotation);
      this.up.set(0.0F, 1.0F, 0.0F);
      this.up.transform(this.rotation);
      this.left.set(1.0F, 0.0F, 0.0F);
      this.left.transform(this.rotation);
   }

   protected void setPosition(double p_216775_1_, double p_216775_3_, double p_216775_5_) {
      this.setPosition(new Vector3d(p_216775_1_, p_216775_3_, p_216775_5_));
   }

   protected void setPosition(Vector3d p_216774_1_) {
      this.position = p_216774_1_;
      this.blockPosition.set(p_216774_1_.x, p_216774_1_.y, p_216774_1_.z);
   }

   public Vector3d getPosition() {
      return this.position;
   }

   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   public float getXRot() {
      return this.xRot;
   }

   public float getYRot() {
      return this.yRot;
   }

   public Quaternion rotation() {
      return this.rotation;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public boolean isDetached() {
      return this.detached;
   }

   public FluidState getFluidInCamera() {
      if (!this.initialized) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         FluidState fluidstate = this.level.getFluidState(this.blockPosition);
         return !fluidstate.isEmpty() && this.position.y >= (double)((float)this.blockPosition.getY() + fluidstate.getHeight(this.level, this.blockPosition)) ? Fluids.EMPTY.defaultFluidState() : fluidstate;
      }
   }

   public final Vector3f getLookVector() {
      return this.forwards;
   }

   public final Vector3f getUpVector() {
      return this.up;
   }

   public void reset() {
      this.level = null;
      this.entity = null;
      this.initialized = false;
   }

   public void setAnglesInternal(float yaw, float pitch) {
      this.yRot = yaw;
      this.xRot = pitch;
   }

   public net.minecraft.block.BlockState getBlockAtCamera() {
      if (!this.initialized)
         return net.minecraft.block.Blocks.AIR.defaultBlockState();
      else
         return this.level.getBlockState(this.blockPosition).getStateAtViewpoint(this.level, this.blockPosition, this.position);
   }
}
