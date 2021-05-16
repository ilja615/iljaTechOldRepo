package net.minecraft.client.gui.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeList {
   private final List<IRecipe<?>> recipes;
   private final boolean singleResultItem;
   private final Set<IRecipe<?>> craftable = Sets.newHashSet();
   private final Set<IRecipe<?>> fitsDimensions = Sets.newHashSet();
   private final Set<IRecipe<?>> known = Sets.newHashSet();

   public RecipeList(List<IRecipe<?>> p_i242062_1_) {
      this.recipes = ImmutableList.copyOf(p_i242062_1_);
      if (p_i242062_1_.size() <= 1) {
         this.singleResultItem = true;
      } else {
         this.singleResultItem = allRecipesHaveSameResult(p_i242062_1_);
      }

   }

   private static boolean allRecipesHaveSameResult(List<IRecipe<?>> p_243413_0_) {
      int i = p_243413_0_.size();
      ItemStack itemstack = p_243413_0_.get(0).getResultItem();

      for(int j = 1; j < i; ++j) {
         ItemStack itemstack1 = p_243413_0_.get(j).getResultItem();
         if (!ItemStack.isSame(itemstack, itemstack1) || !ItemStack.tagMatches(itemstack, itemstack1)) {
            return false;
         }
      }

      return true;
   }

   public boolean hasKnownRecipes() {
      return !this.known.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook p_194214_1_) {
      for(IRecipe<?> irecipe : this.recipes) {
         if (p_194214_1_.contains(irecipe)) {
            this.known.add(irecipe);
         }
      }

   }

   public void canCraft(RecipeItemHelper p_194210_1_, int p_194210_2_, int p_194210_3_, RecipeBook p_194210_4_) {
      for(IRecipe<?> irecipe : this.recipes) {
         boolean flag = irecipe.canCraftInDimensions(p_194210_2_, p_194210_3_) && p_194210_4_.contains(irecipe);
         if (flag) {
            this.fitsDimensions.add(irecipe);
         } else {
            this.fitsDimensions.remove(irecipe);
         }

         if (flag && p_194210_1_.canCraft(irecipe, (IntList)null)) {
            this.craftable.add(irecipe);
         } else {
            this.craftable.remove(irecipe);
         }
      }

   }

   public boolean isCraftable(IRecipe<?> p_194213_1_) {
      return this.craftable.contains(p_194213_1_);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasFitting() {
      return !this.fitsDimensions.isEmpty();
   }

   public List<IRecipe<?>> getRecipes() {
      return this.recipes;
   }

   public List<IRecipe<?>> getRecipes(boolean p_194208_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();
      Set<IRecipe<?>> set = p_194208_1_ ? this.craftable : this.fitsDimensions;

      for(IRecipe<?> irecipe : this.recipes) {
         if (set.contains(irecipe)) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public List<IRecipe<?>> getDisplayRecipes(boolean p_194207_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(IRecipe<?> irecipe : this.recipes) {
         if (this.fitsDimensions.contains(irecipe) && this.craftable.contains(irecipe) == p_194207_1_) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}
