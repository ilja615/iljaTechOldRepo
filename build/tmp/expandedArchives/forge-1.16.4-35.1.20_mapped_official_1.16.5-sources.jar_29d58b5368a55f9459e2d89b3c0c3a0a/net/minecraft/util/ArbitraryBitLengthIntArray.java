package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class ArbitraryBitLengthIntArray {
   private final long[] data;
   private final int bits;
   private final long mask;
   private final int size;

   public ArbitraryBitLengthIntArray(int p_i231442_1_, int p_i231442_2_) {
      this(p_i231442_1_, p_i231442_2_, new long[MathHelper.roundUp(p_i231442_2_ * p_i231442_1_, 64) / 64]);
   }

   public ArbitraryBitLengthIntArray(int p_i231443_1_, int p_i231443_2_, long[] p_i231443_3_) {
      Validate.inclusiveBetween(1L, 32L, (long)p_i231443_1_);
      this.size = p_i231443_2_;
      this.bits = p_i231443_1_;
      this.data = p_i231443_3_;
      this.mask = (1L << p_i231443_1_) - 1L;
      int i = MathHelper.roundUp(p_i231443_2_ * p_i231443_1_, 64) / 64;
      if (p_i231443_3_.length != i) {
         throw new IllegalArgumentException("Invalid length given for storage, got: " + p_i231443_3_.length + " but expected: " + i);
      }
   }

   public void set(int p_233049_1_, int p_233049_2_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_233049_1_);
      Validate.inclusiveBetween(0L, this.mask, (long)p_233049_2_);
      int i = p_233049_1_ * this.bits;
      int j = i >> 6;
      int k = (p_233049_1_ + 1) * this.bits - 1 >> 6;
      int l = i ^ j << 6;
      this.data[j] = this.data[j] & ~(this.mask << l) | ((long)p_233049_2_ & this.mask) << l;
      if (j != k) {
         int i1 = 64 - l;
         int j1 = this.bits - i1;
         this.data[k] = this.data[k] >>> j1 << j1 | ((long)p_233049_2_ & this.mask) >> i1;
      }

   }

   public int get(int p_233048_1_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_233048_1_);
      int i = p_233048_1_ * this.bits;
      int j = i >> 6;
      int k = (p_233048_1_ + 1) * this.bits - 1 >> 6;
      int l = i ^ j << 6;
      if (j == k) {
         return (int)(this.data[j] >>> l & this.mask);
      } else {
         int i1 = 64 - l;
         return (int)((this.data[j] >>> l | this.data[k] << i1) & this.mask);
      }
   }

   public long[] getRaw() {
      return this.data;
   }

   public int getBits() {
      return this.bits;
   }
}
