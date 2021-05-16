package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Property<T extends Comparable<T>> {
   private final Class<T> clazz;
   private final String name;
   private Integer hashCode;
   private final Codec<T> codec = Codec.STRING.comapFlatMap((p_235919_1_) -> {
      return this.getValue(p_235919_1_).map(DataResult::success).orElseGet(() -> {
         return DataResult.error("Unable to read property: " + this + " with value: " + p_235919_1_);
      });
   }, this::getName);
   private final Codec<Property.ValuePair<T>> valueCodec = this.codec.xmap(this::value, Property.ValuePair::value);

   protected Property(String p_i45652_1_, Class<T> p_i45652_2_) {
      this.clazz = p_i45652_2_;
      this.name = p_i45652_1_;
   }

   public Property.ValuePair<T> value(T p_241490_1_) {
      return new Property.ValuePair<>(this, p_241490_1_);
   }

   public Property.ValuePair<T> value(StateHolder<?, ?> p_241489_1_) {
      return new Property.ValuePair<>(this, p_241489_1_.getValue(this));
   }

   public Stream<Property.ValuePair<T>> getAllValues() {
      return this.getPossibleValues().stream().map(this::value);
   }

   public Codec<Property.ValuePair<T>> valueCodec() {
      return this.valueCodec;
   }

   public String getName() {
      return this.name;
   }

   public Class<T> getValueClass() {
      return this.clazz;
   }

   public abstract Collection<T> getPossibleValues();

   public abstract String getName(T p_177702_1_);

   public abstract Optional<T> getValue(String p_185929_1_);

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Property)) {
         return false;
      } else {
         Property<?> property = (Property)p_equals_1_;
         return this.clazz.equals(property.clazz) && this.name.equals(property.name);
      }
   }

   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.generateHashCode();
      }

      return this.hashCode;
   }

   public int generateHashCode() {
      return 31 * this.clazz.hashCode() + this.name.hashCode();
   }

   public static final class ValuePair<T extends Comparable<T>> {
      private final Property<T> property;
      private final T value;

      private ValuePair(Property<T> p_i232540_1_, T p_i232540_2_) {
         if (!p_i232540_1_.getPossibleValues().contains(p_i232540_2_)) {
            throw new IllegalArgumentException("Value " + p_i232540_2_ + " does not belong to property " + p_i232540_1_);
         } else {
            this.property = p_i232540_1_;
            this.value = p_i232540_2_;
         }
      }

      public Property<T> getProperty() {
         return this.property;
      }

      public T value() {
         return this.value;
      }

      public String toString() {
         return this.property.getName() + "=" + this.property.getName(this.value);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof Property.ValuePair)) {
            return false;
         } else {
            Property.ValuePair<?> valuepair = (Property.ValuePair)p_equals_1_;
            return this.property == valuepair.property && this.value.equals(valuepair.value);
         }
      }

      public int hashCode() {
         int i = this.property.hashCode();
         return 31 * i + this.value.hashCode();
      }
   }
}
