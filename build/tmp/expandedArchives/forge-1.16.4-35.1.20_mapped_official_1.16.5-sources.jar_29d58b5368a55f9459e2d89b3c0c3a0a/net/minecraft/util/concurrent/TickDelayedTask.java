package net.minecraft.util.concurrent;

public class TickDelayedTask implements Runnable {
   private final int tick;
   private final Runnable runnable;

   public TickDelayedTask(int p_i50745_1_, Runnable p_i50745_2_) {
      this.tick = p_i50745_1_;
      this.runnable = p_i50745_2_;
   }

   public int getTick() {
      return this.tick;
   }

   public void run() {
      this.runnable.run();
   }
}
