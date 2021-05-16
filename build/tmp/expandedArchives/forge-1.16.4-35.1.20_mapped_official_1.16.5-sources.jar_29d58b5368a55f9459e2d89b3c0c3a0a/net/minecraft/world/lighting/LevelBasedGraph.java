package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.function.LongPredicate;
import net.minecraft.util.math.MathHelper;

public abstract class LevelBasedGraph {
   private final int levelCount;
   private final LongLinkedOpenHashSet[] queues;
   private final Long2ByteMap computedLevels;
   private int firstQueuedLevel;
   private volatile boolean hasWork;

   protected LevelBasedGraph(int p_i51298_1_, final int p_i51298_2_, final int p_i51298_3_) {
      if (p_i51298_1_ >= 254) {
         throw new IllegalArgumentException("Level count must be < 254.");
      } else {
         this.levelCount = p_i51298_1_;
         this.queues = new LongLinkedOpenHashSet[p_i51298_1_];

         for(int i = 0; i < p_i51298_1_; ++i) {
            this.queues[i] = new LongLinkedOpenHashSet(p_i51298_2_, 0.5F) {
               protected void rehash(int p_rehash_1_) {
                  if (p_rehash_1_ > p_i51298_2_) {
                     super.rehash(p_rehash_1_);
                  }

               }
            };
         }

         this.computedLevels = new Long2ByteOpenHashMap(p_i51298_3_, 0.5F) {
            protected void rehash(int p_rehash_1_) {
               if (p_rehash_1_ > p_i51298_3_) {
                  super.rehash(p_rehash_1_);
               }

            }
         };
         this.computedLevels.defaultReturnValue((byte)-1);
         this.firstQueuedLevel = p_i51298_1_;
      }
   }

   private int getKey(int p_215482_1_, int p_215482_2_) {
      int i = p_215482_1_;
      if (p_215482_1_ > p_215482_2_) {
         i = p_215482_2_;
      }

      if (i > this.levelCount - 1) {
         i = this.levelCount - 1;
      }

      return i;
   }

   private void checkFirstQueuedLevel(int p_215472_1_) {
      int i = this.firstQueuedLevel;
      this.firstQueuedLevel = p_215472_1_;

      for(int j = i + 1; j < p_215472_1_; ++j) {
         if (!this.queues[j].isEmpty()) {
            this.firstQueuedLevel = j;
            break;
         }
      }

   }

   protected void removeFromQueue(long p_215479_1_) {
      int i = this.computedLevels.get(p_215479_1_) & 255;
      if (i != 255) {
         int j = this.getLevel(p_215479_1_);
         int k = this.getKey(j, i);
         this.dequeue(p_215479_1_, k, this.levelCount, true);
         this.hasWork = this.firstQueuedLevel < this.levelCount;
      }
   }

   public void removeIf(LongPredicate p_227465_1_) {
      LongList longlist = new LongArrayList();
      this.computedLevels.keySet().forEach((long p_229982_2_) -> {
         if (p_227465_1_.test(p_229982_2_)) {
            longlist.add(p_229982_2_);
         }

      });
      longlist.forEach((java.util.function.LongConsumer) this::removeFromQueue);
   }

   private void dequeue(long p_215484_1_, int p_215484_3_, int p_215484_4_, boolean p_215484_5_) {
      if (p_215484_5_) {
         this.computedLevels.remove(p_215484_1_);
      }

      this.queues[p_215484_3_].remove(p_215484_1_);
      if (this.queues[p_215484_3_].isEmpty() && this.firstQueuedLevel == p_215484_3_) {
         this.checkFirstQueuedLevel(p_215484_4_);
      }

   }

   private void enqueue(long p_215470_1_, int p_215470_3_, int p_215470_4_) {
      this.computedLevels.put(p_215470_1_, (byte)p_215470_3_);
      this.queues[p_215470_4_].add(p_215470_1_);
      if (this.firstQueuedLevel > p_215470_4_) {
         this.firstQueuedLevel = p_215470_4_;
      }

   }

   protected void checkNode(long p_215473_1_) {
      this.checkEdge(p_215473_1_, p_215473_1_, this.levelCount - 1, false);
   }

   protected void checkEdge(long p_215469_1_, long p_215469_3_, int p_215469_5_, boolean p_215469_6_) {
      this.checkEdge(p_215469_1_, p_215469_3_, p_215469_5_, this.getLevel(p_215469_3_), this.computedLevels.get(p_215469_3_) & 255, p_215469_6_);
      this.hasWork = this.firstQueuedLevel < this.levelCount;
   }

   private void checkEdge(long p_215474_1_, long p_215474_3_, int p_215474_5_, int p_215474_6_, int p_215474_7_, boolean p_215474_8_) {
      if (!this.isSource(p_215474_3_)) {
         p_215474_5_ = MathHelper.clamp(p_215474_5_, 0, this.levelCount - 1);
         p_215474_6_ = MathHelper.clamp(p_215474_6_, 0, this.levelCount - 1);
         boolean flag;
         if (p_215474_7_ == 255) {
            flag = true;
            p_215474_7_ = p_215474_6_;
         } else {
            flag = false;
         }

         int i;
         if (p_215474_8_) {
            i = Math.min(p_215474_7_, p_215474_5_);
         } else {
            i = MathHelper.clamp(this.getComputedLevel(p_215474_3_, p_215474_1_, p_215474_5_), 0, this.levelCount - 1);
         }

         int j = this.getKey(p_215474_6_, p_215474_7_);
         if (p_215474_6_ != i) {
            int k = this.getKey(p_215474_6_, i);
            if (j != k && !flag) {
               this.dequeue(p_215474_3_, j, k, false);
            }

            this.enqueue(p_215474_3_, i, k);
         } else if (!flag) {
            this.dequeue(p_215474_3_, j, this.levelCount, true);
         }

      }
   }

   protected final void checkNeighbor(long p_215475_1_, long p_215475_3_, int p_215475_5_, boolean p_215475_6_) {
      int i = this.computedLevels.get(p_215475_3_) & 255;
      int j = MathHelper.clamp(this.computeLevelFromNeighbor(p_215475_1_, p_215475_3_, p_215475_5_), 0, this.levelCount - 1);
      if (p_215475_6_) {
         this.checkEdge(p_215475_1_, p_215475_3_, j, this.getLevel(p_215475_3_), i, true);
      } else {
         int k;
         boolean flag;
         if (i == 255) {
            flag = true;
            k = MathHelper.clamp(this.getLevel(p_215475_3_), 0, this.levelCount - 1);
         } else {
            k = i;
            flag = false;
         }

         if (j == k) {
            this.checkEdge(p_215475_1_, p_215475_3_, this.levelCount - 1, flag ? k : this.getLevel(p_215475_3_), i, false);
         }
      }

   }

   protected final boolean hasWork() {
      return this.hasWork;
   }

   protected final int runUpdates(int p_215483_1_) {
      if (this.firstQueuedLevel >= this.levelCount) {
         return p_215483_1_;
      } else {
         while(this.firstQueuedLevel < this.levelCount && p_215483_1_ > 0) {
            --p_215483_1_;
            LongLinkedOpenHashSet longlinkedopenhashset = this.queues[this.firstQueuedLevel];
            long i = longlinkedopenhashset.removeFirstLong();
            int j = MathHelper.clamp(this.getLevel(i), 0, this.levelCount - 1);
            if (longlinkedopenhashset.isEmpty()) {
               this.checkFirstQueuedLevel(this.levelCount);
            }

            int k = this.computedLevels.remove(i) & 255;
            if (k < j) {
               this.setLevel(i, k);
               this.checkNeighborsAfterUpdate(i, k, true);
            } else if (k > j) {
               this.enqueue(i, k, this.getKey(this.levelCount - 1, k));
               this.setLevel(i, this.levelCount - 1);
               this.checkNeighborsAfterUpdate(i, j, false);
            }
         }

         this.hasWork = this.firstQueuedLevel < this.levelCount;
         return p_215483_1_;
      }
   }

   public int getQueueSize() {
      return this.computedLevels.size();
   }

   protected abstract boolean isSource(long p_215485_1_);

   protected abstract int getComputedLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_);

   protected abstract void checkNeighborsAfterUpdate(long p_215478_1_, int p_215478_3_, boolean p_215478_4_);

   protected abstract int getLevel(long p_215471_1_);

   protected abstract void setLevel(long p_215476_1_, int p_215476_3_);

   protected abstract int computeLevelFromNeighbor(long p_215480_1_, long p_215480_3_, int p_215480_5_);

   protected int queuedUpdateSize() {
      return computedLevels.size();
   }
}
