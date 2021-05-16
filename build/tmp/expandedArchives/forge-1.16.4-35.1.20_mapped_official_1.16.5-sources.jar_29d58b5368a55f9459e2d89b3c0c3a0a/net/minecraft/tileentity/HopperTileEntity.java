package net.minecraft.tileentity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class HopperTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
   private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
   private int cooldownTime = -1;
   private long tickedGameTime;

   public HopperTileEntity() {
      super(TileEntityType.HOPPER);
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(p_230337_2_)) {
         ItemStackHelper.loadAllItems(p_230337_2_, this.items);
      }

      this.cooldownTime = p_230337_2_.getInt("TransferCooldown");
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      if (!this.trySaveLootTable(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.items);
      }

      p_189515_1_.putInt("TransferCooldown", this.cooldownTime);
      return p_189515_1_;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      this.unpackLootTable((PlayerEntity)null);
      return ItemStackHelper.removeItem(this.getItems(), p_70298_1_, p_70298_2_);
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      this.unpackLootTable((PlayerEntity)null);
      this.getItems().set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getMaxStackSize()) {
         p_70299_2_.setCount(this.getMaxStackSize());
      }

   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.hopper");
   }

   public void tick() {
      if (this.level != null && !this.level.isClientSide) {
         --this.cooldownTime;
         this.tickedGameTime = this.level.getGameTime();
         if (!this.isOnCooldown()) {
            this.setCooldown(0);
            this.tryMoveItems(() -> {
               return suckInItems(this);
            });
         }

      }
   }

   private boolean tryMoveItems(Supplier<Boolean> p_200109_1_) {
      if (this.level != null && !this.level.isClientSide) {
         if (!this.isOnCooldown() && this.getBlockState().getValue(HopperBlock.ENABLED)) {
            boolean flag = false;
            if (!this.isEmpty()) {
               flag = this.ejectItems();
            }

            if (!this.inventoryFull()) {
               flag |= p_200109_1_.get();
            }

            if (flag) {
               this.setCooldown(8);
               this.setChanged();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean inventoryFull() {
      for(ItemStack itemstack : this.items) {
         if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean ejectItems() {
      if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(this)) return true;
      IInventory iinventory = this.getAttachedContainer();
      if (iinventory == null) {
         return false;
      } else {
         Direction direction = this.getBlockState().getValue(HopperBlock.FACING).getOpposite();
         if (this.isFullContainer(iinventory, direction)) {
            return false;
         } else {
            for(int i = 0; i < this.getContainerSize(); ++i) {
               if (!this.getItem(i).isEmpty()) {
                  ItemStack itemstack = this.getItem(i).copy();
                  ItemStack itemstack1 = addItem(this, iinventory, this.removeItem(i, 1), direction);
                  if (itemstack1.isEmpty()) {
                     iinventory.setChanged();
                     return true;
                  }

                  this.setItem(i, itemstack);
               }
            }

            return false;
         }
      }
   }

   private static IntStream getSlots(IInventory p_213972_0_, Direction p_213972_1_) {
      return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory)p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getContainerSize());
   }

   private boolean isFullContainer(IInventory p_174919_1_, Direction p_174919_2_) {
      return getSlots(p_174919_1_, p_174919_2_).allMatch((p_213970_1_) -> {
         ItemStack itemstack = p_174919_1_.getItem(p_213970_1_);
         return itemstack.getCount() >= itemstack.getMaxStackSize();
      });
   }

   private static boolean isEmptyContainer(IInventory p_174917_0_, Direction p_174917_1_) {
      return getSlots(p_174917_0_, p_174917_1_).allMatch((p_213973_1_) -> {
         return p_174917_0_.getItem(p_213973_1_).isEmpty();
      });
   }

   public static boolean suckInItems(IHopper p_145891_0_) {
      Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(p_145891_0_);
      if (ret != null) return ret;
      IInventory iinventory = getSourceContainer(p_145891_0_);
      if (iinventory != null) {
         Direction direction = Direction.DOWN;
         return isEmptyContainer(iinventory, direction) ? false : getSlots(iinventory, direction).anyMatch((p_213971_3_) -> {
            return tryTakeInItemFromSlot(p_145891_0_, iinventory, p_213971_3_, direction);
         });
      } else {
         for(ItemEntity itementity : getItemsAtAndAbove(p_145891_0_)) {
            if (addItem(p_145891_0_, itementity)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean tryTakeInItemFromSlot(IHopper p_174915_0_, IInventory p_174915_1_, int p_174915_2_, Direction p_174915_3_) {
      ItemStack itemstack = p_174915_1_.getItem(p_174915_2_);
      if (!itemstack.isEmpty() && canTakeItemFromContainer(p_174915_1_, itemstack, p_174915_2_, p_174915_3_)) {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = addItem(p_174915_1_, p_174915_0_, p_174915_1_.removeItem(p_174915_2_, 1), (Direction)null);
         if (itemstack2.isEmpty()) {
            p_174915_1_.setChanged();
            return true;
         }

         p_174915_1_.setItem(p_174915_2_, itemstack1);
      }

      return false;
   }

   public static boolean addItem(IInventory p_200114_0_, ItemEntity p_200114_1_) {
      boolean flag = false;
      ItemStack itemstack = p_200114_1_.getItem().copy();
      ItemStack itemstack1 = addItem((IInventory)null, p_200114_0_, itemstack, (Direction)null);
      if (itemstack1.isEmpty()) {
         flag = true;
         p_200114_1_.remove();
      } else {
         p_200114_1_.setItem(itemstack1);
      }

      return flag;
   }

   public static ItemStack addItem(@Nullable IInventory p_174918_0_, IInventory p_174918_1_, ItemStack p_174918_2_, @Nullable Direction p_174918_3_) {
      if (p_174918_1_ instanceof ISidedInventory && p_174918_3_ != null) {
         ISidedInventory isidedinventory = (ISidedInventory)p_174918_1_;
         int[] aint = isidedinventory.getSlotsForFace(p_174918_3_);

         for(int k = 0; k < aint.length && !p_174918_2_.isEmpty(); ++k) {
            p_174918_2_ = tryMoveInItem(p_174918_0_, p_174918_1_, p_174918_2_, aint[k], p_174918_3_);
         }
      } else {
         int i = p_174918_1_.getContainerSize();

         for(int j = 0; j < i && !p_174918_2_.isEmpty(); ++j) {
            p_174918_2_ = tryMoveInItem(p_174918_0_, p_174918_1_, p_174918_2_, j, p_174918_3_);
         }
      }

      return p_174918_2_;
   }

   private static boolean canPlaceItemInContainer(IInventory p_174920_0_, ItemStack p_174920_1_, int p_174920_2_, @Nullable Direction p_174920_3_) {
      if (!p_174920_0_.canPlaceItem(p_174920_2_, p_174920_1_)) {
         return false;
      } else {
         return !(p_174920_0_ instanceof ISidedInventory) || ((ISidedInventory)p_174920_0_).canPlaceItemThroughFace(p_174920_2_, p_174920_1_, p_174920_3_);
      }
   }

   private static boolean canTakeItemFromContainer(IInventory p_174921_0_, ItemStack p_174921_1_, int p_174921_2_, Direction p_174921_3_) {
      return !(p_174921_0_ instanceof ISidedInventory) || ((ISidedInventory)p_174921_0_).canTakeItemThroughFace(p_174921_2_, p_174921_1_, p_174921_3_);
   }

   private static ItemStack tryMoveInItem(@Nullable IInventory p_174916_0_, IInventory p_174916_1_, ItemStack p_174916_2_, int p_174916_3_, @Nullable Direction p_174916_4_) {
      ItemStack itemstack = p_174916_1_.getItem(p_174916_3_);
      if (canPlaceItemInContainer(p_174916_1_, p_174916_2_, p_174916_3_, p_174916_4_)) {
         boolean flag = false;
         boolean flag1 = p_174916_1_.isEmpty();
         if (itemstack.isEmpty()) {
            p_174916_1_.setItem(p_174916_3_, p_174916_2_);
            p_174916_2_ = ItemStack.EMPTY;
            flag = true;
         } else if (canMergeItems(itemstack, p_174916_2_)) {
            int i = p_174916_2_.getMaxStackSize() - itemstack.getCount();
            int j = Math.min(p_174916_2_.getCount(), i);
            p_174916_2_.shrink(j);
            itemstack.grow(j);
            flag = j > 0;
         }

         if (flag) {
            if (flag1 && p_174916_1_ instanceof HopperTileEntity) {
               HopperTileEntity hoppertileentity1 = (HopperTileEntity)p_174916_1_;
               if (!hoppertileentity1.isOnCustomCooldown()) {
                  int k = 0;
                  if (p_174916_0_ instanceof HopperTileEntity) {
                     HopperTileEntity hoppertileentity = (HopperTileEntity)p_174916_0_;
                     if (hoppertileentity1.tickedGameTime >= hoppertileentity.tickedGameTime) {
                        k = 1;
                     }
                  }

                  hoppertileentity1.setCooldown(8 - k);
               }
            }

            p_174916_1_.setChanged();
         }
      }

      return p_174916_2_;
   }

   @Nullable
   private IInventory getAttachedContainer() {
      Direction direction = this.getBlockState().getValue(HopperBlock.FACING);
      return getContainerAt(this.getLevel(), this.worldPosition.relative(direction));
   }

   @Nullable
   public static IInventory getSourceContainer(IHopper p_145884_0_) {
      return getContainerAt(p_145884_0_.getLevel(), p_145884_0_.getLevelX(), p_145884_0_.getLevelY() + 1.0D, p_145884_0_.getLevelZ());
   }

   public static List<ItemEntity> getItemsAtAndAbove(IHopper p_200115_0_) {
      return p_200115_0_.getSuckShape().toAabbs().stream().flatMap((p_200110_1_) -> {
         return p_200115_0_.getLevel().getEntitiesOfClass(ItemEntity.class, p_200110_1_.move(p_200115_0_.getLevelX() - 0.5D, p_200115_0_.getLevelY() - 0.5D, p_200115_0_.getLevelZ() - 0.5D), EntityPredicates.ENTITY_STILL_ALIVE).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static IInventory getContainerAt(World p_195484_0_, BlockPos p_195484_1_) {
      return getContainerAt(p_195484_0_, (double)p_195484_1_.getX() + 0.5D, (double)p_195484_1_.getY() + 0.5D, (double)p_195484_1_.getZ() + 0.5D);
   }

   @Nullable
   public static IInventory getContainerAt(World p_145893_0_, double p_145893_1_, double p_145893_3_, double p_145893_5_) {
      IInventory iinventory = null;
      BlockPos blockpos = new BlockPos(p_145893_1_, p_145893_3_, p_145893_5_);
      BlockState blockstate = p_145893_0_.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block instanceof ISidedInventoryProvider) {
         iinventory = ((ISidedInventoryProvider)block).getContainer(blockstate, p_145893_0_, blockpos);
      } else if (blockstate.hasTileEntity()) {
         TileEntity tileentity = p_145893_0_.getBlockEntity(blockpos);
         if (tileentity instanceof IInventory) {
            iinventory = (IInventory)tileentity;
            if (iinventory instanceof ChestTileEntity && block instanceof ChestBlock) {
               iinventory = ChestBlock.getContainer((ChestBlock)block, blockstate, p_145893_0_, blockpos, true);
            }
         }
      }

      if (iinventory == null) {
         List<Entity> list = p_145893_0_.getEntities((Entity)null, new AxisAlignedBB(p_145893_1_ - 0.5D, p_145893_3_ - 0.5D, p_145893_5_ - 0.5D, p_145893_1_ + 0.5D, p_145893_3_ + 0.5D, p_145893_5_ + 0.5D), EntityPredicates.CONTAINER_ENTITY_SELECTOR);
         if (!list.isEmpty()) {
            iinventory = (IInventory)list.get(p_145893_0_.random.nextInt(list.size()));
         }
      }

      return iinventory;
   }

   private static boolean canMergeItems(ItemStack p_145894_0_, ItemStack p_145894_1_) {
      if (p_145894_0_.getItem() != p_145894_1_.getItem()) {
         return false;
      } else if (p_145894_0_.getDamageValue() != p_145894_1_.getDamageValue()) {
         return false;
      } else if (p_145894_0_.getCount() > p_145894_0_.getMaxStackSize()) {
         return false;
      } else {
         return ItemStack.tagMatches(p_145894_0_, p_145894_1_);
      }
   }

   public double getLevelX() {
      return (double)this.worldPosition.getX() + 0.5D;
   }

   public double getLevelY() {
      return (double)this.worldPosition.getY() + 0.5D;
   }

   public double getLevelZ() {
      return (double)this.worldPosition.getZ() + 0.5D;
   }

   public void setCooldown(int p_145896_1_) {
      this.cooldownTime = p_145896_1_;
   }

   private boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   public boolean isOnCustomCooldown() {
      return this.cooldownTime > 8;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.items = p_199721_1_;
   }

   public void entityInside(Entity p_200113_1_) {
      if (p_200113_1_ instanceof ItemEntity) {
         BlockPos blockpos = this.getBlockPos();
         if (VoxelShapes.joinIsNotEmpty(VoxelShapes.create(p_200113_1_.getBoundingBox().move((double)(-blockpos.getX()), (double)(-blockpos.getY()), (double)(-blockpos.getZ()))), this.getSuckShape(), IBooleanFunction.AND)) {
            this.tryMoveItems(() -> {
               return addItem(this, (ItemEntity)p_200113_1_);
            });
         }
      }

   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new HopperContainer(p_213906_1_, p_213906_2_, this);
   }

   @Override
   protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
      return new net.minecraftforge.items.VanillaHopperItemHandler(this);
   }

   public long getLastUpdateTime() {
      return this.tickedGameTime;
   }
}
