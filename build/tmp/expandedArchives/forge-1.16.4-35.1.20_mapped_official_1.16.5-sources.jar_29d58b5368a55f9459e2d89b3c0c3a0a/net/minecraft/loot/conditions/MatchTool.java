package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;

public class MatchTool implements ILootCondition {
   private final ItemPredicate predicate;

   public MatchTool(ItemPredicate p_i51193_1_) {
      this.predicate = p_i51193_1_;
   }

   public LootConditionType getType() {
      return LootConditionManager.MATCH_TOOL;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public boolean test(LootContext p_test_1_) {
      ItemStack itemstack = p_test_1_.getParamOrNull(LootParameters.TOOL);
      return itemstack != null && this.predicate.matches(itemstack);
   }

   public static ILootCondition.IBuilder toolMatches(ItemPredicate.Builder p_216012_0_) {
      return () -> {
         return new MatchTool(p_216012_0_.build());
      };
   }

   public static class Serializer implements ILootSerializer<MatchTool> {
      public void serialize(JsonObject p_230424_1_, MatchTool p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.add("predicate", p_230424_2_.predicate.serializeToJson());
      }

      public MatchTool deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         ItemPredicate itempredicate = ItemPredicate.fromJson(p_230423_1_.get("predicate"));
         return new MatchTool(itempredicate);
      }
   }
}
