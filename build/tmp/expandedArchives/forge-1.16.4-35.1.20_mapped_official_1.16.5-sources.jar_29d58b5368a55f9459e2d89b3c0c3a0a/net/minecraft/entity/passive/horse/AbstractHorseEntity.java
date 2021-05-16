package net.minecraft.entity.passive.horse;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.ILivingEntityData;
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
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractHorseEntity extends AnimalEntity implements IInventoryChangedListener, IJumpingMount, IEquipable {
   private static final Predicate<LivingEntity> PARENT_HORSE_SELECTOR = (p_213617_0_) -> {
      return p_213617_0_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_213617_0_).isBred();
   };
   private static final EntityPredicate MOMMY_TARGETING = (new EntityPredicate()).range(16.0D).allowInvulnerable().allowSameTeam().allowUnseeable().selector(PARENT_HORSE_SELECTOR);
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
   private static final DataParameter<Byte> DATA_ID_FLAGS = EntityDataManager.defineId(AbstractHorseEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Optional<UUID>> DATA_ID_OWNER_UUID = EntityDataManager.defineId(AbstractHorseEntity.class, DataSerializers.OPTIONAL_UUID);
   private int eatingCounter;
   private int mouthCounter;
   private int standCounter;
   public int tailCounter;
   public int sprintCounter;
   protected boolean isJumping;
   protected Inventory inventory;
   protected int temper;
   protected float playerJumpPendingScale;
   private boolean allowStandSliding;
   private float eatAnim;
   private float eatAnimO;
   private float standAnim;
   private float standAnimO;
   private float mouthAnim;
   private float mouthAnimO;
   protected boolean canGallop = true;
   protected int gallopSoundCounter;

   protected AbstractHorseEntity(EntityType<? extends AbstractHorseEntity> p_i48563_1_, World p_i48563_2_) {
      super(p_i48563_1_, p_i48563_2_);
      this.maxUpStep = 1.0F;
      this.createInventory();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FLAGS, (byte)0);
      this.entityData.define(DATA_ID_OWNER_UUID, Optional.empty());
   }

   protected boolean getFlag(int p_110233_1_) {
      return (this.entityData.get(DATA_ID_FLAGS) & p_110233_1_) != 0;
   }

   protected void setFlag(int p_110208_1_, boolean p_110208_2_) {
      byte b0 = this.entityData.get(DATA_ID_FLAGS);
      if (p_110208_2_) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | p_110208_1_));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & ~p_110208_1_));
      }

   }

   public boolean isTamed() {
      return this.getFlag(2);
   }

   @Nullable
   public UUID getOwnerUUID() {
      return this.entityData.get(DATA_ID_OWNER_UUID).orElse((UUID)null);
   }

   public void setOwnerUUID(@Nullable UUID p_184779_1_) {
      this.entityData.set(DATA_ID_OWNER_UUID, Optional.ofNullable(p_184779_1_));
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   public void setTamed(boolean p_110234_1_) {
      this.setFlag(2, p_110234_1_);
   }

   public void setIsJumping(boolean p_110255_1_) {
      this.isJumping = p_110255_1_;
   }

   protected void onLeashDistance(float p_142017_1_) {
      if (p_142017_1_ > 6.0F && this.isEating()) {
         this.setEating(false);
      }

   }

   public boolean isEating() {
      return this.getFlag(16);
   }

   public boolean isStanding() {
      return this.getFlag(32);
   }

   public boolean isBred() {
      return this.getFlag(8);
   }

   public void setBred(boolean p_110242_1_) {
      this.setFlag(8, p_110242_1_);
   }

   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby() && this.isTamed();
   }

   public void equipSaddle(@Nullable SoundCategory p_230266_1_) {
      this.inventory.setItem(0, new ItemStack(Items.SADDLE));
      if (p_230266_1_ != null) {
         this.level.playSound((PlayerEntity)null, this, SoundEvents.HORSE_SADDLE, p_230266_1_, 0.5F, 1.0F);
      }

   }

   public boolean isSaddled() {
      return this.getFlag(4);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int p_110238_1_) {
      this.temper = p_110238_1_;
   }

   public int modifyTemper(int p_110198_1_) {
      int i = MathHelper.clamp(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
      this.setTemper(i);
      return i;
   }

   public boolean isPushable() {
      return !this.isVehicle();
   }

   private void eating() {
      this.openMouth();
      if (!this.isSilent()) {
         SoundEvent soundevent = this.getEatingSound();
         if (soundevent != null) {
            this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }
      }

   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      if (p_225503_1_ > 1.0F) {
         this.playSound(SoundEvents.HORSE_LAND, 0.4F, 1.0F);
      }

      int i = this.calculateFallDamage(p_225503_1_, p_225503_2_);
      if (i <= 0) {
         return false;
      } else {
         this.hurt(DamageSource.FALL, (float)i);
         if (this.isVehicle()) {
            for(Entity entity : this.getIndirectPassengers()) {
               entity.hurt(DamageSource.FALL, (float)i);
            }
         }

         this.playBlockFallSound();
         return true;
      }
   }

   protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
      return MathHelper.ceil((p_225508_1_ * 0.5F - 3.0F) * p_225508_2_);
   }

   protected int getInventorySize() {
      return 2;
   }

   protected void createInventory() {
      Inventory inventory = this.inventory;
      this.inventory = new Inventory(this.getInventorySize());
      if (inventory != null) {
         inventory.removeListener(this);
         int i = Math.min(inventory.getContainerSize(), this.inventory.getContainerSize());

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = inventory.getItem(j);
            if (!itemstack.isEmpty()) {
               this.inventory.setItem(j, itemstack.copy());
            }
         }
      }

      this.inventory.addListener(this);
      this.updateContainerEquipment();
      this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
   }

   protected void updateContainerEquipment() {
      if (!this.level.isClientSide) {
         this.setFlag(4, !this.inventory.getItem(0).isEmpty());
      }
   }

   public void containerChanged(IInventory p_76316_1_) {
      boolean flag = this.isSaddled();
      this.updateContainerEquipment();
      if (this.tickCount > 20 && !flag && this.isSaddled()) {
         this.playSound(SoundEvents.HORSE_SADDLE, 0.5F, 1.0F);
      }

   }

   public double getCustomJump() {
      return this.getAttributeValue(Attributes.JUMP_STRENGTH);
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      if (this.random.nextInt(3) == 0) {
         this.stand();
      }

      return null;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
         this.stand();
      }

      return null;
   }

   @Nullable
   protected SoundEvent getAngrySound() {
      this.stand();
      return null;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      if (!p_180429_2_.getMaterial().isLiquid()) {
         BlockState blockstate = this.level.getBlockState(p_180429_1_.above());
         SoundType soundtype = p_180429_2_.getSoundType(level, p_180429_1_, this);
         if (blockstate.is(Blocks.SNOW)) {
            soundtype = blockstate.getSoundType(level, p_180429_1_, this);
         }

         if (this.isVehicle() && this.canGallop) {
            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
               this.playGallopSound(soundtype);
            } else if (this.gallopSoundCounter <= 5) {
               this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
         } else if (soundtype == SoundType.WOOD) {
            this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         } else {
            this.playSound(SoundEvents.HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         }

      }
   }

   protected void playGallopSound(SoundType p_190680_1_) {
      this.playSound(SoundEvents.HORSE_GALLOP, p_190680_1_.getVolume() * 0.15F, p_190680_1_.getPitch());
   }

   public static AttributeModifierMap.MutableAttribute createBaseHorseAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.JUMP_STRENGTH).add(Attributes.MAX_HEALTH, 53.0D).add(Attributes.MOVEMENT_SPEED, (double)0.225F);
   }

   public int getMaxSpawnClusterSize() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   protected float getSoundVolume() {
      return 0.8F;
   }

   public int getAmbientSoundInterval() {
      return 400;
   }

   public void openInventory(PlayerEntity p_110199_1_) {
      if (!this.level.isClientSide && (!this.isVehicle() || this.hasPassenger(p_110199_1_)) && this.isTamed()) {
         p_110199_1_.openHorseInventory(this, this.inventory);
      }

   }

   public ActionResultType fedFood(PlayerEntity p_241395_1_, ItemStack p_241395_2_) {
      boolean flag = this.handleEating(p_241395_1_, p_241395_2_);
      if (!p_241395_1_.abilities.instabuild) {
         p_241395_2_.shrink(1);
      }

      if (this.level.isClientSide) {
         return ActionResultType.CONSUME;
      } else {
         return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
      }
   }

   protected boolean handleEating(PlayerEntity p_190678_1_, ItemStack p_190678_2_) {
      boolean flag = false;
      float f = 0.0F;
      int i = 0;
      int j = 0;
      Item item = p_190678_2_.getItem();
      if (item == Items.WHEAT) {
         f = 2.0F;
         i = 20;
         j = 3;
      } else if (item == Items.SUGAR) {
         f = 1.0F;
         i = 30;
         j = 3;
      } else if (item == Blocks.HAY_BLOCK.asItem()) {
         f = 20.0F;
         i = 180;
      } else if (item == Items.APPLE) {
         f = 3.0F;
         i = 60;
         j = 3;
      } else if (item == Items.GOLDEN_CARROT) {
         f = 4.0F;
         i = 60;
         j = 5;
         if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_190678_1_);
         }
      } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
         f = 10.0F;
         i = 240;
         j = 10;
         if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_190678_1_);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
         this.heal(f);
         flag = true;
      }

      if (this.isBaby() && i > 0) {
         this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
         if (!this.level.isClientSide) {
            this.ageUp(i);
         }

         flag = true;
      }

      if (j > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
         flag = true;
         if (!this.level.isClientSide) {
            this.modifyTemper(j);
         }
      }

      if (flag) {
         this.eating();
      }

      return flag;
   }

   protected void doPlayerRide(PlayerEntity p_110237_1_) {
      this.setEating(false);
      this.setStanding(false);
      if (!this.level.isClientSide) {
         p_110237_1_.yRot = this.yRot;
         p_110237_1_.xRot = this.xRot;
         p_110237_1_.startRiding(this);
      }

   }

   protected boolean isImmobile() {
      return super.isImmobile() && this.isVehicle() && this.isSaddled() || this.isEating() || this.isStanding();
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return FOOD_ITEMS.test(p_70877_1_);
   }

   private void moveTail() {
      this.tailCounter = 1;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.inventory != null) {
         for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
               this.spawnAtLocation(itemstack);
            }
         }

      }
   }

   public void aiStep() {
      if (this.random.nextInt(200) == 0) {
         this.moveTail();
      }

      super.aiStep();
      if (!this.level.isClientSide && this.isAlive()) {
         if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (this.canEatGrass()) {
            if (!this.isEating() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.level.getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
               this.setEating(true);
            }

            if (this.isEating() && ++this.eatingCounter > 50) {
               this.eatingCounter = 0;
               this.setEating(false);
            }
         }

         this.followMommy();
      }
   }

   protected void followMommy() {
      if (this.isBred() && this.isBaby() && !this.isEating()) {
         LivingEntity livingentity = this.level.getNearestEntity(AbstractHorseEntity.class, MOMMY_TARGETING, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(16.0D));
         if (livingentity != null && this.distanceToSqr(livingentity) > 4.0D) {
            this.navigation.createPath(livingentity, 0);
         }
      }

   }

   public boolean canEatGrass() {
      return true;
   }

   public void tick() {
      super.tick();
      if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
         this.mouthCounter = 0;
         this.setFlag(64, false);
      }

      if ((this.isControlledByLocalInstance() || this.isEffectiveAi()) && this.standCounter > 0 && ++this.standCounter > 20) {
         this.standCounter = 0;
         this.setStanding(false);
      }

      if (this.tailCounter > 0 && ++this.tailCounter > 8) {
         this.tailCounter = 0;
      }

      if (this.sprintCounter > 0) {
         ++this.sprintCounter;
         if (this.sprintCounter > 300) {
            this.sprintCounter = 0;
         }
      }

      this.eatAnimO = this.eatAnim;
      if (this.isEating()) {
         this.eatAnim += (1.0F - this.eatAnim) * 0.4F + 0.05F;
         if (this.eatAnim > 1.0F) {
            this.eatAnim = 1.0F;
         }
      } else {
         this.eatAnim += (0.0F - this.eatAnim) * 0.4F - 0.05F;
         if (this.eatAnim < 0.0F) {
            this.eatAnim = 0.0F;
         }
      }

      this.standAnimO = this.standAnim;
      if (this.isStanding()) {
         this.eatAnim = 0.0F;
         this.eatAnimO = this.eatAnim;
         this.standAnim += (1.0F - this.standAnim) * 0.4F + 0.05F;
         if (this.standAnim > 1.0F) {
            this.standAnim = 1.0F;
         }
      } else {
         this.allowStandSliding = false;
         this.standAnim += (0.8F * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6F - 0.05F;
         if (this.standAnim < 0.0F) {
            this.standAnim = 0.0F;
         }
      }

      this.mouthAnimO = this.mouthAnim;
      if (this.getFlag(64)) {
         this.mouthAnim += (1.0F - this.mouthAnim) * 0.7F + 0.05F;
         if (this.mouthAnim > 1.0F) {
            this.mouthAnim = 1.0F;
         }
      } else {
         this.mouthAnim += (0.0F - this.mouthAnim) * 0.7F - 0.05F;
         if (this.mouthAnim < 0.0F) {
            this.mouthAnim = 0.0F;
         }
      }

   }

   private void openMouth() {
      if (!this.level.isClientSide) {
         this.mouthCounter = 1;
         this.setFlag(64, true);
      }

   }

   public void setEating(boolean p_110227_1_) {
      this.setFlag(16, p_110227_1_);
   }

   public void setStanding(boolean p_110219_1_) {
      if (p_110219_1_) {
         this.setEating(false);
      }

      this.setFlag(32, p_110219_1_);
   }

   private void stand() {
      if (this.isControlledByLocalInstance() || this.isEffectiveAi()) {
         this.standCounter = 1;
         this.setStanding(true);
      }

   }

   public void makeMad() {
      if (!this.isStanding()) {
         this.stand();
         SoundEvent soundevent = this.getAngrySound();
         if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
         }
      }

   }

   public boolean tameWithName(PlayerEntity p_110263_1_) {
      this.setOwnerUUID(p_110263_1_.getUUID());
      this.setTamed(true);
      if (p_110263_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)p_110263_1_, this);
      }

      this.level.broadcastEntityEvent(this, (byte)7);
      return true;
   }

   public void travel(Vector3d p_213352_1_) {
      if (this.isAlive()) {
         if (this.isVehicle() && this.canBeControlledByRider() && this.isSaddled()) {
            LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
            this.yRot = livingentity.yRot;
            this.yRotO = this.yRot;
            this.xRot = livingentity.xRot * 0.5F;
            this.setRot(this.yRot, this.xRot);
            this.yBodyRot = this.yRot;
            this.yHeadRot = this.yBodyRot;
            float f = livingentity.xxa * 0.5F;
            float f1 = livingentity.zza;
            if (f1 <= 0.0F) {
               f1 *= 0.25F;
               this.gallopSoundCounter = 0;
            }

            if (this.onGround && this.playerJumpPendingScale == 0.0F && this.isStanding() && !this.allowStandSliding) {
               f = 0.0F;
               f1 = 0.0F;
            }

            if (this.playerJumpPendingScale > 0.0F && !this.isJumping() && this.onGround) {
               double d0 = this.getCustomJump() * (double)this.playerJumpPendingScale * (double)this.getBlockJumpFactor();
               double d1;
               if (this.hasEffect(Effects.JUMP)) {
                  d1 = d0 + (double)((float)(this.getEffect(Effects.JUMP).getAmplifier() + 1) * 0.1F);
               } else {
                  d1 = d0;
               }

               Vector3d vector3d = this.getDeltaMovement();
               this.setDeltaMovement(vector3d.x, d1, vector3d.z);
               this.setIsJumping(true);
               this.hasImpulse = true;
               net.minecraftforge.common.ForgeHooks.onLivingJump(this);
               if (f1 > 0.0F) {
                  float f2 = MathHelper.sin(this.yRot * ((float)Math.PI / 180F));
                  float f3 = MathHelper.cos(this.yRot * ((float)Math.PI / 180F));
                  this.setDeltaMovement(this.getDeltaMovement().add((double)(-0.4F * f2 * this.playerJumpPendingScale), 0.0D, (double)(0.4F * f3 * this.playerJumpPendingScale)));
               }

               this.playerJumpPendingScale = 0.0F;
            }

            this.flyingSpeed = this.getSpeed() * 0.1F;
            if (this.isControlledByLocalInstance()) {
               this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
               super.travel(new Vector3d((double)f, p_213352_1_.y, (double)f1));
            } else if (livingentity instanceof PlayerEntity) {
               this.setDeltaMovement(Vector3d.ZERO);
            }

            if (this.onGround) {
               this.playerJumpPendingScale = 0.0F;
               this.setIsJumping(false);
            }

            this.calculateEntityAnimation(this, false);
         } else {
            this.flyingSpeed = 0.02F;
            super.travel(p_213352_1_);
         }
      }
   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.HORSE_JUMP, 0.4F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("EatingHaystack", this.isEating());
      p_213281_1_.putBoolean("Bred", this.isBred());
      p_213281_1_.putInt("Temper", this.getTemper());
      p_213281_1_.putBoolean("Tame", this.isTamed());
      if (this.getOwnerUUID() != null) {
         p_213281_1_.putUUID("Owner", this.getOwnerUUID());
      }

      if (!this.inventory.getItem(0).isEmpty()) {
         p_213281_1_.put("SaddleItem", this.inventory.getItem(0).save(new CompoundNBT()));
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setEating(p_70037_1_.getBoolean("EatingHaystack"));
      this.setBred(p_70037_1_.getBoolean("Bred"));
      this.setTemper(p_70037_1_.getInt("Temper"));
      this.setTamed(p_70037_1_.getBoolean("Tame"));
      UUID uuid;
      if (p_70037_1_.hasUUID("Owner")) {
         uuid = p_70037_1_.getUUID("Owner");
      } else {
         String s = p_70037_1_.getString("Owner");
         uuid = PreYggdrasilConverter.convertMobOwnerIfNecessary(this.getServer(), s);
      }

      if (uuid != null) {
         this.setOwnerUUID(uuid);
      }

      if (p_70037_1_.contains("SaddleItem", 10)) {
         ItemStack itemstack = ItemStack.of(p_70037_1_.getCompound("SaddleItem"));
         if (itemstack.getItem() == Items.SADDLE) {
            this.inventory.setItem(0, itemstack);
         }
      }

      this.updateContainerEquipment();
   }

   public boolean canMate(AnimalEntity p_70878_1_) {
      return false;
   }

   protected boolean canParent() {
      return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
   }

   @Nullable
   public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      return null;
   }

   protected void setOffspringAttributes(AgeableEntity p_190681_1_, AbstractHorseEntity p_190681_2_) {
      double d0 = this.getAttributeBaseValue(Attributes.MAX_HEALTH) + p_190681_1_.getAttributeBaseValue(Attributes.MAX_HEALTH) + (double)this.generateRandomMaxHealth();
      p_190681_2_.getAttribute(Attributes.MAX_HEALTH).setBaseValue(d0 / 3.0D);
      double d1 = this.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + p_190681_1_.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + this.generateRandomJumpStrength();
      p_190681_2_.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(d1 / 3.0D);
      double d2 = this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + p_190681_1_.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + this.generateRandomSpeed();
      p_190681_2_.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(d2 / 3.0D);
   }

   public boolean canBeControlledByRider() {
      return this.getControllingPassenger() instanceof LivingEntity;
   }

   @OnlyIn(Dist.CLIENT)
   public float getEatAnim(float p_110258_1_) {
      return MathHelper.lerp(p_110258_1_, this.eatAnimO, this.eatAnim);
   }

   @OnlyIn(Dist.CLIENT)
   public float getStandAnim(float p_110223_1_) {
      return MathHelper.lerp(p_110223_1_, this.standAnimO, this.standAnim);
   }

   @OnlyIn(Dist.CLIENT)
   public float getMouthAnim(float p_110201_1_) {
      return MathHelper.lerp(p_110201_1_, this.mouthAnimO, this.mouthAnim);
   }

   @OnlyIn(Dist.CLIENT)
   public void onPlayerJump(int p_110206_1_) {
      if (this.isSaddled()) {
         if (p_110206_1_ < 0) {
            p_110206_1_ = 0;
         } else {
            this.allowStandSliding = true;
            this.stand();
         }

         if (p_110206_1_ >= 90) {
            this.playerJumpPendingScale = 1.0F;
         } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float)p_110206_1_ / 90.0F;
         }

      }
   }

   public boolean canJump() {
      return this.isSaddled();
   }

   public void handleStartJump(int p_184775_1_) {
      this.allowStandSliding = true;
      this.stand();
      this.playJumpSound();
   }

   public void handleStopJump() {
   }

   @OnlyIn(Dist.CLIENT)
   protected void spawnTamingParticles(boolean p_110216_1_) {
      IParticleData iparticledata = p_110216_1_ ? ParticleTypes.HEART : ParticleTypes.SMOKE;

      for(int i = 0; i < 7; ++i) {
         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(iparticledata, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 7) {
         this.spawnTamingParticles(true);
      } else if (p_70103_1_ == 6) {
         this.spawnTamingParticles(false);
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public void positionRider(Entity p_184232_1_) {
      super.positionRider(p_184232_1_);
      if (p_184232_1_ instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)p_184232_1_;
         this.yBodyRot = mobentity.yBodyRot;
      }

      if (this.standAnimO > 0.0F) {
         float f3 = MathHelper.sin(this.yBodyRot * ((float)Math.PI / 180F));
         float f = MathHelper.cos(this.yBodyRot * ((float)Math.PI / 180F));
         float f1 = 0.7F * this.standAnimO;
         float f2 = 0.15F * this.standAnimO;
         p_184232_1_.setPos(this.getX() + (double)(f1 * f3), this.getY() + this.getPassengersRidingOffset() + p_184232_1_.getMyRidingOffset() + (double)f2, this.getZ() - (double)(f1 * f));
         if (p_184232_1_ instanceof LivingEntity) {
            ((LivingEntity)p_184232_1_).yBodyRot = this.yBodyRot;
         }
      }

   }

   protected float generateRandomMaxHealth() {
      return 15.0F + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
   }

   protected double generateRandomJumpStrength() {
      return (double)0.4F + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
   }

   protected double generateRandomSpeed() {
      return ((double)0.45F + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
   }

   public boolean onClimbable() {
      return false;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.95F;
   }

   public boolean canWearArmor() {
      return false;
   }

   public boolean isWearingArmor() {
      return !this.getItemBySlot(EquipmentSlotType.CHEST).isEmpty();
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      return false;
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      int i = p_174820_1_ - 400;
      if (i >= 0 && i < 2 && i < this.inventory.getContainerSize()) {
         if (i == 0 && p_174820_2_.getItem() != Items.SADDLE) {
            return false;
         } else if (i != 1 || this.canWearArmor() && this.isArmor(p_174820_2_)) {
            this.inventory.setItem(i, p_174820_2_);
            this.updateContainerEquipment();
            return true;
         } else {
            return false;
         }
      } else {
         int j = p_174820_1_ - 500 + 2;
         if (j >= 2 && j < this.inventory.getContainerSize()) {
            this.inventory.setItem(j, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
   }

   @Nullable
   private Vector3d getDismountLocationInDirection(Vector3d p_234236_1_, LivingEntity p_234236_2_) {
      double d0 = this.getX() + p_234236_1_.x;
      double d1 = this.getBoundingBox().minY;
      double d2 = this.getZ() + p_234236_1_.z;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Pose pose : p_234236_2_.getDismountPoses()) {
         blockpos$mutable.set(d0, d1, d2);
         double d3 = this.getBoundingBox().maxY + 0.75D;

         while(true) {
            double d4 = this.level.getBlockFloorHeight(blockpos$mutable);
            if ((double)blockpos$mutable.getY() + d4 > d3) {
               break;
            }

            if (TransportationHelper.isBlockFloorValid(d4)) {
               AxisAlignedBB axisalignedbb = p_234236_2_.getLocalBoundsForPose(pose);
               Vector3d vector3d = new Vector3d(d0, (double)blockpos$mutable.getY() + d4, d2);
               if (TransportationHelper.canDismountTo(this.level, p_234236_2_, axisalignedbb.move(vector3d))) {
                  p_234236_2_.setPose(pose);
                  return vector3d;
               }
            }

            blockpos$mutable.move(Direction.UP);
            if (!((double)blockpos$mutable.getY() < d3)) {
               break;
            }
         }
      }

      return null;
   }

   public Vector3d getDismountLocationForPassenger(LivingEntity p_230268_1_) {
      Vector3d vector3d = getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_230268_1_.getBbWidth(), this.yRot + (p_230268_1_.getMainArm() == HandSide.RIGHT ? 90.0F : -90.0F));
      Vector3d vector3d1 = this.getDismountLocationInDirection(vector3d, p_230268_1_);
      if (vector3d1 != null) {
         return vector3d1;
      } else {
         Vector3d vector3d2 = getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_230268_1_.getBbWidth(), this.yRot + (p_230268_1_.getMainArm() == HandSide.LEFT ? 90.0F : -90.0F));
         Vector3d vector3d3 = this.getDismountLocationInDirection(vector3d2, p_230268_1_);
         return vector3d3 != null ? vector3d3 : this.position();
      }
   }

   protected void randomizeAttributes() {
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData(0.2F);
      }

      this.randomizeAttributes();
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.Direction facing) {
      if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
         return itemHandler.cast();
      return super.getCapability(capability, facing);
   }

   @Override
   protected void invalidateCaps() {
      super.invalidateCaps();
      if (itemHandler != null) {
         itemHandler.invalidate();
         itemHandler = null;
      }
   }
}
