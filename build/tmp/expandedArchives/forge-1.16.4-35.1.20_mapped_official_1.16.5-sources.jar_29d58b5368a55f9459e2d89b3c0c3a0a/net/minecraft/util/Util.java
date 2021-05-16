package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.util.ICharacterPredicate;
import net.minecraft.crash.ReportedException;
import net.minecraft.state.Property;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ExecutorService BOOTSTRAP_EXECUTOR = makeExecutor("Bootstrap");
   private static final ExecutorService BACKGROUND_EXECUTOR = makeExecutor("Main");
   private static final ExecutorService IO_POOL = makeIoExecutor();
   public static LongSupplier timeSource = System::nanoTime;
   public static final UUID NIL_UUID = new UUID(0L, 0L);
   private static final Logger LOGGER = LogManager.getLogger();

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String getPropertyName(Property<T> p_200269_0_, Object p_200269_1_) {
      return p_200269_0_.getName((T)(p_200269_1_));
   }

   public static String makeDescriptionId(String p_200697_0_, @Nullable ResourceLocation p_200697_1_) {
      return p_200697_1_ == null ? p_200697_0_ + ".unregistered_sadface" : p_200697_0_ + '.' + p_200697_1_.getNamespace() + '.' + p_200697_1_.getPath().replace('/', '.');
   }

   public static long getMillis() {
      return getNanos() / 1000000L;
   }

   public static long getNanos() {
      return timeSource.getAsLong();
   }

   public static long getEpochMillis() {
      return Instant.now().toEpochMilli();
   }

   private static ExecutorService makeExecutor(String p_240979_0_) {
      int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
      ExecutorService executorservice;
      if (i <= 0) {
         executorservice = MoreExecutors.newDirectExecutorService();
      } else {
         executorservice = new ForkJoinPool(i, (p_240981_1_) -> {
            ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(p_240981_1_) {
               protected void onTermination(Throwable p_onTermination_1_) {
                  if (p_onTermination_1_ != null) {
                     Util.LOGGER.warn("{} died", this.getName(), p_onTermination_1_);
                  } else {
                     Util.LOGGER.debug("{} shutdown", (Object)this.getName());
                  }

                  super.onTermination(p_onTermination_1_);
               }
            };
            forkjoinworkerthread.setName("Worker-" + p_240979_0_ + "-" + WORKER_COUNT.getAndIncrement());
            return forkjoinworkerthread;
         }, Util::onThreadException, true);
      }

      return executorservice;
   }

   public static Executor bootstrapExecutor() {
      return BOOTSTRAP_EXECUTOR;
   }

   public static Executor backgroundExecutor() {
      return BACKGROUND_EXECUTOR;
   }

   public static Executor ioPool() {
      return IO_POOL;
   }

   public static void shutdownExecutors() {
      shutdownExecutor(BACKGROUND_EXECUTOR);
      shutdownExecutor(IO_POOL);
   }

   private static void shutdownExecutor(ExecutorService p_240985_0_) {
      p_240985_0_.shutdown();

      boolean flag;
      try {
         flag = p_240985_0_.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException interruptedexception) {
         flag = false;
      }

      if (!flag) {
         p_240985_0_.shutdownNow();
      }

   }

   private static ExecutorService makeIoExecutor() {
      return Executors.newCachedThreadPool((p_240978_0_) -> {
         Thread thread = new Thread(p_240978_0_);
         thread.setName("IO-Worker-" + WORKER_COUNT.getAndIncrement());
         thread.setUncaughtExceptionHandler(Util::onThreadException);
         return thread;
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static <T> CompletableFuture<T> failedFuture(Throwable p_215087_0_) {
      CompletableFuture<T> completablefuture = new CompletableFuture<>();
      completablefuture.completeExceptionally(p_215087_0_);
      return completablefuture;
   }

   @OnlyIn(Dist.CLIENT)
   public static void throwAsRuntime(Throwable p_229756_0_) {
      throw p_229756_0_ instanceof RuntimeException ? (RuntimeException)p_229756_0_ : new RuntimeException(p_229756_0_);
   }

   private static void onThreadException(Thread p_240983_0_, Throwable p_240983_1_) {
      pauseInIde(p_240983_1_);
      if (p_240983_1_ instanceof CompletionException) {
         p_240983_1_ = p_240983_1_.getCause();
      }

      if (p_240983_1_ instanceof ReportedException) {
         Bootstrap.realStdoutPrintln(((ReportedException)p_240983_1_).getReport().getFriendlyReport());
         System.exit(-1);
      }

      LOGGER.error(String.format("Caught exception in thread %s", p_240983_0_), p_240983_1_);
   }

   @Nullable
   public static Type<?> fetchChoiceType(TypeReference p_240976_0_, String p_240976_1_) {
      return !SharedConstants.CHECK_DATA_FIXER_SCHEMA ? null : doFetchChoiceType(p_240976_0_, p_240976_1_);
   }

   @Nullable
   private static Type<?> doFetchChoiceType(TypeReference p_240990_0_, String p_240990_1_) {
      Type<?> type = null;

      try {
         type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(p_240990_0_, p_240990_1_);
      } catch (IllegalArgumentException illegalargumentexception) {
         LOGGER.error("No data fixer registered for {}", (Object)p_240990_1_);
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw illegalargumentexception;
         }
      }

      return type;
   }

   public static Util.OS getPlatform() {
      String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (s.contains("win")) {
         return Util.OS.WINDOWS;
      } else if (s.contains("mac")) {
         return Util.OS.OSX;
      } else if (s.contains("solaris")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("sunos")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("linux")) {
         return Util.OS.LINUX;
      } else {
         return s.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
      }
   }

   public static Stream<String> getVmArguments() {
      RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
      return runtimemxbean.getInputArguments().stream().filter((p_211566_0_) -> {
         return p_211566_0_.startsWith("-X");
      });
   }

   public static <T> T lastOf(List<T> p_223378_0_) {
      return p_223378_0_.get(p_223378_0_.size() - 1);
   }

   public static <T> T findNextInIterable(Iterable<T> p_195647_0_, @Nullable T p_195647_1_) {
      Iterator<T> iterator = p_195647_0_.iterator();
      T t = iterator.next();
      if (p_195647_1_ != null) {
         T t1 = t;

         while(t1 != p_195647_1_) {
            if (iterator.hasNext()) {
               t1 = iterator.next();
            }
         }

         if (iterator.hasNext()) {
            return iterator.next();
         }
      }

      return t;
   }

   public static <T> T findPreviousInIterable(Iterable<T> p_195648_0_, @Nullable T p_195648_1_) {
      Iterator<T> iterator = p_195648_0_.iterator();

      T t;
      T t1;
      for(t = null; iterator.hasNext(); t = t1) {
         t1 = iterator.next();
         if (t1 == p_195648_1_) {
            if (t == null) {
               t = (T)(iterator.hasNext() ? Iterators.getLast(iterator) : p_195648_1_);
            }
            break;
         }
      }

      return t;
   }

   public static <T> T make(Supplier<T> p_199748_0_) {
      return p_199748_0_.get();
   }

   public static <T> T make(T p_200696_0_, Consumer<T> p_200696_1_) {
      p_200696_1_.accept(p_200696_0_);
      return p_200696_0_;
   }

   public static <K> Strategy<K> identityStrategy() {
      return (Strategy<K>)Util.IdentityStrategy.INSTANCE;
   }

   public static <V> CompletableFuture<List<V>> sequence(List<? extends CompletableFuture<? extends V>> p_215079_0_) {
      List<V> list = Lists.newArrayListWithCapacity(p_215079_0_.size());
      CompletableFuture<?>[] completablefuture = new CompletableFuture[p_215079_0_.size()];
      CompletableFuture<Void> completablefuture1 = new CompletableFuture<>();
      p_215079_0_.forEach((p_215083_3_) -> {
         int i = list.size();
         list.add((V)null);
         completablefuture[i] = p_215083_3_.whenComplete((p_215085_3_, p_215085_4_) -> {
            if (p_215085_4_ != null) {
               completablefuture1.completeExceptionally(p_215085_4_);
            } else {
               list.set(i, p_215085_3_);
            }

         });
      });
      return CompletableFuture.allOf(completablefuture).applyToEither(completablefuture1, (p_215089_1_) -> {
         return list;
      });
   }

   public static <T> Stream<T> toStream(Optional<? extends T> p_215081_0_) {
      return DataFixUtils.orElseGet(p_215081_0_.map(Stream::of), Stream::empty);
   }

   public static <T> Optional<T> ifElse(Optional<T> p_215077_0_, Consumer<T> p_215077_1_, Runnable p_215077_2_) {
      if (p_215077_0_.isPresent()) {
         p_215077_1_.accept(p_215077_0_.get());
      } else {
         p_215077_2_.run();
      }

      return p_215077_0_;
   }

   public static Runnable name(Runnable p_215075_0_, Supplier<String> p_215075_1_) {
      return p_215075_0_;
   }

   public static <T extends Throwable> T pauseInIde(T p_229757_0_) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", p_229757_0_);

         while(true) {
            try {
               Thread.sleep(1000L);
               LOGGER.error("paused");
            } catch (InterruptedException interruptedexception) {
               return p_229757_0_;
            }
         }
      } else {
         return p_229757_0_;
      }
   }

   public static String describeError(Throwable p_229758_0_) {
      if (p_229758_0_.getCause() != null) {
         return describeError(p_229758_0_.getCause());
      } else {
         return p_229758_0_.getMessage() != null ? p_229758_0_.getMessage() : p_229758_0_.toString();
      }
   }

   public static <T> T getRandom(T[] p_240989_0_, Random p_240989_1_) {
      return p_240989_0_[p_240989_1_.nextInt(p_240989_0_.length)];
   }

   public static int getRandom(int[] p_240988_0_, Random p_240988_1_) {
      return p_240988_0_[p_240988_1_.nextInt(p_240988_0_.length)];
   }

   private static BooleanSupplier createRenamer(final Path p_244363_0_, final Path p_244363_1_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.move(p_244363_0_, p_244363_1_);
               return true;
            } catch (IOException ioexception) {
               Util.LOGGER.error("Failed to rename", (Throwable)ioexception);
               return false;
            }
         }

         public String toString() {
            return "rename " + p_244363_0_ + " to " + p_244363_1_;
         }
      };
   }

   private static BooleanSupplier createDeleter(final Path p_244362_0_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.deleteIfExists(p_244362_0_);
               return true;
            } catch (IOException ioexception) {
               Util.LOGGER.warn("Failed to delete", (Throwable)ioexception);
               return false;
            }
         }

         public String toString() {
            return "delete old " + p_244362_0_;
         }
      };
   }

   private static BooleanSupplier createFileDeletedCheck(final Path p_244366_0_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return !Files.exists(p_244366_0_);
         }

         public String toString() {
            return "verify that " + p_244366_0_ + " is deleted";
         }
      };
   }

   private static BooleanSupplier createFileCreatedCheck(final Path p_244367_0_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return Files.isRegularFile(p_244367_0_);
         }

         public String toString() {
            return "verify that " + p_244367_0_ + " is present";
         }
      };
   }

   private static boolean executeInSequence(BooleanSupplier... p_244365_0_) {
      for(BooleanSupplier booleansupplier : p_244365_0_) {
         if (!booleansupplier.getAsBoolean()) {
            LOGGER.warn("Failed to execute {}", (Object)booleansupplier);
            return false;
         }
      }

      return true;
   }

   private static boolean runWithRetries(int p_244359_0_, String p_244359_1_, BooleanSupplier... p_244359_2_) {
      for(int i = 0; i < p_244359_0_; ++i) {
         if (executeInSequence(p_244359_2_)) {
            return true;
         }

         LOGGER.error("Failed to {}, retrying {}/{}", p_244359_1_, i, p_244359_0_);
      }

      LOGGER.error("Failed to {}, aborting, progress might be lost", (Object)p_244359_1_);
      return false;
   }

   public static void safeReplaceFile(File p_240977_0_, File p_240977_1_, File p_240977_2_) {
      safeReplaceFile(p_240977_0_.toPath(), p_240977_1_.toPath(), p_240977_2_.toPath());
   }

   public static void safeReplaceFile(Path p_244364_0_, Path p_244364_1_, Path p_244364_2_) {
      int i = 10;
      if (!Files.exists(p_244364_0_) || runWithRetries(10, "create backup " + p_244364_2_, createDeleter(p_244364_2_), createRenamer(p_244364_0_, p_244364_2_), createFileCreatedCheck(p_244364_2_))) {
         if (runWithRetries(10, "remove old " + p_244364_0_, createDeleter(p_244364_0_), createFileDeletedCheck(p_244364_0_))) {
            if (!runWithRetries(10, "replace " + p_244364_0_ + " with " + p_244364_1_, createRenamer(p_244364_1_, p_244364_0_), createFileCreatedCheck(p_244364_0_))) {
               runWithRetries(10, "restore " + p_244364_0_ + " from " + p_244364_2_, createRenamer(p_244364_2_, p_244364_0_), createFileCreatedCheck(p_244364_0_));
            }

         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static int offsetByCodepoints(String p_240980_0_, int p_240980_1_, int p_240980_2_) {
      int i = p_240980_0_.length();
      if (p_240980_2_ >= 0) {
         for(int j = 0; p_240980_1_ < i && j < p_240980_2_; ++j) {
            if (Character.isHighSurrogate(p_240980_0_.charAt(p_240980_1_++)) && p_240980_1_ < i && Character.isLowSurrogate(p_240980_0_.charAt(p_240980_1_))) {
               ++p_240980_1_;
            }
         }
      } else {
         for(int k = p_240980_2_; p_240980_1_ > 0 && k < 0; ++k) {
            --p_240980_1_;
            if (Character.isLowSurrogate(p_240980_0_.charAt(p_240980_1_)) && p_240980_1_ > 0 && Character.isHighSurrogate(p_240980_0_.charAt(p_240980_1_ - 1))) {
               --p_240980_1_;
            }
         }
      }

      return p_240980_1_;
   }

   public static Consumer<String> prefix(String p_240982_0_, Consumer<String> p_240982_1_) {
      return (p_240986_2_) -> {
         p_240982_1_.accept(p_240982_0_ + p_240986_2_);
      };
   }

   public static DataResult<int[]> fixedSize(IntStream p_240987_0_, int p_240987_1_) {
      int[] aint = p_240987_0_.limit((long)(p_240987_1_ + 1)).toArray();
      if (aint.length != p_240987_1_) {
         String s = "Input is not a list of " + p_240987_1_ + " ints";
         return aint.length >= p_240987_1_ ? DataResult.error(s, Arrays.copyOf(aint, p_240987_1_)) : DataResult.error(s);
      } else {
         return DataResult.success(aint);
      }
   }

   public static void startTimerHackThread() {
      Thread thread = new Thread("Timer hack thread") {
         public void run() {
            while(true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException interruptedexception) {
                  Util.LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                  return;
               }
            }
         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   @OnlyIn(Dist.CLIENT)
   public static void copyBetweenDirs(Path p_240984_0_, Path p_240984_1_, Path p_240984_2_) throws IOException {
      Path path = p_240984_0_.relativize(p_240984_2_);
      Path path1 = p_240984_1_.resolve(path);
      Files.copy(p_240984_2_, path1);
   }

   @OnlyIn(Dist.CLIENT)
   public static String sanitizeName(String p_244361_0_, ICharacterPredicate p_244361_1_) {
      return p_244361_0_.toLowerCase(Locale.ROOT).chars().mapToObj((p_244360_1_) -> {
         return p_244361_1_.test((char)p_244360_1_) ? Character.toString((char)p_244360_1_) : "_";
      }).collect(Collectors.joining());
   }

   static enum IdentityStrategy implements Strategy<Object> {
      INSTANCE;

      public int hashCode(Object p_hashCode_1_) {
         return System.identityHashCode(p_hashCode_1_);
      }

      public boolean equals(Object p_equals_1_, Object p_equals_2_) {
         return p_equals_1_ == p_equals_2_;
      }
   }

   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenUrlArguments(URL p_195643_1_) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", p_195643_1_.toString()};
         }
      },
      OSX {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenUrlArguments(URL p_195643_1_) {
            return new String[]{"open", p_195643_1_.toString()};
         }
      },
      UNKNOWN;

      private OS() {
      }

      @OnlyIn(Dist.CLIENT)
      public void openUrl(URL p_195639_1_) {
         try {
            Process process = AccessController.doPrivileged((PrivilegedExceptionAction<Process>)(() -> {
               return Runtime.getRuntime().exec(this.getOpenUrlArguments(p_195639_1_));
            }));

            for(String s : IOUtils.readLines(process.getErrorStream())) {
               Util.LOGGER.error(s);
            }

            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
         } catch (IOException | PrivilegedActionException privilegedactionexception) {
            Util.LOGGER.error("Couldn't open url '{}'", p_195639_1_, privilegedactionexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openUri(URI p_195642_1_) {
         try {
            this.openUrl(p_195642_1_.toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195642_1_, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openFile(File p_195641_1_) {
         try {
            this.openUrl(p_195641_1_.toURI().toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open file '{}'", p_195641_1_, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      protected String[] getOpenUrlArguments(URL p_195643_1_) {
         String s = p_195643_1_.toString();
         if ("file".equals(p_195643_1_.getProtocol())) {
            s = s.replace("file:", "file://");
         }

         return new String[]{"xdg-open", s};
      }

      @OnlyIn(Dist.CLIENT)
      public void openUri(String p_195640_1_) {
         try {
            this.openUrl((new URI(p_195640_1_)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException urisyntaxexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195640_1_, urisyntaxexception);
         }

      }
   }
}
