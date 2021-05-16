package net.minecraft.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;

public interface IReloadableResourceManager extends IResourceManager, AutoCloseable {
   default CompletableFuture<Unit> reload(Executor p_219536_1_, Executor p_219536_2_, List<IResourcePack> p_219536_3_, CompletableFuture<Unit> p_219536_4_) {
      return this.createFullReload(p_219536_1_, p_219536_2_, p_219536_4_, p_219536_3_).done();
   }

   IAsyncReloader createFullReload(Executor p_219537_1_, Executor p_219537_2_, CompletableFuture<Unit> p_219537_3_, List<IResourcePack> p_219537_4_);

   void registerReloadListener(IFutureReloadListener p_219534_1_);

   void close();
}
