package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TameableEntity extends AnimalEntity {
   protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(TameableEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Optional<UUID>> DATA_OWNERUUID_ID = EntityDataManager.defineId(TameableEntity.class, DataSerializers.OPTIONAL_UUID);
   private boolean orderedToSit;

   protected TameableEntity(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
      super(p_i48574_1_, p_i48574_2_);
      this.reassessTameGoals();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (this.getOwnerUUID() != null) {
         p_213281_1_.putUUID("Owner", this.getOwnerUUID());
      }

      p_213281_1_.putBoolean("Sitting", this.orderedToSit);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      UUID uuid;
      if (p_70037_1_.hasUUID("Owner")) {
         uuid = p_70037_1_.getUUID("Owner");
      } else {
         String s = p_70037_1_.getString("Owner");
         uuid = PreYggdrasilConverter.convertMobOwnerIfNecessary(this.getServer(), s);
      }

      if (uuid != null) {
         try {
            this.setOwnerUUID(uuid);
            this.setTame(true);
         } catch (Throwable throwable) {
            this.setTame(false);
         }
      }

      this.orderedToSit = p_70037_1_.getBoolean("Sitting");
      this.setInSittingPose(this.orderedToSit);
   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return !this.isLeashed();
   }

   @OnlyIn(Dist.CLIENT)
   protected void spawnTamingParticles(boolean p_70908_1_) {
      IParticleData iparticledata = ParticleTypes.HEART;
      if (!p_70908_1_) {
         iparticledata = ParticleTypes.SMOKE;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(iparticledata, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 7) {
         this.spawnTamingParticles(true);
      } else if (p_70103_1_ == 6) {
         this.spawnTamingParticles(false);
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public boolean isTame() {
      return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
   }

   public void setTame(boolean p_70903_1_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_70903_1_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 4));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -5));
      }

      this.reassessTameGoals();
   }

   protected void reassessTameGoals() {
   }

   public boolean isInSittingPose() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setInSittingPose(boolean p_233686_1_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_233686_1_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
      }

   }

   @Nullable
   public UUID getOwnerUUID() {
      return this.entityData.get(DATA_OWNERUUID_ID).orElse((UUID)null);
   }

   public void setOwnerUUID(@Nullable UUID p_184754_1_) {
      this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(p_184754_1_));
   }

   public void tame(PlayerEntity p_193101_1_) {
      this.setTame(true);
      this.setOwnerUUID(p_193101_1_.getUUID());
      if (p_193101_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)p_193101_1_, this);
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      try {
         UUID uuid = this.getOwnerUUID();
         return uuid == null ? null : this.level.getPlayerByUUID(uuid);
      } catch (IllegalArgumentException illegalargumentexception) {
         return null;
      }
   }

   public boolean canAttack(LivingEntity p_213336_1_) {
      return this.isOwnedBy(p_213336_1_) ? false : super.canAttack(p_213336_1_);
   }

   public boolean isOwnedBy(LivingEntity p_152114_1_) {
      return p_152114_1_ == this.getOwner();
   }

   public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
      return true;
   }

   public Team getTeam() {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (livingentity != null) {
            return livingentity.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isAlliedTo(Entity p_184191_1_) {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (p_184191_1_ == livingentity) {
            return true;
         }

         if (livingentity != null) {
            return livingentity.isAlliedTo(p_184191_1_);
         }
      }

      return super.isAlliedTo(p_184191_1_);
   }

   public void die(DamageSource p_70645_1_) {
      if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
         this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), Util.NIL_UUID);
      }

      super.die(p_70645_1_);
   }

   public boolean isOrderedToSit() {
      return this.orderedToSit;
   }

   public void setOrderedToSit(boolean p_233687_1_) {
      this.orderedToSit = p_233687_1_;
   }
}
