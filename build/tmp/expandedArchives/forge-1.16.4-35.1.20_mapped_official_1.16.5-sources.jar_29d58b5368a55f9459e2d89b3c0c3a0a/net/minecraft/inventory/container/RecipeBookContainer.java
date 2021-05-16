package net.minecraft.inventory.container;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ServerRecipePlacer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class RecipeBookContainer<C extends IInventory> extends Container {
   public RecipeBookContainer(ContainerType<?> p_i50067_1_, int p_i50067_2_) {
      super(p_i50067_1_, p_i50067_2_);
   }

   public void handlePlacement(boolean p_217056_1_, IRecipe<?> p_217056_2_, ServerPlayerEntity p_217056_3_) {
      (new ServerRecipePlacer<>(this)).recipeClicked(p_217056_3_, (IRecipe<C>)p_217056_2_, p_217056_1_);
   }

   public abstract void fillCraftSlotsStackedContents(RecipeItemHelper p_201771_1_);

   public abstract void clearCraftingContent();

   public abstract boolean recipeMatches(IRecipe<? super C> p_201769_1_);

   public abstract int getResultSlotIndex();

   public abstract int getGridWidth();

   public abstract int getGridHeight();

   @OnlyIn(Dist.CLIENT)
   public abstract int getSize();

   public java.util.List<net.minecraft.client.util.RecipeBookCategories> getRecipeBookCategories() {
      return net.minecraft.client.util.RecipeBookCategories.getCategories(this.getRecipeBookType());
   }

   @OnlyIn(Dist.CLIENT)
   public abstract RecipeBookCategory getRecipeBookType();
}
