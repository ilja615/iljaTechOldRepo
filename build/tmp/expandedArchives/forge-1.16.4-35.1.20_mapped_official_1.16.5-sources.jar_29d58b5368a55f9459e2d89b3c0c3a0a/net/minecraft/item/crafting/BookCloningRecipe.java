package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BookCloningRecipe extends SpecialRecipe {
   public BookCloningRecipe(ResourceLocation p_i48170_1_) {
      super(p_i48170_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_77569_1_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_77569_1_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                  return false;
               }

               ++i;
            }
         }
      }

      return !itemstack.isEmpty() && itemstack.hasTag() && i > 0;
   }

   public ItemStack assemble(CraftingInventory p_77572_1_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_77572_1_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_77572_1_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && itemstack.hasTag() && i >= 1 && WrittenBookItem.getGeneration(itemstack) < 2) {
         ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
         CompoundNBT compoundnbt = itemstack.getTag().copy();
         compoundnbt.putInt("generation", WrittenBookItem.getGeneration(itemstack) + 1);
         itemstack2.setTag(compoundnbt);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInventory p_179532_1_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_179532_1_.getContainerSize(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = p_179532_1_.getItem(i);
         if (itemstack.hasContainerItem()) {
            nonnulllist.set(i, itemstack.getContainerItem());
         } else if (itemstack.getItem() instanceof WrittenBookItem) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            nonnulllist.set(i, itemstack1);
            break;
         }
      }

      return nonnulllist;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.BOOK_CLONING;
   }

   public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= 3 && p_194133_2_ >= 3;
   }
}
