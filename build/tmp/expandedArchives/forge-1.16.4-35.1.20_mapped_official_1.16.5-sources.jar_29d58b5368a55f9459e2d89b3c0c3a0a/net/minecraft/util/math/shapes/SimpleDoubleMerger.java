package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class SimpleDoubleMerger implements IDoubleListMerger {
   private final DoubleList coords;

   public SimpleDoubleMerger(DoubleList p_i49559_1_) {
      this.coords = p_i49559_1_;
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      for(int i = 0; i <= this.coords.size(); ++i) {
         if (!p_197855_1_.merge(i, i, i)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.coords;
   }
}
