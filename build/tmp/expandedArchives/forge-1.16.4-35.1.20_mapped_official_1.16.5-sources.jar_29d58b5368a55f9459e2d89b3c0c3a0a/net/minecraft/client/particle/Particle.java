package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Particle {
   private static final AxisAlignedBB INITIAL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   protected final ClientWorld level;
   protected double xo;
   protected double yo;
   protected double zo;
   protected double x;
   protected double y;
   protected double z;
   protected double xd;
   protected double yd;
   protected double zd;
   private AxisAlignedBB bb = INITIAL_AABB;
   protected boolean onGround;
   protected boolean hasPhysics = true;
   private boolean stoppedByCollision;
   protected boolean removed;
   protected float bbWidth = 0.6F;
   protected float bbHeight = 1.8F;
   protected final Random random = new Random();
   protected int age;
   protected int lifetime;
   protected float gravity;
   protected float rCol = 1.0F;
   protected float gCol = 1.0F;
   protected float bCol = 1.0F;
   protected float alpha = 1.0F;
   protected float roll;
   protected float oRoll;

   protected Particle(ClientWorld p_i232411_1_, double p_i232411_2_, double p_i232411_4_, double p_i232411_6_) {
      this.level = p_i232411_1_;
      this.setSize(0.2F, 0.2F);
      this.setPos(p_i232411_2_, p_i232411_4_, p_i232411_6_);
      this.xo = p_i232411_2_;
      this.yo = p_i232411_4_;
      this.zo = p_i232411_6_;
      this.lifetime = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
   }

   public Particle(ClientWorld p_i232412_1_, double p_i232412_2_, double p_i232412_4_, double p_i232412_6_, double p_i232412_8_, double p_i232412_10_, double p_i232412_12_) {
      this(p_i232412_1_, p_i232412_2_, p_i232412_4_, p_i232412_6_);
      this.xd = p_i232412_8_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.yd = p_i232412_10_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.zd = p_i232412_12_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float f1 = MathHelper.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
      this.xd = this.xd / (double)f1 * (double)f * (double)0.4F;
      this.yd = this.yd / (double)f1 * (double)f * (double)0.4F + (double)0.1F;
      this.zd = this.zd / (double)f1 * (double)f * (double)0.4F;
   }

   public Particle setPower(float p_70543_1_) {
      this.xd *= (double)p_70543_1_;
      this.yd = (this.yd - (double)0.1F) * (double)p_70543_1_ + (double)0.1F;
      this.zd *= (double)p_70543_1_;
      return this;
   }

   public Particle scale(float p_70541_1_) {
      this.setSize(0.2F * p_70541_1_, 0.2F * p_70541_1_);
      return this;
   }

   public void setColor(float p_70538_1_, float p_70538_2_, float p_70538_3_) {
      this.rCol = p_70538_1_;
      this.gCol = p_70538_2_;
      this.bCol = p_70538_3_;
   }

   protected void setAlpha(float p_82338_1_) {
      this.alpha = p_82338_1_;
   }

   public void setLifetime(int p_187114_1_) {
      this.lifetime = p_187114_1_;
   }

   public int getLifetime() {
      return this.lifetime;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= 0.04D * (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.98F;
         this.yd *= (double)0.98F;
         this.zd *= (double)0.98F;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   public abstract void render(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_);

   public abstract IParticleRenderType getRenderType();

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
   }

   public void remove() {
      this.removed = true;
   }

   protected void setSize(float p_187115_1_, float p_187115_2_) {
      if (p_187115_1_ != this.bbWidth || p_187115_2_ != this.bbHeight) {
         this.bbWidth = p_187115_1_;
         this.bbHeight = p_187115_2_;
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double)p_187115_1_) / 2.0D;
         double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double)p_187115_1_) / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double)this.bbWidth, axisalignedbb.minY + (double)this.bbHeight, d1 + (double)this.bbWidth));
      }

   }

   public void setPos(double p_187109_1_, double p_187109_3_, double p_187109_5_) {
      this.x = p_187109_1_;
      this.y = p_187109_3_;
      this.z = p_187109_5_;
      float f = this.bbWidth / 2.0F;
      float f1 = this.bbHeight;
      this.setBoundingBox(new AxisAlignedBB(p_187109_1_ - (double)f, p_187109_3_, p_187109_5_ - (double)f, p_187109_1_ + (double)f, p_187109_3_ + (double)f1, p_187109_5_ + (double)f));
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      if (!this.stoppedByCollision) {
         double d0 = p_187110_1_;
         double d1 = p_187110_3_;
         double d2 = p_187110_5_;
         if (this.hasPhysics && (p_187110_1_ != 0.0D || p_187110_3_ != 0.0D || p_187110_5_ != 0.0D)) {
            Vector3d vector3d = Entity.collideBoundingBoxHeuristically((Entity)null, new Vector3d(p_187110_1_, p_187110_3_, p_187110_5_), this.getBoundingBox(), this.level, ISelectionContext.empty(), new ReuseableStream<>(Stream.empty()));
            p_187110_1_ = vector3d.x;
            p_187110_3_ = vector3d.y;
            p_187110_5_ = vector3d.z;
         }

         if (p_187110_1_ != 0.0D || p_187110_3_ != 0.0D || p_187110_5_ != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().move(p_187110_1_, p_187110_3_, p_187110_5_));
            this.setLocationFromBoundingbox();
         }

         if (Math.abs(d1) >= (double)1.0E-5F && Math.abs(p_187110_3_) < (double)1.0E-5F) {
            this.stoppedByCollision = true;
         }

         this.onGround = d1 != p_187110_3_ && d1 < 0.0D;
         if (d0 != p_187110_1_) {
            this.xd = 0.0D;
         }

         if (d2 != p_187110_5_) {
            this.zd = 0.0D;
         }

      }
   }

   protected void setLocationFromBoundingbox() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.x = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
      this.y = axisalignedbb.minY;
      this.z = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
   }

   protected int getLightColor(float p_189214_1_) {
      BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
      return this.level.hasChunkAt(blockpos) ? WorldRenderer.getLightColor(this.level, blockpos) : 0;
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.bb;
   }

   public void setBoundingBox(AxisAlignedBB p_187108_1_) {
      this.bb = p_187108_1_;
   }

   /**
    * Forge added method that controls if a particle should be culled to it's bounding box.
    * Default behaviour is culling enabled
    */
   public boolean shouldCull() {
      return true;
   }
}
