package net.minecraft.entity.item;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEntity extends Entity {
   private static final DataParameter<ItemStack> DATA_ITEM = EntityDataManager.defineId(ItemEntity.class, DataSerializers.ITEM_STACK);
   private int age;
   private int pickupDelay;
   private int health = 5;
   private UUID thrower;
   private UUID owner;
   public final float bobOffs;
   /**
    * The maximum age of this EntityItem.  The item is expired once this is reached.
    */
   public int lifespan = 6000;

   public ItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_) {
      super(p_i50217_1_, p_i50217_2_);
      this.bobOffs = (float)(Math.random() * Math.PI * 2.0D);
   }

   public ItemEntity(World p_i1709_1_, double p_i1709_2_, double p_i1709_4_, double p_i1709_6_) {
      this(EntityType.ITEM, p_i1709_1_);
      this.setPos(p_i1709_2_, p_i1709_4_, p_i1709_6_);
      this.yRot = this.random.nextFloat() * 360.0F;
      this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
   }

   public ItemEntity(World p_i1710_1_, double p_i1710_2_, double p_i1710_4_, double p_i1710_6_, ItemStack p_i1710_8_) {
      this(p_i1710_1_, p_i1710_2_, p_i1710_4_, p_i1710_6_);
      this.setItem(p_i1710_8_);
      this.lifespan = (p_i1710_8_.getItem() == null ? 6000 : p_i1710_8_.getEntityLifespan(p_i1710_1_));
   }

   @OnlyIn(Dist.CLIENT)
   private ItemEntity(ItemEntity p_i231561_1_) {
      super(p_i231561_1_.getType(), p_i231561_1_.level);
      this.setItem(p_i231561_1_.getItem().copy());
      this.copyPosition(p_i231561_1_);
      this.age = p_i231561_1_.age;
      this.bobOffs = p_i231561_1_.bobOffs;
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
   }

   public void tick() {
      if (getItem().onEntityItemUpdate(this)) return;
      if (this.getItem().isEmpty()) {
         this.remove();
      } else {
         super.tick();
         if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.xo = this.getX();
         this.yo = this.getY();
         this.zo = this.getZ();
         Vector3d vector3d = this.getDeltaMovement();
         float f = this.getEyeHeight() - 0.11111111F;
         if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
            this.setUnderwaterMovement();
         } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)f) {
            this.setUnderLavaMovement();
         } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         if (this.level.isClientSide) {
            this.noPhysics = false;
         } else {
            this.noPhysics = !this.level.noCollision(this);
            if (this.noPhysics) {
               this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
            }
         }

         if (!this.onGround || getHorizontalDistanceSqr(this.getDeltaMovement()) > (double)1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f1 = 0.98F;
            if (this.onGround) {
               f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getFriction() * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f1, 0.98D, (double)f1));
            if (this.onGround) {
               Vector3d vector3d1 = this.getDeltaMovement();
               if (vector3d1.y < 0.0D) {
                  this.setDeltaMovement(vector3d1.multiply(1.0D, -0.5D, 1.0D));
               }
            }
         }

         boolean flag = MathHelper.floor(this.xo) != MathHelper.floor(this.getX()) || MathHelper.floor(this.yo) != MathHelper.floor(this.getY()) || MathHelper.floor(this.zo) != MathHelper.floor(this.getZ());
         int i = flag ? 2 : 40;
         if (this.tickCount % i == 0) {
            if (this.level.getFluidState(this.blockPosition()).is(FluidTags.LAVA) && !this.fireImmune()) {
               this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }

            if (!this.level.isClientSide && this.isMergable()) {
               this.mergeWithNeighbours();
            }
         }

         if (this.age != -32768) {
            ++this.age;
         }

         this.hasImpulse |= this.updateInWaterStateAndDoFluidPushing();
         if (!this.level.isClientSide) {
            double d0 = this.getDeltaMovement().subtract(vector3d).lengthSqr();
            if (d0 > 0.01D) {
               this.hasImpulse = true;
            }
         }

         ItemStack item = this.getItem();
         if (!this.level.isClientSide && this.age >= lifespan) {
             int hook = net.minecraftforge.event.ForgeEventFactory.onItemExpire(this, item);
             if (hook < 0) this.remove();
             else          this.lifespan += hook;
         }

         if (item.isEmpty()) {
            this.remove();
         }

      }
   }

   private void setUnderwaterMovement() {
      Vector3d vector3d = this.getDeltaMovement();
      this.setDeltaMovement(vector3d.x * (double)0.99F, vector3d.y + (double)(vector3d.y < (double)0.06F ? 5.0E-4F : 0.0F), vector3d.z * (double)0.99F);
   }

   private void setUnderLavaMovement() {
      Vector3d vector3d = this.getDeltaMovement();
      this.setDeltaMovement(vector3d.x * (double)0.95F, vector3d.y + (double)(vector3d.y < (double)0.06F ? 5.0E-4F : 0.0F), vector3d.z * (double)0.95F);
   }

   private void mergeWithNeighbours() {
      if (this.isMergable()) {
         for(ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.5D, 0.0D, 0.5D), (p_213859_1_) -> {
            return p_213859_1_ != this && p_213859_1_.isMergable();
         })) {
            if (itementity.isMergable()) {
               this.tryToMerge(itementity);
               if (this.removed) {
                  break;
               }
            }
         }

      }
   }

   private boolean isMergable() {
      ItemStack itemstack = this.getItem();
      return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemstack.getCount() < itemstack.getMaxStackSize();
   }

   private void tryToMerge(ItemEntity p_226530_1_) {
      ItemStack itemstack = this.getItem();
      ItemStack itemstack1 = p_226530_1_.getItem();
      if (Objects.equals(this.getOwner(), p_226530_1_.getOwner()) && areMergable(itemstack, itemstack1)) {
         if (itemstack1.getCount() < itemstack.getCount()) {
            merge(this, itemstack, p_226530_1_, itemstack1);
         } else {
            merge(p_226530_1_, itemstack1, this, itemstack);
         }

      }
   }

   public static boolean areMergable(ItemStack p_226532_0_, ItemStack p_226532_1_) {
      if (p_226532_1_.getItem() != p_226532_0_.getItem()) {
         return false;
      } else if (p_226532_1_.getCount() + p_226532_0_.getCount() > p_226532_1_.getMaxStackSize()) {
         return false;
      } else if (p_226532_1_.hasTag() ^ p_226532_0_.hasTag()) {
         return false;
      } else if (!p_226532_0_.areCapsCompatible(p_226532_1_)) {
         return false;
      } else {
         return !p_226532_1_.hasTag() || p_226532_1_.getTag().equals(p_226532_0_.getTag());
      }
   }

   public static ItemStack merge(ItemStack p_226533_0_, ItemStack p_226533_1_, int p_226533_2_) {
      int i = Math.min(Math.min(p_226533_0_.getMaxStackSize(), p_226533_2_) - p_226533_0_.getCount(), p_226533_1_.getCount());
      ItemStack itemstack = p_226533_0_.copy();
      itemstack.grow(i);
      p_226533_1_.shrink(i);
      return itemstack;
   }

   private static void merge(ItemEntity p_226531_0_, ItemStack p_226531_1_, ItemStack p_226531_2_) {
      ItemStack itemstack = merge(p_226531_1_, p_226531_2_, 64);
      p_226531_0_.setItem(itemstack);
   }

   private static void merge(ItemEntity p_213858_0_, ItemStack p_213858_1_, ItemEntity p_213858_2_, ItemStack p_213858_3_) {
      merge(p_213858_0_, p_213858_1_, p_213858_3_);
      p_213858_0_.pickupDelay = Math.max(p_213858_0_.pickupDelay, p_213858_2_.pickupDelay);
      p_213858_0_.age = Math.min(p_213858_0_.age, p_213858_2_.age);
      if (p_213858_3_.isEmpty()) {
         p_213858_2_.remove();
      }

   }

   public boolean fireImmune() {
      return this.getItem().getItem().isFireResistant() || super.fireImmune();
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.level.isClientSide || this.removed) return false; //Forge: Fixes MC-53850
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && p_70097_1_.isExplosion()) {
         return false;
      } else if (!this.getItem().getItem().canBeHurtBy(p_70097_1_)) {
         return false;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - p_70097_2_);
         if (this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("Health", (short)this.health);
      p_213281_1_.putShort("Age", (short)this.age);
      p_213281_1_.putShort("PickupDelay", (short)this.pickupDelay);
      p_213281_1_.putInt("Lifespan", lifespan);
      if (this.getThrower() != null) {
         p_213281_1_.putUUID("Thrower", this.getThrower());
      }

      if (this.getOwner() != null) {
         p_213281_1_.putUUID("Owner", this.getOwner());
      }

      if (!this.getItem().isEmpty()) {
         p_213281_1_.put("Item", this.getItem().save(new CompoundNBT()));
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.health = p_70037_1_.getShort("Health");
      this.age = p_70037_1_.getShort("Age");
      if (p_70037_1_.contains("PickupDelay")) {
         this.pickupDelay = p_70037_1_.getShort("PickupDelay");
      }
      if (p_70037_1_.contains("Lifespan")) lifespan = p_70037_1_.getInt("Lifespan");

      if (p_70037_1_.hasUUID("Owner")) {
         this.owner = p_70037_1_.getUUID("Owner");
      }

      if (p_70037_1_.hasUUID("Thrower")) {
         this.thrower = p_70037_1_.getUUID("Thrower");
      }

      CompoundNBT compoundnbt = p_70037_1_.getCompound("Item");
      this.setItem(ItemStack.of(compoundnbt));
      if (this.getItem().isEmpty()) {
         this.remove();
      }

   }

   public void playerTouch(PlayerEntity p_70100_1_) {
      if (!this.level.isClientSide) {
         if (this.pickupDelay > 0) return;
         ItemStack itemstack = this.getItem();
         Item item = itemstack.getItem();
         int i = itemstack.getCount();

         int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(this, p_70100_1_);
         if (hook < 0) return;

         ItemStack copy = itemstack.copy();
         if (this.pickupDelay == 0 && (this.owner == null || lifespan - this.age <= 200 || this.owner.equals(p_70100_1_.getUUID())) && (hook == 1 || i <= 0 || p_70100_1_.inventory.add(itemstack))) {
            copy.setCount(copy.getCount() - getItem().getCount());
            net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerItemPickupEvent(p_70100_1_, this, copy);
            p_70100_1_.take(this, i);
            if (itemstack.isEmpty()) {
               this.remove();
               itemstack.setCount(i);
            }

            p_70100_1_.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
            p_70100_1_.onItemPickup(this);
         }

      }
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TranslationTextComponent(this.getItem().getDescriptionId()));
   }

   public boolean isAttackable() {
      return false;
   }

   @Nullable
   public Entity changeDimension(ServerWorld p_241206_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      Entity entity = super.changeDimension(p_241206_1_, teleporter);
      if (!this.level.isClientSide && entity instanceof ItemEntity) {
         ((ItemEntity)entity).mergeWithNeighbours();
      }

      return entity;
   }

   public ItemStack getItem() {
      return this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack p_92058_1_) {
      this.getEntityData().set(DATA_ITEM, p_92058_1_);
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      super.onSyncedDataUpdated(p_184206_1_);
      if (DATA_ITEM.equals(p_184206_1_)) {
         this.getItem().setEntityRepresentation(this);
      }

   }

   @Nullable
   public UUID getOwner() {
      return this.owner;
   }

   public void setOwner(@Nullable UUID p_200217_1_) {
      this.owner = p_200217_1_;
   }

   @Nullable
   public UUID getThrower() {
      return this.thrower;
   }

   public void setThrower(@Nullable UUID p_200216_1_) {
      this.thrower = p_200216_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAge() {
      return this.age;
   }

   public void setDefaultPickUpDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickUpDelay() {
      this.pickupDelay = 0;
   }

   public void setNeverPickUp() {
      this.pickupDelay = 32767;
   }

   public void setPickUpDelay(int p_174867_1_) {
      this.pickupDelay = p_174867_1_;
   }

   public boolean hasPickUpDelay() {
      return this.pickupDelay > 0;
   }

   public void setExtendedLifetime() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setNeverPickUp();
      this.age = getItem().getEntityLifespan(level) - 1;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSpin(float p_234272_1_) {
      return ((float)this.getAge() + p_234272_1_) / 20.0F + this.bobOffs;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemEntity copy() {
      return new ItemEntity(this);
   }
}
