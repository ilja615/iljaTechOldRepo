package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddSnowLayer implements IC1Transformer {
   INSTANCE;

   public int apply(INoiseRandom p_202716_1_, int p_202716_2_) {
      if (LayerUtil.isShallowOcean(p_202716_2_)) {
         return p_202716_2_;
      } else {
         int i = p_202716_1_.nextRandom(6);
         if (i == 0) {
            return 4;
         } else {
            return i == 1 ? 3 : 1;
         }
      }
   }
}
