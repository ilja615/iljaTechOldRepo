package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTEntity extends Entity {
   private static final DataParameter<Integer> DATA_FUSE_ID = EntityDataManager.defineId(TNTEntity.class, DataSerializers.INT);
   @Nullable
   private LivingEntity owner;
   private int life = 80;

   public TNTEntity(EntityType<? extends TNTEntity> p_i50216_1_, World p_i50216_2_) {
      super(p_i50216_1_, p_i50216_2_);
      this.blocksBuilding = true;
   }

   public TNTEntity(World p_i1730_1_, double p_i1730_2_, double p_i1730_4_, double p_i1730_6_, @Nullable LivingEntity p_i1730_8_) {
      this(EntityType.TNT, p_i1730_1_);
      this.setPos(p_i1730_2_, p_i1730_4_, p_i1730_6_);
      double d0 = p_i1730_1_.random.nextDouble() * (double)((float)Math.PI * 2F);
      this.setDeltaMovement(-Math.sin(d0) * 0.02D, (double)0.2F, -Math.cos(d0) * 0.02D);
      this.setFuse(80);
      this.xo = p_i1730_2_;
      this.yo = p_i1730_4_;
      this.zo = p_i1730_6_;
      this.owner = p_i1730_8_;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_FUSE_ID, 80);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   public boolean isPickable() {
      return !this.removed;
   }

   public void tick() {
      if (!this.isNoGravity()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
      if (this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
      }

      --this.life;
      if (this.life <= 0) {
         this.remove();
         if (!this.level.isClientSide) {
            this.explode();
         }
      } else {
         this.updateInWaterStateAndDoFluidPushing();
         if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void explode() {
      float f = 4.0F;
      this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 4.0F, Explosion.Mode.BREAK);
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("Fuse", (short)this.getLife());
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.setFuse(p_70037_1_.getShort("Fuse"));
   }

   @Nullable
   public LivingEntity getOwner() {
      return this.owner;
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return 0.15F;
   }

   public void setFuse(int p_184534_1_) {
      this.entityData.set(DATA_FUSE_ID, p_184534_1_);
      this.life = p_184534_1_;
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_FUSE_ID.equals(p_184206_1_)) {
         this.life = this.getFuse();
      }

   }

   public int getFuse() {
      return this.entityData.get(DATA_FUSE_ID);
   }

   public int getLife() {
      return this.life;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
