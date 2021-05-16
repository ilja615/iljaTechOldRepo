package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.RangedInteger;
import net.minecraft.world.server.ServerWorld;

public class RunSometimesTask<E extends LivingEntity> extends Task<E> {
   private boolean resetTicks;
   private boolean wasRunning;
   private final RangedInteger interval;
   private final Task<? super E> wrappedBehavior;
   private int ticksUntilNextStart;

   public RunSometimesTask(Task<? super E> p_i231530_1_, RangedInteger p_i231530_2_) {
      this(p_i231530_1_, false, p_i231530_2_);
   }

   public RunSometimesTask(Task<? super E> p_i231531_1_, boolean p_i231531_2_, RangedInteger p_i231531_3_) {
      super(p_i231531_1_.entryCondition);
      this.wrappedBehavior = p_i231531_1_;
      this.resetTicks = !p_i231531_2_;
      this.interval = p_i231531_3_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      if (!this.wrappedBehavior.checkExtraStartConditions(p_212832_1_, p_212832_2_)) {
         return false;
      } else {
         if (this.resetTicks) {
            this.resetTicksUntilNextStart(p_212832_1_);
            this.resetTicks = false;
         }

         if (this.ticksUntilNextStart > 0) {
            --this.ticksUntilNextStart;
         }

         return !this.wasRunning && this.ticksUntilNextStart == 0;
      }
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      this.wrappedBehavior.start(p_212831_1_, p_212831_2_, p_212831_3_);
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, E p_212834_2_, long p_212834_3_) {
      return this.wrappedBehavior.canStillUse(p_212834_1_, p_212834_2_, p_212834_3_);
   }

   protected void tick(ServerWorld p_212833_1_, E p_212833_2_, long p_212833_3_) {
      this.wrappedBehavior.tick(p_212833_1_, p_212833_2_, p_212833_3_);
      this.wasRunning = this.wrappedBehavior.getStatus() == Task.Status.RUNNING;
   }

   protected void stop(ServerWorld p_212835_1_, E p_212835_2_, long p_212835_3_) {
      this.resetTicksUntilNextStart(p_212835_1_);
      this.wrappedBehavior.stop(p_212835_1_, p_212835_2_, p_212835_3_);
   }

   private void resetTicksUntilNextStart(ServerWorld p_233949_1_) {
      this.ticksUntilNextStart = this.interval.randomValue(p_233949_1_.random);
   }

   protected boolean timedOut(long p_220383_1_) {
      return false;
   }

   public String toString() {
      return "RunSometimes: " + this.wrappedBehavior;
   }
}
