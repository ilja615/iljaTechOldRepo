package net.minecraft.client.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;

public abstract class ReloadListener<T> implements IFutureReloadListener {
   public final CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      return CompletableFuture.supplyAsync(() -> {
         return this.prepare(p_215226_2_, p_215226_3_);
      }, p_215226_5_).thenCompose(p_215226_1_::wait).thenAcceptAsync((p_215269_3_) -> {
         this.apply(p_215269_3_, p_215226_2_, p_215226_4_);
      }, p_215226_6_);
   }

   protected abstract T prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_);

   protected abstract void apply(T p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_);
}
