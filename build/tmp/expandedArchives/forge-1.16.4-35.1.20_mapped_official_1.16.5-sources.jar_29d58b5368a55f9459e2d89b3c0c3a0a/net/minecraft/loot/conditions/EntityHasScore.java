package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.JSONUtils;

public class EntityHasScore implements ILootCondition {
   private final Map<String, RandomValueRange> scores;
   private final LootContext.EntityTarget entityTarget;

   private EntityHasScore(Map<String, RandomValueRange> p_i46618_1_, LootContext.EntityTarget p_i46618_2_) {
      this.scores = ImmutableMap.copyOf(p_i46618_1_);
      this.entityTarget = p_i46618_2_;
   }

   public LootConditionType getType() {
      return LootConditionManager.ENTITY_SCORES;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.entityTarget.getParam());
   }

   public boolean test(LootContext p_test_1_) {
      Entity entity = p_test_1_.getParamOrNull(this.entityTarget.getParam());
      if (entity == null) {
         return false;
      } else {
         Scoreboard scoreboard = entity.level.getScoreboard();

         for(Entry<String, RandomValueRange> entry : this.scores.entrySet()) {
            if (!this.hasScore(entity, scoreboard, entry.getKey(), entry.getValue())) {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean hasScore(Entity p_186631_1_, Scoreboard p_186631_2_, String p_186631_3_, RandomValueRange p_186631_4_) {
      ScoreObjective scoreobjective = p_186631_2_.getObjective(p_186631_3_);
      if (scoreobjective == null) {
         return false;
      } else {
         String s = p_186631_1_.getScoreboardName();
         return !p_186631_2_.hasPlayerScore(s, scoreobjective) ? false : p_186631_4_.matchesValue(p_186631_2_.getOrCreatePlayerScore(s, scoreobjective).getScore());
      }
   }

   public static class Serializer implements ILootSerializer<EntityHasScore> {
      public void serialize(JsonObject p_230424_1_, EntityHasScore p_230424_2_, JsonSerializationContext p_230424_3_) {
         JsonObject jsonobject = new JsonObject();

         for(Entry<String, RandomValueRange> entry : p_230424_2_.scores.entrySet()) {
            jsonobject.add(entry.getKey(), p_230424_3_.serialize(entry.getValue()));
         }

         p_230424_1_.add("scores", jsonobject);
         p_230424_1_.add("entity", p_230424_3_.serialize(p_230424_2_.entityTarget));
      }

      public EntityHasScore deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         Set<Entry<String, JsonElement>> set = JSONUtils.getAsJsonObject(p_230423_1_, "scores").entrySet();
         Map<String, RandomValueRange> map = Maps.newLinkedHashMap();

         for(Entry<String, JsonElement> entry : set) {
            map.put(entry.getKey(), JSONUtils.convertToObject(entry.getValue(), "score", p_230423_2_, RandomValueRange.class));
         }

         return new EntityHasScore(map, JSONUtils.getAsObject(p_230423_1_, "entity", p_230423_2_, LootContext.EntityTarget.class));
      }
   }
}
