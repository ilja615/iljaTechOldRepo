package net.minecraft.entity.monster;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ZombieVillagerEntity extends ZombieEntity implements IVillagerDataHolder {
   private static final DataParameter<Boolean> DATA_CONVERTING_ID = EntityDataManager.defineId(ZombieVillagerEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<VillagerData> DATA_VILLAGER_DATA = EntityDataManager.defineId(ZombieVillagerEntity.class, DataSerializers.VILLAGER_DATA);
   private int villagerConversionTime;
   private UUID conversionStarter;
   private INBT gossips;
   private CompoundNBT tradeOffers;
   private int villagerXp;

   public ZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> p_i50186_1_, World p_i50186_2_) {
      super(p_i50186_1_, p_i50186_2_);
      this.setVillagerData(this.getVillagerData().setProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CONVERTING_ID, false);
      this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      VillagerData.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent((p_234343_1_) -> {
         p_213281_1_.put("VillagerData", p_234343_1_);
      });
      if (this.tradeOffers != null) {
         p_213281_1_.put("Offers", this.tradeOffers);
      }

      if (this.gossips != null) {
         p_213281_1_.put("Gossips", this.gossips);
      }

      p_213281_1_.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
      if (this.conversionStarter != null) {
         p_213281_1_.putUUID("ConversionPlayer", this.conversionStarter);
      }

      p_213281_1_.putInt("Xp", this.villagerXp);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("VillagerData", 10)) {
         DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NBTDynamicOps.INSTANCE, p_70037_1_.get("VillagerData")));
         dataresult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
      }

      if (p_70037_1_.contains("Offers", 10)) {
         this.tradeOffers = p_70037_1_.getCompound("Offers");
      }

      if (p_70037_1_.contains("Gossips", 10)) {
         this.gossips = p_70037_1_.getList("Gossips", 10);
      }

      if (p_70037_1_.contains("ConversionTime", 99) && p_70037_1_.getInt("ConversionTime") > -1) {
         this.startConverting(p_70037_1_.hasUUID("ConversionPlayer") ? p_70037_1_.getUUID("ConversionPlayer") : null, p_70037_1_.getInt("ConversionTime"));
      }

      if (p_70037_1_.contains("Xp", 3)) {
         this.villagerXp = p_70037_1_.getInt("Xp");
      }

   }

   public void tick() {
      if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
         int i = this.getConversionProgress();
         this.villagerConversionTime -= i;
         if (this.villagerConversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.villagerConversionTime = timer)) {
            this.finishConversion((ServerWorld)this.level);
         }
      }

      super.tick();
   }

   public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      if (itemstack.getItem() == Items.GOLDEN_APPLE) {
         if (this.hasEffect(Effects.WEAKNESS)) {
            if (!p_230254_1_.abilities.instabuild) {
               itemstack.shrink(1);
            }

            if (!this.level.isClientSide) {
               this.startConverting(p_230254_1_.getUUID(), this.random.nextInt(2401) + 3600);
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.CONSUME;
         }
      } else {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return !this.isConverting() && this.villagerXp == 0;
   }

   public boolean isConverting() {
      return this.getEntityData().get(DATA_CONVERTING_ID);
   }

   private void startConverting(@Nullable UUID p_191991_1_, int p_191991_2_) {
      this.conversionStarter = p_191991_1_;
      this.villagerConversionTime = p_191991_2_;
      this.getEntityData().set(DATA_CONVERTING_ID, true);
      this.removeEffect(Effects.WEAKNESS);
      this.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, p_191991_2_, Math.min(this.level.getDifficulty().getId() - 1, 0)));
      this.level.broadcastEntityEvent(this, (byte)16);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 16) {
         if (!this.isSilent()) {
            this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

      } else {
         super.handleEntityEvent(p_70103_1_);
      }
   }

   private void finishConversion(ServerWorld p_213791_1_) {
      VillagerEntity villagerentity = this.convertTo(EntityType.VILLAGER, false);

      for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
         ItemStack itemstack = this.getItemBySlot(equipmentslottype);
         if (!itemstack.isEmpty()) {
            if (EnchantmentHelper.hasBindingCurse(itemstack)) {
               villagerentity.setSlot(equipmentslottype.getIndex() + 300, itemstack);
            } else {
               double d0 = (double)this.getEquipmentDropChance(equipmentslottype);
               if (d0 > 1.0D) {
                  this.spawnAtLocation(itemstack);
               }
            }
         }
      }

      villagerentity.setVillagerData(this.getVillagerData());
      if (this.gossips != null) {
         villagerentity.setGossips(this.gossips);
      }

      if (this.tradeOffers != null) {
         villagerentity.setOffers(new MerchantOffers(this.tradeOffers));
      }

      villagerentity.setVillagerXp(this.villagerXp);
      villagerentity.finalizeSpawn(p_213791_1_, p_213791_1_.getCurrentDifficultyAt(villagerentity.blockPosition()), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
      if (this.conversionStarter != null) {
         PlayerEntity playerentity = p_213791_1_.getPlayerByUUID(this.conversionStarter);
         if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)playerentity, this, villagerentity);
            p_213791_1_.onReputationEvent(IReputationType.ZOMBIE_VILLAGER_CURED, playerentity, villagerentity);
         }
      }

      villagerentity.addEffect(new EffectInstance(Effects.CONFUSION, 200, 0));
      if (!this.isSilent()) {
         p_213791_1_.levelEvent((PlayerEntity)null, 1027, this.blockPosition(), 0);
      }
      net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, villagerentity);
   }

   private int getConversionProgress() {
      int i = 1;
      if (this.random.nextFloat() < 0.01F) {
         int j = 0;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
            for(int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
               for(int i1 = (int)this.getZ() - 4; i1 < (int)this.getZ() + 4 && j < 14; ++i1) {
                  Block block = this.level.getBlockState(blockpos$mutable.set(k, l, i1)).getBlock();
                  if (block == Blocks.IRON_BARS || block instanceof BedBlock) {
                     if (this.random.nextFloat() < 0.3F) {
                        ++i;
                     }

                     ++j;
                  }
               }
            }
         }
      }

      return i;
   }

   protected float getVoicePitch() {
      return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_VILLAGER_STEP;
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   public void setTradeOffers(CompoundNBT p_213790_1_) {
      this.tradeOffers = p_213790_1_;
   }

   public void setGossips(INBT p_223727_1_) {
      this.gossips = p_223727_1_;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(p_213386_1_.getBiomeName(this.blockPosition()))));
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public void setVillagerData(VillagerData p_213792_1_) {
      VillagerData villagerdata = this.getVillagerData();
      if (villagerdata.getProfession() != p_213792_1_.getProfession()) {
         this.tradeOffers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, p_213792_1_);
   }

   public VillagerData getVillagerData() {
      return this.entityData.get(DATA_VILLAGER_DATA);
   }

   public void setVillagerXp(int p_213789_1_) {
      this.villagerXp = p_213789_1_;
   }
}
