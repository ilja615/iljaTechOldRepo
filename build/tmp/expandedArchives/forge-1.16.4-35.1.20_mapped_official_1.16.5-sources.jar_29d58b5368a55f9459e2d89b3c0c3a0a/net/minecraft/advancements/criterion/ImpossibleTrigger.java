package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class ImpossibleTrigger implements ICriterionTrigger<ImpossibleTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("impossible");

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<ImpossibleTrigger.Instance> p_192165_2_) {
   }

   public void removePlayerListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<ImpossibleTrigger.Instance> p_192164_2_) {
   }

   public void removePlayerListeners(PlayerAdvancements p_192167_1_) {
   }

   public ImpossibleTrigger.Instance createInstance(JsonObject p_230307_1_, ConditionArrayParser p_230307_2_) {
      return new ImpossibleTrigger.Instance();
   }

   public static class Instance implements ICriterionInstance {
      public ResourceLocation getCriterion() {
         return ImpossibleTrigger.ID;
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         return new JsonObject();
      }
   }
}
