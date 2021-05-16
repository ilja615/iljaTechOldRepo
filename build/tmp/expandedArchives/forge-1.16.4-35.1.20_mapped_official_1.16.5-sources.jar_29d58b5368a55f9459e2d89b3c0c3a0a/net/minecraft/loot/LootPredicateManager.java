package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootPredicateManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = LootSerializers.createConditionSerializer().create();
   private Map<ResourceLocation, ILootCondition> conditions = ImmutableMap.of();

   public LootPredicateManager() {
      super(GSON, "predicates");
   }

   @Nullable
   public ILootCondition get(ResourceLocation p_227517_1_) {
      return this.conditions.get(p_227517_1_);
   }

   protected void apply(Map<ResourceLocation, JsonElement> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Builder<ResourceLocation, ILootCondition> builder = ImmutableMap.builder();
      p_212853_1_.forEach((p_237404_1_, p_237404_2_) -> {
         try {
            if (p_237404_2_.isJsonArray()) {
               ILootCondition[] ailootcondition = GSON.fromJson(p_237404_2_, ILootCondition[].class);
               builder.put(p_237404_1_, new LootPredicateManager.AndCombiner(ailootcondition));
            } else {
               ILootCondition ilootcondition = GSON.fromJson(p_237404_2_, ILootCondition.class);
               builder.put(p_237404_1_, ilootcondition);
            }
         } catch (Exception exception) {
            LOGGER.error("Couldn't parse loot table {}", p_237404_1_, exception);
         }

      });
      Map<ResourceLocation, ILootCondition> map = builder.build();
      ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.ALL_PARAMS, map::get, (p_227518_0_) -> {
         return null;
      });
      map.forEach((p_227515_1_, p_227515_2_) -> {
         p_227515_2_.validate(validationtracker.enterCondition("{" + p_227515_1_ + "}", p_227515_1_));
      });
      validationtracker.getProblems().forEach((p_227516_0_, p_227516_1_) -> {
         LOGGER.warn("Found validation problem in " + p_227516_0_ + ": " + p_227516_1_);
      });
      this.conditions = map;
   }

   public Set<ResourceLocation> getKeys() {
      return Collections.unmodifiableSet(this.conditions.keySet());
   }

   static class AndCombiner implements ILootCondition {
      private final ILootCondition[] terms;
      private final Predicate<LootContext> composedPredicate;

      private AndCombiner(ILootCondition[] p_i232164_1_) {
         this.terms = p_i232164_1_;
         this.composedPredicate = LootConditionManager.andConditions(p_i232164_1_);
      }

      public final boolean test(LootContext p_test_1_) {
         return this.composedPredicate.test(p_test_1_);
      }

      public void validate(ValidationTracker p_225580_1_) {
         ILootCondition.super.validate(p_225580_1_);

         for(int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(p_225580_1_.forChild(".term[" + i + "]"));
         }

      }

      public LootConditionType getType() {
         throw new UnsupportedOperationException();
      }
   }
}
