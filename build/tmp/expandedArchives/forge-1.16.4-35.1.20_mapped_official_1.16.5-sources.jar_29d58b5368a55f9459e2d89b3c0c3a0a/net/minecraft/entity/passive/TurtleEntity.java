package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TurtleEntity extends AnimalEntity {
   private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.defineId(TurtleEntity.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.defineId(TurtleEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> LAYING_EGG = EntityDataManager.defineId(TurtleEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.defineId(TurtleEntity.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> GOING_HOME = EntityDataManager.defineId(TurtleEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.defineId(TurtleEntity.class, DataSerializers.BOOLEAN);
   private int layEggCounter;
   public static final Predicate<LivingEntity> BABY_ON_LAND_SELECTOR = (p_213616_0_) -> {
      return p_213616_0_.isBaby() && !p_213616_0_.isInWater();
   };

   public TurtleEntity(EntityType<? extends TurtleEntity> p_i50241_1_, World p_i50241_2_) {
      super(p_i50241_1_, p_i50241_2_);
      this.setPathfindingMalus(PathNodeType.WATER, 0.0F);
      this.moveControl = new TurtleEntity.MoveHelperController(this);
      this.maxUpStep = 1.0F;
   }

   public void setHomePos(BlockPos p_203011_1_) {
      this.entityData.set(HOME_POS, p_203011_1_);
   }

   private BlockPos getHomePos() {
      return this.entityData.get(HOME_POS);
   }

   private void setTravelPos(BlockPos p_203019_1_) {
      this.entityData.set(TRAVEL_POS, p_203019_1_);
   }

   private BlockPos getTravelPos() {
      return this.entityData.get(TRAVEL_POS);
   }

   public boolean hasEgg() {
      return this.entityData.get(HAS_EGG);
   }

   private void setHasEgg(boolean p_203017_1_) {
      this.entityData.set(HAS_EGG, p_203017_1_);
   }

   public boolean isLayingEgg() {
      return this.entityData.get(LAYING_EGG);
   }

   private void setLayingEgg(boolean p_203015_1_) {
      this.layEggCounter = p_203015_1_ ? 1 : 0;
      this.entityData.set(LAYING_EGG, p_203015_1_);
   }

   private boolean isGoingHome() {
      return this.entityData.get(GOING_HOME);
   }

   private void setGoingHome(boolean p_203012_1_) {
      this.entityData.set(GOING_HOME, p_203012_1_);
   }

   private boolean isTravelling() {
      return this.entityData.get(TRAVELLING);
   }

   private void setTravelling(boolean p_203021_1_) {
      this.entityData.set(TRAVELLING, p_203021_1_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(HOME_POS, BlockPos.ZERO);
      this.entityData.define(HAS_EGG, false);
      this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
      this.entityData.define(GOING_HOME, false);
      this.entityData.define(TRAVELLING, false);
      this.entityData.define(LAYING_EGG, false);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("HomePosX", this.getHomePos().getX());
      p_213281_1_.putInt("HomePosY", this.getHomePos().getY());
      p_213281_1_.putInt("HomePosZ", this.getHomePos().getZ());
      p_213281_1_.putBoolean("HasEgg", this.hasEgg());
      p_213281_1_.putInt("TravelPosX", this.getTravelPos().getX());
      p_213281_1_.putInt("TravelPosY", this.getTravelPos().getY());
      p_213281_1_.putInt("TravelPosZ", this.getTravelPos().getZ());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      int i = p_70037_1_.getInt("HomePosX");
      int j = p_70037_1_.getInt("HomePosY");
      int k = p_70037_1_.getInt("HomePosZ");
      this.setHomePos(new BlockPos(i, j, k));
      super.readAdditionalSaveData(p_70037_1_);
      this.setHasEgg(p_70037_1_.getBoolean("HasEgg"));
      int l = p_70037_1_.getInt("TravelPosX");
      int i1 = p_70037_1_.getInt("TravelPosY");
      int j1 = p_70037_1_.getInt("TravelPosZ");
      this.setTravelPos(new BlockPos(l, i1, j1));
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setHomePos(this.blockPosition());
      this.setTravelPos(BlockPos.ZERO);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public static boolean checkTurtleSpawnRules(EntityType<TurtleEntity> p_223322_0_, IWorld p_223322_1_, SpawnReason p_223322_2_, BlockPos p_223322_3_, Random p_223322_4_) {
      return p_223322_3_.getY() < p_223322_1_.getSeaLevel() + 4 && TurtleEggBlock.onSand(p_223322_1_, p_223322_3_) && p_223322_1_.getRawBrightness(p_223322_3_, 0) > 8;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new TurtleEntity.PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new TurtleEntity.MateGoal(this, 1.0D));
      this.goalSelector.addGoal(1, new TurtleEntity.LayEggGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new TurtleEntity.PlayerTemptGoal(this, 1.1D, Blocks.SEAGRASS.asItem()));
      this.goalSelector.addGoal(3, new TurtleEntity.GoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new TurtleEntity.GoHomeGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new TurtleEntity.TravelGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(9, new TurtleEntity.WanderGoal(this, 1.0D, 100));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.WATER;
   }

   public int getAmbientSoundInterval() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround && !this.isBaby() ? SoundEvents.TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(float p_203006_1_) {
      super.playSwimSound(p_203006_1_ * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.TURTLE_SWIM;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isBaby() ? SoundEvents.TURTLE_HURT_BABY : SoundEvents.TURTLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.TURTLE_DEATH_BABY : SoundEvents.TURTLE_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      SoundEvent soundevent = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
      this.playSound(soundevent, 0.15F, 1.0F);
   }

   public boolean canFallInLove() {
      return super.canFallInLove() && !this.hasEgg();
   }

   protected float nextStep() {
      return this.moveDist + 0.15F;
   }

   public float getScale() {
      return this.isBaby() ? 0.3F : 1.0F;
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new TurtleEntity.Navigator(this, p_175447_1_);
   }

   @Nullable
   public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      return EntityType.TURTLE.create(p_241840_1_);
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Blocks.SEAGRASS.asItem();
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      if (!this.isGoingHome() && p_205022_2_.getFluidState(p_205022_1_).is(FluidTags.WATER)) {
         return 10.0F;
      } else {
         return TurtleEggBlock.onSand(p_205022_2_, p_205022_1_) ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
         BlockPos blockpos = this.blockPosition();
         if (TurtleEggBlock.onSand(this.level, blockpos)) {
            this.level.levelEvent(2001, blockpos, Block.getId(Blocks.SAND.defaultBlockState()));
         }
      }

   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         this.spawnAtLocation(Items.SCUTE, 1);
      }

   }

   public void travel(Vector3d p_213352_1_) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(0.1F, p_213352_1_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if (this.getTarget() == null && (!this.isGoingHome() || !this.getHomePos().closerThan(this.position(), 20.0D))) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return false;
   }

   public void thunderHit(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
      this.hurt(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
   }

   static class GoHomeGoal extends Goal {
      private final TurtleEntity turtle;
      private final double speedModifier;
      private boolean stuck;
      private int closeToHomeTryTicks;

      GoHomeGoal(TurtleEntity p_i48821_1_, double p_i48821_2_) {
         this.turtle = p_i48821_1_;
         this.speedModifier = p_i48821_2_;
      }

      public boolean canUse() {
         if (this.turtle.isBaby()) {
            return false;
         } else if (this.turtle.hasEgg()) {
            return true;
         } else if (this.turtle.getRandom().nextInt(700) != 0) {
            return false;
         } else {
            return !this.turtle.getHomePos().closerThan(this.turtle.position(), 64.0D);
         }
      }

      public void start() {
         this.turtle.setGoingHome(true);
         this.stuck = false;
         this.closeToHomeTryTicks = 0;
      }

      public void stop() {
         this.turtle.setGoingHome(false);
      }

      public boolean canContinueToUse() {
         return !this.turtle.getHomePos().closerThan(this.turtle.position(), 7.0D) && !this.stuck && this.closeToHomeTryTicks <= 600;
      }

      public void tick() {
         BlockPos blockpos = this.turtle.getHomePos();
         boolean flag = blockpos.closerThan(this.turtle.position(), 16.0D);
         if (flag) {
            ++this.closeToHomeTryTicks;
         }

         if (this.turtle.getNavigation().isDone()) {
            Vector3d vector3d = Vector3d.atBottomCenterOf(blockpos);
            Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(this.turtle, 16, 3, vector3d, (double)((float)Math.PI / 10F));
            if (vector3d1 == null) {
               vector3d1 = RandomPositionGenerator.getPosTowards(this.turtle, 8, 7, vector3d);
            }

            if (vector3d1 != null && !flag && !this.turtle.level.getBlockState(new BlockPos(vector3d1)).is(Blocks.WATER)) {
               vector3d1 = RandomPositionGenerator.getPosTowards(this.turtle, 16, 5, vector3d);
            }

            if (vector3d1 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speedModifier);
         }

      }
   }

   static class GoToWaterGoal extends MoveToBlockGoal {
      private final TurtleEntity turtle;

      private GoToWaterGoal(TurtleEntity p_i48819_1_, double p_i48819_2_) {
         super(p_i48819_1_, p_i48819_1_.isBaby() ? 2.0D : p_i48819_2_, 24);
         this.turtle = p_i48819_1_;
         this.verticalSearchStart = -1;
      }

      public boolean canContinueToUse() {
         return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
      }

      public boolean canUse() {
         if (this.turtle.isBaby() && !this.turtle.isInWater()) {
            return super.canUse();
         } else {
            return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.canUse() : false;
         }
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 160 == 0;
      }

      protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         return p_179488_1_.getBlockState(p_179488_2_).is(Blocks.WATER);
      }
   }

   static class LayEggGoal extends MoveToBlockGoal {
      private final TurtleEntity turtle;

      LayEggGoal(TurtleEntity p_i48818_1_, double p_i48818_2_) {
         super(p_i48818_1_, p_i48818_2_, 16);
         this.turtle = p_i48818_1_;
      }

      public boolean canUse() {
         return this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0D) ? super.canUse() : false;
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0D);
      }

      public void tick() {
         super.tick();
         BlockPos blockpos = this.turtle.blockPosition();
         if (!this.turtle.isInWater() && this.isReachedTarget()) {
            if (this.turtle.layEggCounter < 1) {
               this.turtle.setLayingEgg(true);
            } else if (this.turtle.layEggCounter > 200) {
               World world = this.turtle.level;
               world.playSound((PlayerEntity)null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
               world.setBlock(this.blockPos.above(), Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, Integer.valueOf(this.turtle.random.nextInt(4) + 1)), 3);
               this.turtle.setHasEgg(false);
               this.turtle.setLayingEgg(false);
               this.turtle.setInLoveTime(600);
            }

            if (this.turtle.isLayingEgg()) {
               this.turtle.layEggCounter++;
            }
         }

      }

      protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         return !p_179488_1_.isEmptyBlock(p_179488_2_.above()) ? false : TurtleEggBlock.isSand(p_179488_1_, p_179488_2_);
      }
   }

   static class MateGoal extends BreedGoal {
      private final TurtleEntity turtle;

      MateGoal(TurtleEntity p_i48822_1_, double p_i48822_2_) {
         super(p_i48822_1_, p_i48822_2_);
         this.turtle = p_i48822_1_;
      }

      public boolean canUse() {
         return super.canUse() && !this.turtle.hasEgg();
      }

      protected void breed() {
         ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
         if (serverplayerentity == null && this.partner.getLoveCause() != null) {
            serverplayerentity = this.partner.getLoveCause();
         }

         if (serverplayerentity != null) {
            serverplayerentity.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, (AgeableEntity)null);
         }

         this.turtle.setHasEgg(true);
         this.animal.resetLove();
         this.partner.resetLove();
         Random random = this.animal.getRandom();
         if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
         }

      }
   }

   static class MoveHelperController extends MovementController {
      private final TurtleEntity turtle;

      MoveHelperController(TurtleEntity p_i48817_1_) {
         super(p_i48817_1_);
         this.turtle = p_i48817_1_;
      }

      private void updateSpeed() {
         if (this.turtle.isInWater()) {
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
            if (!this.turtle.getHomePos().closerThan(this.turtle.position(), 16.0D)) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.08F));
            }

            if (this.turtle.isBaby()) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.onGround) {
            this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateSpeed();
         if (this.operation == MovementController.Action.MOVE_TO && !this.turtle.getNavigation().isDone()) {
            double d0 = this.wantedX - this.turtle.getX();
            double d1 = this.wantedY - this.turtle.getY();
            double d2 = this.wantedZ - this.turtle.getZ();
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 = d1 / d3;
            float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.turtle.yRot = this.rotlerp(this.turtle.yRot, f, 90.0F);
            this.turtle.yBodyRot = this.turtle.yRot;
            float f1 = (float)(this.speedModifier * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.turtle.setSpeed(MathHelper.lerp(0.125F, this.turtle.getSpeed(), f1));
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, (double)this.turtle.getSpeed() * d1 * 0.1D, 0.0D));
         } else {
            this.turtle.setSpeed(0.0F);
         }
      }
   }

   static class Navigator extends SwimmerPathNavigator {
      Navigator(TurtleEntity p_i48815_1_, World p_i48815_2_) {
         super(p_i48815_1_, p_i48815_2_);
      }

      protected boolean canUpdatePath() {
         return true;
      }

      protected PathFinder createPathFinder(int p_179679_1_) {
         this.nodeEvaluator = new WalkAndSwimNodeProcessor();
         return new PathFinder(this.nodeEvaluator, p_179679_1_);
      }

      public boolean isStableDestination(BlockPos p_188555_1_) {
         if (this.mob instanceof TurtleEntity) {
            TurtleEntity turtleentity = (TurtleEntity)this.mob;
            if (turtleentity.isTravelling()) {
               return this.level.getBlockState(p_188555_1_).is(Blocks.WATER);
            }
         }

         return !this.level.getBlockState(p_188555_1_.below()).isAir();
      }
   }

   static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      PanicGoal(TurtleEntity p_i48816_1_, double p_i48816_2_) {
         super(p_i48816_1_, p_i48816_2_);
      }

      public boolean canUse() {
         if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
            return false;
         } else {
            BlockPos blockpos = this.lookForWater(this.mob.level, this.mob, 7, 4);
            if (blockpos != null) {
               this.posX = (double)blockpos.getX();
               this.posY = (double)blockpos.getY();
               this.posZ = (double)blockpos.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }

   static class PlayerTemptGoal extends Goal {
      private static final EntityPredicate TEMPT_TARGETING = (new EntityPredicate()).range(10.0D).allowSameTeam().allowInvulnerable();
      private final TurtleEntity turtle;
      private final double speedModifier;
      private PlayerEntity player;
      private int calmDown;
      private final Set<Item> items;

      PlayerTemptGoal(TurtleEntity p_i48812_1_, double p_i48812_2_, Item p_i48812_4_) {
         this.turtle = p_i48812_1_;
         this.speedModifier = p_i48812_2_;
         this.items = Sets.newHashSet(p_i48812_4_);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         if (this.calmDown > 0) {
            --this.calmDown;
            return false;
         } else {
            this.player = this.turtle.level.getNearestPlayer(TEMPT_TARGETING, this.turtle);
            if (this.player == null) {
               return false;
            } else {
               return this.shouldFollowItem(this.player.getMainHandItem()) || this.shouldFollowItem(this.player.getOffhandItem());
            }
         }
      }

      private boolean shouldFollowItem(ItemStack p_203131_1_) {
         return this.items.contains(p_203131_1_.getItem());
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public void stop() {
         this.player = null;
         this.turtle.getNavigation().stop();
         this.calmDown = 100;
      }

      public void tick() {
         this.turtle.getLookControl().setLookAt(this.player, (float)(this.turtle.getMaxHeadYRot() + 20), (float)this.turtle.getMaxHeadXRot());
         if (this.turtle.distanceToSqr(this.player) < 6.25D) {
            this.turtle.getNavigation().stop();
         } else {
            this.turtle.getNavigation().moveTo(this.player, this.speedModifier);
         }

      }
   }

   static class TravelGoal extends Goal {
      private final TurtleEntity turtle;
      private final double speedModifier;
      private boolean stuck;

      TravelGoal(TurtleEntity p_i48811_1_, double p_i48811_2_) {
         this.turtle = p_i48811_1_;
         this.speedModifier = p_i48811_2_;
      }

      public boolean canUse() {
         return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
      }

      public void start() {
         int i = 512;
         int j = 4;
         Random random = this.turtle.random;
         int k = random.nextInt(1025) - 512;
         int l = random.nextInt(9) - 4;
         int i1 = random.nextInt(1025) - 512;
         if ((double)l + this.turtle.getY() > (double)(this.turtle.level.getSeaLevel() - 1)) {
            l = 0;
         }

         BlockPos blockpos = new BlockPos((double)k + this.turtle.getX(), (double)l + this.turtle.getY(), (double)i1 + this.turtle.getZ());
         this.turtle.setTravelPos(blockpos);
         this.turtle.setTravelling(true);
         this.stuck = false;
      }

      public void tick() {
         if (this.turtle.getNavigation().isDone()) {
            Vector3d vector3d = Vector3d.atBottomCenterOf(this.turtle.getTravelPos());
            Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(this.turtle, 16, 3, vector3d, (double)((float)Math.PI / 10F));
            if (vector3d1 == null) {
               vector3d1 = RandomPositionGenerator.getPosTowards(this.turtle, 8, 7, vector3d);
            }

            if (vector3d1 != null) {
               int i = MathHelper.floor(vector3d1.x);
               int j = MathHelper.floor(vector3d1.z);
               int k = 34;
               if (!this.turtle.level.hasChunksAt(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
                  vector3d1 = null;
               }
            }

            if (vector3d1 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speedModifier);
         }

      }

      public boolean canContinueToUse() {
         return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      public void stop() {
         this.turtle.setTravelling(false);
         super.stop();
      }
   }

   static class WanderGoal extends RandomWalkingGoal {
      private final TurtleEntity turtle;

      private WanderGoal(TurtleEntity p_i48813_1_, double p_i48813_2_, int p_i48813_4_) {
         super(p_i48813_1_, p_i48813_2_, p_i48813_4_);
         this.turtle = p_i48813_1_;
      }

      public boolean canUse() {
         return !this.mob.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() ? super.canUse() : false;
      }
   }
}
