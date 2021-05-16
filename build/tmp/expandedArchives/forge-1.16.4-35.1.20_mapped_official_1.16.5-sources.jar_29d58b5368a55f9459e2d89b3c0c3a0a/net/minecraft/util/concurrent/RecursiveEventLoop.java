package net.minecraft.util.concurrent;

public abstract class RecursiveEventLoop<R extends Runnable> extends ThreadTaskExecutor<R> {
   private int reentrantCount;

   public RecursiveEventLoop(String p_i50401_1_) {
      super(p_i50401_1_);
   }

   protected boolean scheduleExecutables() {
      return this.runningTask() || super.scheduleExecutables();
   }

   protected boolean runningTask() {
      return this.reentrantCount != 0;
   }

   protected void doRunTask(R p_213166_1_) {
      ++this.reentrantCount;

      try {
         super.doRunTask(p_213166_1_);
      } finally {
         --this.reentrantCount;
      }

   }
}
