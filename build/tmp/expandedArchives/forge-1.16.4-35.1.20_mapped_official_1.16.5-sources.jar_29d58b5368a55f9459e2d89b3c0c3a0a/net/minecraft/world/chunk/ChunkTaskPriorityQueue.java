package net.minecraft.world.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ChunkManager;

public class ChunkTaskPriorityQueue<T> {
   public static final int PRIORITY_LEVEL_COUNT = ChunkManager.MAX_CHUNK_DISTANCE + 2;
   private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> taskQueue = IntStream.range(0, PRIORITY_LEVEL_COUNT).mapToObj((p_219415_0_) -> {
      return new Long2ObjectLinkedOpenHashMap<List<Optional<T>>>();
   }).collect(Collectors.toList());
   private volatile int firstQueue = PRIORITY_LEVEL_COUNT;
   private final String name;
   private final LongSet acquired = new LongOpenHashSet();
   private final int maxTasks;

   public ChunkTaskPriorityQueue(String p_i50714_1_, int p_i50714_2_) {
      this.name = p_i50714_1_;
      this.maxTasks = p_i50714_2_;
   }

   protected void resortChunkTasks(int p_219407_1_, ChunkPos p_219407_2_, int p_219407_3_) {
      if (p_219407_1_ < PRIORITY_LEVEL_COUNT) {
         Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap = this.taskQueue.get(p_219407_1_);
         List<Optional<T>> list = long2objectlinkedopenhashmap.remove(p_219407_2_.toLong());
         if (p_219407_1_ == this.firstQueue) {
            while(this.firstQueue < PRIORITY_LEVEL_COUNT && this.taskQueue.get(this.firstQueue).isEmpty()) {
               ++this.firstQueue;
            }
         }

         if (list != null && !list.isEmpty()) {
            this.taskQueue.get(p_219407_3_).computeIfAbsent(p_219407_2_.toLong(), (p_219411_0_) -> {
               return Lists.newArrayList();
            }).addAll(list);
            this.firstQueue = Math.min(this.firstQueue, p_219407_3_);
         }

      }
   }

   protected void submit(Optional<T> p_219412_1_, long p_219412_2_, int p_219412_4_) {
      this.taskQueue.get(p_219412_4_).computeIfAbsent(p_219412_2_, (p_219410_0_) -> {
         return Lists.newArrayList();
      }).add(p_219412_1_);
      this.firstQueue = Math.min(this.firstQueue, p_219412_4_);
   }

   protected void release(long p_219416_1_, boolean p_219416_3_) {
      for(Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap : this.taskQueue) {
         List<Optional<T>> list = long2objectlinkedopenhashmap.get(p_219416_1_);
         if (list != null) {
            if (p_219416_3_) {
               list.clear();
            } else {
               list.removeIf((p_219413_0_) -> {
                  return !p_219413_0_.isPresent();
               });
            }

            if (list.isEmpty()) {
               long2objectlinkedopenhashmap.remove(p_219416_1_);
            }
         }
      }

      while(this.firstQueue < PRIORITY_LEVEL_COUNT && this.taskQueue.get(this.firstQueue).isEmpty()) {
         ++this.firstQueue;
      }

      this.acquired.remove(p_219416_1_);
   }

   private Runnable acquire(long p_219418_1_) {
      return () -> {
         this.acquired.add(p_219418_1_);
      };
   }

   @Nullable
   public Stream<Either<T, Runnable>> pop() {
      if (this.acquired.size() >= this.maxTasks) {
         return null;
      } else if (this.firstQueue >= PRIORITY_LEVEL_COUNT) {
         return null;
      } else {
         int i = this.firstQueue;
         Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap = this.taskQueue.get(i);
         long j = long2objectlinkedopenhashmap.firstLongKey();

         List<Optional<T>> list;
         for(list = long2objectlinkedopenhashmap.removeFirst(); this.firstQueue < PRIORITY_LEVEL_COUNT && this.taskQueue.get(this.firstQueue).isEmpty(); ++this.firstQueue) {
         }

         return list.stream().map((p_219408_3_) -> {
            return p_219408_3_.<Either<T, Runnable>>map(Either::left).orElseGet(() -> {
               return Either.right(this.acquire(j));
            });
         });
      }
   }

   public String toString() {
      return this.name + " " + this.firstQueue + "...";
   }

   @VisibleForTesting
   LongSet getAcquired() {
      return new LongOpenHashSet(this.acquired);
   }
}
