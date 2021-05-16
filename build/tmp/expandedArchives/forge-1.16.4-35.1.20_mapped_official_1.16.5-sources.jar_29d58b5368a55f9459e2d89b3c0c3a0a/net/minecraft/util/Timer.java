package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Timer {
   public float partialTick;
   public float tickDelta;
   private long lastMs;
   private final float msPerTick;

   public Timer(float p_i49528_1_, long p_i49528_2_) {
      this.msPerTick = 1000.0F / p_i49528_1_;
      this.lastMs = p_i49528_2_;
   }

   public int advanceTime(long p_238400_1_) {
      this.tickDelta = (float)(p_238400_1_ - this.lastMs) / this.msPerTick;
      this.lastMs = p_238400_1_;
      this.partialTick += this.tickDelta;
      int i = (int)this.partialTick;
      this.partialTick -= (float)i;
      return i;
   }
}
