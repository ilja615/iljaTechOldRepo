package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHangWatchdog implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DedicatedServer server;
   private final long maxTickTime;

   public ServerHangWatchdog(DedicatedServer p_i46310_1_) {
      this.server = p_i46310_1_;
      this.maxTickTime = p_i46310_1_.getMaxTickLength();
   }

   public void run() {
      while(this.server.isRunning()) {
         long i = this.server.getNextTickTime();
         long j = Util.getMillis();
         long k = j - i;
         if (k > this.maxTickTime) {
            LOGGER.fatal("A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float)k / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
            LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
            ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
            StringBuilder stringbuilder = new StringBuilder();
            Error error = new Error(String.format("ServerHangWatchdog detected that a single server tick took %.2f seconds (should be max 0.05)", k / 1000F)); // Forge: don't just make a crash report with a seemingly-inexplicable Error

            for(ThreadInfo threadinfo : athreadinfo) {
               if (threadinfo.getThreadId() == this.server.getRunningThread().getId()) {
                  error.setStackTrace(threadinfo.getStackTrace());
               }

               stringbuilder.append((Object)threadinfo);
               stringbuilder.append("\n");
            }

            CrashReport crashreport = new CrashReport("Watching Server", error);
            this.server.fillReport(crashreport);
            CrashReportCategory crashreportcategory = crashreport.addCategory("Thread Dump");
            crashreportcategory.setDetail("Threads", stringbuilder);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Performance stats");
            crashreportcategory1.setDetail("Random tick rate", () -> {
               return this.server.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).toString();
            });
            crashreportcategory1.setDetail("Level stats", () -> {
               return Streams.stream(this.server.getAllLevels()).map((p_244716_0_) -> {
                  return p_244716_0_.dimension() + ": " + p_244716_0_.getWatchdogStats();
               }).collect(Collectors.joining(",\n"));
            });
            Bootstrap.realStdoutPrintln("Crash report:\n" + crashreport.getFriendlyReport());
            File file1 = new File(new File(this.server.getServerDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
            if (crashreport.saveToFile(file1)) {
               LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
            } else {
               LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.exit();
         }

         try {
            Thread.sleep(i + this.maxTickTime - j);
         } catch (InterruptedException interruptedexception) {
         }
      }

   }

   private void exit() {
      try {
         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
            public void run() {
               Runtime.getRuntime().halt(1);
            }
         }, 10000L);
         System.exit(1);
      } catch (Throwable throwable) {
         Runtime.getRuntime().halt(1);
      }

   }
}
