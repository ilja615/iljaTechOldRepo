package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AsyncReloader<S> implements IAsyncReloader {
   protected final IResourceManager resourceManager;
   protected final CompletableFuture<Unit> allPreparations = new CompletableFuture<>();
   protected final CompletableFuture<List<S>> allDone;
   private final Set<IFutureReloadListener> preparingListeners;
   private final int listenerCount;
   private int startedReloads;
   private int finishedReloads;
   private final AtomicInteger startedTaskCounter = new AtomicInteger();
   private final AtomicInteger doneTaskCounter = new AtomicInteger();

   public static AsyncReloader<Void> of(IResourceManager p_219562_0_, List<IFutureReloadListener> p_219562_1_, Executor p_219562_2_, Executor p_219562_3_, CompletableFuture<Unit> p_219562_4_) {
      return new AsyncReloader<>(p_219562_2_, p_219562_3_, p_219562_0_, p_219562_1_, (p_219561_1_, p_219561_2_, p_219561_3_, p_219561_4_, p_219561_5_) -> {
         return p_219561_3_.reload(p_219561_1_, p_219561_2_, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, p_219562_2_, p_219561_5_);
      }, p_219562_4_);
   }

   protected AsyncReloader(Executor p_i50690_1_, final Executor p_i50690_2_, IResourceManager p_i50690_3_, List<IFutureReloadListener> p_i50690_4_, AsyncReloader.IStateFactory<S> p_i50690_5_, CompletableFuture<Unit> p_i50690_6_) {
      this.resourceManager = p_i50690_3_;
      this.listenerCount = p_i50690_4_.size();
      this.startedTaskCounter.incrementAndGet();
      p_i50690_6_.thenRun(this.doneTaskCounter::incrementAndGet);
      List<CompletableFuture<S>> list = Lists.newArrayList();
      CompletableFuture<?> completablefuture = p_i50690_6_;
      this.preparingListeners = Sets.newHashSet(p_i50690_4_);

      for(final IFutureReloadListener ifuturereloadlistener : p_i50690_4_) {
         final CompletableFuture<?> completablefuture1 = completablefuture;
         CompletableFuture<S> completablefuture2 = p_i50690_5_.create(new IFutureReloadListener.IStage() {
            public <T> CompletableFuture<T> wait(T p_216872_1_) {
               p_i50690_2_.execute(() -> {
                  AsyncReloader.this.preparingListeners.remove(ifuturereloadlistener);
                  if (AsyncReloader.this.preparingListeners.isEmpty()) {
                     AsyncReloader.this.allPreparations.complete(Unit.INSTANCE);
                  }

               });
               return AsyncReloader.this.allPreparations.thenCombine(completablefuture1, (p_216874_1_, p_216874_2_) -> {
                  return p_216872_1_;
               });
            }
         }, p_i50690_3_, ifuturereloadlistener, (p_219564_2_) -> {
            this.startedTaskCounter.incrementAndGet();
            p_i50690_1_.execute(() -> {
               p_219564_2_.run();
               this.doneTaskCounter.incrementAndGet();
            });
         }, (p_219560_2_) -> {
            ++this.startedReloads;
            p_i50690_2_.execute(() -> {
               p_219560_2_.run();
               ++this.finishedReloads;
            });
         });
         list.add(completablefuture2);
         completablefuture = completablefuture2;
      }

      this.allDone = Util.sequence(list);
   }

   public CompletableFuture<Unit> done() {
      return this.allDone.thenApply((p_219558_0_) -> {
         return Unit.INSTANCE;
      });
   }

   @OnlyIn(Dist.CLIENT)
   public float getActualProgress() {
      int i = this.listenerCount - this.preparingListeners.size();
      float f = (float)(this.doneTaskCounter.get() * 2 + this.finishedReloads * 2 + i * 1);
      float f1 = (float)(this.startedTaskCounter.get() * 2 + this.startedReloads * 2 + this.listenerCount * 1);
      return f / f1;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isApplying() {
      return this.allPreparations.isDone();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isDone() {
      return this.allDone.isDone();
   }

   @OnlyIn(Dist.CLIENT)
   public void checkExceptions() {
      if (this.allDone.isCompletedExceptionally()) {
         this.allDone.join();
      }

   }

   public interface IStateFactory<S> {
      CompletableFuture<S> create(IFutureReloadListener.IStage p_create_1_, IResourceManager p_create_2_, IFutureReloadListener p_create_3_, Executor p_create_4_, Executor p_create_5_);
   }
}
