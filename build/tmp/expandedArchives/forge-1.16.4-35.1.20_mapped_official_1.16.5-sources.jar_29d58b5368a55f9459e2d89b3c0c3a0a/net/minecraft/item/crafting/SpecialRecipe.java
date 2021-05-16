package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class SpecialRecipe implements ICraftingRecipe {
   private final ResourceLocation id;

   public SpecialRecipe(ResourceLocation p_i48169_1_) {
      this.id = p_i48169_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean isSpecial() {
      return true;
   }

   public ItemStack getResultItem() {
      return ItemStack.EMPTY;
   }
}
