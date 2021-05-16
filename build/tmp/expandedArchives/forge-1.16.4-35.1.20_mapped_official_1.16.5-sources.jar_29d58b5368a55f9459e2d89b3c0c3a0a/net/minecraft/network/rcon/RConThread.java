package net.minecraft.network.rcon;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.util.DefaultWithNameUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RConThread implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   protected volatile boolean running;
   protected final String name;
   @Nullable
   protected Thread thread;

   protected RConThread(String p_i231426_1_) {
      this.name = p_i231426_1_;
   }

   public synchronized boolean start() {
      if (this.running) {
         return true;
      } else {
         this.running = true;
         this.thread = new Thread(this, this.name + " #" + UNIQUE_THREAD_ID.incrementAndGet());
         this.thread.setUncaughtExceptionHandler(new DefaultWithNameUncaughtExceptionHandler(LOGGER));
         this.thread.start();
         LOGGER.info("Thread {} started", (Object)this.name);
         return true;
      }
   }

   public synchronized void stop() {
      this.running = false;
      if (null != this.thread) {
         int i = 0;

         while(this.thread.isAlive()) {
            try {
               this.thread.join(1000L);
               ++i;
               if (i >= 5) {
                  LOGGER.warn("Waited {} seconds attempting force stop!", (int)i);
               } else if (this.thread.isAlive()) {
                  LOGGER.warn("Thread {} ({}) failed to exit after {} second(s)", this, this.thread.getState(), i, new Exception("Stack:"));
                  this.thread.interrupt();
               }
            } catch (InterruptedException interruptedexception) {
            }
         }

         LOGGER.info("Thread {} stopped", (Object)this.name);
         this.thread = null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }
}
