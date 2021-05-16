package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootFunction implements ILootFunction {
   protected final ILootCondition[] predicates;
   private final Predicate<LootContext> compositePredicates;

   protected LootFunction(ILootCondition[] p_i51231_1_) {
      this.predicates = p_i51231_1_;
      this.compositePredicates = LootConditionManager.andConditions(p_i51231_1_);
   }

   public final ItemStack apply(ItemStack p_apply_1_, LootContext p_apply_2_) {
      return this.compositePredicates.test(p_apply_2_) ? this.run(p_apply_1_, p_apply_2_) : p_apply_1_;
   }

   protected abstract ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_);

   public void validate(ValidationTracker p_225580_1_) {
      ILootFunction.super.validate(p_225580_1_);

      for(int i = 0; i < this.predicates.length; ++i) {
         this.predicates[i].validate(p_225580_1_.forChild(".conditions[" + i + "]"));
      }

   }

   protected static LootFunction.Builder<?> simpleBuilder(Function<ILootCondition[], ILootFunction> p_215860_0_) {
      return new LootFunction.SimpleBuilder(p_215860_0_);
   }

   public abstract static class Builder<T extends LootFunction.Builder<T>> implements ILootFunction.IBuilder, ILootConditionConsumer<T> {
      private final List<ILootCondition> conditions = Lists.newArrayList();

      public T when(ILootCondition.IBuilder p_212840_1_) {
         this.conditions.add(p_212840_1_.build());
         return this.getThis();
      }

      public final T unwrap() {
         return this.getThis();
      }

      protected abstract T getThis();

      protected ILootCondition[] getConditions() {
         return this.conditions.toArray(new ILootCondition[0]);
      }
   }

   public abstract static class Serializer<T extends LootFunction> implements ILootSerializer<T> {
      public void serialize(JsonObject p_230424_1_, T p_230424_2_, JsonSerializationContext p_230424_3_) {
         if (!ArrayUtils.isEmpty((Object[])p_230424_2_.predicates)) {
            p_230424_1_.add("conditions", p_230424_3_.serialize(p_230424_2_.predicates));
         }

      }

      public final T deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         ILootCondition[] ailootcondition = JSONUtils.getAsObject(p_230423_1_, "conditions", new ILootCondition[0], p_230423_2_, ILootCondition[].class);
         return this.deserialize(p_230423_1_, p_230423_2_, ailootcondition);
      }

      public abstract T deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_);
   }

   static final class SimpleBuilder extends LootFunction.Builder<LootFunction.SimpleBuilder> {
      private final Function<ILootCondition[], ILootFunction> constructor;

      public SimpleBuilder(Function<ILootCondition[], ILootFunction> p_i50229_1_) {
         this.constructor = p_i50229_1_;
      }

      protected LootFunction.SimpleBuilder getThis() {
         return this;
      }

      public ILootFunction build() {
         return this.constructor.apply(this.getConditions());
      }
   }
}
