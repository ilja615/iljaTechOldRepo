package net.minecraft.inventory;

import javax.annotation.Nullable;

public interface IClearable {
   void clearContent();

   static void tryClear(@Nullable Object p_213131_0_) {
      if (p_213131_0_ instanceof IClearable) {
         ((IClearable)p_213131_0_).clearContent();
      }

   }
}
