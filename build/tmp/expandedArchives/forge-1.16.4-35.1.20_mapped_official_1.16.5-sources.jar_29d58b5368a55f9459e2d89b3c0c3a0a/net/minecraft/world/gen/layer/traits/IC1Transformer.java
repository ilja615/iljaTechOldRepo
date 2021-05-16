package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;

public interface IC1Transformer extends IAreaTransformer1, IDimOffset1Transformer {
   int apply(INoiseRandom p_202716_1_, int p_202716_2_);

   default int applyPixel(IExtendedNoiseRandom<?> p_215728_1_, IArea p_215728_2_, int p_215728_3_, int p_215728_4_) {
      int i = p_215728_2_.get(this.getParentX(p_215728_3_ + 1), this.getParentY(p_215728_4_ + 1));
      return this.apply(p_215728_1_, i);
   }
}
