package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Unit;

/**
 * @deprecated Forge: {@link net.minecraftforge.resource.ISelectiveResourceReloadListener}, which selectively allows
 * individual resource types being reloaded should rather be used where possible.
 */
@Deprecated
public interface IResourceManagerReloadListener extends IFutureReloadListener {
   default CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      return p_215226_1_.wait(Unit.INSTANCE).thenRunAsync(() -> {
         p_215226_4_.startTick();
         p_215226_4_.push("listener");
         this.onResourceManagerReload(p_215226_2_);
         p_215226_4_.pop();
         p_215226_4_.endTick();
      }, p_215226_6_);
   }

   void onResourceManagerReload(IResourceManager p_195410_1_);

   @javax.annotation.Nullable
   default net.minecraftforge.resource.IResourceType getResourceType() {
      return null;
   }
}
