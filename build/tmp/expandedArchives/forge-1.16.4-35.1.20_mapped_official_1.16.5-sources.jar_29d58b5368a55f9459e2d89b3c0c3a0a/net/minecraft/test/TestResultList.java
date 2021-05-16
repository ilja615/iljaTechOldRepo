package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class TestResultList {
   private final Collection<TestTracker> tests = Lists.newArrayList();
   @Nullable
   private Collection<ITestCallback> listeners = Lists.newArrayList();

   public TestResultList() {
   }

   public TestResultList(Collection<TestTracker> p_i226072_1_) {
      this.tests.addAll(p_i226072_1_);
   }

   public void addTestToTrack(TestTracker p_229579_1_) {
      this.tests.add(p_229579_1_);
      this.listeners.forEach(p_229579_1_::addListener);
   }

   public void addListener(ITestCallback p_240558_1_) {
      this.listeners.add(p_240558_1_);
      this.tests.forEach((p_240559_1_) -> {
         p_240559_1_.addListener(p_240558_1_);
      });
   }

   public void addFailureListener(final Consumer<TestTracker> p_240556_1_) {
      this.addListener(new ITestCallback() {
         public void testStructureLoaded(TestTracker p_225644_1_) {
         }

         public void testFailed(TestTracker p_225645_1_) {
            p_240556_1_.accept(p_225645_1_);
         }
      });
   }

   public int getFailedRequiredCount() {
      return (int)this.tests.stream().filter(TestTracker::hasFailed).filter(TestTracker::isRequired).count();
   }

   public int getFailedOptionalCount() {
      return (int)this.tests.stream().filter(TestTracker::hasFailed).filter(TestTracker::isOptional).count();
   }

   public int getDoneCount() {
      return (int)this.tests.stream().filter(TestTracker::isDone).count();
   }

   public boolean hasFailedRequired() {
      return this.getFailedRequiredCount() > 0;
   }

   public boolean hasFailedOptional() {
      return this.getFailedOptionalCount() > 0;
   }

   public int getTotalCount() {
      return this.tests.size();
   }

   public boolean isDone() {
      return this.getDoneCount() == this.getTotalCount();
   }

   public String getProgressBar() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append('[');
      this.tests.forEach((p_229582_1_) -> {
         if (!p_229582_1_.hasStarted()) {
            stringbuffer.append(' ');
         } else if (p_229582_1_.hasSucceeded()) {
            stringbuffer.append('+');
         } else if (p_229582_1_.hasFailed()) {
            stringbuffer.append((char)(p_229582_1_.isRequired() ? 'X' : 'x'));
         } else {
            stringbuffer.append('_');
         }

      });
      stringbuffer.append(']');
      return stringbuffer.toString();
   }

   public String toString() {
      return this.getProgressBar();
   }
}
