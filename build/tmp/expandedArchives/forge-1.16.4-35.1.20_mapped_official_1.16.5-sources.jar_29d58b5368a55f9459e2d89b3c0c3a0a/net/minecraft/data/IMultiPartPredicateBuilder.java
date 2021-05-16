package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

public interface IMultiPartPredicateBuilder extends Supplier<JsonElement> {
   void validate(StateContainer<?, ?> p_230523_1_);

   static IMultiPartPredicateBuilder.Properties condition() {
      return new IMultiPartPredicateBuilder.Properties();
   }

   static IMultiPartPredicateBuilder or(IMultiPartPredicateBuilder... p_240090_0_) {
      return new IMultiPartPredicateBuilder.Serializer(IMultiPartPredicateBuilder.Operator.OR, Arrays.asList(p_240090_0_));
   }

   public static enum Operator {
      AND("AND"),
      OR("OR");

      private final String id;

      private Operator(String p_i232523_3_) {
         this.id = p_i232523_3_;
      }
   }

   public static class Properties implements IMultiPartPredicateBuilder {
      private final Map<Property<?>, String> terms = Maps.newHashMap();

      private static <T extends Comparable<T>> String joinValues(Property<T> p_240101_0_, Stream<T> p_240101_1_) {
         return p_240101_1_.map(p_240101_0_::getName).collect(Collectors.joining("|"));
      }

      private static <T extends Comparable<T>> String getTerm(Property<T> p_240103_0_, T p_240103_1_, T[] p_240103_2_) {
         return joinValues(p_240103_0_, Stream.concat(Stream.of(p_240103_1_), Stream.of(p_240103_2_)));
      }

      private <T extends Comparable<T>> void putValue(Property<T> p_240100_1_, String p_240100_2_) {
         String s = this.terms.put(p_240100_1_, p_240100_2_);
         if (s != null) {
            throw new IllegalStateException("Tried to replace " + p_240100_1_ + " value from " + s + " to " + p_240100_2_);
         }
      }

      public final <T extends Comparable<T>> IMultiPartPredicateBuilder.Properties term(Property<T> p_240098_1_, T p_240098_2_) {
         this.putValue(p_240098_1_, p_240098_1_.getName(p_240098_2_));
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> IMultiPartPredicateBuilder.Properties term(Property<T> p_240099_1_, T p_240099_2_, T... p_240099_3_) {
         this.putValue(p_240099_1_, getTerm(p_240099_1_, p_240099_2_, p_240099_3_));
         return this;
      }

      public JsonElement get() {
         JsonObject jsonobject = new JsonObject();
         this.terms.forEach((p_240102_1_, p_240102_2_) -> {
            jsonobject.addProperty(p_240102_1_.getName(), p_240102_2_);
         });
         return jsonobject;
      }

      public void validate(StateContainer<?, ?> p_230523_1_) {
         List<Property<?>> list = this.terms.keySet().stream().filter((p_240097_1_) -> {
            return p_230523_1_.getProperty(p_240097_1_.getName()) != p_240097_1_;
         }).collect(Collectors.toList());
         if (!list.isEmpty()) {
            throw new IllegalStateException("Properties " + list + " are missing from " + p_230523_1_);
         }
      }
   }

   public static class Serializer implements IMultiPartPredicateBuilder {
      private final IMultiPartPredicateBuilder.Operator operation;
      private final List<IMultiPartPredicateBuilder> subconditions;

      private Serializer(IMultiPartPredicateBuilder.Operator p_i232521_1_, List<IMultiPartPredicateBuilder> p_i232521_2_) {
         this.operation = p_i232521_1_;
         this.subconditions = p_i232521_2_;
      }

      public void validate(StateContainer<?, ?> p_230523_1_) {
         this.subconditions.forEach((p_240093_1_) -> {
            p_240093_1_.validate(p_230523_1_);
         });
      }

      public JsonElement get() {
         JsonArray jsonarray = new JsonArray();
         this.subconditions.stream().map(Supplier::get).forEach(jsonarray::add);
         JsonObject jsonobject = new JsonObject();
         jsonobject.add(this.operation.id, jsonarray);
         return jsonobject;
      }
   }
}
