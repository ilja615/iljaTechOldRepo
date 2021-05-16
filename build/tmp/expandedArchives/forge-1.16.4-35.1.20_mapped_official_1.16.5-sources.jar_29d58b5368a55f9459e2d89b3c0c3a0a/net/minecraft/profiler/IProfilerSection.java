package net.minecraft.profiler;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

public interface IProfilerSection {
   long getDuration();

   long getCount();

   Object2LongMap<String> getCounters();
}
