package net.minecraft.client.renderer.color;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorCache {
   private final ThreadLocal<ColorCache.Entry> latestChunkOnThread = ThreadLocal.withInitial(() -> {
      return new ColorCache.Entry();
   });
   private final Long2ObjectLinkedOpenHashMap<int[]> cache = new Long2ObjectLinkedOpenHashMap<>(256, 0.25F);
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

   public int getColor(BlockPos p_228071_1_, IntSupplier p_228071_2_) {
      int i = p_228071_1_.getX() >> 4;
      int j = p_228071_1_.getZ() >> 4;
      ColorCache.Entry colorcache$entry = this.latestChunkOnThread.get();
      if (colorcache$entry.x != i || colorcache$entry.z != j) {
         colorcache$entry.x = i;
         colorcache$entry.z = j;
         colorcache$entry.cache = this.findOrCreateChunkCache(i, j);
      }

      int k = p_228071_1_.getX() & 15;
      int l = p_228071_1_.getZ() & 15;
      int i1 = l << 4 | k;
      int j1 = colorcache$entry.cache[i1];
      if (j1 != -1) {
         return j1;
      } else {
         int k1 = p_228071_2_.getAsInt();
         colorcache$entry.cache[i1] = k1;
         return k1;
      }
   }

   public void invalidateForChunk(int p_228070_1_, int p_228070_2_) {
      try {
         this.lock.writeLock().lock();

         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               long k = ChunkPos.asLong(p_228070_1_ + i, p_228070_2_ + j);
               this.cache.remove(k);
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public void invalidateAll() {
      try {
         this.lock.writeLock().lock();
         this.cache.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   private int[] findOrCreateChunkCache(int p_228073_1_, int p_228073_2_) {
      long i = ChunkPos.asLong(p_228073_1_, p_228073_2_);
      this.lock.readLock().lock();

      int[] aint;
      try {
         aint = this.cache.get(i);
      } finally {
         this.lock.readLock().unlock();
      }

      if (aint != null) {
         return aint;
      } else {
         int[] aint1 = new int[256];
         Arrays.fill(aint1, -1);

         try {
            this.lock.writeLock().lock();
            if (this.cache.size() >= 256) {
               this.cache.removeFirst();
            }

            this.cache.put(i, aint1);
         } finally {
            this.lock.writeLock().unlock();
         }

         return aint1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      public int x = Integer.MIN_VALUE;
      public int z = Integer.MIN_VALUE;
      public int[] cache;

      private Entry() {
      }
   }
}
