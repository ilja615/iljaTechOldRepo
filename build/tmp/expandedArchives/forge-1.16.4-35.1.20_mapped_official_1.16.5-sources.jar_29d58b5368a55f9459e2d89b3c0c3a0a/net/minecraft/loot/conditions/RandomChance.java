package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.util.JSONUtils;

public class RandomChance implements ILootCondition {
   private final float probability;

   private RandomChance(float p_i46615_1_) {
      this.probability = p_i46615_1_;
   }

   public LootConditionType getType() {
      return LootConditionManager.RANDOM_CHANCE;
   }

   public boolean test(LootContext p_test_1_) {
      return p_test_1_.getRandom().nextFloat() < this.probability;
   }

   public static ILootCondition.IBuilder randomChance(float p_216004_0_) {
      return () -> {
         return new RandomChance(p_216004_0_);
      };
   }

   public static class Serializer implements ILootSerializer<RandomChance> {
      public void serialize(JsonObject p_230424_1_, RandomChance p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.addProperty("chance", p_230424_2_.probability);
      }

      public RandomChance deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         return new RandomChance(JSONUtils.getAsFloat(p_230423_1_, "chance"));
      }
   }
}
