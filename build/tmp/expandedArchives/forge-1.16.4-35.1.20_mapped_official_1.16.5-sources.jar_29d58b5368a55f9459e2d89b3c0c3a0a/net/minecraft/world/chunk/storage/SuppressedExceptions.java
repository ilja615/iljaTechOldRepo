package net.minecraft.world.chunk.storage;

import javax.annotation.Nullable;

public class SuppressedExceptions<T extends Throwable> {
   @Nullable
   private T result;

   public void add(T p_233003_1_) {
      if (this.result == null) {
         this.result = p_233003_1_;
      } else {
         this.result.addSuppressed(p_233003_1_);
      }

   }

   public void throwIfPresent() throws T {
      if (this.result != null) {
         throw this.result;
      }
   }
}
