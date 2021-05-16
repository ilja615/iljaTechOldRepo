package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum EdgeBiomeLayer implements ICastleTransformer {
   INSTANCE;

   public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      int[] aint = new int[1];
      if (!this.checkEdge(aint, p_202748_6_) && !this.checkEdgeStrict(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, 38, 37) && !this.checkEdgeStrict(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, 39, 37) && !this.checkEdgeStrict(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, 32, 5)) {
         if (p_202748_6_ != 2 || p_202748_2_ != 12 && p_202748_3_ != 12 && p_202748_5_ != 12 && p_202748_4_ != 12) {
            if (p_202748_6_ == 6) {
               if (p_202748_2_ == 2 || p_202748_3_ == 2 || p_202748_5_ == 2 || p_202748_4_ == 2 || p_202748_2_ == 30 || p_202748_3_ == 30 || p_202748_5_ == 30 || p_202748_4_ == 30 || p_202748_2_ == 12 || p_202748_3_ == 12 || p_202748_5_ == 12 || p_202748_4_ == 12) {
                  return 1;
               }

               if (p_202748_2_ == 21 || p_202748_4_ == 21 || p_202748_3_ == 21 || p_202748_5_ == 21 || p_202748_2_ == 168 || p_202748_4_ == 168 || p_202748_3_ == 168 || p_202748_5_ == 168) {
                  return 23;
               }
            }

            return p_202748_6_;
         } else {
            return 34;
         }
      } else {
         return aint[0];
      }
   }

   private boolean checkEdge(int[] p_242935_1_, int p_242935_2_) {
      if (!LayerUtil.isSame(p_242935_2_, 3)) {
         return false;
      } else {
         p_242935_1_[0] = p_242935_2_;
         return true;
      }
   }

   private boolean checkEdgeStrict(int[] p_151635_1_, int p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_) {
      if (p_151635_6_ != p_151635_7_) {
         return false;
      } else {
         if (LayerUtil.isSame(p_151635_2_, p_151635_7_) && LayerUtil.isSame(p_151635_3_, p_151635_7_) && LayerUtil.isSame(p_151635_5_, p_151635_7_) && LayerUtil.isSame(p_151635_4_, p_151635_7_)) {
            p_151635_1_[0] = p_151635_6_;
         } else {
            p_151635_1_[0] = p_151635_8_;
         }

         return true;
      }
   }
}
