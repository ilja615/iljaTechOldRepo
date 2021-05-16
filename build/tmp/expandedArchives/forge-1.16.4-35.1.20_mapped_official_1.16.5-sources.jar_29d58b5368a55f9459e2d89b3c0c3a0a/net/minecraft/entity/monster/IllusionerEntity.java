package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IllusionerEntity extends SpellcastingIllagerEntity implements IRangedAttackMob {
   private int clientSideIllusionTicks;
   private final Vector3d[][] clientSideIllusionOffsets;

   public IllusionerEntity(EntityType<? extends IllusionerEntity> p_i50203_1_, World p_i50203_2_) {
      super(p_i50203_1_, p_i50203_2_);
      this.xpReward = 5;
      this.clientSideIllusionOffsets = new Vector3d[2][4];

      for(int i = 0; i < 4; ++i) {
         this.clientSideIllusionOffsets[0][i] = Vector3d.ZERO;
         this.clientSideIllusionOffsets[1][i] = Vector3d.ZERO;
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new SpellcastingIllagerEntity.CastingASpellGoal());
      this.goalSelector.addGoal(4, new IllusionerEntity.MirrorSpellGoal());
      this.goalSelector.addGoal(5, new IllusionerEntity.BlindnessSpellGoal());
      this.goalSelector.addGoal(6, new RangedBowAttackGoal<>(this, 0.5D, 20, 15.0F));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 18.0D).add(Attributes.MAX_HEALTH, 32.0D);
   }

   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && this.isInvisible()) {
         --this.clientSideIllusionTicks;
         if (this.clientSideIllusionTicks < 0) {
            this.clientSideIllusionTicks = 0;
         }

         if (this.hurtTime != 1 && this.tickCount % 1200 != 0) {
            if (this.hurtTime == this.hurtDuration - 1) {
               this.clientSideIllusionTicks = 3;

               for(int k = 0; k < 4; ++k) {
                  this.clientSideIllusionOffsets[0][k] = this.clientSideIllusionOffsets[1][k];
                  this.clientSideIllusionOffsets[1][k] = new Vector3d(0.0D, 0.0D, 0.0D);
               }
            }
         } else {
            this.clientSideIllusionTicks = 3;
            float f = -6.0F;
            int i = 13;

            for(int j = 0; j < 4; ++j) {
               this.clientSideIllusionOffsets[0][j] = this.clientSideIllusionOffsets[1][j];
               this.clientSideIllusionOffsets[1][j] = new Vector3d((double)(-6.0F + (float)this.random.nextInt(13)) * 0.5D, (double)Math.max(0, this.random.nextInt(6) - 4), (double)(-6.0F + (float)this.random.nextInt(13)) * 0.5D);
            }

            for(int l = 0; l < 16; ++l) {
               this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getRandomY(), this.getZ(0.5D), 0.0D, 0.0D, 0.0D);
            }

            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, this.getSoundSource(), 1.0F, 1.0F, false);
         }
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d[] getIllusionOffsets(float p_193098_1_) {
      if (this.clientSideIllusionTicks <= 0) {
         return this.clientSideIllusionOffsets[1];
      } else {
         double d0 = (double)(((float)this.clientSideIllusionTicks - p_193098_1_) / 3.0F);
         d0 = Math.pow(d0, 0.25D);
         Vector3d[] avector3d = new Vector3d[4];

         for(int i = 0; i < 4; ++i) {
            avector3d[i] = this.clientSideIllusionOffsets[1][i].scale(1.0D - d0).add(this.clientSideIllusionOffsets[0][i].scale(d0));
         }

         return avector3d;
      }
   }

   public boolean isAlliedTo(Entity p_184191_1_) {
      if (super.isAlliedTo(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getMobType() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ILLUSIONER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ILLUSIONER_HURT;
   }

   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.ILLUSIONER_CAST_SPELL;
   }

   public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
   }

   public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
      AbstractArrowEntity abstractarrowentity = ProjectileHelper.getMobArrow(this, itemstack, p_82196_2_);
      if (this.getMainHandItem().getItem() instanceof net.minecraft.item.BowItem)
         abstractarrowentity = ((net.minecraft.item.BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
      double d0 = p_82196_1_.getX() - this.getX();
      double d1 = p_82196_1_.getY(0.3333333333333333D) - abstractarrowentity.getY();
      double d2 = p_82196_1_.getZ() - this.getZ();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(abstractarrowentity);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllagerEntity.ArmPose.SPELLCASTING;
      } else {
         return this.isAggressive() ? AbstractIllagerEntity.ArmPose.BOW_AND_ARROW : AbstractIllagerEntity.ArmPose.CROSSED;
      }
   }

   class BlindnessSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private int lastTargetId;

      private BlindnessSpellGoal() {
      }

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else if (IllusionerEntity.this.getTarget() == null) {
            return false;
         } else if (IllusionerEntity.this.getTarget().getId() == this.lastTargetId) {
            return false;
         } else {
            return IllusionerEntity.this.level.getCurrentDifficultyAt(IllusionerEntity.this.blockPosition()).isHarderThan((float)Difficulty.NORMAL.ordinal());
         }
      }

      public void start() {
         super.start();
         this.lastTargetId = IllusionerEntity.this.getTarget().getId();
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 180;
      }

      protected void performSpellCasting() {
         IllusionerEntity.this.getTarget().addEffect(new EffectInstance(Effects.BLINDNESS, 400));
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
      }

      protected SpellcastingIllagerEntity.SpellType getSpell() {
         return SpellcastingIllagerEntity.SpellType.BLINDNESS;
      }
   }

   class MirrorSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private MirrorSpellGoal() {
      }

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else {
            return !IllusionerEntity.this.hasEffect(Effects.INVISIBILITY);
         }
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void performSpellCasting() {
         IllusionerEntity.this.addEffect(new EffectInstance(Effects.INVISIBILITY, 1200));
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
      }

      protected SpellcastingIllagerEntity.SpellType getSpell() {
         return SpellcastingIllagerEntity.SpellType.DISAPPEAR;
      }
   }
}
