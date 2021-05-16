package net.minecraft.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EvokerEntity extends SpellcastingIllagerEntity {
   private SheepEntity wololoTarget;

   public EvokerEntity(EntityType<? extends EvokerEntity> p_i50207_1_, World p_i50207_2_) {
      super(p_i50207_1_, p_i50207_2_);
      this.xpReward = 10;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EvokerEntity.CastingSpellGoal());
      this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 0.6D, 1.0D));
      this.goalSelector.addGoal(4, new EvokerEntity.SummonSpellGoal());
      this.goalSelector.addGoal(5, new EvokerEntity.AttackSpellGoal());
      this.goalSelector.addGoal(6, new EvokerEntity.WololoSpellGoal());
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MAX_HEALTH, 24.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.EVOKER_CELEBRATE;
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
   }

   public boolean isAlliedTo(Entity p_184191_1_) {
      if (p_184191_1_ == null) {
         return false;
      } else if (p_184191_1_ == this) {
         return true;
      } else if (super.isAlliedTo(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof VexEntity) {
         return this.isAlliedTo(((VexEntity)p_184191_1_).getOwner());
      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getMobType() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.EVOKER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.EVOKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.EVOKER_HURT;
   }

   private void setWololoTarget(@Nullable SheepEntity p_190748_1_) {
      this.wololoTarget = p_190748_1_;
   }

   @Nullable
   private SheepEntity getWololoTarget() {
      return this.wololoTarget;
   }

   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.EVOKER_CAST_SPELL;
   }

   public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
   }

   class AttackSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private AttackSpellGoal() {
      }

      protected int getCastingTime() {
         return 40;
      }

      protected int getCastingInterval() {
         return 100;
      }

      protected void performSpellCasting() {
         LivingEntity livingentity = EvokerEntity.this.getTarget();
         double d0 = Math.min(livingentity.getY(), EvokerEntity.this.getY());
         double d1 = Math.max(livingentity.getY(), EvokerEntity.this.getY()) + 1.0D;
         float f = (float)MathHelper.atan2(livingentity.getZ() - EvokerEntity.this.getZ(), livingentity.getX() - EvokerEntity.this.getX());
         if (EvokerEntity.this.distanceToSqr(livingentity) < 9.0D) {
            for(int i = 0; i < 5; ++i) {
               float f1 = f + (float)i * (float)Math.PI * 0.4F;
               this.createSpellEntity(EvokerEntity.this.getX() + (double)MathHelper.cos(f1) * 1.5D, EvokerEntity.this.getZ() + (double)MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
            }

            for(int k = 0; k < 8; ++k) {
               float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
               this.createSpellEntity(EvokerEntity.this.getX() + (double)MathHelper.cos(f2) * 2.5D, EvokerEntity.this.getZ() + (double)MathHelper.sin(f2) * 2.5D, d0, d1, f2, 3);
            }
         } else {
            for(int l = 0; l < 16; ++l) {
               double d2 = 1.25D * (double)(l + 1);
               int j = 1 * l;
               this.createSpellEntity(EvokerEntity.this.getX() + (double)MathHelper.cos(f) * d2, EvokerEntity.this.getZ() + (double)MathHelper.sin(f) * d2, d0, d1, f, j);
            }
         }

      }

      private void createSpellEntity(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
         BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
         boolean flag = false;
         double d0 = 0.0D;

         do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = EvokerEntity.this.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(EvokerEntity.this.level, blockpos1, Direction.UP)) {
               if (!EvokerEntity.this.level.isEmptyBlock(blockpos)) {
                  BlockState blockstate1 = EvokerEntity.this.level.getBlockState(blockpos);
                  VoxelShape voxelshape = blockstate1.getCollisionShape(EvokerEntity.this.level, blockpos);
                  if (!voxelshape.isEmpty()) {
                     d0 = voxelshape.max(Direction.Axis.Y);
                  }
               }

               flag = true;
               break;
            }

            blockpos = blockpos.below();
         } while(blockpos.getY() >= MathHelper.floor(p_190876_5_) - 1);

         if (flag) {
            EvokerEntity.this.level.addFreshEntity(new EvokerFangsEntity(EvokerEntity.this.level, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EvokerEntity.this));
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_ATTACK;
      }

      protected SpellcastingIllagerEntity.SpellType getSpell() {
         return SpellcastingIllagerEntity.SpellType.FANGS;
      }
   }

   class CastingSpellGoal extends SpellcastingIllagerEntity.CastingASpellGoal {
      private CastingSpellGoal() {
      }

      public void tick() {
         if (EvokerEntity.this.getTarget() != null) {
            EvokerEntity.this.getLookControl().setLookAt(EvokerEntity.this.getTarget(), (float)EvokerEntity.this.getMaxHeadYRot(), (float)EvokerEntity.this.getMaxHeadXRot());
         } else if (EvokerEntity.this.getWololoTarget() != null) {
            EvokerEntity.this.getLookControl().setLookAt(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.getMaxHeadYRot(), (float)EvokerEntity.this.getMaxHeadXRot());
         }

      }
   }

   class SummonSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private final EntityPredicate vexCountTargeting = (new EntityPredicate()).range(16.0D).allowUnseeable().ignoreInvisibilityTesting().allowInvulnerable().allowSameTeam();

      private SummonSpellGoal() {
      }

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else {
            int i = EvokerEntity.this.level.getNearbyEntities(VexEntity.class, this.vexCountTargeting, EvokerEntity.this, EvokerEntity.this.getBoundingBox().inflate(16.0D)).size();
            return EvokerEntity.this.random.nextInt(8) + 1 > i;
         }
      }

      protected int getCastingTime() {
         return 100;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void performSpellCasting() {
         ServerWorld serverworld = (ServerWorld)EvokerEntity.this.level;

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = EvokerEntity.this.blockPosition().offset(-2 + EvokerEntity.this.random.nextInt(5), 1, -2 + EvokerEntity.this.random.nextInt(5));
            VexEntity vexentity = EntityType.VEX.create(EvokerEntity.this.level);
            vexentity.moveTo(blockpos, 0.0F, 0.0F);
            vexentity.finalizeSpawn(serverworld, EvokerEntity.this.level.getCurrentDifficultyAt(blockpos), SpawnReason.MOB_SUMMONED, (ILivingEntityData)null, (CompoundNBT)null);
            vexentity.setOwner(EvokerEntity.this);
            vexentity.setBoundOrigin(blockpos);
            vexentity.setLimitedLife(20 * (30 + EvokerEntity.this.random.nextInt(90)));
            serverworld.addFreshEntityWithPassengers(vexentity);
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_SUMMON;
      }

      protected SpellcastingIllagerEntity.SpellType getSpell() {
         return SpellcastingIllagerEntity.SpellType.SUMMON_VEX;
      }
   }

   public class WololoSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private final EntityPredicate wololoTargeting = (new EntityPredicate()).range(16.0D).allowInvulnerable().selector((p_220844_0_) -> {
         return ((SheepEntity)p_220844_0_).getColor() == DyeColor.BLUE;
      });

      public boolean canUse() {
         if (EvokerEntity.this.getTarget() != null) {
            return false;
         } else if (EvokerEntity.this.isCastingSpell()) {
            return false;
         } else if (EvokerEntity.this.tickCount < this.nextAttackTickCount) {
            return false;
         } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(EvokerEntity.this.level, EvokerEntity.this)) {
            return false;
         } else {
            List<SheepEntity> list = EvokerEntity.this.level.getNearbyEntities(SheepEntity.class, this.wololoTargeting, EvokerEntity.this, EvokerEntity.this.getBoundingBox().inflate(16.0D, 4.0D, 16.0D));
            if (list.isEmpty()) {
               return false;
            } else {
               EvokerEntity.this.setWololoTarget(list.get(EvokerEntity.this.random.nextInt(list.size())));
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         return EvokerEntity.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
      }

      public void stop() {
         super.stop();
         EvokerEntity.this.setWololoTarget((SheepEntity)null);
      }

      protected void performSpellCasting() {
         SheepEntity sheepentity = EvokerEntity.this.getWololoTarget();
         if (sheepentity != null && sheepentity.isAlive()) {
            sheepentity.setColor(DyeColor.RED);
         }

      }

      protected int getCastWarmupTime() {
         return 40;
      }

      protected int getCastingTime() {
         return 60;
      }

      protected int getCastingInterval() {
         return 140;
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_WOLOLO;
      }

      protected SpellcastingIllagerEntity.SpellType getSpell() {
         return SpellcastingIllagerEntity.SpellType.WOLOLO;
      }
   }
}
