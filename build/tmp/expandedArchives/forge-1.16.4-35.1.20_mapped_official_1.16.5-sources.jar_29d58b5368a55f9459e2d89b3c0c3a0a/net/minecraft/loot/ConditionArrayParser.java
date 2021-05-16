package net.minecraft.loot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConditionArrayParser {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation id;
   private final LootPredicateManager predicateManager;
   private final Gson predicateGson = LootSerializers.createConditionSerializer().create();

   public ConditionArrayParser(ResourceLocation p_i231549_1_, LootPredicateManager p_i231549_2_) {
      this.id = p_i231549_1_;
      this.predicateManager = p_i231549_2_;
   }

   public final ILootCondition[] deserializeConditions(JsonArray p_234050_1_, String p_234050_2_, LootParameterSet p_234050_3_) {
      ILootCondition[] ailootcondition = this.predicateGson.fromJson(p_234050_1_, ILootCondition[].class);
      ValidationTracker validationtracker = new ValidationTracker(p_234050_3_, this.predicateManager::get, (p_234052_0_) -> {
         return null;
      });

      for(ILootCondition ilootcondition : ailootcondition) {
         ilootcondition.validate(validationtracker);
         validationtracker.getProblems().forEach((p_234051_1_, p_234051_2_) -> {
            LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", p_234050_2_, p_234051_1_, p_234051_2_);
         });
      }

      return ailootcondition;
   }

   public ResourceLocation getAdvancementId() {
      return this.id;
   }
}
