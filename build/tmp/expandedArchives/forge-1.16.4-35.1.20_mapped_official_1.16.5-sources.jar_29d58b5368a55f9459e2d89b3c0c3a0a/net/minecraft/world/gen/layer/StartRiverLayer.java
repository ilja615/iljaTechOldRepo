package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum StartRiverLayer implements IC0Transformer {
   INSTANCE;

   public int apply(INoiseRandom p_202726_1_, int p_202726_2_) {
      return LayerUtil.isShallowOcean(p_202726_2_) ? p_202726_2_ : p_202726_1_.nextRandom(299999) + 2;
   }
}
