package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleBuilder {
   private final Schedule schedule;
   private final List<ScheduleBuilder.ActivityEntry> transitions = Lists.newArrayList();

   public ScheduleBuilder(Schedule p_i50135_1_) {
      this.schedule = p_i50135_1_;
   }

   public ScheduleBuilder changeActivityAt(int p_221402_1_, Activity p_221402_2_) {
      this.transitions.add(new ScheduleBuilder.ActivityEntry(p_221402_1_, p_221402_2_));
      return this;
   }

   public Schedule build() {
      this.transitions.stream().map(ScheduleBuilder.ActivityEntry::getActivity).collect(Collectors.toSet()).forEach(this.schedule::ensureTimelineExistsFor);
      this.transitions.forEach((p_221405_1_) -> {
         Activity activity = p_221405_1_.getActivity();
         this.schedule.getAllTimelinesExceptFor(activity).forEach((p_221403_1_) -> {
            p_221403_1_.addKeyframe(p_221405_1_.getTime(), 0.0F);
         });
         this.schedule.getTimelineFor(activity).addKeyframe(p_221405_1_.getTime(), 1.0F);
      });
      return this.schedule;
   }

   static class ActivityEntry {
      private final int time;
      private final Activity activity;

      public ActivityEntry(int p_i51309_1_, Activity p_i51309_2_) {
         this.time = p_i51309_1_;
         this.activity = p_i51309_2_;
      }

      public int getTime() {
         return this.time;
      }

      public Activity getActivity() {
         return this.activity;
      }
   }
}
