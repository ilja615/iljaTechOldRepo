package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyProfiler implements IResultableProfiler {
   public static final EmptyProfiler INSTANCE = new EmptyProfiler();

   private EmptyProfiler() {
   }

   public void startTick() {
   }

   public void endTick() {
   }

   public void push(String p_76320_1_) {
   }

   public void push(Supplier<String> p_194340_1_) {
   }

   public void pop() {
   }

   public void popPush(String p_219895_1_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void popPush(Supplier<String> p_194339_1_) {
   }

   public void incrementCounter(String p_230035_1_) {
   }

   public void incrementCounter(Supplier<String> p_230036_1_) {
   }

   public IProfileResult getResults() {
      return EmptyProfileResult.EMPTY;
   }
}
