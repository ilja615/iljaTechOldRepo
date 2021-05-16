package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Container {
   private final NonNullList<ItemStack> lastSlots = NonNullList.create();
   public final List<Slot> slots = Lists.newArrayList();
   private final List<IntReferenceHolder> dataSlots = Lists.newArrayList();
   @Nullable
   private final ContainerType<?> menuType;
   public final int containerId;
   @OnlyIn(Dist.CLIENT)
   private short changeUid;
   private int quickcraftType = -1;
   private int quickcraftStatus;
   private final Set<Slot> quickcraftSlots = Sets.newHashSet();
   private final List<IContainerListener> containerListeners = Lists.newArrayList();
   private final Set<PlayerEntity> unSynchedPlayers = Sets.newHashSet();

   protected Container(@Nullable ContainerType<?> p_i50105_1_, int p_i50105_2_) {
      this.menuType = p_i50105_1_;
      this.containerId = p_i50105_2_;
   }

   protected static boolean stillValid(IWorldPosCallable p_216963_0_, PlayerEntity p_216963_1_, Block p_216963_2_) {
      return p_216963_0_.evaluate((p_216960_2_, p_216960_3_) -> {
         return !p_216960_2_.getBlockState(p_216960_3_).is(p_216963_2_) ? false : p_216963_1_.distanceToSqr((double)p_216960_3_.getX() + 0.5D, (double)p_216960_3_.getY() + 0.5D, (double)p_216960_3_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   public ContainerType<?> getType() {
      if (this.menuType == null) {
         throw new UnsupportedOperationException("Unable to construct this menu by type");
      } else {
         return this.menuType;
      }
   }

   protected static void checkContainerSize(IInventory p_216962_0_, int p_216962_1_) {
      int i = p_216962_0_.getContainerSize();
      if (i < p_216962_1_) {
         throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + p_216962_1_);
      }
   }

   protected static void checkContainerDataCount(IIntArray p_216959_0_, int p_216959_1_) {
      int i = p_216959_0_.getCount();
      if (i < p_216959_1_) {
         throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + p_216959_1_);
      }
   }

   protected Slot addSlot(Slot p_75146_1_) {
      p_75146_1_.index = this.slots.size();
      this.slots.add(p_75146_1_);
      this.lastSlots.add(ItemStack.EMPTY);
      return p_75146_1_;
   }

   protected IntReferenceHolder addDataSlot(IntReferenceHolder p_216958_1_) {
      this.dataSlots.add(p_216958_1_);
      return p_216958_1_;
   }

   protected void addDataSlots(IIntArray p_216961_1_) {
      for(int i = 0; i < p_216961_1_.getCount(); ++i) {
         this.addDataSlot(IntReferenceHolder.forContainer(p_216961_1_, i));
      }

   }

   public void addSlotListener(IContainerListener p_75132_1_) {
      if (!this.containerListeners.contains(p_75132_1_)) {
         this.containerListeners.add(p_75132_1_);
         p_75132_1_.refreshContainer(this, this.getItems());
         this.broadcastChanges();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void removeSlotListener(IContainerListener p_82847_1_) {
      this.containerListeners.remove(p_82847_1_);
   }

   public NonNullList<ItemStack> getItems() {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(int i = 0; i < this.slots.size(); ++i) {
         nonnulllist.add(this.slots.get(i).getItem());
      }

      return nonnulllist;
   }

   public void broadcastChanges() {
      for(int i = 0; i < this.slots.size(); ++i) {
         ItemStack itemstack = this.slots.get(i).getItem();
         ItemStack itemstack1 = this.lastSlots.get(i);
         if (!ItemStack.matches(itemstack1, itemstack)) {
            boolean clientStackChanged = !itemstack1.equals(itemstack, true);
            ItemStack itemstack2 = itemstack.copy();
            this.lastSlots.set(i, itemstack2);

            if (clientStackChanged)
            for(IContainerListener icontainerlistener : this.containerListeners) {
               icontainerlistener.slotChanged(this, i, itemstack2);
            }
         }
      }

      for(int j = 0; j < this.dataSlots.size(); ++j) {
         IntReferenceHolder intreferenceholder = this.dataSlots.get(j);
         if (intreferenceholder.checkAndClearUpdateFlag()) {
            for(IContainerListener icontainerlistener1 : this.containerListeners) {
               icontainerlistener1.setContainerData(this, j, intreferenceholder.get());
            }
         }
      }

   }

   public boolean clickMenuButton(PlayerEntity p_75140_1_, int p_75140_2_) {
      return false;
   }

   public Slot getSlot(int p_75139_1_) {
      return this.slots.get(p_75139_1_);
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      Slot slot = this.slots.get(p_82846_2_);
      return slot != null ? slot.getItem() : ItemStack.EMPTY;
   }

   public ItemStack clicked(int p_184996_1_, int p_184996_2_, ClickType p_184996_3_, PlayerEntity p_184996_4_) {
      try {
         return this.doClick(p_184996_1_, p_184996_2_, p_184996_3_, p_184996_4_);
      } catch (Exception exception) {
         CrashReport crashreport = CrashReport.forThrowable(exception, "Container click");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Click info");
         crashreportcategory.setDetail("Menu Type", () -> {
            return this.menuType != null ? Registry.MENU.getKey(this.menuType).toString() : "<no type>";
         });
         crashreportcategory.setDetail("Menu Class", () -> {
            return this.getClass().getCanonicalName();
         });
         crashreportcategory.setDetail("Slot Count", this.slots.size());
         crashreportcategory.setDetail("Slot", p_184996_1_);
         crashreportcategory.setDetail("Button", p_184996_2_);
         crashreportcategory.setDetail("Type", p_184996_3_);
         throw new ReportedException(crashreport);
      }
   }

   private ItemStack doClick(int p_241440_1_, int p_241440_2_, ClickType p_241440_3_, PlayerEntity p_241440_4_) {
      ItemStack itemstack = ItemStack.EMPTY;
      PlayerInventory playerinventory = p_241440_4_.inventory;
      if (p_241440_3_ == ClickType.QUICK_CRAFT) {
         int i1 = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(p_241440_2_);
         if ((i1 != 1 || this.quickcraftStatus != 2) && i1 != this.quickcraftStatus) {
            this.resetQuickCraft();
         } else if (playerinventory.getCarried().isEmpty()) {
            this.resetQuickCraft();
         } else if (this.quickcraftStatus == 0) {
            this.quickcraftType = getQuickcraftType(p_241440_2_);
            if (isValidQuickcraftType(this.quickcraftType, p_241440_4_)) {
               this.quickcraftStatus = 1;
               this.quickcraftSlots.clear();
            } else {
               this.resetQuickCraft();
            }
         } else if (this.quickcraftStatus == 1) {
            Slot slot7 = this.slots.get(p_241440_1_);
            ItemStack itemstack12 = playerinventory.getCarried();
            if (slot7 != null && canItemQuickReplace(slot7, itemstack12, true) && slot7.mayPlace(itemstack12) && (this.quickcraftType == 2 || itemstack12.getCount() > this.quickcraftSlots.size()) && this.canDragTo(slot7)) {
               this.quickcraftSlots.add(slot7);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               ItemStack itemstack10 = playerinventory.getCarried().copy();
               int k1 = playerinventory.getCarried().getCount();

               for(Slot slot8 : this.quickcraftSlots) {
                  ItemStack itemstack13 = playerinventory.getCarried();
                  if (slot8 != null && canItemQuickReplace(slot8, itemstack13, true) && slot8.mayPlace(itemstack13) && (this.quickcraftType == 2 || itemstack13.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(slot8)) {
                     ItemStack itemstack14 = itemstack10.copy();
                     int j3 = slot8.hasItem() ? slot8.getItem().getCount() : 0;
                     getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, itemstack14, j3);
                     int k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getMaxStackSize(itemstack14));
                     if (itemstack14.getCount() > k3) {
                        itemstack14.setCount(k3);
                     }

                     k1 -= itemstack14.getCount() - j3;
                     slot8.set(itemstack14);
                  }
               }

               itemstack10.setCount(k1);
               playerinventory.setCarried(itemstack10);
            }

            this.resetQuickCraft();
         } else {
            this.resetQuickCraft();
         }
      } else if (this.quickcraftStatus != 0) {
         this.resetQuickCraft();
      } else if ((p_241440_3_ == ClickType.PICKUP || p_241440_3_ == ClickType.QUICK_MOVE) && (p_241440_2_ == 0 || p_241440_2_ == 1)) {
         if (p_241440_1_ == -999) {
            if (!playerinventory.getCarried().isEmpty()) {
               if (p_241440_2_ == 0) {
                  p_241440_4_.drop(playerinventory.getCarried(), true);
                  playerinventory.setCarried(ItemStack.EMPTY);
               }

               if (p_241440_2_ == 1) {
                  p_241440_4_.drop(playerinventory.getCarried().split(1), true);
               }
            }
         } else if (p_241440_3_ == ClickType.QUICK_MOVE) {
            if (p_241440_1_ < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot5 = this.slots.get(p_241440_1_);
            if (slot5 == null || !slot5.mayPickup(p_241440_4_)) {
               return ItemStack.EMPTY;
            }

            for(ItemStack itemstack8 = this.quickMoveStack(p_241440_4_, p_241440_1_); !itemstack8.isEmpty() && ItemStack.isSame(slot5.getItem(), itemstack8); itemstack8 = this.quickMoveStack(p_241440_4_, p_241440_1_)) {
               itemstack = itemstack8.copy();
            }
         } else {
            if (p_241440_1_ < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot6 = this.slots.get(p_241440_1_);
            if (slot6 != null) {
               ItemStack itemstack9 = slot6.getItem();
               ItemStack itemstack11 = playerinventory.getCarried();
               if (!itemstack9.isEmpty()) {
                  itemstack = itemstack9.copy();
               }

               if (itemstack9.isEmpty()) {
                  if (!itemstack11.isEmpty() && slot6.mayPlace(itemstack11)) {
                     int j2 = p_241440_2_ == 0 ? itemstack11.getCount() : 1;
                     if (j2 > slot6.getMaxStackSize(itemstack11)) {
                        j2 = slot6.getMaxStackSize(itemstack11);
                     }

                     slot6.set(itemstack11.split(j2));
                  }
               } else if (slot6.mayPickup(p_241440_4_)) {
                  if (itemstack11.isEmpty()) {
                     if (itemstack9.isEmpty()) {
                        slot6.set(ItemStack.EMPTY);
                        playerinventory.setCarried(ItemStack.EMPTY);
                     } else {
                        int k2 = p_241440_2_ == 0 ? itemstack9.getCount() : (itemstack9.getCount() + 1) / 2;
                        playerinventory.setCarried(slot6.remove(k2));
                        if (itemstack9.isEmpty()) {
                           slot6.set(ItemStack.EMPTY);
                        }

                        slot6.onTake(p_241440_4_, playerinventory.getCarried());
                     }
                  } else if (slot6.mayPlace(itemstack11)) {
                     if (consideredTheSameItem(itemstack9, itemstack11)) {
                        int l2 = p_241440_2_ == 0 ? itemstack11.getCount() : 1;
                        if (l2 > slot6.getMaxStackSize(itemstack11) - itemstack9.getCount()) {
                           l2 = slot6.getMaxStackSize(itemstack11) - itemstack9.getCount();
                        }

                        if (l2 > itemstack11.getMaxStackSize() - itemstack9.getCount()) {
                           l2 = itemstack11.getMaxStackSize() - itemstack9.getCount();
                        }

                        itemstack11.shrink(l2);
                        itemstack9.grow(l2);
                     } else if (itemstack11.getCount() <= slot6.getMaxStackSize(itemstack11)) {
                        slot6.set(itemstack11);
                        playerinventory.setCarried(itemstack9);
                     }
                  } else if (itemstack11.getMaxStackSize() > 1 && consideredTheSameItem(itemstack9, itemstack11) && !itemstack9.isEmpty()) {
                     int i3 = itemstack9.getCount();
                     if (i3 + itemstack11.getCount() <= itemstack11.getMaxStackSize()) {
                        itemstack11.grow(i3);
                        itemstack9 = slot6.remove(i3);
                        if (itemstack9.isEmpty()) {
                           slot6.set(ItemStack.EMPTY);
                        }

                        slot6.onTake(p_241440_4_, playerinventory.getCarried());
                     }
                  }
               }

               slot6.setChanged();
            }
         }
      } else if (p_241440_3_ == ClickType.SWAP) {
         Slot slot = this.slots.get(p_241440_1_);
         ItemStack itemstack1 = playerinventory.getItem(p_241440_2_);
         ItemStack itemstack2 = slot.getItem();
         if (!itemstack1.isEmpty() || !itemstack2.isEmpty()) {
            if (itemstack1.isEmpty()) {
               if (slot.mayPickup(p_241440_4_)) {
                  playerinventory.setItem(p_241440_2_, itemstack2);
                  slot.onSwapCraft(itemstack2.getCount());
                  slot.set(ItemStack.EMPTY);
                  slot.onTake(p_241440_4_, itemstack2);
               }
            } else if (itemstack2.isEmpty()) {
               if (slot.mayPlace(itemstack1)) {
                  int i = slot.getMaxStackSize(itemstack1);
                  if (itemstack1.getCount() > i) {
                     slot.set(itemstack1.split(i));
                  } else {
                     slot.set(itemstack1);
                     playerinventory.setItem(p_241440_2_, ItemStack.EMPTY);
                  }
               }
            } else if (slot.mayPickup(p_241440_4_) && slot.mayPlace(itemstack1)) {
               int l1 = slot.getMaxStackSize(itemstack1);
               if (itemstack1.getCount() > l1) {
                  slot.set(itemstack1.split(l1));
                  slot.onTake(p_241440_4_, itemstack2);
                  if (!playerinventory.add(itemstack2)) {
                     p_241440_4_.drop(itemstack2, true);
                  }
               } else {
                  slot.set(itemstack1);
                  playerinventory.setItem(p_241440_2_, itemstack2);
                  slot.onTake(p_241440_4_, itemstack2);
               }
            }
         }
      } else if (p_241440_3_ == ClickType.CLONE && p_241440_4_.abilities.instabuild && playerinventory.getCarried().isEmpty() && p_241440_1_ >= 0) {
         Slot slot4 = this.slots.get(p_241440_1_);
         if (slot4 != null && slot4.hasItem()) {
            ItemStack itemstack7 = slot4.getItem().copy();
            itemstack7.setCount(itemstack7.getMaxStackSize());
            playerinventory.setCarried(itemstack7);
         }
      } else if (p_241440_3_ == ClickType.THROW && playerinventory.getCarried().isEmpty() && p_241440_1_ >= 0) {
         Slot slot3 = this.slots.get(p_241440_1_);
         if (slot3 != null && slot3.hasItem() && slot3.mayPickup(p_241440_4_)) {
            ItemStack itemstack6 = slot3.remove(p_241440_2_ == 0 ? 1 : slot3.getItem().getCount());
            slot3.onTake(p_241440_4_, itemstack6);
            p_241440_4_.drop(itemstack6, true);
         }
      } else if (p_241440_3_ == ClickType.PICKUP_ALL && p_241440_1_ >= 0) {
         Slot slot2 = this.slots.get(p_241440_1_);
         ItemStack itemstack5 = playerinventory.getCarried();
         if (!itemstack5.isEmpty() && (slot2 == null || !slot2.hasItem() || !slot2.mayPickup(p_241440_4_))) {
            int j1 = p_241440_2_ == 0 ? 0 : this.slots.size() - 1;
            int i2 = p_241440_2_ == 0 ? 1 : -1;

            for(int j = 0; j < 2; ++j) {
               for(int k = j1; k >= 0 && k < this.slots.size() && itemstack5.getCount() < itemstack5.getMaxStackSize(); k += i2) {
                  Slot slot1 = this.slots.get(k);
                  if (slot1.hasItem() && canItemQuickReplace(slot1, itemstack5, true) && slot1.mayPickup(p_241440_4_) && this.canTakeItemForPickAll(itemstack5, slot1)) {
                     ItemStack itemstack3 = slot1.getItem();
                     if (j != 0 || itemstack3.getCount() != itemstack3.getMaxStackSize()) {
                        int l = Math.min(itemstack5.getMaxStackSize() - itemstack5.getCount(), itemstack3.getCount());
                        ItemStack itemstack4 = slot1.remove(l);
                        itemstack5.grow(l);
                        if (itemstack4.isEmpty()) {
                           slot1.set(ItemStack.EMPTY);
                        }

                        slot1.onTake(p_241440_4_, itemstack4);
                     }
                  }
               }
            }
         }

         this.broadcastChanges();
      }

      return itemstack;
   }

   public static boolean consideredTheSameItem(ItemStack p_195929_0_, ItemStack p_195929_1_) {
      return p_195929_0_.getItem() == p_195929_1_.getItem() && ItemStack.tagMatches(p_195929_0_, p_195929_1_);
   }

   public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
      return true;
   }

   public void removed(PlayerEntity p_75134_1_) {
      PlayerInventory playerinventory = p_75134_1_.inventory;
      if (!playerinventory.getCarried().isEmpty()) {
         p_75134_1_.drop(playerinventory.getCarried(), false);
         playerinventory.setCarried(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(PlayerEntity p_193327_1_, World p_193327_2_, IInventory p_193327_3_) {
      if (!p_193327_1_.isAlive() || p_193327_1_ instanceof ServerPlayerEntity && ((ServerPlayerEntity)p_193327_1_).hasDisconnected()) {
         for(int j = 0; j < p_193327_3_.getContainerSize(); ++j) {
            p_193327_1_.drop(p_193327_3_.removeItemNoUpdate(j), false);
         }

      } else {
         for(int i = 0; i < p_193327_3_.getContainerSize(); ++i) {
            p_193327_1_.inventory.placeItemBackInInventory(p_193327_2_, p_193327_3_.removeItemNoUpdate(i));
         }

      }
   }

   public void slotsChanged(IInventory p_75130_1_) {
      this.broadcastChanges();
   }

   public void setItem(int p_75141_1_, ItemStack p_75141_2_) {
      this.getSlot(p_75141_1_).set(p_75141_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setAll(List<ItemStack> p_190896_1_) {
      for(int i = 0; i < p_190896_1_.size(); ++i) {
         this.getSlot(i).set(p_190896_1_.get(i));
      }

   }

   public void setData(int p_75137_1_, int p_75137_2_) {
      this.dataSlots.get(p_75137_1_).set(p_75137_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public short backup(PlayerInventory p_75136_1_) {
      ++this.changeUid;
      return this.changeUid;
   }

   public boolean isSynched(PlayerEntity p_75129_1_) {
      return !this.unSynchedPlayers.contains(p_75129_1_);
   }

   public void setSynched(PlayerEntity p_75128_1_, boolean p_75128_2_) {
      if (p_75128_2_) {
         this.unSynchedPlayers.remove(p_75128_1_);
      } else {
         this.unSynchedPlayers.add(p_75128_1_);
      }

   }

   public abstract boolean stillValid(PlayerEntity p_75145_1_);

   protected boolean moveItemStackTo(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
      boolean flag = false;
      int i = p_75135_2_;
      if (p_75135_4_) {
         i = p_75135_3_ - 1;
      }

      if (p_75135_1_.isStackable()) {
         while(!p_75135_1_.isEmpty()) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            Slot slot = this.slots.get(i);
            ItemStack itemstack = slot.getItem();
            if (!itemstack.isEmpty() && consideredTheSameItem(p_75135_1_, itemstack)) {
               int j = itemstack.getCount() + p_75135_1_.getCount();
               int maxSize = Math.min(slot.getMaxStackSize(), p_75135_1_.getMaxStackSize());
               if (j <= maxSize) {
                  p_75135_1_.setCount(0);
                  itemstack.setCount(j);
                  slot.setChanged();
                  flag = true;
               } else if (itemstack.getCount() < maxSize) {
                  p_75135_1_.shrink(maxSize - itemstack.getCount());
                  itemstack.setCount(maxSize);
                  slot.setChanged();
                  flag = true;
               }
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      if (!p_75135_1_.isEmpty()) {
         if (p_75135_4_) {
            i = p_75135_3_ - 1;
         } else {
            i = p_75135_2_;
         }

         while(true) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            Slot slot1 = this.slots.get(i);
            ItemStack itemstack1 = slot1.getItem();
            if (itemstack1.isEmpty() && slot1.mayPlace(p_75135_1_)) {
               if (p_75135_1_.getCount() > slot1.getMaxStackSize()) {
                  slot1.set(p_75135_1_.split(slot1.getMaxStackSize()));
               } else {
                  slot1.set(p_75135_1_.split(p_75135_1_.getCount()));
               }

               slot1.setChanged();
               flag = true;
               break;
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      return flag;
   }

   public static int getQuickcraftType(int p_94529_0_) {
      return p_94529_0_ >> 2 & 3;
   }

   public static int getQuickcraftHeader(int p_94532_0_) {
      return p_94532_0_ & 3;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getQuickcraftMask(int p_94534_0_, int p_94534_1_) {
      return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
   }

   public static boolean isValidQuickcraftType(int p_180610_0_, PlayerEntity p_180610_1_) {
      if (p_180610_0_ == 0) {
         return true;
      } else if (p_180610_0_ == 1) {
         return true;
      } else {
         return p_180610_0_ == 2 && p_180610_1_.abilities.instabuild;
      }
   }

   protected void resetQuickCraft() {
      this.quickcraftStatus = 0;
      this.quickcraftSlots.clear();
   }

   public static boolean canItemQuickReplace(@Nullable Slot p_94527_0_, ItemStack p_94527_1_, boolean p_94527_2_) {
      boolean flag = p_94527_0_ == null || !p_94527_0_.hasItem();
      if (!flag && p_94527_1_.sameItem(p_94527_0_.getItem()) && ItemStack.tagMatches(p_94527_0_.getItem(), p_94527_1_)) {
         return p_94527_0_.getItem().getCount() + (p_94527_2_ ? 0 : p_94527_1_.getCount()) <= p_94527_1_.getMaxStackSize();
      } else {
         return flag;
      }
   }

   public static void getQuickCraftSlotCount(Set<Slot> p_94525_0_, int p_94525_1_, ItemStack p_94525_2_, int p_94525_3_) {
      switch(p_94525_1_) {
      case 0:
         p_94525_2_.setCount(MathHelper.floor((float)p_94525_2_.getCount() / (float)p_94525_0_.size()));
         break;
      case 1:
         p_94525_2_.setCount(1);
         break;
      case 2:
         p_94525_2_.setCount(p_94525_2_.getMaxStackSize());
      }

      p_94525_2_.grow(p_94525_3_);
   }

   public boolean canDragTo(Slot p_94531_1_) {
      return true;
   }

   public static int getRedstoneSignalFromBlockEntity(@Nullable TileEntity p_178144_0_) {
      return p_178144_0_ instanceof IInventory ? getRedstoneSignalFromContainer((IInventory)p_178144_0_) : 0;
   }

   public static int getRedstoneSignalFromContainer(@Nullable IInventory p_94526_0_) {
      if (p_94526_0_ == null) {
         return 0;
      } else {
         int i = 0;
         float f = 0.0F;

         for(int j = 0; j < p_94526_0_.getContainerSize(); ++j) {
            ItemStack itemstack = p_94526_0_.getItem(j);
            if (!itemstack.isEmpty()) {
               f += (float)itemstack.getCount() / (float)Math.min(p_94526_0_.getMaxStackSize(), itemstack.getMaxStackSize());
               ++i;
            }
         }

         f = f / (float)p_94526_0_.getContainerSize();
         return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
      }
   }
}
