package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IChargeableMob.class
)
public class WitherEntity extends MonsterEntity implements IChargeableMob, IRangedAttackMob {
   private static final DataParameter<Integer> DATA_TARGET_A = EntityDataManager.defineId(WitherEntity.class, DataSerializers.INT);
   private static final DataParameter<Integer> DATA_TARGET_B = EntityDataManager.defineId(WitherEntity.class, DataSerializers.INT);
   private static final DataParameter<Integer> DATA_TARGET_C = EntityDataManager.defineId(WitherEntity.class, DataSerializers.INT);
   private static final List<DataParameter<Integer>> DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
   private static final DataParameter<Integer> DATA_ID_INV = EntityDataManager.defineId(WitherEntity.class, DataSerializers.INT);
   private final float[] xRotHeads = new float[2];
   private final float[] yRotHeads = new float[2];
   private final float[] xRotOHeads = new float[2];
   private final float[] yRotOHeads = new float[2];
   private final int[] nextHeadUpdate = new int[2];
   private final int[] idleHeadUpdates = new int[2];
   private int destroyBlocksTick;
   private final ServerBossInfo bossEvent = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true);
   private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (p_213797_0_) -> {
      return p_213797_0_.getMobType() != CreatureAttribute.UNDEAD && p_213797_0_.attackable();
   };
   private static final EntityPredicate TARGETING_CONDITIONS = (new EntityPredicate()).range(20.0D).selector(LIVING_ENTITY_SELECTOR);

   public WitherEntity(EntityType<? extends WitherEntity> p_i50226_1_, World p_i50226_2_) {
      super(p_i50226_1_, p_i50226_2_);
      this.setHealth(this.getMaxHealth());
      this.getNavigation().setCanFloat(true);
      this.xpReward = 50;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new WitherEntity.DoNothingGoal());
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MobEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TARGET_A, 0);
      this.entityData.define(DATA_TARGET_B, 0);
      this.entityData.define(DATA_TARGET_C, 0);
      this.entityData.define(DATA_ID_INV, 0);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Invul", this.getInvulnerableTicks());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setInvulnerableTicks(p_70037_1_.getInt("Invul"));
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }

   }

   public void setCustomName(@Nullable ITextComponent p_200203_1_) {
      super.setCustomName(p_200203_1_);
      this.bossEvent.setName(this.getDisplayName());
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.WITHER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_DEATH;
   }

   public void aiStep() {
      Vector3d vector3d = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
      if (!this.level.isClientSide && this.getAlternativeTarget(0) > 0) {
         Entity entity = this.level.getEntity(this.getAlternativeTarget(0));
         if (entity != null) {
            double d0 = vector3d.y;
            if (this.getY() < entity.getY() || !this.isPowered() && this.getY() < entity.getY() + 5.0D) {
               d0 = Math.max(0.0D, d0);
               d0 = d0 + (0.3D - d0 * (double)0.6F);
            }

            vector3d = new Vector3d(vector3d.x, d0, vector3d.z);
            Vector3d vector3d1 = new Vector3d(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());
            if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
               Vector3d vector3d2 = vector3d1.normalize();
               vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D, vector3d2.z * 0.3D - vector3d.z * 0.6D);
            }
         }
      }

      this.setDeltaMovement(vector3d);
      if (getHorizontalDistanceSqr(vector3d) > 0.05D) {
         this.yRot = (float)MathHelper.atan2(vector3d.z, vector3d.x) * (180F / (float)Math.PI) - 90.0F;
      }

      super.aiStep();

      for(int i = 0; i < 2; ++i) {
         this.yRotOHeads[i] = this.yRotHeads[i];
         this.xRotOHeads[i] = this.xRotHeads[i];
      }

      for(int j = 0; j < 2; ++j) {
         int k = this.getAlternativeTarget(j + 1);
         Entity entity1 = null;
         if (k > 0) {
            entity1 = this.level.getEntity(k);
         }

         if (entity1 != null) {
            double d9 = this.getHeadX(j + 1);
            double d1 = this.getHeadY(j + 1);
            double d3 = this.getHeadZ(j + 1);
            double d4 = entity1.getX() - d9;
            double d5 = entity1.getEyeY() - d1;
            double d6 = entity1.getZ() - d3;
            double d7 = (double)MathHelper.sqrt(d4 * d4 + d6 * d6);
            float f = (float)(MathHelper.atan2(d6, d4) * (double)(180F / (float)Math.PI)) - 90.0F;
            float f1 = (float)(-(MathHelper.atan2(d5, d7) * (double)(180F / (float)Math.PI)));
            this.xRotHeads[j] = this.rotlerp(this.xRotHeads[j], f1, 40.0F);
            this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], f, 10.0F);
         } else {
            this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], this.yBodyRot, 10.0F);
         }
      }

      boolean flag = this.isPowered();

      for(int l = 0; l < 3; ++l) {
         double d8 = this.getHeadX(l);
         double d10 = this.getHeadY(l);
         double d2 = this.getHeadZ(l);
         this.level.addParticle(ParticleTypes.SMOKE, d8 + this.random.nextGaussian() * (double)0.3F, d10 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
         if (flag && this.level.random.nextInt(4) == 0) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d8 + this.random.nextGaussian() * (double)0.3F, d10 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, (double)0.7F, (double)0.7F, 0.5D);
         }
      }

      if (this.getInvulnerableTicks() > 0) {
         for(int i1 = 0; i1 < 3; ++i1) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * 3.3F), this.getZ() + this.random.nextGaussian(), (double)0.7F, (double)0.7F, (double)0.9F);
         }
      }

   }

   protected void customServerAiStep() {
      if (this.getInvulnerableTicks() > 0) {
         int j1 = this.getInvulnerableTicks() - 1;
         if (j1 <= 0) {
            Explosion.Mode explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
            this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, explosion$mode);
            if (!this.isSilent()) {
               this.level.globalLevelEvent(1023, this.blockPosition(), 0);
            }
         }

         this.setInvulnerableTicks(j1);
         if (this.tickCount % 10 == 0) {
            this.heal(10.0F);
         }

      } else {
         super.customServerAiStep();

         for(int i = 1; i < 3; ++i) {
            if (this.tickCount >= this.nextHeadUpdate[i - 1]) {
               this.nextHeadUpdate[i - 1] = this.tickCount + 10 + this.random.nextInt(10);
               if (this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) {
                  int j3 = i - 1;
                  int k3 = this.idleHeadUpdates[i - 1];
                  this.idleHeadUpdates[j3] = this.idleHeadUpdates[i - 1] + 1;
                  if (k3 > 15) {
                     float f = 10.0F;
                     float f1 = 5.0F;
                     double d0 = MathHelper.nextDouble(this.random, this.getX() - 10.0D, this.getX() + 10.0D);
                     double d1 = MathHelper.nextDouble(this.random, this.getY() - 5.0D, this.getY() + 5.0D);
                     double d2 = MathHelper.nextDouble(this.random, this.getZ() - 10.0D, this.getZ() + 10.0D);
                     this.performRangedAttack(i + 1, d0, d1, d2, true);
                     this.idleHeadUpdates[i - 1] = 0;
                  }
               }

               int k1 = this.getAlternativeTarget(i);
               if (k1 > 0) {
                  Entity entity = this.level.getEntity(k1);
                  if (entity != null && entity.isAlive() && !(this.distanceToSqr(entity) > 900.0D) && this.canSee(entity)) {
                     if (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.invulnerable) {
                        this.setAlternativeTarget(i, 0);
                     } else {
                        this.performRangedAttack(i + 1, (LivingEntity)entity);
                        this.nextHeadUpdate[i - 1] = this.tickCount + 40 + this.random.nextInt(20);
                        this.idleHeadUpdates[i - 1] = 0;
                     }
                  } else {
                     this.setAlternativeTarget(i, 0);
                  }
               } else {
                  List<LivingEntity> list = this.level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));

                  for(int j2 = 0; j2 < 10 && !list.isEmpty(); ++j2) {
                     LivingEntity livingentity = list.get(this.random.nextInt(list.size()));
                     if (livingentity != this && livingentity.isAlive() && this.canSee(livingentity)) {
                        if (livingentity instanceof PlayerEntity) {
                           if (!((PlayerEntity)livingentity).abilities.invulnerable) {
                              this.setAlternativeTarget(i, livingentity.getId());
                           }
                        } else {
                           this.setAlternativeTarget(i, livingentity.getId());
                        }
                        break;
                     }

                     list.remove(livingentity);
                  }
               }
            }
         }

         if (this.getTarget() != null) {
            this.setAlternativeTarget(0, this.getTarget().getId());
         } else {
            this.setAlternativeTarget(0, 0);
         }

         if (this.destroyBlocksTick > 0) {
            --this.destroyBlocksTick;
            if (this.destroyBlocksTick == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
               int i1 = MathHelper.floor(this.getY());
               int l1 = MathHelper.floor(this.getX());
               int i2 = MathHelper.floor(this.getZ());
               boolean flag = false;

               for(int k2 = -1; k2 <= 1; ++k2) {
                  for(int l2 = -1; l2 <= 1; ++l2) {
                     for(int j = 0; j <= 3; ++j) {
                        int i3 = l1 + k2;
                        int k = i1 + j;
                        int l = i2 + l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        BlockState blockstate = this.level.getBlockState(blockpos);
                        if (blockstate.canEntityDestroy(this.level, blockpos, this) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                           flag = this.level.destroyBlock(blockpos, true, this) || flag;
                        }
                     }
                  }
               }

               if (flag) {
                  this.level.levelEvent((PlayerEntity)null, 1022, this.blockPosition(), 0);
               }
            }
         }

         if (this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
      }
   }

   @Deprecated //Forge: DO NOT USE use BlockState.canEntityDestroy
   public static boolean canDestroy(BlockState p_181033_0_) {
      return !p_181033_0_.isAir() && !BlockTags.WITHER_IMMUNE.contains(p_181033_0_.getBlock());
   }

   public void makeInvulnerable() {
      this.setInvulnerableTicks(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   public void makeStuckInBlock(BlockState p_213295_1_, Vector3d p_213295_2_) {
   }

   public void startSeenByPlayer(ServerPlayerEntity p_184178_1_) {
      super.startSeenByPlayer(p_184178_1_);
      this.bossEvent.addPlayer(p_184178_1_);
   }

   public void stopSeenByPlayer(ServerPlayerEntity p_184203_1_) {
      super.stopSeenByPlayer(p_184203_1_);
      this.bossEvent.removePlayer(p_184203_1_);
   }

   private double getHeadX(int p_82214_1_) {
      if (p_82214_1_ <= 0) {
         return this.getX();
      } else {
         float f = (this.yBodyRot + (float)(180 * (p_82214_1_ - 1))) * ((float)Math.PI / 180F);
         float f1 = MathHelper.cos(f);
         return this.getX() + (double)f1 * 1.3D;
      }
   }

   private double getHeadY(int p_82208_1_) {
      return p_82208_1_ <= 0 ? this.getY() + 3.0D : this.getY() + 2.2D;
   }

   private double getHeadZ(int p_82213_1_) {
      if (p_82213_1_ <= 0) {
         return this.getZ();
      } else {
         float f = (this.yBodyRot + (float)(180 * (p_82213_1_ - 1))) * ((float)Math.PI / 180F);
         float f1 = MathHelper.sin(f);
         return this.getZ() + (double)f1 * 1.3D;
      }
   }

   private float rotlerp(float p_82204_1_, float p_82204_2_, float p_82204_3_) {
      float f = MathHelper.wrapDegrees(p_82204_2_ - p_82204_1_);
      if (f > p_82204_3_) {
         f = p_82204_3_;
      }

      if (f < -p_82204_3_) {
         f = -p_82204_3_;
      }

      return p_82204_1_ + f;
   }

   private void performRangedAttack(int p_82216_1_, LivingEntity p_82216_2_) {
      this.performRangedAttack(p_82216_1_, p_82216_2_.getX(), p_82216_2_.getY() + (double)p_82216_2_.getEyeHeight() * 0.5D, p_82216_2_.getZ(), p_82216_1_ == 0 && this.random.nextFloat() < 0.001F);
   }

   private void performRangedAttack(int p_82209_1_, double p_82209_2_, double p_82209_4_, double p_82209_6_, boolean p_82209_8_) {
      if (!this.isSilent()) {
         this.level.levelEvent((PlayerEntity)null, 1024, this.blockPosition(), 0);
      }

      double d0 = this.getHeadX(p_82209_1_);
      double d1 = this.getHeadY(p_82209_1_);
      double d2 = this.getHeadZ(p_82209_1_);
      double d3 = p_82209_2_ - d0;
      double d4 = p_82209_4_ - d1;
      double d5 = p_82209_6_ - d2;
      WitherSkullEntity witherskullentity = new WitherSkullEntity(this.level, this, d3, d4, d5);
      witherskullentity.setOwner(this);
      if (p_82209_8_) {
         witherskullentity.setDangerous(true);
      }

      witherskullentity.setPosRaw(d0, d1, d2);
      this.level.addFreshEntity(witherskullentity);
   }

   public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      this.performRangedAttack(0, p_82196_1_);
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (p_70097_1_ != DamageSource.DROWN && !(p_70097_1_.getEntity() instanceof WitherEntity)) {
         if (this.getInvulnerableTicks() > 0 && p_70097_1_ != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (this.isPowered()) {
               Entity entity = p_70097_1_.getDirectEntity();
               if (entity instanceof AbstractArrowEntity) {
                  return false;
               }
            }

            Entity entity1 = p_70097_1_.getEntity();
            if (entity1 != null && !(entity1 instanceof PlayerEntity) && entity1 instanceof LivingEntity && ((LivingEntity)entity1).getMobType() == this.getMobType()) {
               return false;
            } else {
               if (this.destroyBlocksTick <= 0) {
                  this.destroyBlocksTick = 20;
               }

               for(int i = 0; i < this.idleHeadUpdates.length; ++i) {
                  this.idleHeadUpdates[i] += 3;
               }

               return super.hurt(p_70097_1_, p_70097_2_);
            }
         }
      } else {
         return false;
      }
   }

   protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
      ItemEntity itementity = this.spawnAtLocation(Items.NETHER_STAR);
      if (itementity != null) {
         itementity.setExtendedLifetime();
      }

   }

   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.remove();
      } else {
         this.noActionTime = 0;
      }
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   public boolean addEffect(EffectInstance p_195064_1_) {
      return false;
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.MAX_HEALTH, 300.0D).add(Attributes.MOVEMENT_SPEED, (double)0.6F).add(Attributes.FOLLOW_RANGE, 40.0D).add(Attributes.ARMOR, 4.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadYRot(int p_82207_1_) {
      return this.yRotHeads[p_82207_1_];
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadXRot(int p_82210_1_) {
      return this.xRotHeads[p_82210_1_];
   }

   public int getInvulnerableTicks() {
      return this.entityData.get(DATA_ID_INV);
   }

   public void setInvulnerableTicks(int p_82215_1_) {
      this.entityData.set(DATA_ID_INV, p_82215_1_);
   }

   public int getAlternativeTarget(int p_82203_1_) {
      return this.entityData.get(DATA_TARGETS.get(p_82203_1_));
   }

   public void setAlternativeTarget(int p_82211_1_, int p_82211_2_) {
      this.entityData.set(DATA_TARGETS.get(p_82211_1_), p_82211_2_);
   }

   public boolean isPowered() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.UNDEAD;
   }

   protected boolean canRide(Entity p_184228_1_) {
      return false;
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public boolean canBeAffected(EffectInstance p_70687_1_) {
      return p_70687_1_.getEffect() == Effects.WITHER ? false : super.canBeAffected(p_70687_1_);
   }

   class DoNothingGoal extends Goal {
      public DoNothingGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return WitherEntity.this.getInvulnerableTicks() > 0;
      }
   }
}
