package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.BoostHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StriderEntity extends AnimalEntity implements IRideable, IEquipable {
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WARPED_FUNGUS);
   private static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
   private static final DataParameter<Integer> DATA_BOOST_TIME = EntityDataManager.defineId(StriderEntity.class, DataSerializers.INT);
   private static final DataParameter<Boolean> DATA_SUFFOCATING = EntityDataManager.defineId(StriderEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> DATA_SADDLE_ID = EntityDataManager.defineId(StriderEntity.class, DataSerializers.BOOLEAN);
   private final BoostHelper steering = new BoostHelper(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
   private TemptGoal temptGoal;
   private PanicGoal panicGoal;

   public StriderEntity(EntityType<? extends StriderEntity> p_i231562_1_, World p_i231562_2_) {
      super(p_i231562_1_, p_i231562_2_);
      this.blocksBuilding = true;
      this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
      this.setPathfindingMalus(PathNodeType.LAVA, 0.0F);
      this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0.0F);
   }

   public static boolean checkStriderSpawnRules(EntityType<StriderEntity> p_234314_0_, IWorld p_234314_1_, SpawnReason p_234314_2_, BlockPos p_234314_3_, Random p_234314_4_) {
      BlockPos.Mutable blockpos$mutable = p_234314_3_.mutable();

      do {
         blockpos$mutable.move(Direction.UP);
      } while(p_234314_1_.getFluidState(blockpos$mutable).is(FluidTags.LAVA));

      return p_234314_1_.getBlockState(blockpos$mutable).isAir();
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_BOOST_TIME.equals(p_184206_1_) && this.level.isClientSide) {
         this.steering.onSynced();
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BOOST_TIME, 0);
      this.entityData.define(DATA_SUFFOCATING, false);
      this.entityData.define(DATA_SADDLE_ID, false);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      this.steering.addAdditionalSaveData(p_213281_1_);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.steering.readAdditionalSaveData(p_70037_1_);
   }

   public boolean isSaddled() {
      return this.steering.hasSaddle();
   }

   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby();
   }

   public void equipSaddle(@Nullable SoundCategory p_230266_1_) {
      this.steering.setSaddle(true);
      if (p_230266_1_ != null) {
         this.level.playSound((PlayerEntity)null, this, SoundEvents.STRIDER_SADDLE, p_230266_1_, 0.5F, 1.0F);
      }

   }

   protected void registerGoals() {
      this.panicGoal = new PanicGoal(this, 1.65D);
      this.goalSelector.addGoal(1, this.panicGoal);
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.temptGoal = new TemptGoal(this, 1.4D, false, TEMPT_ITEMS);
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(4, new StriderEntity.MoveToLavaGoal(this, 1.5D));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D, 60));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.goalSelector.addGoal(9, new LookAtGoal(this, StriderEntity.class, 8.0F));
   }

   public void setSuffocating(boolean p_234319_1_) {
      this.entityData.set(DATA_SUFFOCATING, p_234319_1_);
   }

   public boolean isSuffocating() {
      return this.getVehicle() instanceof StriderEntity ? ((StriderEntity)this.getVehicle()).isSuffocating() : this.entityData.get(DATA_SUFFOCATING);
   }

   public boolean canStandOnFluid(Fluid p_230285_1_) {
      return p_230285_1_.is(FluidTags.LAVA);
   }

   public double getPassengersRidingOffset() {
      float f = Math.min(0.25F, this.animationSpeed);
      float f1 = this.animationPosition;
      return (double)this.getBbHeight() - 0.19D + (double)(0.12F * MathHelper.cos(f1 * 1.5F) * 2.0F * f);
   }

   public boolean canBeControlledByRider() {
      Entity entity = this.getControllingPassenger();
      if (!(entity instanceof PlayerEntity)) {
         return false;
      } else {
         PlayerEntity playerentity = (PlayerEntity)entity;
         return playerentity.getMainHandItem().getItem() == Items.WARPED_FUNGUS_ON_A_STICK || playerentity.getOffhandItem().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
      }
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return p_205019_1_.isUnobstructed(this);
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
   }

   public Vector3d getDismountLocationForPassenger(LivingEntity p_230268_1_) {
      Vector3d[] avector3d = new Vector3d[]{getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_230268_1_.getBbWidth(), p_230268_1_.yRot), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_230268_1_.getBbWidth(), p_230268_1_.yRot - 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_230268_1_.getBbWidth(), p_230268_1_.yRot + 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_230268_1_.getBbWidth(), p_230268_1_.yRot - 45.0F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_230268_1_.getBbWidth(), p_230268_1_.yRot + 45.0F)};
      Set<BlockPos> set = Sets.newLinkedHashSet();
      double d0 = this.getBoundingBox().maxY;
      double d1 = this.getBoundingBox().minY - 0.5D;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Vector3d vector3d : avector3d) {
         blockpos$mutable.set(this.getX() + vector3d.x, d0, this.getZ() + vector3d.z);

         for(double d2 = d0; d2 > d1; --d2) {
            set.add(blockpos$mutable.immutable());
            blockpos$mutable.move(Direction.DOWN);
         }
      }

      for(BlockPos blockpos : set) {
         if (!this.level.getFluidState(blockpos).is(FluidTags.LAVA)) {
            double d3 = this.level.getBlockFloorHeight(blockpos);
            if (TransportationHelper.isBlockFloorValid(d3)) {
               Vector3d vector3d1 = Vector3d.upFromBottomCenterOf(blockpos, d3);

               for(Pose pose : p_230268_1_.getDismountPoses()) {
                  AxisAlignedBB axisalignedbb = p_230268_1_.getLocalBoundsForPose(pose);
                  if (TransportationHelper.canDismountTo(this.level, p_230268_1_, axisalignedbb.move(vector3d1))) {
                     p_230268_1_.setPose(pose);
                     return vector3d1;
                  }
               }
            }
         }
      }

      return new Vector3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   public void travel(Vector3d p_213352_1_) {
      this.setSpeed(this.getMoveSpeed());
      this.travel(this, this.steering, p_213352_1_);
   }

   public float getMoveSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.66F : 1.0F);
   }

   public float getSteeringSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.23F : 0.55F);
   }

   public void travelWithInput(Vector3d p_230267_1_) {
      super.travel(p_230267_1_);
   }

   protected float nextStep() {
      return this.moveDist + 0.6F;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(this.isInLava() ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.STRIDER_STEP, 1.0F, 1.0F);
   }

   public boolean boost() {
      return this.steering.boost(this.getRandom());
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
      this.checkInsideBlocks();
      if (this.isInLava()) {
         this.fallDistance = 0.0F;
      } else {
         super.checkFallDamage(p_184231_1_, p_184231_3_, p_184231_4_, p_184231_5_);
      }
   }

   public void tick() {
      if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
         this.playSound(SoundEvents.STRIDER_HAPPY, 1.0F, this.getVoicePitch());
      } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
         this.playSound(SoundEvents.STRIDER_RETREAT, 1.0F, this.getVoicePitch());
      }

      BlockState blockstate = this.level.getBlockState(this.blockPosition());
      BlockState blockstate1 = this.getBlockStateOn();
      boolean flag = blockstate.is(BlockTags.STRIDER_WARM_BLOCKS) || blockstate1.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0D;
      this.setSuffocating(!flag);
      super.tick();
      this.floatStrider();
      this.checkInsideBlocks();
   }

   private boolean isPanicking() {
      return this.panicGoal != null && this.panicGoal.isRunning();
   }

   private boolean isBeingTempted() {
      return this.temptGoal != null && this.temptGoal.isRunning();
   }

   protected boolean shouldPassengersInheritMalus() {
      return true;
   }

   private void floatStrider() {
      if (this.isInLava()) {
         ISelectionContext iselectioncontext = ISelectionContext.of(this);
         if (iselectioncontext.isAbove(FlowingFluidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
            this.onGround = true;
         } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.05D, 0.0D));
         }
      }

   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.175F).add(Attributes.FOLLOW_RANGE, 16.0D);
   }

   protected SoundEvent getAmbientSound() {
      return !this.isPanicking() && !this.isBeingTempted() ? SoundEvents.STRIDER_AMBIENT : null;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.STRIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.STRIDER_DEATH;
   }

   protected boolean canAddPassenger(Entity p_184219_1_) {
      return this.getPassengers().isEmpty() && !this.isEyeInFluid(FluidTags.LAVA);
   }

   public boolean isSensitiveToWater() {
      return true;
   }

   public boolean isOnFire() {
      return false;
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new StriderEntity.LavaPathNavigator(this, p_175447_1_);
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      if (p_205022_2_.getBlockState(p_205022_1_).getFluidState().is(FluidTags.LAVA)) {
         return 10.0F;
      } else {
         return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
      }
   }

   public StriderEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      return EntityType.STRIDER.create(p_241840_1_);
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return FOOD_ITEMS.test(p_70877_1_);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled()) {
         this.spawnAtLocation(Items.SADDLE);
      }

   }

   public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      boolean flag = this.isFood(p_230254_1_.getItemInHand(p_230254_2_));
      if (!flag && this.isSaddled() && !this.isVehicle() && !p_230254_1_.isSecondaryUseActive()) {
         if (!this.level.isClientSide) {
            p_230254_1_.startRiding(this);
         }

         return ActionResultType.sidedSuccess(this.level.isClientSide);
      } else {
         ActionResultType actionresulttype = super.mobInteract(p_230254_1_, p_230254_2_);
         if (!actionresulttype.consumesAction()) {
            ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
            return itemstack.getItem() == Items.SADDLE ? itemstack.interactLivingEntity(p_230254_1_, this, p_230254_2_) : ActionResultType.PASS;
         } else {
            if (flag && !this.isSilent()) {
               this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.STRIDER_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            return actionresulttype;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getLeashOffset() {
      return new Vector3d(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (this.isBaby()) {
         return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      } else {
         Object object;
         if (this.random.nextInt(30) == 0) {
            MobEntity mobentity = EntityType.ZOMBIFIED_PIGLIN.create(p_213386_1_.getLevel());
            object = this.spawnJockey(p_213386_1_, p_213386_2_, mobentity, new ZombieEntity.GroupData(ZombieEntity.getSpawnAsBabyOdds(this.random), false));
            mobentity.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
            this.equipSaddle((SoundCategory)null);
         } else if (this.random.nextInt(10) == 0) {
            AgeableEntity ageableentity = EntityType.STRIDER.create(p_213386_1_.getLevel());
            ageableentity.setAge(-24000);
            object = this.spawnJockey(p_213386_1_, p_213386_2_, ageableentity, (ILivingEntityData)null);
         } else {
            object = new AgeableEntity.AgeableData(0.5F);
         }

         return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)object, p_213386_5_);
      }
   }

   private ILivingEntityData spawnJockey(IServerWorld p_242331_1_, DifficultyInstance p_242331_2_, MobEntity p_242331_3_, @Nullable ILivingEntityData p_242331_4_) {
      p_242331_3_.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
      p_242331_3_.finalizeSpawn(p_242331_1_, p_242331_2_, SpawnReason.JOCKEY, p_242331_4_, (CompoundNBT)null);
      p_242331_3_.startRiding(this, true);
      return new AgeableEntity.AgeableData(0.0F);
   }

   static class LavaPathNavigator extends GroundPathNavigator {
      LavaPathNavigator(StriderEntity p_i231565_1_, World p_i231565_2_) {
         super(p_i231565_1_, p_i231565_2_);
      }

      protected PathFinder createPathFinder(int p_179679_1_) {
         this.nodeEvaluator = new WalkNodeProcessor();
         return new PathFinder(this.nodeEvaluator, p_179679_1_);
      }

      protected boolean hasValidPathType(PathNodeType p_230287_1_) {
         return p_230287_1_ != PathNodeType.LAVA && p_230287_1_ != PathNodeType.DAMAGE_FIRE && p_230287_1_ != PathNodeType.DANGER_FIRE ? super.hasValidPathType(p_230287_1_) : true;
      }

      public boolean isStableDestination(BlockPos p_188555_1_) {
         return this.level.getBlockState(p_188555_1_).is(Blocks.LAVA) || super.isStableDestination(p_188555_1_);
      }
   }

   static class MoveToLavaGoal extends MoveToBlockGoal {
      private final StriderEntity strider;

      private MoveToLavaGoal(StriderEntity p_i241913_1_, double p_i241913_2_) {
         super(p_i241913_1_, p_i241913_2_, 8, 2);
         this.strider = p_i241913_1_;
      }

      public BlockPos getMoveToTarget() {
         return this.blockPos;
      }

      public boolean canContinueToUse() {
         return !this.strider.isInLava() && this.isValidTarget(this.strider.level, this.blockPos);
      }

      public boolean canUse() {
         return !this.strider.isInLava() && super.canUse();
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         return p_179488_1_.getBlockState(p_179488_2_).is(Blocks.LAVA) && p_179488_1_.getBlockState(p_179488_2_.above()).isPathfindable(p_179488_1_, p_179488_2_, PathType.LAND);
      }
   }
}
