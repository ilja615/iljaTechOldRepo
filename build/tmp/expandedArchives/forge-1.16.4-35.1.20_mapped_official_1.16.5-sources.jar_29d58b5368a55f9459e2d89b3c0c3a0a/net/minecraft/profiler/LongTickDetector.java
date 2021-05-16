package net.minecraft.profiler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LongTickDetector {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LongSupplier realTime = null;
   private final long saveThreshold = 0L;
   private int tick;
   private final File location = null;
   private IResultableProfiler profiler;

   public IProfiler startTick() {
      this.profiler = new Profiler(this.realTime, () -> {
         return this.tick;
      }, false);
      ++this.tick;
      return this.profiler;
   }

   public void endTick() {
      if (this.profiler != EmptyProfiler.INSTANCE) {
         IProfileResult iprofileresult = this.profiler.getResults();
         this.profiler = EmptyProfiler.INSTANCE;
         if (iprofileresult.getNanoDuration() >= this.saveThreshold) {
            File file1 = new File(this.location, "tick-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
            iprofileresult.saveResults(file1);
            LOGGER.info("Recorded long tick -- wrote info to: {}", (Object)file1.getAbsolutePath());
         }

      }
   }

   @Nullable
   public static LongTickDetector createTickProfiler(String p_233524_0_) {
      return null;
   }

   public static IProfiler decorateFiller(IProfiler p_233523_0_, @Nullable LongTickDetector p_233523_1_) {
      return p_233523_1_ != null ? IProfiler.tee(p_233523_1_.startTick(), p_233523_0_) : p_233523_0_;
   }

   private LongTickDetector() {
      throw new RuntimeException("Synthetic constructor added by MCP, do not call");
   }
}
