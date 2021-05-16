package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class StateHolder<O, S> {
   private static final Function<Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Entry<Property<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<Property<?>, Comparable<?>> p_apply_1_) {
         if (p_apply_1_ == null) {
            return "<NULL>";
         } else {
            Property<?> property = p_apply_1_.getKey();
            return property.getName() + "=" + this.getName(property, p_apply_1_.getValue());
         }
      }

      private <T extends Comparable<T>> String getName(Property<T> p_235905_1_, Comparable<?> p_235905_2_) {
         return p_235905_1_.getName((T)p_235905_2_);
      }
   };
   protected final O owner;
   private final ImmutableMap<Property<?>, Comparable<?>> values;
   private Table<Property<?>, Comparable<?>, S> neighbours;
   protected final MapCodec<S> propertiesCodec;

   protected StateHolder(O p_i231879_1_, ImmutableMap<Property<?>, Comparable<?>> p_i231879_2_, MapCodec<S> p_i231879_3_) {
      this.owner = p_i231879_1_;
      this.values = p_i231879_2_;
      this.propertiesCodec = p_i231879_3_;
   }

   public <T extends Comparable<T>> S cycle(Property<T> p_235896_1_) {
      return this.setValue(p_235896_1_, findNextInCollection(p_235896_1_.getPossibleValues(), this.getValue(p_235896_1_)));
   }

   protected static <T> T findNextInCollection(Collection<T> p_235898_0_, T p_235898_1_) {
      Iterator<T> iterator = p_235898_0_.iterator();

      while(iterator.hasNext()) {
         if (iterator.next().equals(p_235898_1_)) {
            if (iterator.hasNext()) {
               return iterator.next();
            }

            return p_235898_0_.iterator().next();
         }
      }

      return iterator.next();
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(this.owner);
      if (!this.getValues().isEmpty()) {
         stringbuilder.append('[');
         stringbuilder.append(this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   public Collection<Property<?>> getProperties() {
      return Collections.unmodifiableCollection(this.values.keySet());
   }

   public <T extends Comparable<T>> boolean hasProperty(Property<T> p_235901_1_) {
      return this.values.containsKey(p_235901_1_);
   }

   public <T extends Comparable<T>> T getValue(Property<T> p_177229_1_) {
      Comparable<?> comparable = this.values.get(p_177229_1_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot get property " + p_177229_1_ + " as it does not exist in " + this.owner);
      } else {
         return p_177229_1_.getValueClass().cast(comparable);
      }
   }

   public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> p_235903_1_) {
      Comparable<?> comparable = this.values.get(p_235903_1_);
      return comparable == null ? Optional.empty() : Optional.of(p_235903_1_.getValueClass().cast(comparable));
   }

   public <T extends Comparable<T>, V extends T> S setValue(Property<T> p_206870_1_, V p_206870_2_) {
      Comparable<?> comparable = this.values.get(p_206870_1_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " as it does not exist in " + this.owner);
      } else if (comparable == p_206870_2_) {
         return (S)this;
      } else {
         S s = this.neighbours.get(p_206870_1_, p_206870_2_);
         if (s == null) {
            throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " to " + p_206870_2_ + " on " + this.owner + ", it is not an allowed value");
         } else {
            return s;
         }
      }
   }

   public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, S> p_235899_1_) {
      if (this.neighbours != null) {
         throw new IllegalStateException();
      } else {
         Table<Property<?>, Comparable<?>, S> table = HashBasedTable.create();

         for(Entry<Property<?>, Comparable<?>> entry : this.values.entrySet()) {
            Property<?> property = entry.getKey();

            for(Comparable<?> comparable : property.getPossibleValues()) {
               if (comparable != entry.getValue()) {
                  table.put(property, comparable, p_235899_1_.get(this.makeNeighbourValues(property, comparable)));
               }
            }
         }

         this.neighbours = (Table<Property<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
      }
   }

   private Map<Property<?>, Comparable<?>> makeNeighbourValues(Property<?> p_235902_1_, Comparable<?> p_235902_2_) {
      Map<Property<?>, Comparable<?>> map = Maps.newHashMap(this.values);
      map.put(p_235902_1_, p_235902_2_);
      return map;
   }

   public ImmutableMap<Property<?>, Comparable<?>> getValues() {
      return this.values;
   }

   protected static <O, S extends StateHolder<O, S>> Codec<S> codec(Codec<O> p_235897_0_, Function<O, S> p_235897_1_) {
      return p_235897_0_.dispatch("Name", (p_235895_0_) -> {
         return p_235895_0_.owner;
      }, (p_235900_1_) -> {
         S s = p_235897_1_.apply(p_235900_1_);
         return s.getValues().isEmpty() ? Codec.unit(s) : s.propertiesCodec.fieldOf("Properties").codec();
      });
   }
}
