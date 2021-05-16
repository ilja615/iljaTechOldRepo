package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

public class CraftResultInventory implements IInventory, IRecipeHolder {
   private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
   @Nullable
   private IRecipe<?> recipeUsed;

   public int getContainerSize() {
      return 1;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.itemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int p_70301_1_) {
      return this.itemStacks.get(0);
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.takeItem(this.itemStacks, 0);
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      return ItemStackHelper.takeItem(this.itemStacks, 0);
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      this.itemStacks.set(0, p_70299_2_);
   }

   public void setChanged() {
   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      return true;
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe<?> p_193056_1_) {
      this.recipeUsed = p_193056_1_;
   }

   @Nullable
   public IRecipe<?> getRecipeUsed() {
      return this.recipeUsed;
   }
}
