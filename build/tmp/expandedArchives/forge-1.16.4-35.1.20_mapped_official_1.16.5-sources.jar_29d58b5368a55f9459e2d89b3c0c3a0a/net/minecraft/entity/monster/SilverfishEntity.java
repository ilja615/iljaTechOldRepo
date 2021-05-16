package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class SilverfishEntity extends MonsterEntity {
   private SilverfishEntity.SummonSilverfishGoal friendsGoal;

   public SilverfishEntity(EntityType<? extends SilverfishEntity> p_i50195_1_, World p_i50195_2_) {
      super(p_i50195_1_, p_i50195_2_);
   }

   protected void registerGoals() {
      this.friendsGoal = new SilverfishEntity.SummonSilverfishGoal(this);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(3, this.friendsGoal);
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new SilverfishEntity.HideInStoneGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
   }

   public double getMyRidingOffset() {
      return 0.1D;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.13F;
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 1.0D);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SILVERFISH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.SILVERFISH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SILVERFISH_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if ((p_70097_1_ instanceof EntityDamageSource || p_70097_1_ == DamageSource.MAGIC) && this.friendsGoal != null) {
            this.friendsGoal.notifyHurt();
         }

         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public void tick() {
      this.yBodyRot = this.yRot;
      super.tick();
   }

   public void setYBodyRot(float p_181013_1_) {
      this.yRot = p_181013_1_;
      super.setYBodyRot(p_181013_1_);
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return SilverfishBlock.isCompatibleHostBlock(p_205022_2_.getBlockState(p_205022_1_.below())) ? 10.0F : super.getWalkTargetValue(p_205022_1_, p_205022_2_);
   }

   public static boolean checkSliverfishSpawnRules(EntityType<SilverfishEntity> p_223331_0_, IWorld p_223331_1_, SpawnReason p_223331_2_, BlockPos p_223331_3_, Random p_223331_4_) {
      if (checkAnyLightMonsterSpawnRules(p_223331_0_, p_223331_1_, p_223331_2_, p_223331_3_, p_223331_4_)) {
         PlayerEntity playerentity = p_223331_1_.getNearestPlayer((double)p_223331_3_.getX() + 0.5D, (double)p_223331_3_.getY() + 0.5D, (double)p_223331_3_.getZ() + 0.5D, 5.0D, true);
         return playerentity == null;
      } else {
         return false;
      }
   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.ARTHROPOD;
   }

   static class HideInStoneGoal extends RandomWalkingGoal {
      private Direction selectedDirection;
      private boolean doMerge;

      public HideInStoneGoal(SilverfishEntity p_i45827_1_) {
         super(p_i45827_1_, 1.0D, 10);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (this.mob.getTarget() != null) {
            return false;
         } else if (!this.mob.getNavigation().isDone()) {
            return false;
         } else {
            Random random = this.mob.getRandom();
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.mob.level, this.mob) && random.nextInt(10) == 0) {
               this.selectedDirection = Direction.getRandom(random);
               BlockPos blockpos = (new BlockPos(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ())).relative(this.selectedDirection);
               BlockState blockstate = this.mob.level.getBlockState(blockpos);
               if (SilverfishBlock.isCompatibleHostBlock(blockstate)) {
                  this.doMerge = true;
                  return true;
               }
            }

            this.doMerge = false;
            return super.canUse();
         }
      }

      public boolean canContinueToUse() {
         return this.doMerge ? false : super.canContinueToUse();
      }

      public void start() {
         if (!this.doMerge) {
            super.start();
         } else {
            IWorld iworld = this.mob.level;
            BlockPos blockpos = (new BlockPos(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ())).relative(this.selectedDirection);
            BlockState blockstate = iworld.getBlockState(blockpos);
            if (SilverfishBlock.isCompatibleHostBlock(blockstate)) {
               iworld.setBlock(blockpos, SilverfishBlock.stateByHostBlock(blockstate.getBlock()), 3);
               this.mob.spawnAnim();
               this.mob.remove();
            }

         }
      }
   }

   static class SummonSilverfishGoal extends Goal {
      private final SilverfishEntity silverfish;
      private int lookForFriends;

      public SummonSilverfishGoal(SilverfishEntity p_i45826_1_) {
         this.silverfish = p_i45826_1_;
      }

      public void notifyHurt() {
         if (this.lookForFriends == 0) {
            this.lookForFriends = 20;
         }

      }

      public boolean canUse() {
         return this.lookForFriends > 0;
      }

      public void tick() {
         --this.lookForFriends;
         if (this.lookForFriends <= 0) {
            World world = this.silverfish.level;
            Random random = this.silverfish.getRandom();
            BlockPos blockpos = this.silverfish.blockPosition();

            for(int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
               for(int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
                  for(int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
                     BlockPos blockpos1 = blockpos.offset(j, i, k);
                     BlockState blockstate = world.getBlockState(blockpos1);
                     Block block = blockstate.getBlock();
                     if (block instanceof SilverfishBlock) {
                        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this.silverfish)) {
                           world.destroyBlock(blockpos1, true, this.silverfish);
                        } else {
                           world.setBlock(blockpos1, ((SilverfishBlock)block).getHostBlock().defaultBlockState(), 3);
                        }

                        if (random.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }

      }
   }
}
