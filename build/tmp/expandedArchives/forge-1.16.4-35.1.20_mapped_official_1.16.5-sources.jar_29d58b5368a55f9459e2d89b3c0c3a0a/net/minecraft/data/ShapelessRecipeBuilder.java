package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
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
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;

   public ShapelessRecipeBuilder(IItemProvider p_i48260_1_, int p_i48260_2_) {
      this.result = p_i48260_1_.asItem();
      this.count = p_i48260_2_;
   }

   public static ShapelessRecipeBuilder shapeless(IItemProvider p_200486_0_) {
      return new ShapelessRecipeBuilder(p_200486_0_, 1);
   }

   public static ShapelessRecipeBuilder shapeless(IItemProvider p_200488_0_, int p_200488_1_) {
      return new ShapelessRecipeBuilder(p_200488_0_, p_200488_1_);
   }

   public ShapelessRecipeBuilder requires(ITag<Item> p_203221_1_) {
      return this.requires(Ingredient.of(p_203221_1_));
   }

   public ShapelessRecipeBuilder requires(IItemProvider p_200487_1_) {
      return this.requires(p_200487_1_, 1);
   }

   public ShapelessRecipeBuilder requires(IItemProvider p_200491_1_, int p_200491_2_) {
      for(int i = 0; i < p_200491_2_; ++i) {
         this.requires(Ingredient.of(p_200491_1_));
      }

      return this;
   }

   public ShapelessRecipeBuilder requires(Ingredient p_200489_1_) {
      return this.requires(p_200489_1_, 1);
   }

   public ShapelessRecipeBuilder requires(Ingredient p_200492_1_, int p_200492_2_) {
      for(int i = 0; i < p_200492_2_; ++i) {
         this.ingredients.add(p_200492_1_);
      }

      return this;
   }

   public ShapelessRecipeBuilder unlockedBy(String p_200483_1_, ICriterionInstance p_200483_2_) {
      this.advancement.addCriterion(p_200483_1_, p_200483_2_);
      return this;
   }

   public ShapelessRecipeBuilder group(String p_200490_1_) {
      this.group = p_200490_1_;
      return this;
   }

   public void save(Consumer<IFinishedRecipe> p_200482_1_) {
      this.save(p_200482_1_, Registry.ITEM.getKey(this.result));
   }

   public void save(Consumer<IFinishedRecipe> p_200484_1_, String p_200484_2_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(p_200484_2_)).equals(resourcelocation)) {
         throw new IllegalStateException("Shapeless Recipe " + p_200484_2_ + " should remove its 'save' argument");
      } else {
         this.save(p_200484_1_, new ResourceLocation(p_200484_2_));
      }
   }

   public void save(Consumer<IFinishedRecipe> p_200485_1_, ResourceLocation p_200485_2_) {
      this.ensureValid(p_200485_2_);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_200485_2_)).rewards(AdvancementRewards.Builder.recipe(p_200485_2_)).requirements(IRequirementsStrategy.OR);
      p_200485_1_.accept(new ShapelessRecipeBuilder.Result(p_200485_2_, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(p_200485_2_.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + p_200485_2_.getPath())));
   }

   private void ensureValid(ResourceLocation p_200481_1_) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_200481_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation p_i48268_1_, Item p_i48268_2_, int p_i48268_3_, String p_i48268_4_, List<Ingredient> p_i48268_5_, Advancement.Builder p_i48268_6_, ResourceLocation p_i48268_7_) {
         this.id = p_i48268_1_;
         this.result = p_i48268_2_;
         this.count = p_i48268_3_;
         this.group = p_i48268_4_;
         this.ingredients = p_i48268_5_;
         this.advancement = p_i48268_6_;
         this.advancementId = p_i48268_7_;
      }

      public void serializeRecipeData(JsonObject p_218610_1_) {
         if (!this.group.isEmpty()) {
            p_218610_1_.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(Ingredient ingredient : this.ingredients) {
            jsonarray.add(ingredient.toJson());
         }

         p_218610_1_.add("ingredients", jsonarray);
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            jsonobject.addProperty("count", this.count);
         }

         p_218610_1_.add("result", jsonobject);
      }

      public IRecipeSerializer<?> getType() {
         return IRecipeSerializer.SHAPELESS_RECIPE;
      }

      public ResourceLocation getId() {
         return this.id;
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
