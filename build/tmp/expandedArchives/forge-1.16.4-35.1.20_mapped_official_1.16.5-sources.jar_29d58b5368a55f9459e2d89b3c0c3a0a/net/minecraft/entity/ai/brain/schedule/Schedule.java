package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.util.registry.Registry;

public class Schedule extends net.minecraftforge.registries.ForgeRegistryEntry<Schedule> {
   public static final Schedule EMPTY = register("empty").changeActivityAt(0, Activity.IDLE).build();
   public static final Schedule SIMPLE = register("simple").changeActivityAt(5000, Activity.WORK).changeActivityAt(11000, Activity.REST).build();
   public static final Schedule VILLAGER_BABY = register("villager_baby").changeActivityAt(10, Activity.IDLE).changeActivityAt(3000, Activity.PLAY).changeActivityAt(6000, Activity.IDLE).changeActivityAt(10000, Activity.PLAY).changeActivityAt(12000, Activity.REST).build();
   public static final Schedule VILLAGER_DEFAULT = register("villager_default").changeActivityAt(10, Activity.IDLE).changeActivityAt(2000, Activity.WORK).changeActivityAt(9000, Activity.MEET).changeActivityAt(11000, Activity.IDLE).changeActivityAt(12000, Activity.REST).build();
   private final Map<Activity, ScheduleDuties> timelines = Maps.newHashMap();

   protected static ScheduleBuilder register(String p_221380_0_) {
      Schedule schedule = Registry.register(Registry.SCHEDULE, p_221380_0_, new Schedule());
      return new ScheduleBuilder(schedule);
   }

   protected void ensureTimelineExistsFor(Activity p_221379_1_) {
      if (!this.timelines.containsKey(p_221379_1_)) {
         this.timelines.put(p_221379_1_, new ScheduleDuties());
      }

   }

   protected ScheduleDuties getTimelineFor(Activity p_221382_1_) {
      return this.timelines.get(p_221382_1_);
   }

   protected List<ScheduleDuties> getAllTimelinesExceptFor(Activity p_221381_1_) {
      return this.timelines.entrySet().stream().filter((p_221378_1_) -> {
         return p_221378_1_.getKey() != p_221381_1_;
      }).map(Entry::getValue).collect(Collectors.toList());
   }

   public Activity getActivityAt(int p_221377_1_) {
      return this.timelines.entrySet().stream().max(Comparator.comparingDouble((p_221376_1_) -> {
         return (double)p_221376_1_.getValue().getValueAt(p_221377_1_);
      })).map(Entry::getKey).orElse(Activity.IDLE);
   }
}
