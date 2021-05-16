package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MapCloningRecipe extends SpecialRecipe {
   public MapCloningRecipe(ResourceLocation p_i48165_1_) {
      super(p_i48165_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_77569_1_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_77569_1_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.FILLED_MAP) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.MAP) {
                  return false;
               }

               ++i;
            }
         }
      }

      return !itemstack.isEmpty() && i > 0;
   }

   public ItemStack assemble(CraftingInventory p_77572_1_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_77572_1_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_77572_1_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.FILLED_MAP) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.MAP) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && i >= 1) {
         ItemStack itemstack2 = itemstack.copy();
         itemstack2.setCount(i + 1);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= 3 && p_194133_2_ >= 3;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.MAP_CLONING;
   }
}
