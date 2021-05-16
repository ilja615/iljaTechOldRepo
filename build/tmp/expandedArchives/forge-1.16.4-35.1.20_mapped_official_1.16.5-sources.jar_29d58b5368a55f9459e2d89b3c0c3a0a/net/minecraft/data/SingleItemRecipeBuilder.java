package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SingleItemRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;
   private final IRecipeSerializer<?> type;

   public SingleItemRecipeBuilder(IRecipeSerializer<?> p_i50787_1_, Ingredient p_i50787_2_, IItemProvider p_i50787_3_, int p_i50787_4_) {
      this.type = p_i50787_1_;
      this.result = p_i50787_3_.asItem();
      this.ingredient = p_i50787_2_;
      this.count = p_i50787_4_;
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient p_218648_0_, IItemProvider p_218648_1_) {
      return new SingleItemRecipeBuilder(IRecipeSerializer.STONECUTTER, p_218648_0_, p_218648_1_, 1);
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient p_218644_0_, IItemProvider p_218644_1_, int p_218644_2_) {
      return new SingleItemRecipeBuilder(IRecipeSerializer.STONECUTTER, p_218644_0_, p_218644_1_, p_218644_2_);
   }

   public SingleItemRecipeBuilder unlocks(String p_218643_1_, ICriterionInstance p_218643_2_) {
      this.advancement.addCriterion(p_218643_1_, p_218643_2_);
      return this;
   }

   public void save(Consumer<IFinishedRecipe> p_218645_1_, String p_218645_2_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(p_218645_2_)).equals(resourcelocation)) {
         throw new IllegalStateException("Single Item Recipe " + p_218645_2_ + " should remove its 'save' argument");
      } else {
         this.save(p_218645_1_, new ResourceLocation(p_218645_2_));
      }
   }

   public void save(Consumer<IFinishedRecipe> p_218647_1_, ResourceLocation p_218647_2_) {
      this.ensureValid(p_218647_2_);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_218647_2_)).rewards(AdvancementRewards.Builder.recipe(p_218647_2_)).requirements(IRequirementsStrategy.OR);
      p_218647_1_.accept(new SingleItemRecipeBuilder.Result(p_218647_2_, this.type, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.advancement, new ResourceLocation(p_218647_2_.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + p_218647_2_.getPath())));
   }

   private void ensureValid(ResourceLocation p_218646_1_) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_218646_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final int count;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final IRecipeSerializer<?> type;

      public Result(ResourceLocation p_i50574_1_, IRecipeSerializer<?> p_i50574_2_, String p_i50574_3_, Ingredient p_i50574_4_, Item p_i50574_5_, int p_i50574_6_, Advancement.Builder p_i50574_7_, ResourceLocation p_i50574_8_) {
         this.id = p_i50574_1_;
         this.type = p_i50574_2_;
         this.group = p_i50574_3_;
         this.ingredient = p_i50574_4_;
         this.result = p_i50574_5_;
         this.count = p_i50574_6_;
         this.advancement = p_i50574_7_;
         this.advancementId = p_i50574_8_;
      }

      public void serializeRecipeData(JsonObject p_218610_1_) {
         if (!this.group.isEmpty()) {
            p_218610_1_.addProperty("group", this.group);
         }

         p_218610_1_.add("ingredient", this.ingredient.toJson());
         p_218610_1_.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         p_218610_1_.addProperty("count", this.count);
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public IRecipeSerializer<?> getType() {
         return this.type;
      }

      @Nullable
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      @Nullable
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}
