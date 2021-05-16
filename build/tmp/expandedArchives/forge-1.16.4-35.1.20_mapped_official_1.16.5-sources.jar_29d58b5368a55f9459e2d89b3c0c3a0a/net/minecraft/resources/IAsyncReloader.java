package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAsyncReloader {
   CompletableFuture<Unit> done();

   @OnlyIn(Dist.CLIENT)
   float getActualProgress();

   @OnlyIn(Dist.CLIENT)
   boolean isApplying();

   @OnlyIn(Dist.CLIENT)
   boolean isDone();

   @OnlyIn(Dist.CLIENT)
   void checkExceptions();
}
