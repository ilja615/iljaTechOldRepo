package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.BreakBlockGoal;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class ZombieEntity extends MonsterEntity {
   private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
   private static final DataParameter<Boolean> DATA_BABY_ID = EntityDataManager.defineId(ZombieEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> DATA_SPECIAL_TYPE_ID = EntityDataManager.defineId(ZombieEntity.class, DataSerializers.INT);
   private static final DataParameter<Boolean> DATA_DROWNED_CONVERSION_ID = EntityDataManager.defineId(ZombieEntity.class, DataSerializers.BOOLEAN);
   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (p_213697_0_) -> {
      return p_213697_0_ == Difficulty.HARD;
   };
   private final BreakDoorGoal breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
   private boolean canBreakDoors;
   private int inWaterTime;
   private int conversionTime;

   public ZombieEntity(EntityType<? extends ZombieEntity> p_i48549_1_, World p_i48549_2_) {
      super(p_i48549_1_, p_i48549_2_);
   }

   public ZombieEntity(World p_i1745_1_) {
      this(EntityType.ZOMBIE, p_i1745_1_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new ZombieEntity.AttackTurtleEggGoal(this, 1.0D, 3));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglinEntity.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_BABY_ID, false);
      this.getEntityData().define(DATA_SPECIAL_TYPE_ID, 0);
      this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
   }

   public boolean isUnderWaterConverting() {
      return this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean p_146070_1_) {
      if (this.supportsBreakDoorGoal() && GroundPathHelper.hasGroundPathNavigation(this)) {
         if (this.canBreakDoors != p_146070_1_) {
            this.canBreakDoors = p_146070_1_;
            ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(p_146070_1_);
            if (p_146070_1_) {
               this.goalSelector.addGoal(1, this.breakDoorGoal);
            } else {
               this.goalSelector.removeGoal(this.breakDoorGoal);
            }
         }
      } else if (this.canBreakDoors) {
         this.goalSelector.removeGoal(this.breakDoorGoal);
         this.canBreakDoors = false;
      }

   }

   protected boolean supportsBreakDoorGoal() {
      return true;
   }

   public boolean isBaby() {
      return this.getEntityData().get(DATA_BABY_ID);
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      if (this.isBaby()) {
         this.xpReward = (int)((float)this.xpReward * 2.5F);
      }

      return super.getExperienceReward(p_70693_1_);
   }

   public void setBaby(boolean p_82227_1_) {
      this.getEntityData().set(DATA_BABY_ID, p_82227_1_);
      if (this.level != null && !this.level.isClientSide) {
         ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
         modifiableattributeinstance.removeModifier(SPEED_MODIFIER_BABY);
         if (p_82227_1_) {
            modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_BABY);
         }
      }

   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_BABY_ID.equals(p_184206_1_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   protected boolean convertsInWater() {
      return true;
   }

   public void tick() {
      if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
         if (this.isUnderWaterConverting()) {
            --this.conversionTime;

            if (this.conversionTime < 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.ZOMBIE, (timer) -> this.conversionTime = timer)) {
               this.doUnderWaterConversion();
            }
         } else if (this.convertsInWater()) {
            if (this.isEyeInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.startUnderWaterConversion(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   public void aiStep() {
      if (this.isAlive()) {
         boolean flag = this.isSunSensitive() && this.isSunBurnTick();
         if (flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.HEAD);
            if (!itemstack.isEmpty()) {
               if (itemstack.isDamageableItem()) {
                  itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                  if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                     this.broadcastBreakEvent(EquipmentSlotType.HEAD);
                     this.setItemSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
                  }
               }

               flag = false;
            }

            if (flag) {
               this.setSecondsOnFire(8);
            }
         }
      }

      super.aiStep();
   }

   private void startUnderWaterConversion(int p_204704_1_) {
      this.conversionTime = p_204704_1_;
      this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
   }

   protected void doUnderWaterConversion() {
      this.convertToZombieType(EntityType.DROWNED);
      if (!this.isSilent()) {
         this.level.levelEvent((PlayerEntity)null, 1040, this.blockPosition(), 0);
      }

   }

   protected void convertToZombieType(EntityType<? extends ZombieEntity> p_234341_1_) {
      ZombieEntity zombieentity = this.convertTo(p_234341_1_, true);
      if (zombieentity != null) {
         zombieentity.handleAttributes(zombieentity.level.getCurrentDifficultyAt(zombieentity.blockPosition()).getSpecialMultiplier());
         zombieentity.setCanBreakDoors(zombieentity.supportsBreakDoorGoal() && this.canBreakDoors());
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zombieentity);
      }

   }

   protected boolean isSunSensitive() {
      return true;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (!super.hurt(p_70097_1_, p_70097_2_)) {
         return false;
      } else if (!(this.level instanceof ServerWorld)) {
         return false;
      } else {
         ServerWorld serverworld = (ServerWorld)this.level;
         LivingEntity livingentity = this.getTarget();
         if (livingentity == null && p_70097_1_.getEntity() instanceof LivingEntity) {
            livingentity = (LivingEntity)p_70097_1_.getEntity();
         }

            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());

         net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent event = net.minecraftforge.event.ForgeEventFactory.fireZombieSummonAid(this, level, i, j, k, livingentity, this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).getValue());
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return true;
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW  ||
            livingentity != null && this.level.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).getValue() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            ZombieEntity zombieentity = event.getCustomSummonedAid() != null && event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ? event.getCustomSummonedAid() : EntityType.ZOMBIE.create(this.level);

            for(int l = 0; l < 50; ++l) {
               int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               BlockPos blockpos = new BlockPos(i1, j1, k1);
               EntityType<?> entitytype = zombieentity.getType();
               EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);
               if (WorldEntitySpawner.isSpawnPositionOk(entityspawnplacementregistry$placementtype, this.level, blockpos, entitytype) && EntitySpawnPlacementRegistry.checkSpawnRules(entitytype, serverworld, SpawnReason.REINFORCEMENT, blockpos, this.level.random)) {
                  zombieentity.setPos((double)i1, (double)j1, (double)k1);
                  if (!this.level.hasNearbyAlivePlayer((double)i1, (double)j1, (double)k1, 7.0D) && this.level.isUnobstructed(zombieentity) && this.level.noCollision(zombieentity) && !this.level.containsAnyLiquid(zombieentity.getBoundingBox())) {
                     if (livingentity != null)
                     zombieentity.setTarget(livingentity);
                     zombieentity.finalizeSpawn(serverworld, this.level.getCurrentDifficultyAt(zombieentity.blockPosition()), SpawnReason.REINFORCEMENT, (ILivingEntityData)null, (CompoundNBT)null);
                     serverworld.addFreshEntityWithPassengers(zombieentity);
                     this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement caller charge", (double)-0.05F, AttributeModifier.Operation.ADDITION));
                     zombieentity.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement callee charge", (double)-0.05F, AttributeModifier.Operation.ADDITION));
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      boolean flag = super.doHurtTarget(p_70652_1_);
      if (flag) {
         float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
            p_70652_1_.setSecondsOnFire(2 * (int)f);
         }
      }

      return flag;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.UNDEAD;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      super.populateDefaultEquipmentSlots(p_180481_1_);
      if (this.random.nextFloat() < (this.level.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int i = this.random.nextInt(3);
         if (i == 0) {
            this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("IsBaby", this.isBaby());
      p_213281_1_.putBoolean("CanBreakDoors", this.canBreakDoors());
      p_213281_1_.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      p_213281_1_.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setBaby(p_70037_1_.getBoolean("IsBaby"));
      this.setCanBreakDoors(p_70037_1_.getBoolean("CanBreakDoors"));
      this.inWaterTime = p_70037_1_.getInt("InWaterTime");
      if (p_70037_1_.contains("DrownedConversionTime", 99) && p_70037_1_.getInt("DrownedConversionTime") > -1) {
         this.startUnderWaterConversion(p_70037_1_.getInt("DrownedConversionTime"));
      }

   }

   public void killed(ServerWorld p_241847_1_, LivingEntity p_241847_2_) {
      super.killed(p_241847_1_, p_241847_2_);
      if ((p_241847_1_.getDifficulty() == Difficulty.NORMAL || p_241847_1_.getDifficulty() == Difficulty.HARD) && p_241847_2_ instanceof VillagerEntity && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(p_241847_2_, EntityType.ZOMBIE_VILLAGER, (timer) -> {})) {
         if (p_241847_1_.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return;
         }

         VillagerEntity villagerentity = (VillagerEntity)p_241847_2_;
         ZombieVillagerEntity zombievillagerentity = villagerentity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
         zombievillagerentity.finalizeSpawn(p_241847_1_, p_241847_1_.getCurrentDifficultyAt(zombievillagerentity.blockPosition()), SpawnReason.CONVERSION, new ZombieEntity.GroupData(false, true), (CompoundNBT)null);
         zombievillagerentity.setVillagerData(villagerentity.getVillagerData());
         zombievillagerentity.setGossips(villagerentity.getGossips().store(NBTDynamicOps.INSTANCE).getValue());
         zombievillagerentity.setTradeOffers(villagerentity.getOffers().createTag());
         zombievillagerentity.setVillagerXp(villagerentity.getVillagerXp());
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(p_241847_2_, zombievillagerentity);
         if (!this.isSilent()) {
            p_241847_1_.levelEvent((PlayerEntity)null, 1026, this.blockPosition(), 0);
         }
      }

   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isBaby() ? 0.93F : 1.74F;
   }

   public boolean canHoldItem(ItemStack p_175448_1_) {
      return p_175448_1_.getItem() == Items.EGG && this.isBaby() && this.isPassenger() ? false : super.canHoldItem(p_175448_1_);
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      float f = p_213386_2_.getSpecialMultiplier();
      this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
      if (p_213386_4_ == null) {
         p_213386_4_ = new ZombieEntity.GroupData(getSpawnAsBabyOdds(p_213386_1_.getRandom()), true);
      }

      if (p_213386_4_ instanceof ZombieEntity.GroupData) {
         ZombieEntity.GroupData zombieentity$groupdata = (ZombieEntity.GroupData)p_213386_4_;
         if (zombieentity$groupdata.isBaby) {
            this.setBaby(true);
            if (zombieentity$groupdata.canSpawnJockey) {
               if ((double)p_213386_1_.getRandom().nextFloat() < 0.05D) {
                  List<ChickenEntity> list = p_213386_1_.getEntitiesOfClass(ChickenEntity.class, this.getBoundingBox().inflate(5.0D, 3.0D, 5.0D), EntityPredicates.ENTITY_NOT_BEING_RIDDEN);
                  if (!list.isEmpty()) {
                     ChickenEntity chickenentity = list.get(0);
                     chickenentity.setChickenJockey(true);
                     this.startRiding(chickenentity);
                  }
               } else if ((double)p_213386_1_.getRandom().nextFloat() < 0.05D) {
                  ChickenEntity chickenentity1 = EntityType.CHICKEN.create(this.level);
                  chickenentity1.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
                  chickenentity1.finalizeSpawn(p_213386_1_, p_213386_2_, SpawnReason.JOCKEY, (ILivingEntityData)null, (CompoundNBT)null);
                  chickenentity1.setChickenJockey(true);
                  this.startRiding(chickenentity1);
                  p_213386_1_.addFreshEntity(chickenentity1);
               }
            }
         }

         this.setCanBreakDoors(this.supportsBreakDoorGoal() && this.random.nextFloat() < f * 0.1F);
         this.populateDefaultEquipmentSlots(p_213386_2_);
         this.populateDefaultEquipmentEnchantments(p_213386_2_);
      }

      if (this.getItemBySlot(EquipmentSlotType.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlotType.HEAD.getIndex()] = 0.0F;
         }
      }

      this.handleAttributes(f);
      return p_213386_4_;
   }

   public static boolean getSpawnAsBabyOdds(Random p_241399_0_) {
      return p_241399_0_.nextFloat() < net.minecraftforge.common.ForgeConfig.SERVER.zombieBabyChance.get();
   }

   protected void handleAttributes(float p_207304_1_) {
      this.randomizeReinforcementsChance();
      this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * (double)0.05F, AttributeModifier.Operation.ADDITION));
      double d0 = this.random.nextDouble() * 1.5D * (double)p_207304_1_;
      if (d0 > 1.0D) {
         this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
      }

      if (this.random.nextFloat() < p_207304_1_ * 0.05F) {
         this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
         this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
         this.setCanBreakDoors(this.supportsBreakDoorGoal());
      }

   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * net.minecraftforge.common.ForgeConfig.SERVER.zombieBaseSummonChance.get());
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? 0.0D : -0.45D;
   }

   protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
      Entity entity = p_213333_1_.getEntity();
      if (entity instanceof CreeperEntity) {
         CreeperEntity creeperentity = (CreeperEntity)entity;
         if (creeperentity.canDropMobsSkull()) {
            ItemStack itemstack = this.getSkull();
            if (!itemstack.isEmpty()) {
               creeperentity.increaseDroppedSkulls();
               this.spawnAtLocation(itemstack);
            }
         }
      }

   }

   protected ItemStack getSkull() {
      return new ItemStack(Items.ZOMBIE_HEAD);
   }

   class AttackTurtleEggGoal extends BreakBlockGoal {
      AttackTurtleEggGoal(CreatureEntity p_i50465_2_, double p_i50465_3_, int p_i50465_5_) {
         super(Blocks.TURTLE_EGG, p_i50465_2_, p_i50465_3_, p_i50465_5_);
      }

      public void playDestroyProgressSound(IWorld p_203114_1_, BlockPos p_203114_2_) {
         p_203114_1_.playSound((PlayerEntity)null, p_203114_2_, SoundEvents.ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieEntity.this.random.nextFloat() * 0.2F);
      }

      public void playBreakSound(World p_203116_1_, BlockPos p_203116_2_) {
         p_203116_1_.playSound((PlayerEntity)null, p_203116_2_, SoundEvents.TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_203116_1_.random.nextFloat() * 0.2F);
      }

      public double acceptedDistance() {
         return 1.14D;
      }
   }

   public static class GroupData implements ILivingEntityData {
      public final boolean isBaby;
      public final boolean canSpawnJockey;

      public GroupData(boolean p_i231567_1_, boolean p_i231567_2_) {
         this.isBaby = p_i231567_1_;
         this.canSpawnJockey = p_i231567_2_;
      }
   }
}
