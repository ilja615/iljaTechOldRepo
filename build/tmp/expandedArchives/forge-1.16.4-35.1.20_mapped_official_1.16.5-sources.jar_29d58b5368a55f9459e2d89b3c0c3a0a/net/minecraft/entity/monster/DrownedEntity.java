package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity extends ZombieEntity implements IRangedAttackMob {
   private boolean searchingForLand;
   protected final SwimmerPathNavigator waterNavigation;
   protected final GroundPathNavigator groundNavigation;

   public DrownedEntity(EntityType<? extends DrownedEntity> p_i50212_1_, World p_i50212_2_) {
      super(p_i50212_1_, p_i50212_2_);
      this.maxUpStep = 1.0F;
      this.moveControl = new DrownedEntity.MoveHelperController(this);
      this.setPathfindingMalus(PathNodeType.WATER, 0.0F);
      this.waterNavigation = new SwimmerPathNavigator(this, p_i50212_2_);
      this.groundNavigation = new GroundPathNavigator(this, p_i50212_2_);
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(1, new DrownedEntity.GoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new DrownedEntity.TridentAttackGoal(this, 1.0D, 40, 10.0F));
      this.goalSelector.addGoal(2, new DrownedEntity.AttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new DrownedEntity.GoToBeachGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new DrownedEntity.SwimUpGoal(this, 1.0D, this.level.getSeaLevel()));
      this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, DrownedEntity.class)).setAlertOthers(ZombifiedPiglinEntity.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::okTarget));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
   }

   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      if (this.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
         this.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
         this.handDropChances[EquipmentSlotType.OFFHAND.getIndex()] = 2.0F;
      }

      return p_213386_4_;
   }

   public static boolean checkDrownedSpawnRules(EntityType<DrownedEntity> p_223332_0_, IServerWorld p_223332_1_, SpawnReason p_223332_2_, BlockPos p_223332_3_, Random p_223332_4_) {
      Optional<RegistryKey<Biome>> optional = p_223332_1_.getBiomeName(p_223332_3_);
      boolean flag = p_223332_1_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_223332_1_, p_223332_3_, p_223332_4_) && (p_223332_2_ == SpawnReason.SPAWNER || p_223332_1_.getFluidState(p_223332_3_).is(FluidTags.WATER));
      if (!Objects.equals(optional, Optional.of(Biomes.RIVER)) && !Objects.equals(optional, Optional.of(Biomes.FROZEN_RIVER))) {
         return p_223332_4_.nextInt(40) == 0 && isDeepEnoughToSpawn(p_223332_1_, p_223332_3_) && flag;
      } else {
         return p_223332_4_.nextInt(15) == 0 && flag;
      }
   }

   private static boolean isDeepEnoughToSpawn(IWorld p_223333_0_, BlockPos p_223333_1_) {
      return p_223333_1_.getY() < p_223333_0_.getSeaLevel() - 5;
   }

   protected boolean supportsBreakDoorGoal() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isInWater() ? SoundEvents.DROWNED_HURT_WATER : SoundEvents.DROWNED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWater() ? SoundEvents.DROWNED_DEATH_WATER : SoundEvents.DROWNED_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.DROWNED_STEP;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.DROWNED_SWIM;
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      if ((double)this.random.nextFloat() > 0.9D) {
         int i = this.random.nextInt(16);
         if (i < 10) {
            this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.TRIDENT));
         } else {
            this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.FISHING_ROD));
         }
      }

   }

   protected boolean canReplaceCurrentItem(ItemStack p_208003_1_, ItemStack p_208003_2_) {
      if (p_208003_2_.getItem() == Items.NAUTILUS_SHELL) {
         return false;
      } else if (p_208003_2_.getItem() == Items.TRIDENT) {
         if (p_208003_1_.getItem() == Items.TRIDENT) {
            return p_208003_1_.getDamageValue() < p_208003_2_.getDamageValue();
         } else {
            return false;
         }
      } else {
         return p_208003_1_.getItem() == Items.TRIDENT ? true : super.canReplaceCurrentItem(p_208003_1_, p_208003_2_);
      }
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return p_205019_1_.isUnobstructed(this);
   }

   public boolean okTarget(@Nullable LivingEntity p_204714_1_) {
      if (p_204714_1_ != null) {
         return !this.level.isDay() || p_204714_1_.isInWater();
      } else {
         return false;
      }
   }

   public boolean isPushedByFluid() {
      return !this.isSwimming();
   }

   private boolean wantsToSwim() {
      if (this.searchingForLand) {
         return true;
      } else {
         LivingEntity livingentity = this.getTarget();
         return livingentity != null && livingentity.isInWater();
      }
   }

   public void travel(Vector3d p_213352_1_) {
      if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
         this.moveRelative(0.01F, p_213352_1_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
      } else {
         super.travel(p_213352_1_);
      }

   }

   public void updateSwimming() {
      if (!this.level.isClientSide) {
         if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.navigation = this.waterNavigation;
            this.setSwimming(true);
         } else {
            this.navigation = this.groundNavigation;
            this.setSwimming(false);
         }
      }

   }

   protected boolean closeToNextPos() {
      Path path = this.getNavigation().getPath();
      if (path != null) {
         BlockPos blockpos = path.getTarget();
         if (blockpos != null) {
            double d0 = this.distanceToSqr((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
            if (d0 < 4.0D) {
               return true;
            }
         }
      }

      return false;
   }

   public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      TridentEntity tridententity = new TridentEntity(this.level, this, new ItemStack(Items.TRIDENT));
      double d0 = p_82196_1_.getX() - this.getX();
      double d1 = p_82196_1_.getY(0.3333333333333333D) - tridententity.getY();
      double d2 = p_82196_1_.getZ() - this.getZ();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      tridententity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(tridententity);
   }

   public void setSearchingForLand(boolean p_204713_1_) {
      this.searchingForLand = p_204713_1_;
   }

   static class AttackGoal extends ZombieAttackGoal {
      private final DrownedEntity drowned;

      public AttackGoal(DrownedEntity p_i48913_1_, double p_i48913_2_, boolean p_i48913_4_) {
         super(p_i48913_1_, p_i48913_2_, p_i48913_4_);
         this.drowned = p_i48913_1_;
      }

      public boolean canUse() {
         return super.canUse() && this.drowned.okTarget(this.drowned.getTarget());
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget());
      }
   }

   static class GoToBeachGoal extends MoveToBlockGoal {
      private final DrownedEntity drowned;

      public GoToBeachGoal(DrownedEntity p_i48911_1_, double p_i48911_2_) {
         super(p_i48911_1_, p_i48911_2_, 8, 2);
         this.drowned = p_i48911_1_;
      }

      public boolean canUse() {
         return super.canUse() && !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() >= (double)(this.drowned.level.getSeaLevel() - 3);
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse();
      }

      protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         BlockPos blockpos = p_179488_2_.above();
         return p_179488_1_.isEmptyBlock(blockpos) && p_179488_1_.isEmptyBlock(blockpos.above()) ? p_179488_1_.getBlockState(p_179488_2_).entityCanStandOn(p_179488_1_, p_179488_2_, this.drowned) : false;
      }

      public void start() {
         this.drowned.setSearchingForLand(false);
         this.drowned.navigation = this.drowned.groundNavigation;
         super.start();
      }

      public void stop() {
         super.stop();
      }
   }

   static class GoToWaterGoal extends Goal {
      private final CreatureEntity mob;
      private double wantedX;
      private double wantedY;
      private double wantedZ;
      private final double speedModifier;
      private final World level;

      public GoToWaterGoal(CreatureEntity p_i48910_1_, double p_i48910_2_) {
         this.mob = p_i48910_1_;
         this.speedModifier = p_i48910_2_;
         this.level = p_i48910_1_.level;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (!this.level.isDay()) {
            return false;
         } else if (this.mob.isInWater()) {
            return false;
         } else {
            Vector3d vector3d = this.getWaterPos();
            if (vector3d == null) {
               return false;
            } else {
               this.wantedX = vector3d.x;
               this.wantedY = vector3d.y;
               this.wantedZ = vector3d.z;
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         return !this.mob.getNavigation().isDone();
      }

      public void start() {
         this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
      }

      @Nullable
      private Vector3d getWaterPos() {
         Random random = this.mob.getRandom();
         BlockPos blockpos = this.mob.blockPosition();

         for(int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
               return Vector3d.atBottomCenterOf(blockpos1);
            }
         }

         return null;
      }
   }

   static class MoveHelperController extends MovementController {
      private final DrownedEntity drowned;

      public MoveHelperController(DrownedEntity p_i48909_1_) {
         super(p_i48909_1_);
         this.drowned = p_i48909_1_;
      }

      public void tick() {
         LivingEntity livingentity = this.drowned.getTarget();
         if (this.drowned.wantsToSwim() && this.drowned.isInWater()) {
            if (livingentity != null && livingentity.getY() > this.drowned.getY() || this.drowned.searchingForLand) {
               this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
            }

            if (this.operation != MovementController.Action.MOVE_TO || this.drowned.getNavigation().isDone()) {
               this.drowned.setSpeed(0.0F);
               return;
            }

            double d0 = this.wantedX - this.drowned.getX();
            double d1 = this.wantedY - this.drowned.getY();
            double d2 = this.wantedZ - this.drowned.getZ();
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 = d1 / d3;
            float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.drowned.yRot = this.rotlerp(this.drowned.yRot, f, 90.0F);
            this.drowned.yBodyRot = this.drowned.yRot;
            float f1 = (float)(this.speedModifier * this.drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float f2 = MathHelper.lerp(0.125F, this.drowned.getSpeed(), f1);
            this.drowned.setSpeed(f2);
            this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
         } else {
            if (!this.drowned.onGround) {
               this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
            }

            super.tick();
         }

      }
   }

   static class SwimUpGoal extends Goal {
      private final DrownedEntity drowned;
      private final double speedModifier;
      private final int seaLevel;
      private boolean stuck;

      public SwimUpGoal(DrownedEntity p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
         this.drowned = p_i48908_1_;
         this.speedModifier = p_i48908_2_;
         this.seaLevel = p_i48908_4_;
      }

      public boolean canUse() {
         return !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() < (double)(this.seaLevel - 2);
      }

      public boolean canContinueToUse() {
         return this.canUse() && !this.stuck;
      }

      public void tick() {
         if (this.drowned.getY() < (double)(this.seaLevel - 1) && (this.drowned.getNavigation().isDone() || this.drowned.closeToNextPos())) {
            Vector3d vector3d = RandomPositionGenerator.getPosTowards(this.drowned, 4, 8, new Vector3d(this.drowned.getX(), (double)(this.seaLevel - 1), this.drowned.getZ()));
            if (vector3d == null) {
               this.stuck = true;
               return;
            }

            this.drowned.getNavigation().moveTo(vector3d.x, vector3d.y, vector3d.z, this.speedModifier);
         }

      }

      public void start() {
         this.drowned.setSearchingForLand(true);
         this.stuck = false;
      }

      public void stop() {
         this.drowned.setSearchingForLand(false);
      }
   }

   static class TridentAttackGoal extends RangedAttackGoal {
      private final DrownedEntity drowned;

      public TridentAttackGoal(IRangedAttackMob p_i48907_1_, double p_i48907_2_, int p_i48907_4_, float p_i48907_5_) {
         super(p_i48907_1_, p_i48907_2_, p_i48907_4_, p_i48907_5_);
         this.drowned = (DrownedEntity)p_i48907_1_;
      }

      public boolean canUse() {
         return super.canUse() && this.drowned.getMainHandItem().getItem() == Items.TRIDENT;
      }

      public void start() {
         super.start();
         this.drowned.setAggressive(true);
         this.drowned.startUsingItem(Hand.MAIN_HAND);
      }

      public void stop() {
         super.stop();
         this.drowned.stopUsingItem();
         this.drowned.setAggressive(false);
      }
   }
}
