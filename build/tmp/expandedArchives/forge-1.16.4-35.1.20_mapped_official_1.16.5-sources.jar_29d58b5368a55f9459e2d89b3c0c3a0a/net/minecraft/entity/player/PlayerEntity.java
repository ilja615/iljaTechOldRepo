package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class PlayerEntity extends LivingEntity {
   public static final String PERSISTED_NBT_TAG = "PlayerPersisted";
   public static final EntitySize STANDING_DIMENSIONS = EntitySize.scalable(0.6F, 1.8F);
   private static final Map<Pose, EntitySize> POSES = ImmutableMap.<Pose, EntitySize>builder().put(Pose.STANDING, STANDING_DIMENSIONS).put(Pose.SLEEPING, SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntitySize.scalable(0.6F, 0.6F)).put(Pose.SWIMMING, EntitySize.scalable(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntitySize.scalable(0.6F, 0.6F)).put(Pose.CROUCHING, EntitySize.scalable(0.6F, 1.5F)).put(Pose.DYING, EntitySize.fixed(0.2F, 0.2F)).build();
   private static final DataParameter<Float> DATA_PLAYER_ABSORPTION_ID = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> DATA_SCORE_ID = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);
   protected static final DataParameter<Byte> DATA_PLAYER_MODE_CUSTOMISATION = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Byte> DATA_PLAYER_MAIN_HAND = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<CompoundNBT> DATA_SHOULDER_LEFT = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.COMPOUND_TAG);
   protected static final DataParameter<CompoundNBT> DATA_SHOULDER_RIGHT = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.COMPOUND_TAG);
   private long timeEntitySatOnShoulder;
   public final PlayerInventory inventory = new PlayerInventory(this);
   protected EnderChestInventory enderChestInventory = new EnderChestInventory();
   public final PlayerContainer inventoryMenu;
   public Container containerMenu;
   protected FoodStats foodData = new FoodStats();
   protected int jumpTriggerTime;
   public float oBob;
   public float bob;
   public int takeXpDelay;
   public double xCloakO;
   public double yCloakO;
   public double zCloakO;
   public double xCloak;
   public double yCloak;
   public double zCloak;
   private int sleepCounter;
   protected boolean wasUnderwater;
   public final PlayerAbilities abilities = new PlayerAbilities();
   public int experienceLevel;
   public int totalExperience;
   public float experienceProgress;
   protected int enchantmentSeed;
   protected final float defaultFlySpeed = 0.02F;
   private int lastLevelUpTime;
   private final GameProfile gameProfile;
   @OnlyIn(Dist.CLIENT)
   private boolean reducedDebugInfo;
   private ItemStack lastItemInMainHand = ItemStack.EMPTY;
   private final CooldownTracker cooldowns = this.createItemCooldowns();
   @Nullable
   public FishingBobberEntity fishing;
   private final java.util.Collection<IFormattableTextComponent> prefixes = new java.util.LinkedList<>();
   private final java.util.Collection<IFormattableTextComponent> suffixes = new java.util.LinkedList<>();
   @Nullable private Pose forcedPose;

   public PlayerEntity(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
      super(EntityType.PLAYER, p_i241920_1_);
      this.setUUID(createPlayerUUID(p_i241920_4_));
      this.gameProfile = p_i241920_4_;
      this.inventoryMenu = new PlayerContainer(this.inventory, !p_i241920_1_.isClientSide, this);
      this.containerMenu = this.inventoryMenu;
      this.moveTo((double)p_i241920_2_.getX() + 0.5D, (double)(p_i241920_2_.getY() + 1), (double)p_i241920_2_.getZ() + 0.5D, p_i241920_3_, 0.0F);
      this.rotOffs = 180.0F;
   }

   public boolean blockActionRestricted(World p_223729_1_, BlockPos p_223729_2_, GameType p_223729_3_) {
      if (!p_223729_3_.isBlockPlacingRestricted()) {
         return false;
      } else if (p_223729_3_ == GameType.SPECTATOR) {
         return true;
      } else if (this.mayBuild()) {
         return false;
      } else {
         ItemStack itemstack = this.getMainHandItem();
         return itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(p_223729_1_.getTagManager(), new CachedBlockInfo(p_223729_1_, p_223729_2_, false));
      }
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, (double)0.1F).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK).add(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_PLAYER_ABSORPTION_ID, 0.0F);
      this.entityData.define(DATA_SCORE_ID, 0);
      this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
      this.entityData.define(DATA_PLAYER_MAIN_HAND, (byte)1);
      this.entityData.define(DATA_SHOULDER_LEFT, new CompoundNBT());
      this.entityData.define(DATA_SHOULDER_RIGHT, new CompoundNBT());
   }

   public void tick() {
      net.minecraftforge.fml.hooks.BasicEventHooks.onPlayerPreTick(this);
      this.noPhysics = this.isSpectator();
      if (this.isSpectator()) {
         this.onGround = false;
      }

      if (this.takeXpDelay > 0) {
         --this.takeXpDelay;
      }

      if (this.isSleeping()) {
         ++this.sleepCounter;
         if (this.sleepCounter > 100) {
            this.sleepCounter = 100;
         }

         if (!this.level.isClientSide && !net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(this, getSleepingPos())) {
            this.stopSleepInBed(false, true);
         }
      } else if (this.sleepCounter > 0) {
         ++this.sleepCounter;
         if (this.sleepCounter >= 110) {
            this.sleepCounter = 0;
         }
      }

      this.updateIsUnderwater();
      super.tick();
      if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      this.moveCloak();
      if (!this.level.isClientSide) {
         this.foodData.tick(this);
         this.awardStat(Stats.PLAY_ONE_MINUTE);
         if (this.isAlive()) {
            this.awardStat(Stats.TIME_SINCE_DEATH);
         }

         if (this.isDiscrete()) {
            this.awardStat(Stats.CROUCH_TIME);
         }

         if (!this.isSleeping()) {
            this.awardStat(Stats.TIME_SINCE_REST);
         }
      }

      int i = 29999999;
      double d0 = MathHelper.clamp(this.getX(), -2.9999999E7D, 2.9999999E7D);
      double d1 = MathHelper.clamp(this.getZ(), -2.9999999E7D, 2.9999999E7D);
      if (d0 != this.getX() || d1 != this.getZ()) {
         this.setPos(d0, this.getY(), d1);
      }

      ++this.attackStrengthTicker;
      ItemStack itemstack = this.getMainHandItem();
      if (!ItemStack.matches(this.lastItemInMainHand, itemstack)) {
         if (!ItemStack.isSameIgnoreDurability(this.lastItemInMainHand, itemstack)) {
            this.resetAttackStrengthTicker();
         }

         this.lastItemInMainHand = itemstack.copy();
      }

      this.turtleHelmetTick();
      this.cooldowns.tick();
      this.updatePlayerPose();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPlayerPostTick(this);
   }

   public boolean isSecondaryUseActive() {
      return this.isShiftKeyDown();
   }

   protected boolean wantsToStopRiding() {
      return this.isShiftKeyDown();
   }

   protected boolean isStayingOnGroundSurface() {
      return this.isShiftKeyDown();
   }

   protected boolean updateIsUnderwater() {
      this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
      return this.wasUnderwater;
   }

   private void turtleHelmetTick() {
      ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.HEAD);
      if (itemstack.getItem() == Items.TURTLE_HELMET && !this.isEyeInFluid(FluidTags.WATER)) {
         this.addEffect(new EffectInstance(Effects.WATER_BREATHING, 200, 0, false, false, true));
      }

   }

   protected CooldownTracker createItemCooldowns() {
      return new CooldownTracker();
   }

   private void moveCloak() {
      this.xCloakO = this.xCloak;
      this.yCloakO = this.yCloak;
      this.zCloakO = this.zCloak;
      double d0 = this.getX() - this.xCloak;
      double d1 = this.getY() - this.yCloak;
      double d2 = this.getZ() - this.zCloak;
      double d3 = 10.0D;
      if (d0 > 10.0D) {
         this.xCloak = this.getX();
         this.xCloakO = this.xCloak;
      }

      if (d2 > 10.0D) {
         this.zCloak = this.getZ();
         this.zCloakO = this.zCloak;
      }

      if (d1 > 10.0D) {
         this.yCloak = this.getY();
         this.yCloakO = this.yCloak;
      }

      if (d0 < -10.0D) {
         this.xCloak = this.getX();
         this.xCloakO = this.xCloak;
      }

      if (d2 < -10.0D) {
         this.zCloak = this.getZ();
         this.zCloakO = this.zCloak;
      }

      if (d1 < -10.0D) {
         this.yCloak = this.getY();
         this.yCloakO = this.yCloak;
      }

      this.xCloak += d0 * 0.25D;
      this.zCloak += d2 * 0.25D;
      this.yCloak += d1 * 0.25D;
   }

   protected void updatePlayerPose() {
      if(forcedPose != null) {
         this.setPose(forcedPose);
         return;
      }
      if (this.canEnterPose(Pose.SWIMMING)) {
         Pose pose;
         if (this.isFallFlying()) {
            pose = Pose.FALL_FLYING;
         } else if (this.isSleeping()) {
            pose = Pose.SLEEPING;
         } else if (this.isSwimming()) {
            pose = Pose.SWIMMING;
         } else if (this.isAutoSpinAttack()) {
            pose = Pose.SPIN_ATTACK;
         } else if (this.isShiftKeyDown() && !this.abilities.flying) {
            pose = Pose.CROUCHING;
         } else {
            pose = Pose.STANDING;
         }

         Pose pose1;
         if (!this.isSpectator() && !this.isPassenger() && !this.canEnterPose(pose)) {
            if (this.canEnterPose(Pose.CROUCHING)) {
               pose1 = Pose.CROUCHING;
            } else {
               pose1 = Pose.SWIMMING;
            }
         } else {
            pose1 = pose;
         }

         this.setPose(pose1);
      }
   }

   public int getPortalWaitTime() {
      return this.abilities.invulnerable ? 1 : 80;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.PLAYER_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.PLAYER_SPLASH;
   }

   protected SoundEvent getSwimHighSpeedSplashSound() {
      return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
   }

   public int getDimensionChangingDelay() {
      return 10;
   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      this.level.playSound(this, this.getX(), this.getY(), this.getZ(), p_184185_1_, this.getSoundSource(), p_184185_2_, p_184185_3_);
   }

   public void playNotifySound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.PLAYERS;
   }

   protected int getFireImmuneTicks() {
      return 20;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 9) {
         this.completeUsingItem();
      } else if (p_70103_1_ == 23) {
         this.reducedDebugInfo = false;
      } else if (p_70103_1_ == 22) {
         this.reducedDebugInfo = true;
      } else if (p_70103_1_ == 43) {
         this.addParticlesAroundSelf(ParticleTypes.CLOUD);
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   private void addParticlesAroundSelf(IParticleData p_213824_1_) {
      for(int i = 0; i < 5; ++i) {
         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(p_213824_1_, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   public void closeContainer() {
      this.containerMenu = this.inventoryMenu;
   }

   public void rideTick() {
      if (this.wantsToStopRiding() && this.isPassenger()) {
         this.stopRiding();
         this.setShiftKeyDown(false);
      } else {
         double d0 = this.getX();
         double d1 = this.getY();
         double d2 = this.getZ();
         super.rideTick();
         this.oBob = this.bob;
         this.bob = 0.0F;
         this.checkRidingStatistics(this.getX() - d0, this.getY() - d1, this.getZ() - d2);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void resetPos() {
      this.setPose(Pose.STANDING);
      super.resetPos();
      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   protected void serverAiStep() {
      super.serverAiStep();
      this.updateSwingTime();
      this.yHeadRot = this.yRot;
   }

   public void aiStep() {
      if (this.jumpTriggerTime > 0) {
         --this.jumpTriggerTime;
      }

      if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
         if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
            this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
         }
      }

      this.inventory.tick();
      this.oBob = this.bob;
      super.aiStep();
      this.flyingSpeed = 0.02F;
      if (this.isSprinting()) {
         this.flyingSpeed = (float)((double)this.flyingSpeed + 0.005999999865889549D);
      }

      this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
      float f;
      if (this.onGround && !this.isDeadOrDying() && !this.isSwimming()) {
         f = Math.min(0.1F, MathHelper.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())));
      } else {
         f = 0.0F;
      }

      this.bob += (f - this.bob) * 0.4F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AxisAlignedBB axisalignedbb;
         if (this.isPassenger() && !this.getVehicle().removed) {
            axisalignedbb = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0D, 0.0D, 1.0D);
         } else {
            axisalignedbb = this.getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
         }

         List<Entity> list = this.level.getEntities(this, axisalignedbb);

         for(int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!entity.removed) {
               this.touch(entity);
            }
         }
      }

      this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
      this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
      if (!this.level.isClientSide && (this.fallDistance > 0.5F || this.isInWater()) || this.abilities.flying || this.isSleeping()) {
         this.removeEntitiesOnShoulder();
      }

   }

   private void playShoulderEntityAmbientSound(@Nullable CompoundNBT p_192028_1_) {
      if (p_192028_1_ != null && (!p_192028_1_.contains("Silent") || !p_192028_1_.getBoolean("Silent")) && this.level.random.nextInt(200) == 0) {
         String s = p_192028_1_.getString("id");
         EntityType.byString(s).filter((p_213830_0_) -> {
            return p_213830_0_ == EntityType.PARROT;
         }).ifPresent((p_213834_1_) -> {
            if (!ParrotEntity.imitateNearbyMobs(this.level, this)) {
               this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), ParrotEntity.getAmbient(this.level, this.level.random), this.getSoundSource(), 1.0F, ParrotEntity.getPitch(this.level.random));
            }

         });
      }

   }

   private void touch(Entity p_71044_1_) {
      p_71044_1_.playerTouch(this);
   }

   public int getScore() {
      return this.entityData.get(DATA_SCORE_ID);
   }

   public void setScore(int p_85040_1_) {
      this.entityData.set(DATA_SCORE_ID, p_85040_1_);
   }

   public void increaseScore(int p_85039_1_) {
      int i = this.getScore();
      this.entityData.set(DATA_SCORE_ID, i + p_85039_1_);
   }

   public void die(DamageSource p_70645_1_) {
      if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this,  p_70645_1_)) return;
      super.die(p_70645_1_);
      this.reapplyPosition();
      if (!this.isSpectator()) {
         this.dropAllDeathLoot(p_70645_1_);
      }

      if (p_70645_1_ != null) {
         this.setDeltaMovement((double)(-MathHelper.cos((this.hurtDir + this.yRot) * ((float)Math.PI / 180F)) * 0.1F), (double)0.1F, (double)(-MathHelper.sin((this.hurtDir + this.yRot) * ((float)Math.PI / 180F)) * 0.1F));
      } else {
         this.setDeltaMovement(0.0D, 0.1D, 0.0D);
      }

      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlag(0, false);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
         this.destroyVanishingCursedItems();
         this.inventory.dropAll();
      }

   }

   protected void destroyVanishingCursedItems() {
      for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
         ItemStack itemstack = this.inventory.getItem(i);
         if (!itemstack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemstack)) {
            this.inventory.removeItemNoUpdate(i);
         }
      }

   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      if (p_184601_1_ == DamageSource.ON_FIRE) {
         return SoundEvents.PLAYER_HURT_ON_FIRE;
      } else if (p_184601_1_ == DamageSource.DROWN) {
         return SoundEvents.PLAYER_HURT_DROWN;
      } else {
         return p_184601_1_ == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.PLAYER_HURT;
      }
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   public boolean drop(boolean p_225609_1_) {
      ItemStack stack = inventory.getSelected();
      if (stack.isEmpty() || !stack.onDroppedByPlayer(this)) return false;
      return net.minecraftforge.common.ForgeHooks.onPlayerTossEvent(this, this.inventory.removeItem(this.inventory.selected, p_225609_1_ && !this.inventory.getSelected().isEmpty() ? this.inventory.getSelected().getCount() : 1), true) != null;
   }

   @Nullable
   public ItemEntity drop(ItemStack p_71019_1_, boolean p_71019_2_) {
      return net.minecraftforge.common.ForgeHooks.onPlayerTossEvent(this, p_71019_1_, false);
   }

   @Nullable
   public ItemEntity drop(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_) {
      if (p_146097_1_.isEmpty()) {
         return null;
      } else {
         if (this.level.isClientSide) {
            this.swing(Hand.MAIN_HAND);
         }

         double d0 = this.getEyeY() - (double)0.3F;
         ItemEntity itementity = new ItemEntity(this.level, this.getX(), d0, this.getZ(), p_146097_1_);
         itementity.setPickUpDelay(40);
         if (p_146097_3_) {
            itementity.setThrower(this.getUUID());
         }

         if (p_146097_2_) {
            float f = this.random.nextFloat() * 0.5F;
            float f1 = this.random.nextFloat() * ((float)Math.PI * 2F);
            itementity.setDeltaMovement((double)(-MathHelper.sin(f1) * f), (double)0.2F, (double)(MathHelper.cos(f1) * f));
         } else {
            float f7 = 0.3F;
            float f8 = MathHelper.sin(this.xRot * ((float)Math.PI / 180F));
            float f2 = MathHelper.cos(this.xRot * ((float)Math.PI / 180F));
            float f3 = MathHelper.sin(this.yRot * ((float)Math.PI / 180F));
            float f4 = MathHelper.cos(this.yRot * ((float)Math.PI / 180F));
            float f5 = this.random.nextFloat() * ((float)Math.PI * 2F);
            float f6 = 0.02F * this.random.nextFloat();
            itementity.setDeltaMovement((double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
         }

         return itementity;
      }
   }

   @Deprecated //Use location sensitive version below
   public float getDestroySpeed(BlockState p_184813_1_) {
      return getDigSpeed(p_184813_1_, null);
   }

   public float getDigSpeed(BlockState p_184813_1_, @Nullable BlockPos pos) {
      float f = this.inventory.getDestroySpeed(p_184813_1_);
      if (f > 1.0F) {
         int i = EnchantmentHelper.getBlockEfficiency(this);
         ItemStack itemstack = this.getMainHandItem();
         if (i > 0 && !itemstack.isEmpty()) {
            f += (float)(i * i + 1);
         }
      }

      if (EffectUtils.hasDigSpeed(this)) {
         f *= 1.0F + (float)(EffectUtils.getDigSpeedAmplification(this) + 1) * 0.2F;
      }

      if (this.hasEffect(Effects.DIG_SLOWDOWN)) {
         float f1;
         switch(this.getEffect(Effects.DIG_SLOWDOWN).getAmplifier()) {
         case 0:
            f1 = 0.3F;
            break;
         case 1:
            f1 = 0.09F;
            break;
         case 2:
            f1 = 0.0027F;
            break;
         case 3:
         default:
            f1 = 8.1E-4F;
         }

         f *= f1;
      }

      if (this.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
         f /= 5.0F;
      }

      if (!this.onGround) {
         f /= 5.0F;
      }

      f = net.minecraftforge.event.ForgeEventFactory.getBreakSpeed(this, p_184813_1_, f, pos);
      return f;
   }

   public boolean hasCorrectToolForDrops(BlockState p_234569_1_) {
      return net.minecraftforge.event.ForgeEventFactory.doPlayerHarvestCheck(this, p_234569_1_, !p_234569_1_.requiresCorrectToolForDrops() || this.inventory.getSelected().isCorrectToolForDrops(p_234569_1_));
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setUUID(createPlayerUUID(this.gameProfile));
      ListNBT listnbt = p_70037_1_.getList("Inventory", 10);
      this.inventory.load(listnbt);
      this.inventory.selected = p_70037_1_.getInt("SelectedItemSlot");
      this.sleepCounter = p_70037_1_.getShort("SleepTimer");
      this.experienceProgress = p_70037_1_.getFloat("XpP");
      this.experienceLevel = p_70037_1_.getInt("XpLevel");
      this.totalExperience = p_70037_1_.getInt("XpTotal");
      this.enchantmentSeed = p_70037_1_.getInt("XpSeed");
      if (this.enchantmentSeed == 0) {
         this.enchantmentSeed = this.random.nextInt();
      }

      this.setScore(p_70037_1_.getInt("Score"));
      this.foodData.readAdditionalSaveData(p_70037_1_);
      this.abilities.loadSaveData(p_70037_1_);
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)this.abilities.getWalkingSpeed());
      if (p_70037_1_.contains("EnderItems", 9)) {
         this.enderChestInventory.fromTag(p_70037_1_.getList("EnderItems", 10));
      }

      if (p_70037_1_.contains("ShoulderEntityLeft", 10)) {
         this.setShoulderEntityLeft(p_70037_1_.getCompound("ShoulderEntityLeft"));
      }

      if (p_70037_1_.contains("ShoulderEntityRight", 10)) {
         this.setShoulderEntityRight(p_70037_1_.getCompound("ShoulderEntityRight"));
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      p_213281_1_.put("Inventory", this.inventory.save(new ListNBT()));
      p_213281_1_.putInt("SelectedItemSlot", this.inventory.selected);
      p_213281_1_.putShort("SleepTimer", (short)this.sleepCounter);
      p_213281_1_.putFloat("XpP", this.experienceProgress);
      p_213281_1_.putInt("XpLevel", this.experienceLevel);
      p_213281_1_.putInt("XpTotal", this.totalExperience);
      p_213281_1_.putInt("XpSeed", this.enchantmentSeed);
      p_213281_1_.putInt("Score", this.getScore());
      this.foodData.addAdditionalSaveData(p_213281_1_);
      this.abilities.addSaveData(p_213281_1_);
      p_213281_1_.put("EnderItems", this.enderChestInventory.createTag());
      if (!this.getShoulderEntityLeft().isEmpty()) {
         p_213281_1_.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
      }

      if (!this.getShoulderEntityRight().isEmpty()) {
         p_213281_1_.put("ShoulderEntityRight", this.getShoulderEntityRight());
      }

   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      if (super.isInvulnerableTo(p_180431_1_)) {
         return true;
      } else if (p_180431_1_ == DamageSource.DROWN) {
         return !this.level.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
      } else if (p_180431_1_ == DamageSource.FALL) {
         return !this.level.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
      } else if (p_180431_1_.isFire()) {
         return !this.level.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
      } else {
         return false;
      }
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (!net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, p_70097_1_, p_70097_2_)) return false;
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (this.abilities.invulnerable && !p_70097_1_.isBypassInvul()) {
         return false;
      } else {
         this.noActionTime = 0;
         if (this.isDeadOrDying()) {
            return false;
         } else {
            this.removeEntitiesOnShoulder();
            if (p_70097_1_.scalesWithDifficulty()) {
               if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                  p_70097_2_ = 0.0F;
               }

               if (this.level.getDifficulty() == Difficulty.EASY) {
                  p_70097_2_ = Math.min(p_70097_2_ / 2.0F + 1.0F, p_70097_2_);
               }

               if (this.level.getDifficulty() == Difficulty.HARD) {
                  p_70097_2_ = p_70097_2_ * 3.0F / 2.0F;
               }
            }

            return p_70097_2_ == 0.0F ? false : super.hurt(p_70097_1_, p_70097_2_);
         }
      }
   }

   protected void blockUsingShield(LivingEntity p_190629_1_) {
      super.blockUsingShield(p_190629_1_);
      if (p_190629_1_.getMainHandItem().canDisableShield(this.useItem, this, p_190629_1_)) {
         this.disableShield(true);
      }

   }

   public boolean canHarmPlayer(PlayerEntity p_96122_1_) {
      Team team = this.getTeam();
      Team team1 = p_96122_1_.getTeam();
      if (team == null) {
         return true;
      } else {
         return !team.isAlliedTo(team1) ? true : team.isAllowFriendlyFire();
      }
   }

   protected void hurtArmor(DamageSource p_230294_1_, float p_230294_2_) {
      this.inventory.hurtArmor(p_230294_1_, p_230294_2_);
   }

   protected void hurtCurrentlyUsedShield(float p_184590_1_) {
      if (this.useItem.isShield(this)) {
         if (!this.level.isClientSide) {
            this.awardStat(Stats.ITEM_USED.get(this.useItem.getItem()));
         }

         if (p_184590_1_ >= 3.0F) {
            int i = 1 + MathHelper.floor(p_184590_1_);
            Hand hand = this.getUsedItemHand();
            this.useItem.hurtAndBreak(i, this, (p_213833_1_) -> {
               p_213833_1_.broadcastBreakEvent(hand);
               net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, this.useItem, hand);
            });
            if (this.useItem.isEmpty()) {
               if (hand == Hand.MAIN_HAND) {
                  this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
               } else {
                  this.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
               }

               this.useItem = ItemStack.EMPTY;
               this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
            }
         }

      }
   }

   protected void actuallyHurt(DamageSource p_70665_1_, float p_70665_2_) {
      if (!this.isInvulnerableTo(p_70665_1_)) {
         p_70665_2_ = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, p_70665_1_, p_70665_2_);
         if (p_70665_2_ <= 0) return;
         p_70665_2_ = this.getDamageAfterArmorAbsorb(p_70665_1_, p_70665_2_);
         p_70665_2_ = this.getDamageAfterMagicAbsorb(p_70665_1_, p_70665_2_);
         float f2 = Math.max(p_70665_2_ - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (p_70665_2_ - f2));
         f2 = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, p_70665_1_, f2);
         float f = p_70665_2_ - f2;
         if (f > 0.0F && f < 3.4028235E37F) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(f * 10.0F));
         }

         if (f2 != 0.0F) {
            this.causeFoodExhaustion(p_70665_1_.getFoodExhaustion());
            float f1 = this.getHealth();
            this.setHealth(this.getHealth() - f2);
            this.getCombatTracker().recordDamage(p_70665_1_, f1, f2);
            if (f2 < 3.4028235E37F) {
               this.awardStat(Stats.DAMAGE_TAKEN, Math.round(f2 * 10.0F));
            }

         }
      }
   }

   protected boolean onSoulSpeedBlock() {
      return !this.abilities.flying && super.onSoulSpeedBlock();
   }

   public void openTextEdit(SignTileEntity p_175141_1_) {
   }

   public void openMinecartCommandBlock(CommandBlockLogic p_184809_1_) {
   }

   public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
   }

   public void openStructureBlock(StructureBlockTileEntity p_189807_1_) {
   }

   public void openJigsawBlock(JigsawTileEntity p_213826_1_) {
   }

   public void openHorseInventory(AbstractHorseEntity p_184826_1_, IInventory p_184826_2_) {
   }

   public OptionalInt openMenu(@Nullable INamedContainerProvider p_213829_1_) {
      return OptionalInt.empty();
   }

   public void sendMerchantOffers(int p_213818_1_, MerchantOffers p_213818_2_, int p_213818_3_, int p_213818_4_, boolean p_213818_5_, boolean p_213818_6_) {
   }

   public void openItemGui(ItemStack p_184814_1_, Hand p_184814_2_) {
   }

   public ActionResultType interactOn(Entity p_190775_1_, Hand p_190775_2_) {
      if (this.isSpectator()) {
         if (p_190775_1_ instanceof INamedContainerProvider) {
            this.openMenu((INamedContainerProvider)p_190775_1_);
         }

         return ActionResultType.PASS;
      } else {
         ActionResultType cancelResult = net.minecraftforge.common.ForgeHooks.onInteractEntity(this, p_190775_1_, p_190775_2_);
         if (cancelResult != null) return cancelResult;
         ItemStack itemstack = this.getItemInHand(p_190775_2_);
         ItemStack itemstack1 = itemstack.copy();
         ActionResultType actionresulttype = p_190775_1_.interact(this, p_190775_2_);
         if (actionresulttype.consumesAction()) {
            if (this.abilities.instabuild && itemstack == this.getItemInHand(p_190775_2_) && itemstack.getCount() < itemstack1.getCount()) {
               itemstack.setCount(itemstack1.getCount());
            }

            if (!this.abilities.instabuild && itemstack.isEmpty()) {
               net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, itemstack1, p_190775_2_);
            }
            return actionresulttype;
         } else {
            if (!itemstack.isEmpty() && p_190775_1_ instanceof LivingEntity) {
               if (this.abilities.instabuild) {
                  itemstack = itemstack1;
               }

               ActionResultType actionresulttype1 = itemstack.interactLivingEntity(this, (LivingEntity)p_190775_1_, p_190775_2_);
               if (actionresulttype1.consumesAction()) {
                  if (itemstack.isEmpty() && !this.abilities.instabuild) {
                     net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, itemstack1, p_190775_2_);
                     this.setItemInHand(p_190775_2_, ItemStack.EMPTY);
                  }

                  return actionresulttype1;
               }
            }

            return ActionResultType.PASS;
         }
      }
   }

   public double getMyRidingOffset() {
      return -0.35D;
   }

   public void removeVehicle() {
      super.removeVehicle();
      this.boardingCooldown = 0;
   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.isSleeping();
   }

   public boolean isAffectedByFluids() {
      return !this.abilities.flying;
   }

   protected Vector3d maybeBackOffFromEdge(Vector3d p_225514_1_, MoverType p_225514_2_) {
      if (!this.abilities.flying && (p_225514_2_ == MoverType.SELF || p_225514_2_ == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround()) {
         double d0 = p_225514_1_.x;
         double d1 = p_225514_1_.z;
         double d2 = 0.05D;

         while(d0 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(d0, (double)(-this.maxUpStep), 0.0D))) {
            if (d0 < 0.05D && d0 >= -0.05D) {
               d0 = 0.0D;
            } else if (d0 > 0.0D) {
               d0 -= 0.05D;
            } else {
               d0 += 0.05D;
            }
         }

         while(d1 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double)(-this.maxUpStep), d1))) {
            if (d1 < 0.05D && d1 >= -0.05D) {
               d1 = 0.0D;
            } else if (d1 > 0.0D) {
               d1 -= 0.05D;
            } else {
               d1 += 0.05D;
            }
         }

         while(d0 != 0.0D && d1 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(d0, (double)(-this.maxUpStep), d1))) {
            if (d0 < 0.05D && d0 >= -0.05D) {
               d0 = 0.0D;
            } else if (d0 > 0.0D) {
               d0 -= 0.05D;
            } else {
               d0 += 0.05D;
            }

            if (d1 < 0.05D && d1 >= -0.05D) {
               d1 = 0.0D;
            } else if (d1 > 0.0D) {
               d1 -= 0.05D;
            } else {
               d1 += 0.05D;
            }
         }

         p_225514_1_ = new Vector3d(d0, p_225514_1_.y, d1);
      }

      return p_225514_1_;
   }

   private boolean isAboveGround() {
      return this.onGround || this.fallDistance < this.maxUpStep && !this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double)(this.fallDistance - this.maxUpStep), 0.0D));
   }

   public void attack(Entity p_71059_1_) {
      if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(this, p_71059_1_)) return;
      if (p_71059_1_.isAttackable()) {
         if (!p_71059_1_.skipAttackInteraction(this)) {
            float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1;
            if (p_71059_1_ instanceof LivingEntity) {
               f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)p_71059_1_).getMobType());
            } else {
               f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), CreatureAttribute.UNDEFINED);
            }

            float f2 = this.getAttackStrengthScale(0.5F);
            f = f * (0.2F + f2 * f2 * 0.8F);
            f1 = f1 * f2;
            this.resetAttackStrengthTicker();
            if (f > 0.0F || f1 > 0.0F) {
               boolean flag = f2 > 0.9F;
               boolean flag1 = false;
               int i = 0;
               i = i + EnchantmentHelper.getKnockbackBonus(this);
               if (this.isSprinting() && flag) {
                  this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                  ++i;
                  flag1 = true;
               }

               boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.onClimbable() && !this.isInWater() && !this.hasEffect(Effects.BLINDNESS) && !this.isPassenger() && p_71059_1_ instanceof LivingEntity;
               flag2 = flag2 && !this.isSprinting();
               net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(this, p_71059_1_, flag2, flag2 ? 1.5F : 1.0F);
               flag2 = hitResult != null;
               if (flag2) {
                  f *= hitResult.getDamageModifier();
               }

               f = f + f1;
               boolean flag3 = false;
               double d0 = (double)(this.walkDist - this.walkDistO);
               if (flag && !flag2 && !flag1 && this.onGround && d0 < (double)this.getSpeed()) {
                  ItemStack itemstack = this.getItemInHand(Hand.MAIN_HAND);
                  if (itemstack.getItem() instanceof SwordItem) {
                     flag3 = true;
                  }
               }

               float f4 = 0.0F;
               boolean flag4 = false;
               int j = EnchantmentHelper.getFireAspect(this);
               if (p_71059_1_ instanceof LivingEntity) {
                  f4 = ((LivingEntity)p_71059_1_).getHealth();
                  if (j > 0 && !p_71059_1_.isOnFire()) {
                     flag4 = true;
                     p_71059_1_.setSecondsOnFire(1);
                  }
               }

               Vector3d vector3d = p_71059_1_.getDeltaMovement();
               boolean flag5 = p_71059_1_.hurt(DamageSource.playerAttack(this), f);
               if (flag5) {
                  if (i > 0) {
                     if (p_71059_1_ instanceof LivingEntity) {
                        ((LivingEntity)p_71059_1_).knockback((float)i * 0.5F, (double)MathHelper.sin(this.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.yRot * ((float)Math.PI / 180F))));
                     } else {
                        p_71059_1_.push((double)(-MathHelper.sin(this.yRot * ((float)Math.PI / 180F)) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.yRot * ((float)Math.PI / 180F)) * (float)i * 0.5F));
                     }

                     this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                     this.setSprinting(false);
                  }

                  if (flag3) {
                     float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;

                     for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, p_71059_1_.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                        if (livingentity != this && livingentity != p_71059_1_ && !this.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingentity).isMarker()) && this.distanceToSqr(livingentity) < 9.0D) {
                           livingentity.knockback(0.4F, (double)MathHelper.sin(this.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.yRot * ((float)Math.PI / 180F))));
                           livingentity.hurt(DamageSource.playerAttack(this), f3);
                        }
                     }

                     this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                     this.sweepAttack();
                  }

                  if (p_71059_1_ instanceof ServerPlayerEntity && p_71059_1_.hurtMarked) {
                     ((ServerPlayerEntity)p_71059_1_).connection.send(new SEntityVelocityPacket(p_71059_1_));
                     p_71059_1_.hurtMarked = false;
                     p_71059_1_.setDeltaMovement(vector3d);
                  }

                  if (flag2) {
                     this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                     this.crit(p_71059_1_);
                  }

                  if (!flag2 && !flag3) {
                     if (flag) {
                        this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                     } else {
                        this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                     }
                  }

                  if (f1 > 0.0F) {
                     this.magicCrit(p_71059_1_);
                  }

                  this.setLastHurtMob(p_71059_1_);
                  if (p_71059_1_ instanceof LivingEntity) {
                     EnchantmentHelper.doPostHurtEffects((LivingEntity)p_71059_1_, this);
                  }

                  EnchantmentHelper.doPostDamageEffects(this, p_71059_1_);
                  ItemStack itemstack1 = this.getMainHandItem();
                  Entity entity = p_71059_1_;
                  if (p_71059_1_ instanceof EnderDragonPartEntity) {
                     entity = ((EnderDragonPartEntity)p_71059_1_).parentMob;
                  }

                  if (!this.level.isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                     ItemStack copy = itemstack1.copy();
                     itemstack1.hurtEnemy((LivingEntity)entity, this);
                     if (itemstack1.isEmpty()) {
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, copy, Hand.MAIN_HAND);
                        this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                     }
                  }

                  if (p_71059_1_ instanceof LivingEntity) {
                     float f5 = f4 - ((LivingEntity)p_71059_1_).getHealth();
                     this.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                     if (j > 0) {
                        p_71059_1_.setSecondsOnFire(j * 4);
                     }

                     if (this.level instanceof ServerWorld && f5 > 2.0F) {
                        int k = (int)((double)f5 * 0.5D);
                        ((ServerWorld)this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, p_71059_1_.getX(), p_71059_1_.getY(0.5D), p_71059_1_.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                     }
                  }

                  this.causeFoodExhaustion(0.1F);
               } else {
                  this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                  if (flag4) {
                     p_71059_1_.clearFire();
                  }
               }
            }

         }
      }
   }

   protected void doAutoAttackOnTouch(LivingEntity p_204804_1_) {
      this.attack(p_204804_1_);
   }

   public void disableShield(boolean p_190777_1_) {
      float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
      if (p_190777_1_) {
         f += 0.75F;
      }

      if (this.random.nextFloat() < f) {
         this.getCooldowns().addCooldown(this.getUseItem().getItem(), 100);
         this.stopUsingItem();
         this.level.broadcastEntityEvent(this, (byte)30);
      }

   }

   public void crit(Entity p_71009_1_) {
   }

   public void magicCrit(Entity p_71047_1_) {
   }

   public void sweepAttack() {
      double d0 = (double)(-MathHelper.sin(this.yRot * ((float)Math.PI / 180F)));
      double d1 = (double)MathHelper.cos(this.yRot * ((float)Math.PI / 180F));
      if (this.level instanceof ServerWorld) {
         ((ServerWorld)this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d0, this.getY(0.5D), this.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void respawn() {
   }

   @Override
   public void remove(boolean keepData) {
      super.remove(keepData);
      this.inventoryMenu.removed(this);
      if (this.containerMenu != null) {
         this.containerMenu.removed(this);
      }

   }

   public boolean isLocalPlayer() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public Either<PlayerEntity.SleepResult, Unit> startSleepInBed(BlockPos p_213819_1_) {
      this.startSleeping(p_213819_1_);
      this.sleepCounter = 0;
      return Either.right(Unit.INSTANCE);
   }

   public void stopSleepInBed(boolean p_225652_1_, boolean p_225652_2_) {
      net.minecraftforge.event.ForgeEventFactory.onPlayerWakeup(this, p_225652_1_, p_225652_2_);
      super.stopSleeping();
      if (this.level instanceof ServerWorld && p_225652_2_) {
         ((ServerWorld)this.level).updateSleepingPlayerList();
      }

      this.sleepCounter = p_225652_1_ ? 0 : 100;
   }

   public void stopSleeping() {
      this.stopSleepInBed(true, true);
   }

   public static Optional<Vector3d> findRespawnPositionAndUseSpawnBlock(ServerWorld p_242374_0_, BlockPos p_242374_1_, float p_242374_2_, boolean p_242374_3_, boolean p_242374_4_) {
      BlockState blockstate = p_242374_0_.getBlockState(p_242374_1_);
      Block block = blockstate.getBlock();
      if (block instanceof RespawnAnchorBlock && blockstate.getValue(RespawnAnchorBlock.CHARGE) > 0 && RespawnAnchorBlock.canSetSpawn(p_242374_0_)) {
         Optional<Vector3d> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, p_242374_0_, p_242374_1_);
         if (!p_242374_4_ && optional.isPresent()) {
            p_242374_0_.setBlock(p_242374_1_, blockstate.setValue(RespawnAnchorBlock.CHARGE, Integer.valueOf(blockstate.getValue(RespawnAnchorBlock.CHARGE) - 1)), 3);
         }

         return optional;
      } else if (blockstate.isBed(p_242374_0_, p_242374_1_, null) && BedBlock.canSetSpawn(p_242374_0_)) {
         return blockstate.getBedSpawnPosition(EntityType.PLAYER, p_242374_0_, p_242374_1_, p_242374_2_, null);
      } else if (!p_242374_3_) {
         return Optional.empty();
      } else {
         boolean flag = block.isPossibleToRespawnInThis();
         boolean flag1 = p_242374_0_.getBlockState(p_242374_1_.above()).getBlock().isPossibleToRespawnInThis();
         return flag && flag1 ? Optional.of(new Vector3d((double)p_242374_1_.getX() + 0.5D, (double)p_242374_1_.getY() + 0.1D, (double)p_242374_1_.getZ() + 0.5D)) : Optional.empty();
      }
   }

   public boolean isSleepingLongEnough() {
      return this.isSleeping() && this.sleepCounter >= 100;
   }

   public int getSleepTimer() {
      return this.sleepCounter;
   }

   public void displayClientMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
   }

   public void awardStat(ResourceLocation p_195066_1_) {
      this.awardStat(Stats.CUSTOM.get(p_195066_1_));
   }

   public void awardStat(ResourceLocation p_195067_1_, int p_195067_2_) {
      this.awardStat(Stats.CUSTOM.get(p_195067_1_), p_195067_2_);
   }

   public void awardStat(Stat<?> p_71029_1_) {
      this.awardStat(p_71029_1_, 1);
   }

   public void awardStat(Stat<?> p_71064_1_, int p_71064_2_) {
   }

   public void resetStat(Stat<?> p_175145_1_) {
   }

   public int awardRecipes(Collection<IRecipe<?>> p_195065_1_) {
      return 0;
   }

   public void awardRecipesByKey(ResourceLocation[] p_193102_1_) {
   }

   public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
      return 0;
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      this.awardStat(Stats.JUMP);
      if (this.isSprinting()) {
         this.causeFoodExhaustion(0.2F);
      } else {
         this.causeFoodExhaustion(0.05F);
      }

   }

   public void travel(Vector3d p_213352_1_) {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      if (this.isSwimming() && !this.isPassenger()) {
         double d3 = this.getLookAngle().y;
         double d4 = d3 < -0.2D ? 0.085D : 0.06D;
         if (d3 <= 0.0D || this.jumping || !this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
            Vector3d vector3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vector3d1.add(0.0D, (d3 - vector3d1.y) * d4, 0.0D));
         }
      }

      if (this.abilities.flying && !this.isPassenger()) {
         double d5 = this.getDeltaMovement().y;
         float f = this.flyingSpeed;
         this.flyingSpeed = this.abilities.getFlyingSpeed() * (float)(this.isSprinting() ? 2 : 1);
         super.travel(p_213352_1_);
         Vector3d vector3d = this.getDeltaMovement();
         this.setDeltaMovement(vector3d.x, d5 * 0.6D, vector3d.z);
         this.flyingSpeed = f;
         this.fallDistance = 0.0F;
         this.setSharedFlag(7, false);
      } else {
         super.travel(p_213352_1_);
      }

      this.checkMovementStatistics(this.getX() - d0, this.getY() - d1, this.getZ() - d2);
   }

   public void updateSwimming() {
      if (this.abilities.flying) {
         this.setSwimming(false);
      } else {
         super.updateSwimming();
      }

   }

   protected boolean freeAt(BlockPos p_207401_1_) {
      return !this.level.getBlockState(p_207401_1_).isSuffocating(this.level, p_207401_1_);
   }

   public float getSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   public void checkMovementStatistics(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
      if (!this.isPassenger()) {
         if (this.isSwimming()) {
            int i = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (i > 0) {
               this.awardStat(Stats.SWIM_ONE_CM, i);
               this.causeFoodExhaustion(0.01F * (float)i * 0.01F);
            }
         } else if (this.isEyeInFluid(FluidTags.WATER)) {
            int j = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (j > 0) {
               this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, j);
               this.causeFoodExhaustion(0.01F * (float)j * 0.01F);
            }
         } else if (this.isInWater()) {
            int k = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (k > 0) {
               this.awardStat(Stats.WALK_ON_WATER_ONE_CM, k);
               this.causeFoodExhaustion(0.01F * (float)k * 0.01F);
            }
         } else if (this.onClimbable()) {
            if (p_71000_3_ > 0.0D) {
               this.awardStat(Stats.CLIMB_ONE_CM, (int)Math.round(p_71000_3_ * 100.0D));
            }
         } else if (this.onGround) {
            int l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (l > 0) {
               if (this.isSprinting()) {
                  this.awardStat(Stats.SPRINT_ONE_CM, l);
                  this.causeFoodExhaustion(0.1F * (float)l * 0.01F);
               } else if (this.isCrouching()) {
                  this.awardStat(Stats.CROUCH_ONE_CM, l);
                  this.causeFoodExhaustion(0.0F * (float)l * 0.01F);
               } else {
                  this.awardStat(Stats.WALK_ONE_CM, l);
                  this.causeFoodExhaustion(0.0F * (float)l * 0.01F);
               }
            }
         } else if (this.isFallFlying()) {
            int i1 = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
            this.awardStat(Stats.AVIATE_ONE_CM, i1);
         } else {
            int j1 = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (j1 > 25) {
               this.awardStat(Stats.FLY_ONE_CM, j1);
            }
         }

      }
   }

   private void checkRidingStatistics(double p_71015_1_, double p_71015_3_, double p_71015_5_) {
      if (this.isPassenger()) {
         int i = Math.round(MathHelper.sqrt(p_71015_1_ * p_71015_1_ + p_71015_3_ * p_71015_3_ + p_71015_5_ * p_71015_5_) * 100.0F);
         if (i > 0) {
            Entity entity = this.getVehicle();
            if (entity instanceof AbstractMinecartEntity) {
               this.awardStat(Stats.MINECART_ONE_CM, i);
            } else if (entity instanceof BoatEntity) {
               this.awardStat(Stats.BOAT_ONE_CM, i);
            } else if (entity instanceof PigEntity) {
               this.awardStat(Stats.PIG_ONE_CM, i);
            } else if (entity instanceof AbstractHorseEntity) {
               this.awardStat(Stats.HORSE_ONE_CM, i);
            } else if (entity instanceof StriderEntity) {
               this.awardStat(Stats.STRIDER_ONE_CM, i);
            }
         }
      }

   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      if (this.abilities.mayfly) {
         net.minecraftforge.event.ForgeEventFactory.onPlayerFall(this, p_225503_1_, p_225503_2_);
         return false;
      } else {
         if (p_225503_1_ >= 2.0F) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round((double)p_225503_1_ * 100.0D));
         }

         return super.causeFallDamage(p_225503_1_, p_225503_2_);
      }
   }

   public boolean tryToStartFallFlying() {
      if (!this.onGround && !this.isFallFlying() && !this.isInWater() && !this.hasEffect(Effects.LEVITATION)) {
         ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.CHEST);
         if (itemstack.canElytraFly(this)) {
            this.startFallFlying();
            return true;
         }
      }

      return false;
   }

   public void startFallFlying() {
      this.setSharedFlag(7, true);
   }

   public void stopFallFlying() {
      this.setSharedFlag(7, true);
      this.setSharedFlag(7, false);
   }

   protected void doWaterSplashEffect() {
      if (!this.isSpectator()) {
         super.doWaterSplashEffect();
      }

   }

   protected SoundEvent getFallDamageSound(int p_184588_1_) {
      return p_184588_1_ > 4 ? SoundEvents.PLAYER_BIG_FALL : SoundEvents.PLAYER_SMALL_FALL;
   }

   public void killed(ServerWorld p_241847_1_, LivingEntity p_241847_2_) {
      this.awardStat(Stats.ENTITY_KILLED.get(p_241847_2_.getType()));
   }

   public void makeStuckInBlock(BlockState p_213295_1_, Vector3d p_213295_2_) {
      if (!this.abilities.flying) {
         super.makeStuckInBlock(p_213295_1_, p_213295_2_);
      }

   }

   public void giveExperiencePoints(int p_195068_1_) {
      net.minecraftforge.event.entity.player.PlayerXpEvent.XpChange event = new net.minecraftforge.event.entity.player.PlayerXpEvent.XpChange(this, p_195068_1_);
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;
      p_195068_1_ = event.getAmount();

      this.increaseScore(p_195068_1_);
      this.experienceProgress += (float)p_195068_1_ / (float)this.getXpNeededForNextLevel();
      this.totalExperience = MathHelper.clamp(this.totalExperience + p_195068_1_, 0, Integer.MAX_VALUE);

      while(this.experienceProgress < 0.0F) {
         float f = this.experienceProgress * (float)this.getXpNeededForNextLevel();
         if (this.experienceLevel > 0) {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 1.0F + f / (float)this.getXpNeededForNextLevel();
         } else {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 0.0F;
         }
      }

      while(this.experienceProgress >= 1.0F) {
         this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getXpNeededForNextLevel();
         this.giveExperienceLevels(1);
         this.experienceProgress /= (float)this.getXpNeededForNextLevel();
      }

   }

   public int getEnchantmentSeed() {
      return this.enchantmentSeed;
   }

   public void onEnchantmentPerformed(ItemStack p_192024_1_, int p_192024_2_) {
      giveExperienceLevels(-p_192024_2_);
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experienceProgress = 0.0F;
         this.totalExperience = 0;
      }

      this.enchantmentSeed = this.random.nextInt();
   }

   public void giveExperienceLevels(int p_82242_1_) {
      net.minecraftforge.event.entity.player.PlayerXpEvent.LevelChange event = new net.minecraftforge.event.entity.player.PlayerXpEvent.LevelChange(this, p_82242_1_);
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;
      p_82242_1_ = event.getLevels();

      this.experienceLevel += p_82242_1_;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experienceProgress = 0.0F;
         this.totalExperience = 0;
      }

      if (p_82242_1_ > 0 && this.experienceLevel % 5 == 0 && (float)this.lastLevelUpTime < (float)this.tickCount - 100.0F) {
         float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
         this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), f * 0.75F, 1.0F);
         this.lastLevelUpTime = this.tickCount;
      }

   }

   public int getXpNeededForNextLevel() {
      if (this.experienceLevel >= 30) {
         return 112 + (this.experienceLevel - 30) * 9;
      } else {
         return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
      }
   }

   public void causeFoodExhaustion(float p_71020_1_) {
      if (!this.abilities.invulnerable) {
         if (!this.level.isClientSide) {
            this.foodData.addExhaustion(p_71020_1_);
         }

      }
   }

   public FoodStats getFoodData() {
      return this.foodData;
   }

   public boolean canEat(boolean p_71043_1_) {
      return this.abilities.invulnerable || p_71043_1_ || this.foodData.needsFood();
   }

   public boolean isHurt() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public boolean mayBuild() {
      return this.abilities.mayBuild;
   }

   public boolean mayUseItemAt(BlockPos p_175151_1_, Direction p_175151_2_, ItemStack p_175151_3_) {
      if (this.abilities.mayBuild) {
         return true;
      } else {
         BlockPos blockpos = p_175151_1_.relative(p_175151_2_.getOpposite());
         CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.level, blockpos, false);
         return p_175151_3_.hasAdventureModePlaceTagForBlock(this.level.getTagManager(), cachedblockinfo);
      }
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator()) {
         int i = this.experienceLevel * 7;
         return i > 100 ? 100 : i;
      } else {
         return 0;
      }
   }

   protected boolean isAlwaysExperienceDropper() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldShowName() {
      return true;
   }

   protected boolean isMovementNoisy() {
      return !this.abilities.flying && (!this.onGround || !this.isDiscrete());
   }

   public void onUpdateAbilities() {
   }

   public void setGameMode(GameType p_71033_1_) {
   }

   public ITextComponent getName() {
      return new StringTextComponent(this.gameProfile.getName());
   }

   public EnderChestInventory getEnderChestInventory() {
      return this.enderChestInventory;
   }

   public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
      if (p_184582_1_ == EquipmentSlotType.MAINHAND) {
         return this.inventory.getSelected();
      } else if (p_184582_1_ == EquipmentSlotType.OFFHAND) {
         return this.inventory.offhand.get(0);
      } else {
         return p_184582_1_.getType() == EquipmentSlotType.Group.ARMOR ? this.inventory.armor.get(p_184582_1_.getIndex()) : ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      if (p_184201_1_ == EquipmentSlotType.MAINHAND) {
         this.playEquipSound(p_184201_2_);
         this.inventory.items.set(this.inventory.selected, p_184201_2_);
      } else if (p_184201_1_ == EquipmentSlotType.OFFHAND) {
         this.playEquipSound(p_184201_2_);
         this.inventory.offhand.set(0, p_184201_2_);
      } else if (p_184201_1_.getType() == EquipmentSlotType.Group.ARMOR) {
         this.playEquipSound(p_184201_2_);
         this.inventory.armor.set(p_184201_1_.getIndex(), p_184201_2_);
      }

   }

   public boolean addItem(ItemStack p_191521_1_) {
      this.playEquipSound(p_191521_1_);
      return this.inventory.add(p_191521_1_);
   }

   public Iterable<ItemStack> getHandSlots() {
      return Lists.newArrayList(this.getMainHandItem(), this.getOffhandItem());
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.inventory.armor;
   }

   public boolean setEntityOnShoulder(CompoundNBT p_192027_1_) {
      if (!this.isPassenger() && this.onGround && !this.isInWater()) {
         if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(p_192027_1_);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
         } else if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(p_192027_1_);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void removeEntitiesOnShoulder() {
      if (this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
         this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
         this.setShoulderEntityLeft(new CompoundNBT());
         this.respawnEntityOnShoulder(this.getShoulderEntityRight());
         this.setShoulderEntityRight(new CompoundNBT());
      }

   }

   private void respawnEntityOnShoulder(CompoundNBT p_192026_1_) {
      if (!this.level.isClientSide && !p_192026_1_.isEmpty()) {
         EntityType.create(p_192026_1_, this.level).ifPresent((p_226562_1_) -> {
            if (p_226562_1_ instanceof TameableEntity) {
               ((TameableEntity)p_226562_1_).setOwnerUUID(this.uuid);
            }

            p_226562_1_.setPos(this.getX(), this.getY() + (double)0.7F, this.getZ());
            ((ServerWorld)this.level).addWithUUID(p_226562_1_);
         });
      }

   }

   public abstract boolean isSpectator();

   public boolean isSwimming() {
      return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
   }

   public abstract boolean isCreative();

   public boolean isPushedByFluid() {
      return !this.abilities.flying;
   }

   public Scoreboard getScoreboard() {
      return this.level.getScoreboard();
   }

   public ITextComponent getDisplayName() {
      if (this.displayname == null) this.displayname = net.minecraftforge.event.ForgeEventFactory.getPlayerDisplayName(this, this.getName());
      IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("");
      iformattabletextcomponent = prefixes.stream().reduce(iformattabletextcomponent, IFormattableTextComponent::append);
      iformattabletextcomponent = iformattabletextcomponent.append(ScorePlayerTeam.formatNameForTeam(this.getTeam(), this.displayname));
      iformattabletextcomponent = suffixes.stream().reduce(iformattabletextcomponent, IFormattableTextComponent::append);
      return this.decorateDisplayNameComponent(iformattabletextcomponent);
   }

   private IFormattableTextComponent decorateDisplayNameComponent(IFormattableTextComponent p_208016_1_) {
      String s = this.getGameProfile().getName();
      return p_208016_1_.withStyle((p_234565_2_) -> {
         return p_234565_2_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + s + " ")).withHoverEvent(this.createHoverEvent()).withInsertion(s);
      });
   }

   public String getScoreboardName() {
      return this.getGameProfile().getName();
   }

   public float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      switch(p_213348_1_) {
      case SWIMMING:
      case FALL_FLYING:
      case SPIN_ATTACK:
         return 0.4F;
      case CROUCHING:
         return 1.27F;
      default:
         return 1.62F;
      }
   }

   public void setAbsorptionAmount(float p_110149_1_) {
      if (p_110149_1_ < 0.0F) {
         p_110149_1_ = 0.0F;
      }

      this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, p_110149_1_);
   }

   public float getAbsorptionAmount() {
      return this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID);
   }

   public static UUID createPlayerUUID(GameProfile p_146094_0_) {
      UUID uuid = p_146094_0_.getId();
      if (uuid == null) {
         uuid = createPlayerUUID(p_146094_0_.getName());
      }

      return uuid;
   }

   public static UUID createPlayerUUID(String p_175147_0_) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + p_175147_0_).getBytes(StandardCharsets.UTF_8));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isModelPartShown(PlayerModelPart p_175148_1_) {
      return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & p_175148_1_.getMask()) == p_175148_1_.getMask();
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ >= 0 && p_174820_1_ < this.inventory.items.size()) {
         this.inventory.setItem(p_174820_1_, p_174820_2_);
         return true;
      } else {
         EquipmentSlotType equipmentslottype;
         if (p_174820_1_ == 100 + EquipmentSlotType.HEAD.getIndex()) {
            equipmentslottype = EquipmentSlotType.HEAD;
         } else if (p_174820_1_ == 100 + EquipmentSlotType.CHEST.getIndex()) {
            equipmentslottype = EquipmentSlotType.CHEST;
         } else if (p_174820_1_ == 100 + EquipmentSlotType.LEGS.getIndex()) {
            equipmentslottype = EquipmentSlotType.LEGS;
         } else if (p_174820_1_ == 100 + EquipmentSlotType.FEET.getIndex()) {
            equipmentslottype = EquipmentSlotType.FEET;
         } else {
            equipmentslottype = null;
         }

         if (p_174820_1_ == 98) {
            this.setItemSlot(EquipmentSlotType.MAINHAND, p_174820_2_);
            return true;
         } else if (p_174820_1_ == 99) {
            this.setItemSlot(EquipmentSlotType.OFFHAND, p_174820_2_);
            return true;
         } else if (equipmentslottype == null) {
            int i = p_174820_1_ - 200;
            if (i >= 0 && i < this.enderChestInventory.getContainerSize()) {
               this.enderChestInventory.setItem(i, p_174820_2_);
               return true;
            } else {
               return false;
            }
         } else {
            if (!p_174820_2_.isEmpty()) {
               if (!(p_174820_2_.getItem() instanceof ArmorItem) && !(p_174820_2_.getItem() instanceof ElytraItem)) {
                  if (equipmentslottype != EquipmentSlotType.HEAD) {
                     return false;
                  }
               } else if (MobEntity.getEquipmentSlotForItem(p_174820_2_) != equipmentslottype) {
                  return false;
               }
            }

            this.inventory.setItem(equipmentslottype.getIndex() + this.inventory.items.size(), p_174820_2_);
            return true;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   @OnlyIn(Dist.CLIENT)
   public void setReducedDebugInfo(boolean p_175150_1_) {
      this.reducedDebugInfo = p_175150_1_;
   }

   public void setRemainingFireTicks(int p_241209_1_) {
      super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(p_241209_1_, 1) : p_241209_1_);
   }

   public HandSide getMainArm() {
      return this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HandSide.LEFT : HandSide.RIGHT;
   }

   public void setMainArm(HandSide p_184819_1_) {
      this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte)(p_184819_1_ == HandSide.LEFT ? 0 : 1));
   }

   public CompoundNBT getShoulderEntityLeft() {
      return this.entityData.get(DATA_SHOULDER_LEFT);
   }

   protected void setShoulderEntityLeft(CompoundNBT p_192029_1_) {
      this.entityData.set(DATA_SHOULDER_LEFT, p_192029_1_);
   }

   public CompoundNBT getShoulderEntityRight() {
      return this.entityData.get(DATA_SHOULDER_RIGHT);
   }

   protected void setShoulderEntityRight(CompoundNBT p_192031_1_) {
      this.entityData.set(DATA_SHOULDER_RIGHT, p_192031_1_);
   }

   public float getCurrentItemAttackStrengthDelay() {
      return (float)(1.0D / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0D);
   }

   public float getAttackStrengthScale(float p_184825_1_) {
      return MathHelper.clamp(((float)this.attackStrengthTicker + p_184825_1_) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
   }

   public void resetAttackStrengthTicker() {
      this.attackStrengthTicker = 0;
   }

   public CooldownTracker getCooldowns() {
      return this.cooldowns;
   }

   protected float getBlockSpeedFactor() {
      return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
   }

   public float getLuck() {
      return (float)this.getAttributeValue(Attributes.LUCK);
   }

   public boolean canUseGameMasterBlocks() {
      return this.abilities.instabuild && this.getPermissionLevel() >= 2;
   }

   public boolean canTakeItem(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(p_213365_1_);
      return this.getItemBySlot(equipmentslottype).isEmpty();
   }

   public EntitySize getDimensions(Pose p_213305_1_) {
      return POSES.getOrDefault(p_213305_1_, STANDING_DIMENSIONS);
   }

   public ImmutableList<Pose> getDismountPoses() {
      return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
   }

   public ItemStack getProjectile(ItemStack p_213356_1_) {
      if (!(p_213356_1_.getItem() instanceof ShootableItem)) {
         return ItemStack.EMPTY;
      } else {
         Predicate<ItemStack> predicate = ((ShootableItem)p_213356_1_.getItem()).getSupportedHeldProjectiles();
         ItemStack itemstack = ShootableItem.getHeldProjectile(this, predicate);
         if (!itemstack.isEmpty()) {
            return itemstack;
         } else {
            predicate = ((ShootableItem)p_213356_1_.getItem()).getAllSupportedProjectiles();

            for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
               ItemStack itemstack1 = this.inventory.getItem(i);
               if (predicate.test(itemstack1)) {
                  return itemstack1;
               }
            }

            return this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
         }
      }
   }

   public ItemStack eat(World p_213357_1_, ItemStack p_213357_2_) {
      this.getFoodData().eat(p_213357_2_.getItem(), p_213357_2_);
      this.awardStat(Stats.ITEM_USED.get(p_213357_2_.getItem()));
      p_213357_1_.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, p_213357_1_.random.nextFloat() * 0.1F + 0.9F);
      if (this instanceof ServerPlayerEntity) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)this, p_213357_2_);
      }

      return super.eat(p_213357_1_, p_213357_2_);
   }

   protected boolean shouldRemoveSoulSpeed(BlockState p_230295_1_) {
      return this.abilities.flying || super.shouldRemoveSoulSpeed(p_230295_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getRopeHoldPosition(float p_241843_1_) {
      double d0 = 0.22D * (this.getMainArm() == HandSide.RIGHT ? -1.0D : 1.0D);
      float f = MathHelper.lerp(p_241843_1_ * 0.5F, this.xRot, this.xRotO) * ((float)Math.PI / 180F);
      float f1 = MathHelper.lerp(p_241843_1_, this.yBodyRotO, this.yBodyRot) * ((float)Math.PI / 180F);
      if (!this.isFallFlying() && !this.isAutoSpinAttack()) {
         if (this.isVisuallySwimming()) {
            return this.getPosition(p_241843_1_).add((new Vector3d(d0, 0.2D, -0.15D)).xRot(-f).yRot(-f1));
         } else {
            double d5 = this.getBoundingBox().getYsize() - 1.0D;
            double d6 = this.isCrouching() ? -0.2D : 0.07D;
            return this.getPosition(p_241843_1_).add((new Vector3d(d0, d5, d6)).yRot(-f1));
         }
      } else {
         Vector3d vector3d = this.getViewVector(p_241843_1_);
         Vector3d vector3d1 = this.getDeltaMovement();
         double d1 = Entity.getHorizontalDistanceSqr(vector3d1);
         double d2 = Entity.getHorizontalDistanceSqr(vector3d);
         float f2;
         if (d1 > 0.0D && d2 > 0.0D) {
            double d3 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d1 * d2);
            double d4 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
            f2 = (float)(Math.signum(d4) * Math.acos(d3));
         } else {
            f2 = 0.0F;
         }

         return this.getPosition(p_241843_1_).add((new Vector3d(d0, -0.11D, 0.85D)).zRot(-f2).xRot(-f).yRot(-f1));
      }
   }

   public static enum SleepResult {
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW(new TranslationTextComponent("block.minecraft.bed.no_sleep")),
      TOO_FAR_AWAY(new TranslationTextComponent("block.minecraft.bed.too_far_away")),
      OBSTRUCTED(new TranslationTextComponent("block.minecraft.bed.obstructed")),
      OTHER_PROBLEM,
      NOT_SAFE(new TranslationTextComponent("block.minecraft.bed.not_safe"));

      @Nullable
      private final ITextComponent message;

      private SleepResult() {
         this.message = null;
      }

      private SleepResult(ITextComponent p_i50668_3_) {
         this.message = p_i50668_3_;
      }

      @Nullable
      public ITextComponent getMessage() {
         return this.message;
      }
   }

   // =========== FORGE START ==============//
   public Collection<IFormattableTextComponent> getPrefixes() {
       return this.prefixes;
   }

   public Collection<IFormattableTextComponent> getSuffixes() {
       return this.suffixes;
   }

   private ITextComponent displayname = null;
   /**
    * Force the displayed name to refresh, by firing {@link net.minecraftforge.event.entity.player.PlayerEvent.NameFormat}, using the real player name as event parameter.
    */
   public void refreshDisplayName() {
      this.displayname = net.minecraftforge.event.ForgeEventFactory.getPlayerDisplayName(this, this.getName());
   }

   private final net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandler>
         playerMainHandler = net.minecraftforge.common.util.LazyOptional.of(
               () -> new net.minecraftforge.items.wrapper.PlayerMainInvWrapper(inventory));

   private final net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandler>
         playerEquipmentHandler = net.minecraftforge.common.util.LazyOptional.of(
               () -> new net.minecraftforge.items.wrapper.CombinedInvWrapper(
                     new net.minecraftforge.items.wrapper.PlayerArmorInvWrapper(inventory),
                     new net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper(inventory)));

   private final net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandler>
         playerJoinedHandler = net.minecraftforge.common.util.LazyOptional.of(
               () -> new net.minecraftforge.items.wrapper.PlayerInvWrapper(inventory));

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
      if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == null) return playerJoinedHandler.cast();
         else if (facing.getAxis().isVertical()) return playerMainHandler.cast();
         else if (facing.getAxis().isHorizontal()) return playerEquipmentHandler.cast();
      }
      return super.getCapability(capability, facing);
   }

   /**
    * Force a pose for the player. If set, the vanilla pose determination and clearance check is skipped. Make sure the pose is clear yourself (e.g. in PlayerTick).
    * This has to be set just once, do not set it every tick.
    * Make sure to clear (null) the pose if not required anymore and only use if necessary.
    */
   public void setForcedPose(@Nullable Pose pose) {
      this.forcedPose = pose;
   }

   /**
    * @return The forced pose if set, null otherwise
    */
   @Nullable
   public Pose getForcedPose() {
      return this.forcedPose;
   }
}
