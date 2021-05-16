package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TestTracker {
   private final TestFunctionInfo testFunction;
   @Nullable
   private BlockPos structureBlockPos;
   private final ServerWorld level;
   private final Collection<ITestCallback> listeners = Lists.newArrayList();
   private final int timeoutTicks;
   private final Collection<TestList> sequences = Lists.newCopyOnWriteArrayList();
   private Object2LongMap<Runnable> runAtTickTimeMap = new Object2LongOpenHashMap<>();
   private long startTick;
   private long tickCount;
   private boolean started = false;
   private final Stopwatch timer = Stopwatch.createUnstarted();
   private boolean done = false;
   private final Rotation rotation;
   @Nullable
   private Throwable error;

   public TestTracker(TestFunctionInfo p_i232556_1_, Rotation p_i232556_2_, ServerWorld p_i232556_3_) {
      this.testFunction = p_i232556_1_;
      this.level = p_i232556_3_;
      this.timeoutTicks = p_i232556_1_.getMaxTicks();
      this.rotation = p_i232556_1_.getRotation().getRotated(p_i232556_2_);
   }

   void setStructureBlockPos(BlockPos p_229503_1_) {
      this.structureBlockPos = p_229503_1_;
   }

   void startExecution() {
      this.startTick = this.level.getGameTime() + 1L + this.testFunction.getSetupTicks();
      this.timer.start();
   }

   public void tick() {
      if (!this.isDone()) {
         this.tickCount = this.level.getGameTime() - this.startTick;
         if (this.tickCount >= 0L) {
            if (this.tickCount == 0L) {
               this.startTest();
            }

            ObjectIterator<Entry<Runnable>> objectiterator = this.runAtTickTimeMap.object2LongEntrySet().iterator();

            while(objectiterator.hasNext()) {
               Entry<Runnable> entry = objectiterator.next();
               if (entry.getLongValue() <= this.tickCount) {
                  try {
                     entry.getKey().run();
                  } catch (Exception exception) {
                     this.fail(exception);
                  }

                  objectiterator.remove();
               }
            }

            if (this.tickCount > (long)this.timeoutTicks) {
               if (this.sequences.isEmpty()) {
                  this.fail(new TestTimeoutException("Didn't succeed or fail within " + this.testFunction.getMaxTicks() + " ticks"));
               } else {
                  this.sequences.forEach((p_229509_1_) -> {
                     p_229509_1_.tickAndFailIfNotComplete(this.tickCount);
                  });
                  if (this.error == null) {
                     this.fail(new TestTimeoutException("No sequences finished"));
                  }
               }
            } else {
               this.sequences.forEach((p_229505_1_) -> {
                  p_229505_1_.tickAndContinue(this.tickCount);
               });
            }

         }
      }
   }

   private void startTest() {
      if (this.started) {
         throw new IllegalStateException("Test already started");
      } else {
         this.started = true;

         try {
            this.testFunction.run(new TestTrackerHolder(this));
         } catch (Exception exception) {
            this.fail(exception);
         }

      }
   }

   public String getTestName() {
      return this.testFunction.getTestName();
   }

   public BlockPos getStructureBlockPos() {
      return this.structureBlockPos;
   }

   public ServerWorld getLevel() {
      return this.level;
   }

   public boolean hasSucceeded() {
      return this.done && this.error == null;
   }

   public boolean hasFailed() {
      return this.error != null;
   }

   public boolean hasStarted() {
      return this.started;
   }

   public boolean isDone() {
      return this.done;
   }

   private void finish() {
      if (!this.done) {
         this.done = true;
         this.timer.stop();
      }

   }

   public void fail(Throwable p_229506_1_) {
      this.finish();
      this.error = p_229506_1_;
      this.listeners.forEach((p_229511_1_) -> {
         p_229511_1_.testFailed(this);
      });
   }

   @Nullable
   public Throwable getError() {
      return this.error;
   }

   public String toString() {
      return this.getTestName();
   }

   public void addListener(ITestCallback p_229504_1_) {
      this.listeners.add(p_229504_1_);
   }

   public void spawnStructure(BlockPos p_240543_1_, int p_240543_2_) {
      StructureBlockTileEntity structureblocktileentity = StructureHelper.spawnStructure(this.getStructureName(), p_240543_1_, this.getRotation(), p_240543_2_, this.level, false);
      this.setStructureBlockPos(structureblocktileentity.getBlockPos());
      structureblocktileentity.setStructureName(this.getTestName());
      StructureHelper.addCommandBlockAndButtonToStartTest(this.structureBlockPos, new BlockPos(1, 0, -1), this.getRotation(), this.level);
      this.listeners.forEach((p_229508_1_) -> {
         p_229508_1_.testStructureLoaded(this);
      });
   }

   public boolean isRequired() {
      return this.testFunction.isRequired();
   }

   public boolean isOptional() {
      return !this.testFunction.isRequired();
   }

   public String getStructureName() {
      return this.testFunction.getStructureName();
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public TestFunctionInfo getTestFunction() {
      return this.testFunction;
   }
}
