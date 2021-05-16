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
   private static final PrioritizedGoal NO_GOAL = new PrioritizedGoal(Integer.MAX_VALUE, new Goal() {
      public boolean canUse() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   private final Map<Goal.Flag, PrioritizedGoal> lockedFlags = new EnumMap<>(Goal.Flag.class);
   private final Set<PrioritizedGoal> availableGoals = Sets.newLinkedHashSet();
   private final Supplier<IProfiler> profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int newGoalRate = 3;

   public GoalSelector(Supplier<IProfiler> p_i231546_1_) {
      this.profiler = p_i231546_1_;
   }

   public void addGoal(int p_75776_1_, Goal p_75776_2_) {
      this.availableGoals.add(new PrioritizedGoal(p_75776_1_, p_75776_2_));
   }

   public void removeGoal(Goal p_85156_1_) {
      this.availableGoals.stream().filter((p_220882_1_) -> {
         return p_220882_1_.getGoal() == p_85156_1_;
      }).filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::stop);
      this.availableGoals.removeIf((p_220884_1_) -> {
         return p_220884_1_.getGoal() == p_85156_1_;
      });
   }

   public void tick() {
      IProfiler iprofiler = this.profiler.get();
      iprofiler.push("goalCleanup");
      this.getRunningGoals().filter((p_220881_1_) -> {
         return !p_220881_1_.isRunning() || p_220881_1_.getFlags().stream().anyMatch(this.disabledFlags::contains) || !p_220881_1_.canContinueToUse();
      }).forEach(Goal::stop);
      this.lockedFlags.forEach((p_220885_1_, p_220885_2_) -> {
         if (!p_220885_2_.isRunning()) {
            this.lockedFlags.remove(p_220885_1_);
         }

      });
      iprofiler.pop();
      iprofiler.push("goalUpdate");
      this.availableGoals.stream().filter((p_220883_0_) -> {
         return !p_220883_0_.isRunning();
      }).filter((p_220879_1_) -> {
         return p_220879_1_.getFlags().stream().noneMatch(this.disabledFlags::contains);
      }).filter((p_220889_1_) -> {
         return p_220889_1_.getFlags().stream().allMatch((p_220887_2_) -> {
            return this.lockedFlags.getOrDefault(p_220887_2_, NO_GOAL).canBeReplacedBy(p_220889_1_);
         });
      }).filter(PrioritizedGoal::canUse).forEach((p_220877_1_) -> {
         p_220877_1_.getFlags().forEach((p_220876_2_) -> {
            PrioritizedGoal prioritizedgoal = this.lockedFlags.getOrDefault(p_220876_2_, NO_GOAL);
            prioritizedgoal.stop();
            this.lockedFlags.put(p_220876_2_, p_220877_1_);
         });
         p_220877_1_.start();
      });
      iprofiler.pop();
      iprofiler.push("goalTick");
      this.getRunningGoals().forEach(PrioritizedGoal::tick);
      iprofiler.pop();
   }

   public Stream<PrioritizedGoal> getRunningGoals() {
      return this.availableGoals.stream().filter(PrioritizedGoal::isRunning);
   }

   public void disableControlFlag(Goal.Flag p_220880_1_) {
      this.disabledFlags.add(p_220880_1_);
   }

   public void enableControlFlag(Goal.Flag p_220886_1_) {
      this.disabledFlags.remove(p_220886_1_);
   }

   public void setControlFlag(Goal.Flag p_220878_1_, boolean p_220878_2_) {
      if (p_220878_2_) {
         this.enableControlFlag(p_220878_1_);
      } else {
         this.disableControlFlag(p_220878_1_);
      }

   }
}
