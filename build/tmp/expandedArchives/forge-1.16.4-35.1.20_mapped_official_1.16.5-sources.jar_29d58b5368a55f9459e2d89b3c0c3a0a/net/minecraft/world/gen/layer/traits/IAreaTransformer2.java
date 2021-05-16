package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer2 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> run(IExtendedNoiseRandom<R> p_202707_1_, IAreaFactory<R> p_202707_2_, IAreaFactory<R> p_202707_3_) {
      return () -> {
         R r = p_202707_2_.make();
         R r1 = p_202707_3_.make();
         return p_202707_1_.createResult((p_215724_4_, p_215724_5_) -> {
            p_202707_1_.initRandom((long)p_215724_4_, (long)p_215724_5_);
            return this.applyPixel(p_202707_1_, r, r1, p_215724_4_, p_215724_5_);
         }, r, r1);
      };
   }

   int applyPixel(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_);
}
