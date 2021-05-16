package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AgeableEntity extends CreatureEntity {
   private static final DataParameter<Boolean> DATA_BABY_ID = EntityDataManager.defineId(AgeableEntity.class, DataSerializers.BOOLEAN);
   protected int age;
   protected int forcedAge;
   protected int forcedAgeTimer;

   protected AgeableEntity(EntityType<? extends AgeableEntity> p_i48581_1_, World p_i48581_2_) {
      super(p_i48581_1_, p_i48581_2_);
   }

   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData(true);
      }

      AgeableEntity.AgeableData ageableentity$ageabledata = (AgeableEntity.AgeableData)p_213386_4_;
      if (ageableentity$ageabledata.isShouldSpawnBaby() && ageableentity$ageabledata.getGroupSize() > 0 && this.random.nextFloat() <= ageableentity$ageabledata.getBabySpawnChance()) {
         this.setAge(-24000);
      }

      ageableentity$ageabledata.increaseGroupSizeByOne();
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   @Nullable
   public abstract AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_);

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BABY_ID, false);
   }

   public boolean canBreed() {
      return false;
   }

   public int getAge() {
      if (this.level.isClientSide) {
         return this.entityData.get(DATA_BABY_ID) ? -1 : 1;
      } else {
         return this.age;
      }
   }

   public void ageUp(int p_175501_1_, boolean p_175501_2_) {
      int i = this.getAge();
      i = i + p_175501_1_ * 20;
      if (i > 0) {
         i = 0;
      }

      int j = i - i;
      this.setAge(i);
      if (p_175501_2_) {
         this.forcedAge += j;
         if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if (this.getAge() == 0) {
         this.setAge(this.forcedAge);
      }

   }

   public void ageUp(int p_110195_1_) {
      this.ageUp(p_110195_1_, false);
   }

   public void setAge(int p_70873_1_) {
      int i = this.age;
      this.age = p_70873_1_;
      if (i < 0 && p_70873_1_ >= 0 || i >= 0 && p_70873_1_ < 0) {
         this.entityData.set(DATA_BABY_ID, p_70873_1_ < 0);
         this.ageBoundaryReached();
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Age", this.getAge());
      p_213281_1_.putInt("ForcedAge", this.forcedAge);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setAge(p_70037_1_.getInt("Age"));
      this.forcedAge = p_70037_1_.getInt("ForcedAge");
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_BABY_ID.equals(p_184206_1_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            }

            --this.forcedAgeTimer;
         }
      } else if (this.isAlive()) {
         int i = this.getAge();
         if (i < 0) {
            ++i;
            this.setAge(i);
         } else if (i > 0) {
            --i;
            this.setAge(i);
         }
      }

   }

   protected void ageBoundaryReached() {
   }

   public boolean isBaby() {
      return this.getAge() < 0;
   }

   public void setBaby(boolean p_82227_1_) {
      this.setAge(p_82227_1_ ? -24000 : 0);
   }

   public static class AgeableData implements ILivingEntityData {
      private int groupSize;
      private final boolean shouldSpawnBaby;
      private final float babySpawnChance;

      private AgeableData(boolean p_i241905_1_, float p_i241905_2_) {
         this.shouldSpawnBaby = p_i241905_1_;
         this.babySpawnChance = p_i241905_2_;
      }

      public AgeableData(boolean p_i241904_1_) {
         this(p_i241904_1_, 0.05F);
      }

      public AgeableData(float p_i241903_1_) {
         this(true, p_i241903_1_);
      }

      public int getGroupSize() {
         return this.groupSize;
      }

      public void increaseGroupSizeByOne() {
         ++this.groupSize;
      }

      public boolean isShouldSpawnBaby() {
         return this.shouldSpawnBaby;
      }

      public float getBabySpawnChance() {
         return this.babySpawnChance;
      }
   }
}
