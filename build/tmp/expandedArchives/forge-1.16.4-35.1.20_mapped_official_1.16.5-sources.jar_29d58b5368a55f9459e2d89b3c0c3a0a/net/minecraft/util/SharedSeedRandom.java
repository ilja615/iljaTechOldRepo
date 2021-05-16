package net.minecraft.util;

import java.util.Random;

public class SharedSeedRandom extends Random {
   private int count;

   public SharedSeedRandom() {
   }

   public SharedSeedRandom(long p_i48691_1_) {
      super(p_i48691_1_);
   }

   public void consumeCount(int p_202423_1_) {
      for(int i = 0; i < p_202423_1_; ++i) {
         this.next(1);
      }

   }

   protected int next(int p_next_1_) {
      ++this.count;
      return super.next(p_next_1_);
   }

   public long setBaseChunkSeed(int p_202422_1_, int p_202422_2_) {
      long i = (long)p_202422_1_ * 341873128712L + (long)p_202422_2_ * 132897987541L;
      this.setSeed(i);
      return i;
   }

   public long setDecorationSeed(long p_202424_1_, int p_202424_3_, int p_202424_4_) {
      this.setSeed(p_202424_1_);
      long i = this.nextLong() | 1L;
      long j = this.nextLong() | 1L;
      long k = (long)p_202424_3_ * i + (long)p_202424_4_ * j ^ p_202424_1_;
      this.setSeed(k);
      return k;
   }

   public long setFeatureSeed(long p_202426_1_, int p_202426_3_, int p_202426_4_) {
      long i = p_202426_1_ + (long)p_202426_3_ + (long)(10000 * p_202426_4_);
      this.setSeed(i);
      return i;
   }

   public long setLargeFeatureSeed(long p_202425_1_, int p_202425_3_, int p_202425_4_) {
      this.setSeed(p_202425_1_);
      long i = this.nextLong();
      long j = this.nextLong();
      long k = (long)p_202425_3_ * i ^ (long)p_202425_4_ * j ^ p_202425_1_;
      this.setSeed(k);
      return k;
   }

   public long setLargeFeatureWithSalt(long p_202427_1_, int p_202427_3_, int p_202427_4_, int p_202427_5_) {
      long i = (long)p_202427_3_ * 341873128712L + (long)p_202427_4_ * 132897987541L + p_202427_1_ + (long)p_202427_5_;
      this.setSeed(i);
      return i;
   }

   public static Random seedSlimeChunk(int p_205190_0_, int p_205190_1_, long p_205190_2_, long p_205190_4_) {
      return new Random(p_205190_2_ + (long)(p_205190_0_ * p_205190_0_ * 4987142) + (long)(p_205190_0_ * 5947611) + (long)(p_205190_1_ * p_205190_1_) * 4392871L + (long)(p_205190_1_ * 389711) ^ p_205190_4_);
   }
}
