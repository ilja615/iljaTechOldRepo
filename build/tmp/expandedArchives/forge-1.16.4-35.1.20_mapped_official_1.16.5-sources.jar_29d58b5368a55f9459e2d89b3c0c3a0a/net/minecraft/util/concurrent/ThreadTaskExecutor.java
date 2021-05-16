package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadTaskExecutor<R extends Runnable> implements ITaskExecutor<R>, Executor {
   private final String name;
   private static final Logger LOGGER = LogManager.getLogger();
   private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
   private int blockingCount;

   protected ThreadTaskExecutor(String p_i50403_1_) {
      this.name = p_i50403_1_;
   }

   protected abstract R wrapRunnable(Runnable p_212875_1_);

   protected abstract boolean shouldRun(R p_212874_1_);

   public boolean isSameThread() {
      return Thread.currentThread() == this.getRunningThread();
   }

   protected abstract Thread getRunningThread();

   protected boolean scheduleExecutables() {
      return !this.isSameThread();
   }

   public int getPendingTasksCount() {
      return this.pendingRunnables.size();
   }

   public String name() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public <V> CompletableFuture<V> submit(Supplier<V> p_213169_1_) {
      return this.scheduleExecutables() ? CompletableFuture.supplyAsync(p_213169_1_, this) : CompletableFuture.completedFuture(p_213169_1_.get());
   }

   public CompletableFuture<Void> submitAsync(Runnable p_213165_1_) {
      return CompletableFuture.supplyAsync(() -> {
         p_213165_1_.run();
         return null;
      }, this);
   }

   public CompletableFuture<Void> submit(Runnable p_222817_1_) {
      if (this.scheduleExecutables()) {
         return this.submitAsync(p_222817_1_);
      } else {
         p_222817_1_.run();
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   public void executeBlocking(Runnable p_213167_1_) {
      if (!this.isSameThread()) {
         this.submitAsync(p_213167_1_).join();
      } else {
         p_213167_1_.run();
      }

   }

   public void tell(R p_212871_1_) {
      this.pendingRunnables.add(p_212871_1_);
      LockSupport.unpark(this.getRunningThread());
   }

   public void execute(Runnable p_execute_1_) {
      if (this.scheduleExecutables()) {
         this.tell(this.wrapRunnable(p_execute_1_));
      } else {
         p_execute_1_.run();
      }

   }

   @OnlyIn(Dist.CLIENT)
   protected void dropAllTasks() {
      this.pendingRunnables.clear();
   }

   protected void runAllTasks() {
      while(this.pollTask()) {
      }

   }

   protected boolean pollTask() {
      R r = this.pendingRunnables.peek();
      if (r == null) {
         return false;
      } else if (this.blockingCount == 0 && !this.shouldRun(r)) {
         return false;
      } else {
         this.doRunTask(this.pendingRunnables.remove());
         return true;
      }
   }

   public void managedBlock(BooleanSupplier p_213161_1_) {
      ++this.blockingCount;

      try {
         while(!p_213161_1_.getAsBoolean()) {
            if (!this.pollTask()) {
               this.waitForTasks();
            }
         }
      } finally {
         --this.blockingCount;
      }

   }

   protected void waitForTasks() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void doRunTask(R p_213166_1_) {
      try {
         p_213166_1_.run();
      } catch (Exception exception) {
         LOGGER.fatal("Error executing task on {}", this.name(), exception);
      }

   }
}
