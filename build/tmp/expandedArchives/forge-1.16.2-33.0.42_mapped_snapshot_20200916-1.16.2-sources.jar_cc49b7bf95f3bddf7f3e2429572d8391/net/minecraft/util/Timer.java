package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Timer {
   public float renderPartialTicks;
   public float elapsedPartialTicks;
   private long lastSyncSysClock;
   private final float tickLength;

   public Timer(float ticks, long lastSyncSysClock) {
      this.tickLength = 1000.0F / ticks;
      this.lastSyncSysClock = lastSyncSysClock;
   }

   public int getPartialTicks(long gameTime) {
      this.elapsedPartialTicks = (float)(gameTime - this.lastSyncSysClock) / this.tickLength;
      this.lastSyncSysClock = gameTime;
      this.renderPartialTicks += this.elapsedPartialTicks;
      int i = (int)this.renderPartialTicks;
      this.renderPartialTicks -= (float)i;
      return i;
   }
}