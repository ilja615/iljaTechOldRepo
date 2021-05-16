package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageAtNightGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoxEntity extends AnimalEntity {
   private static final DataParameter<Integer> DATA_TYPE_ID = EntityDataManager.defineId(FoxEntity.class, DataSerializers.INT);
   private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(FoxEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Optional<UUID>> DATA_TRUSTED_ID_0 = EntityDataManager.defineId(FoxEntity.class, DataSerializers.OPTIONAL_UUID);
   private static final DataParameter<Optional<UUID>> DATA_TRUSTED_ID_1 = EntityDataManager.defineId(FoxEntity.class, DataSerializers.OPTIONAL_UUID);
   private static final Predicate<ItemEntity> ALLOWED_ITEMS = (p_213489_0_) -> {
      return !p_213489_0_.hasPickUpDelay() && p_213489_0_.isAlive();
   };
   private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = (p_213470_0_) -> {
      if (!(p_213470_0_ instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity livingentity = (LivingEntity)p_213470_0_;
         return livingentity.getLastHurtMob() != null && livingentity.getLastHurtMobTimestamp() < livingentity.tickCount + 600;
      }
   };
   private static final Predicate<Entity> STALKABLE_PREY = (p_213498_0_) -> {
      return p_213498_0_ instanceof ChickenEntity || p_213498_0_ instanceof RabbitEntity;
   };
   private static final Predicate<Entity> AVOID_PLAYERS = (p_213463_0_) -> {
      return !p_213463_0_.isDiscrete() && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(p_213463_0_);
   };
   private Goal landTargetGoal;
   private Goal turtleEggTargetGoal;
   private Goal fishTargetGoal;
   private float interestedAngle;
   private float interestedAngleO;
   private float crouchAmount;
   private float crouchAmountO;
   private int ticksSinceEaten;

   public FoxEntity(EntityType<? extends FoxEntity> p_i50271_1_, World p_i50271_2_) {
      super(p_i50271_1_, p_i50271_2_);
      this.lookControl = new FoxEntity.LookHelperController();
      this.moveControl = new FoxEntity.MoveHelperController();
      this.setPathfindingMalus(PathNodeType.DANGER_OTHER, 0.0F);
      this.setPathfindingMalus(PathNodeType.DAMAGE_OTHER, 0.0F);
      this.setCanPickUpLoot(true);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TRUSTED_ID_0, Optional.empty());
      this.entityData.define(DATA_TRUSTED_ID_1, Optional.empty());
      this.entityData.define(DATA_TYPE_ID, 0);
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   protected void registerGoals() {
      this.landTargetGoal = new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, false, false, (p_213487_0_) -> {
         return p_213487_0_ instanceof ChickenEntity || p_213487_0_ instanceof RabbitEntity;
      });
      this.turtleEggTargetGoal = new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_ON_LAND_SELECTOR);
      this.fishTargetGoal = new NearestAttackableTargetGoal<>(this, AbstractFishEntity.class, 20, false, false, (p_213456_0_) -> {
         return p_213456_0_ instanceof AbstractGroupFishEntity;
      });
      this.goalSelector.addGoal(0, new FoxEntity.SwimGoal());
      this.goalSelector.addGoal(1, new FoxEntity.JumpGoal());
      this.goalSelector.addGoal(2, new FoxEntity.PanicGoal(2.2D));
      this.goalSelector.addGoal(3, new FoxEntity.MateGoal(1.0D));
      this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.6D, 1.4D, (p_213497_1_) -> {
         return AVOID_PLAYERS.test(p_213497_1_) && !this.trusts(p_213497_1_.getUUID()) && !this.isDefending();
      }));
      this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, WolfEntity.class, 8.0F, 1.6D, 1.4D, (p_213469_1_) -> {
         return !((WolfEntity)p_213469_1_).isTame() && !this.isDefending();
      }));
      this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PolarBearEntity.class, 8.0F, 1.6D, 1.4D, (p_213493_1_) -> {
         return !this.isDefending();
      }));
      this.goalSelector.addGoal(5, new FoxEntity.FollowTargetGoal());
      this.goalSelector.addGoal(6, new FoxEntity.PounceGoal());
      this.goalSelector.addGoal(6, new FoxEntity.FindShelterGoal(1.25D));
      this.goalSelector.addGoal(7, new FoxEntity.BiteGoal((double)1.2F, true));
      this.goalSelector.addGoal(7, new FoxEntity.SleepGoal());
      this.goalSelector.addGoal(8, new FoxEntity.FollowGoal(this, 1.25D));
      this.goalSelector.addGoal(9, new FoxEntity.StrollGoal(32, 200));
      this.goalSelector.addGoal(10, new FoxEntity.EatBerriesGoal((double)1.2F, 12, 2));
      this.goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(11, new FoxEntity.FindItemsGoal());
      this.goalSelector.addGoal(12, new FoxEntity.WatchGoal(this, PlayerEntity.class, 24.0F));
      this.goalSelector.addGoal(13, new FoxEntity.SitAndLookGoal());
      this.targetSelector.addGoal(3, new FoxEntity.RevengeGoal(LivingEntity.class, false, false, (p_234193_1_) -> {
         return TRUSTED_TARGET_SELECTOR.test(p_234193_1_) && !this.trusts(p_234193_1_.getUUID());
      }));
   }

   public SoundEvent getEatingSound(ItemStack p_213353_1_) {
      return SoundEvents.FOX_EAT;
   }

   public void aiStep() {
      if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
         ++this.ticksSinceEaten;
         ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
         if (this.canEat(itemstack)) {
            if (this.ticksSinceEaten > 600) {
               ItemStack itemstack1 = itemstack.finishUsingItem(this.level, this);
               if (!itemstack1.isEmpty()) {
                  this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack1);
               }

               this.ticksSinceEaten = 0;
            } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
               this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
               this.level.broadcastEntityEvent(this, (byte)45);
            }
         }

         LivingEntity livingentity = this.getTarget();
         if (livingentity == null || !livingentity.isAlive()) {
            this.setIsCrouching(false);
            this.setIsInterested(false);
         }
      }

      if (this.isSleeping() || this.isImmobile()) {
         this.jumping = false;
         this.xxa = 0.0F;
         this.zza = 0.0F;
      }

      super.aiStep();
      if (this.isDefending() && this.random.nextFloat() < 0.05F) {
         this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
      }

   }

   protected boolean isImmobile() {
      return this.isDeadOrDying();
   }

   private boolean canEat(ItemStack p_213464_1_) {
      return p_213464_1_.getItem().isEdible() && this.getTarget() == null && this.onGround && !this.isSleeping();
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      if (this.random.nextFloat() < 0.2F) {
         float f = this.random.nextFloat();
         ItemStack itemstack;
         if (f < 0.05F) {
            itemstack = new ItemStack(Items.EMERALD);
         } else if (f < 0.2F) {
            itemstack = new ItemStack(Items.EGG);
         } else if (f < 0.4F) {
            itemstack = this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
         } else if (f < 0.6F) {
            itemstack = new ItemStack(Items.WHEAT);
         } else if (f < 0.8F) {
            itemstack = new ItemStack(Items.LEATHER);
         } else {
            itemstack = new ItemStack(Items.FEATHER);
         }

         this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 45) {
         ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
         if (!itemstack.isEmpty()) {
            for(int i = 0; i < 8; ++i) {
               Vector3d vector3d = (new Vector3d(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.xRot * ((float)Math.PI / 180F)).yRot(-this.yRot * ((float)Math.PI / 180F));
               this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vector3d.x, vector3d.y + 0.05D, vector3d.z);
            }
         }
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
   }

   public FoxEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      FoxEntity foxentity = EntityType.FOX.create(p_241840_1_);
      foxentity.setFoxType(this.random.nextBoolean() ? this.getFoxType() : ((FoxEntity)p_241840_2_).getFoxType());
      return foxentity;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      Optional<RegistryKey<Biome>> optional = p_213386_1_.getBiomeName(this.blockPosition());
      FoxEntity.Type foxentity$type = FoxEntity.Type.byBiome(optional);
      boolean flag = false;
      if (p_213386_4_ instanceof FoxEntity.FoxData) {
         foxentity$type = ((FoxEntity.FoxData)p_213386_4_).type;
         if (((FoxEntity.FoxData)p_213386_4_).getGroupSize() >= 2) {
            flag = true;
         }
      } else {
         p_213386_4_ = new FoxEntity.FoxData(foxentity$type);
      }

      this.setFoxType(foxentity$type);
      if (flag) {
         this.setAge(-24000);
      }

      if (p_213386_1_ instanceof ServerWorld) {
         this.setTargetGoals();
      }

      this.populateDefaultEquipmentSlots(p_213386_2_);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   private void setTargetGoals() {
      if (this.getFoxType() == FoxEntity.Type.RED) {
         this.targetSelector.addGoal(4, this.landTargetGoal);
         this.targetSelector.addGoal(4, this.turtleEggTargetGoal);
         this.targetSelector.addGoal(6, this.fishTargetGoal);
      } else {
         this.targetSelector.addGoal(4, this.fishTargetGoal);
         this.targetSelector.addGoal(6, this.landTargetGoal);
         this.targetSelector.addGoal(6, this.turtleEggTargetGoal);
      }

   }

   protected void usePlayerItem(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
      if (this.isFood(p_175505_2_)) {
         this.playSound(this.getEatingSound(p_175505_2_), 1.0F, 1.0F);
      }

      super.usePlayerItem(p_175505_1_, p_175505_2_);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isBaby() ? p_213348_2_.height * 0.85F : 0.4F;
   }

   public FoxEntity.Type getFoxType() {
      return FoxEntity.Type.byId(this.entityData.get(DATA_TYPE_ID));
   }

   private void setFoxType(FoxEntity.Type p_213474_1_) {
      this.entityData.set(DATA_TYPE_ID, p_213474_1_.getId());
   }

   private List<UUID> getTrustedUUIDs() {
      List<UUID> list = Lists.newArrayList();
      list.add(this.entityData.get(DATA_TRUSTED_ID_0).orElse((UUID)null));
      list.add(this.entityData.get(DATA_TRUSTED_ID_1).orElse((UUID)null));
      return list;
   }

   private void addTrustedUUID(@Nullable UUID p_213465_1_) {
      if (this.entityData.get(DATA_TRUSTED_ID_0).isPresent()) {
         this.entityData.set(DATA_TRUSTED_ID_1, Optional.ofNullable(p_213465_1_));
      } else {
         this.entityData.set(DATA_TRUSTED_ID_0, Optional.ofNullable(p_213465_1_));
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      List<UUID> list = this.getTrustedUUIDs();
      ListNBT listnbt = new ListNBT();

      for(UUID uuid : list) {
         if (uuid != null) {
            listnbt.add(NBTUtil.createUUID(uuid));
         }
      }

      p_213281_1_.put("Trusted", listnbt);
      p_213281_1_.putBoolean("Sleeping", this.isSleeping());
      p_213281_1_.putString("Type", this.getFoxType().getName());
      p_213281_1_.putBoolean("Sitting", this.isSitting());
      p_213281_1_.putBoolean("Crouching", this.isCrouching());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      ListNBT listnbt = p_70037_1_.getList("Trusted", 11);

      for(int i = 0; i < listnbt.size(); ++i) {
         this.addTrustedUUID(NBTUtil.loadUUID(listnbt.get(i)));
      }

      this.setSleeping(p_70037_1_.getBoolean("Sleeping"));
      this.setFoxType(FoxEntity.Type.byName(p_70037_1_.getString("Type")));
      this.setSitting(p_70037_1_.getBoolean("Sitting"));
      this.setIsCrouching(p_70037_1_.getBoolean("Crouching"));
      if (this.level instanceof ServerWorld) {
         this.setTargetGoals();
      }

   }

   public boolean isSitting() {
      return this.getFlag(1);
   }

   public void setSitting(boolean p_213466_1_) {
      this.setFlag(1, p_213466_1_);
   }

   public boolean isFaceplanted() {
      return this.getFlag(64);
   }

   private void setFaceplanted(boolean p_213492_1_) {
      this.setFlag(64, p_213492_1_);
   }

   private boolean isDefending() {
      return this.getFlag(128);
   }

   private void setDefending(boolean p_213482_1_) {
      this.setFlag(128, p_213482_1_);
   }

   public boolean isSleeping() {
      return this.getFlag(32);
   }

   private void setSleeping(boolean p_213485_1_) {
      this.setFlag(32, p_213485_1_);
   }

   private void setFlag(int p_213505_1_, boolean p_213505_2_) {
      if (p_213505_2_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | p_213505_1_));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~p_213505_1_));
      }

   }

   private boolean getFlag(int p_213507_1_) {
      return (this.entityData.get(DATA_FLAGS_ID) & p_213507_1_) != 0;
   }

   public boolean canTakeItem(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(p_213365_1_);
      if (!this.getItemBySlot(equipmentslottype).isEmpty()) {
         return false;
      } else {
         return equipmentslottype == EquipmentSlotType.MAINHAND && super.canTakeItem(p_213365_1_);
      }
   }

   public boolean canHoldItem(ItemStack p_175448_1_) {
      Item item = p_175448_1_.getItem();
      ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
      return itemstack.isEmpty() || this.ticksSinceEaten > 0 && item.isEdible() && !itemstack.getItem().isEdible();
   }

   private void spitOutItem(ItemStack p_213495_1_) {
      if (!p_213495_1_.isEmpty() && !this.level.isClientSide) {
         ItemEntity itementity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, p_213495_1_);
         itementity.setPickUpDelay(40);
         itementity.setThrower(this.getUUID());
         this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
         this.level.addFreshEntity(itementity);
      }
   }

   private void dropItemStack(ItemStack p_213486_1_) {
      ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), p_213486_1_);
      this.level.addFreshEntity(itementity);
   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      if (this.canHoldItem(itemstack)) {
         int i = itemstack.getCount();
         if (i > 1) {
            this.dropItemStack(itemstack.split(i - 1));
         }

         this.spitOutItem(this.getItemBySlot(EquipmentSlotType.MAINHAND));
         this.onItemPickup(p_175445_1_);
         this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack.split(1));
         this.handDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
         this.take(p_175445_1_, itemstack.getCount());
         p_175445_1_.remove();
         this.ticksSinceEaten = 0;
      }

   }

   public void tick() {
      super.tick();
      if (this.isEffectiveAi()) {
         boolean flag = this.isInWater();
         if (flag || this.getTarget() != null || this.level.isThundering()) {
            this.wakeUp();
         }

         if (flag || this.isSleeping()) {
            this.setSitting(false);
         }

         if (this.isFaceplanted() && this.level.random.nextFloat() < 0.2F) {
            BlockPos blockpos = this.blockPosition();
            BlockState blockstate = this.level.getBlockState(blockpos);
            this.level.levelEvent(2001, blockpos, Block.getId(blockstate));
         }
      }

      this.interestedAngleO = this.interestedAngle;
      if (this.isInterested()) {
         this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
      } else {
         this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
      }

      this.crouchAmountO = this.crouchAmount;
      if (this.isCrouching()) {
         this.crouchAmount += 0.2F;
         if (this.crouchAmount > 3.0F) {
            this.crouchAmount = 3.0F;
         }
      } else {
         this.crouchAmount = 0.0F;
      }

   }

   public boolean isFood(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Items.SWEET_BERRIES;
   }

   protected void onOffspringSpawnedFromEgg(PlayerEntity p_213406_1_, MobEntity p_213406_2_) {
      ((FoxEntity)p_213406_2_).addTrustedUUID(p_213406_1_.getUUID());
   }

   public boolean isPouncing() {
      return this.getFlag(16);
   }

   public void setIsPouncing(boolean p_213461_1_) {
      this.setFlag(16, p_213461_1_);
   }

   public boolean isFullyCrouched() {
      return this.crouchAmount == 3.0F;
   }

   public void setIsCrouching(boolean p_213451_1_) {
      this.setFlag(4, p_213451_1_);
   }

   public boolean isCrouching() {
      return this.getFlag(4);
   }

   public void setIsInterested(boolean p_213502_1_) {
      this.setFlag(8, p_213502_1_);
   }

   public boolean isInterested() {
      return this.getFlag(8);
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadRollAngle(float p_213475_1_) {
      return MathHelper.lerp(p_213475_1_, this.interestedAngleO, this.interestedAngle) * 0.11F * (float)Math.PI;
   }

   @OnlyIn(Dist.CLIENT)
   public float getCrouchAmount(float p_213503_1_) {
      return MathHelper.lerp(p_213503_1_, this.crouchAmountO, this.crouchAmount);
   }

   public void setTarget(@Nullable LivingEntity p_70624_1_) {
      if (this.isDefending() && p_70624_1_ == null) {
         this.setDefending(false);
      }

      super.setTarget(p_70624_1_);
   }

   protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
      return MathHelper.ceil((p_225508_1_ - 5.0F) * p_225508_2_);
   }

   private void wakeUp() {
      this.setSleeping(false);
   }

   private void clearStates() {
      this.setIsInterested(false);
      this.setIsCrouching(false);
      this.setSitting(false);
      this.setSleeping(false);
      this.setDefending(false);
      this.setFaceplanted(false);
   }

   private boolean canMove() {
      return !this.isSleeping() && !this.isSitting() && !this.isFaceplanted();
   }

   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent == SoundEvents.FOX_SCREECH) {
         this.playSound(soundevent, 2.0F, this.getVoicePitch());
      } else {
         super.playAmbientSound();
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return SoundEvents.FOX_SLEEP;
      } else {
         if (!this.level.isDay() && this.random.nextFloat() < 0.1F) {
            List<PlayerEntity> list = this.level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(16.0D, 16.0D, 16.0D), EntityPredicates.NO_SPECTATORS);
            if (list.isEmpty()) {
               return SoundEvents.FOX_SCREECH;
            }
         }

         return SoundEvents.FOX_AMBIENT;
      }
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.FOX_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.FOX_DEATH;
   }

   private boolean trusts(UUID p_213468_1_) {
      return this.getTrustedUUIDs().contains(p_213468_1_);
   }

   protected void dropAllDeathLoot(DamageSource p_213345_1_) {
      ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
      if (!itemstack.isEmpty()) {
         this.spawnAtLocation(itemstack);
         this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
      }

      super.dropAllDeathLoot(p_213345_1_);
   }

   public static boolean isPathClear(FoxEntity p_213481_0_, LivingEntity p_213481_1_) {
      double d0 = p_213481_1_.getZ() - p_213481_0_.getZ();
      double d1 = p_213481_1_.getX() - p_213481_0_.getX();
      double d2 = d0 / d1;
      int i = 6;

      for(int j = 0; j < 6; ++j) {
         double d3 = d2 == 0.0D ? 0.0D : d0 * (double)((float)j / 6.0F);
         double d4 = d2 == 0.0D ? d1 * (double)((float)j / 6.0F) : d3 / d2;

         for(int k = 1; k < 4; ++k) {
            if (!p_213481_0_.level.getBlockState(new BlockPos(p_213481_0_.getX() + d4, p_213481_0_.getY() + (double)k, p_213481_0_.getZ() + d3)).getMaterial().isReplaceable()) {
               return false;
            }
         }
      }

      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getLeashOffset() {
      return new Vector3d(0.0D, (double)(0.55F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   public class AlertablePredicate implements Predicate<LivingEntity> {
      public boolean test(LivingEntity p_test_1_) {
         if (p_test_1_ instanceof FoxEntity) {
            return false;
         } else if (!(p_test_1_ instanceof ChickenEntity) && !(p_test_1_ instanceof RabbitEntity) && !(p_test_1_ instanceof MonsterEntity)) {
            if (p_test_1_ instanceof TameableEntity) {
               return !((TameableEntity)p_test_1_).isTame();
            } else if (!(p_test_1_ instanceof PlayerEntity) || !p_test_1_.isSpectator() && !((PlayerEntity)p_test_1_).isCreative()) {
               if (FoxEntity.this.trusts(p_test_1_.getUUID())) {
                  return false;
               } else {
                  return !p_test_1_.isSleeping() && !p_test_1_.isDiscrete();
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   abstract class BaseGoal extends Goal {
      private final EntityPredicate alertableTargeting = (new EntityPredicate()).range(12.0D).allowUnseeable().selector(FoxEntity.this.new AlertablePredicate());

      private BaseGoal() {
      }

      protected boolean hasShelter() {
         BlockPos blockpos = new BlockPos(FoxEntity.this.getX(), FoxEntity.this.getBoundingBox().maxY, FoxEntity.this.getZ());
         return !FoxEntity.this.level.canSeeSky(blockpos) && FoxEntity.this.getWalkTargetValue(blockpos) >= 0.0F;
      }

      protected boolean alertable() {
         return !FoxEntity.this.level.getNearbyEntities(LivingEntity.class, this.alertableTargeting, FoxEntity.this, FoxEntity.this.getBoundingBox().inflate(12.0D, 6.0D, 12.0D)).isEmpty();
      }
   }

   class BiteGoal extends MeleeAttackGoal {
      public BiteGoal(double p_i50731_2_, boolean p_i50731_4_) {
         super(FoxEntity.this, p_i50731_2_, p_i50731_4_);
      }

      protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
         double d0 = this.getAttackReachSqr(p_190102_1_);
         if (p_190102_2_ <= d0 && this.isTimeToAttack()) {
            this.resetAttackCooldown();
            this.mob.doHurtTarget(p_190102_1_);
            FoxEntity.this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.0F);
         }

      }

      public void start() {
         FoxEntity.this.setIsInterested(false);
         super.start();
      }

      public boolean canUse() {
         return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isFaceplanted() && super.canUse();
      }
   }

   public class EatBerriesGoal extends MoveToBlockGoal {
      protected int ticksWaited;

      public EatBerriesGoal(double p_i50737_2_, int p_i50737_4_, int p_i50737_5_) {
         super(FoxEntity.this, p_i50737_2_, p_i50737_4_, p_i50737_5_);
      }

      public double acceptedDistance() {
         return 2.0D;
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 100 == 0;
      }

      protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         BlockState blockstate = p_179488_1_.getBlockState(p_179488_2_);
         return blockstate.is(Blocks.SWEET_BERRY_BUSH) && blockstate.getValue(SweetBerryBushBlock.AGE) >= 2;
      }

      public void tick() {
         if (this.isReachedTarget()) {
            if (this.ticksWaited >= 40) {
               this.onReachedTarget();
            } else {
               ++this.ticksWaited;
            }
         } else if (!this.isReachedTarget() && FoxEntity.this.random.nextFloat() < 0.05F) {
            FoxEntity.this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
         }

         super.tick();
      }

      protected void onReachedTarget() {
         if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(FoxEntity.this.level, FoxEntity.this)) {
            BlockState blockstate = FoxEntity.this.level.getBlockState(this.blockPos);
            if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
               int i = blockstate.getValue(SweetBerryBushBlock.AGE);
               blockstate.setValue(SweetBerryBushBlock.AGE, Integer.valueOf(1));
               int j = 1 + FoxEntity.this.level.random.nextInt(2) + (i == 3 ? 1 : 0);
               ItemStack itemstack = FoxEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND);
               if (itemstack.isEmpty()) {
                  FoxEntity.this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                  --j;
               }

               if (j > 0) {
                  Block.popResource(FoxEntity.this.level, this.blockPos, new ItemStack(Items.SWEET_BERRIES, j));
               }

               FoxEntity.this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
               FoxEntity.this.level.setBlock(this.blockPos, blockstate.setValue(SweetBerryBushBlock.AGE, Integer.valueOf(1)), 2);
            }
         }
      }

      public boolean canUse() {
         return !FoxEntity.this.isSleeping() && super.canUse();
      }

      public void start() {
         this.ticksWaited = 0;
         FoxEntity.this.setSitting(false);
         super.start();
      }
   }

   class FindItemsGoal extends Goal {
      public FindItemsGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (!FoxEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            return false;
         } else if (FoxEntity.this.getTarget() == null && FoxEntity.this.getLastHurtByMob() == null) {
            if (!FoxEntity.this.canMove()) {
               return false;
            } else if (FoxEntity.this.getRandom().nextInt(10) != 0) {
               return false;
            } else {
               List<ItemEntity> list = FoxEntity.this.level.getEntitiesOfClass(ItemEntity.class, FoxEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), FoxEntity.ALLOWED_ITEMS);
               return !list.isEmpty() && FoxEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty();
            }
         } else {
            return false;
         }
      }

      public void tick() {
         List<ItemEntity> list = FoxEntity.this.level.getEntitiesOfClass(ItemEntity.class, FoxEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), FoxEntity.ALLOWED_ITEMS);
         ItemStack itemstack = FoxEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND);
         if (itemstack.isEmpty() && !list.isEmpty()) {
            FoxEntity.this.getNavigation().moveTo(list.get(0), (double)1.2F);
         }

      }

      public void start() {
         List<ItemEntity> list = FoxEntity.this.level.getEntitiesOfClass(ItemEntity.class, FoxEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), FoxEntity.ALLOWED_ITEMS);
         if (!list.isEmpty()) {
            FoxEntity.this.getNavigation().moveTo(list.get(0), (double)1.2F);
         }

      }
   }

   class FindShelterGoal extends FleeSunGoal {
      private int interval = 100;

      public FindShelterGoal(double p_i50724_2_) {
         super(FoxEntity.this, p_i50724_2_);
      }

      public boolean canUse() {
         if (!FoxEntity.this.isSleeping() && this.mob.getTarget() == null) {
            if (FoxEntity.this.level.isThundering()) {
               return true;
            } else if (this.interval > 0) {
               --this.interval;
               return false;
            } else {
               this.interval = 100;
               BlockPos blockpos = this.mob.blockPosition();
               return FoxEntity.this.level.isDay() && FoxEntity.this.level.canSeeSky(blockpos) && !((ServerWorld)FoxEntity.this.level).isVillage(blockpos) && this.setWantedPos();
            }
         } else {
            return false;
         }
      }

      public void start() {
         FoxEntity.this.clearStates();
         super.start();
      }
   }

   class FollowGoal extends FollowParentGoal {
      private final FoxEntity fox;

      public FollowGoal(FoxEntity p_i50735_2_, double p_i50735_3_) {
         super(p_i50735_2_, p_i50735_3_);
         this.fox = p_i50735_2_;
      }

      public boolean canUse() {
         return !this.fox.isDefending() && super.canUse();
      }

      public boolean canContinueToUse() {
         return !this.fox.isDefending() && super.canContinueToUse();
      }

      public void start() {
         this.fox.clearStates();
         super.start();
      }
   }

   class FollowTargetGoal extends Goal {
      public FollowTargetGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         if (FoxEntity.this.isSleeping()) {
            return false;
         } else {
            LivingEntity livingentity = FoxEntity.this.getTarget();
            return livingentity != null && livingentity.isAlive() && FoxEntity.STALKABLE_PREY.test(livingentity) && FoxEntity.this.distanceToSqr(livingentity) > 36.0D && !FoxEntity.this.isCrouching() && !FoxEntity.this.isInterested() && !FoxEntity.this.jumping;
         }
      }

      public void start() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.setFaceplanted(false);
      }

      public void stop() {
         LivingEntity livingentity = FoxEntity.this.getTarget();
         if (livingentity != null && FoxEntity.isPathClear(FoxEntity.this, livingentity)) {
            FoxEntity.this.setIsInterested(true);
            FoxEntity.this.setIsCrouching(true);
            FoxEntity.this.getNavigation().stop();
            FoxEntity.this.getLookControl().setLookAt(livingentity, (float)FoxEntity.this.getMaxHeadYRot(), (float)FoxEntity.this.getMaxHeadXRot());
         } else {
            FoxEntity.this.setIsInterested(false);
            FoxEntity.this.setIsCrouching(false);
         }

      }

      public void tick() {
         LivingEntity livingentity = FoxEntity.this.getTarget();
         FoxEntity.this.getLookControl().setLookAt(livingentity, (float)FoxEntity.this.getMaxHeadYRot(), (float)FoxEntity.this.getMaxHeadXRot());
         if (FoxEntity.this.distanceToSqr(livingentity) <= 36.0D) {
            FoxEntity.this.setIsInterested(true);
            FoxEntity.this.setIsCrouching(true);
            FoxEntity.this.getNavigation().stop();
         } else {
            FoxEntity.this.getNavigation().moveTo(livingentity, 1.5D);
         }

      }
   }

   public static class FoxData extends AgeableEntity.AgeableData {
      public final FoxEntity.Type type;

      public FoxData(FoxEntity.Type p_i50734_1_) {
         super(false);
         this.type = p_i50734_1_;
      }
   }

   class JumpGoal extends Goal {
      int countdown;

      public JumpGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return FoxEntity.this.isFaceplanted();
      }

      public boolean canContinueToUse() {
         return this.canUse() && this.countdown > 0;
      }

      public void start() {
         this.countdown = 40;
      }

      public void stop() {
         FoxEntity.this.setFaceplanted(false);
      }

      public void tick() {
         --this.countdown;
      }
   }

   public class LookHelperController extends LookController {
      public LookHelperController() {
         super(FoxEntity.this);
      }

      public void tick() {
         if (!FoxEntity.this.isSleeping()) {
            super.tick();
         }

      }

      protected boolean resetXRotOnTick() {
         return !FoxEntity.this.isPouncing() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isInterested() & !FoxEntity.this.isFaceplanted();
      }
   }

   class MateGoal extends BreedGoal {
      public MateGoal(double p_i50738_2_) {
         super(FoxEntity.this, p_i50738_2_);
      }

      public void start() {
         ((FoxEntity)this.animal).clearStates();
         ((FoxEntity)this.partner).clearStates();
         super.start();
      }

      protected void breed() {
         ServerWorld serverworld = (ServerWorld)this.level;
         FoxEntity foxentity = (FoxEntity)this.animal.getBreedOffspring(serverworld, this.partner);
         final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(animal, partner, foxentity);
         final boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
         foxentity = (FoxEntity) event.getChild();
         if (cancelled) {
            //Reset the "inLove" state for the animals
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            return;
         }
         if (foxentity != null) {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            ServerPlayerEntity serverplayerentity1 = this.partner.getLoveCause();
            ServerPlayerEntity serverplayerentity2 = serverplayerentity;
            if (serverplayerentity != null) {
               foxentity.addTrustedUUID(serverplayerentity.getUUID());
            } else {
               serverplayerentity2 = serverplayerentity1;
            }

            if (serverplayerentity1 != null && serverplayerentity != serverplayerentity1) {
               foxentity.addTrustedUUID(serverplayerentity1.getUUID());
            }

            if (serverplayerentity2 != null) {
               serverplayerentity2.awardStat(Stats.ANIMALS_BRED);
               CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity2, this.animal, this.partner, foxentity);
            }

            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            foxentity.setAge(-24000);
            foxentity.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
            serverworld.addFreshEntityWithPassengers(foxentity);
            this.level.broadcastEntityEvent(this.animal, (byte)18);
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
               this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
            }

         }
      }
   }

   class MoveHelperController extends MovementController {
      public MoveHelperController() {
         super(FoxEntity.this);
      }

      public void tick() {
         if (FoxEntity.this.canMove()) {
            super.tick();
         }

      }
   }

   class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      public PanicGoal(double p_i50729_2_) {
         super(FoxEntity.this, p_i50729_2_);
      }

      public boolean canUse() {
         return !FoxEntity.this.isDefending() && super.canUse();
      }
   }

   public class PounceGoal extends net.minecraft.entity.ai.goal.JumpGoal {
      public boolean canUse() {
         if (!FoxEntity.this.isFullyCrouched()) {
            return false;
         } else {
            LivingEntity livingentity = FoxEntity.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
               if (livingentity.getMotionDirection() != livingentity.getDirection()) {
                  return false;
               } else {
                  boolean flag = FoxEntity.isPathClear(FoxEntity.this, livingentity);
                  if (!flag) {
                     FoxEntity.this.getNavigation().createPath(livingentity, 0);
                     FoxEntity.this.setIsCrouching(false);
                     FoxEntity.this.setIsInterested(false);
                  }

                  return flag;
               }
            } else {
               return false;
            }
         }
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = FoxEntity.this.getTarget();
         if (livingentity != null && livingentity.isAlive()) {
            double d0 = FoxEntity.this.getDeltaMovement().y;
            return (!(d0 * d0 < (double)0.05F) || !(Math.abs(FoxEntity.this.xRot) < 15.0F) || !FoxEntity.this.onGround) && !FoxEntity.this.isFaceplanted();
         } else {
            return false;
         }
      }

      public boolean isInterruptable() {
         return false;
      }

      public void start() {
         FoxEntity.this.setJumping(true);
         FoxEntity.this.setIsPouncing(true);
         FoxEntity.this.setIsInterested(false);
         LivingEntity livingentity = FoxEntity.this.getTarget();
         FoxEntity.this.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
         Vector3d vector3d = (new Vector3d(livingentity.getX() - FoxEntity.this.getX(), livingentity.getY() - FoxEntity.this.getY(), livingentity.getZ() - FoxEntity.this.getZ())).normalize();
         FoxEntity.this.setDeltaMovement(FoxEntity.this.getDeltaMovement().add(vector3d.x * 0.8D, 0.9D, vector3d.z * 0.8D));
         FoxEntity.this.getNavigation().stop();
      }

      public void stop() {
         FoxEntity.this.setIsCrouching(false);
         FoxEntity.this.crouchAmount = 0.0F;
         FoxEntity.this.crouchAmountO = 0.0F;
         FoxEntity.this.setIsInterested(false);
         FoxEntity.this.setIsPouncing(false);
      }

      public void tick() {
         LivingEntity livingentity = FoxEntity.this.getTarget();
         if (livingentity != null) {
            FoxEntity.this.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
         }

         if (!FoxEntity.this.isFaceplanted()) {
            Vector3d vector3d = FoxEntity.this.getDeltaMovement();
            if (vector3d.y * vector3d.y < (double)0.03F && FoxEntity.this.xRot != 0.0F) {
               FoxEntity.this.xRot = MathHelper.rotlerp(FoxEntity.this.xRot, 0.0F, 0.2F);
            } else {
               double d0 = Math.sqrt(Entity.getHorizontalDistanceSqr(vector3d));
               double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double)(180F / (float)Math.PI);
               FoxEntity.this.xRot = (float)d1;
            }
         }

         if (livingentity != null && FoxEntity.this.distanceTo(livingentity) <= 2.0F) {
            FoxEntity.this.doHurtTarget(livingentity);
         } else if (FoxEntity.this.xRot > 0.0F && FoxEntity.this.onGround && (float)FoxEntity.this.getDeltaMovement().y != 0.0F && FoxEntity.this.level.getBlockState(FoxEntity.this.blockPosition()).is(Blocks.SNOW)) {
            FoxEntity.this.xRot = 60.0F;
            FoxEntity.this.setTarget((LivingEntity)null);
            FoxEntity.this.setFaceplanted(true);
         }

      }
   }

   class RevengeGoal extends NearestAttackableTargetGoal<LivingEntity> {
      @Nullable
      private LivingEntity trustedLastHurtBy;
      private LivingEntity trustedLastHurt;
      private int timestamp;

      public RevengeGoal(Class<LivingEntity> p_i50743_2_, boolean p_i50743_3_, boolean p_i50743_4_, @Nullable Predicate<LivingEntity> p_i50743_5_) {
         super(FoxEntity.this, p_i50743_2_, 10, p_i50743_3_, p_i50743_4_, p_i50743_5_);
      }

      public boolean canUse() {
         if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
         } else {
            for(UUID uuid : FoxEntity.this.getTrustedUUIDs()) {
               if (uuid != null && FoxEntity.this.level instanceof ServerWorld) {
                  Entity entity = ((ServerWorld)FoxEntity.this.level).getEntity(uuid);
                  if (entity instanceof LivingEntity) {
                     LivingEntity livingentity = (LivingEntity)entity;
                     this.trustedLastHurt = livingentity;
                     this.trustedLastHurtBy = livingentity.getLastHurtByMob();
                     int i = livingentity.getLastHurtByMobTimestamp();
                     return i != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions);
                  }
               }
            }

            return false;
         }
      }

      public void start() {
         this.setTarget(this.trustedLastHurtBy);
         this.target = this.trustedLastHurtBy;
         if (this.trustedLastHurt != null) {
            this.timestamp = this.trustedLastHurt.getLastHurtByMobTimestamp();
         }

         FoxEntity.this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
         FoxEntity.this.setDefending(true);
         FoxEntity.this.wakeUp();
         super.start();
      }
   }

   class SitAndLookGoal extends FoxEntity.BaseGoal {
      private double relX;
      private double relZ;
      private int lookTime;
      private int looksRemaining;

      public SitAndLookGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return FoxEntity.this.getLastHurtByMob() == null && FoxEntity.this.getRandom().nextFloat() < 0.02F && !FoxEntity.this.isSleeping() && FoxEntity.this.getTarget() == null && FoxEntity.this.getNavigation().isDone() && !this.alertable() && !FoxEntity.this.isPouncing() && !FoxEntity.this.isCrouching();
      }

      public boolean canContinueToUse() {
         return this.looksRemaining > 0;
      }

      public void start() {
         this.resetLook();
         this.looksRemaining = 2 + FoxEntity.this.getRandom().nextInt(3);
         FoxEntity.this.setSitting(true);
         FoxEntity.this.getNavigation().stop();
      }

      public void stop() {
         FoxEntity.this.setSitting(false);
      }

      public void tick() {
         --this.lookTime;
         if (this.lookTime <= 0) {
            --this.looksRemaining;
            this.resetLook();
         }

         FoxEntity.this.getLookControl().setLookAt(FoxEntity.this.getX() + this.relX, FoxEntity.this.getEyeY(), FoxEntity.this.getZ() + this.relZ, (float)FoxEntity.this.getMaxHeadYRot(), (float)FoxEntity.this.getMaxHeadXRot());
      }

      private void resetLook() {
         double d0 = (Math.PI * 2D) * FoxEntity.this.getRandom().nextDouble();
         this.relX = Math.cos(d0);
         this.relZ = Math.sin(d0);
         this.lookTime = 80 + FoxEntity.this.getRandom().nextInt(20);
      }
   }

   class SleepGoal extends FoxEntity.BaseGoal {
      private int countdown = FoxEntity.this.random.nextInt(140);

      public SleepGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      public boolean canUse() {
         if (FoxEntity.this.xxa == 0.0F && FoxEntity.this.yya == 0.0F && FoxEntity.this.zza == 0.0F) {
            return this.canSleep() || FoxEntity.this.isSleeping();
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.canSleep();
      }

      private boolean canSleep() {
         if (this.countdown > 0) {
            --this.countdown;
            return false;
         } else {
            return FoxEntity.this.level.isDay() && this.hasShelter() && !this.alertable();
         }
      }

      public void stop() {
         this.countdown = FoxEntity.this.random.nextInt(140);
         FoxEntity.this.clearStates();
      }

      public void start() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.setIsCrouching(false);
         FoxEntity.this.setIsInterested(false);
         FoxEntity.this.setJumping(false);
         FoxEntity.this.setSleeping(true);
         FoxEntity.this.getNavigation().stop();
         FoxEntity.this.getMoveControl().setWantedPosition(FoxEntity.this.getX(), FoxEntity.this.getY(), FoxEntity.this.getZ(), 0.0D);
      }
   }

   class StrollGoal extends MoveThroughVillageAtNightGoal {
      public StrollGoal(int p_i50726_2_, int p_i50726_3_) {
         super(FoxEntity.this, p_i50726_3_);
      }

      public void start() {
         FoxEntity.this.clearStates();
         super.start();
      }

      public boolean canUse() {
         return super.canUse() && this.canFoxMove();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.canFoxMove();
      }

      private boolean canFoxMove() {
         return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isDefending() && FoxEntity.this.getTarget() == null;
      }
   }

   class SwimGoal extends net.minecraft.entity.ai.goal.SwimGoal {
      public SwimGoal() {
         super(FoxEntity.this);
      }

      public void start() {
         super.start();
         FoxEntity.this.clearStates();
      }

      public boolean canUse() {
         return FoxEntity.this.isInWater() && FoxEntity.this.getFluidHeight(FluidTags.WATER) > 0.25D || FoxEntity.this.isInLava();
      }
   }

   public static enum Type {
      RED(0, "red", Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS),
      SNOW(1, "snow", Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS);

      private static final FoxEntity.Type[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(FoxEntity.Type::getId)).toArray((p_221084_0_) -> {
         return new FoxEntity.Type[p_221084_0_];
      });
      private static final Map<String, FoxEntity.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(FoxEntity.Type::getName, (p_221081_0_) -> {
         return p_221081_0_;
      }));
      private final int id;
      private final String name;
      private final List<RegistryKey<Biome>> biomes;

      private Type(int p_i241911_3_, String p_i241911_4_, RegistryKey<Biome>... p_i241911_5_) {
         this.id = p_i241911_3_;
         this.name = p_i241911_4_;
         this.biomes = Arrays.asList(p_i241911_5_);
      }

      public String getName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      public static FoxEntity.Type byName(String p_221087_0_) {
         return BY_NAME.getOrDefault(p_221087_0_, RED);
      }

      public static FoxEntity.Type byId(int p_221080_0_) {
         if (p_221080_0_ < 0 || p_221080_0_ > BY_ID.length) {
            p_221080_0_ = 0;
         }

         return BY_ID[p_221080_0_];
      }

      public static FoxEntity.Type byBiome(Optional<RegistryKey<Biome>> p_242325_0_) {
         return p_242325_0_.isPresent() && SNOW.biomes.contains(p_242325_0_.get()) ? SNOW : RED;
      }
   }

   class WatchGoal extends LookAtGoal {
      public WatchGoal(MobEntity p_i50733_2_, Class<? extends LivingEntity> p_i50733_3_, float p_i50733_4_) {
         super(p_i50733_2_, p_i50733_3_, p_i50733_4_);
      }

      public boolean canUse() {
         return super.canUse() && !FoxEntity.this.isFaceplanted() && !FoxEntity.this.isInterested();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && !FoxEntity.this.isFaceplanted() && !FoxEntity.this.isInterested();
      }
   }
}
