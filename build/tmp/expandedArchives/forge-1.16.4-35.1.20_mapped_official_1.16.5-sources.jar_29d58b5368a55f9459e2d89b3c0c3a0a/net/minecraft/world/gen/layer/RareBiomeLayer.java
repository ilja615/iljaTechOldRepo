package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum RareBiomeLayer implements IC1Transformer {
   INSTANCE;

   public int apply(INoiseRandom p_202716_1_, int p_202716_2_) {
      return p_202716_1_.nextRandom(57) == 0 && p_202716_2_ == 1 ? 129 : p_202716_2_;
   }
}
