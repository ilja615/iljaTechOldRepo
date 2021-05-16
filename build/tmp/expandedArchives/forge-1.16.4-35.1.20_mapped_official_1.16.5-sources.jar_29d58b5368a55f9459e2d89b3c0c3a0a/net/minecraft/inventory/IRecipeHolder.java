package net.minecraft.inventory;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface IRecipeHolder {
   void setRecipeUsed(@Nullable IRecipe<?> p_193056_1_);

   @Nullable
   IRecipe<?> getRecipeUsed();

   default void awardUsedRecipes(PlayerEntity p_201560_1_) {
      IRecipe<?> irecipe = this.getRecipeUsed();
      if (irecipe != null && !irecipe.isSpecial()) {
         p_201560_1_.awardRecipes(Collections.singleton(irecipe));
         this.setRecipeUsed((IRecipe<?>)null);
      }

   }

   default boolean setRecipeUsed(World p_201561_1_, ServerPlayerEntity p_201561_2_, IRecipe<?> p_201561_3_) {
      if (!p_201561_3_.isSpecial() && p_201561_1_.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !p_201561_2_.getRecipeBook().contains(p_201561_3_)) {
         return false;
      } else {
         this.setRecipeUsed(p_201561_3_);
         return true;
      }
   }
}
