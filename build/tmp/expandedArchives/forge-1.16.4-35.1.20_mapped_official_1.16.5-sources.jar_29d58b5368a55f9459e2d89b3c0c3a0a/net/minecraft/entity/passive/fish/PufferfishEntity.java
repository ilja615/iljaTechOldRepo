package net.minecraft.entity.passive.fish;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity extends AbstractFishEntity {
   private static final DataParameter<Integer> PUFF_STATE = EntityDataManager.defineId(PufferfishEntity.class, DataSerializers.INT);
   private int inflateCounter;
   private int deflateTimer;
   private static final Predicate<LivingEntity> NO_SPECTATORS_AND_NO_WATER_MOB = (p_210139_0_) -> {
      if (p_210139_0_ == null) {
         return false;
      } else if (!(p_210139_0_ instanceof PlayerEntity) || !p_210139_0_.isSpectator() && !((PlayerEntity)p_210139_0_).isCreative()) {
         return p_210139_0_.getMobType() != CreatureAttribute.WATER;
      } else {
         return false;
      }
   };

   public PufferfishEntity(EntityType<? extends PufferfishEntity> p_i50248_1_, World p_i50248_2_) {
      super(p_i50248_1_, p_i50248_2_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return this.entityData.get(PUFF_STATE);
   }

   public void setPuffState(int p_203714_1_) {
      this.entityData.set(PUFF_STATE, p_203714_1_);
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (PUFF_STATE.equals(p_184206_1_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("PuffState", this.getPuffState());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setPuffState(p_70037_1_.getInt("PuffState"));
   }

   protected ItemStack getBucketItemStack() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PufferfishEntity.PuffGoal(this));
   }

   public void tick() {
      if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
         if (this.inflateCounter > 0) {
            if (this.getPuffState() == 0) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(1);
            } else if (this.inflateCounter > 40 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(2);
            }

            ++this.inflateCounter;
         } else if (this.getPuffState() != 0) {
            if (this.deflateTimer > 60 && this.getPuffState() == 2) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(1);
            } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(0);
            }

            ++this.deflateTimer;
         }
      }

      super.tick();
   }

   public void aiStep() {
      super.aiStep();
      if (this.isAlive() && this.getPuffState() > 0) {
         for(MobEntity mobentity : this.level.getEntitiesOfClass(MobEntity.class, this.getBoundingBox().inflate(0.3D), NO_SPECTATORS_AND_NO_WATER_MOB)) {
            if (mobentity.isAlive()) {
               this.touch(mobentity);
            }
         }
      }

   }

   private void touch(MobEntity p_205719_1_) {
      int i = this.getPuffState();
      if (p_205719_1_.hurt(DamageSource.mobAttack(this), (float)(1 + i))) {
         p_205719_1_.addEffect(new EffectInstance(Effects.POISON, 60 * i, 0));
         this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void playerTouch(PlayerEntity p_70100_1_) {
      int i = this.getPuffState();
      if (p_70100_1_ instanceof ServerPlayerEntity && i > 0 && p_70100_1_.hurt(DamageSource.mobAttack(this), (float)(1 + i))) {
         if (!this.isSilent()) {
            ((ServerPlayerEntity)p_70100_1_).connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.PUFFER_FISH_STING, 0.0F));
         }

         p_70100_1_.addEffect(new EffectInstance(Effects.POISON, 60 * i, 0));
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PUFFER_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.PUFFER_FISH_FLOP;
   }

   public EntitySize getDimensions(Pose p_213305_1_) {
      return super.getDimensions(p_213305_1_).scale(getScale(this.getPuffState()));
   }

   private static float getScale(int p_213806_0_) {
      switch(p_213806_0_) {
      case 0:
         return 0.5F;
      case 1:
         return 0.7F;
      default:
         return 1.0F;
      }
   }

   static class PuffGoal extends Goal {
      private final PufferfishEntity fish;

      public PuffGoal(PufferfishEntity p_i48861_1_) {
         this.fish = p_i48861_1_;
      }

      public boolean canUse() {
         List<LivingEntity> list = this.fish.level.getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0D), PufferfishEntity.NO_SPECTATORS_AND_NO_WATER_MOB);
         return !list.isEmpty();
      }

      public void start() {
         this.fish.inflateCounter = 1;
         this.fish.deflateTimer = 0;
      }

      public void stop() {
         this.fish.inflateCounter = 0;
      }

      public boolean canContinueToUse() {
         List<LivingEntity> list = this.fish.level.getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0D), PufferfishEntity.NO_SPECTATORS_AND_NO_WATER_MOB);
         return !list.isEmpty();
      }
   }
}
