package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeOverlayGui extends AbstractGui implements IRenderable, IGuiEventListener {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private final List<RecipeOverlayGui.RecipeButtonWidget> recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   private Minecraft minecraft;
   private RecipeList collection;
   private IRecipe<?> lastRecipeClicked;
   private float time;
   private boolean isFurnaceMenu;

   public void init(Minecraft p_201703_1_, RecipeList p_201703_2_, int p_201703_3_, int p_201703_4_, int p_201703_5_, int p_201703_6_, float p_201703_7_) {
      this.minecraft = p_201703_1_;
      this.collection = p_201703_2_;
      if (p_201703_1_.player.containerMenu instanceof AbstractFurnaceContainer) {
         this.isFurnaceMenu = true;
      }

      boolean flag = p_201703_1_.player.getRecipeBook().isFiltering((RecipeBookContainer)p_201703_1_.player.containerMenu);
      List<IRecipe<?>> list = p_201703_2_.getDisplayRecipes(true);
      List<IRecipe<?>> list1 = flag ? Collections.emptyList() : p_201703_2_.getDisplayRecipes(false);
      int i = list.size();
      int j = i + list1.size();
      int k = j <= 16 ? 4 : 5;
      int l = (int)Math.ceil((double)((float)j / (float)k));
      this.x = p_201703_3_;
      this.y = p_201703_4_;
      int i1 = 25;
      float f = (float)(this.x + Math.min(j, k) * 25);
      float f1 = (float)(p_201703_5_ + 50);
      if (f > f1) {
         this.x = (int)((float)this.x - p_201703_7_ * (float)((int)((f - f1) / p_201703_7_)));
      }

      float f2 = (float)(this.y + l * 25);
      float f3 = (float)(p_201703_6_ + 50);
      if (f2 > f3) {
         this.y = (int)((float)this.y - p_201703_7_ * (float)MathHelper.ceil((f2 - f3) / p_201703_7_));
      }

      float f4 = (float)this.y;
      float f5 = (float)(p_201703_6_ - 100);
      if (f4 < f5) {
         this.y = (int)((float)this.y - p_201703_7_ * (float)MathHelper.ceil((f4 - f5) / p_201703_7_));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for(int j1 = 0; j1 < j; ++j1) {
         boolean flag1 = j1 < i;
         IRecipe<?> irecipe = flag1 ? list.get(j1) : list1.get(j1 - i);
         int k1 = this.x + 4 + 25 * (j1 % k);
         int l1 = this.y + 5 + 25 * (j1 / k);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new RecipeOverlayGui.FurnaceRecipeButtonWidget(k1, l1, irecipe, flag1));
         } else {
            this.recipeButtons.add(new RecipeOverlayGui.RecipeButtonWidget(k1, l1, irecipe, flag1));
         }
      }

      this.lastRecipeClicked = null;
   }

   public boolean changeFocus(boolean p_231049_1_) {
      return false;
   }

   public RecipeList getRecipeCollection() {
      return this.collection;
   }

   public IRecipe<?> getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (p_231044_5_ != 0) {
         return false;
      } else {
         for(RecipeOverlayGui.RecipeButtonWidget recipeoverlaygui$recipebuttonwidget : this.recipeButtons) {
            if (recipeoverlaygui$recipebuttonwidget.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)) {
               this.lastRecipeClicked = recipeoverlaygui$recipebuttonwidget.recipe;
               return true;
            }
         }

         return false;
      }
   }

   public boolean isMouseOver(double p_231047_1_, double p_231047_3_) {
      return false;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      if (this.isVisible) {
         this.time += p_230430_4_;
         RenderSystem.enableBlend();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 170.0F);
         int i = this.recipeButtons.size() <= 16 ? 4 : 5;
         int j = Math.min(this.recipeButtons.size(), i);
         int k = MathHelper.ceil((float)this.recipeButtons.size() / (float)i);
         int l = 24;
         int i1 = 4;
         int j1 = 82;
         int k1 = 208;
         this.nineInchSprite(p_230430_1_, j, k, 24, 4, 82, 208);
         RenderSystem.disableBlend();

         for(RecipeOverlayGui.RecipeButtonWidget recipeoverlaygui$recipebuttonwidget : this.recipeButtons) {
            recipeoverlaygui$recipebuttonwidget.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         }

         RenderSystem.popMatrix();
      }
   }

   private void nineInchSprite(MatrixStack p_238923_1_, int p_238923_2_, int p_238923_3_, int p_238923_4_, int p_238923_5_, int p_238923_6_, int p_238923_7_) {
      this.blit(p_238923_1_, this.x, this.y, p_238923_6_, p_238923_7_, p_238923_5_, p_238923_5_);
      this.blit(p_238923_1_, this.x + p_238923_5_ * 2 + p_238923_2_ * p_238923_4_, this.y, p_238923_6_ + p_238923_4_ + p_238923_5_, p_238923_7_, p_238923_5_, p_238923_5_);
      this.blit(p_238923_1_, this.x, this.y + p_238923_5_ * 2 + p_238923_3_ * p_238923_4_, p_238923_6_, p_238923_7_ + p_238923_4_ + p_238923_5_, p_238923_5_, p_238923_5_);
      this.blit(p_238923_1_, this.x + p_238923_5_ * 2 + p_238923_2_ * p_238923_4_, this.y + p_238923_5_ * 2 + p_238923_3_ * p_238923_4_, p_238923_6_ + p_238923_4_ + p_238923_5_, p_238923_7_ + p_238923_4_ + p_238923_5_, p_238923_5_, p_238923_5_);

      for(int i = 0; i < p_238923_2_; ++i) {
         this.blit(p_238923_1_, this.x + p_238923_5_ + i * p_238923_4_, this.y, p_238923_6_ + p_238923_5_, p_238923_7_, p_238923_4_, p_238923_5_);
         this.blit(p_238923_1_, this.x + p_238923_5_ + (i + 1) * p_238923_4_, this.y, p_238923_6_ + p_238923_5_, p_238923_7_, p_238923_5_, p_238923_5_);

         for(int j = 0; j < p_238923_3_; ++j) {
            if (i == 0) {
               this.blit(p_238923_1_, this.x, this.y + p_238923_5_ + j * p_238923_4_, p_238923_6_, p_238923_7_ + p_238923_5_, p_238923_5_, p_238923_4_);
               this.blit(p_238923_1_, this.x, this.y + p_238923_5_ + (j + 1) * p_238923_4_, p_238923_6_, p_238923_7_ + p_238923_5_, p_238923_5_, p_238923_5_);
            }

            this.blit(p_238923_1_, this.x + p_238923_5_ + i * p_238923_4_, this.y + p_238923_5_ + j * p_238923_4_, p_238923_6_ + p_238923_5_, p_238923_7_ + p_238923_5_, p_238923_4_, p_238923_4_);
            this.blit(p_238923_1_, this.x + p_238923_5_ + (i + 1) * p_238923_4_, this.y + p_238923_5_ + j * p_238923_4_, p_238923_6_ + p_238923_5_, p_238923_7_ + p_238923_5_, p_238923_5_, p_238923_4_);
            this.blit(p_238923_1_, this.x + p_238923_5_ + i * p_238923_4_, this.y + p_238923_5_ + (j + 1) * p_238923_4_, p_238923_6_ + p_238923_5_, p_238923_7_ + p_238923_5_, p_238923_4_, p_238923_5_);
            this.blit(p_238923_1_, this.x + p_238923_5_ + (i + 1) * p_238923_4_ - 1, this.y + p_238923_5_ + (j + 1) * p_238923_4_ - 1, p_238923_6_ + p_238923_5_, p_238923_7_ + p_238923_5_, p_238923_5_ + 1, p_238923_5_ + 1);
            if (i == p_238923_2_ - 1) {
               this.blit(p_238923_1_, this.x + p_238923_5_ * 2 + p_238923_2_ * p_238923_4_, this.y + p_238923_5_ + j * p_238923_4_, p_238923_6_ + p_238923_4_ + p_238923_5_, p_238923_7_ + p_238923_5_, p_238923_5_, p_238923_4_);
               this.blit(p_238923_1_, this.x + p_238923_5_ * 2 + p_238923_2_ * p_238923_4_, this.y + p_238923_5_ + (j + 1) * p_238923_4_, p_238923_6_ + p_238923_4_ + p_238923_5_, p_238923_7_ + p_238923_5_, p_238923_5_, p_238923_5_);
            }
         }

         this.blit(p_238923_1_, this.x + p_238923_5_ + i * p_238923_4_, this.y + p_238923_5_ * 2 + p_238923_3_ * p_238923_4_, p_238923_6_ + p_238923_5_, p_238923_7_ + p_238923_4_ + p_238923_5_, p_238923_4_, p_238923_5_);
         this.blit(p_238923_1_, this.x + p_238923_5_ + (i + 1) * p_238923_4_, this.y + p_238923_5_ * 2 + p_238923_3_ * p_238923_4_, p_238923_6_ + p_238923_5_, p_238923_7_ + p_238923_4_ + p_238923_5_, p_238923_5_, p_238923_5_);
      }

   }

   public void setVisible(boolean p_192999_1_) {
      this.isVisible = p_192999_1_;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   @OnlyIn(Dist.CLIENT)
   class FurnaceRecipeButtonWidget extends RecipeOverlayGui.RecipeButtonWidget {
      public FurnaceRecipeButtonWidget(int p_i48747_2_, int p_i48747_3_, IRecipe<?> p_i48747_4_, boolean p_i48747_5_) {
         super(p_i48747_2_, p_i48747_3_, p_i48747_4_, p_i48747_5_);
      }

      protected void calculateIngredientsPositions(IRecipe<?> p_201505_1_) {
         ItemStack[] aitemstack = p_201505_1_.getIngredients().get(0).getItems();
         this.ingredientPos.add(new RecipeOverlayGui.RecipeButtonWidget.Child(10, 10, aitemstack));
      }
   }

   @OnlyIn(Dist.CLIENT)
   class RecipeButtonWidget extends Widget implements IRecipePlacer<Ingredient> {
      private final IRecipe<?> recipe;
      private final boolean isCraftable;
      protected final List<RecipeOverlayGui.RecipeButtonWidget.Child> ingredientPos = Lists.newArrayList();

      public RecipeButtonWidget(int p_i47594_2_, int p_i47594_3_, IRecipe<?> p_i47594_4_, boolean p_i47594_5_) {
         super(p_i47594_2_, p_i47594_3_, 200, 20, StringTextComponent.EMPTY);
         this.width = 24;
         this.height = 24;
         this.recipe = p_i47594_4_;
         this.isCraftable = p_i47594_5_;
         this.calculateIngredientsPositions(p_i47594_4_);
      }

      protected void calculateIngredientsPositions(IRecipe<?> p_201505_1_) {
         this.placeRecipe(3, 3, -1, p_201505_1_, p_201505_1_.getIngredients().iterator(), 0);
      }

      public void addItemToSlot(Iterator<Ingredient> p_201500_1_, int p_201500_2_, int p_201500_3_, int p_201500_4_, int p_201500_5_) {
         ItemStack[] aitemstack = p_201500_1_.next().getItems();
         if (aitemstack.length != 0) {
            this.ingredientPos.add(new RecipeOverlayGui.RecipeButtonWidget.Child(3 + p_201500_5_ * 7, 3 + p_201500_4_ * 7, aitemstack));
         }

      }

      public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
         RenderSystem.enableAlphaTest();
         RecipeOverlayGui.this.minecraft.getTextureManager().bind(RecipeOverlayGui.RECIPE_BOOK_LOCATION);
         int i = 152;
         if (!this.isCraftable) {
            i += 26;
         }

         int j = RecipeOverlayGui.this.isFurnaceMenu ? 130 : 78;
         if (this.isHovered()) {
            j += 26;
         }

         this.blit(p_230431_1_, this.x, this.y, i, j, this.width, this.height);

         for(RecipeOverlayGui.RecipeButtonWidget.Child recipeoverlaygui$recipebuttonwidget$child : this.ingredientPos) {
            RenderSystem.pushMatrix();
            float f = 0.42F;
            int k = (int)((float)(this.x + recipeoverlaygui$recipebuttonwidget$child.x) / 0.42F - 3.0F);
            int l = (int)((float)(this.y + recipeoverlaygui$recipebuttonwidget$child.y) / 0.42F - 3.0F);
            RenderSystem.scalef(0.42F, 0.42F, 1.0F);
            RecipeOverlayGui.this.minecraft.getItemRenderer().renderAndDecorateItem(recipeoverlaygui$recipebuttonwidget$child.ingredients[MathHelper.floor(RecipeOverlayGui.this.time / 30.0F) % recipeoverlaygui$recipebuttonwidget$child.ingredients.length], k, l);
            RenderSystem.popMatrix();
         }

         RenderSystem.disableAlphaTest();
      }

      @OnlyIn(Dist.CLIENT)
      public class Child {
         public final ItemStack[] ingredients;
         public final int x;
         public final int y;

         public Child(int p_i48748_2_, int p_i48748_3_, ItemStack[] p_i48748_4_) {
            this.x = p_i48748_2_;
            this.y = p_i48748_3_;
            this.ingredients = p_i48748_4_;
         }
      }
   }
}
