package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum DeepOceanLayer implements ICastleTransformer {
   INSTANCE;

   public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      if (LayerUtil.isShallowOcean(p_202748_6_)) {
         int i = 0;
         if (LayerUtil.isShallowOcean(p_202748_2_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_3_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_5_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_4_)) {
            ++i;
         }

         if (i > 3) {
            if (p_202748_6_ == 44) {
               return 47;
            }

            if (p_202748_6_ == 45) {
               return 48;
            }

            if (p_202748_6_ == 0) {
               return 24;
            }

            if (p_202748_6_ == 46) {
               return 49;
            }

            if (p_202748_6_ == 10) {
               return 50;
            }

            return 24;
         }
      }

      return p_202748_6_;
   }
}
