package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class PrioritizedGoal extends Goal {
   private final Goal goal;
   private final int priority;
   private boolean isRunning;

   public PrioritizedGoal(int p_i50318_1_, Goal p_i50318_2_) {
      this.priority = p_i50318_1_;
      this.goal = p_i50318_2_;
   }

   public boolean canBeReplacedBy(PrioritizedGoal p_220771_1_) {
      return this.isInterruptable() && p_220771_1_.getPriority() < this.getPriority();
   }

   public boolean canUse() {
      return this.goal.canUse();
   }

   public boolean canContinueToUse() {
      return this.goal.canContinueToUse();
   }

   public boolean isInterruptable() {
      return this.goal.isInterruptable();
   }

   public void start() {
      if (!this.isRunning) {
         this.isRunning = true;
         this.goal.start();
      }
   }

   public void stop() {
      if (this.isRunning) {
         this.isRunning = false;
         this.goal.stop();
      }
   }

   public void tick() {
      this.goal.tick();
   }

   public void setFlags(EnumSet<Goal.Flag> p_220684_1_) {
      this.goal.setFlags(p_220684_1_);
   }

   public EnumSet<Goal.Flag> getFlags() {
      return this.goal.getFlags();
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public int getPriority() {
      return this.priority;
   }

   public Goal getGoal() {
      return this.goal;
   }

   public boolean equals(@Nullable Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? this.goal.equals(((PrioritizedGoal)p_equals_1_).goal) : false;
      }
   }

   public int hashCode() {
      return this.goal.hashCode();
   }
}
