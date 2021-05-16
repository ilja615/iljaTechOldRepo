package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FurnaceRecipe extends AbstractCookingRecipe {
   public FurnaceRecipe(ResourceLocation p_i48715_1_, String p_i48715_2_, Ingredient p_i48715_3_, ItemStack p_i48715_4_, float p_i48715_5_, int p_i48715_6_) {
      super(IRecipeType.SMELTING, p_i48715_1_, p_i48715_2_, p_i48715_3_, p_i48715_4_, p_i48715_5_, p_i48715_6_);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.FURNACE);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.SMELTING_RECIPE;
   }
}
