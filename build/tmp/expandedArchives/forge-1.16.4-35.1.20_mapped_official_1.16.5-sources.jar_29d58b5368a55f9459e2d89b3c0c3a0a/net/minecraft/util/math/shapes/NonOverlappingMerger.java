package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class NonOverlappingMerger extends AbstractDoubleList implements IDoubleListMerger {
   private final DoubleList lower;
   private final DoubleList upper;
   private final boolean swap;

   public NonOverlappingMerger(DoubleList p_i48187_1_, DoubleList p_i48187_2_, boolean p_i48187_3_) {
      this.lower = p_i48187_1_;
      this.upper = p_i48187_2_;
      this.swap = p_i48187_3_;
   }

   public int size() {
      return this.lower.size() + this.upper.size();
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      return this.swap ? this.forNonSwappedIndexes((p_199636_1_, p_199636_2_, p_199636_3_) -> {
         return p_197855_1_.merge(p_199636_2_, p_199636_1_, p_199636_3_);
      }) : this.forNonSwappedIndexes(p_197855_1_);
   }

   private boolean forNonSwappedIndexes(IDoubleListMerger.IConsumer p_199637_1_) {
      int i = this.lower.size() - 1;

      for(int j = 0; j < i; ++j) {
         if (!p_199637_1_.merge(j, -1, j)) {
            return false;
         }
      }

      if (!p_199637_1_.merge(i, -1, i)) {
         return false;
      } else {
         for(int k = 0; k < this.upper.size(); ++k) {
            if (!p_199637_1_.merge(i, k, i + 1 + k)) {
               return false;
            }
         }

         return true;
      }
   }

   public double getDouble(int p_getDouble_1_) {
      return p_getDouble_1_ < this.lower.size() ? this.lower.getDouble(p_getDouble_1_) : this.upper.getDouble(p_getDouble_1_ - this.lower.size());
   }

   public DoubleList getList() {
      return this;
   }
}
