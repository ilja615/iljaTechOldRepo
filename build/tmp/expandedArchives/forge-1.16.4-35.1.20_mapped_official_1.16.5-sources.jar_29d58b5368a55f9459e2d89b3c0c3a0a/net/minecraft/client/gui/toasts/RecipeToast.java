package net.minecraft.client.gui.toasts;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeToast implements IToast {
   private static final ITextComponent TITLE_TEXT = new TranslationTextComponent("recipe.toast.title");
   private static final ITextComponent DESCRIPTION_TEXT = new TranslationTextComponent("recipe.toast.description");
   private final List<IRecipe<?>> recipes = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;

   public RecipeToast(IRecipe<?> p_i48624_1_) {
      this.recipes.add(p_i48624_1_);
   }

   public IToast.Visibility render(MatrixStack p_230444_1_, ToastGui p_230444_2_, long p_230444_3_) {
      if (this.changed) {
         this.lastChanged = p_230444_3_;
         this.changed = false;
      }

      if (this.recipes.isEmpty()) {
         return IToast.Visibility.HIDE;
      } else {
         p_230444_2_.getMinecraft().getTextureManager().bind(TEXTURE);
         RenderSystem.color3f(1.0F, 1.0F, 1.0F);
         p_230444_2_.blit(p_230444_1_, 0, 0, 0, 32, this.width(), this.height());
         p_230444_2_.getMinecraft().font.draw(p_230444_1_, TITLE_TEXT, 30.0F, 7.0F, -11534256);
         p_230444_2_.getMinecraft().font.draw(p_230444_1_, DESCRIPTION_TEXT, 30.0F, 18.0F, -16777216);
         IRecipe<?> irecipe = this.recipes.get((int)(p_230444_3_ / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
         ItemStack itemstack = irecipe.getToastSymbol();
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.6F, 0.6F, 1.0F);
         p_230444_2_.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(itemstack, 3, 3);
         RenderSystem.popMatrix();
         p_230444_2_.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(irecipe.getResultItem(), 8, 8);
         return p_230444_3_ - this.lastChanged >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      }
   }

   private void addItem(IRecipe<?> p_202905_1_) {
      this.recipes.add(p_202905_1_);
      this.changed = true;
   }

   public static void addOrUpdate(ToastGui p_193665_0_, IRecipe<?> p_193665_1_) {
      RecipeToast recipetoast = p_193665_0_.getToast(RecipeToast.class, NO_TOKEN);
      if (recipetoast == null) {
         p_193665_0_.addToast(new RecipeToast(p_193665_1_));
      } else {
         recipetoast.addItem(p_193665_1_);
      }

   }
}
