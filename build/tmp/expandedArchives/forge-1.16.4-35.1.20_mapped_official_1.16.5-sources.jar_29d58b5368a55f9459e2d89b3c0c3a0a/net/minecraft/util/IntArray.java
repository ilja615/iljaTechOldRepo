package net.minecraft.util;

public class IntArray implements IIntArray {
   private final int[] ints;

   public IntArray(int p_i50063_1_) {
      this.ints = new int[p_i50063_1_];
   }

   public int get(int p_221476_1_) {
      return this.ints[p_221476_1_];
   }

   public void set(int p_221477_1_, int p_221477_2_) {
      this.ints[p_221477_1_] = p_221477_2_;
   }

   public int getCount() {
      return this.ints.length;
   }
}
