package net.minecraft.world.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.concurrent.ITaskQueue;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ChunkHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPriorityQueueSorter implements AutoCloseable, ChunkHolder.IListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ITaskExecutor<?>, ChunkTaskPriorityQueue<? extends Function<ITaskExecutor<Unit>, ?>>> queues;
   private final Set<ITaskExecutor<?>> sleeping;
   private final DelegatedTaskExecutor<ITaskQueue.RunnableWithPriority> mailbox;

   public ChunkTaskPriorityQueueSorter(List<ITaskExecutor<?>> p_i50713_1_, Executor p_i50713_2_, int p_i50713_3_) {
      this.queues = p_i50713_1_.stream().collect(Collectors.toMap(Function.identity(), (p_219084_1_) -> {
         return new ChunkTaskPriorityQueue<>(p_219084_1_.name() + "_queue", p_i50713_3_);
      }));
      this.sleeping = Sets.newHashSet(p_i50713_1_);
      this.mailbox = new DelegatedTaskExecutor<>(new ITaskQueue.Priority(4), p_i50713_2_, "sorter");
   }

   public static ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable> message(Runnable p_219069_0_, long p_219069_1_, IntSupplier p_219069_3_) {
      return new ChunkTaskPriorityQueueSorter.FunctionEntry<>((p_219072_1_) -> {
         return () -> {
            p_219069_0_.run();
            p_219072_1_.tell(Unit.INSTANCE);
         };
      }, p_219069_1_, p_219069_3_);
   }

   public static ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable> message(ChunkHolder p_219081_0_, Runnable p_219081_1_) {
      return message(p_219081_1_, p_219081_0_.getPos().toLong(), p_219081_0_::getQueueLevel);
   }

   public static ChunkTaskPriorityQueueSorter.RunnableEntry release(Runnable p_219073_0_, long p_219073_1_, boolean p_219073_3_) {
      return new ChunkTaskPriorityQueueSorter.RunnableEntry(p_219073_0_, p_219073_1_, p_219073_3_);
   }

   public <T> ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<T>> getProcessor(ITaskExecutor<T> p_219087_1_, boolean p_219087_2_) {
      return this.mailbox.<ITaskExecutor<FunctionEntry<T>>>ask((p_219086_3_) -> {
         return new ITaskQueue.RunnableWithPriority(0, () -> {
            this.getQueue(p_219087_1_);
            p_219086_3_.tell(ITaskExecutor.of("chunk priority sorter around " + p_219087_1_.name(), (p_219071_3_) -> {
               this.submit(p_219087_1_, p_219071_3_.task, p_219071_3_.pos, p_219071_3_.level, p_219087_2_);
            }));
         });
      }).join();
   }

   public ITaskExecutor<ChunkTaskPriorityQueueSorter.RunnableEntry> getReleaseProcessor(ITaskExecutor<Runnable> p_219091_1_) {
      return this.mailbox.<ITaskExecutor<RunnableEntry>>ask((p_219080_2_) -> {
         return new ITaskQueue.RunnableWithPriority(0, () -> {
            p_219080_2_.tell(ITaskExecutor.of("chunk priority sorter around " + p_219091_1_.name(), (p_219075_2_) -> {
               this.release(p_219091_1_, p_219075_2_.pos, p_219075_2_.task, p_219075_2_.clearQueue);
            }));
         });
      }).join();
   }

   public void onLevelChange(ChunkPos p_219066_1_, IntSupplier p_219066_2_, int p_219066_3_, IntConsumer p_219066_4_) {
      this.mailbox.tell(new ITaskQueue.RunnableWithPriority(0, () -> {
         int i = p_219066_2_.getAsInt();
         this.queues.values().forEach((p_219076_3_) -> {
            p_219076_3_.resortChunkTasks(i, p_219066_1_, p_219066_3_);
         });
         p_219066_4_.accept(p_219066_3_);
      }));
   }

   private <T> void release(ITaskExecutor<T> p_219074_1_, long p_219074_2_, Runnable p_219074_4_, boolean p_219074_5_) {
      this.mailbox.tell(new ITaskQueue.RunnableWithPriority(1, () -> {
         ChunkTaskPriorityQueue<Function<ITaskExecutor<Unit>, T>> chunktaskpriorityqueue = this.getQueue(p_219074_1_);
         chunktaskpriorityqueue.release(p_219074_2_, p_219074_5_);
         if (this.sleeping.remove(p_219074_1_)) {
            this.pollTask(chunktaskpriorityqueue, p_219074_1_);
         }

         p_219074_4_.run();
      }));
   }

   private <T> void submit(ITaskExecutor<T> p_219067_1_, Function<ITaskExecutor<Unit>, T> p_219067_2_, long p_219067_3_, IntSupplier p_219067_5_, boolean p_219067_6_) {
      this.mailbox.tell(new ITaskQueue.RunnableWithPriority(2, () -> {
         ChunkTaskPriorityQueue<Function<ITaskExecutor<Unit>, T>> chunktaskpriorityqueue = this.getQueue(p_219067_1_);
         int i = p_219067_5_.getAsInt();
         chunktaskpriorityqueue.submit(Optional.of(p_219067_2_), p_219067_3_, i);
         if (p_219067_6_) {
            chunktaskpriorityqueue.submit(Optional.empty(), p_219067_3_, i);
         }

         if (this.sleeping.remove(p_219067_1_)) {
            this.pollTask(chunktaskpriorityqueue, p_219067_1_);
         }

      }));
   }

   private <T> void pollTask(ChunkTaskPriorityQueue<Function<ITaskExecutor<Unit>, T>> p_219078_1_, ITaskExecutor<T> p_219078_2_) {
      this.mailbox.tell(new ITaskQueue.RunnableWithPriority(3, () -> {
         Stream<Either<Function<ITaskExecutor<Unit>, T>, Runnable>> stream = p_219078_1_.pop();
         if (stream == null) {
            this.sleeping.add(p_219078_2_);
         } else {
            Util.sequence(stream.map((p_219092_1_) -> {
               return p_219092_1_.map(p_219078_2_::ask, (p_219077_0_) -> {
                  p_219077_0_.run();
                  return CompletableFuture.completedFuture(Unit.INSTANCE);
               });
            }).collect(Collectors.toList())).thenAccept((p_219088_3_) -> {
               this.pollTask(p_219078_1_, p_219078_2_);
            });
         }

      }));
   }

   private <T> ChunkTaskPriorityQueue<Function<ITaskExecutor<Unit>, T>> getQueue(ITaskExecutor<T> p_219068_1_) {
      ChunkTaskPriorityQueue<? extends Function<ITaskExecutor<Unit>, ?>> chunktaskpriorityqueue = this.queues.get(p_219068_1_);
      if (chunktaskpriorityqueue == null) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("No queue for: " + p_219068_1_));
      } else {
         return (ChunkTaskPriorityQueue<Function<ITaskExecutor<Unit>, T>>) chunktaskpriorityqueue;
      }
   }

   @VisibleForTesting
   public String getDebugStatus() {
      return (String)this.queues.entrySet().stream().map((p_225397_0_) -> {
         return p_225397_0_.getKey().name() + "=[" + (String)p_225397_0_.getValue().getAcquired().stream().map((p_225398_0_) -> {
            return p_225398_0_ + ":" + new ChunkPos(p_225398_0_);
         }).collect(Collectors.joining(",")) + "]";
      }).collect(Collectors.joining(",")) + ", s=" + this.sleeping.size();
   }

   public void close() {
      this.queues.keySet().forEach(ITaskExecutor::close);
   }

   public static final class FunctionEntry<T> {
      private final Function<ITaskExecutor<Unit>, T> task;
      private final long pos;
      private final IntSupplier level;

      private FunctionEntry(Function<ITaskExecutor<Unit>, T> p_i50028_1_, long p_i50028_2_, IntSupplier p_i50028_4_) {
         this.task = p_i50028_1_;
         this.pos = p_i50028_2_;
         this.level = p_i50028_4_;
      }
   }

   public static final class RunnableEntry {
      private final Runnable task;
      private final long pos;
      private final boolean clearQueue;

      private RunnableEntry(Runnable p_i50026_1_, long p_i50026_2_, boolean p_i50026_4_) {
         this.task = p_i50026_1_;
         this.pos = p_i50026_2_;
         this.clearQueue = p_i50026_4_;
      }
   }
}
