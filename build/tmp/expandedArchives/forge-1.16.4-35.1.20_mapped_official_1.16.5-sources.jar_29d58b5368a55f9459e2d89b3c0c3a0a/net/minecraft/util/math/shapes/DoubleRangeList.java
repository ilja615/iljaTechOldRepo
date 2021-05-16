package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class DoubleRangeList extends AbstractDoubleList {
   private final int parts;

   DoubleRangeList(int p_i47689_1_) {
      this.parts = p_i47689_1_;
   }

   public double getDouble(int p_getDouble_1_) {
      return (double)p_getDouble_1_ / (double)this.parts;
   }

   public int size() {
      return this.parts + 1;
   }
}
