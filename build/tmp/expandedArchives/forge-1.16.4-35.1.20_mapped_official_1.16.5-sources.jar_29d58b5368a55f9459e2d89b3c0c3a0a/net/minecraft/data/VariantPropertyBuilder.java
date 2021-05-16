package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.state.Property;

public final class VariantPropertyBuilder {
   private static final VariantPropertyBuilder EMPTY = new VariantPropertyBuilder(ImmutableList.of());
   private static final Comparator<Property.ValuePair<?>> COMPARE_BY_NAME = Comparator.comparing((p_240192_0_) -> {
      return p_240192_0_.getProperty().getName();
   });
   private final List<Property.ValuePair<?>> values;

   public VariantPropertyBuilder extend(Property.ValuePair<?> p_240188_1_) {
      return new VariantPropertyBuilder(ImmutableList.<Property.ValuePair<?>>builder().addAll(this.values).add(p_240188_1_).build());
   }

   public VariantPropertyBuilder extend(VariantPropertyBuilder p_240189_1_) {
      return new VariantPropertyBuilder(ImmutableList.<Property.ValuePair<?>>builder().addAll(this.values).addAll(p_240189_1_.values).build());
   }

   private VariantPropertyBuilder(List<Property.ValuePair<?>> p_i232541_1_) {
      this.values = p_i232541_1_;
   }

   public static VariantPropertyBuilder empty() {
      return EMPTY;
   }

   public static VariantPropertyBuilder of(Property.ValuePair<?>... p_240190_0_) {
      return new VariantPropertyBuilder(ImmutableList.copyOf(p_240190_0_));
   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_ || p_equals_1_ instanceof VariantPropertyBuilder && this.values.equals(((VariantPropertyBuilder)p_equals_1_).values);
   }

   public int hashCode() {
      return this.values.hashCode();
   }

   public String getKey() {
      return this.values.stream().sorted(COMPARE_BY_NAME).map(Property.ValuePair::toString).collect(Collectors.joining(","));
   }

   public String toString() {
      return this.getKey();
   }
}
