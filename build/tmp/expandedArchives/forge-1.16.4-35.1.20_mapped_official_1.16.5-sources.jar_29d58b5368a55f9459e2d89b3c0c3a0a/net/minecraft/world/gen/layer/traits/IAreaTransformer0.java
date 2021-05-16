package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer0 {
   default <R extends IArea> IAreaFactory<R> run(IExtendedNoiseRandom<R> p_202823_1_) {
      return () -> {
         return p_202823_1_.createResult((p_202820_2_, p_202820_3_) -> {
            p_202823_1_.initRandom((long)p_202820_2_, (long)p_202820_3_);
            return this.applyPixel(p_202823_1_, p_202820_2_, p_202820_3_);
         });
      };
   }

   int applyPixel(INoiseRandom p_215735_1_, int p_215735_2_, int p_215735_3_);
}
