package net.minecraft.advancements.criterion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;

public class StatePropertiesPredicate {
   public static final StatePropertiesPredicate ANY = new StatePropertiesPredicate(ImmutableList.of());
   private final List<StatePropertiesPredicate.Matcher> properties;

   private static StatePropertiesPredicate.Matcher fromJson(String p_227188_0_, JsonElement p_227188_1_) {
      if (p_227188_1_.isJsonPrimitive()) {
         String s2 = p_227188_1_.getAsString();
         return new StatePropertiesPredicate.ExactMatcher(p_227188_0_, s2);
      } else {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_227188_1_, "value");
         String s = jsonobject.has("min") ? getStringOrNull(jsonobject.get("min")) : null;
         String s1 = jsonobject.has("max") ? getStringOrNull(jsonobject.get("max")) : null;
         return (StatePropertiesPredicate.Matcher)(s != null && s.equals(s1) ? new StatePropertiesPredicate.ExactMatcher(p_227188_0_, s) : new StatePropertiesPredicate.RangedMacher(p_227188_0_, s, s1));
      }
   }

   @Nullable
   private static String getStringOrNull(JsonElement p_227189_0_) {
      return p_227189_0_.isJsonNull() ? null : p_227189_0_.getAsString();
   }

   private StatePropertiesPredicate(List<StatePropertiesPredicate.Matcher> p_i225790_1_) {
      this.properties = ImmutableList.copyOf(p_i225790_1_);
   }

   public <S extends StateHolder<?, S>> boolean matches(StateContainer<?, S> p_227182_1_, S p_227182_2_) {
      for(StatePropertiesPredicate.Matcher statepropertiespredicate$matcher : this.properties) {
         if (!statepropertiespredicate$matcher.match(p_227182_1_, p_227182_2_)) {
            return false;
         }
      }

      return true;
   }

   public boolean matches(BlockState p_227181_1_) {
      return this.matches(p_227181_1_.getBlock().getStateDefinition(), p_227181_1_);
   }

   public boolean matches(FluidState p_227185_1_) {
      return this.matches(p_227185_1_.getType().getStateDefinition(), p_227185_1_);
   }

   public void checkState(StateContainer<?, ?> p_227183_1_, Consumer<String> p_227183_2_) {
      this.properties.forEach((p_227184_2_) -> {
         p_227184_2_.checkState(p_227183_1_, p_227183_2_);
      });
   }

   public static StatePropertiesPredicate fromJson(@Nullable JsonElement p_227186_0_) {
      if (p_227186_0_ != null && !p_227186_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_227186_0_, "properties");
         List<StatePropertiesPredicate.Matcher> list = Lists.newArrayList();

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            list.add(fromJson(entry.getKey(), entry.getValue()));
         }

         return new StatePropertiesPredicate(list);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.properties.isEmpty()) {
            this.properties.forEach((p_227187_1_) -> {
               jsonobject.add(p_227187_1_.getName(), p_227187_1_.toJson());
            });
         }

         return jsonobject;
      }
   }

   public static class Builder {
      private final List<StatePropertiesPredicate.Matcher> matchers = Lists.newArrayList();

      private Builder() {
      }

      public static StatePropertiesPredicate.Builder properties() {
         return new StatePropertiesPredicate.Builder();
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<?> p_227194_1_, String p_227194_2_) {
         this.matchers.add(new StatePropertiesPredicate.ExactMatcher(p_227194_1_.getName(), p_227194_2_));
         return this;
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<Integer> p_227192_1_, int p_227192_2_) {
         return this.hasProperty(p_227192_1_, Integer.toString(p_227192_2_));
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<Boolean> p_227195_1_, boolean p_227195_2_) {
         return this.hasProperty(p_227195_1_, Boolean.toString(p_227195_2_));
      }

      public <T extends Comparable<T> & IStringSerializable> StatePropertiesPredicate.Builder hasProperty(Property<T> p_227193_1_, T p_227193_2_) {
         return this.hasProperty(p_227193_1_, p_227193_2_.getSerializedName());
      }

      public StatePropertiesPredicate build() {
         return new StatePropertiesPredicate(this.matchers);
      }
   }

   static class ExactMatcher extends StatePropertiesPredicate.Matcher {
      private final String value;

      public ExactMatcher(String p_i225792_1_, String p_i225792_2_) {
         super(p_i225792_1_);
         this.value = p_i225792_2_;
      }

      protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_225554_1_, Property<T> p_225554_2_) {
         T t = p_225554_1_.getValue(p_225554_2_);
         Optional<T> optional = p_225554_2_.getValue(this.value);
         return optional.isPresent() && t.compareTo(optional.get()) == 0;
      }

      public JsonElement toJson() {
         return new JsonPrimitive(this.value);
      }
   }

   abstract static class Matcher {
      private final String name;

      public Matcher(String p_i225793_1_) {
         this.name = p_i225793_1_;
      }

      public <S extends StateHolder<?, S>> boolean match(StateContainer<?, S> p_227199_1_, S p_227199_2_) {
         Property<?> property = p_227199_1_.getProperty(this.name);
         return property == null ? false : this.match(p_227199_2_, property);
      }

      protected abstract <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_225554_1_, Property<T> p_225554_2_);

      public abstract JsonElement toJson();

      public String getName() {
         return this.name;
      }

      public void checkState(StateContainer<?, ?> p_227200_1_, Consumer<String> p_227200_2_) {
         Property<?> property = p_227200_1_.getProperty(this.name);
         if (property == null) {
            p_227200_2_.accept(this.name);
         }

      }
   }

   static class RangedMacher extends StatePropertiesPredicate.Matcher {
      @Nullable
      private final String minValue;
      @Nullable
      private final String maxValue;

      public RangedMacher(String p_i225794_1_, @Nullable String p_i225794_2_, @Nullable String p_i225794_3_) {
         super(p_i225794_1_);
         this.minValue = p_i225794_2_;
         this.maxValue = p_i225794_3_;
      }

      protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_225554_1_, Property<T> p_225554_2_) {
         T t = p_225554_1_.getValue(p_225554_2_);
         if (this.minValue != null) {
            Optional<T> optional = p_225554_2_.getValue(this.minValue);
            if (!optional.isPresent() || t.compareTo(optional.get()) < 0) {
               return false;
            }
         }

         if (this.maxValue != null) {
            Optional<T> optional1 = p_225554_2_.getValue(this.maxValue);
            if (!optional1.isPresent() || t.compareTo(optional1.get()) > 0) {
               return false;
            }
         }

         return true;
      }

      public JsonElement toJson() {
         JsonObject jsonobject = new JsonObject();
         if (this.minValue != null) {
            jsonobject.addProperty("min", this.minValue);
         }

         if (this.maxValue != null) {
            jsonobject.addProperty("max", this.maxValue);
         }

         return jsonobject;
      }
   }
}
