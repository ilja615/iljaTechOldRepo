package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.server.ServerWorld;

public class TimeCheck implements ILootCondition {
   @Nullable
   private final Long period;
   private final RandomValueRange value;

   private TimeCheck(@Nullable Long p_i225898_1_, RandomValueRange p_i225898_2_) {
      this.period = p_i225898_1_;
      this.value = p_i225898_2_;
   }

   public LootConditionType getType() {
      return LootConditionManager.TIME_CHECK;
   }

   public boolean test(LootContext p_test_1_) {
      ServerWorld serverworld = p_test_1_.getLevel();
      long i = serverworld.getDayTime();
      if (this.period != null) {
         i %= this.period;
      }

      return this.value.matchesValue((int)i);
   }

   public static class Serializer implements ILootSerializer<TimeCheck> {
      public void serialize(JsonObject p_230424_1_, TimeCheck p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.addProperty("period", p_230424_2_.period);
         p_230424_1_.add("value", p_230424_3_.serialize(p_230424_2_.value));
      }

      public TimeCheck deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         Long olong = p_230423_1_.has("period") ? JSONUtils.getAsLong(p_230423_1_, "period") : null;
         RandomValueRange randomvaluerange = JSONUtils.getAsObject(p_230423_1_, "value", p_230423_2_, RandomValueRange.class);
         return new TimeCheck(olong, randomvaluerange);
      }
   }
}
