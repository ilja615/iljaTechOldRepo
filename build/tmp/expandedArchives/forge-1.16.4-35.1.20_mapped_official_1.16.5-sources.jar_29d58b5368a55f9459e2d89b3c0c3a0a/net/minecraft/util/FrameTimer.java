package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FrameTimer {
   private final long[] loggedTimes = new long[240];
   private int logStart;
   private int logLength;
   private int logEnd;

   public void logFrameDuration(long p_181747_1_) {
      this.loggedTimes[this.logEnd] = p_181747_1_;
      ++this.logEnd;
      if (this.logEnd == 240) {
         this.logEnd = 0;
      }

      if (this.logLength < 240) {
         this.logStart = 0;
         ++this.logLength;
      } else {
         this.logStart = this.wrapIndex(this.logEnd + 1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int scaleSampleTo(long p_219792_1_, int p_219792_3_, int p_219792_4_) {
      double d0 = (double)p_219792_1_ / (double)(1000000000L / (long)p_219792_4_);
      return (int)(d0 * (double)p_219792_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getLogStart() {
      return this.logStart;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLogEnd() {
      return this.logEnd;
   }

   public int wrapIndex(int p_181751_1_) {
      return p_181751_1_ % 240;
   }

   @OnlyIn(Dist.CLIENT)
   public long[] getLog() {
      return this.loggedTimes;
   }
}
