package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class Criterion {
   private final ICriterionInstance trigger;

   public Criterion(ICriterionInstance p_i47470_1_) {
      this.trigger = p_i47470_1_;
   }

   public Criterion() {
      this.trigger = null;
   }

   public void serializeToNetwork(PacketBuffer p_192140_1_) {
   }

   public static Criterion criterionFromJson(JsonObject p_232633_0_, ConditionArrayParser p_232633_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_232633_0_, "trigger"));
      ICriterionTrigger<?> icriteriontrigger = CriteriaTriggers.getCriterion(resourcelocation);
      if (icriteriontrigger == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + resourcelocation);
      } else {
         ICriterionInstance icriterioninstance = icriteriontrigger.createInstance(JSONUtils.getAsJsonObject(p_232633_0_, "conditions", new JsonObject()), p_232633_1_);
         return new Criterion(icriterioninstance);
      }
   }

   public static Criterion criterionFromNetwork(PacketBuffer p_192146_0_) {
      return new Criterion();
   }

   public static Map<String, Criterion> criteriaFromJson(JsonObject p_232634_0_, ConditionArrayParser p_232634_1_) {
      Map<String, Criterion> map = Maps.newHashMap();

      for(Entry<String, JsonElement> entry : p_232634_0_.entrySet()) {
         map.put(entry.getKey(), criterionFromJson(JSONUtils.convertToJsonObject(entry.getValue(), "criterion"), p_232634_1_));
      }

      return map;
   }

   public static Map<String, Criterion> criteriaFromNetwork(PacketBuffer p_192142_0_) {
      Map<String, Criterion> map = Maps.newHashMap();
      int i = p_192142_0_.readVarInt();

      for(int j = 0; j < i; ++j) {
         map.put(p_192142_0_.readUtf(32767), criterionFromNetwork(p_192142_0_));
      }

      return map;
   }

   public static void serializeToNetwork(Map<String, Criterion> p_192141_0_, PacketBuffer p_192141_1_) {
      p_192141_1_.writeVarInt(p_192141_0_.size());

      for(Entry<String, Criterion> entry : p_192141_0_.entrySet()) {
         p_192141_1_.writeUtf(entry.getKey());
         entry.getValue().serializeToNetwork(p_192141_1_);
      }

   }

   @Nullable
   public ICriterionInstance getTrigger() {
      return this.trigger;
   }

   public JsonElement serializeToJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("trigger", this.trigger.getCriterion().toString());
      JsonObject jsonobject1 = this.trigger.serializeToJson(ConditionArraySerializer.INSTANCE);
      if (jsonobject1.size() != 0) {
         jsonobject.add("conditions", jsonobject1);
      }

      return jsonobject;
   }
}
