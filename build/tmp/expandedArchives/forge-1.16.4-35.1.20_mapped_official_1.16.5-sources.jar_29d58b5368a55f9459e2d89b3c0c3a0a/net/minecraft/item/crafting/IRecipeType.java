package net.minecraft.item.crafting;

import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public interface IRecipeType<T extends IRecipe<?>> {
   IRecipeType<ICraftingRecipe> CRAFTING = register("crafting");
   IRecipeType<FurnaceRecipe> SMELTING = register("smelting");
   IRecipeType<BlastingRecipe> BLASTING = register("blasting");
   IRecipeType<SmokingRecipe> SMOKING = register("smoking");
   IRecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
   IRecipeType<StonecuttingRecipe> STONECUTTING = register("stonecutting");
   IRecipeType<SmithingRecipe> SMITHING = register("smithing");

   static <T extends IRecipe<?>> IRecipeType<T> register(final String p_222147_0_) {
      return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(p_222147_0_), new IRecipeType<T>() {
         public String toString() {
            return p_222147_0_;
         }
      });
   }

   default <C extends IInventory> Optional<T> tryMatch(IRecipe<C> p_222148_1_, World p_222148_2_, C p_222148_3_) {
      return p_222148_1_.matches(p_222148_3_, p_222148_2_) ? Optional.of((T)p_222148_1_) : Optional.empty();
   }
}
