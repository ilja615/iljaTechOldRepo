package net.minecraft.entity.passive.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class HorseEntity extends AbstractHorseEntity {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final DataParameter<Integer> DATA_ID_TYPE_VARIANT = EntityDataManager.defineId(HorseEntity.class, DataSerializers.INT);

   public HorseEntity(EntityType<? extends HorseEntity> p_i50238_1_, World p_i50238_2_) {
      super(p_i50238_1_, p_i50238_2_);
   }

   protected void randomizeAttributes() {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth());
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
      this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Variant", this.getTypeVariant());
      if (!this.inventory.getItem(1).isEmpty()) {
         p_213281_1_.put("ArmorItem", this.inventory.getItem(1).save(new CompoundNBT()));
      }

   }

   public ItemStack getArmor() {
      return this.getItemBySlot(EquipmentSlotType.CHEST);
   }

   private void setArmor(ItemStack p_213805_1_) {
      this.setItemSlot(EquipmentSlotType.CHEST, p_213805_1_);
      this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setTypeVariant(p_70037_1_.getInt("Variant"));
      if (p_70037_1_.contains("ArmorItem", 10)) {
         ItemStack itemstack = ItemStack.of(p_70037_1_.getCompound("ArmorItem"));
         if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
            this.inventory.setItem(1, itemstack);
         }
      }

      this.updateContainerEquipment();
   }

   private void setTypeVariant(int p_234242_1_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_234242_1_);
   }

   private int getTypeVariant() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   private void setVariantAndMarkings(CoatColors p_234238_1_, CoatTypes p_234238_2_) {
      this.setTypeVariant(p_234238_1_.getId() & 255 | p_234238_2_.getId() << 8 & '\uff00');
   }

   public CoatColors getVariant() {
      return CoatColors.byId(this.getTypeVariant() & 255);
   }

   public CoatTypes getMarkings() {
      return CoatTypes.byId((this.getTypeVariant() & '\uff00') >> 8);
   }

   protected void updateContainerEquipment() {
      if (!this.level.isClientSide) {
         super.updateContainerEquipment();
         this.setArmorEquipment(this.inventory.getItem(1));
         this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
      }
   }

   private void setArmorEquipment(ItemStack p_213804_1_) {
      this.setArmor(p_213804_1_);
      if (!this.level.isClientSide) {
         this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         if (this.isArmor(p_213804_1_)) {
            int i = ((HorseArmorItem)p_213804_1_.getItem()).getProtection();
            if (i != 0) {
               this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION));
            }
         }
      }

   }

   public void containerChanged(IInventory p_76316_1_) {
      ItemStack itemstack = this.getArmor();
      super.containerChanged(p_76316_1_);
      ItemStack itemstack1 = this.getArmor();
      if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
         this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType p_190680_1_) {
      super.playGallopSound(p_190680_1_);
      if (this.random.nextInt(10) == 0) {
         this.playSound(SoundEvents.HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
      }

      ItemStack stack = this.inventory.getItem(1);
      if (isArmor(stack)) stack.onHorseArmorTick(level, this);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.HORSE_DEATH;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.HORSE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.HORSE_ANGRY;
   }

   public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      if (!this.isBaby()) {
         if (this.isTamed() && p_230254_1_.isSecondaryUseActive()) {
            this.openInventory(p_230254_1_);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
         }

         if (this.isVehicle()) {
            return super.mobInteract(p_230254_1_, p_230254_2_);
         }
      }

      if (!itemstack.isEmpty()) {
         if (this.isFood(itemstack)) {
            return this.fedFood(p_230254_1_, itemstack);
         }

         ActionResultType actionresulttype = itemstack.interactLivingEntity(p_230254_1_, this, p_230254_2_);
         if (actionresulttype.consumesAction()) {
            return actionresulttype;
         }

         if (!this.isTamed()) {
            this.makeMad();
            return ActionResultType.sidedSuccess(this.level.isClientSide);
         }

         boolean flag = !this.isBaby() && !this.isSaddled() && itemstack.getItem() == Items.SADDLE;
         if (this.isArmor(itemstack) || flag) {
            this.openInventory(p_230254_1_);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
         }
      }

      if (this.isBaby()) {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      } else {
         this.doPlayerRide(p_230254_1_);
         return ActionResultType.sidedSuccess(this.level.isClientSide);
      }
   }

   public boolean canMate(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!(p_70878_1_ instanceof DonkeyEntity) && !(p_70878_1_ instanceof HorseEntity)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorseEntity)p_70878_1_).canParent();
      }
   }

   public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      AbstractHorseEntity abstracthorseentity;
      if (p_241840_2_ instanceof DonkeyEntity) {
         abstracthorseentity = EntityType.MULE.create(p_241840_1_);
      } else {
         HorseEntity horseentity = (HorseEntity)p_241840_2_;
         abstracthorseentity = EntityType.HORSE.create(p_241840_1_);
         int i = this.random.nextInt(9);
         CoatColors coatcolors;
         if (i < 4) {
            coatcolors = this.getVariant();
         } else if (i < 8) {
            coatcolors = horseentity.getVariant();
         } else {
            coatcolors = Util.getRandom(CoatColors.values(), this.random);
         }

         int j = this.random.nextInt(5);
         CoatTypes coattypes;
         if (j < 2) {
            coattypes = this.getMarkings();
         } else if (j < 4) {
            coattypes = horseentity.getMarkings();
         } else {
            coattypes = Util.getRandom(CoatTypes.values(), this.random);
         }

         ((HorseEntity)abstracthorseentity).setVariantAndMarkings(coatcolors, coattypes);
      }

      this.setOffspringAttributes(p_241840_2_, abstracthorseentity);
      return abstracthorseentity;
   }

   public boolean canWearArmor() {
      return true;
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      return p_190682_1_.getItem() instanceof HorseArmorItem;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      CoatColors coatcolors;
      if (p_213386_4_ instanceof HorseEntity.HorseData) {
         coatcolors = ((HorseEntity.HorseData)p_213386_4_).variant;
      } else {
         coatcolors = Util.getRandom(CoatColors.values(), this.random);
         p_213386_4_ = new HorseEntity.HorseData(coatcolors);
      }

      this.setVariantAndMarkings(coatcolors, Util.getRandom(CoatTypes.values(), this.random));
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public static class HorseData extends AgeableEntity.AgeableData {
      public final CoatColors variant;

      public HorseData(CoatColors p_i231557_1_) {
         super(true);
         this.variant = p_i231557_1_;
      }
   }
}
