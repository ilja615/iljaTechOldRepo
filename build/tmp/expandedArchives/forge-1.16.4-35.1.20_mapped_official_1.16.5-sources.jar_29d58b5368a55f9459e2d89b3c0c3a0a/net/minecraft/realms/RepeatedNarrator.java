package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RepeatedNarrator {
   private final float permitsPerSecond;
   private final AtomicReference<RepeatedNarrator.Parameter> params = new AtomicReference<>();

   public RepeatedNarrator(Duration p_i49961_1_) {
      this.permitsPerSecond = 1000.0F / (float)p_i49961_1_.toMillis();
   }

   public void narrate(String p_231415_1_) {
      RepeatedNarrator.Parameter repeatednarrator$parameter = this.params.updateAndGet((p_229956_2_) -> {
         return p_229956_2_ != null && p_231415_1_.equals(p_229956_2_.narration) ? p_229956_2_ : new RepeatedNarrator.Parameter(p_231415_1_, RateLimiter.create((double)this.permitsPerSecond));
      });
      if (repeatednarrator$parameter.rateLimiter.tryAcquire(1)) {
         NarratorChatListener.INSTANCE.handle(ChatType.SYSTEM, new StringTextComponent(p_231415_1_), Util.NIL_UUID);
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class Parameter {
      private final String narration;
      private final RateLimiter rateLimiter;

      Parameter(String p_i50913_1_, RateLimiter p_i50913_2_) {
         this.narration = p_i50913_1_;
         this.rateLimiter = p_i50913_2_;
      }
   }
}
