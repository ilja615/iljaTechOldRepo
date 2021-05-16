package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameType;

public class PlayerPredicate {
   public static final PlayerPredicate ANY = (new PlayerPredicate.Default()).build();
   private final MinMaxBounds.IntBound level;
   private final GameType gameType;
   private final Map<Stat<?>, MinMaxBounds.IntBound> stats;
   private final Object2BooleanMap<ResourceLocation> recipes;
   private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> advancements;

   private static PlayerPredicate.IAdvancementPredicate advancementPredicateFromJson(JsonElement p_227004_0_) {
      if (p_227004_0_.isJsonPrimitive()) {
         boolean flag = p_227004_0_.getAsBoolean();
         return new PlayerPredicate.CompletedAdvancementPredicate(flag);
      } else {
         Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap<>();
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_227004_0_, "criterion data");
         jsonobject.entrySet().forEach((p_227003_1_) -> {
            boolean flag1 = JSONUtils.convertToBoolean(p_227003_1_.getValue(), "criterion test");
            object2booleanmap.put(p_227003_1_.getKey(), flag1);
         });
         return new PlayerPredicate.CriteriaPredicate(object2booleanmap);
      }
   }

   private PlayerPredicate(MinMaxBounds.IntBound p_i225770_1_, GameType p_i225770_2_, Map<Stat<?>, MinMaxBounds.IntBound> p_i225770_3_, Object2BooleanMap<ResourceLocation> p_i225770_4_, Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> p_i225770_5_) {
      this.level = p_i225770_1_;
      this.gameType = p_i225770_2_;
      this.stats = p_i225770_3_;
      this.recipes = p_i225770_4_;
      this.advancements = p_i225770_5_;
   }

   public boolean matches(Entity p_226998_1_) {
      if (this == ANY) {
         return true;
      } else if (!(p_226998_1_ instanceof ServerPlayerEntity)) {
         return false;
      } else {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_226998_1_;
         if (!this.level.matches(serverplayerentity.experienceLevel)) {
            return false;
         } else if (this.gameType != GameType.NOT_SET && this.gameType != serverplayerentity.gameMode.getGameModeForPlayer()) {
            return false;
         } else {
            StatisticsManager statisticsmanager = serverplayerentity.getStats();

            for(Entry<Stat<?>, MinMaxBounds.IntBound> entry : this.stats.entrySet()) {
               int i = statisticsmanager.getValue(entry.getKey());
               if (!entry.getValue().matches(i)) {
                  return false;
               }
            }

            RecipeBook recipebook = serverplayerentity.getRecipeBook();

            for(it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<ResourceLocation> entry2 : this.recipes.object2BooleanEntrySet()) {
               if (recipebook.contains(entry2.getKey()) != entry2.getBooleanValue()) {
                  return false;
               }
            }

            if (!this.advancements.isEmpty()) {
               PlayerAdvancements playeradvancements = serverplayerentity.getAdvancements();
               AdvancementManager advancementmanager = serverplayerentity.getServer().getAdvancements();

               for(Entry<ResourceLocation, PlayerPredicate.IAdvancementPredicate> entry1 : this.advancements.entrySet()) {
                  Advancement advancement = advancementmanager.getAdvancement(entry1.getKey());
                  if (advancement == null || !entry1.getValue().test(playeradvancements.getOrStartProgress(advancement))) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public static PlayerPredicate fromJson(@Nullable JsonElement p_227000_0_) {
      if (p_227000_0_ != null && !p_227000_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_227000_0_, "player");
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("level"));
         String s = JSONUtils.getAsString(jsonobject, "gamemode", "");
         GameType gametype = GameType.byName(s, GameType.NOT_SET);
         Map<Stat<?>, MinMaxBounds.IntBound> map = Maps.newHashMap();
         JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "stats", (JsonArray)null);
         if (jsonarray != null) {
            for(JsonElement jsonelement : jsonarray) {
               JsonObject jsonobject1 = JSONUtils.convertToJsonObject(jsonelement, "stats entry");
               ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject1, "type"));
               StatType<?> stattype = Registry.STAT_TYPE.get(resourcelocation);
               if (stattype == null) {
                  throw new JsonParseException("Invalid stat type: " + resourcelocation);
               }

               ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getAsString(jsonobject1, "stat"));
               Stat<?> stat = getStat(stattype, resourcelocation1);
               MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject1.get("value"));
               map.put(stat, minmaxbounds$intbound1);
            }
         }

         Object2BooleanMap<ResourceLocation> object2booleanmap = new Object2BooleanOpenHashMap<>();
         JsonObject jsonobject2 = JSONUtils.getAsJsonObject(jsonobject, "recipes", new JsonObject());

         for(Entry<String, JsonElement> entry : jsonobject2.entrySet()) {
            ResourceLocation resourcelocation2 = new ResourceLocation(entry.getKey());
            boolean flag = JSONUtils.convertToBoolean(entry.getValue(), "recipe present");
            object2booleanmap.put(resourcelocation2, flag);
         }

         Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> map1 = Maps.newHashMap();
         JsonObject jsonobject3 = JSONUtils.getAsJsonObject(jsonobject, "advancements", new JsonObject());

         for(Entry<String, JsonElement> entry1 : jsonobject3.entrySet()) {
            ResourceLocation resourcelocation3 = new ResourceLocation(entry1.getKey());
            PlayerPredicate.IAdvancementPredicate playerpredicate$iadvancementpredicate = advancementPredicateFromJson(entry1.getValue());
            map1.put(resourcelocation3, playerpredicate$iadvancementpredicate);
         }

         return new PlayerPredicate(minmaxbounds$intbound, gametype, map, object2booleanmap, map1);
      } else {
         return ANY;
      }
   }

   private static <T> Stat<T> getStat(StatType<T> p_226997_0_, ResourceLocation p_226997_1_) {
      Registry<T> registry = p_226997_0_.getRegistry();
      T t = registry.get(p_226997_1_);
      if (t == null) {
         throw new JsonParseException("Unknown object " + p_226997_1_ + " for stat type " + Registry.STAT_TYPE.getKey(p_226997_0_));
      } else {
         return p_226997_0_.get(t);
      }
   }

   private static <T> ResourceLocation getStatValueId(Stat<T> p_226996_0_) {
      return p_226996_0_.getType().getRegistry().getKey(p_226996_0_.getValue());
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("level", this.level.serializeToJson());
         if (this.gameType != GameType.NOT_SET) {
            jsonobject.addProperty("gamemode", this.gameType.getName());
         }

         if (!this.stats.isEmpty()) {
            JsonArray jsonarray = new JsonArray();
            this.stats.forEach((p_226999_1_, p_226999_2_) -> {
               JsonObject jsonobject3 = new JsonObject();
               jsonobject3.addProperty("type", Registry.STAT_TYPE.getKey(p_226999_1_.getType()).toString());
               jsonobject3.addProperty("stat", getStatValueId(p_226999_1_).toString());
               jsonobject3.add("value", p_226999_2_.serializeToJson());
               jsonarray.add(jsonobject3);
            });
            jsonobject.add("stats", jsonarray);
         }

         if (!this.recipes.isEmpty()) {
            JsonObject jsonobject1 = new JsonObject();
            this.recipes.forEach((p_227002_1_, p_227002_2_) -> {
               jsonobject1.addProperty(p_227002_1_.toString(), p_227002_2_);
            });
            jsonobject.add("recipes", jsonobject1);
         }

         if (!this.advancements.isEmpty()) {
            JsonObject jsonobject2 = new JsonObject();
            this.advancements.forEach((p_227001_1_, p_227001_2_) -> {
               jsonobject2.add(p_227001_1_.toString(), p_227001_2_.toJson());
            });
            jsonobject.add("advancements", jsonobject2);
         }

         return jsonobject;
      }
   }

   static class CompletedAdvancementPredicate implements PlayerPredicate.IAdvancementPredicate {
      private final boolean state;

      public CompletedAdvancementPredicate(boolean p_i225773_1_) {
         this.state = p_i225773_1_;
      }

      public JsonElement toJson() {
         return new JsonPrimitive(this.state);
      }

      public boolean test(AdvancementProgress p_test_1_) {
         return p_test_1_.isDone() == this.state;
      }
   }

   static class CriteriaPredicate implements PlayerPredicate.IAdvancementPredicate {
      private final Object2BooleanMap<String> criterions;

      public CriteriaPredicate(Object2BooleanMap<String> p_i225772_1_) {
         this.criterions = p_i225772_1_;
      }

      public JsonElement toJson() {
         JsonObject jsonobject = new JsonObject();
         this.criterions.forEach(jsonobject::addProperty);
         return jsonobject;
      }

      public boolean test(AdvancementProgress p_test_1_) {
         for(it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<String> entry : this.criterions.object2BooleanEntrySet()) {
            CriterionProgress criterionprogress = p_test_1_.getCriterion(entry.getKey());
            if (criterionprogress == null || criterionprogress.isDone() != entry.getBooleanValue()) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Default {
      private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.ANY;
      private GameType gameType = GameType.NOT_SET;
      private final Map<Stat<?>, MinMaxBounds.IntBound> stats = Maps.newHashMap();
      private final Object2BooleanMap<ResourceLocation> recipes = new Object2BooleanOpenHashMap<>();
      private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> advancements = Maps.newHashMap();

      public PlayerPredicate build() {
         return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements);
      }
   }

   interface IAdvancementPredicate extends Predicate<AdvancementProgress> {
      JsonElement toJson();
   }
}
