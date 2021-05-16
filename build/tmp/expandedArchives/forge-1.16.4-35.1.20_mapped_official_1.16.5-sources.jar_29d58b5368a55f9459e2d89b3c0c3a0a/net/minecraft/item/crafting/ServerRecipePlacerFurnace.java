package net.minecraft.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ServerRecipePlacerFurnace<C extends IInventory> extends ServerRecipePlacer<C> {
   private boolean recipeMatchesPlaced;

   public ServerRecipePlacerFurnace(RecipeBookContainer<C> p_i50751_1_) {
      super(p_i50751_1_);
   }

   protected void handleRecipeClicked(IRecipe<C> p_201508_1_, boolean p_201508_2_) {
      this.recipeMatchesPlaced = this.menu.recipeMatches(p_201508_1_);
      int i = this.stackedContents.getBiggestCraftableStack(p_201508_1_, (IntList)null);
      if (this.recipeMatchesPlaced) {
         ItemStack itemstack = this.menu.getSlot(0).getItem();
         if (itemstack.isEmpty() || i <= itemstack.getCount()) {
            return;
         }
      }

      int j = this.getStackSize(p_201508_2_, i, this.recipeMatchesPlaced);
      IntList intlist = new IntArrayList();
      if (this.stackedContents.canCraft(p_201508_1_, intlist, j)) {
         if (!this.recipeMatchesPlaced) {
            this.moveItemToInventory(this.menu.getResultSlotIndex());
            this.moveItemToInventory(0);
         }

         this.placeRecipe(j, intlist);
      }
   }

   protected void clearGrid() {
      this.moveItemToInventory(this.menu.getResultSlotIndex());
      super.clearGrid();
   }

   protected void placeRecipe(int p_201516_1_, IntList p_201516_2_) {
      Iterator<Integer> iterator = p_201516_2_.iterator();
      Slot slot = this.menu.getSlot(0);
      ItemStack itemstack = RecipeItemHelper.fromStackingIndex(iterator.next());
      if (!itemstack.isEmpty()) {
         int i = Math.min(itemstack.getMaxStackSize(), p_201516_1_);
         if (this.recipeMatchesPlaced) {
            i -= slot.getItem().getCount();
         }

         for(int j = 0; j < i; ++j) {
            this.moveItemToGrid(slot, itemstack);
         }

      }
   }
}
