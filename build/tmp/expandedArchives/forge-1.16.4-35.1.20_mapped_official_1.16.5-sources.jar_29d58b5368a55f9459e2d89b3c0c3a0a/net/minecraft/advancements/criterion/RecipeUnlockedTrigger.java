package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger extends AbstractCriterionTrigger<RecipeUnlockedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public ResourceLocation getId() {
      return ID;
   }

   public RecipeUnlockedTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_230241_1_, "recipe"));
      return new RecipeUnlockedTrigger.Instance(p_230241_2_, resourcelocation);
   }

   public void trigger(ServerPlayerEntity p_192225_1_, IRecipe<?> p_192225_2_) {
      this.trigger(p_192225_1_, (p_227018_1_) -> {
         return p_227018_1_.matches(p_192225_2_);
      });
   }

   public static RecipeUnlockedTrigger.Instance unlocked(ResourceLocation p_235675_0_) {
      return new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_235675_0_);
   }

   public static class Instance extends CriterionInstance {
      private final ResourceLocation recipe;

      public Instance(EntityPredicate.AndPredicate p_i231865_1_, ResourceLocation p_i231865_2_) {
         super(RecipeUnlockedTrigger.ID, p_i231865_1_);
         this.recipe = p_i231865_2_;
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.addProperty("recipe", this.recipe.toString());
         return jsonobject;
      }

      public boolean matches(IRecipe<?> p_193215_1_) {
         return this.recipe.equals(p_193215_1_.getId());
      }
   }
}
