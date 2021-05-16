package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ThrowableEntity extends ProjectileEntity {
   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48540_1_, World p_i48540_2_) {
      super(p_i48540_1_, p_i48540_2_);
   }

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48541_1_, double p_i48541_2_, double p_i48541_4_, double p_i48541_6_, World p_i48541_8_) {
      this(p_i48541_1_, p_i48541_8_);
      this.setPos(p_i48541_2_, p_i48541_4_, p_i48541_6_);
   }

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48542_1_, LivingEntity p_i48542_2_, World p_i48542_3_) {
      this(p_i48542_1_, p_i48542_2_.getX(), p_i48542_2_.getEyeY() - (double)0.1F, p_i48542_2_.getZ(), p_i48542_3_);
      this.setOwner(p_i48542_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return p_70112_1_ < d0 * d0;
   }

   public void tick() {
      super.tick();
      RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
      boolean flag = false;
      if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getBlockPos();
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (blockstate.is(Blocks.NETHER_PORTAL)) {
            this.handleInsidePortal(blockpos);
            flag = true;
         } else if (blockstate.is(Blocks.END_GATEWAY)) {
            TileEntity tileentity = this.level.getBlockEntity(blockpos);
            if (tileentity instanceof EndGatewayTileEntity && EndGatewayTileEntity.canEntityTeleport(this)) {
               ((EndGatewayTileEntity)tileentity).teleportEntity(this);
            }

            flag = true;
         }
      }

      if (raytraceresult.getType() != RayTraceResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
         this.onHit(raytraceresult);
      }

      this.checkInsideBlocks();
      Vector3d vector3d = this.getDeltaMovement();
      double d2 = this.getX() + vector3d.x;
      double d0 = this.getY() + vector3d.y;
      double d1 = this.getZ() + vector3d.z;
      this.updateRotation();
      float f;
      if (this.isInWater()) {
         for(int i = 0; i < 4; ++i) {
            float f1 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, d2 - vector3d.x * 0.25D, d0 - vector3d.y * 0.25D, d1 - vector3d.z * 0.25D, vector3d.x, vector3d.y, vector3d.z);
         }

         f = 0.8F;
      } else {
         f = 0.99F;
      }

      this.setDeltaMovement(vector3d.scale((double)f));
      if (!this.isNoGravity()) {
         Vector3d vector3d1 = this.getDeltaMovement();
         this.setDeltaMovement(vector3d1.x, vector3d1.y - (double)this.getGravity(), vector3d1.z);
      }

      this.setPos(d2, d0, d1);
   }

   protected float getGravity() {
      return 0.03F;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
