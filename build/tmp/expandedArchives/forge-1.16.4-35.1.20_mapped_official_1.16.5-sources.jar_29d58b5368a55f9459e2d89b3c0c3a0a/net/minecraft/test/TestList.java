package net.minecraft.test;

import java.util.Iterator;
import java.util.List;

public class TestList {
   private final TestTracker parent = null;
   private final List<TestTickResult> events = null;
   private long lastTick;

   public void tickAndContinue(long p_229567_1_) {
      try {
         this.tick(p_229567_1_);
      } catch (Exception exception) {
      }

   }

   public void tickAndFailIfNotComplete(long p_229568_1_) {
      try {
         this.tick(p_229568_1_);
      } catch (Exception exception) {
         this.parent.fail(exception);
      }

   }

   private void tick(long p_229569_1_) {
      Iterator<TestTickResult> iterator = this.events.iterator();

      while(iterator.hasNext()) {
         TestTickResult testtickresult = iterator.next();
         testtickresult.assertion.run();
         iterator.remove();
         long i = p_229569_1_ - this.lastTick;
         long j = this.lastTick;
         this.lastTick = p_229569_1_;
         if (testtickresult.expectedDelay != null && testtickresult.expectedDelay != i) {
            this.parent.fail(new TestRuntimeException("Succeeded in invalid tick: expected " + (j + testtickresult.expectedDelay) + ", but current tick is " + p_229569_1_));
            break;
         }
      }

   }

   private TestList() {
      throw new RuntimeException("Synthetic constructor added by MCP, do not call");
   }
}
