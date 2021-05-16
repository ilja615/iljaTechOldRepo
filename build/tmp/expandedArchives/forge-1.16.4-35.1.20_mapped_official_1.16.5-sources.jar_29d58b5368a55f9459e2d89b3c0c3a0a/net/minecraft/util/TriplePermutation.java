package net.minecraft.util;

import java.util.Arrays;
import net.minecraft.util.math.vector.Matrix3f;

public enum TriplePermutation {
   P123(0, 1, 2),
   P213(1, 0, 2),
   P132(0, 2, 1),
   P231(1, 2, 0),
   P312(2, 0, 1),
   P321(2, 1, 0);

   private final int[] permutation;
   private final Matrix3f transformation;
   private static final TriplePermutation[][] cayleyTable = Util.make(new TriplePermutation[values().length][values().length], (p_239190_0_) -> {
      for(TriplePermutation triplepermutation : values()) {
         for(TriplePermutation triplepermutation1 : values()) {
            int[] aint = new int[3];

            for(int i = 0; i < 3; ++i) {
               aint[i] = triplepermutation.permutation[triplepermutation1.permutation[i]];
            }

            TriplePermutation triplepermutation2 = Arrays.stream(values()).filter((p_239189_1_) -> {
               return Arrays.equals(p_239189_1_.permutation, aint);
            }).findFirst().get();
            p_239190_0_[triplepermutation.ordinal()][triplepermutation1.ordinal()] = triplepermutation2;
         }
      }

   });

   private TriplePermutation(int p_i232416_3_, int p_i232416_4_, int p_i232416_5_) {
      this.permutation = new int[]{p_i232416_3_, p_i232416_4_, p_i232416_5_};
      this.transformation = new Matrix3f();
      this.transformation.set(0, this.permutation(0), 1.0F);
      this.transformation.set(1, this.permutation(1), 1.0F);
      this.transformation.set(2, this.permutation(2), 1.0F);
   }

   public TriplePermutation compose(TriplePermutation p_239188_1_) {
      return cayleyTable[this.ordinal()][p_239188_1_.ordinal()];
   }

   public int permutation(int p_239187_1_) {
      return this.permutation[p_239187_1_];
   }

   public Matrix3f transformation() {
      return this.transformation;
   }
}
