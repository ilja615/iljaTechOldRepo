package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class SpiderEntity extends MonsterEntity {
   private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(SpiderEntity.class, DataSerializers.BYTE);

   public SpiderEntity(EntityType<? extends SpiderEntity> p_i48550_1_, World p_i48550_2_) {
      super(p_i48550_1_, p_i48550_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new SpiderEntity.AttackGoal(this));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new SpiderEntity.TargetGoal<>(this, PlayerEntity.class));
      this.targetSelector.addGoal(3, new SpiderEntity.TargetGoal<>(this, IronGolemEntity.class));
   }

   public double getPassengersRidingOffset() {
      return (double)(this.getBbHeight() * 0.5F);
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new ClimberPathNavigator(this, p_175447_1_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         this.setClimbing(this.horizontalCollision);
      }

   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SPIDER_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
   }

   public boolean onClimbable() {
      return this.isClimbing();
   }

   public void makeStuckInBlock(BlockState p_213295_1_, Vector3d p_213295_2_) {
      if (!p_213295_1_.is(Blocks.COBWEB)) {
         super.makeStuckInBlock(p_213295_1_, p_213295_2_);
      }

   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.ARTHROPOD;
   }

   public boolean canBeAffected(EffectInstance p_70687_1_) {
      if (p_70687_1_.getEffect() == Effects.POISON) {
         net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(this, p_70687_1_);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
         return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
      }
      return super.canBeAffected(p_70687_1_);
   }

   public boolean isClimbing() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setClimbing(boolean p_70839_1_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_70839_1_) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, b0);
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      if (p_213386_1_.getRandom().nextInt(100) == 0) {
         SkeletonEntity skeletonentity = EntityType.SKELETON.create(this.level);
         skeletonentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
         skeletonentity.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)null, (CompoundNBT)null);
         skeletonentity.startRiding(this);
      }

      if (p_213386_4_ == null) {
         p_213386_4_ = new SpiderEntity.GroupData();
         if (p_213386_1_.getDifficulty() == Difficulty.HARD && p_213386_1_.getRandom().nextFloat() < 0.1F * p_213386_2_.getSpecialMultiplier()) {
            ((SpiderEntity.GroupData)p_213386_4_).setRandomEffect(p_213386_1_.getRandom());
         }
      }

      if (p_213386_4_ instanceof SpiderEntity.GroupData) {
         Effect effect = ((SpiderEntity.GroupData)p_213386_4_).effect;
         if (effect != null) {
            this.addEffect(new EffectInstance(effect, Integer.MAX_VALUE));
         }
      }

      return p_213386_4_;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.65F;
   }

   static class AttackGoal extends MeleeAttackGoal {
      public AttackGoal(SpiderEntity p_i46676_1_) {
         super(p_i46676_1_, 1.0D, true);
      }

      public boolean canUse() {
         return super.canUse() && !this.mob.isVehicle();
      }

      public boolean canContinueToUse() {
         float f = this.mob.getBrightness();
         if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget((LivingEntity)null);
            return false;
         } else {
            return super.canContinueToUse();
         }
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         return (double)(4.0F + p_179512_1_.getBbWidth());
      }
   }

   public static class GroupData implements ILivingEntityData {
      public Effect effect;

      public void setRandomEffect(Random p_111104_1_) {
         int i = p_111104_1_.nextInt(5);
         if (i <= 1) {
            this.effect = Effects.MOVEMENT_SPEED;
         } else if (i <= 2) {
            this.effect = Effects.DAMAGE_BOOST;
         } else if (i <= 3) {
            this.effect = Effects.REGENERATION;
         } else if (i <= 4) {
            this.effect = Effects.INVISIBILITY;
         }

      }
   }

   static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
      public TargetGoal(SpiderEntity p_i45818_1_, Class<T> p_i45818_2_) {
         super(p_i45818_1_, p_i45818_2_, true);
      }

      public boolean canUse() {
         float f = this.mob.getBrightness();
         return f >= 0.5F ? false : super.canUse();
      }
   }
}
