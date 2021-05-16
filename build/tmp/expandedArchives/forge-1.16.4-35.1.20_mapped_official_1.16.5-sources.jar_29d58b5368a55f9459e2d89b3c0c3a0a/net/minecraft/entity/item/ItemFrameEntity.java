package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity extends HangingEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DataParameter<ItemStack> DATA_ITEM = EntityDataManager.defineId(ItemFrameEntity.class, DataSerializers.ITEM_STACK);
   private static final DataParameter<Integer> DATA_ROTATION = EntityDataManager.defineId(ItemFrameEntity.class, DataSerializers.INT);
   private float dropChance = 1.0F;
   private boolean fixed;

   public ItemFrameEntity(EntityType<? extends ItemFrameEntity> p_i50224_1_, World p_i50224_2_) {
      super(p_i50224_1_, p_i50224_2_);
   }

   public ItemFrameEntity(World p_i45852_1_, BlockPos p_i45852_2_, Direction p_i45852_3_) {
      super(EntityType.ITEM_FRAME, p_i45852_1_, p_i45852_2_);
      this.setDirection(p_i45852_3_);
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return 0.0F;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
      this.getEntityData().define(DATA_ROTATION, 0);
   }

   protected void setDirection(Direction p_174859_1_) {
      Validate.notNull(p_174859_1_);
      this.direction = p_174859_1_;
      if (p_174859_1_.getAxis().isHorizontal()) {
         this.xRot = 0.0F;
         this.yRot = (float)(this.direction.get2DDataValue() * 90);
      } else {
         this.xRot = (float)(-90 * p_174859_1_.getAxisDirection().getStep());
         this.yRot = 0.0F;
      }

      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double d0 = 0.46875D;
         double d1 = (double)this.pos.getX() + 0.5D - (double)this.direction.getStepX() * 0.46875D;
         double d2 = (double)this.pos.getY() + 0.5D - (double)this.direction.getStepY() * 0.46875D;
         double d3 = (double)this.pos.getZ() + 0.5D - (double)this.direction.getStepZ() * 0.46875D;
         this.setPosRaw(d1, d2, d3);
         double d4 = (double)this.getWidth();
         double d5 = (double)this.getHeight();
         double d6 = (double)this.getWidth();
         Direction.Axis direction$axis = this.direction.getAxis();
         switch(direction$axis) {
         case X:
            d4 = 1.0D;
            break;
         case Y:
            d5 = 1.0D;
            break;
         case Z:
            d6 = 1.0D;
         }

         d4 = d4 / 32.0D;
         d5 = d5 / 32.0D;
         d6 = d6 / 32.0D;
         this.setBoundingBox(new AxisAlignedBB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6));
      }
   }

   public boolean survives() {
      if (this.fixed) {
         return true;
      } else if (!this.level.noCollision(this)) {
         return false;
      } else {
         BlockState blockstate = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
         return blockstate.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && RedstoneDiodeBlock.isDiode(blockstate) ? this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty() : false;
      }
   }

   public void move(MoverType p_213315_1_, Vector3d p_213315_2_) {
      if (!this.fixed) {
         super.move(p_213315_1_, p_213315_2_);
      }

   }

   public void push(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
      if (!this.fixed) {
         super.push(p_70024_1_, p_70024_3_, p_70024_5_);
      }

   }

   public float getPickRadius() {
      return 0.0F;
   }

   public void kill() {
      this.removeFramedMap(this.getItem());
      super.kill();
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.fixed) {
         return p_70097_1_ != DamageSource.OUT_OF_WORLD && !p_70097_1_.isCreativePlayer() ? false : super.hurt(p_70097_1_, p_70097_2_);
      } else if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (!p_70097_1_.isExplosion() && !this.getItem().isEmpty()) {
         if (!this.level.isClientSide) {
            this.dropItem(p_70097_1_.getEntity(), false);
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public int getWidth() {
      return 12;
   }

   public int getHeight() {
      return 12;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = 16.0D;
      d0 = d0 * 64.0D * getViewScale();
      return p_70112_1_ < d0 * d0;
   }

   public void dropItem(@Nullable Entity p_110128_1_) {
      this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
      this.dropItem(p_110128_1_, true);
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0F, 1.0F);
   }

   private void dropItem(@Nullable Entity p_146065_1_, boolean p_146065_2_) {
      if (!this.fixed) {
         ItemStack itemstack = this.getItem();
         this.setItem(ItemStack.EMPTY);
         if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (p_146065_1_ == null) {
               this.removeFramedMap(itemstack);
            }

         } else {
            if (p_146065_1_ instanceof PlayerEntity) {
               PlayerEntity playerentity = (PlayerEntity)p_146065_1_;
               if (playerentity.abilities.instabuild) {
                  this.removeFramedMap(itemstack);
                  return;
               }
            }

            if (p_146065_2_) {
               this.spawnAtLocation(Items.ITEM_FRAME);
            }

            if (!itemstack.isEmpty()) {
               itemstack = itemstack.copy();
               this.removeFramedMap(itemstack);
               if (this.random.nextFloat() < this.dropChance) {
                  this.spawnAtLocation(itemstack);
               }
            }

         }
      }
   }

   private void removeFramedMap(ItemStack p_110131_1_) {
      if (p_110131_1_.getItem() instanceof net.minecraft.item.FilledMapItem) {
         MapData mapdata = FilledMapItem.getOrCreateSavedData(p_110131_1_, this.level);
         mapdata.removedFromFrame(this.pos, this.getId());
         mapdata.setDirty(true);
      }

      p_110131_1_.setEntityRepresentation((Entity)null);
   }

   public ItemStack getItem() {
      return this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack p_82334_1_) {
      this.setItem(p_82334_1_, true);
   }

   public void setItem(ItemStack p_174864_1_, boolean p_174864_2_) {
      if (!p_174864_1_.isEmpty()) {
         p_174864_1_ = p_174864_1_.copy();
         p_174864_1_.setCount(1);
         p_174864_1_.setEntityRepresentation(this);
      }

      this.getEntityData().set(DATA_ITEM, p_174864_1_);
      if (!p_174864_1_.isEmpty()) {
         this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      }

      if (p_174864_2_ && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ == 0) {
         this.setItem(p_174820_2_);
         return true;
      } else {
         return false;
      }
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (p_184206_1_.equals(DATA_ITEM)) {
         ItemStack itemstack = this.getItem();
         if (!itemstack.isEmpty() && itemstack.getFrame() != this) {
            itemstack.setEntityRepresentation(this);
         }
      }

   }

   public int getRotation() {
      return this.getEntityData().get(DATA_ROTATION);
   }

   public void setRotation(int p_82336_1_) {
      this.setRotation(p_82336_1_, true);
   }

   private void setRotation(int p_174865_1_, boolean p_174865_2_) {
      this.getEntityData().set(DATA_ROTATION, p_174865_1_ % 8);
      if (p_174865_2_ && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (!this.getItem().isEmpty()) {
         p_213281_1_.put("Item", this.getItem().save(new CompoundNBT()));
         p_213281_1_.putByte("ItemRotation", (byte)this.getRotation());
         p_213281_1_.putFloat("ItemDropChance", this.dropChance);
      }

      p_213281_1_.putByte("Facing", (byte)this.direction.get3DDataValue());
      p_213281_1_.putBoolean("Invisible", this.isInvisible());
      p_213281_1_.putBoolean("Fixed", this.fixed);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      CompoundNBT compoundnbt = p_70037_1_.getCompound("Item");
      if (compoundnbt != null && !compoundnbt.isEmpty()) {
         ItemStack itemstack = ItemStack.of(compoundnbt);
         if (itemstack.isEmpty()) {
            LOGGER.warn("Unable to load item from: {}", (Object)compoundnbt);
         }

         ItemStack itemstack1 = this.getItem();
         if (!itemstack1.isEmpty() && !ItemStack.matches(itemstack, itemstack1)) {
            this.removeFramedMap(itemstack1);
         }

         this.setItem(itemstack, false);
         this.setRotation(p_70037_1_.getByte("ItemRotation"), false);
         if (p_70037_1_.contains("ItemDropChance", 99)) {
            this.dropChance = p_70037_1_.getFloat("ItemDropChance");
         }
      }

      this.setDirection(Direction.from3DDataValue(p_70037_1_.getByte("Facing")));
      this.setInvisible(p_70037_1_.getBoolean("Invisible"));
      this.fixed = p_70037_1_.getBoolean("Fixed");
   }

   public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      ItemStack itemstack = p_184230_1_.getItemInHand(p_184230_2_);
      boolean flag = !this.getItem().isEmpty();
      boolean flag1 = !itemstack.isEmpty();
      if (this.fixed) {
         return ActionResultType.PASS;
      } else if (!this.level.isClientSide) {
         if (!flag) {
            if (flag1 && !this.removed) {
               this.setItem(itemstack);
               if (!p_184230_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }
            }
         } else {
            this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
         }

         return ActionResultType.CONSUME;
      } else {
         return !flag && !flag1 ? ActionResultType.PASS : ActionResultType.SUCCESS;
      }
   }

   public int getAnalogOutput() {
      return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
   }
}
