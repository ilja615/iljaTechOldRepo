package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class IndirectMerger implements IDoubleListMerger {
   private final DoubleArrayList result;
   private final IntArrayList firstIndices;
   private final IntArrayList secondIndices;

   protected IndirectMerger(DoubleList p_i47685_1_, DoubleList p_i47685_2_, boolean p_i47685_3_, boolean p_i47685_4_) {
      int i = 0;
      int j = 0;
      double d0 = Double.NaN;
      int k = p_i47685_1_.size();
      int l = p_i47685_2_.size();
      int i1 = k + l;
      this.result = new DoubleArrayList(i1);
      this.firstIndices = new IntArrayList(i1);
      this.secondIndices = new IntArrayList(i1);

      while(true) {
         boolean flag = i < k;
         boolean flag1 = j < l;
         if (!flag && !flag1) {
            if (this.result.isEmpty()) {
               this.result.add(Math.min(p_i47685_1_.getDouble(k - 1), p_i47685_2_.getDouble(l - 1)));
            }

            return;
         }

         boolean flag2 = flag && (!flag1 || p_i47685_1_.getDouble(i) < p_i47685_2_.getDouble(j) + 1.0E-7D);
         double d1 = flag2 ? p_i47685_1_.getDouble(i++) : p_i47685_2_.getDouble(j++);
         if ((i != 0 && flag || flag2 || p_i47685_4_) && (j != 0 && flag1 || !flag2 || p_i47685_3_)) {
            if (!(d0 >= d1 - 1.0E-7D)) {
               this.firstIndices.add(i - 1);
               this.secondIndices.add(j - 1);
               this.result.add(d1);
               d0 = d1;
            } else if (!this.result.isEmpty()) {
               this.firstIndices.set(this.firstIndices.size() - 1, i - 1);
               this.secondIndices.set(this.secondIndices.size() - 1, j - 1);
            }
         }
      }
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      for(int i = 0; i < this.result.size() - 1; ++i) {
         if (!p_197855_1_.merge(this.firstIndices.getInt(i), this.secondIndices.getInt(i), i)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.result;
   }
}
