package net.minecraft.resources;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugAsyncReloader extends AsyncReloader<DebugAsyncReloader.DataPoint> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Stopwatch total = Stopwatch.createUnstarted();

   public DebugAsyncReloader(IResourceManager p_i50694_1_, List<IFutureReloadListener> p_i50694_2_, Executor p_i50694_3_, Executor p_i50694_4_, CompletableFuture<Unit> p_i50694_5_) {
      super(p_i50694_3_, p_i50694_4_, p_i50694_1_, p_i50694_2_, (p_219578_1_, p_219578_2_, p_219578_3_, p_219578_4_, p_219578_5_) -> {
         AtomicLong atomiclong = new AtomicLong();
         AtomicLong atomiclong1 = new AtomicLong();
         Profiler profiler = new Profiler(Util.timeSource, () -> {
            return 0;
         }, false);
         Profiler profiler1 = new Profiler(Util.timeSource, () -> {
            return 0;
         }, false);
         CompletableFuture<Void> completablefuture = p_219578_3_.reload(p_219578_1_, p_219578_2_, profiler, profiler1, (p_219577_2_) -> {
            p_219578_4_.execute(() -> {
               long i = Util.getNanos();
               p_219577_2_.run();
               atomiclong.addAndGet(Util.getNanos() - i);
            });
         }, (p_219574_2_) -> {
            p_219578_5_.execute(() -> {
               long i = Util.getNanos();
               p_219574_2_.run();
               atomiclong1.addAndGet(Util.getNanos() - i);
            });
         });
         return completablefuture.thenApplyAsync((p_219576_5_) -> {
            return new DebugAsyncReloader.DataPoint(p_219578_3_.getName(), profiler.getResults(), profiler1.getResults(), atomiclong, atomiclong1);
         }, p_i50694_4_);
      }, p_i50694_5_);
      this.total.start();
      this.allDone.thenAcceptAsync(this::finish, p_i50694_4_);
   }

   private void finish(List<DebugAsyncReloader.DataPoint> p_219575_1_) {
      this.total.stop();
      int i = 0;
      LOGGER.info("Resource reload finished after " + this.total.elapsed(TimeUnit.MILLISECONDS) + " ms");

      for(DebugAsyncReloader.DataPoint debugasyncreloader$datapoint : p_219575_1_) {
         IProfileResult iprofileresult = debugasyncreloader$datapoint.preparationResult;
         IProfileResult iprofileresult1 = debugasyncreloader$datapoint.reloadResult;
         int j = (int)((double)debugasyncreloader$datapoint.preparationNanos.get() / 1000000.0D);
         int k = (int)((double)debugasyncreloader$datapoint.reloadNanos.get() / 1000000.0D);
         int l = j + k;
         String s = debugasyncreloader$datapoint.name;
         LOGGER.info(s + " took approximately " + l + " ms (" + j + " ms preparing, " + k + " ms applying)");
         i += k;
      }

      LOGGER.info("Total blocking time: " + i + " ms");
   }

   public static class DataPoint {
      private final String name;
      private final IProfileResult preparationResult;
      private final IProfileResult reloadResult;
      private final AtomicLong preparationNanos;
      private final AtomicLong reloadNanos;

      private DataPoint(String p_i50542_1_, IProfileResult p_i50542_2_, IProfileResult p_i50542_3_, AtomicLong p_i50542_4_, AtomicLong p_i50542_5_) {
         this.name = p_i50542_1_;
         this.preparationResult = p_i50542_2_;
         this.reloadResult = p_i50542_3_;
         this.preparationNanos = p_i50542_4_;
         this.reloadNanos = p_i50542_5_;
      }
   }
}
