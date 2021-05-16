package net.minecraft.world.chunk.storage;

import java.util.BitSet;

public class RegionBitmap {
   private final BitSet used = new BitSet();

   public void force(int p_227120_1_, int p_227120_2_) {
      this.used.set(p_227120_1_, p_227120_1_ + p_227120_2_);
   }

   public void free(int p_227121_1_, int p_227121_2_) {
      this.used.clear(p_227121_1_, p_227121_1_ + p_227121_2_);
   }

   public int allocate(int p_227119_1_) {
      int i = 0;

      while(true) {
         int j = this.used.nextClearBit(i);
         int k = this.used.nextSetBit(j);
         if (k == -1 || k - j >= p_227119_1_) {
            this.force(j, p_227119_1_);
            return j;
         }

         i = k;
      }
   }
}
