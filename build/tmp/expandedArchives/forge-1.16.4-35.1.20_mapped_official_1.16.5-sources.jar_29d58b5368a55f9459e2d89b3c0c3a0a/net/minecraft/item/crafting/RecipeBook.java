package net.minecraft.item.crafting;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeBook {
   protected final Set<ResourceLocation> known = Sets.newHashSet();
   protected final Set<ResourceLocation> highlight = Sets.newHashSet();
   private final RecipeBookStatus bookSettings = new RecipeBookStatus();

   public void copyOverData(RecipeBook p_193824_1_) {
      this.known.clear();
      this.highlight.clear();
      this.bookSettings.replaceFrom(p_193824_1_.bookSettings);
      this.known.addAll(p_193824_1_.known);
      this.highlight.addAll(p_193824_1_.highlight);
   }

   public void add(IRecipe<?> p_194073_1_) {
      if (!p_194073_1_.isSpecial()) {
         this.add(p_194073_1_.getId());
      }

   }

   protected void add(ResourceLocation p_209118_1_) {
      this.known.add(p_209118_1_);
   }

   public boolean contains(@Nullable IRecipe<?> p_193830_1_) {
      return p_193830_1_ == null ? false : this.known.contains(p_193830_1_.getId());
   }

   public boolean contains(ResourceLocation p_226144_1_) {
      return this.known.contains(p_226144_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void remove(IRecipe<?> p_193831_1_) {
      this.remove(p_193831_1_.getId());
   }

   protected void remove(ResourceLocation p_209119_1_) {
      this.known.remove(p_209119_1_);
      this.highlight.remove(p_209119_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean willHighlight(IRecipe<?> p_194076_1_) {
      return this.highlight.contains(p_194076_1_.getId());
   }

   public void removeHighlight(IRecipe<?> p_194074_1_) {
      this.highlight.remove(p_194074_1_.getId());
   }

   public void addHighlight(IRecipe<?> p_193825_1_) {
      this.addHighlight(p_193825_1_.getId());
   }

   protected void addHighlight(ResourceLocation p_209120_1_) {
      this.highlight.add(p_209120_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOpen(RecipeBookCategory p_242142_1_) {
      return this.bookSettings.isOpen(p_242142_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setOpen(RecipeBookCategory p_242143_1_, boolean p_242143_2_) {
      this.bookSettings.setOpen(p_242143_1_, p_242143_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFiltering(RecipeBookContainer<?> p_242141_1_) {
      return this.isFiltering(p_242141_1_.getRecipeBookType());
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFiltering(RecipeBookCategory p_242145_1_) {
      return this.bookSettings.isFiltering(p_242145_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setFiltering(RecipeBookCategory p_242146_1_, boolean p_242146_2_) {
      this.bookSettings.setFiltering(p_242146_1_, p_242146_2_);
   }

   public void setBookSettings(RecipeBookStatus p_242140_1_) {
      this.bookSettings.replaceFrom(p_242140_1_);
   }

   public RecipeBookStatus getBookSettings() {
      return this.bookSettings.copy();
   }

   public void setBookSetting(RecipeBookCategory p_242144_1_, boolean p_242144_2_, boolean p_242144_3_) {
      this.bookSettings.setOpen(p_242144_1_, p_242144_2_);
      this.bookSettings.setFiltering(p_242144_1_, p_242144_3_);
   }
}
