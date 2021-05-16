package net.minecraft.world.chunk.listener;

import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingChunkStatusListener implements IChunkStatusListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int maxCount;
   private int count;
   private long startTime;
   private long nextTickTime = Long.MAX_VALUE;

   public LoggingChunkStatusListener(int p_i50697_1_) {
      int i = p_i50697_1_ * 2 + 1;
      this.maxCount = i * i;
   }

   public void updateSpawnPos(ChunkPos p_219509_1_) {
      this.nextTickTime = Util.getMillis();
      this.startTime = this.nextTickTime;
   }

   public void onStatusChange(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_) {
      if (p_219508_2_ == ChunkStatus.FULL) {
         ++this.count;
      }

      int i = this.getProgress();
      if (Util.getMillis() > this.nextTickTime) {
         this.nextTickTime += 500L;
         LOGGER.info((new TranslationTextComponent("menu.preparingSpawn", MathHelper.clamp(i, 0, 100))).getString());
      }

   }

   public void stop() {
      LOGGER.info("Time elapsed: {} ms", (long)(Util.getMillis() - this.startTime));
      this.nextTickTime = Long.MAX_VALUE;
   }

   public int getProgress() {
      return MathHelper.floor((float)this.count * 100.0F / (float)this.maxCount);
   }
}
