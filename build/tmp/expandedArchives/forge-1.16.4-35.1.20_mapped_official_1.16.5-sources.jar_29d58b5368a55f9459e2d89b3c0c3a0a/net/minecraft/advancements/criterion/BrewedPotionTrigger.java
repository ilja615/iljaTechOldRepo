package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BrewedPotionTrigger extends AbstractCriterionTrigger<BrewedPotionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");

   public ResourceLocation getId() {
      return ID;
   }

   public BrewedPotionTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      Potion potion = null;
      if (p_230241_1_.has("potion")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_230241_1_, "potion"));
         potion = Registry.POTION.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + resourcelocation + "'");
         });
      }

      return new BrewedPotionTrigger.Instance(p_230241_2_, potion);
   }

   public void trigger(ServerPlayerEntity p_192173_1_, Potion p_192173_2_) {
      this.trigger(p_192173_1_, (p_226301_1_) -> {
         return p_226301_1_.matches(p_192173_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Potion potion;

      public Instance(EntityPredicate.AndPredicate p_i231487_1_, @Nullable Potion p_i231487_2_) {
         super(BrewedPotionTrigger.ID, p_i231487_1_);
         this.potion = p_i231487_2_;
      }

      public static BrewedPotionTrigger.Instance brewedPotion() {
         return new BrewedPotionTrigger.Instance(EntityPredicate.AndPredicate.ANY, (Potion)null);
      }

      public boolean matches(Potion p_192250_1_) {
         return this.potion == null || this.potion == p_192250_1_;
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         if (this.potion != null) {
            jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }
}
