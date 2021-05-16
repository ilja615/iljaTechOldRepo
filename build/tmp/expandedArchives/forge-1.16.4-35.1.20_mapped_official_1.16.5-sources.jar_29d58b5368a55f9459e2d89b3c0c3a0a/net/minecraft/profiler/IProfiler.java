package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProfiler {
   void startTick();

   void endTick();

   void push(String p_76320_1_);

   void push(Supplier<String> p_194340_1_);

   void pop();

   void popPush(String p_219895_1_);

   @OnlyIn(Dist.CLIENT)
   void popPush(Supplier<String> p_194339_1_);

   void incrementCounter(String p_230035_1_);

   void incrementCounter(Supplier<String> p_230036_1_);

   static IProfiler tee(final IProfiler p_233513_0_, final IProfiler p_233513_1_) {
      if (p_233513_0_ == EmptyProfiler.INSTANCE) {
         return p_233513_1_;
      } else {
         return p_233513_1_ == EmptyProfiler.INSTANCE ? p_233513_0_ : new IProfiler() {
            public void startTick() {
               p_233513_0_.startTick();
               p_233513_1_.startTick();
            }

            public void endTick() {
               p_233513_0_.endTick();
               p_233513_1_.endTick();
            }

            public void push(String p_76320_1_) {
               p_233513_0_.push(p_76320_1_);
               p_233513_1_.push(p_76320_1_);
            }

            public void push(Supplier<String> p_194340_1_) {
               p_233513_0_.push(p_194340_1_);
               p_233513_1_.push(p_194340_1_);
            }

            public void pop() {
               p_233513_0_.pop();
               p_233513_1_.pop();
            }

            public void popPush(String p_219895_1_) {
               p_233513_0_.popPush(p_219895_1_);
               p_233513_1_.popPush(p_219895_1_);
            }

            @OnlyIn(Dist.CLIENT)
            public void popPush(Supplier<String> p_194339_1_) {
               p_233513_0_.popPush(p_194339_1_);
               p_233513_1_.popPush(p_194339_1_);
            }

            public void incrementCounter(String p_230035_1_) {
               p_233513_0_.incrementCounter(p_230035_1_);
               p_233513_1_.incrementCounter(p_230035_1_);
            }

            public void incrementCounter(Supplier<String> p_230036_1_) {
               p_233513_0_.incrementCounter(p_230036_1_);
               p_233513_1_.incrementCounter(p_230036_1_);
            }
         };
      }
   }
}
