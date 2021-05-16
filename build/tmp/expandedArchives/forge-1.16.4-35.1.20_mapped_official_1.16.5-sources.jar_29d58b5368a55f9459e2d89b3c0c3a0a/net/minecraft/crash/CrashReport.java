package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String title;
   private final Throwable exception;
   private final CrashReportCategory systemDetails = new CrashReportCategory(this, "System Details");
   private final List<CrashReportCategory> details = Lists.newArrayList();
   private File saveFile;
   private boolean trackingStackTrace = true;
   private StackTraceElement[] uncategorizedStackTrace = new StackTraceElement[0];

   public CrashReport(String p_i1348_1_, Throwable p_i1348_2_) {
      this.title = p_i1348_1_;
      this.exception = p_i1348_2_;
      this.initDetails();
   }

   private void initDetails() {
      this.systemDetails.setDetail("Minecraft Version", () -> {
         return SharedConstants.getCurrentVersion().getName();
      });
      this.systemDetails.setDetail("Minecraft Version ID", () -> {
         return SharedConstants.getCurrentVersion().getId();
      });
      this.systemDetails.setDetail("Operating System", () -> {
         return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
      });
      this.systemDetails.setDetail("Java Version", () -> {
         return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
      });
      this.systemDetails.setDetail("Java VM Version", () -> {
         return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
      });
      this.systemDetails.setDetail("Memory", () -> {
         Runtime runtime = Runtime.getRuntime();
         long i = runtime.maxMemory();
         long j = runtime.totalMemory();
         long k = runtime.freeMemory();
         long l = i / 1024L / 1024L;
         long i1 = j / 1024L / 1024L;
         long j1 = k / 1024L / 1024L;
         return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
      });
      this.systemDetails.setDetail("CPUs", Runtime.getRuntime().availableProcessors());
      this.systemDetails.setDetail("JVM Flags", () -> {
         List<String> list = Util.getVmArguments().collect(Collectors.toList());
         return String.format("%d total; %s", list.size(), list.stream().collect(Collectors.joining(" ")));
      });
      net.minecraftforge.fml.CrashReportExtender.enhanceCrashReport(this, this.systemDetails);
   }

   public String getTitle() {
      return this.title;
   }

   public Throwable getException() {
      return this.exception;
   }

   public void getDetails(StringBuilder p_71506_1_) {
      if ((this.uncategorizedStackTrace == null || this.uncategorizedStackTrace.length <= 0) && !this.details.isEmpty()) {
         this.uncategorizedStackTrace = ArrayUtils.subarray(this.details.get(0).getStacktrace(), 0, 1);
      }

      if (this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
         p_71506_1_.append("-- Head --\n");
         p_71506_1_.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         p_71506_1_.append("Stacktrace:");
         p_71506_1_.append(net.minecraftforge.fml.CrashReportExtender.generateEnhancedStackTrace(this.uncategorizedStackTrace));
      }

      for(CrashReportCategory crashreportcategory : this.details) {
         crashreportcategory.getDetails(p_71506_1_);
         p_71506_1_.append("\n\n");
      }

      this.systemDetails.getDetails(p_71506_1_);
   }

   public String getExceptionMessage() {
      StringWriter stringwriter = null;
      PrintWriter printwriter = null;
      Throwable throwable = this.exception;
      if (throwable.getMessage() == null) {
         if (throwable instanceof NullPointerException) {
            throwable = new NullPointerException(this.title);
         } else if (throwable instanceof StackOverflowError) {
            throwable = new StackOverflowError(this.title);
         } else if (throwable instanceof OutOfMemoryError) {
            throwable = new OutOfMemoryError(this.title);
         }

         throwable.setStackTrace(this.exception.getStackTrace());
      }

      return net.minecraftforge.fml.CrashReportExtender.generateEnhancedStackTrace(throwable);
   }

   public String getFriendlyReport() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Crash Report ----\n");
      net.minecraftforge.fml.CrashReportExtender.addCrashReportHeader(stringbuilder, this);
      stringbuilder.append("// ");
      stringbuilder.append(getErrorComment());
      stringbuilder.append("\n\n");
      stringbuilder.append("Time: ");
      stringbuilder.append((new SimpleDateFormat()).format(new Date()));
      stringbuilder.append("\n");
      stringbuilder.append("Description: ");
      stringbuilder.append(this.title);
      stringbuilder.append("\n\n");
      stringbuilder.append(this.getExceptionMessage());
      stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int i = 0; i < 87; ++i) {
         stringbuilder.append("-");
      }

      stringbuilder.append("\n\n");
      this.getDetails(stringbuilder);
      return stringbuilder.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public File getSaveFile() {
      return this.saveFile;
   }

   public boolean saveToFile(File p_147149_1_) {
      if (this.saveFile != null) {
         return false;
      } else {
         if (p_147149_1_.getParentFile() != null) {
            p_147149_1_.getParentFile().mkdirs();
         }

         Writer writer = null;

         boolean flag;
         try {
            writer = new OutputStreamWriter(new FileOutputStream(p_147149_1_), StandardCharsets.UTF_8);
            writer.write(this.getFriendlyReport());
            this.saveFile = p_147149_1_;
            return true;
         } catch (Throwable throwable) {
            LOGGER.error("Could not save crash report to {}", p_147149_1_, throwable);
            flag = false;
         } finally {
            IOUtils.closeQuietly(writer);
         }

         return flag;
      }
   }

   public CrashReportCategory getSystemDetails() {
      return this.systemDetails;
   }

   public CrashReportCategory addCategory(String p_85058_1_) {
      return this.addCategory(p_85058_1_, 1);
   }

   public CrashReportCategory addCategory(String p_85057_1_, int p_85057_2_) {
      CrashReportCategory crashreportcategory = new CrashReportCategory(this, p_85057_1_);
      if (this.trackingStackTrace) {
         int i = crashreportcategory.fillInStackTrace(p_85057_2_);
         StackTraceElement[] astacktraceelement = this.exception.getStackTrace();
         StackTraceElement stacktraceelement = null;
         StackTraceElement stacktraceelement1 = null;
         int j = astacktraceelement.length - i;
         if (j < 0) {
            System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");
         }

         if (astacktraceelement != null && 0 <= j && j < astacktraceelement.length) {
            stacktraceelement = astacktraceelement[j];
            if (astacktraceelement.length + 1 - i < astacktraceelement.length) {
               stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
            }
         }

         this.trackingStackTrace = crashreportcategory.validateStackTrace(stacktraceelement, stacktraceelement1);
         if (i > 0 && !this.details.isEmpty()) {
            CrashReportCategory crashreportcategory1 = this.details.get(this.details.size() - 1);
            crashreportcategory1.trimStacktrace(i);
         } else if (astacktraceelement != null && astacktraceelement.length >= i && 0 <= j && j < astacktraceelement.length) {
            this.uncategorizedStackTrace = new StackTraceElement[j];
            System.arraycopy(astacktraceelement, 0, this.uncategorizedStackTrace, 0, this.uncategorizedStackTrace.length);
         } else {
            this.trackingStackTrace = false;
         }
      }

      this.details.add(crashreportcategory);
      return crashreportcategory;
   }

   private static String getErrorComment() {
      String[] astring = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

      try {
         return astring[(int)(Util.getNanos() % (long)astring.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport forThrowable(Throwable p_85055_0_, String p_85055_1_) {
      while(p_85055_0_ instanceof CompletionException && p_85055_0_.getCause() != null) {
         p_85055_0_ = p_85055_0_.getCause();
      }

      CrashReport crashreport;
      if (p_85055_0_ instanceof ReportedException) {
         crashreport = ((ReportedException)p_85055_0_).getReport();
      } else {
         crashreport = new CrashReport(p_85055_1_, p_85055_0_);
      }

      return crashreport;
   }

   public static void preload() {
      (new CrashReport("Don't panic!", new Throwable())).getFriendlyReport();
   }
}
