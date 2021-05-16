package net.minecraft.tags;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;

public class Tag<T> implements ITag<T> {
   private final ImmutableList<T> valuesList;
   private final Set<T> values;
   @VisibleForTesting
   protected final Class<?> closestCommonSuperType;

   protected Tag(Set<T> p_i241226_1_, Class<?> p_i241226_2_) {
      this.closestCommonSuperType = p_i241226_2_;
      this.values = p_i241226_1_;
      this.valuesList = ImmutableList.copyOf(p_i241226_1_);
   }

   public static <T> Tag<T> empty() {
      return new Tag<>(ImmutableSet.of(), Void.class);
   }

   public static <T> Tag<T> create(Set<T> p_241286_0_) {
      return new Tag<>(p_241286_0_, findCommonSuperClass(p_241286_0_));
   }

   public boolean contains(T p_230235_1_) {
      return this.closestCommonSuperType.isInstance(p_230235_1_) && this.values.contains(p_230235_1_);
   }

   public List<T> getValues() {
      return this.valuesList;
   }

   private static <T> Class<?> findCommonSuperClass(Set<T> p_241287_0_) {
      if (p_241287_0_.isEmpty()) {
         return Void.class;
      } else {
         Class<?> oclass = null;

         for(T t : p_241287_0_) {
            if (oclass == null) {
               oclass = t.getClass();
            } else {
               oclass = findClosestAncestor(oclass, t.getClass());
            }
         }

         return oclass;
      }
   }

   private static Class<?> findClosestAncestor(Class<?> p_241285_0_, Class<?> p_241285_1_) {
      while(!p_241285_0_.isAssignableFrom(p_241285_1_)) {
         p_241285_0_ = p_241285_0_.getSuperclass();
      }

      return p_241285_0_;
   }
}
