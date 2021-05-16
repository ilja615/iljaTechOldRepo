package net.minecraft.profiler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilledProfileResult implements IProfileResult {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final IProfilerSection EMPTY = new IProfilerSection() {
      public long getDuration() {
         return 0L;
      }

      public long getCount() {
         return 0L;
      }

      public Object2LongMap<String> getCounters() {
         return Object2LongMaps.emptyMap();
      }
   };
   private static final Splitter SPLITTER = Splitter.on('\u001e');
   private static final Comparator<Entry<String, FilledProfileResult.Section>> COUNTER_ENTRY_COMPARATOR = Entry.<String, FilledProfileResult.Section>comparingByValue(Comparator.comparingLong((p_230096_0_) -> {
      return p_230096_0_.totalValue;
   })).reversed();
   private final Map<String, ? extends IProfilerSection> entries;
   private final long startTimeNano;
   private final int startTimeTicks;
   private final long endTimeNano;
   private final int endTimeTicks;
   private final int tickDuration;

   public FilledProfileResult(Map<String, ? extends IProfilerSection> p_i50407_1_, long p_i50407_2_, int p_i50407_4_, long p_i50407_5_, int p_i50407_7_) {
      this.entries = p_i50407_1_;
      this.startTimeNano = p_i50407_2_;
      this.startTimeTicks = p_i50407_4_;
      this.endTimeNano = p_i50407_5_;
      this.endTimeTicks = p_i50407_7_;
      this.tickDuration = p_i50407_7_ - p_i50407_4_;
   }

   private IProfilerSection getEntry(String p_230104_1_) {
      IProfilerSection iprofilersection = this.entries.get(p_230104_1_);
      return iprofilersection != null ? iprofilersection : EMPTY;
   }

   public List<DataPoint> getTimes(String p_219917_1_) {
      String s = p_219917_1_;
      IProfilerSection iprofilersection = this.getEntry("root");
      long i = iprofilersection.getDuration();
      IProfilerSection iprofilersection1 = this.getEntry(p_219917_1_);
      long j = iprofilersection1.getDuration();
      long k = iprofilersection1.getCount();
      List<DataPoint> list = Lists.newArrayList();
      if (!p_219917_1_.isEmpty()) {
         p_219917_1_ = p_219917_1_ + '\u001e';
      }

      long l = 0L;

      for(String s1 : this.entries.keySet()) {
         if (isDirectChild(p_219917_1_, s1)) {
            l += this.getEntry(s1).getDuration();
         }
      }

      float f = (float)l;
      if (l < j) {
         l = j;
      }

      if (i < l) {
         i = l;
      }

      for(String s2 : this.entries.keySet()) {
         if (isDirectChild(p_219917_1_, s2)) {
            IProfilerSection iprofilersection2 = this.getEntry(s2);
            long i1 = iprofilersection2.getDuration();
            double d0 = (double)i1 * 100.0D / (double)l;
            double d1 = (double)i1 * 100.0D / (double)i;
            String s3 = s2.substring(p_219917_1_.length());
            list.add(new DataPoint(s3, d0, d1, iprofilersection2.getCount()));
         }
      }

      if ((float)l > f) {
         list.add(new DataPoint("unspecified", (double)((float)l - f) * 100.0D / (double)l, (double)((float)l - f) * 100.0D / (double)i, k));
      }

      Collections.sort(list);
      list.add(0, new DataPoint(s, 100.0D, (double)l * 100.0D / (double)i, k));
      return list;
   }

   private static boolean isDirectChild(String p_230097_0_, String p_230097_1_) {
      return p_230097_1_.length() > p_230097_0_.length() && p_230097_1_.startsWith(p_230097_0_) && p_230097_1_.indexOf(30, p_230097_0_.length() + 1) < 0;
   }

   private Map<String, FilledProfileResult.Section> getCounterValues() {
      Map<String, FilledProfileResult.Section> map = Maps.newTreeMap();
      this.entries.forEach((p_230101_1_, p_230101_2_) -> {
         Object2LongMap<String> object2longmap = p_230101_2_.getCounters();
         if (!object2longmap.isEmpty()) {
            List<String> list = SPLITTER.splitToList(p_230101_1_);
            object2longmap.forEach((p_230103_2_, p_230103_3_) -> {
               map.computeIfAbsent(p_230103_2_, (p_230105_0_) -> {
                  return new FilledProfileResult.Section();
               }).addValue(list.iterator(), p_230103_3_);
            });
         }

      });
      return map;
   }

   public long getStartTimeNano() {
      return this.startTimeNano;
   }

   public int getStartTimeTicks() {
      return this.startTimeTicks;
   }

   public long getEndTimeNano() {
      return this.endTimeNano;
   }

   public int getEndTimeTicks() {
      return this.endTimeTicks;
   }

   public boolean saveResults(File p_219919_1_) {
      p_219919_1_.getParentFile().mkdirs();
      Writer writer = null;

      boolean flag;
      try {
         writer = new OutputStreamWriter(new FileOutputStream(p_219919_1_), StandardCharsets.UTF_8);
         writer.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
         return true;
      } catch (Throwable throwable) {
         LOGGER.error("Could not save profiler results to {}", p_219919_1_, throwable);
         flag = false;
      } finally {
         IOUtils.closeQuietly(writer);
      }

      return flag;
   }

   protected String getProfilerResults(long p_219929_1_, int p_219929_3_) {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Profiler Results ----\n");
      stringbuilder.append("// ");
      stringbuilder.append(getComment());
      stringbuilder.append("\n\n");
      stringbuilder.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
      stringbuilder.append("Time span: ").append(p_219929_1_ / 1000000L).append(" ms\n");
      stringbuilder.append("Tick span: ").append(p_219929_3_).append(" ticks\n");
      stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)p_219929_3_ / ((float)p_219929_1_ / 1.0E9F))).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
      stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.appendProfilerResults(0, "root", stringbuilder);
      stringbuilder.append("--- END PROFILE DUMP ---\n\n");
      Map<String, FilledProfileResult.Section> map = this.getCounterValues();
      if (!map.isEmpty()) {
         stringbuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
         this.appendCounters(map, stringbuilder, p_219929_3_);
         stringbuilder.append("--- END COUNTER DUMP ---\n\n");
      }

      return stringbuilder.toString();
   }

   private static StringBuilder indentLine(StringBuilder p_230098_0_, int p_230098_1_) {
      p_230098_0_.append(String.format("[%02d] ", p_230098_1_));

      for(int i = 0; i < p_230098_1_; ++i) {
         p_230098_0_.append("|   ");
      }

      return p_230098_0_;
   }

   private void appendProfilerResults(int p_219928_1_, String p_219928_2_, StringBuilder p_219928_3_) {
      List<DataPoint> list = this.getTimes(p_219928_2_);
      Object2LongMap<String> object2longmap = ObjectUtils.firstNonNull(this.entries.get(p_219928_2_), EMPTY).getCounters();
      object2longmap.forEach((p_230100_3_, p_230100_4_) -> {
         indentLine(p_219928_3_, p_219928_1_).append('#').append(p_230100_3_).append(' ').append((Object)p_230100_4_).append('/').append(p_230100_4_ / (long)this.tickDuration).append('\n');
      });
      if (list.size() >= 3) {
         for(int i = 1; i < list.size(); ++i) {
            DataPoint datapoint = list.get(i);
            indentLine(p_219928_3_, p_219928_1_).append(datapoint.name).append('(').append(datapoint.count).append('/').append(String.format(Locale.ROOT, "%.0f", (float)datapoint.count / (float)this.tickDuration)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", datapoint.percentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", datapoint.globalPercentage)).append("%\n");
            if (!"unspecified".equals(datapoint.name)) {
               try {
                  this.appendProfilerResults(p_219928_1_ + 1, p_219928_2_ + '\u001e' + datapoint.name, p_219928_3_);
               } catch (Exception exception) {
                  p_219928_3_.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
               }
            }
         }

      }
   }

   private void appendCounterResults(int p_230095_1_, String p_230095_2_, FilledProfileResult.Section p_230095_3_, int p_230095_4_, StringBuilder p_230095_5_) {
      indentLine(p_230095_5_, p_230095_1_).append(p_230095_2_).append(" total:").append(p_230095_3_.selfValue).append('/').append(p_230095_3_.totalValue).append(" average: ").append(p_230095_3_.selfValue / (long)p_230095_4_).append('/').append(p_230095_3_.totalValue / (long)p_230095_4_).append('\n');
      p_230095_3_.children.entrySet().stream().sorted(COUNTER_ENTRY_COMPARATOR).forEach((p_230094_4_) -> {
         this.appendCounterResults(p_230095_1_ + 1, p_230094_4_.getKey(), p_230094_4_.getValue(), p_230095_4_, p_230095_5_);
      });
   }

   private void appendCounters(Map<String, FilledProfileResult.Section> p_230102_1_, StringBuilder p_230102_2_, int p_230102_3_) {
      p_230102_1_.forEach((p_230099_3_, p_230099_4_) -> {
         p_230102_2_.append("-- Counter: ").append(p_230099_3_).append(" --\n");
         this.appendCounterResults(0, "root", p_230099_4_.children.get("root"), p_230102_3_, p_230102_2_);
         p_230102_2_.append("\n\n");
      });
   }

   private static String getComment() {
      String[] astring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return astring[(int)(Util.getNanos() % (long)astring.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }

   public int getTickDuration() {
      return this.tickDuration;
   }

   static class Section {
      private long selfValue;
      private long totalValue;
      private final Map<String, FilledProfileResult.Section> children = Maps.newHashMap();

      private Section() {
      }

      public void addValue(Iterator<String> p_230112_1_, long p_230112_2_) {
         this.totalValue += p_230112_2_;
         if (!p_230112_1_.hasNext()) {
            this.selfValue += p_230112_2_;
         } else {
            this.children.computeIfAbsent(p_230112_1_.next(), (p_230111_0_) -> {
               return new FilledProfileResult.Section();
            }).addValue(p_230112_1_, p_230112_2_);
         }

      }
   }
}
