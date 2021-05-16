package net.minecraft.entity.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmorStandEntity extends LivingEntity {
   private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);
   private static final EntitySize MARKER_DIMENSIONS = new EntitySize(0.0F, 0.0F, true);
   private static final EntitySize BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5F);
   public static final DataParameter<Byte> DATA_CLIENT_FLAGS = EntityDataManager.defineId(ArmorStandEntity.class, DataSerializers.BYTE);
   public static final DataParameter<Rotations> DATA_HEAD_POSE = EntityDataManager.defineId(ArmorStandEntity.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> DATA_BODY_POSE = EntityDataManager.defineId(ArmorStandEntity.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> DATA_LEFT_ARM_POSE = EntityDataManager.defineId(ArmorStandEntity.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> DATA_RIGHT_ARM_POSE = EntityDataManager.defineId(ArmorStandEntity.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> DATA_LEFT_LEG_POSE = EntityDataManager.defineId(ArmorStandEntity.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> DATA_RIGHT_LEG_POSE = EntityDataManager.defineId(ArmorStandEntity.class, DataSerializers.ROTATIONS);
   private static final Predicate<Entity> RIDABLE_MINECARTS = (p_200617_0_) -> {
      return p_200617_0_ instanceof AbstractMinecartEntity && ((AbstractMinecartEntity)p_200617_0_).canBeRidden();
   };
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   private boolean invisible;
   public long lastHit;
   private int disabledSlots;
   private Rotations headPose = DEFAULT_HEAD_POSE;
   private Rotations bodyPose = DEFAULT_BODY_POSE;
   private Rotations leftArmPose = DEFAULT_LEFT_ARM_POSE;
   private Rotations rightArmPose = DEFAULT_RIGHT_ARM_POSE;
   private Rotations leftLegPose = DEFAULT_LEFT_LEG_POSE;
   private Rotations rightLegPose = DEFAULT_RIGHT_LEG_POSE;

   public ArmorStandEntity(EntityType<? extends ArmorStandEntity> p_i50225_1_, World p_i50225_2_) {
      super(p_i50225_1_, p_i50225_2_);
      this.maxUpStep = 0.0F;
   }

   public ArmorStandEntity(World p_i45855_1_, double p_i45855_2_, double p_i45855_4_, double p_i45855_6_) {
      this(EntityType.ARMOR_STAND, p_i45855_1_);
      this.setPos(p_i45855_2_, p_i45855_4_, p_i45855_6_);
   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   private boolean hasPhysics() {
      return !this.isMarker() && !this.isNoGravity();
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && this.hasPhysics();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CLIENT_FLAGS, (byte)0);
      this.entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
      this.entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
      this.entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
      this.entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
      this.entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
      this.entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
   }

   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
      switch(p_184582_1_.getType()) {
      case HAND:
         return this.handItems.get(p_184582_1_.getIndex());
      case ARMOR:
         return this.armorItems.get(p_184582_1_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      switch(p_184201_1_.getType()) {
      case HAND:
         this.playEquipSound(p_184201_2_);
         this.handItems.set(p_184201_1_.getIndex(), p_184201_2_);
         break;
      case ARMOR:
         this.playEquipSound(p_184201_2_);
         this.armorItems.set(p_184201_1_.getIndex(), p_184201_2_);
      }

   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      EquipmentSlotType equipmentslottype;
      if (p_174820_1_ == 98) {
         equipmentslottype = EquipmentSlotType.MAINHAND;
      } else if (p_174820_1_ == 99) {
         equipmentslottype = EquipmentSlotType.OFFHAND;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.HEAD.getIndex()) {
         equipmentslottype = EquipmentSlotType.HEAD;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.CHEST.getIndex()) {
         equipmentslottype = EquipmentSlotType.CHEST;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.LEGS.getIndex()) {
         equipmentslottype = EquipmentSlotType.LEGS;
      } else {
         if (p_174820_1_ != 100 + EquipmentSlotType.FEET.getIndex()) {
            return false;
         }

         equipmentslottype = EquipmentSlotType.FEET;
      }

      if (!p_174820_2_.isEmpty() && !MobEntity.isValidSlotForItem(equipmentslottype, p_174820_2_) && equipmentslottype != EquipmentSlotType.HEAD) {
         return false;
      } else {
         this.setItemSlot(equipmentslottype, p_174820_2_);
         return true;
      }
   }

   public boolean canTakeItem(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(p_213365_1_);
      return this.getItemBySlot(equipmentslottype).isEmpty() && !this.isDisabled(equipmentslottype);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      ListNBT listnbt = new ListNBT();

      for(ItemStack itemstack : this.armorItems) {
         CompoundNBT compoundnbt = new CompoundNBT();
         if (!itemstack.isEmpty()) {
            itemstack.save(compoundnbt);
         }

         listnbt.add(compoundnbt);
      }

      p_213281_1_.put("ArmorItems", listnbt);
      ListNBT listnbt1 = new ListNBT();

      for(ItemStack itemstack1 : this.handItems) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         if (!itemstack1.isEmpty()) {
            itemstack1.save(compoundnbt1);
         }

         listnbt1.add(compoundnbt1);
      }

      p_213281_1_.put("HandItems", listnbt1);
      p_213281_1_.putBoolean("Invisible", this.isInvisible());
      p_213281_1_.putBoolean("Small", this.isSmall());
      p_213281_1_.putBoolean("ShowArms", this.isShowArms());
      p_213281_1_.putInt("DisabledSlots", this.disabledSlots);
      p_213281_1_.putBoolean("NoBasePlate", this.isNoBasePlate());
      if (this.isMarker()) {
         p_213281_1_.putBoolean("Marker", this.isMarker());
      }

      p_213281_1_.put("Pose", this.writePose());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("ArmorItems", 9)) {
         ListNBT listnbt = p_70037_1_.getList("ArmorItems", 10);

         for(int i = 0; i < this.armorItems.size(); ++i) {
            this.armorItems.set(i, ItemStack.of(listnbt.getCompound(i)));
         }
      }

      if (p_70037_1_.contains("HandItems", 9)) {
         ListNBT listnbt1 = p_70037_1_.getList("HandItems", 10);

         for(int j = 0; j < this.handItems.size(); ++j) {
            this.handItems.set(j, ItemStack.of(listnbt1.getCompound(j)));
         }
      }

      this.setInvisible(p_70037_1_.getBoolean("Invisible"));
      this.setSmall(p_70037_1_.getBoolean("Small"));
      this.setShowArms(p_70037_1_.getBoolean("ShowArms"));
      this.disabledSlots = p_70037_1_.getInt("DisabledSlots");
      this.setNoBasePlate(p_70037_1_.getBoolean("NoBasePlate"));
      this.setMarker(p_70037_1_.getBoolean("Marker"));
      this.noPhysics = !this.hasPhysics();
      CompoundNBT compoundnbt = p_70037_1_.getCompound("Pose");
      this.readPose(compoundnbt);
   }

   private void readPose(CompoundNBT p_175416_1_) {
      ListNBT listnbt = p_175416_1_.getList("Head", 5);
      this.setHeadPose(listnbt.isEmpty() ? DEFAULT_HEAD_POSE : new Rotations(listnbt));
      ListNBT listnbt1 = p_175416_1_.getList("Body", 5);
      this.setBodyPose(listnbt1.isEmpty() ? DEFAULT_BODY_POSE : new Rotations(listnbt1));
      ListNBT listnbt2 = p_175416_1_.getList("LeftArm", 5);
      this.setLeftArmPose(listnbt2.isEmpty() ? DEFAULT_LEFT_ARM_POSE : new Rotations(listnbt2));
      ListNBT listnbt3 = p_175416_1_.getList("RightArm", 5);
      this.setRightArmPose(listnbt3.isEmpty() ? DEFAULT_RIGHT_ARM_POSE : new Rotations(listnbt3));
      ListNBT listnbt4 = p_175416_1_.getList("LeftLeg", 5);
      this.setLeftLegPose(listnbt4.isEmpty() ? DEFAULT_LEFT_LEG_POSE : new Rotations(listnbt4));
      ListNBT listnbt5 = p_175416_1_.getList("RightLeg", 5);
      this.setRightLegPose(listnbt5.isEmpty() ? DEFAULT_RIGHT_LEG_POSE : new Rotations(listnbt5));
   }

   private CompoundNBT writePose() {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (!DEFAULT_HEAD_POSE.equals(this.headPose)) {
         compoundnbt.put("Head", this.headPose.save());
      }

      if (!DEFAULT_BODY_POSE.equals(this.bodyPose)) {
         compoundnbt.put("Body", this.bodyPose.save());
      }

      if (!DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
         compoundnbt.put("LeftArm", this.leftArmPose.save());
      }

      if (!DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
         compoundnbt.put("RightArm", this.rightArmPose.save());
      }

      if (!DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
         compoundnbt.put("LeftLeg", this.leftLegPose.save());
      }

      if (!DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
         compoundnbt.put("RightLeg", this.rightLegPose.save());
      }

      return compoundnbt;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity p_82167_1_) {
   }

   protected void pushEntities() {
      List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS);

      for(int i = 0; i < list.size(); ++i) {
         Entity entity = list.get(i);
         if (this.distanceToSqr(entity) <= 0.2D) {
            entity.push(this);
         }
      }

   }

   public ActionResultType interactAt(PlayerEntity p_184199_1_, Vector3d p_184199_2_, Hand p_184199_3_) {
      ItemStack itemstack = p_184199_1_.getItemInHand(p_184199_3_);
      if (!this.isMarker() && itemstack.getItem() != Items.NAME_TAG) {
         if (p_184199_1_.isSpectator()) {
            return ActionResultType.SUCCESS;
         } else if (p_184199_1_.level.isClientSide) {
            return ActionResultType.CONSUME;
         } else {
            EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(itemstack);
            if (itemstack.isEmpty()) {
               EquipmentSlotType equipmentslottype1 = this.getClickedSlot(p_184199_2_);
               EquipmentSlotType equipmentslottype2 = this.isDisabled(equipmentslottype1) ? equipmentslottype : equipmentslottype1;
               if (this.hasItemInSlot(equipmentslottype2) && this.swapItem(p_184199_1_, equipmentslottype2, itemstack, p_184199_3_)) {
                  return ActionResultType.SUCCESS;
               }
            } else {
               if (this.isDisabled(equipmentslottype)) {
                  return ActionResultType.FAIL;
               }

               if (equipmentslottype.getType() == EquipmentSlotType.Group.HAND && !this.isShowArms()) {
                  return ActionResultType.FAIL;
               }

               if (this.swapItem(p_184199_1_, equipmentslottype, itemstack, p_184199_3_)) {
                  return ActionResultType.SUCCESS;
               }
            }

            return ActionResultType.PASS;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   private EquipmentSlotType getClickedSlot(Vector3d p_190772_1_) {
      EquipmentSlotType equipmentslottype = EquipmentSlotType.MAINHAND;
      boolean flag = this.isSmall();
      double d0 = flag ? p_190772_1_.y * 2.0D : p_190772_1_.y;
      EquipmentSlotType equipmentslottype1 = EquipmentSlotType.FEET;
      if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.hasItemInSlot(equipmentslottype1)) {
         equipmentslottype = EquipmentSlotType.FEET;
      } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.hasItemInSlot(EquipmentSlotType.CHEST)) {
         equipmentslottype = EquipmentSlotType.CHEST;
      } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.hasItemInSlot(EquipmentSlotType.LEGS)) {
         equipmentslottype = EquipmentSlotType.LEGS;
      } else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlotType.HEAD)) {
         equipmentslottype = EquipmentSlotType.HEAD;
      } else if (!this.hasItemInSlot(EquipmentSlotType.MAINHAND) && this.hasItemInSlot(EquipmentSlotType.OFFHAND)) {
         equipmentslottype = EquipmentSlotType.OFFHAND;
      }

      return equipmentslottype;
   }

   private boolean isDisabled(EquipmentSlotType p_184796_1_) {
      return (this.disabledSlots & 1 << p_184796_1_.getFilterFlag()) != 0 || p_184796_1_.getType() == EquipmentSlotType.Group.HAND && !this.isShowArms();
   }

   private boolean swapItem(PlayerEntity p_226529_1_, EquipmentSlotType p_226529_2_, ItemStack p_226529_3_, Hand p_226529_4_) {
      ItemStack itemstack = this.getItemBySlot(p_226529_2_);
      if (!itemstack.isEmpty() && (this.disabledSlots & 1 << p_226529_2_.getFilterFlag() + 8) != 0) {
         return false;
      } else if (itemstack.isEmpty() && (this.disabledSlots & 1 << p_226529_2_.getFilterFlag() + 16) != 0) {
         return false;
      } else if (p_226529_1_.abilities.instabuild && itemstack.isEmpty() && !p_226529_3_.isEmpty()) {
         ItemStack itemstack2 = p_226529_3_.copy();
         itemstack2.setCount(1);
         this.setItemSlot(p_226529_2_, itemstack2);
         return true;
      } else if (!p_226529_3_.isEmpty() && p_226529_3_.getCount() > 1) {
         if (!itemstack.isEmpty()) {
            return false;
         } else {
            ItemStack itemstack1 = p_226529_3_.copy();
            itemstack1.setCount(1);
            this.setItemSlot(p_226529_2_, itemstack1);
            p_226529_3_.shrink(1);
            return true;
         }
      } else {
         this.setItemSlot(p_226529_2_, p_226529_3_);
         p_226529_1_.setItemInHand(p_226529_4_, itemstack);
         return true;
      }
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.level.isClientSide && !this.removed) {
         if (DamageSource.OUT_OF_WORLD.equals(p_70097_1_)) {
            this.remove();
            return false;
         } else if (!this.isInvulnerableTo(p_70097_1_) && !this.invisible && !this.isMarker()) {
            if (p_70097_1_.isExplosion()) {
               this.brokenByAnything(p_70097_1_);
               this.remove();
               return false;
            } else if (DamageSource.IN_FIRE.equals(p_70097_1_)) {
               if (this.isOnFire()) {
                  this.causeDamage(p_70097_1_, 0.15F);
               } else {
                  this.setSecondsOnFire(5);
               }

               return false;
            } else if (DamageSource.ON_FIRE.equals(p_70097_1_) && this.getHealth() > 0.5F) {
               this.causeDamage(p_70097_1_, 4.0F);
               return false;
            } else {
               boolean flag = p_70097_1_.getDirectEntity() instanceof AbstractArrowEntity;
               boolean flag1 = flag && ((AbstractArrowEntity)p_70097_1_.getDirectEntity()).getPierceLevel() > 0;
               boolean flag2 = "player".equals(p_70097_1_.getMsgId());
               if (!flag2 && !flag) {
                  return false;
               } else if (p_70097_1_.getEntity() instanceof PlayerEntity && !((PlayerEntity)p_70097_1_.getEntity()).abilities.mayBuild) {
                  return false;
               } else if (p_70097_1_.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.showBreakingParticles();
                  this.remove();
                  return flag1;
               } else {
                  long i = this.level.getGameTime();
                  if (i - this.lastHit > 5L && !flag) {
                     this.level.broadcastEntityEvent(this, (byte)32);
                     this.lastHit = i;
                  } else {
                     this.brokenByPlayer(p_70097_1_);
                     this.showBreakingParticles();
                     this.remove();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 32) {
         if (this.level.isClientSide) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level.getGameTime();
         }
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(d0) || d0 == 0.0D) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return p_70112_1_ < d0 * d0;
   }

   private void showBreakingParticles() {
      if (this.level instanceof ServerWorld) {
         ((ServerWorld)this.level).sendParticles(new BlockParticleData(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
      }

   }

   private void causeDamage(DamageSource p_213817_1_, float p_213817_2_) {
      float f = this.getHealth();
      f = f - p_213817_2_;
      if (f <= 0.5F) {
         this.brokenByAnything(p_213817_1_);
         this.remove();
      } else {
         this.setHealth(f);
      }

   }

   private void brokenByPlayer(DamageSource p_213815_1_) {
      Block.popResource(this.level, this.blockPosition(), new ItemStack(Items.ARMOR_STAND));
      this.brokenByAnything(p_213815_1_);
   }

   private void brokenByAnything(DamageSource p_213816_1_) {
      this.playBrokenSound();
      this.dropAllDeathLoot(p_213816_1_);

      for(int i = 0; i < this.handItems.size(); ++i) {
         ItemStack itemstack = this.handItems.get(i);
         if (!itemstack.isEmpty()) {
            Block.popResource(this.level, this.blockPosition().above(), itemstack);
            this.handItems.set(i, ItemStack.EMPTY);
         }
      }

      for(int j = 0; j < this.armorItems.size(); ++j) {
         ItemStack itemstack1 = this.armorItems.get(j);
         if (!itemstack1.isEmpty()) {
            Block.popResource(this.level, this.blockPosition().above(), itemstack1);
            this.armorItems.set(j, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   protected float tickHeadTurn(float p_110146_1_, float p_110146_2_) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.yRot;
      return 0.0F;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * (this.isBaby() ? 0.5F : 0.9F);
   }

   public double getMyRidingOffset() {
      return this.isMarker() ? 0.0D : (double)0.1F;
   }

   public void travel(Vector3d p_213352_1_) {
      if (this.hasPhysics()) {
         super.travel(p_213352_1_);
      }
   }

   public void setYBodyRot(float p_181013_1_) {
      this.yBodyRotO = this.yRotO = p_181013_1_;
      this.yHeadRotO = this.yHeadRot = p_181013_1_;
   }

   public void setYHeadRot(float p_70034_1_) {
      this.yBodyRotO = this.yRotO = p_70034_1_;
      this.yHeadRotO = this.yHeadRot = p_70034_1_;
   }

   public void tick() {
      super.tick();
      Rotations rotations = this.entityData.get(DATA_HEAD_POSE);
      if (!this.headPose.equals(rotations)) {
         this.setHeadPose(rotations);
      }

      Rotations rotations1 = this.entityData.get(DATA_BODY_POSE);
      if (!this.bodyPose.equals(rotations1)) {
         this.setBodyPose(rotations1);
      }

      Rotations rotations2 = this.entityData.get(DATA_LEFT_ARM_POSE);
      if (!this.leftArmPose.equals(rotations2)) {
         this.setLeftArmPose(rotations2);
      }

      Rotations rotations3 = this.entityData.get(DATA_RIGHT_ARM_POSE);
      if (!this.rightArmPose.equals(rotations3)) {
         this.setRightArmPose(rotations3);
      }

      Rotations rotations4 = this.entityData.get(DATA_LEFT_LEG_POSE);
      if (!this.leftLegPose.equals(rotations4)) {
         this.setLeftLegPose(rotations4);
      }

      Rotations rotations5 = this.entityData.get(DATA_RIGHT_LEG_POSE);
      if (!this.rightLegPose.equals(rotations5)) {
         this.setRightLegPose(rotations5);
      }

   }

   protected void updateInvisibilityStatus() {
      this.setInvisible(this.invisible);
   }

   public void setInvisible(boolean p_82142_1_) {
      this.invisible = p_82142_1_;
      super.setInvisible(p_82142_1_);
   }

   public boolean isBaby() {
      return this.isSmall();
   }

   public void kill() {
      this.remove();
   }

   public boolean ignoreExplosion() {
      return this.isInvisible();
   }

   public PushReaction getPistonPushReaction() {
      return this.isMarker() ? PushReaction.IGNORE : super.getPistonPushReaction();
   }

   private void setSmall(boolean p_175420_1_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 1, p_175420_1_));
   }

   public boolean isSmall() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
   }

   private void setShowArms(boolean p_175413_1_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 4, p_175413_1_));
   }

   public boolean isShowArms() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
   }

   private void setNoBasePlate(boolean p_175426_1_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 8, p_175426_1_));
   }

   public boolean isNoBasePlate() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 8) != 0;
   }

   private void setMarker(boolean p_181027_1_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 16, p_181027_1_));
   }

   public boolean isMarker() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 16) != 0;
   }

   private byte setBit(byte p_184797_1_, int p_184797_2_, boolean p_184797_3_) {
      if (p_184797_3_) {
         p_184797_1_ = (byte)(p_184797_1_ | p_184797_2_);
      } else {
         p_184797_1_ = (byte)(p_184797_1_ & ~p_184797_2_);
      }

      return p_184797_1_;
   }

   public void setHeadPose(Rotations p_175415_1_) {
      this.headPose = p_175415_1_;
      this.entityData.set(DATA_HEAD_POSE, p_175415_1_);
   }

   public void setBodyPose(Rotations p_175424_1_) {
      this.bodyPose = p_175424_1_;
      this.entityData.set(DATA_BODY_POSE, p_175424_1_);
   }

   public void setLeftArmPose(Rotations p_175405_1_) {
      this.leftArmPose = p_175405_1_;
      this.entityData.set(DATA_LEFT_ARM_POSE, p_175405_1_);
   }

   public void setRightArmPose(Rotations p_175428_1_) {
      this.rightArmPose = p_175428_1_;
      this.entityData.set(DATA_RIGHT_ARM_POSE, p_175428_1_);
   }

   public void setLeftLegPose(Rotations p_175417_1_) {
      this.leftLegPose = p_175417_1_;
      this.entityData.set(DATA_LEFT_LEG_POSE, p_175417_1_);
   }

   public void setRightLegPose(Rotations p_175427_1_) {
      this.rightLegPose = p_175427_1_;
      this.entityData.set(DATA_RIGHT_LEG_POSE, p_175427_1_);
   }

   public Rotations getHeadPose() {
      return this.headPose;
   }

   public Rotations getBodyPose() {
      return this.bodyPose;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftArmPose() {
      return this.leftArmPose;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightArmPose() {
      return this.rightArmPose;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftLegPose() {
      return this.leftLegPose;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightLegPose() {
      return this.rightLegPose;
   }

   public boolean isPickable() {
      return super.isPickable() && !this.isMarker();
   }

   public boolean skipAttackInteraction(Entity p_85031_1_) {
      return p_85031_1_ instanceof PlayerEntity && !this.level.mayInteract((PlayerEntity)p_85031_1_, this.blockPosition());
   }

   public HandSide getMainArm() {
      return HandSide.RIGHT;
   }

   protected SoundEvent getFallDamageSound(int p_184588_1_) {
      return SoundEvents.ARMOR_STAND_FALL;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ARMOR_STAND_HIT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ARMOR_STAND_BREAK;
   }

   public void thunderHit(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
   }

   public boolean isAffectedByPotions() {
      return false;
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_CLIENT_FLAGS.equals(p_184206_1_)) {
         this.refreshDimensions();
         this.blocksBuilding = !this.isMarker();
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   public boolean attackable() {
      return false;
   }

   public EntitySize getDimensions(Pose p_213305_1_) {
      return this.getDimensionsMarker(this.isMarker());
   }

   private EntitySize getDimensionsMarker(boolean p_242330_1_) {
      if (p_242330_1_) {
         return MARKER_DIMENSIONS;
      } else {
         return this.isBaby() ? BABY_DIMENSIONS : this.getType().getDimensions();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getLightProbePosition(float p_241842_1_) {
      if (this.isMarker()) {
         AxisAlignedBB axisalignedbb = this.getDimensionsMarker(false).makeBoundingBox(this.position());
         BlockPos blockpos = this.blockPosition();
         int i = Integer.MIN_VALUE;

         for(BlockPos blockpos1 : BlockPos.betweenClosed(new BlockPos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ), new BlockPos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ))) {
            int j = Math.max(this.level.getBrightness(LightType.BLOCK, blockpos1), this.level.getBrightness(LightType.SKY, blockpos1));
            if (j == 15) {
               return Vector3d.atCenterOf(blockpos1);
            }

            if (j > i) {
               i = j;
               blockpos = blockpos1.immutable();
            }
         }

         return Vector3d.atCenterOf(blockpos);
      } else {
         return super.getLightProbePosition(p_241842_1_);
      }
   }
}
