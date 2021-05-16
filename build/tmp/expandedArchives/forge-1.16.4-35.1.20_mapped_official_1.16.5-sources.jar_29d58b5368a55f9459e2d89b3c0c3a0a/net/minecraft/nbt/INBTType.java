package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface INBTType<T extends INBT> {
   T load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException;

   default boolean isValue() {
      return false;
   }

   String getName();

   String getPrettyName();

   static INBTType<EndNBT> createInvalid(final int p_229707_0_) {
      return new INBTType<EndNBT>() {
         public EndNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
            throw new IllegalArgumentException("Invalid tag id: " + p_229707_0_);
         }

         public String getName() {
            return "INVALID[" + p_229707_0_ + "]";
         }

         public String getPrettyName() {
            return "UNKNOWN_" + p_229707_0_;
         }
      };
   }
}
