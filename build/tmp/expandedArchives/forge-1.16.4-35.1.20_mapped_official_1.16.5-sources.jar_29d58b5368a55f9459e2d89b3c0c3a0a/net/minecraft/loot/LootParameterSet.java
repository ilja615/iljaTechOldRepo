package net.minecraft.loot;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;

public class LootParameterSet {
   private final Set<LootParameter<?>> required;
   private final Set<LootParameter<?>> all;

   private LootParameterSet(Set<LootParameter<?>> p_i51211_1_, Set<LootParameter<?>> p_i51211_2_) {
      this.required = ImmutableSet.copyOf(p_i51211_1_);
      this.all = ImmutableSet.copyOf(Sets.union(p_i51211_1_, p_i51211_2_));
   }

   public Set<LootParameter<?>> getRequired() {
      return this.required;
   }

   public Set<LootParameter<?>> getAllowed() {
      return this.all;
   }

   public String toString() {
      return "[" + Joiner.on(", ").join(this.all.stream().map((p_216275_1_) -> {
         return (this.required.contains(p_216275_1_) ? "!" : "") + p_216275_1_.getName();
      }).iterator()) + "]";
   }

   public void validateUser(ValidationTracker p_227556_1_, IParameterized p_227556_2_) {
      Set<LootParameter<?>> set = p_227556_2_.getReferencedContextParams();
      Set<LootParameter<?>> set1 = Sets.difference(set, this.all);
      if (!set1.isEmpty()) {
         p_227556_1_.reportProblem("Parameters " + set1 + " are not provided in this context");
      }

   }

   public static class Builder {
      private final Set<LootParameter<?>> required = Sets.newIdentityHashSet();
      private final Set<LootParameter<?>> optional = Sets.newIdentityHashSet();

      public LootParameterSet.Builder required(LootParameter<?> p_216269_1_) {
         if (this.optional.contains(p_216269_1_)) {
            throw new IllegalArgumentException("Parameter " + p_216269_1_.getName() + " is already optional");
         } else {
            this.required.add(p_216269_1_);
            return this;
         }
      }

      public LootParameterSet.Builder optional(LootParameter<?> p_216271_1_) {
         if (this.required.contains(p_216271_1_)) {
            throw new IllegalArgumentException("Parameter " + p_216271_1_.getName() + " is already required");
         } else {
            this.optional.add(p_216271_1_);
            return this;
         }
      }

      public LootParameterSet build() {
         return new LootParameterSet(this.required, this.optional);
      }
   }
}
