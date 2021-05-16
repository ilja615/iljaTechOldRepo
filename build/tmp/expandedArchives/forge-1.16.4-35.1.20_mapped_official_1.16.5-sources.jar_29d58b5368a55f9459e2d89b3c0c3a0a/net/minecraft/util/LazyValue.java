package net.minecraft.util;

import java.util.function.Supplier;

public class LazyValue<T> {
   private Supplier<T> factory;
   private T value;

   public LazyValue(Supplier<T> p_i48587_1_) {
      this.factory = p_i48587_1_;
   }

   public T get() {
      Supplier<T> supplier = this.factory;
      if (supplier != null) {
         this.value = supplier.get();
         this.factory = null;
      }

      return this.value;
   }
}
