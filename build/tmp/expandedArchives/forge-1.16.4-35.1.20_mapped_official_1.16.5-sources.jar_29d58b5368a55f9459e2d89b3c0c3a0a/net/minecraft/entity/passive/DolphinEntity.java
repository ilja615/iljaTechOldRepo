package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.ai.goal.FindWaterGoal;
import net.minecraft.entity.ai.goal.FollowBoatGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DolphinEntity extends WaterMobEntity {
   private static final DataParameter<BlockPos> TREASURE_POS = EntityDataManager.defineId(DolphinEntity.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> GOT_FISH = EntityDataManager.defineId(DolphinEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> MOISTNESS_LEVEL = EntityDataManager.defineId(DolphinEntity.class, DataSerializers.INT);
   private static final EntityPredicate SWIM_WITH_PLAYER_TARGETING = (new EntityPredicate()).range(10.0D).allowSameTeam().allowInvulnerable().allowUnseeable();
   public static final Predicate<ItemEntity> ALLOWED_ITEMS = (p_205023_0_) -> {
      return !p_205023_0_.hasPickUpDelay() && p_205023_0_.isAlive() && p_205023_0_.isInWater();
   };

   public DolphinEntity(EntityType<? extends DolphinEntity> p_i50275_1_, World p_i50275_2_) {
      super(p_i50275_1_, p_i50275_2_);
      this.moveControl = new DolphinEntity.MoveHelperController(this);
      this.lookControl = new DolphinLookController(this, 10);
      this.setCanPickUpLoot(true);
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setAirSupply(this.getMaxAirSupply());
      this.xRot = 0.0F;
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   protected void handleAirSupply(int p_209207_1_) {
   }

   public void setTreasurePos(BlockPos p_208012_1_) {
      this.entityData.set(TREASURE_POS, p_208012_1_);
   }

   public BlockPos getTreasurePos() {
      return this.entityData.get(TREASURE_POS);
   }

   public boolean gotFish() {
      return this.entityData.get(GOT_FISH);
   }

   public void setGotFish(boolean p_208008_1_) {
      this.entityData.set(GOT_FISH, p_208008_1_);
   }

   public int getMoistnessLevel() {
      return this.entityData.get(MOISTNESS_LEVEL);
   }

   public void setMoisntessLevel(int p_211137_1_) {
      this.entityData.set(MOISTNESS_LEVEL, p_211137_1_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(TREASURE_POS, BlockPos.ZERO);
      this.entityData.define(GOT_FISH, false);
      this.entityData.define(MOISTNESS_LEVEL, 2400);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("TreasurePosX", this.getTreasurePos().getX());
      p_213281_1_.putInt("TreasurePosY", this.getTreasurePos().getY());
      p_213281_1_.putInt("TreasurePosZ", this.getTreasurePos().getZ());
      p_213281_1_.putBoolean("GotFish", this.gotFish());
      p_213281_1_.putInt("Moistness", this.getMoistnessLevel());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      int i = p_70037_1_.getInt("TreasurePosX");
      int j = p_70037_1_.getInt("TreasurePosY");
      int k = p_70037_1_.getInt("TreasurePosZ");
      this.setTreasurePos(new BlockPos(i, j, k));
      super.readAdditionalSaveData(p_70037_1_);
      this.setGotFish(p_70037_1_.getBoolean("GotFish"));
      this.setMoisntessLevel(p_70037_1_.getInt("Moistness"));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreatheAirGoal(this));
      this.goalSelector.addGoal(0, new FindWaterGoal(this));
      this.goalSelector.addGoal(1, new DolphinEntity.SwimToTreasureGoal(this));
      this.goalSelector.addGoal(2, new DolphinEntity.SwimWithPlayerGoal(this, 4.0D));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
      this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
      this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
      this.goalSelector.addGoal(6, new MeleeAttackGoal(this, (double)1.2F, true));
      this.goalSelector.addGoal(8, new DolphinEntity.PlayWithItemsGoal());
      this.goalSelector.addGoal(8, new FollowBoatGoal(this));
      this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, GuardianEntity.class, 8.0F, 1.0D, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, GuardianEntity.class)).setAlertOthers());
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)1.2F).add(Attributes.ATTACK_DAMAGE, 3.0D);
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new SwimmerPathNavigator(this, p_175447_1_);
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      boolean flag = p_70652_1_.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (flag) {
         this.doEnchantDamageEffects(this, p_70652_1_);
         this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
      }

      return flag;
   }

   public int getMaxAirSupply() {
      return 4800;
   }

   protected int increaseAirSupply(int p_207300_1_) {
      return this.getMaxAirSupply();
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.3F;
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   protected boolean canRide(Entity p_184228_1_) {
      return true;
   }

   public boolean canTakeItem(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(p_213365_1_);
      if (!this.getItemBySlot(equipmentslottype).isEmpty()) {
         return false;
      } else {
         return equipmentslottype == EquipmentSlotType.MAINHAND && super.canTakeItem(p_213365_1_);
      }
   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      if (this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()) {
         ItemStack itemstack = p_175445_1_.getItem();
         if (this.canHoldItem(itemstack)) {
            this.onItemPickup(p_175445_1_);
            this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
            this.handDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
            this.take(p_175445_1_, itemstack.getCount());
            p_175445_1_.remove();
         }
      }

   }

   public void tick() {
      super.tick();
      if (this.isNoAi()) {
         this.setAirSupply(this.getMaxAirSupply());
      } else {
         if (this.isInWaterRainOrBubble()) {
            this.setMoisntessLevel(2400);
         } else {
            this.setMoisntessLevel(this.getMoistnessLevel() - 1);
            if (this.getMoistnessLevel() <= 0) {
               this.hurt(DamageSource.DRY_OUT, 1.0F);
            }

            if (this.onGround) {
               this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
               this.yRot = this.random.nextFloat() * 360.0F;
               this.onGround = false;
               this.hasImpulse = true;
            }
         }

         if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
            Vector3d vector3d = this.getViewVector(0.0F);
            float f = MathHelper.cos(this.yRot * ((float)Math.PI / 180F)) * 0.3F;
            float f1 = MathHelper.sin(this.yRot * ((float)Math.PI / 180F)) * 0.3F;
            float f2 = 1.2F - this.random.nextFloat() * 0.7F;

            for(int i = 0; i < 2; ++i) {
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vector3d.x * (double)f2 + (double)f, this.getY() - vector3d.y, this.getZ() - vector3d.z * (double)f2 + (double)f1, 0.0D, 0.0D, 0.0D);
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vector3d.x * (double)f2 - (double)f, this.getY() - vector3d.y, this.getZ() - vector3d.z * (double)f2 - (double)f1, 0.0D, 0.0D, 0.0D);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 38) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   private void addParticlesAroundSelf(IParticleData p_208401_1_) {
      for(int i = 0; i < 7; ++i) {
         double d0 = this.random.nextGaussian() * 0.01D;
         double d1 = this.random.nextGaussian() * 0.01D;
         double d2 = this.random.nextGaussian() * 0.01D;
         this.level.addParticle(p_208401_1_, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   protected ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      if (!itemstack.isEmpty() && itemstack.getItem().is(ItemTags.FISHES)) {
         if (!this.level.isClientSide) {
            this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 1.0F);
         }

         this.setGotFish(true);
         if (!p_230254_1_.abilities.instabuild) {
            itemstack.shrink(1);
         }

         return ActionResultType.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   public static boolean checkDolphinSpawnRules(EntityType<DolphinEntity> p_223364_0_, IWorld p_223364_1_, SpawnReason p_223364_2_, BlockPos p_223364_3_, Random p_223364_4_) {
      if (p_223364_3_.getY() > 45 && p_223364_3_.getY() < p_223364_1_.getSeaLevel()) {
         Optional<RegistryKey<Biome>> optional = p_223364_1_.getBiomeName(p_223364_3_);
         return (!Objects.equals(optional, Optional.of(Biomes.OCEAN)) || !Objects.equals(optional, Optional.of(Biomes.DEEP_OCEAN))) && p_223364_1_.getFluidState(p_223364_3_).is(FluidTags.WATER);
      } else {
         return false;
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.DOLPHIN_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.DOLPHIN_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.DOLPHIN_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.DOLPHIN_SWIM;
   }

   protected boolean closeToNextPos() {
      BlockPos blockpos = this.getNavigation().getTargetPos();
      return blockpos != null ? blockpos.closerThan(this.position(), 12.0D) : false;
   }

   public void travel(Vector3d p_213352_1_) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), p_213352_1_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return true;
   }

   static class MoveHelperController extends MovementController {
      private final DolphinEntity dolphin;

      public MoveHelperController(DolphinEntity p_i48945_1_) {
         super(p_i48945_1_);
         this.dolphin = p_i48945_1_;
      }

      public void tick() {
         if (this.dolphin.isInWater()) {
            this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
         }

         if (this.operation == MovementController.Action.MOVE_TO && !this.dolphin.getNavigation().isDone()) {
            double d0 = this.wantedX - this.dolphin.getX();
            double d1 = this.wantedY - this.dolphin.getY();
            double d2 = this.wantedZ - this.dolphin.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < (double)2.5000003E-7F) {
               this.mob.setZza(0.0F);
            } else {
               float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
               this.dolphin.yRot = this.rotlerp(this.dolphin.yRot, f, 10.0F);
               this.dolphin.yBodyRot = this.dolphin.yRot;
               this.dolphin.yHeadRot = this.dolphin.yRot;
               float f1 = (float)(this.speedModifier * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
               if (this.dolphin.isInWater()) {
                  this.dolphin.setSpeed(f1 * 0.02F);
                  float f2 = -((float)(MathHelper.atan2(d1, (double)MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double)(180F / (float)Math.PI)));
                  f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                  this.dolphin.xRot = this.rotlerp(this.dolphin.xRot, f2, 5.0F);
                  float f3 = MathHelper.cos(this.dolphin.xRot * ((float)Math.PI / 180F));
                  float f4 = MathHelper.sin(this.dolphin.xRot * ((float)Math.PI / 180F));
                  this.dolphin.zza = f3 * f1;
                  this.dolphin.yya = -f4 * f1;
               } else {
                  this.dolphin.setSpeed(f1 * 0.1F);
               }

            }
         } else {
            this.dolphin.setSpeed(0.0F);
            this.dolphin.setXxa(0.0F);
            this.dolphin.setYya(0.0F);
            this.dolphin.setZza(0.0F);
         }
      }
   }

   class PlayWithItemsGoal extends Goal {
      private int cooldown;

      private PlayWithItemsGoal() {
      }

      public boolean canUse() {
         if (this.cooldown > DolphinEntity.this.tickCount) {
            return false;
         } else {
            List<ItemEntity> list = DolphinEntity.this.level.getEntitiesOfClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), DolphinEntity.ALLOWED_ITEMS);
            return !list.isEmpty() || !DolphinEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty();
         }
      }

      public void start() {
         List<ItemEntity> list = DolphinEntity.this.level.getEntitiesOfClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), DolphinEntity.ALLOWED_ITEMS);
         if (!list.isEmpty()) {
            DolphinEntity.this.getNavigation().moveTo(list.get(0), (double)1.2F);
            DolphinEntity.this.playSound(SoundEvents.DOLPHIN_PLAY, 1.0F, 1.0F);
         }

         this.cooldown = 0;
      }

      public void stop() {
         ItemStack itemstack = DolphinEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND);
         if (!itemstack.isEmpty()) {
            this.drop(itemstack);
            DolphinEntity.this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            this.cooldown = DolphinEntity.this.tickCount + DolphinEntity.this.random.nextInt(100);
         }

      }

      public void tick() {
         List<ItemEntity> list = DolphinEntity.this.level.getEntitiesOfClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), DolphinEntity.ALLOWED_ITEMS);
         ItemStack itemstack = DolphinEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND);
         if (!itemstack.isEmpty()) {
            this.drop(itemstack);
            DolphinEntity.this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
         } else if (!list.isEmpty()) {
            DolphinEntity.this.getNavigation().moveTo(list.get(0), (double)1.2F);
         }

      }

      private void drop(ItemStack p_220810_1_) {
         if (!p_220810_1_.isEmpty()) {
            double d0 = DolphinEntity.this.getEyeY() - (double)0.3F;
            ItemEntity itementity = new ItemEntity(DolphinEntity.this.level, DolphinEntity.this.getX(), d0, DolphinEntity.this.getZ(), p_220810_1_);
            itementity.setPickUpDelay(40);
            itementity.setThrower(DolphinEntity.this.getUUID());
            float f = 0.3F;
            float f1 = DolphinEntity.this.random.nextFloat() * ((float)Math.PI * 2F);
            float f2 = 0.02F * DolphinEntity.this.random.nextFloat();
            itementity.setDeltaMovement((double)(0.3F * -MathHelper.sin(DolphinEntity.this.yRot * ((float)Math.PI / 180F)) * MathHelper.cos(DolphinEntity.this.xRot * ((float)Math.PI / 180F)) + MathHelper.cos(f1) * f2), (double)(0.3F * MathHelper.sin(DolphinEntity.this.xRot * ((float)Math.PI / 180F)) * 1.5F), (double)(0.3F * MathHelper.cos(DolphinEntity.this.yRot * ((float)Math.PI / 180F)) * MathHelper.cos(DolphinEntity.this.xRot * ((float)Math.PI / 180F)) + MathHelper.sin(f1) * f2));
            DolphinEntity.this.level.addFreshEntity(itementity);
         }
      }
   }

   static class SwimToTreasureGoal extends Goal {
      private final DolphinEntity dolphin;
      private boolean stuck;

      SwimToTreasureGoal(DolphinEntity p_i49344_1_) {
         this.dolphin = p_i49344_1_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean isInterruptable() {
         return false;
      }

      public boolean canUse() {
         return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
      }

      public boolean canContinueToUse() {
         BlockPos blockpos = this.dolphin.getTreasurePos();
         return !(new BlockPos((double)blockpos.getX(), this.dolphin.getY(), (double)blockpos.getZ())).closerThan(this.dolphin.position(), 4.0D) && !this.stuck && this.dolphin.getAirSupply() >= 100;
      }

      public void start() {
         if (this.dolphin.level instanceof ServerWorld) {
            ServerWorld serverworld = (ServerWorld)this.dolphin.level;
            this.stuck = false;
            this.dolphin.getNavigation().stop();
            BlockPos blockpos = this.dolphin.blockPosition();
            Structure<?> structure = (double)serverworld.random.nextFloat() >= 0.5D ? Structure.OCEAN_RUIN : Structure.SHIPWRECK;
            BlockPos blockpos1 = serverworld.findNearestMapFeature(structure, blockpos, 50, false);
            if (blockpos1 == null) {
               Structure<?> structure1 = structure.equals(Structure.OCEAN_RUIN) ? Structure.SHIPWRECK : Structure.OCEAN_RUIN;
               BlockPos blockpos2 = serverworld.findNearestMapFeature(structure1, blockpos, 50, false);
               if (blockpos2 == null) {
                  this.stuck = true;
                  return;
               }

               this.dolphin.setTreasurePos(blockpos2);
            } else {
               this.dolphin.setTreasurePos(blockpos1);
            }

            serverworld.broadcastEntityEvent(this.dolphin, (byte)38);
         }
      }

      public void stop() {
         BlockPos blockpos = this.dolphin.getTreasurePos();
         if ((new BlockPos((double)blockpos.getX(), this.dolphin.getY(), (double)blockpos.getZ())).closerThan(this.dolphin.position(), 4.0D) || this.stuck) {
            this.dolphin.setGotFish(false);
         }

      }

      public void tick() {
         World world = this.dolphin.level;
         if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
            Vector3d vector3d = Vector3d.atCenterOf(this.dolphin.getTreasurePos());
            Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(this.dolphin, 16, 1, vector3d, (double)((float)Math.PI / 8F));
            if (vector3d1 == null) {
               vector3d1 = RandomPositionGenerator.getPosTowards(this.dolphin, 8, 4, vector3d);
            }

            if (vector3d1 != null) {
               BlockPos blockpos = new BlockPos(vector3d1);
               if (!world.getFluidState(blockpos).is(FluidTags.WATER) || !world.getBlockState(blockpos).isPathfindable(world, blockpos, PathType.WATER)) {
                  vector3d1 = RandomPositionGenerator.getPosTowards(this.dolphin, 8, 5, vector3d);
               }
            }

            if (vector3d1 == null) {
               this.stuck = true;
               return;
            }

            this.dolphin.getLookControl().setLookAt(vector3d1.x, vector3d1.y, vector3d1.z, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
            this.dolphin.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, 1.3D);
            if (world.random.nextInt(80) == 0) {
               world.broadcastEntityEvent(this.dolphin, (byte)38);
            }
         }

      }
   }

   static class SwimWithPlayerGoal extends Goal {
      private final DolphinEntity dolphin;
      private final double speedModifier;
      private PlayerEntity player;

      SwimWithPlayerGoal(DolphinEntity p_i48994_1_, double p_i48994_2_) {
         this.dolphin = p_i48994_1_;
         this.speedModifier = p_i48994_2_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         this.player = this.dolphin.level.getNearestPlayer(DolphinEntity.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
         if (this.player == null) {
            return false;
         } else {
            return this.player.isSwimming() && this.dolphin.getTarget() != this.player;
         }
      }

      public boolean canContinueToUse() {
         return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0D;
      }

      public void start() {
         this.player.addEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
      }

      public void stop() {
         this.player = null;
         this.dolphin.getNavigation().stop();
      }

      public void tick() {
         this.dolphin.getLookControl().setLookAt(this.player, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
         if (this.dolphin.distanceToSqr(this.player) < 6.25D) {
            this.dolphin.getNavigation().stop();
         } else {
            this.dolphin.getNavigation().moveTo(this.player, this.speedModifier);
         }

         if (this.player.isSwimming() && this.player.level.random.nextInt(6) == 0) {
            this.player.addEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
         }

      }
   }
}
