package net.minecraft.entity.monster;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveTowardsRaidGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractRaiderEntity extends PatrollerEntity {
   protected static final DataParameter<Boolean> IS_CELEBRATING = EntityDataManager.defineId(AbstractRaiderEntity.class, DataSerializers.BOOLEAN);
   private static final Predicate<ItemEntity> ALLOWED_ITEMS = (p_213647_0_) -> {
      return !p_213647_0_.hasPickUpDelay() && p_213647_0_.isAlive() && ItemStack.matches(p_213647_0_.getItem(), Raid.getLeaderBannerInstance());
   };
   @Nullable
   protected Raid raid;
   private int wave;
   private boolean canJoinRaid;
   private int ticksOutsideRaid;

   protected AbstractRaiderEntity(EntityType<? extends AbstractRaiderEntity> p_i50143_1_, World p_i50143_2_) {
      super(p_i50143_1_, p_i50143_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new AbstractRaiderEntity.PromoteLeaderGoal<>(this));
      this.goalSelector.addGoal(3, new MoveTowardsRaidGoal<>(this));
      this.goalSelector.addGoal(4, new AbstractRaiderEntity.InvadeHomeGoal(this, (double)1.05F, 1));
      this.goalSelector.addGoal(5, new AbstractRaiderEntity.CelebrateRaidLossGoal(this));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_CELEBRATING, false);
   }

   public abstract void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_);

   public boolean canJoinRaid() {
      return this.canJoinRaid;
   }

   public void setCanJoinRaid(boolean p_213644_1_) {
      this.canJoinRaid = p_213644_1_;
   }

   public void aiStep() {
      if (this.level instanceof ServerWorld && this.isAlive()) {
         Raid raid = this.getCurrentRaid();
         if (this.canJoinRaid()) {
            if (raid == null) {
               if (this.level.getGameTime() % 20L == 0L) {
                  Raid raid1 = ((ServerWorld)this.level).getRaidAt(this.blockPosition());
                  if (raid1 != null && RaidManager.canJoinRaid(this, raid1)) {
                     raid1.joinRaid(raid1.getGroupsSpawned(), this, (BlockPos)null, true);
                  }
               }
            } else {
               LivingEntity livingentity = this.getTarget();
               if (livingentity != null && (livingentity.getType() == EntityType.PLAYER || livingentity.getType() == EntityType.IRON_GOLEM)) {
                  this.noActionTime = 0;
               }
            }
         }
      }

      super.aiStep();
   }

   protected void updateNoActionTime() {
      this.noActionTime += 2;
   }

   public void die(DamageSource p_70645_1_) {
      if (this.level instanceof ServerWorld) {
         Entity entity = p_70645_1_.getEntity();
         Raid raid = this.getCurrentRaid();
         if (raid != null) {
            if (this.isPatrolLeader()) {
               raid.removeLeader(this.getWave());
            }

            if (entity != null && entity.getType() == EntityType.PLAYER) {
               raid.addHeroOfTheVillage(entity);
            }

            raid.removeFromRaid(this, false);
         }

         if (this.isPatrolLeader() && raid == null && ((ServerWorld)this.level).getRaidAt(this.blockPosition()) == null) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.HEAD);
            PlayerEntity playerentity = null;
            if (entity instanceof PlayerEntity) {
               playerentity = (PlayerEntity)entity;
            } else if (entity instanceof WolfEntity) {
               WolfEntity wolfentity = (WolfEntity)entity;
               LivingEntity livingentity = wolfentity.getOwner();
               if (wolfentity.isTame() && livingentity instanceof PlayerEntity) {
                  playerentity = (PlayerEntity)livingentity;
               }
            }

            if (!itemstack.isEmpty() && ItemStack.matches(itemstack, Raid.getLeaderBannerInstance()) && playerentity != null) {
               EffectInstance effectinstance1 = playerentity.getEffect(Effects.BAD_OMEN);
               int i = 1;
               if (effectinstance1 != null) {
                  i += effectinstance1.getAmplifier();
                  playerentity.removeEffectNoUpdate(Effects.BAD_OMEN);
               } else {
                  --i;
               }

               i = MathHelper.clamp(i, 0, 4);
               EffectInstance effectinstance = new EffectInstance(Effects.BAD_OMEN, 120000, i, false, false, true);
               if (!this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                  playerentity.addEffect(effectinstance);
               }
            }
         }
      }

      super.die(p_70645_1_);
   }

   public boolean canJoinPatrol() {
      return !this.hasActiveRaid();
   }

   public void setCurrentRaid(@Nullable Raid p_213652_1_) {
      this.raid = p_213652_1_;
   }

   @Nullable
   public Raid getCurrentRaid() {
      return this.raid;
   }

   public boolean hasActiveRaid() {
      return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
   }

   public void setWave(int p_213651_1_) {
      this.wave = p_213651_1_;
   }

   public int getWave() {
      return this.wave;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isCelebrating() {
      return this.entityData.get(IS_CELEBRATING);
   }

   public void setCelebrating(boolean p_213655_1_) {
      this.entityData.set(IS_CELEBRATING, p_213655_1_);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Wave", this.wave);
      p_213281_1_.putBoolean("CanJoinRaid", this.canJoinRaid);
      if (this.raid != null) {
         p_213281_1_.putInt("RaidId", this.raid.getId());
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.wave = p_70037_1_.getInt("Wave");
      this.canJoinRaid = p_70037_1_.getBoolean("CanJoinRaid");
      if (p_70037_1_.contains("RaidId", 3)) {
         if (this.level instanceof ServerWorld) {
            this.raid = ((ServerWorld)this.level).getRaids().get(p_70037_1_.getInt("RaidId"));
         }

         if (this.raid != null) {
            this.raid.addWaveMob(this.wave, this, false);
            if (this.isPatrolLeader()) {
               this.raid.setLeader(this.wave, this);
            }
         }
      }

   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      boolean flag = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;
      if (this.hasActiveRaid() && !flag && ItemStack.matches(itemstack, Raid.getLeaderBannerInstance())) {
         EquipmentSlotType equipmentslottype = EquipmentSlotType.HEAD;
         ItemStack itemstack1 = this.getItemBySlot(equipmentslottype);
         double d0 = (double)this.getEquipmentDropChance(equipmentslottype);
         if (!itemstack1.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
            this.spawnAtLocation(itemstack1);
         }

         this.onItemPickup(p_175445_1_);
         this.setItemSlot(equipmentslottype, itemstack);
         this.take(p_175445_1_, itemstack.getCount());
         p_175445_1_.remove();
         this.getCurrentRaid().setLeader(this.getWave(), this);
         this.setPatrolLeader(true);
      } else {
         super.pickUpItem(p_175445_1_);
      }

   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return this.getCurrentRaid() == null ? super.removeWhenFarAway(p_213397_1_) : false;
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.getCurrentRaid() != null;
   }

   public int getTicksOutsideRaid() {
      return this.ticksOutsideRaid;
   }

   public void setTicksOutsideRaid(int p_213653_1_) {
      this.ticksOutsideRaid = p_213653_1_;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.hasActiveRaid()) {
         this.getCurrentRaid().updateBossbar();
      }

      return super.hurt(p_70097_1_, p_70097_2_);
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setCanJoinRaid(this.getType() != EntityType.WITCH || p_213386_3_ != SpawnReason.NATURAL);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public abstract SoundEvent getCelebrateSound();

   public class CelebrateRaidLossGoal extends Goal {
      private final AbstractRaiderEntity mob;

      CelebrateRaidLossGoal(AbstractRaiderEntity p_i50571_2_) {
         this.mob = p_i50571_2_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         Raid raid = this.mob.getCurrentRaid();
         return this.mob.isAlive() && this.mob.getTarget() == null && raid != null && raid.isLoss();
      }

      public void start() {
         this.mob.setCelebrating(true);
         super.start();
      }

      public void stop() {
         this.mob.setCelebrating(false);
         super.stop();
      }

      public void tick() {
         if (!this.mob.isSilent() && this.mob.random.nextInt(100) == 0) {
            AbstractRaiderEntity.this.playSound(AbstractRaiderEntity.this.getCelebrateSound(), AbstractRaiderEntity.this.getSoundVolume(), AbstractRaiderEntity.this.getVoicePitch());
         }

         if (!this.mob.isPassenger() && this.mob.random.nextInt(50) == 0) {
            this.mob.getJumpControl().jump();
         }

         super.tick();
      }
   }

   public class FindTargetGoal extends Goal {
      private final AbstractRaiderEntity mob;
      private final float hostileRadiusSqr;
      public final EntityPredicate shoutTargeting = (new EntityPredicate()).range(8.0D).allowNonAttackable().allowInvulnerable().allowSameTeam().allowUnseeable().ignoreInvisibilityTesting();

      public FindTargetGoal(AbstractIllagerEntity p_i50573_2_, float p_i50573_3_) {
         this.mob = p_i50573_2_;
         this.hostileRadiusSqr = p_i50573_3_ * p_i50573_3_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.mob.getLastHurtByMob();
         return this.mob.getCurrentRaid() == null && this.mob.isPatrolling() && this.mob.getTarget() != null && !this.mob.isAggressive() && (livingentity == null || livingentity.getType() != EntityType.PLAYER);
      }

      public void start() {
         super.start();
         this.mob.getNavigation().stop();

         for(AbstractRaiderEntity abstractraiderentity : this.mob.level.getNearbyEntities(AbstractRaiderEntity.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D))) {
            abstractraiderentity.setTarget(this.mob.getTarget());
         }

      }

      public void stop() {
         super.stop();
         LivingEntity livingentity = this.mob.getTarget();
         if (livingentity != null) {
            for(AbstractRaiderEntity abstractraiderentity : this.mob.level.getNearbyEntities(AbstractRaiderEntity.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D))) {
               abstractraiderentity.setTarget(livingentity);
               abstractraiderentity.setAggressive(true);
            }

            this.mob.setAggressive(true);
         }

      }

      public void tick() {
         LivingEntity livingentity = this.mob.getTarget();
         if (livingentity != null) {
            if (this.mob.distanceToSqr(livingentity) > (double)this.hostileRadiusSqr) {
               this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
               if (this.mob.random.nextInt(50) == 0) {
                  this.mob.playAmbientSound();
               }
            } else {
               this.mob.setAggressive(true);
            }

            super.tick();
         }
      }
   }

   static class InvadeHomeGoal extends Goal {
      private final AbstractRaiderEntity raider;
      private final double speedModifier;
      private BlockPos poiPos;
      private final List<BlockPos> visited = Lists.newArrayList();
      private final int distanceToPoi;
      private boolean stuck;

      public InvadeHomeGoal(AbstractRaiderEntity p_i50570_1_, double p_i50570_2_, int p_i50570_4_) {
         this.raider = p_i50570_1_;
         this.speedModifier = p_i50570_2_;
         this.distanceToPoi = p_i50570_4_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         this.updateVisited();
         return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
      }

      private boolean isValidRaid() {
         return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
      }

      private boolean hasSuitablePoi() {
         ServerWorld serverworld = (ServerWorld)this.raider.level;
         BlockPos blockpos = this.raider.blockPosition();
         Optional<BlockPos> optional = serverworld.getPoiManager().getRandom((p_220859_0_) -> {
            return p_220859_0_ == PointOfInterestType.HOME;
         }, this::hasNotVisited, PointOfInterestManager.Status.ANY, blockpos, 48, this.raider.random);
         if (!optional.isPresent()) {
            return false;
         } else {
            this.poiPos = optional.get().immutable();
            return true;
         }
      }

      public boolean canContinueToUse() {
         if (this.raider.getNavigation().isDone()) {
            return false;
         } else {
            return this.raider.getTarget() == null && !this.poiPos.closerThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi)) && !this.stuck;
         }
      }

      public void stop() {
         if (this.poiPos.closerThan(this.raider.position(), (double)this.distanceToPoi)) {
            this.visited.add(this.poiPos);
         }

      }

      public void start() {
         super.start();
         this.raider.setNoActionTime(0);
         this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
         this.stuck = false;
      }

      public void tick() {
         if (this.raider.getNavigation().isDone()) {
            Vector3d vector3d = Vector3d.atBottomCenterOf(this.poiPos);
            Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(this.raider, 16, 7, vector3d, (double)((float)Math.PI / 10F));
            if (vector3d1 == null) {
               vector3d1 = RandomPositionGenerator.getPosTowards(this.raider, 8, 7, vector3d);
            }

            if (vector3d1 == null) {
               this.stuck = true;
               return;
            }

            this.raider.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speedModifier);
         }

      }

      private boolean hasNotVisited(BlockPos p_220860_1_) {
         for(BlockPos blockpos : this.visited) {
            if (Objects.equals(p_220860_1_, blockpos)) {
               return false;
            }
         }

         return true;
      }

      private void updateVisited() {
         if (this.visited.size() > 2) {
            this.visited.remove(0);
         }

      }
   }

   public class PromoteLeaderGoal<T extends AbstractRaiderEntity> extends Goal {
      private final T mob;

      public PromoteLeaderGoal(T p_i50572_2_) {
         this.mob = p_i50572_2_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         Raid raid = this.mob.getCurrentRaid();
         if (this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.canBeLeader() && !ItemStack.matches(this.mob.getItemBySlot(EquipmentSlotType.HEAD), Raid.getLeaderBannerInstance())) {
            AbstractRaiderEntity abstractraiderentity = raid.getLeader(this.mob.getWave());
            if (abstractraiderentity == null || !abstractraiderentity.isAlive()) {
               List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D), AbstractRaiderEntity.ALLOWED_ITEMS);
               if (!list.isEmpty()) {
                  return this.mob.getNavigation().moveTo(list.get(0), (double)1.15F);
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.mob.getNavigation().getTargetPos().closerThan(this.mob.position(), 1.414D)) {
            List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D), AbstractRaiderEntity.ALLOWED_ITEMS);
            if (!list.isEmpty()) {
               this.mob.pickUpItem(list.get(0));
            }
         }

      }
   }
}
