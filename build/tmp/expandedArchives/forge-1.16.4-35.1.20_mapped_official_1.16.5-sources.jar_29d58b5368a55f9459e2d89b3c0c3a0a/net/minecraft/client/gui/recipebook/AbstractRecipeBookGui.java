package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractRecipeBookGui extends RecipeBookGui {
   private Iterator<Item> iterator;
   private Set<Item> fuels;
   private Slot fuelSlot;
   private Item fuel;
   private float time;

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
   }

   public void slotClicked(@Nullable Slot p_191874_1_) {
      super.slotClicked(p_191874_1_);
      if (p_191874_1_ != null && p_191874_1_.index < this.menu.getSize()) {
         this.fuelSlot = null;
      }

   }

   public void setupGhostRecipe(IRecipe<?> p_193951_1_, List<Slot> p_193951_2_) {
      ItemStack itemstack = p_193951_1_.getResultItem();
      this.ghostRecipe.setRecipe(p_193951_1_);
      this.ghostRecipe.addIngredient(Ingredient.of(itemstack), (p_193951_2_.get(2)).x, (p_193951_2_.get(2)).y);
      NonNullList<Ingredient> nonnulllist = p_193951_1_.getIngredients();
      this.fuelSlot = p_193951_2_.get(1);
      if (this.fuels == null) {
         this.fuels = this.getFuelItems();
      }

      this.iterator = this.fuels.iterator();
      this.fuel = null;
      Iterator<Ingredient> iterator = nonnulllist.iterator();

      for(int i = 0; i < 2; ++i) {
         if (!iterator.hasNext()) {
            return;
         }

         Ingredient ingredient = iterator.next();
         if (!ingredient.isEmpty()) {
            Slot slot = p_193951_2_.get(i);
            this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
         }
      }

   }

   protected abstract Set<Item> getFuelItems();

   public void renderGhostRecipe(MatrixStack p_230477_1_, int p_230477_2_, int p_230477_3_, boolean p_230477_4_, float p_230477_5_) {
      super.renderGhostRecipe(p_230477_1_, p_230477_2_, p_230477_3_, p_230477_4_, p_230477_5_);
      if (this.fuelSlot != null) {
         if (!Screen.hasControlDown()) {
            this.time += p_230477_5_;
         }

         int i = this.fuelSlot.x + p_230477_2_;
         int j = this.fuelSlot.y + p_230477_3_;
         AbstractGui.fill(p_230477_1_, i, j, i + 16, j + 16, 822018048);
         this.minecraft.getItemRenderer().renderAndDecorateItem(this.minecraft.player, this.getFuel().getDefaultInstance(), i, j);
         RenderSystem.depthFunc(516);
         AbstractGui.fill(p_230477_1_, i, j, i + 16, j + 16, 822083583);
         RenderSystem.depthFunc(515);
      }
   }

   private Item getFuel() {
      if (this.fuel == null || this.time > 30.0F) {
         this.time = 0.0F;
         if (this.iterator == null || !this.iterator.hasNext()) {
            if (this.fuels == null) {
               this.fuels = this.getFuelItems();
            }

            this.iterator = this.fuels.iterator();
         }

         this.fuel = this.iterator.next();
      }

      return this.fuel;
   }
}
