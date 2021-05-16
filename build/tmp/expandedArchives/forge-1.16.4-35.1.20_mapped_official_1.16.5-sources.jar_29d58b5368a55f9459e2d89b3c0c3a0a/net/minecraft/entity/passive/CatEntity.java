package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatEntity extends TameableEntity {
   private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(Items.COD, Items.SALMON);
   private static final DataParameter<Integer> DATA_TYPE_ID = EntityDataManager.defineId(CatEntity.class, DataSerializers.INT);
   private static final DataParameter<Boolean> IS_LYING = EntityDataManager.defineId(CatEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> RELAX_STATE_ONE = EntityDataManager.defineId(CatEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> DATA_COLLAR_COLOR = EntityDataManager.defineId(CatEntity.class, DataSerializers.INT);
   public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (p_213410_0_) -> {
      p_213410_0_.put(0, new ResourceLocation("textures/entity/cat/tabby.png"));
      p_213410_0_.put(1, new ResourceLocation("textures/entity/cat/black.png"));
      p_213410_0_.put(2, new ResourceLocation("textures/entity/cat/red.png"));
      p_213410_0_.put(3, new ResourceLocation("textures/entity/cat/siamese.png"));
      p_213410_0_.put(4, new ResourceLocation("textures/entity/cat/british_shorthair.png"));
      p_213410_0_.put(5, new ResourceLocation("textures/entity/cat/calico.png"));
      p_213410_0_.put(6, new ResourceLocation("textures/entity/cat/persian.png"));
      p_213410_0_.put(7, new ResourceLocation("textures/entity/cat/ragdoll.png"));
      p_213410_0_.put(8, new ResourceLocation("textures/entity/cat/white.png"));
      p_213410_0_.put(9, new ResourceLocation("textures/entity/cat/jellie.png"));
      p_213410_0_.put(10, new ResourceLocation("textures/entity/cat/all_black.png"));
   });
   private CatEntity.AvoidPlayerGoal<PlayerEntity> avoidPlayersGoal;
   private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
   private float lieDownAmount;
   private float lieDownAmountO;
   private float lieDownAmountTail;
   private float lieDownAmountOTail;
   private float relaxStateOneAmount;
   private float relaxStateOneAmountO;

   public CatEntity(EntityType<? extends CatEntity> p_i50284_1_, World p_i50284_2_) {
      super(p_i50284_1_, p_i50284_2_);
   }

   public ResourceLocation getResourceLocation() {
      return TEXTURE_BY_TYPE.getOrDefault(this.getCatType(), TEXTURE_BY_TYPE.get(0));
   }

   protected void registerGoals() {
      this.temptGoal = new CatEntity.TemptGoal(this, 0.6D, TEMPT_INGREDIENT, true);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(1, new SitGoal(this));
      this.goalSelector.addGoal(2, new CatEntity.MorningGiftGoal(this));
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1D, 8));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
      this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8D));
      this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(10, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(12, new LookAtGoal(this, PlayerEntity.class, 10.0F));
      this.targetSelector.addGoal(1, new NonTamedTargetGoal<>(this, RabbitEntity.class, false, (Predicate<LivingEntity>)null));
      this.targetSelector.addGoal(1, new NonTamedTargetGoal<>(this, TurtleEntity.class, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
   }

   public int getCatType() {
      return this.entityData.get(DATA_TYPE_ID);
   }

   public void setCatType(int p_213422_1_) {
      if (p_213422_1_ < 0 || p_213422_1_ >= 11) {
         p_213422_1_ = this.random.nextInt(10);
      }

      this.entityData.set(DATA_TYPE_ID, p_213422_1_);
   }

   public void setLying(boolean p_213419_1_) {
      this.entityData.set(IS_LYING, p_213419_1_);
   }

   public boolean isLying() {
      return this.entityData.get(IS_LYING);
   }

   public void setRelaxStateOne(boolean p_213415_1_) {
      this.entityData.set(RELAX_STATE_ONE, p_213415_1_);
   }

   public boolean isRelaxStateOne() {
      return this.entityData.get(RELAX_STATE_ONE);
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor p_213417_1_) {
      this.entityData.set(DATA_COLLAR_COLOR, p_213417_1_.getId());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE_ID, 1);
      this.entityData.define(IS_LYING, false);
      this.entityData.define(RELAX_STATE_ONE, false);
      this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("CatType", this.getCatType());
      p_213281_1_.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setCatType(p_70037_1_.getInt("CatType"));
      if (p_70037_1_.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(p_70037_1_.getInt("CollarColor")));
      }

   }

   public void customServerAiStep() {
      if (this.getMoveControl().hasWanted()) {
         double d0 = this.getMoveControl().getSpeedModifier();
         if (d0 == 0.6D) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (d0 == 1.33D) {
            this.setPose(Pose.STANDING);
            this.setSprinting(true);
         } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
         }
      } else {
         this.setPose(Pose.STANDING);
         this.setSprinting(false);
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isTame()) {
         if (this.isInLove()) {
            return SoundEvents.CAT_PURR;
         } else {
            return this.random.nextInt(4) == 0 ? SoundEvents.CAT_PURREOW : SoundEvents.CAT_AMBIENT;
         }
      } else {
         return SoundEvents.CAT_STRAY_AMBIENT;
      }
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public void hiss() {
      this.playSound(SoundEvents.CAT_HISS, this.getSoundVolume(), this.getVoicePitch());
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CAT_DEATH;
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.ATTACK_DAMAGE, 3.0D);
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected void usePlayerItem(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
      if (this.isFood(p_175505_2_)) {
         this.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
      }

      super.usePlayerItem(p_175505_1_, p_175505_2_);
   }

   private float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      return p_70652_1_.hurt(DamageSource.mobAttack(this), this.getAttackDamage());
   }

   public void tick() {
      super.tick();
      if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTame() && this.tickCount % 100 == 0) {
         this.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
      }

      this.handleLieDown();
   }

   private void handleLieDown() {
      if ((this.isLying() || this.isRelaxStateOne()) && this.tickCount % 5 == 0) {
         this.playSound(SoundEvents.CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
      }

      this.updateLieDownAmount();
      this.updateRelaxStateOneAmount();
   }

   private void updateLieDownAmount() {
      this.lieDownAmountO = this.lieDownAmount;
      this.lieDownAmountOTail = this.lieDownAmountTail;
      if (this.isLying()) {
         this.lieDownAmount = Math.min(1.0F, this.lieDownAmount + 0.15F);
         this.lieDownAmountTail = Math.min(1.0F, this.lieDownAmountTail + 0.08F);
      } else {
         this.lieDownAmount = Math.max(0.0F, this.lieDownAmount - 0.22F);
         this.lieDownAmountTail = Math.max(0.0F, this.lieDownAmountTail - 0.13F);
      }

   }

   private void updateRelaxStateOneAmount() {
      this.relaxStateOneAmountO = this.relaxStateOneAmount;
      if (this.isRelaxStateOne()) {
         this.relaxStateOneAmount = Math.min(1.0F, this.relaxStateOneAmount + 0.1F);
      } else {
         this.relaxStateOneAmount = Math.max(0.0F, this.relaxStateOneAmount - 0.13F);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getLieDownAmount(float p_213408_1_) {
      return MathHelper.lerp(p_213408_1_, this.lieDownAmountO, this.lieDownAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public float getLieDownAmountTail(float p_213421_1_) {
      return MathHelper.lerp(p_213421_1_, this.lieDownAmountOTail, this.lieDownAmountTail);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRelaxStateOneAmount(float p_213424_1_) {
      return MathHelper.lerp(p_213424_1_, this.relaxStateOneAmountO, this.relaxStateOneAmount);
   }

   public CatEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      CatEntity catentity = EntityType.CAT.create(p_241840_1_);
      if (p_241840_2_ instanceof CatEntity) {
         if (this.random.nextBoolean()) {
            catentity.setCatType(this.getCatType());
         } else {
            catentity.setCatType(((CatEntity)p_241840_2_).getCatType());
         }

         if (this.isTame()) {
            catentity.setOwnerUUID(this.getOwnerUUID());
            catentity.setTame(true);
            if (this.random.nextBoolean()) {
               catentity.setCollarColor(this.getCollarColor());
            } else {
               catentity.setCollarColor(((CatEntity)p_241840_2_).getCollarColor());
            }
         }
      }

      return catentity;
   }

   public boolean canMate(AnimalEntity p_70878_1_) {
      if (!this.isTame()) {
         return false;
      } else if (!(p_70878_1_ instanceof CatEntity)) {
         return false;
      } else {
         CatEntity catentity = (CatEntity)p_70878_1_;
         return catentity.isTame() && super.canMate(p_70878_1_);
      }
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      if (p_213386_1_.getMoonBrightness() > 0.9F) {
         this.setCatType(this.random.nextInt(11));
      } else {
         this.setCatType(this.random.nextInt(10));
      }

      World world = p_213386_1_.getLevel();
      if (world instanceof ServerWorld && ((ServerWorld)world).structureFeatureManager().getStructureAt(this.blockPosition(), true, Structure.SWAMP_HUT).isValid()) {
         this.setCatType(10);
         this.setPersistenceRequired();
      }

      return p_213386_4_;
   }

   public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      Item item = itemstack.getItem();
      if (this.level.isClientSide) {
         if (this.isTame() && this.isOwnedBy(p_230254_1_)) {
            return ActionResultType.SUCCESS;
         } else {
            return !this.isFood(itemstack) || !(this.getHealth() < this.getMaxHealth()) && this.isTame() ? ActionResultType.PASS : ActionResultType.SUCCESS;
         }
      } else {
         if (this.isTame()) {
            if (this.isOwnedBy(p_230254_1_)) {
               if (!(item instanceof DyeItem)) {
                  if (item.isEdible() && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                     this.usePlayerItem(p_230254_1_, itemstack);
                     this.heal((float)item.getFoodProperties().getNutrition());
                     return ActionResultType.CONSUME;
                  }

                  ActionResultType actionresulttype = super.mobInteract(p_230254_1_, p_230254_2_);
                  if (!actionresulttype.consumesAction() || this.isBaby()) {
                     this.setOrderedToSit(!this.isOrderedToSit());
                  }

                  return actionresulttype;
               }

               DyeColor dyecolor = ((DyeItem)item).getDyeColor();
               if (dyecolor != this.getCollarColor()) {
                  this.setCollarColor(dyecolor);
                  if (!p_230254_1_.abilities.instabuild) {
                     itemstack.shrink(1);
                  }

                  this.setPersistenceRequired();
                  return ActionResultType.CONSUME;
               }
            }
         } else if (this.isFood(itemstack)) {
            this.usePlayerItem(p_230254_1_, itemstack);
            if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
               this.tame(p_230254_1_);
               this.setOrderedToSit(true);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.level.broadcastEntityEvent(this, (byte)6);
            }

            this.setPersistenceRequired();
            return ActionResultType.CONSUME;
         }

         ActionResultType actionresulttype1 = super.mobInteract(p_230254_1_, p_230254_2_);
         if (actionresulttype1.consumesAction()) {
            this.setPersistenceRequired();
         }

         return actionresulttype1;
      }
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return TEMPT_INGREDIENT.test(p_70877_1_);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.5F;
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return !this.isTame() && this.tickCount > 2400;
   }

   protected void reassessTameGoals() {
      if (this.avoidPlayersGoal == null) {
         this.avoidPlayersGoal = new CatEntity.AvoidPlayerGoal<>(this, PlayerEntity.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.avoidPlayersGoal);
      if (!this.isTame()) {
         this.goalSelector.addGoal(4, this.avoidPlayersGoal);
      }

   }

   static class AvoidPlayerGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final CatEntity cat;

      public AvoidPlayerGoal(CatEntity p_i50440_1_, Class<T> p_i50440_2_, float p_i50440_3_, double p_i50440_4_, double p_i50440_6_) {
         super(p_i50440_1_, p_i50440_2_, p_i50440_3_, p_i50440_4_, p_i50440_6_, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test);
         this.cat = p_i50440_1_;
      }

      public boolean canUse() {
         return !this.cat.isTame() && super.canUse();
      }

      public boolean canContinueToUse() {
         return !this.cat.isTame() && super.canContinueToUse();
      }
   }

   static class MorningGiftGoal extends Goal {
      private final CatEntity cat;
      private PlayerEntity ownerPlayer;
      private BlockPos goalPos;
      private int onBedTicks;

      public MorningGiftGoal(CatEntity p_i50439_1_) {
         this.cat = p_i50439_1_;
      }

      public boolean canUse() {
         if (!this.cat.isTame()) {
            return false;
         } else if (this.cat.isOrderedToSit()) {
            return false;
         } else {
            LivingEntity livingentity = this.cat.getOwner();
            if (livingentity instanceof PlayerEntity) {
               this.ownerPlayer = (PlayerEntity)livingentity;
               if (!livingentity.isSleeping()) {
                  return false;
               }

               if (this.cat.distanceToSqr(this.ownerPlayer) > 100.0D) {
                  return false;
               }

               BlockPos blockpos = this.ownerPlayer.blockPosition();
               BlockState blockstate = this.cat.level.getBlockState(blockpos);
               if (blockstate.getBlock().is(BlockTags.BEDS)) {
                  this.goalPos = blockstate.getOptionalValue(BedBlock.FACING).map((p_234186_1_) -> {
                     return blockpos.relative(p_234186_1_.getOpposite());
                  }).orElseGet(() -> {
                     return new BlockPos(blockpos);
                  });
                  return !this.spaceIsOccupied();
               }
            }

            return false;
         }
      }

      private boolean spaceIsOccupied() {
         for(CatEntity catentity : this.cat.level.getEntitiesOfClass(CatEntity.class, (new AxisAlignedBB(this.goalPos)).inflate(2.0D))) {
            if (catentity != this.cat && (catentity.isLying() || catentity.isRelaxStateOne())) {
               return true;
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.cat.isTame() && !this.cat.isOrderedToSit() && this.ownerPlayer != null && this.ownerPlayer.isSleeping() && this.goalPos != null && !this.spaceIsOccupied();
      }

      public void start() {
         if (this.goalPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), (double)1.1F);
         }

      }

      public void stop() {
         this.cat.setLying(false);
         float f = this.cat.level.getTimeOfDay(1.0F);
         if (this.ownerPlayer.getSleepTimer() >= 100 && (double)f > 0.77D && (double)f < 0.8D && (double)this.cat.level.getRandom().nextFloat() < 0.7D) {
            this.giveMorningGift();
         }

         this.onBedTicks = 0;
         this.cat.setRelaxStateOne(false);
         this.cat.getNavigation().stop();
      }

      private void giveMorningGift() {
         Random random = this.cat.getRandom();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         blockpos$mutable.set(this.cat.blockPosition());
         this.cat.randomTeleport((double)(blockpos$mutable.getX() + random.nextInt(11) - 5), (double)(blockpos$mutable.getY() + random.nextInt(5) - 2), (double)(blockpos$mutable.getZ() + random.nextInt(11) - 5), false);
         blockpos$mutable.set(this.cat.blockPosition());
         LootTable loottable = this.cat.level.getServer().getLootTables().get(LootTables.CAT_MORNING_GIFT);
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.cat.level)).withParameter(LootParameters.ORIGIN, this.cat.position()).withParameter(LootParameters.THIS_ENTITY, this.cat).withRandom(random);

         for(ItemStack itemstack : loottable.getRandomItems(lootcontext$builder.create(LootParameterSets.GIFT))) {
            this.cat.level.addFreshEntity(new ItemEntity(this.cat.level, (double)blockpos$mutable.getX() - (double)MathHelper.sin(this.cat.yBodyRot * ((float)Math.PI / 180F)), (double)blockpos$mutable.getY(), (double)blockpos$mutable.getZ() + (double)MathHelper.cos(this.cat.yBodyRot * ((float)Math.PI / 180F)), itemstack));
         }

      }

      public void tick() {
         if (this.ownerPlayer != null && this.goalPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), (double)1.1F);
            if (this.cat.distanceToSqr(this.ownerPlayer) < 2.5D) {
               ++this.onBedTicks;
               if (this.onBedTicks > 16) {
                  this.cat.setLying(true);
                  this.cat.setRelaxStateOne(false);
               } else {
                  this.cat.lookAt(this.ownerPlayer, 45.0F, 45.0F);
                  this.cat.setRelaxStateOne(true);
               }
            } else {
               this.cat.setLying(false);
            }
         }

      }
   }

   static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
      @Nullable
      private PlayerEntity selectedPlayer;
      private final CatEntity cat;

      public TemptGoal(CatEntity p_i50438_1_, double p_i50438_2_, Ingredient p_i50438_4_, boolean p_i50438_5_) {
         super(p_i50438_1_, p_i50438_2_, p_i50438_4_, p_i50438_5_);
         this.cat = p_i50438_1_;
      }

      public void tick() {
         super.tick();
         if (this.selectedPlayer == null && this.mob.getRandom().nextInt(600) == 0) {
            this.selectedPlayer = this.player;
         } else if (this.mob.getRandom().nextInt(500) == 0) {
            this.selectedPlayer = null;
         }

      }

      protected boolean canScare() {
         return this.selectedPlayer != null && this.selectedPlayer.equals(this.player) ? false : super.canScare();
      }

      public boolean canUse() {
         return super.canUse() && !this.cat.isTame();
      }
   }
}
