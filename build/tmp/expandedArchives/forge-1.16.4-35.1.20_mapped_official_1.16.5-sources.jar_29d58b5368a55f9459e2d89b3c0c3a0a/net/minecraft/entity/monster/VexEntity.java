package net.minecraft.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class VexEntity extends MonsterEntity {
   protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(VexEntity.class, DataSerializers.BYTE);
   private MobEntity owner;
   @Nullable
   private BlockPos boundOrigin;
   private boolean hasLimitedLife;
   private int limitedLifeTicks;

   public VexEntity(EntityType<? extends VexEntity> p_i50190_1_, World p_i50190_2_) {
      super(p_i50190_1_, p_i50190_2_);
      this.moveControl = new VexEntity.MoveHelperController(this);
      this.xpReward = 3;
   }

   public void move(MoverType p_213315_1_, Vector3d p_213315_2_) {
      super.move(p_213315_1_, p_213315_2_);
      this.checkInsideBlocks();
   }

   public void tick() {
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
      if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
         this.limitedLifeTicks = 20;
         this.hurt(DamageSource.STARVE, 1.0F);
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(4, new VexEntity.ChargeAttackGoal());
      this.goalSelector.addGoal(8, new VexEntity.MoveRandomGoal());
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
      this.targetSelector.addGoal(2, new VexEntity.CopyOwnerTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MonsterEntity.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ATTACK_DAMAGE, 4.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("BoundX")) {
         this.boundOrigin = new BlockPos(p_70037_1_.getInt("BoundX"), p_70037_1_.getInt("BoundY"), p_70037_1_.getInt("BoundZ"));
      }

      if (p_70037_1_.contains("LifeTicks")) {
         this.setLimitedLife(p_70037_1_.getInt("LifeTicks"));
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (this.boundOrigin != null) {
         p_213281_1_.putInt("BoundX", this.boundOrigin.getX());
         p_213281_1_.putInt("BoundY", this.boundOrigin.getY());
         p_213281_1_.putInt("BoundZ", this.boundOrigin.getZ());
      }

      if (this.hasLimitedLife) {
         p_213281_1_.putInt("LifeTicks", this.limitedLifeTicks);
      }

   }

   public MobEntity getOwner() {
      return this.owner;
   }

   @Nullable
   public BlockPos getBoundOrigin() {
      return this.boundOrigin;
   }

   public void setBoundOrigin(@Nullable BlockPos p_190651_1_) {
      this.boundOrigin = p_190651_1_;
   }

   private boolean getVexFlag(int p_190656_1_) {
      int i = this.entityData.get(DATA_FLAGS_ID);
      return (i & p_190656_1_) != 0;
   }

   private void setVexFlag(int p_190660_1_, boolean p_190660_2_) {
      int i = this.entityData.get(DATA_FLAGS_ID);
      if (p_190660_2_) {
         i = i | p_190660_1_;
      } else {
         i = i & ~p_190660_1_;
      }

      this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setIsCharging(boolean p_190648_1_) {
      this.setVexFlag(1, p_190648_1_);
   }

   public void setOwner(MobEntity p_190658_1_) {
      this.owner = p_190658_1_;
   }

   public void setLimitedLife(int p_190653_1_) {
      this.hasLimitedLife = true;
      this.limitedLifeTicks = p_190653_1_;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VEX_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.VEX_HURT;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.populateDefaultEquipmentSlots(p_213386_2_);
      this.populateDefaultEquipmentEnchantments(p_213386_2_);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
   }

   class ChargeAttackGoal extends Goal {
      public ChargeAttackGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (VexEntity.this.getTarget() != null && !VexEntity.this.getMoveControl().hasWanted() && VexEntity.this.random.nextInt(7) == 0) {
            return VexEntity.this.distanceToSqr(VexEntity.this.getTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return VexEntity.this.getMoveControl().hasWanted() && VexEntity.this.isCharging() && VexEntity.this.getTarget() != null && VexEntity.this.getTarget().isAlive();
      }

      public void start() {
         LivingEntity livingentity = VexEntity.this.getTarget();
         Vector3d vector3d = livingentity.getEyePosition(1.0F);
         VexEntity.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
         VexEntity.this.setIsCharging(true);
         VexEntity.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
      }

      public void stop() {
         VexEntity.this.setIsCharging(false);
      }

      public void tick() {
         LivingEntity livingentity = VexEntity.this.getTarget();
         if (VexEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
            VexEntity.this.doHurtTarget(livingentity);
            VexEntity.this.setIsCharging(false);
         } else {
            double d0 = VexEntity.this.distanceToSqr(livingentity);
            if (d0 < 9.0D) {
               Vector3d vector3d = livingentity.getEyePosition(1.0F);
               VexEntity.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
            }
         }

      }
   }

   class CopyOwnerTargetGoal extends TargetGoal {
      private final EntityPredicate copyOwnerTargeting = (new EntityPredicate()).allowUnseeable().ignoreInvisibilityTesting();

      public CopyOwnerTargetGoal(CreatureEntity p_i47231_2_) {
         super(p_i47231_2_, false);
      }

      public boolean canUse() {
         return VexEntity.this.owner != null && VexEntity.this.owner.getTarget() != null && this.canAttack(VexEntity.this.owner.getTarget(), this.copyOwnerTargeting);
      }

      public void start() {
         VexEntity.this.setTarget(VexEntity.this.owner.getTarget());
         super.start();
      }
   }

   class MoveHelperController extends MovementController {
      public MoveHelperController(VexEntity p_i47230_2_) {
         super(p_i47230_2_);
      }

      public void tick() {
         if (this.operation == MovementController.Action.MOVE_TO) {
            Vector3d vector3d = new Vector3d(this.wantedX - VexEntity.this.getX(), this.wantedY - VexEntity.this.getY(), this.wantedZ - VexEntity.this.getZ());
            double d0 = vector3d.length();
            if (d0 < VexEntity.this.getBoundingBox().getSize()) {
               this.operation = MovementController.Action.WAIT;
               VexEntity.this.setDeltaMovement(VexEntity.this.getDeltaMovement().scale(0.5D));
            } else {
               VexEntity.this.setDeltaMovement(VexEntity.this.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));
               if (VexEntity.this.getTarget() == null) {
                  Vector3d vector3d1 = VexEntity.this.getDeltaMovement();
                  VexEntity.this.yRot = -((float)MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float)Math.PI);
                  VexEntity.this.yBodyRot = VexEntity.this.yRot;
               } else {
                  double d2 = VexEntity.this.getTarget().getX() - VexEntity.this.getX();
                  double d1 = VexEntity.this.getTarget().getZ() - VexEntity.this.getZ();
                  VexEntity.this.yRot = -((float)MathHelper.atan2(d2, d1)) * (180F / (float)Math.PI);
                  VexEntity.this.yBodyRot = VexEntity.this.yRot;
               }
            }

         }
      }
   }

   class MoveRandomGoal extends Goal {
      public MoveRandomGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !VexEntity.this.getMoveControl().hasWanted() && VexEntity.this.random.nextInt(7) == 0;
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void tick() {
         BlockPos blockpos = VexEntity.this.getBoundOrigin();
         if (blockpos == null) {
            blockpos = VexEntity.this.blockPosition();
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.offset(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
            if (VexEntity.this.level.isEmptyBlock(blockpos1)) {
               VexEntity.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
               if (VexEntity.this.getTarget() == null) {
                  VexEntity.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
