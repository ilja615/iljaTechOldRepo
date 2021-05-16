package net.minecraft.state;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;

public class BooleanProperty extends Property<Boolean> {
   private final ImmutableSet<Boolean> values = ImmutableSet.of(true, false);

   protected BooleanProperty(String p_i45651_1_) {
      super(p_i45651_1_, Boolean.class);
   }

   public Collection<Boolean> getPossibleValues() {
      return this.values;
   }

   public static BooleanProperty create(String p_177716_0_) {
      return new BooleanProperty(p_177716_0_);
   }

   public Optional<Boolean> getValue(String p_185929_1_) {
      return !"true".equals(p_185929_1_) && !"false".equals(p_185929_1_) ? Optional.empty() : Optional.of(Boolean.valueOf(p_185929_1_));
   }

   public String getName(Boolean p_177702_1_) {
      return p_177702_1_.toString();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof BooleanProperty && super.equals(p_equals_1_)) {
         BooleanProperty booleanproperty = (BooleanProperty)p_equals_1_;
         return this.values.equals(booleanproperty.values);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }
}
