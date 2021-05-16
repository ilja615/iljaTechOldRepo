package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.INameable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;

public abstract class LockableTileEntity extends TileEntity implements IInventory, INamedContainerProvider, INameable {
   private LockCode lockKey = LockCode.NO_LOCK;
   private ITextComponent name;

   protected LockableTileEntity(TileEntityType<?> p_i48285_1_) {
      super(p_i48285_1_);
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.lockKey = LockCode.fromTag(p_230337_2_);
      if (p_230337_2_.contains("CustomName", 8)) {
         this.name = ITextComponent.Serializer.fromJson(p_230337_2_.getString("CustomName"));
      }

   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      this.lockKey.addToTag(p_189515_1_);
      if (this.name != null) {
         p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
      }

      return p_189515_1_;
   }

   public void setCustomName(ITextComponent p_213903_1_) {
      this.name = p_213903_1_;
   }

   public ITextComponent getName() {
      return this.name != null ? this.name : this.getDefaultName();
   }

   public ITextComponent getDisplayName() {
      return this.getName();
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.name;
   }

   protected abstract ITextComponent getDefaultName();

   public boolean canOpen(PlayerEntity p_213904_1_) {
      return canUnlock(p_213904_1_, this.lockKey, this.getDisplayName());
   }

   public static boolean canUnlock(PlayerEntity p_213905_0_, LockCode p_213905_1_, ITextComponent p_213905_2_) {
      if (!p_213905_0_.isSpectator() && !p_213905_1_.unlocksWith(p_213905_0_.getMainHandItem())) {
         p_213905_0_.displayClientMessage(new TranslationTextComponent("container.isLocked", p_213905_2_), true);
         p_213905_0_.playNotifySound(SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return this.canOpen(p_createMenu_3_) ? this.createMenu(p_createMenu_1_, p_createMenu_2_) : null;
   }

   protected abstract Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_);

   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> createUnSidedHandler());
   protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
      return new net.minecraftforge.items.wrapper.InvWrapper(this);
   }

   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @javax.annotation.Nullable net.minecraft.util.Direction side) {
      if (!this.remove && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
         return itemHandler.cast();
      return super.getCapability(cap, side);
   }

   @Override
   protected void invalidateCaps() {
      super.invalidateCaps();
      itemHandler.invalidate();
   }
}
