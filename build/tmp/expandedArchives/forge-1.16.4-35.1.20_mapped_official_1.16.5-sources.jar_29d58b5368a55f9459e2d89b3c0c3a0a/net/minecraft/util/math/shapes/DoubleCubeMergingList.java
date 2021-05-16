package net.minecraft.util.math.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DoubleCubeMergingList implements IDoubleListMerger {
   private final DoubleRangeList result;
   private final int firstSize;
   private final int secondSize;
   private final int gcd;

   DoubleCubeMergingList(int p_i47687_1_, int p_i47687_2_) {
      this.result = new DoubleRangeList((int)VoxelShapes.lcm(p_i47687_1_, p_i47687_2_));
      this.firstSize = p_i47687_1_;
      this.secondSize = p_i47687_2_;
      this.gcd = IntMath.gcd(p_i47687_1_, p_i47687_2_);
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      int i = this.firstSize / this.gcd;
      int j = this.secondSize / this.gcd;

      for(int k = 0; k <= this.result.size(); ++k) {
         if (!p_197855_1_.merge(k / j, k / i, k)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.result;
   }
}
