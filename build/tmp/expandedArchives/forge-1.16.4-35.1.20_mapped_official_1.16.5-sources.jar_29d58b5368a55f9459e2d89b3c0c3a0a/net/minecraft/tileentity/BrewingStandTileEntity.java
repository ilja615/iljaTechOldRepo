package net.minecraft.tileentity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BrewingStandTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity {
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
   private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
   private int brewTime;
   private boolean[] lastPotionCount;
   private Item ingredient;
   private int fuel;
   protected final IIntArray dataAccess = new IIntArray() {
      public int get(int p_221476_1_) {
         switch(p_221476_1_) {
         case 0:
            return BrewingStandTileEntity.this.brewTime;
         case 1:
            return BrewingStandTileEntity.this.fuel;
         default:
            return 0;
         }
      }

      public void set(int p_221477_1_, int p_221477_2_) {
         switch(p_221477_1_) {
         case 0:
            BrewingStandTileEntity.this.brewTime = p_221477_2_;
            break;
         case 1:
            BrewingStandTileEntity.this.fuel = p_221477_2_;
         }

      }

      public int getCount() {
         return 2;
      }
   };

   public BrewingStandTileEntity() {
      super(TileEntityType.BREWING_STAND);
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.brewing");
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void tick() {
      ItemStack itemstack = this.items.get(4);
      if (this.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
         this.fuel = 20;
         itemstack.shrink(1);
         this.setChanged();
      }

      boolean flag = this.isBrewable();
      boolean flag1 = this.brewTime > 0;
      ItemStack itemstack1 = this.items.get(3);
      if (flag1) {
         --this.brewTime;
         boolean flag2 = this.brewTime == 0;
         if (flag2 && flag) {
            this.doBrew();
            this.setChanged();
         } else if (!flag) {
            this.brewTime = 0;
            this.setChanged();
         } else if (this.ingredient != itemstack1.getItem()) {
            this.brewTime = 0;
            this.setChanged();
         }
      } else if (flag && this.fuel > 0) {
         --this.fuel;
         this.brewTime = 400;
         this.ingredient = itemstack1.getItem();
         this.setChanged();
      }

      if (!this.level.isClientSide) {
         boolean[] aboolean = this.getPotionBits();
         if (!Arrays.equals(aboolean, this.lastPotionCount)) {
            this.lastPotionCount = aboolean;
            BlockState blockstate = this.level.getBlockState(this.getBlockPos());
            if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
               return;
            }

            for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
               blockstate = blockstate.setValue(BrewingStandBlock.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
            }

            this.level.setBlock(this.worldPosition, blockstate, 2);
         }
      }

   }

   public boolean[] getPotionBits() {
      boolean[] aboolean = new boolean[3];

      for(int i = 0; i < 3; ++i) {
         if (!this.items.get(i).isEmpty()) {
            aboolean[i] = true;
         }
      }

      return aboolean;
   }

   private boolean isBrewable() {
      ItemStack itemstack = this.items.get(3);
      if (!itemstack.isEmpty()) return net.minecraftforge.common.brewing.BrewingRecipeRegistry.canBrew(items, itemstack, SLOTS_FOR_SIDES); // divert to VanillaBrewingRegistry
      if (itemstack.isEmpty()) {
         return false;
      } else if (!PotionBrewing.isIngredient(itemstack)) {
         return false;
      } else {
         for(int i = 0; i < 3; ++i) {
            ItemStack itemstack1 = this.items.get(i);
            if (!itemstack1.isEmpty() && PotionBrewing.hasMix(itemstack1, itemstack)) {
               return true;
            }
         }

         return false;
      }
   }

   private void doBrew() {
      if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(items)) return;
      ItemStack itemstack = this.items.get(3);

      net.minecraftforge.common.brewing.BrewingRecipeRegistry.brewPotions(items, itemstack, SLOTS_FOR_SIDES);
      net.minecraftforge.event.ForgeEventFactory.onPotionBrewed(items);
      BlockPos blockpos = this.getBlockPos();
      if (itemstack.hasContainerItem()) {
         ItemStack itemstack1 = itemstack.getContainerItem();
         itemstack.shrink(1);
         if (itemstack.isEmpty()) {
            itemstack = itemstack1;
         } else if (!this.level.isClientSide) {
            InventoryHelper.dropItemStack(this.level, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
         }
      }
      else itemstack.shrink(1);

      this.items.set(3, itemstack);
      this.level.levelEvent(1035, blockpos, 0);
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(p_230337_2_, this.items);
      this.brewTime = p_230337_2_.getShort("BrewTime");
      this.fuel = p_230337_2_.getByte("Fuel");
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.putShort("BrewTime", (short)this.brewTime);
      ItemStackHelper.saveAllItems(p_189515_1_, this.items);
      p_189515_1_.putByte("Fuel", (byte)this.fuel);
      return p_189515_1_;
   }

   public ItemStack getItem(int p_70301_1_) {
      return p_70301_1_ >= 0 && p_70301_1_ < this.items.size() ? this.items.get(p_70301_1_) : ItemStack.EMPTY;
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      return ItemStackHelper.takeItem(this.items, p_70304_1_);
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      if (p_70299_1_ >= 0 && p_70299_1_ < this.items.size()) {
         this.items.set(p_70299_1_, p_70299_2_);
      }

   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         return !(p_70300_1_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
      }
   }

   public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
      if (p_94041_1_ == 3) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(p_94041_2_);
      } else {
         Item item = p_94041_2_.getItem();
         if (p_94041_1_ == 4) {
            return item == Items.BLAZE_POWDER;
         } else {
            return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(p_94041_2_) && this.getItem(p_94041_1_).isEmpty();
         }
      }
   }

   public int[] getSlotsForFace(Direction p_180463_1_) {
      if (p_180463_1_ == Direction.UP) {
         return SLOTS_FOR_UP;
      } else {
         return p_180463_1_ == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDES;
      }
   }

   public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
      return this.canPlaceItem(p_180462_1_, p_180462_2_);
   }

   public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
      if (p_180461_1_ == 3) {
         return p_180461_2_.getItem() == Items.GLASS_BOTTLE;
      } else {
         return true;
      }
   }

   public void clearContent() {
      this.items.clear();
   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new BrewingStandContainer(p_213906_1_, p_213906_2_, this, this.dataAccess);
   }

   net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
      if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == Direction.UP)
            return handlers[0].cast();
         else if (facing == Direction.DOWN)
            return handlers[1].cast();
         else
            return handlers[2].cast();
      }
      return super.getCapability(capability, facing);
   }

   @Override
   protected void invalidateCaps() {
      super.invalidateCaps();
      for (int x = 0; x < handlers.length; x++)
        handlers[x].invalidate();
   }
}
