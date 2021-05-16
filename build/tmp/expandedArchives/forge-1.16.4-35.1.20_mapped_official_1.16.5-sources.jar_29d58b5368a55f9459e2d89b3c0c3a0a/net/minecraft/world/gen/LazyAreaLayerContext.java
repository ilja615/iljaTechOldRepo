package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.FastRandom;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public class LazyAreaLayerContext implements IExtendedNoiseRandom<LazyArea> {
   private final Long2IntLinkedOpenHashMap cache;
   private final int maxCache;
   private final ImprovedNoiseGenerator biomeNoise;
   private final long seed;
   private long rval;

   public LazyAreaLayerContext(int p_i51285_1_, long p_i51285_2_, long p_i51285_4_) {
      this.seed = mixSeed(p_i51285_2_, p_i51285_4_);
      this.biomeNoise = new ImprovedNoiseGenerator(new Random(p_i51285_2_));
      this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
      this.cache.defaultReturnValue(Integer.MIN_VALUE);
      this.maxCache = p_i51285_1_;
   }

   public LazyArea createResult(IPixelTransformer p_212861_1_) {
      return new LazyArea(this.cache, this.maxCache, p_212861_1_);
   }

   public LazyArea createResult(IPixelTransformer p_212859_1_, LazyArea p_212859_2_) {
      return new LazyArea(this.cache, Math.min(1024, p_212859_2_.getMaxCache() * 4), p_212859_1_);
   }

   public LazyArea createResult(IPixelTransformer p_212860_1_, LazyArea p_212860_2_, LazyArea p_212860_3_) {
      return new LazyArea(this.cache, Math.min(1024, Math.max(p_212860_2_.getMaxCache(), p_212860_3_.getMaxCache()) * 4), p_212860_1_);
   }

   public void initRandom(long p_202698_1_, long p_202698_3_) {
      long i = this.seed;
      i = FastRandom.next(i, p_202698_1_);
      i = FastRandom.next(i, p_202698_3_);
      i = FastRandom.next(i, p_202698_1_);
      i = FastRandom.next(i, p_202698_3_);
      this.rval = i;
   }

   public int nextRandom(int p_202696_1_) {
      int i = (int)Math.floorMod(this.rval >> 24, (long)p_202696_1_);
      this.rval = FastRandom.next(this.rval, this.seed);
      return i;
   }

   public ImprovedNoiseGenerator getBiomeNoise() {
      return this.biomeNoise;
   }

   private static long mixSeed(long p_227471_0_, long p_227471_2_) {
      long lvt_4_1_ = FastRandom.next(p_227471_2_, p_227471_2_);
      lvt_4_1_ = FastRandom.next(lvt_4_1_, p_227471_2_);
      lvt_4_1_ = FastRandom.next(lvt_4_1_, p_227471_2_);
      long lvt_6_1_ = FastRandom.next(p_227471_0_, lvt_4_1_);
      lvt_6_1_ = FastRandom.next(lvt_6_1_, lvt_4_1_);
      return FastRandom.next(lvt_6_1_, lvt_4_1_);
   }
}
