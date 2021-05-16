package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LoomContainer extends Container {
   private final IWorldPosCallable access;
   private final IntReferenceHolder selectedBannerPatternIndex = IntReferenceHolder.standalone();
   private Runnable slotUpdateListener = () -> {
   };
   private final Slot bannerSlot;
   private final Slot dyeSlot;
   private final Slot patternSlot;
   private final Slot resultSlot;
   private long lastSoundTime;
   private final IInventory inputContainer = new Inventory(3) {
      public void setChanged() {
         super.setChanged();
         LoomContainer.this.slotsChanged(this);
         LoomContainer.this.slotUpdateListener.run();
      }
   };
   private final IInventory outputContainer = new Inventory(1) {
      public void setChanged() {
         super.setChanged();
         LoomContainer.this.slotUpdateListener.run();
      }
   };

   public LoomContainer(int p_i50073_1_, PlayerInventory p_i50073_2_) {
      this(p_i50073_1_, p_i50073_2_, IWorldPosCallable.NULL);
   }

   public LoomContainer(int p_i50074_1_, PlayerInventory p_i50074_2_, final IWorldPosCallable p_i50074_3_) {
      super(ContainerType.LOOM, p_i50074_1_);
      this.access = p_i50074_3_;
      this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() instanceof BannerItem;
         }
      });
      this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() instanceof DyeItem;
         }
      });
      this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() instanceof BannerPatternItem;
         }
      });
      this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58) {
         public boolean mayPlace(ItemStack p_75214_1_) {
            return false;
         }

         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
            LoomContainer.this.bannerSlot.remove(1);
            LoomContainer.this.dyeSlot.remove(1);
            if (!LoomContainer.this.bannerSlot.hasItem() || !LoomContainer.this.dyeSlot.hasItem()) {
               LoomContainer.this.selectedBannerPatternIndex.set(0);
            }

            p_i50074_3_.execute((p_216951_1_, p_216951_2_) -> {
               long l = p_216951_1_.getGameTime();
               if (LoomContainer.this.lastSoundTime != l) {
                  p_216951_1_.playSound((PlayerEntity)null, p_216951_2_, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  LoomContainer.this.lastSoundTime = l;
               }

            });
            return super.onTake(p_190901_1_, p_190901_2_);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50074_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50074_2_, k, 8 + k * 18, 142));
      }

      this.addDataSlot(this.selectedBannerPatternIndex);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSelectedBannerPatternIndex() {
      return this.selectedBannerPatternIndex.get();
   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return stillValid(this.access, p_75145_1_, Blocks.LOOM);
   }

   public boolean clickMenuButton(PlayerEntity p_75140_1_, int p_75140_2_) {
      if (p_75140_2_ > 0 && p_75140_2_ <= BannerPattern.AVAILABLE_PATTERNS) {
         this.selectedBannerPatternIndex.set(p_75140_2_);
         this.setupResultSlot();
         return true;
      } else {
         return false;
      }
   }

   public void slotsChanged(IInventory p_75130_1_) {
      ItemStack itemstack = this.bannerSlot.getItem();
      ItemStack itemstack1 = this.dyeSlot.getItem();
      ItemStack itemstack2 = this.patternSlot.getItem();
      ItemStack itemstack3 = this.resultSlot.getItem();
      if (itemstack3.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty() && this.selectedBannerPatternIndex.get() > 0 && (this.selectedBannerPatternIndex.get() < BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT || !itemstack2.isEmpty())) {
         if (!itemstack2.isEmpty() && itemstack2.getItem() instanceof BannerPatternItem) {
            CompoundNBT compoundnbt = itemstack.getOrCreateTagElement("BlockEntityTag");
            boolean flag = compoundnbt.contains("Patterns", 9) && !itemstack.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;
            if (flag) {
               this.selectedBannerPatternIndex.set(0);
            } else {
               this.selectedBannerPatternIndex.set(((BannerPatternItem)itemstack2.getItem()).getBannerPattern().ordinal());
            }
         }
      } else {
         this.resultSlot.set(ItemStack.EMPTY);
         this.selectedBannerPatternIndex.set(0);
      }

      this.setupResultSlot();
      this.broadcastChanges();
   }

   @OnlyIn(Dist.CLIENT)
   public void registerUpdateListener(Runnable p_217020_1_) {
      this.slotUpdateListener = p_217020_1_;
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == this.resultSlot.index) {
            if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_82846_2_ != this.dyeSlot.index && p_82846_2_ != this.bannerSlot.index && p_82846_2_ != this.patternSlot.index) {
            if (itemstack1.getItem() instanceof BannerItem) {
               if (!this.moveItemStackTo(itemstack1, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (itemstack1.getItem() instanceof DyeItem) {
               if (!this.moveItemStackTo(itemstack1, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (itemstack1.getItem() instanceof BannerPatternItem) {
               if (!this.moveItemStackTo(itemstack1, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 4 && p_82846_2_ < 31) {
               if (!this.moveItemStackTo(itemstack1, 31, 40, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 31 && p_82846_2_ < 40 && !this.moveItemStackTo(itemstack1, 4, 31, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 4, 40, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_82846_1_, itemstack1);
      }

      return itemstack;
   }

   public void removed(PlayerEntity p_75134_1_) {
      super.removed(p_75134_1_);
      this.access.execute((p_217028_2_, p_217028_3_) -> {
         this.clearContainer(p_75134_1_, p_75134_1_.level, this.inputContainer);
      });
   }

   private void setupResultSlot() {
      if (this.selectedBannerPatternIndex.get() > 0) {
         ItemStack itemstack = this.bannerSlot.getItem();
         ItemStack itemstack1 = this.dyeSlot.getItem();
         ItemStack itemstack2 = ItemStack.EMPTY;
         if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            itemstack2 = itemstack.copy();
            itemstack2.setCount(1);
            BannerPattern bannerpattern = BannerPattern.values()[this.selectedBannerPatternIndex.get()];
            DyeColor dyecolor = ((DyeItem)itemstack1.getItem()).getDyeColor();
            CompoundNBT compoundnbt = itemstack2.getOrCreateTagElement("BlockEntityTag");
            ListNBT listnbt;
            if (compoundnbt.contains("Patterns", 9)) {
               listnbt = compoundnbt.getList("Patterns", 10);
            } else {
               listnbt = new ListNBT();
               compoundnbt.put("Patterns", listnbt);
            }

            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.putString("Pattern", bannerpattern.getHashname());
            compoundnbt1.putInt("Color", dyecolor.getId());
            listnbt.add(compoundnbt1);
         }

         if (!ItemStack.matches(itemstack2, this.resultSlot.getItem())) {
            this.resultSlot.set(itemstack2);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Slot getBannerSlot() {
      return this.bannerSlot;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot getDyeSlot() {
      return this.dyeSlot;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot getPatternSlot() {
      return this.patternSlot;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot getResultSlot() {
      return this.resultSlot;
   }
}
