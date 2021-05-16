package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface ITaskQueue<T, F> {
   @Nullable
   F pop();

   boolean push(T p_212828_1_);

   boolean isEmpty();

   public static final class Priority implements ITaskQueue<ITaskQueue.RunnableWithPriority, Runnable> {
      private final List<Queue<Runnable>> queueList;

      public Priority(int p_i50964_1_) {
         this.queueList = IntStream.range(0, p_i50964_1_).mapToObj((p_219948_0_) -> {
            return Queues.<Runnable>newConcurrentLinkedQueue();
         }).collect(Collectors.toList());
      }

      @Nullable
      public Runnable pop() {
         for(Queue<Runnable> queue : this.queueList) {
            Runnable runnable = queue.poll();
            if (runnable != null) {
               return runnable;
            }
         }

         return null;
      }

      public boolean push(ITaskQueue.RunnableWithPriority p_212828_1_) {
         int i = p_212828_1_.getPriority();
         this.queueList.get(i).add(p_212828_1_);
         return true;
      }

      public boolean isEmpty() {
         return this.queueList.stream().allMatch(Collection::isEmpty);
      }
   }

   public static final class RunnableWithPriority implements Runnable {
      private final int priority;
      private final Runnable task;

      public RunnableWithPriority(int p_i50963_1_, Runnable p_i50963_2_) {
         this.priority = p_i50963_1_;
         this.task = p_i50963_2_;
      }

      public void run() {
         this.task.run();
      }

      public int getPriority() {
         return this.priority;
      }
   }

   public static final class Single<T> implements ITaskQueue<T, T> {
      private final Queue<T> queue;

      public Single(Queue<T> p_i50962_1_) {
         this.queue = p_i50962_1_;
      }

      @Nullable
      public T pop() {
         return this.queue.poll();
      }

      public boolean push(T p_212828_1_) {
         return this.queue.add(p_212828_1_);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }
   }
}
