package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RabbitEntity extends AnimalEntity {
   private static final DataParameter<Integer> DATA_TYPE_ID = EntityDataManager.defineId(RabbitEntity.class, DataSerializers.INT);
   private static final ResourceLocation KILLER_BUNNY = new ResourceLocation("killer_bunny");
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int jumpDelayTicks;
   private int moreCarrotTicks;

   public RabbitEntity(EntityType<? extends RabbitEntity> p_i50247_1_, World p_i50247_2_) {
      super(p_i50247_1_, p_i50247_2_);
      this.jumpControl = new RabbitEntity.JumpHelperController(this);
      this.moveControl = new RabbitEntity.MoveHelperController(this);
      this.setSpeedModifier(0.0D);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(1, new RabbitEntity.PanicGoal(this, 2.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
      this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal<>(this, WolfEntity.class, 10.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal<>(this, MonsterEntity.class, 4.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(5, new RabbitEntity.RaidFarmGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));
   }

   protected float getJumpPower() {
      if (!this.horizontalCollision && (!this.moveControl.hasWanted() || !(this.moveControl.getWantedY() > this.getY() + 0.5D))) {
         Path path = this.navigation.getPath();
         if (path != null && !path.isDone()) {
            Vector3d vector3d = path.getNextEntityPos(this);
            if (vector3d.y > this.getY() + 0.5D) {
               return 0.5F;
            }
         }

         return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F : 0.3F;
      } else {
         return 0.5F;
      }
   }

   protected void jumpFromGround() {
      super.jumpFromGround();
      double d0 = this.moveControl.getSpeedModifier();
      if (d0 > 0.0D) {
         double d1 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if (d1 < 0.01D) {
            this.moveRelative(0.1F, new Vector3d(0.0D, 0.0D, 1.0D));
         }
      }

      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getJumpCompletion(float p_175521_1_) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + p_175521_1_) / (float)this.jumpDuration;
   }

   public void setSpeedModifier(double p_175515_1_) {
      this.getNavigation().setSpeedModifier(p_175515_1_);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), p_175515_1_);
   }

   public void setJumping(boolean p_70637_1_) {
      super.setJumping(p_70637_1_);
      if (p_70637_1_) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE_ID, 0);
   }

   public void customServerAiStep() {
      if (this.jumpDelayTicks > 0) {
         --this.jumpDelayTicks;
      }

      if (this.moreCarrotTicks > 0) {
         this.moreCarrotTicks -= this.random.nextInt(3);
         if (this.moreCarrotTicks < 0) {
            this.moreCarrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.getRabbitType() == 99 && this.jumpDelayTicks == 0) {
            LivingEntity livingentity = this.getTarget();
            if (livingentity != null && this.distanceToSqr(livingentity) < 16.0D) {
               this.facePoint(livingentity.getX(), livingentity.getZ());
               this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.moveControl.getSpeedModifier());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         RabbitEntity.JumpHelperController rabbitentity$jumphelpercontroller = (RabbitEntity.JumpHelperController)this.jumpControl;
         if (!rabbitentity$jumphelpercontroller.wantJump()) {
            if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
               Path path = this.navigation.getPath();
               Vector3d vector3d = new Vector3d(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
               if (path != null && !path.isDone()) {
                  vector3d = path.getNextEntityPos(this);
               }

               this.facePoint(vector3d.x, vector3d.z);
               this.startJumping();
            }
         } else if (!rabbitentity$jumphelpercontroller.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }

   private void facePoint(double p_175533_1_, double p_175533_3_) {
      this.yRot = (float)(MathHelper.atan2(p_175533_3_ - this.getZ(), p_175533_1_ - this.getX()) * (double)(180F / (float)Math.PI)) - 90.0F;
   }

   private void enableJumpControl() {
      ((RabbitEntity.JumpHelperController)this.jumpControl).setCanJump(true);
   }

   private void disableJumpControl() {
      ((RabbitEntity.JumpHelperController)this.jumpControl).setCanJump(false);
   }

   private void setLandingDelay() {
      if (this.moveControl.getSpeedModifier() < 2.2D) {
         this.jumpDelayTicks = 10;
      } else {
         this.jumpDelayTicks = 1;
      }

   }

   private void checkLandingDelay() {
      this.setLandingDelay();
      this.disableJumpControl();
   }

   public void aiStep() {
      super.aiStep();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("RabbitType", this.getRabbitType());
      p_213281_1_.putInt("MoreCarrotTicks", this.moreCarrotTicks);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setRabbitType(p_70037_1_.getInt("RabbitType"));
      this.moreCarrotTicks = p_70037_1_.getInt("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RABBIT_DEATH;
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      if (this.getRabbitType() == 99) {
         this.playSound(SoundEvents.RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         return p_70652_1_.hurt(DamageSource.mobAttack(this), 8.0F);
      } else {
         return p_70652_1_.hurt(DamageSource.mobAttack(this), 3.0F);
      }
   }

   public SoundCategory getSoundSource() {
      return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : super.hurt(p_70097_1_, p_70097_2_);
   }

   private boolean isTemptingItem(Item p_175525_1_) {
      return p_175525_1_ == Items.CARROT || p_175525_1_ == Items.GOLDEN_CARROT || p_175525_1_ == Blocks.DANDELION.asItem();
   }

   public RabbitEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      RabbitEntity rabbitentity = EntityType.RABBIT.create(p_241840_1_);
      int i = this.getRandomRabbitType(p_241840_1_);
      if (this.random.nextInt(20) != 0) {
         if (p_241840_2_ instanceof RabbitEntity && this.random.nextBoolean()) {
            i = ((RabbitEntity)p_241840_2_).getRabbitType();
         } else {
            i = this.getRabbitType();
         }
      }

      rabbitentity.setRabbitType(i);
      return rabbitentity;
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return this.isTemptingItem(p_70877_1_.getItem());
   }

   public int getRabbitType() {
      return this.entityData.get(DATA_TYPE_ID);
   }

   public void setRabbitType(int p_175529_1_) {
      if (p_175529_1_ == 99) {
         this.getAttribute(Attributes.ARMOR).setBaseValue(8.0D);
         this.goalSelector.addGoal(4, new RabbitEntity.EvilAttackGoal(this));
         this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, WolfEntity.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(new TranslationTextComponent(Util.makeDescriptionId("entity", KILLER_BUNNY)));
         }
      }

      this.entityData.set(DATA_TYPE_ID, p_175529_1_);
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      int i = this.getRandomRabbitType(p_213386_1_);
      if (p_213386_4_ instanceof RabbitEntity.RabbitData) {
         i = ((RabbitEntity.RabbitData)p_213386_4_).rabbitType;
      } else {
         p_213386_4_ = new RabbitEntity.RabbitData(i);
      }

      this.setRabbitType(i);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   private int getRandomRabbitType(IWorld p_213610_1_) {
      Biome biome = p_213610_1_.getBiome(this.blockPosition());
      int i = this.random.nextInt(100);
      if (biome.getPrecipitation() == Biome.RainType.SNOW) {
         return i < 80 ? 1 : 3;
      } else if (biome.getBiomeCategory() == Biome.Category.DESERT) {
         return 4;
      } else {
         return i < 50 ? 0 : (i < 90 ? 5 : 2);
      }
   }

   public static boolean checkRabbitSpawnRules(EntityType<RabbitEntity> p_223321_0_, IWorld p_223321_1_, SpawnReason p_223321_2_, BlockPos p_223321_3_, Random p_223321_4_) {
      BlockState blockstate = p_223321_1_.getBlockState(p_223321_3_.below());
      return (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.SNOW) || blockstate.is(Blocks.SAND)) && p_223321_1_.getRawBrightness(p_223321_3_, 0) > 8;
   }

   private boolean wantsMoreFood() {
      return this.moreCarrotTicks == 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 1) {
         this.spawnSprintParticle();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getLeashOffset() {
      return new Vector3d(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   static class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
      private final RabbitEntity rabbit;

      public AvoidEntityGoal(RabbitEntity p_i46403_1_, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_) {
         super(p_i46403_1_, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
         this.rabbit = p_i46403_1_;
      }

      public boolean canUse() {
         return this.rabbit.getRabbitType() != 99 && super.canUse();
      }
   }

   static class EvilAttackGoal extends MeleeAttackGoal {
      public EvilAttackGoal(RabbitEntity p_i45867_1_) {
         super(p_i45867_1_, 1.4D, true);
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         return (double)(4.0F + p_179512_1_.getBbWidth());
      }
   }

   public class JumpHelperController extends JumpController {
      private final RabbitEntity rabbit;
      private boolean canJump;

      public JumpHelperController(RabbitEntity p_i45863_2_) {
         super(p_i45863_2_);
         this.rabbit = p_i45863_2_;
      }

      public boolean wantJump() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean p_180066_1_) {
         this.canJump = p_180066_1_;
      }

      public void tick() {
         if (this.jump) {
            this.rabbit.startJumping();
            this.jump = false;
         }

      }
   }

   static class MoveHelperController extends MovementController {
      private final RabbitEntity rabbit;
      private double nextJumpSpeed;

      public MoveHelperController(RabbitEntity p_i45862_1_) {
         super(p_i45862_1_);
         this.rabbit = p_i45862_1_;
      }

      public void tick() {
         if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitEntity.JumpHelperController)this.rabbit.jumpControl).wantJump()) {
            this.rabbit.setSpeedModifier(0.0D);
         } else if (this.hasWanted()) {
            this.rabbit.setSpeedModifier(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setWantedPosition(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
         if (this.rabbit.isInWater()) {
            p_75642_7_ = 1.5D;
         }

         super.setWantedPosition(p_75642_1_, p_75642_3_, p_75642_5_, p_75642_7_);
         if (p_75642_7_ > 0.0D) {
            this.nextJumpSpeed = p_75642_7_;
         }

      }
   }

   static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      private final RabbitEntity rabbit;

      public PanicGoal(RabbitEntity p_i45861_1_, double p_i45861_2_) {
         super(p_i45861_1_, p_i45861_2_);
         this.rabbit = p_i45861_1_;
      }

      public void tick() {
         super.tick();
         this.rabbit.setSpeedModifier(this.speedModifier);
      }
   }

   public static class RabbitData extends AgeableEntity.AgeableData {
      public final int rabbitType;

      public RabbitData(int p_i45864_1_) {
         super(1.0F);
         this.rabbitType = p_i45864_1_;
      }
   }

   static class RaidFarmGoal extends MoveToBlockGoal {
      private final RabbitEntity rabbit;
      private boolean wantsToRaid;
      private boolean canRaid;

      public RaidFarmGoal(RabbitEntity p_i45860_1_) {
         super(p_i45860_1_, (double)0.7F, 16);
         this.rabbit = p_i45860_1_;
      }

      public boolean canUse() {
         if (this.nextStartTick <= 0) {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.rabbit.level, this.rabbit)) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.wantsMoreFood();
            this.wantsToRaid = true;
         }

         return super.canUse();
      }

      public boolean canContinueToUse() {
         return this.canRaid && super.canContinueToUse();
      }

      public void tick() {
         super.tick();
         this.rabbit.getLookControl().setLookAt((double)this.blockPos.getX() + 0.5D, (double)(this.blockPos.getY() + 1), (double)this.blockPos.getZ() + 0.5D, 10.0F, (float)this.rabbit.getMaxHeadXRot());
         if (this.isReachedTarget()) {
            World world = this.rabbit.level;
            BlockPos blockpos = this.blockPos.above();
            BlockState blockstate = world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (this.canRaid && block instanceof CarrotBlock) {
               Integer integer = blockstate.getValue(CarrotBlock.AGE);
               if (integer == 0) {
                  world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 2);
                  world.destroyBlock(blockpos, true, this.rabbit);
               } else {
                  world.setBlock(blockpos, blockstate.setValue(CarrotBlock.AGE, Integer.valueOf(integer - 1)), 2);
                  world.levelEvent(2001, blockpos, Block.getId(blockstate));
               }

               this.rabbit.moreCarrotTicks = 40;
            }

            this.canRaid = false;
            this.nextStartTick = 10;
         }

      }

      protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         Block block = p_179488_1_.getBlockState(p_179488_2_).getBlock();
         if (block == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid) {
            p_179488_2_ = p_179488_2_.above();
            BlockState blockstate = p_179488_1_.getBlockState(p_179488_2_);
            block = blockstate.getBlock();
            if (block instanceof CarrotBlock && ((CarrotBlock)block).isMaxAge(blockstate)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }
}
