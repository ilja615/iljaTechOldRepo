package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class AbstractCookingRecipe implements IRecipe<IInventory> {
   protected final IRecipeType<?> type;
   protected final ResourceLocation id;
   protected final String group;
   protected final Ingredient ingredient;
   protected final ItemStack result;
   protected final float experience;
   protected final int cookingTime;

   public AbstractCookingRecipe(IRecipeType<?> p_i50032_1_, ResourceLocation p_i50032_2_, String p_i50032_3_, Ingredient p_i50032_4_, ItemStack p_i50032_5_, float p_i50032_6_, int p_i50032_7_) {
      this.type = p_i50032_1_;
      this.id = p_i50032_2_;
      this.group = p_i50032_3_;
      this.ingredient = p_i50032_4_;
      this.result = p_i50032_5_;
      this.experience = p_i50032_6_;
      this.cookingTime = p_i50032_7_;
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      return this.ingredient.test(p_77569_1_.getItem(0));
   }

   public ItemStack assemble(IInventory p_77572_1_) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
      return true;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.ingredient);
      return nonnulllist;
   }

   public float getExperience() {
      return this.experience;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public String getGroup() {
      return this.group;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeType<?> getType() {
      return this.type;
   }
}
