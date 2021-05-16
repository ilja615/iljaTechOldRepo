package net.minecraft.inventory;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public class ItemStackHelper {
   public static ItemStack removeItem(List<ItemStack> p_188382_0_, int p_188382_1_, int p_188382_2_) {
      return p_188382_1_ >= 0 && p_188382_1_ < p_188382_0_.size() && !p_188382_0_.get(p_188382_1_).isEmpty() && p_188382_2_ > 0 ? p_188382_0_.get(p_188382_1_).split(p_188382_2_) : ItemStack.EMPTY;
   }

   public static ItemStack takeItem(List<ItemStack> p_188383_0_, int p_188383_1_) {
      return p_188383_1_ >= 0 && p_188383_1_ < p_188383_0_.size() ? p_188383_0_.set(p_188383_1_, ItemStack.EMPTY) : ItemStack.EMPTY;
   }

   public static CompoundNBT saveAllItems(CompoundNBT p_191282_0_, NonNullList<ItemStack> p_191282_1_) {
      return saveAllItems(p_191282_0_, p_191282_1_, true);
   }

   public static CompoundNBT saveAllItems(CompoundNBT p_191281_0_, NonNullList<ItemStack> p_191281_1_, boolean p_191281_2_) {
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < p_191281_1_.size(); ++i) {
         ItemStack itemstack = p_191281_1_.get(i);
         if (!itemstack.isEmpty()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Slot", (byte)i);
            itemstack.save(compoundnbt);
            listnbt.add(compoundnbt);
         }
      }

      if (!listnbt.isEmpty() || p_191281_2_) {
         p_191281_0_.put("Items", listnbt);
      }

      return p_191281_0_;
   }

   public static void loadAllItems(CompoundNBT p_191283_0_, NonNullList<ItemStack> p_191283_1_) {
      ListNBT listnbt = p_191283_0_.getList("Items", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         int j = compoundnbt.getByte("Slot") & 255;
         if (j >= 0 && j < p_191283_1_.size()) {
            p_191283_1_.set(j, ItemStack.of(compoundnbt));
         }
      }

   }

   public static int clearOrCountMatchingItems(IInventory p_233534_0_, Predicate<ItemStack> p_233534_1_, int p_233534_2_, boolean p_233534_3_) {
      int i = 0;

      for(int j = 0; j < p_233534_0_.getContainerSize(); ++j) {
         ItemStack itemstack = p_233534_0_.getItem(j);
         int k = clearOrCountMatchingItems(itemstack, p_233534_1_, p_233534_2_ - i, p_233534_3_);
         if (k > 0 && !p_233534_3_ && itemstack.isEmpty()) {
            p_233534_0_.setItem(j, ItemStack.EMPTY);
         }

         i += k;
      }

      return i;
   }

   public static int clearOrCountMatchingItems(ItemStack p_233535_0_, Predicate<ItemStack> p_233535_1_, int p_233535_2_, boolean p_233535_3_) {
      if (!p_233535_0_.isEmpty() && p_233535_1_.test(p_233535_0_)) {
         if (p_233535_3_) {
            return p_233535_0_.getCount();
         } else {
            int i = p_233535_2_ < 0 ? p_233535_0_.getCount() : Math.min(p_233535_2_, p_233535_0_.getCount());
            p_233535_0_.shrink(i);
            return i;
         }
      } else {
         return 0;
      }
   }
}
