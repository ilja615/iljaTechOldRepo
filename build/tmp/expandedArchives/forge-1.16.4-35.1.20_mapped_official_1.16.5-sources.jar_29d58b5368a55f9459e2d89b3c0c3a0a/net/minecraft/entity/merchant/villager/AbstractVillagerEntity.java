package net.minecraft.entity.merchant.villager;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.INPC;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractVillagerEntity extends AgeableEntity implements INPC, IMerchant {
   private static final DataParameter<Integer> DATA_UNHAPPY_COUNTER = EntityDataManager.defineId(AbstractVillagerEntity.class, DataSerializers.INT);
   @Nullable
   private PlayerEntity tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private final Inventory inventory = new Inventory(8);

   public AbstractVillagerEntity(EntityType<? extends AbstractVillagerEntity> p_i50185_1_, World p_i50185_2_) {
      super(p_i50185_1_, p_i50185_2_);
      this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
   }

   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData(false);
      }

      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public int getUnhappyCounter() {
      return this.entityData.get(DATA_UNHAPPY_COUNTER);
   }

   public void setUnhappyCounter(int p_213720_1_) {
      this.entityData.set(DATA_UNHAPPY_COUNTER, p_213720_1_);
   }

   public int getVillagerXp() {
      return 0;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isBaby() ? 0.81F : 1.62F;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_UNHAPPY_COUNTER, 0);
   }

   public void setTradingPlayer(@Nullable PlayerEntity p_70932_1_) {
      this.tradingPlayer = p_70932_1_;
   }

   @Nullable
   public PlayerEntity getTradingPlayer() {
      return this.tradingPlayer;
   }

   public boolean isTrading() {
      return this.tradingPlayer != null;
   }

   public MerchantOffers getOffers() {
      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.updateTrades();
      }

      return this.offers;
   }

   @OnlyIn(Dist.CLIENT)
   public void overrideOffers(@Nullable MerchantOffers p_213703_1_) {
   }

   public void overrideXp(int p_213702_1_) {
   }

   public void notifyTrade(MerchantOffer p_213704_1_) {
      p_213704_1_.increaseUses();
      this.ambientSoundTime = -this.getAmbientSoundInterval();
      this.rewardTradeXp(p_213704_1_);
      if (this.tradingPlayer instanceof ServerPlayerEntity) {
         CriteriaTriggers.TRADE.trigger((ServerPlayerEntity)this.tradingPlayer, this, p_213704_1_.getResult());
      }

   }

   protected abstract void rewardTradeXp(MerchantOffer p_213713_1_);

   public boolean showProgressBar() {
      return true;
   }

   public void notifyTradeUpdated(ItemStack p_110297_1_) {
      if (!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
         this.ambientSoundTime = -this.getAmbientSoundInterval();
         this.playSound(this.getTradeUpdatedSound(!p_110297_1_.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }

   protected SoundEvent getTradeUpdatedSound(boolean p_213721_1_) {
      return p_213721_1_ ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
   }

   public void playCelebrateSound() {
      this.playSound(SoundEvents.VILLAGER_CELEBRATE, this.getSoundVolume(), this.getVoicePitch());
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      MerchantOffers merchantoffers = this.getOffers();
      if (!merchantoffers.isEmpty()) {
         p_213281_1_.put("Offers", merchantoffers.createTag());
      }

      p_213281_1_.put("Inventory", this.inventory.createTag());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("Offers", 10)) {
         this.offers = new MerchantOffers(p_70037_1_.getCompound("Offers"));
      }

      this.inventory.fromTag(p_70037_1_.getList("Inventory", 10));
   }

   @Nullable
   public Entity changeDimension(ServerWorld p_241206_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      this.stopTrading();
      return super.changeDimension(p_241206_1_, teleporter);
   }

   protected void stopTrading() {
      this.setTradingPlayer((PlayerEntity)null);
   }

   public void die(DamageSource p_70645_1_) {
      super.die(p_70645_1_);
      this.stopTrading();
   }

   @OnlyIn(Dist.CLIENT)
   protected void addParticlesAroundSelf(IParticleData p_213718_1_) {
      for(int i = 0; i < 5; ++i) {
         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(p_213718_1_, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return false;
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      if (super.setSlot(p_174820_1_, p_174820_2_)) {
         return true;
      } else {
         int i = p_174820_1_ - 300;
         if (i >= 0 && i < this.inventory.getContainerSize()) {
            this.inventory.setItem(i, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   public World getLevel() {
      return this.level;
   }

   protected abstract void updateTrades();

   protected void addOffersFromItemListings(MerchantOffers p_213717_1_, VillagerTrades.ITrade[] p_213717_2_, int p_213717_3_) {
      Set<Integer> set = Sets.newHashSet();
      if (p_213717_2_.length > p_213717_3_) {
         while(set.size() < p_213717_3_) {
            set.add(this.random.nextInt(p_213717_2_.length));
         }
      } else {
         for(int i = 0; i < p_213717_2_.length; ++i) {
            set.add(i);
         }
      }

      for(Integer integer : set) {
         VillagerTrades.ITrade villagertrades$itrade = p_213717_2_[integer];
         MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this, this.random);
         if (merchantoffer != null) {
            p_213717_1_.add(merchantoffer);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getRopeHoldPosition(float p_241843_1_) {
      float f = MathHelper.lerp(p_241843_1_, this.yBodyRotO, this.yBodyRot) * ((float)Math.PI / 180F);
      Vector3d vector3d = new Vector3d(0.0D, this.getBoundingBox().getYsize() - 1.0D, 0.2D);
      return this.getPosition(p_241843_1_).add(vector3d.yRot(-f));
   }
}
