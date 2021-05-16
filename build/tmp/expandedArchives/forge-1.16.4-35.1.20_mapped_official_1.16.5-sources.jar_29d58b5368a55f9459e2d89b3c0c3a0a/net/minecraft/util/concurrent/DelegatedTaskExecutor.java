package net.minecraft.util.concurrent;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DelegatedTaskExecutor<T> implements ITaskExecutor<T>, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final AtomicInteger status = new AtomicInteger(0);
   public final ITaskQueue<? super T, ? extends Runnable> queue;
   private final Executor dispatcher;
   private final String name;

   public static DelegatedTaskExecutor<Runnable> create(Executor p_213144_0_, String p_213144_1_) {
      return new DelegatedTaskExecutor<>(new ITaskQueue.Single<>(new ConcurrentLinkedQueue<>()), p_213144_0_, p_213144_1_);
   }

   public DelegatedTaskExecutor(ITaskQueue<? super T, ? extends Runnable> p_i50402_1_, Executor p_i50402_2_, String p_i50402_3_) {
      this.dispatcher = p_i50402_2_;
      this.queue = p_i50402_1_;
      this.name = p_i50402_3_;
   }

   private boolean setAsScheduled() {
      int i;
      do {
         i = this.status.get();
         if ((i & 3) != 0) {
            return false;
         }
      } while(!this.status.compareAndSet(i, i | 2));

      return true;
   }

   private void setAsIdle() {
      int i;
      do {
         i = this.status.get();
      } while(!this.status.compareAndSet(i, i & -3));

   }

   private boolean canBeScheduled() {
      if ((this.status.get() & 1) != 0) {
         return false;
      } else {
         return !this.queue.isEmpty();
      }
   }

   public void close() {
      int i;
      do {
         i = this.status.get();
      } while(!this.status.compareAndSet(i, i | 1));

   }

   private boolean shouldProcess() {
      return (this.status.get() & 2) != 0;
   }

   private boolean pollTask() {
      if (!this.shouldProcess()) {
         return false;
      } else {
         Runnable runnable = this.queue.pop();
         if (runnable == null) {
            return false;
         } else {
            String s;
            Thread thread;
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               thread = Thread.currentThread();
               s = thread.getName();
               thread.setName(this.name);
            } else {
               thread = null;
               s = null;
            }

            runnable.run();
            if (thread != null) {
               thread.setName(s);
            }

            return true;
         }
      }
   }

   public void run() {
      try {
         this.pollUntil((p_213147_0_) -> {
            return p_213147_0_ == 0;
         });
      } finally {
         this.setAsIdle();
         this.registerForExecution();
      }

   }

   public void tell(T p_212871_1_) {
      this.queue.push(p_212871_1_);
      this.registerForExecution();
   }

   private void registerForExecution() {
      if (this.canBeScheduled() && this.setAsScheduled()) {
         try {
            this.dispatcher.execute(this);
         } catch (RejectedExecutionException rejectedexecutionexception1) {
            try {
               this.dispatcher.execute(this);
            } catch (RejectedExecutionException rejectedexecutionexception) {
               LOGGER.error("Cound not schedule mailbox", (Throwable)rejectedexecutionexception);
            }
         }
      }

   }

   private int pollUntil(Int2BooleanFunction p_213145_1_) {
      int i;
      for(i = 0; p_213145_1_.get(i) && this.pollTask(); ++i) {
      }

      return i;
   }

   public String toString() {
      return this.name + " " + this.status.get() + " " + this.queue.isEmpty();
   }

   public String name() {
      return this.name;
   }
}
