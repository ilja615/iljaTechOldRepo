package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.profiler.IProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final PrioritizedGoal DUMMY = new PrioritizedGoal(Integer.MAX_VALUE, new Goal() {
      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   /** Goals currently using a particular flag */
   private final Map<Goal.Flag, PrioritizedGoal> flagGoals = new EnumMap<>(Goal.Flag.class);
   private final Set<PrioritizedGoal> goals = Sets.newLinkedHashSet();
   private final Supplier<IProfiler> profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int tickRate = 3;

   public GoalSelector(Supplier<IProfiler> profiler) {
      this.profiler = profiler;
   }

   /**
    * Add a now AITask. Args : priority, task
    */
   public void addGoal(int priority, Goal task) {
      this.goals.add(new PrioritizedGoal(priority, task));
   }

   /**
    * removes the indicated task from the entity's AI tasks.
    */
   public void removeGoal(Goal task) {
      this.goals.stream().filter((goal) -> {
         return goal.getGoal() == task;
      }).filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::resetTask);
      this.goals.removeIf((goal) -> {
         return goal.getGoal() == task;
      });
   }

   public void tick() {
      IProfiler iprofiler = this.profiler.get();
      iprofiler.startSection("goalCleanup");
      this.getRunningGoals().filter((goal) -> {
         return !goal.isRunning() || goal.getMutexFlags().stream().anyMatch(this.disabledFlags::contains) || !goal.shouldContinueExecuting();
      }).forEach(Goal::resetTask);
      this.flagGoals.forEach((flag, p_220885_2_) -> {
         if (!p_220885_2_.isRunning()) {
            this.flagGoals.remove(flag);
         }

      });
      iprofiler.endSection();
      iprofiler.startSection("goalUpdate");
      this.goals.stream().filter((goal) -> {
         return !goal.isRunning();
      }).filter((goal) -> {
         return goal.getMutexFlags().stream().noneMatch(this.disabledFlags::contains);
      }).filter((goal) -> {
         return goal.getMutexFlags().stream().allMatch((flag) -> {
            return this.flagGoals.getOrDefault(flag, DUMMY).isPreemptedBy(goal);
         });
      }).filter(PrioritizedGoal::shouldExecute).forEach((goal) -> {
         goal.getMutexFlags().forEach((flag) -> {
            PrioritizedGoal prioritizedgoal = this.flagGoals.getOrDefault(flag, DUMMY);
            prioritizedgoal.resetTask();
            this.flagGoals.put(flag, goal);
         });
         goal.startExecuting();
      });
      iprofiler.endSection();
      iprofiler.startSection("goalTick");
      this.getRunningGoals().forEach(PrioritizedGoal::tick);
      iprofiler.endSection();
   }

   public Stream<PrioritizedGoal> getRunningGoals() {
      return this.goals.stream().filter(PrioritizedGoal::isRunning);
   }

   public void disableFlag(Goal.Flag flag) {
      this.disabledFlags.add(flag);
   }

   public void enableFlag(Goal.Flag flag) {
      this.disabledFlags.remove(flag);
   }

   public void setFlag(Goal.Flag flag, boolean p_220878_2_) {
      if (p_220878_2_) {
         this.enableFlag(flag);
      } else {
         this.disableFlag(flag);
      }

   }
}
