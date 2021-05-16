package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference implements ILootCondition {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation name;

   private Reference(ResourceLocation p_i225894_1_) {
      this.name = p_i225894_1_;
   }

   public LootConditionType getType() {
      return LootConditionManager.REFERENCE;
   }

   public void validate(ValidationTracker p_225580_1_) {
      if (p_225580_1_.hasVisitedCondition(this.name)) {
         p_225580_1_.reportProblem("Condition " + this.name + " is recursively called");
      } else {
         ILootCondition.super.validate(p_225580_1_);
         ILootCondition ilootcondition = p_225580_1_.resolveCondition(this.name);
         if (ilootcondition == null) {
            p_225580_1_.reportProblem("Unknown condition table called " + this.name);
         } else {
            ilootcondition.validate(p_225580_1_.enterTable(".{" + this.name + "}", this.name));
         }

      }
   }

   public boolean test(LootContext p_test_1_) {
      ILootCondition ilootcondition = p_test_1_.getCondition(this.name);
      if (p_test_1_.addVisitedCondition(ilootcondition)) {
         boolean flag;
         try {
            flag = ilootcondition.test(p_test_1_);
         } finally {
            p_test_1_.removeVisitedCondition(ilootcondition);
         }

         return flag;
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
         return false;
      }
   }

   public static class Serializer implements ILootSerializer<Reference> {
      public void serialize(JsonObject p_230424_1_, Reference p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.addProperty("name", p_230424_2_.name.toString());
      }

      public Reference deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_230423_1_, "name"));
         return new Reference(resourcelocation);
      }
   }
}
