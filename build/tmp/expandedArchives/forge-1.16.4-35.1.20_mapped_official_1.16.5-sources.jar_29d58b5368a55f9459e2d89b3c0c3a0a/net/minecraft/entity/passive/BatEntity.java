package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BatEntity extends AmbientEntity {
   private static final DataParameter<Byte> DATA_ID_FLAGS = EntityDataManager.defineId(BatEntity.class, DataSerializers.BYTE);
   private static final EntityPredicate BAT_RESTING_TARGETING = (new EntityPredicate()).range(4.0D).allowSameTeam();
   private BlockPos targetPosition;

   public BatEntity(EntityType<? extends BatEntity> p_i50290_1_, World p_i50290_2_) {
      super(p_i50290_1_, p_i50290_2_);
      this.setResting(true);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FLAGS, (byte)0);
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   protected float getVoicePitch() {
      return super.getVoicePitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BAT_DEATH;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity p_82167_1_) {
   }

   protected void pushEntities() {
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D);
   }

   public boolean isResting() {
      return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
   }

   public void setResting(boolean p_82236_1_) {
      byte b0 = this.entityData.get(DATA_ID_FLAGS);
      if (p_82236_1_) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | 1));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & -2));
      }

   }

   public void tick() {
      super.tick();
      if (this.isResting()) {
         this.setDeltaMovement(Vector3d.ZERO);
         this.setPosRaw(this.getX(), (double)MathHelper.floor(this.getY()) + 1.0D - (double)this.getBbHeight(), this.getZ());
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
      }

   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      BlockPos blockpos = this.blockPosition();
      BlockPos blockpos1 = blockpos.above();
      if (this.isResting()) {
         boolean flag = this.isSilent();
         if (this.level.getBlockState(blockpos1).isRedstoneConductor(this.level, blockpos)) {
            if (this.random.nextInt(200) == 0) {
               this.yHeadRot = (float)this.random.nextInt(360);
            }

            if (this.level.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
               this.setResting(false);
               if (!flag) {
                  this.level.levelEvent((PlayerEntity)null, 1025, blockpos, 0);
               }
            }
         } else {
            this.setResting(false);
            if (!flag) {
               this.level.levelEvent((PlayerEntity)null, 1025, blockpos, 0);
            }
         }
      } else {
         if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() < 1)) {
            this.targetPosition = null;
         }

         if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerThan(this.position(), 2.0D)) {
            this.targetPosition = new BlockPos(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0D, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
         }

         double d2 = (double)this.targetPosition.getX() + 0.5D - this.getX();
         double d0 = (double)this.targetPosition.getY() + 0.1D - this.getY();
         double d1 = (double)this.targetPosition.getZ() + 0.5D - this.getZ();
         Vector3d vector3d = this.getDeltaMovement();
         Vector3d vector3d1 = vector3d.add((Math.signum(d2) * 0.5D - vector3d.x) * (double)0.1F, (Math.signum(d0) * (double)0.7F - vector3d.y) * (double)0.1F, (Math.signum(d1) * 0.5D - vector3d.z) * (double)0.1F);
         this.setDeltaMovement(vector3d1);
         float f = (float)(MathHelper.atan2(vector3d1.z, vector3d1.x) * (double)(180F / (float)Math.PI)) - 90.0F;
         float f1 = MathHelper.wrapDegrees(f - this.yRot);
         this.zza = 0.5F;
         this.yRot += f1;
         if (this.random.nextInt(100) == 0 && this.level.getBlockState(blockpos1).isRedstoneConductor(this.level, blockpos1)) {
            this.setResting(true);
         }
      }

   }

   protected boolean isMovementNoisy() {
      return false;
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (!this.level.isClientSide && this.isResting()) {
            this.setResting(false);
         }

         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.entityData.set(DATA_ID_FLAGS, p_70037_1_.getByte("BatFlags"));
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putByte("BatFlags", this.entityData.get(DATA_ID_FLAGS));
   }

   public static boolean checkBatSpawnRules(EntityType<BatEntity> p_223369_0_, IWorld p_223369_1_, SpawnReason p_223369_2_, BlockPos p_223369_3_, Random p_223369_4_) {
      if (p_223369_3_.getY() >= p_223369_1_.getSeaLevel()) {
         return false;
      } else {
         int i = p_223369_1_.getMaxLocalRawBrightness(p_223369_3_);
         int j = 4;
         if (isHalloween()) {
            j = 7;
         } else if (p_223369_4_.nextBoolean()) {
            return false;
         }

         return i > p_223369_4_.nextInt(j) ? false : checkMobSpawnRules(p_223369_0_, p_223369_1_, p_223369_2_, p_223369_3_, p_223369_4_);
      }
   }

   private static boolean isHalloween() {
      LocalDate localdate = LocalDate.now();
      int i = localdate.get(ChronoField.DAY_OF_MONTH);
      int j = localdate.get(ChronoField.MONTH_OF_YEAR);
      return j == 10 && i >= 20 || j == 11 && i <= 3;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height / 2.0F;
   }
}
