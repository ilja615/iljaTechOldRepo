package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerInventory implements IInventory, INameable {
   public final NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
   public final NonNullList<ItemStack> armor = NonNullList.withSize(4, ItemStack.EMPTY);
   public final NonNullList<ItemStack> offhand = NonNullList.withSize(1, ItemStack.EMPTY);
   private final List<NonNullList<ItemStack>> compartments = ImmutableList.of(this.items, this.armor, this.offhand);
   public int selected;
   public final PlayerEntity player;
   private ItemStack carried = ItemStack.EMPTY;
   private int timesChanged;

   public PlayerInventory(PlayerEntity p_i1750_1_) {
      this.player = p_i1750_1_;
   }

   public ItemStack getSelected() {
      return isHotbarSlot(this.selected) ? this.items.get(this.selected) : ItemStack.EMPTY;
   }

   public static int getSelectionSize() {
      return 9;
   }

   private boolean hasRemainingSpaceForItem(ItemStack p_184436_1_, ItemStack p_184436_2_) {
      return !p_184436_1_.isEmpty() && this.isSameItem(p_184436_1_, p_184436_2_) && p_184436_1_.isStackable() && p_184436_1_.getCount() < p_184436_1_.getMaxStackSize() && p_184436_1_.getCount() < this.getMaxStackSize();
   }

   private boolean isSameItem(ItemStack p_184431_1_, ItemStack p_184431_2_) {
      return p_184431_1_.getItem() == p_184431_2_.getItem() && ItemStack.tagMatches(p_184431_1_, p_184431_2_);
   }

   public int getFreeSlot() {
      for(int i = 0; i < this.items.size(); ++i) {
         if (this.items.get(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPickedItem(ItemStack p_184434_1_) {
      int i = this.findSlotMatchingItem(p_184434_1_);
      if (isHotbarSlot(i)) {
         this.selected = i;
      } else {
         if (i == -1) {
            this.selected = this.getSuitableHotbarSlot();
            if (!this.items.get(this.selected).isEmpty()) {
               int j = this.getFreeSlot();
               if (j != -1) {
                  this.items.set(j, this.items.get(this.selected));
               }
            }

            this.items.set(this.selected, p_184434_1_);
         } else {
            this.pickSlot(i);
         }

      }
   }

   public void pickSlot(int p_184430_1_) {
      this.selected = this.getSuitableHotbarSlot();
      ItemStack itemstack = this.items.get(this.selected);
      this.items.set(this.selected, this.items.get(p_184430_1_));
      this.items.set(p_184430_1_, itemstack);
   }

   public static boolean isHotbarSlot(int p_184435_0_) {
      return p_184435_0_ >= 0 && p_184435_0_ < 9;
   }

   @OnlyIn(Dist.CLIENT)
   public int findSlotMatchingItem(ItemStack p_184429_1_) {
      for(int i = 0; i < this.items.size(); ++i) {
         if (!this.items.get(i).isEmpty() && this.isSameItem(p_184429_1_, this.items.get(i))) {
            return i;
         }
      }

      return -1;
   }

   public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
      for(int i = 0; i < this.items.size(); ++i) {
         ItemStack itemstack = this.items.get(i);
         if (!this.items.get(i).isEmpty() && this.isSameItem(p_194014_1_, this.items.get(i)) && !this.items.get(i).isDamaged() && !itemstack.isEnchanted() && !itemstack.hasCustomHoverName()) {
            return i;
         }
      }

      return -1;
   }

   public int getSuitableHotbarSlot() {
      for(int i = 0; i < 9; ++i) {
         int j = (this.selected + i) % 9;
         if (this.items.get(j).isEmpty()) {
            return j;
         }
      }

      for(int k = 0; k < 9; ++k) {
         int l = (this.selected + k) % 9;
         if (!this.items.get(l).isEnchanted()) {
            return l;
         }
      }

      return this.selected;
   }

   @OnlyIn(Dist.CLIENT)
   public void swapPaint(double p_195409_1_) {
      if (p_195409_1_ > 0.0D) {
         p_195409_1_ = 1.0D;
      }

      if (p_195409_1_ < 0.0D) {
         p_195409_1_ = -1.0D;
      }

      for(this.selected = (int)((double)this.selected - p_195409_1_); this.selected < 0; this.selected += 9) {
      }

      while(this.selected >= 9) {
         this.selected -= 9;
      }

   }

   public int clearOrCountMatchingItems(Predicate<ItemStack> p_234564_1_, int p_234564_2_, IInventory p_234564_3_) {
      int i = 0;
      boolean flag = p_234564_2_ == 0;
      i = i + ItemStackHelper.clearOrCountMatchingItems(this, p_234564_1_, p_234564_2_ - i, flag);
      i = i + ItemStackHelper.clearOrCountMatchingItems(p_234564_3_, p_234564_1_, p_234564_2_ - i, flag);
      i = i + ItemStackHelper.clearOrCountMatchingItems(this.carried, p_234564_1_, p_234564_2_ - i, flag);
      if (this.carried.isEmpty()) {
         this.carried = ItemStack.EMPTY;
      }

      return i;
   }

   private int addResource(ItemStack p_70452_1_) {
      int i = this.getSlotWithRemainingSpace(p_70452_1_);
      if (i == -1) {
         i = this.getFreeSlot();
      }

      return i == -1 ? p_70452_1_.getCount() : this.addResource(i, p_70452_1_);
   }

   private int addResource(int p_191973_1_, ItemStack p_191973_2_) {
      Item item = p_191973_2_.getItem();
      int i = p_191973_2_.getCount();
      ItemStack itemstack = this.getItem(p_191973_1_);
      if (itemstack.isEmpty()) {
         itemstack = p_191973_2_.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
         itemstack.setCount(0);
         if (p_191973_2_.hasTag()) {
            itemstack.setTag(p_191973_2_.getTag().copy());
         }

         this.setItem(p_191973_1_, itemstack);
      }

      int j = i;
      if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
         j = itemstack.getMaxStackSize() - itemstack.getCount();
      }

      if (j > this.getMaxStackSize() - itemstack.getCount()) {
         j = this.getMaxStackSize() - itemstack.getCount();
      }

      if (j == 0) {
         return i;
      } else {
         i = i - j;
         itemstack.grow(j);
         itemstack.setPopTime(5);
         return i;
      }
   }

   public int getSlotWithRemainingSpace(ItemStack p_70432_1_) {
      if (this.hasRemainingSpaceForItem(this.getItem(this.selected), p_70432_1_)) {
         return this.selected;
      } else if (this.hasRemainingSpaceForItem(this.getItem(40), p_70432_1_)) {
         return 40;
      } else {
         for(int i = 0; i < this.items.size(); ++i) {
            if (this.hasRemainingSpaceForItem(this.items.get(i), p_70432_1_)) {
               return i;
            }
         }

         return -1;
      }
   }

   public void tick() {
      for(NonNullList<ItemStack> nonnulllist : this.compartments) {
         for(int i = 0; i < nonnulllist.size(); ++i) {
            if (!nonnulllist.get(i).isEmpty()) {
               nonnulllist.get(i).inventoryTick(this.player.level, this.player, i, this.selected == i);
            }
         }
      }
      armor.forEach(e -> e.onArmorTick(player.level, player));
   }

   public boolean add(ItemStack p_70441_1_) {
      return this.add(-1, p_70441_1_);
   }

   public boolean add(int p_191971_1_, ItemStack p_191971_2_) {
      if (p_191971_2_.isEmpty()) {
         return false;
      } else {
         try {
            if (p_191971_2_.isDamaged()) {
               if (p_191971_1_ == -1) {
                  p_191971_1_ = this.getFreeSlot();
               }

               if (p_191971_1_ >= 0) {
                  this.items.set(p_191971_1_, p_191971_2_.copy());
                  this.items.get(p_191971_1_).setPopTime(5);
                  p_191971_2_.setCount(0);
                  return true;
               } else if (this.player.abilities.instabuild) {
                  p_191971_2_.setCount(0);
                  return true;
               } else {
                  return false;
               }
            } else {
               int i;
               do {
                  i = p_191971_2_.getCount();
                  if (p_191971_1_ == -1) {
                     p_191971_2_.setCount(this.addResource(p_191971_2_));
                  } else {
                     p_191971_2_.setCount(this.addResource(p_191971_1_, p_191971_2_));
                  }
               } while(!p_191971_2_.isEmpty() && p_191971_2_.getCount() < i);

               if (p_191971_2_.getCount() == i && this.player.abilities.instabuild) {
                  p_191971_2_.setCount(0);
                  return true;
               } else {
                  return p_191971_2_.getCount() < i;
               }
            }
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being added");
            crashreportcategory.setDetail("Registry Name", () -> String.valueOf(p_191971_2_.getItem().getRegistryName()));
            crashreportcategory.setDetail("Item Class", () -> p_191971_2_.getItem().getClass().getName());
            crashreportcategory.setDetail("Item ID", Item.getId(p_191971_2_.getItem()));
            crashreportcategory.setDetail("Item data", p_191971_2_.getDamageValue());
            crashreportcategory.setDetail("Item name", () -> {
               return p_191971_2_.getHoverName().getString();
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public void placeItemBackInInventory(World p_191975_1_, ItemStack p_191975_2_) {
      if (!p_191975_1_.isClientSide) {
         while(!p_191975_2_.isEmpty()) {
            int i = this.getSlotWithRemainingSpace(p_191975_2_);
            if (i == -1) {
               i = this.getFreeSlot();
            }

            if (i == -1) {
               this.player.drop(p_191975_2_, false);
               break;
            }

            int j = p_191975_2_.getMaxStackSize() - this.getItem(i).getCount();
            if (this.add(i, p_191975_2_.split(j))) {
               ((ServerPlayerEntity)this.player).connection.send(new SSetSlotPacket(-2, i, this.getItem(i)));
            }
         }

      }
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      List<ItemStack> list = null;

      for(NonNullList<ItemStack> nonnulllist : this.compartments) {
         if (p_70298_1_ < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }

         p_70298_1_ -= nonnulllist.size();
      }

      return list != null && !list.get(p_70298_1_).isEmpty() ? ItemStackHelper.removeItem(list, p_70298_1_, p_70298_2_) : ItemStack.EMPTY;
   }

   public void removeItem(ItemStack p_184437_1_) {
      for(NonNullList<ItemStack> nonnulllist : this.compartments) {
         for(int i = 0; i < nonnulllist.size(); ++i) {
            if (nonnulllist.get(i) == p_184437_1_) {
               nonnulllist.set(i, ItemStack.EMPTY);
               break;
            }
         }
      }

   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      NonNullList<ItemStack> nonnulllist = null;

      for(NonNullList<ItemStack> nonnulllist1 : this.compartments) {
         if (p_70304_1_ < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }

         p_70304_1_ -= nonnulllist1.size();
      }

      if (nonnulllist != null && !nonnulllist.get(p_70304_1_).isEmpty()) {
         ItemStack itemstack = nonnulllist.get(p_70304_1_);
         nonnulllist.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      NonNullList<ItemStack> nonnulllist = null;

      for(NonNullList<ItemStack> nonnulllist1 : this.compartments) {
         if (p_70299_1_ < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }

         p_70299_1_ -= nonnulllist1.size();
      }

      if (nonnulllist != null) {
         nonnulllist.set(p_70299_1_, p_70299_2_);
      }

   }

   public float getDestroySpeed(BlockState p_184438_1_) {
      return this.items.get(this.selected).getDestroySpeed(p_184438_1_);
   }

   public ListNBT save(ListNBT p_70442_1_) {
      for(int i = 0; i < this.items.size(); ++i) {
         if (!this.items.get(i).isEmpty()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Slot", (byte)i);
            this.items.get(i).save(compoundnbt);
            p_70442_1_.add(compoundnbt);
         }
      }

      for(int j = 0; j < this.armor.size(); ++j) {
         if (!this.armor.get(j).isEmpty()) {
            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.putByte("Slot", (byte)(j + 100));
            this.armor.get(j).save(compoundnbt1);
            p_70442_1_.add(compoundnbt1);
         }
      }

      for(int k = 0; k < this.offhand.size(); ++k) {
         if (!this.offhand.get(k).isEmpty()) {
            CompoundNBT compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Slot", (byte)(k + 150));
            this.offhand.get(k).save(compoundnbt2);
            p_70442_1_.add(compoundnbt2);
         }
      }

      return p_70442_1_;
   }

   public void load(ListNBT p_70443_1_) {
      this.items.clear();
      this.armor.clear();
      this.offhand.clear();

      for(int i = 0; i < p_70443_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_70443_1_.getCompound(i);
         int j = compoundnbt.getByte("Slot") & 255;
         ItemStack itemstack = ItemStack.of(compoundnbt);
         if (!itemstack.isEmpty()) {
            if (j >= 0 && j < this.items.size()) {
               this.items.set(j, itemstack);
            } else if (j >= 100 && j < this.armor.size() + 100) {
               this.armor.set(j - 100, itemstack);
            } else if (j >= 150 && j < this.offhand.size() + 150) {
               this.offhand.set(j - 150, itemstack);
            }
         }
      }

   }

   public int getContainerSize() {
      return this.items.size() + this.armor.size() + this.offhand.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      for(ItemStack itemstack1 : this.armor) {
         if (!itemstack1.isEmpty()) {
            return false;
         }
      }

      for(ItemStack itemstack2 : this.offhand) {
         if (!itemstack2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int p_70301_1_) {
      List<ItemStack> list = null;

      for(NonNullList<ItemStack> nonnulllist : this.compartments) {
         if (p_70301_1_ < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }

         p_70301_1_ -= nonnulllist.size();
      }

      return list == null ? ItemStack.EMPTY : list.get(p_70301_1_);
   }

   public ITextComponent getName() {
      return new TranslationTextComponent("container.inventory");
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getArmor(int p_70440_1_) {
      return this.armor.get(p_70440_1_);
   }

   public void hurtArmor(DamageSource p_234563_1_, float p_234563_2_) {
      if (!(p_234563_2_ <= 0.0F)) {
         p_234563_2_ = p_234563_2_ / 4.0F;
         if (p_234563_2_ < 1.0F) {
            p_234563_2_ = 1.0F;
         }

         for(int i = 0; i < this.armor.size(); ++i) {
            ItemStack itemstack = this.armor.get(i);
            if ((!p_234563_1_.isFire() || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ArmorItem) {
               int j = i;
               itemstack.hurtAndBreak((int)p_234563_2_, this.player, (p_214023_1_) -> {
                  p_214023_1_.broadcastBreakEvent(EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.ARMOR, j));
               });
            }
         }

      }
   }

   public void dropAll() {
      for(List<ItemStack> list : this.compartments) {
         for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
               this.player.drop(itemstack, true, false);
               list.set(i, ItemStack.EMPTY);
            }
         }
      }

   }

   public void setChanged() {
      ++this.timesChanged;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTimesChanged() {
      return this.timesChanged;
   }

   public void setCarried(ItemStack p_70437_1_) {
      this.carried = p_70437_1_;
   }

   public ItemStack getCarried() {
      return this.carried;
   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      if (this.player.removed) {
         return false;
      } else {
         return !(p_70300_1_.distanceToSqr(this.player) > 64.0D);
      }
   }

   public boolean contains(ItemStack p_70431_1_) {
      for(List<ItemStack> list : this.compartments) {
         for(ItemStack itemstack : list) {
            if (!itemstack.isEmpty() && itemstack.sameItem(p_70431_1_)) {
               return true;
            }
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean contains(ITag<Item> p_199712_1_) {
      for(List<ItemStack> list : this.compartments) {
         for(ItemStack itemstack : list) {
            if (!itemstack.isEmpty() && p_199712_1_.contains(itemstack.getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   public void replaceWith(PlayerInventory p_70455_1_) {
      for(int i = 0; i < this.getContainerSize(); ++i) {
         this.setItem(i, p_70455_1_.getItem(i));
      }

      this.selected = p_70455_1_.selected;
   }

   public void clearContent() {
      for(List<ItemStack> list : this.compartments) {
         list.clear();
      }

   }

   public void fillStackedContents(RecipeItemHelper p_201571_1_) {
      for(ItemStack itemstack : this.items) {
         p_201571_1_.accountSimpleStack(itemstack);
      }

   }
}
