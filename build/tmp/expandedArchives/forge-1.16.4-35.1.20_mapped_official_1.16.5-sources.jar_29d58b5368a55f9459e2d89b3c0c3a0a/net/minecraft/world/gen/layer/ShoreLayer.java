package net.minecraft.world.gen.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum ShoreLayer implements ICastleTransformer {
   INSTANCE;

   private static final IntSet SNOWY = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
   private static final IntSet JUNGLES = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});

   public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      if (p_202748_6_ == 14) {
         if (LayerUtil.isShallowOcean(p_202748_2_) || LayerUtil.isShallowOcean(p_202748_3_) || LayerUtil.isShallowOcean(p_202748_4_) || LayerUtil.isShallowOcean(p_202748_5_)) {
            return 15;
         }
      } else if (JUNGLES.contains(p_202748_6_)) {
         if (!isJungleCompatible(p_202748_2_) || !isJungleCompatible(p_202748_3_) || !isJungleCompatible(p_202748_4_) || !isJungleCompatible(p_202748_5_)) {
            return 23;
         }

         if (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_)) {
            return 16;
         }
      } else if (p_202748_6_ != 3 && p_202748_6_ != 34 && p_202748_6_ != 20) {
         if (SNOWY.contains(p_202748_6_)) {
            if (!LayerUtil.isOcean(p_202748_6_) && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
               return 26;
            }
         } else if (p_202748_6_ != 37 && p_202748_6_ != 38) {
            if (!LayerUtil.isOcean(p_202748_6_) && p_202748_6_ != 7 && p_202748_6_ != 6 && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
               return 16;
            }
         } else if (!LayerUtil.isOcean(p_202748_2_) && !LayerUtil.isOcean(p_202748_3_) && !LayerUtil.isOcean(p_202748_4_) && !LayerUtil.isOcean(p_202748_5_) && (!this.isMesa(p_202748_2_) || !this.isMesa(p_202748_3_) || !this.isMesa(p_202748_4_) || !this.isMesa(p_202748_5_))) {
            return 2;
         }
      } else if (!LayerUtil.isOcean(p_202748_6_) && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
         return 25;
      }

      return p_202748_6_;
   }

   private static boolean isJungleCompatible(int p_151631_0_) {
      return JUNGLES.contains(p_151631_0_) || p_151631_0_ == 4 || p_151631_0_ == 5 || LayerUtil.isOcean(p_151631_0_);
   }

   private boolean isMesa(int p_151633_1_) {
      return p_151633_1_ == 37 || p_151633_1_ == 38 || p_151633_1_ == 39 || p_151633_1_ == 165 || p_151633_1_ == 166 || p_151633_1_ == 167;
   }
}
