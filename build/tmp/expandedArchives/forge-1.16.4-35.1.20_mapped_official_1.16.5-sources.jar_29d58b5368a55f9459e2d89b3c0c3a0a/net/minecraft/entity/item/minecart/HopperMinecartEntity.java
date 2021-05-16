package net.minecraft.entity.item.minecart;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class HopperMinecartEntity extends ContainerMinecartEntity implements IHopper {
   private boolean enabled = true;
   private int cooldownTime = -1;
   private final BlockPos lastPosition = BlockPos.ZERO;

   public HopperMinecartEntity(EntityType<? extends HopperMinecartEntity> p_i50116_1_, World p_i50116_2_) {
      super(p_i50116_1_, p_i50116_2_);
   }

   public HopperMinecartEntity(World p_i1721_1_, double p_i1721_2_, double p_i1721_4_, double p_i1721_6_) {
      super(EntityType.HOPPER_MINECART, p_i1721_2_, p_i1721_4_, p_i1721_6_, p_i1721_1_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.HOPPER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.HOPPER.defaultBlockState();
   }

   public int getDefaultDisplayOffset() {
      return 1;
   }

   public int getContainerSize() {
      return 5;
   }

   public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      boolean flag = !p_96095_4_;
      if (flag != this.isEnabled()) {
         this.setEnabled(flag);
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean p_96110_1_) {
      this.enabled = p_96110_1_;
   }

   public World getLevel() {
      return this.level;
   }

   public double getLevelX() {
      return this.getX();
   }

   public double getLevelY() {
      return this.getY() + 0.5D;
   }

   public double getLevelZ() {
      return this.getZ();
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide && this.isAlive() && this.isEnabled()) {
         BlockPos blockpos = this.blockPosition();
         if (blockpos.equals(this.lastPosition)) {
            --this.cooldownTime;
         } else {
            this.setCooldown(0);
         }

         if (!this.isOnCooldown()) {
            this.setCooldown(0);
            if (this.suckInItems()) {
               this.setCooldown(4);
               this.setChanged();
            }
         }
      }

   }

   public boolean suckInItems() {
      if (HopperTileEntity.suckInItems(this)) {
         return true;
      } else {
         List<ItemEntity> list = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25D, 0.0D, 0.25D), EntityPredicates.ENTITY_STILL_ALIVE);
         if (!list.isEmpty()) {
            HopperTileEntity.addItem(this, list.get(0));
         }

         return false;
      }
   }

   public void destroy(DamageSource p_94095_1_) {
      super.destroy(p_94095_1_);
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.HOPPER);
      }

   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("TransferCooldown", this.cooldownTime);
      p_213281_1_.putBoolean("Enabled", this.enabled);
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.cooldownTime = p_70037_1_.getInt("TransferCooldown");
      this.enabled = p_70037_1_.contains("Enabled") ? p_70037_1_.getBoolean("Enabled") : true;
   }

   public void setCooldown(int p_98042_1_) {
      this.cooldownTime = p_98042_1_;
   }

   public boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   public Container createMenu(int p_213968_1_, PlayerInventory p_213968_2_) {
      return new HopperContainer(p_213968_1_, p_213968_2_, this);
   }
}
