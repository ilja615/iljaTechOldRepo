package net.minecraft.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public interface IParameterized {
   default Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }

   default void validate(ValidationTracker p_225580_1_) {
      p_225580_1_.validateUser(this);
   }
}
