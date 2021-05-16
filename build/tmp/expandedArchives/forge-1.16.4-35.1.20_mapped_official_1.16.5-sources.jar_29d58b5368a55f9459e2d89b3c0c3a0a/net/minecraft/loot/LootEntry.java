package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootEntry implements ILootEntry {
   protected final ILootCondition[] conditions;
   private final Predicate<LootContext> compositeCondition;

   protected LootEntry(ILootCondition[] p_i51254_1_) {
      this.conditions = p_i51254_1_;
      this.compositeCondition = LootConditionManager.andConditions(p_i51254_1_);
   }

   public void validate(ValidationTracker p_225579_1_) {
      for(int i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].validate(p_225579_1_.forChild(".condition[" + i + "]"));
      }

   }

   protected final boolean canRun(LootContext p_216141_1_) {
      return this.compositeCondition.test(p_216141_1_);
   }

   public abstract LootPoolEntryType getType();

   public abstract static class Builder<T extends LootEntry.Builder<T>> implements ILootConditionConsumer<T> {
      private final List<ILootCondition> conditions = Lists.newArrayList();

      protected abstract T getThis();

      public T when(ILootCondition.IBuilder p_212840_1_) {
         this.conditions.add(p_212840_1_.build());
         return this.getThis();
      }

      public final T unwrap() {
         return this.getThis();
      }

      protected ILootCondition[] getConditions() {
         return this.conditions.toArray(new ILootCondition[0]);
      }

      public AlternativesLootEntry.Builder otherwise(LootEntry.Builder<?> p_216080_1_) {
         return new AlternativesLootEntry.Builder(this, p_216080_1_);
      }

      public abstract LootEntry build();
   }

   public abstract static class Serializer<T extends LootEntry> implements ILootSerializer<T> {
      public final void serialize(JsonObject p_230424_1_, T p_230424_2_, JsonSerializationContext p_230424_3_) {
         if (!ArrayUtils.isEmpty((Object[])p_230424_2_.conditions)) {
            p_230424_1_.add("conditions", p_230424_3_.serialize(p_230424_2_.conditions));
         }

         this.serializeCustom(p_230424_1_, p_230424_2_, p_230424_3_);
      }

      public final T deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         ILootCondition[] ailootcondition = JSONUtils.getAsObject(p_230423_1_, "conditions", new ILootCondition[0], p_230423_2_, ILootCondition[].class);
         return this.deserializeCustom(p_230423_1_, p_230423_2_, ailootcondition);
      }

      public abstract void serializeCustom(JsonObject p_230422_1_, T p_230422_2_, JsonSerializationContext p_230422_3_);

      public abstract T deserializeCustom(JsonObject p_230421_1_, JsonDeserializationContext p_230421_2_, ILootCondition[] p_230421_3_);
   }
}
