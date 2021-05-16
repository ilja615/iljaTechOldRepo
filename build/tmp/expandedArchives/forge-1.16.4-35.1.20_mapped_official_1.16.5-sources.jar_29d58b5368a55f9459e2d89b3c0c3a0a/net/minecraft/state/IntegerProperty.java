package net.minecraft.state;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class IntegerProperty extends Property<Integer> {
   private final ImmutableSet<Integer> values;

   protected IntegerProperty(String p_i45648_1_, int p_i45648_2_, int p_i45648_3_) {
      super(p_i45648_1_, Integer.class);
      if (p_i45648_2_ < 0) {
         throw new IllegalArgumentException("Min value of " + p_i45648_1_ + " must be 0 or greater");
      } else if (p_i45648_3_ <= p_i45648_2_) {
         throw new IllegalArgumentException("Max value of " + p_i45648_1_ + " must be greater than min (" + p_i45648_2_ + ")");
      } else {
         Set<Integer> set = Sets.newHashSet();

         for(int i = p_i45648_2_; i <= p_i45648_3_; ++i) {
            set.add(i);
         }

         this.values = ImmutableSet.copyOf(set);
      }
   }

   public Collection<Integer> getPossibleValues() {
      return this.values;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof IntegerProperty && super.equals(p_equals_1_)) {
         IntegerProperty integerproperty = (IntegerProperty)p_equals_1_;
         return this.values.equals(integerproperty.values);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }

   public static IntegerProperty create(String p_177719_0_, int p_177719_1_, int p_177719_2_) {
      return new IntegerProperty(p_177719_0_, p_177719_1_, p_177719_2_);
   }

   public Optional<Integer> getValue(String p_185929_1_) {
      try {
         Integer integer = Integer.valueOf(p_185929_1_);
         return this.values.contains(integer) ? Optional.of(integer) : Optional.empty();
      } catch (NumberFormatException numberformatexception) {
         return Optional.empty();
      }
   }

   public String getName(Integer p_177702_1_) {
      return p_177702_1_.toString();
   }
}
