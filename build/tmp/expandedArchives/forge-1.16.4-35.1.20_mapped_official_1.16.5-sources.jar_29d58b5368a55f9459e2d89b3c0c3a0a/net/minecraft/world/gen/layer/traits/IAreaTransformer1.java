package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer1 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> run(IExtendedNoiseRandom<R> p_202713_1_, IAreaFactory<R> p_202713_2_) {
      return () -> {
         R r = p_202713_2_.make();
         return p_202713_1_.createResult((p_202711_3_, p_202711_4_) -> {
            p_202713_1_.initRandom((long)p_202711_3_, (long)p_202711_4_);
            return this.applyPixel(p_202713_1_, r, p_202711_3_, p_202711_4_);
         }, r);
      };
   }

   int applyPixel(IExtendedNoiseRandom<?> p_215728_1_, IArea p_215728_2_, int p_215728_3_, int p_215728_4_);
}
