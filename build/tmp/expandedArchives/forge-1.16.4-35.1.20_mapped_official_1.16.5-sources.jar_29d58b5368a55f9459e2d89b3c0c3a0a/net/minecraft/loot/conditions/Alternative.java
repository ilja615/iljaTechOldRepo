package net.minecraft.loot.conditions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.JSONUtils;

public class Alternative implements ILootCondition {
   private final ILootCondition[] terms;
   private final Predicate<LootContext> composedPredicate;

   private Alternative(ILootCondition[] p_i51209_1_) {
      this.terms = p_i51209_1_;
      this.composedPredicate = LootConditionManager.orConditions(p_i51209_1_);
   }

   public LootConditionType getType() {
      return LootConditionManager.ALTERNATIVE;
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

   public static Alternative.Builder alternative(ILootCondition.IBuilder... p_215960_0_) {
      return new Alternative.Builder(p_215960_0_);
   }

   public static class Builder implements ILootCondition.IBuilder {
      private final List<ILootCondition> terms = Lists.newArrayList();

      public Builder(ILootCondition.IBuilder... p_i50046_1_) {
         for(ILootCondition.IBuilder ilootcondition$ibuilder : p_i50046_1_) {
            this.terms.add(ilootcondition$ibuilder.build());
         }

      }

      public Alternative.Builder or(ILootCondition.IBuilder p_216297_1_) {
         this.terms.add(p_216297_1_.build());
         return this;
      }

      public ILootCondition build() {
         return new Alternative(this.terms.toArray(new ILootCondition[0]));
      }
   }

   public static class Serializer implements ILootSerializer<Alternative> {
      public void serialize(JsonObject p_230424_1_, Alternative p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.add("terms", p_230424_3_.serialize(p_230424_2_.terms));
      }

      public Alternative deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         ILootCondition[] ailootcondition = JSONUtils.getAsObject(p_230423_1_, "terms", p_230423_2_, ILootCondition[].class);
         return new Alternative(ailootcondition);
      }
   }
}
