package net.minecraft.profiler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class DataPoint implements Comparable<DataPoint> {
   public final double percentage;
   public final double globalPercentage;
   public final long count;
   public final String name;

   public DataPoint(String p_i51527_1_, double p_i51527_2_, double p_i51527_4_, long p_i51527_6_) {
      this.name = p_i51527_1_;
      this.percentage = p_i51527_2_;
      this.globalPercentage = p_i51527_4_;
      this.count = p_i51527_6_;
   }

   public int compareTo(DataPoint p_compareTo_1_) {
      if (p_compareTo_1_.percentage < this.percentage) {
         return -1;
      } else {
         return p_compareTo_1_.percentage > this.percentage ? 1 : p_compareTo_1_.name.compareTo(this.name);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getColor() {
      return (this.name.hashCode() & 11184810) + 4473924;
   }
}
