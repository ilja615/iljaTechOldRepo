package net.minecraft.entity.monster;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HoglinEntity extends AnimalEntity implements IMob, IFlinging {
   private static final DataParameter<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION = EntityDataManager.defineId(HoglinEntity.class, DataSerializers.BOOLEAN);
   private int attackAnimationRemainingTicks;
   private int timeInOverworld = 0;
   private boolean cannotBeHunted = false;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super HoglinEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ADULT, SensorType.HOGLIN_SPECIFIC_SENSOR);
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.AVOID_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.PACIFIED);

   public HoglinEntity(EntityType<? extends HoglinEntity> p_i231569_1_, World p_i231569_2_) {
      super(p_i231569_1_, p_i231569_2_);
      this.xpReward = 5;
   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return !this.isLeashed();
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.KNOCKBACK_RESISTANCE, (double)0.6F).add(Attributes.ATTACK_KNOCKBACK, 1.0D).add(Attributes.ATTACK_DAMAGE, 6.0D);
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      if (!(p_70652_1_ instanceof LivingEntity)) {
         return false;
      } else {
         this.attackAnimationRemainingTicks = 10;
         this.level.broadcastEntityEvent(this, (byte)4);
         this.playSound(SoundEvents.HOGLIN_ATTACK, 1.0F, this.getVoicePitch());
         HoglinTasks.onHitTarget(this, (LivingEntity)p_70652_1_);
         return IFlinging.hurtAndThrowTarget(this, (LivingEntity)p_70652_1_);
      }
   }

   protected void blockedByShield(LivingEntity p_213371_1_) {
      if (this.isAdult()) {
         IFlinging.throwTarget(this, p_213371_1_);
      }

   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      boolean flag = super.hurt(p_70097_1_, p_70097_2_);
      if (this.level.isClientSide) {
         return false;
      } else {
         if (flag && p_70097_1_.getEntity() instanceof LivingEntity) {
            HoglinTasks.wasHurtBy(this, (LivingEntity)p_70097_1_.getEntity());
         }

         return flag;
      }
   }

   protected Brain.BrainCodec<HoglinEntity> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> p_213364_1_) {
      return HoglinTasks.makeBrain(this.brainProvider().makeBrain(p_213364_1_));
   }

   public Brain<HoglinEntity> getBrain() {
      return (Brain<HoglinEntity>) super.getBrain();
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("hoglinBrain");
      this.getBrain().tick((ServerWorld)this.level, this);
      this.level.getProfiler().pop();
      HoglinTasks.updateActivity(this);
      if (this.isConverting()) {
         ++this.timeInOverworld;
         if (this.timeInOverworld > 300 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.ZOGLIN, (timer) -> this.timeInOverworld = timer)) {
            this.playSound(SoundEvents.HOGLIN_CONVERTED_TO_ZOMBIFIED);
            this.finishConversion((ServerWorld)this.level);
         }
      } else {
         this.timeInOverworld = 0;
      }

   }

   public void aiStep() {
      if (this.attackAnimationRemainingTicks > 0) {
         --this.attackAnimationRemainingTicks;
      }

      super.aiStep();
   }

   protected void ageBoundaryReached() {
      if (this.isBaby()) {
         this.xpReward = 3;
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5D);
      } else {
         this.xpReward = 5;
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
      }

   }

   public static boolean checkHoglinSpawnRules(EntityType<HoglinEntity> p_234361_0_, IWorld p_234361_1_, SpawnReason p_234361_2_, BlockPos p_234361_3_, Random p_234361_4_) {
      return !p_234361_1_.getBlockState(p_234361_3_.below()).is(Blocks.NETHER_WART_BLOCK);
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_1_.getRandom().nextFloat() < 0.2F) {
         this.setBaby(true);
      }

      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return !this.isPersistenceRequired();
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      if (HoglinTasks.isPosNearNearestRepellent(this, p_205022_1_)) {
         return -1.0F;
      } else {
         return p_205022_2_.getBlockState(p_205022_1_.below()).is(Blocks.CRIMSON_NYLIUM) ? 10.0F : 0.0F;
      }
   }

   public double getPassengersRidingOffset() {
      return (double)this.getBbHeight() - (this.isBaby() ? 0.2D : 0.15D);
   }

   public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ActionResultType actionresulttype = super.mobInteract(p_230254_1_, p_230254_2_);
      if (actionresulttype.consumesAction()) {
         this.setPersistenceRequired();
      }

      return actionresulttype;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 4) {
         this.attackAnimationRemainingTicks = 10;
         this.playSound(SoundEvents.HOGLIN_ATTACK, 1.0F, this.getVoicePitch());
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getAttackAnimationRemainingTicks() {
      return this.attackAnimationRemainingTicks;
   }

   protected boolean shouldDropExperience() {
      return true;
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      return this.xpReward;
   }

   private void finishConversion(ServerWorld p_234360_1_) {
      ZoglinEntity zoglinentity = this.convertTo(EntityType.ZOGLIN, true);
      if (zoglinentity != null) {
         zoglinentity.addEffect(new EffectInstance(Effects.CONFUSION, 200, 0));
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zoglinentity);
      }

   }

   public boolean isFood(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Items.CRIMSON_FUNGUS;
   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (this.isImmuneToZombification()) {
         p_213281_1_.putBoolean("IsImmuneToZombification", true);
      }

      p_213281_1_.putInt("TimeInOverworld", this.timeInOverworld);
      if (this.cannotBeHunted) {
         p_213281_1_.putBoolean("CannotBeHunted", true);
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setImmuneToZombification(p_70037_1_.getBoolean("IsImmuneToZombification"));
      this.timeInOverworld = p_70037_1_.getInt("TimeInOverworld");
      this.setCannotBeHunted(p_70037_1_.getBoolean("CannotBeHunted"));
   }

   public void setImmuneToZombification(boolean p_234370_1_) {
      this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, p_234370_1_);
   }

   private boolean isImmuneToZombification() {
      return this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
   }

   public boolean isConverting() {
      return !this.level.dimensionType().piglinSafe() && !this.isImmuneToZombification() && !this.isNoAi();
   }

   private void setCannotBeHunted(boolean p_234371_1_) {
      this.cannotBeHunted = p_234371_1_;
   }

   public boolean canBeHunted() {
      return this.isAdult() && !this.cannotBeHunted;
   }

   @Nullable
   public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      HoglinEntity hoglinentity = EntityType.HOGLIN.create(p_241840_1_);
      if (hoglinentity != null) {
         hoglinentity.setPersistenceRequired();
      }

      return hoglinentity;
   }

   public boolean canFallInLove() {
      return !HoglinTasks.isPacified(this) && super.canFallInLove();
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return this.level.isClientSide ? null : HoglinTasks.getSoundForCurrentActivity(this).orElse((SoundEvent)null);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.HOGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HOGLIN_DEATH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.HOSTILE_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.HOSTILE_SPLASH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.HOGLIN_STEP, 0.15F, 1.0F);
   }

   protected void playSound(SoundEvent p_241412_1_) {
      this.playSound(p_241412_1_, this.getSoundVolume(), this.getVoicePitch());
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPacketSender.sendEntityBrain(this);
   }
}
