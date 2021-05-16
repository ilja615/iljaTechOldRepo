package net.minecraft.world.chunk.listener;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrackingChunkStatusListener implements IChunkStatusListener {
   private final LoggingChunkStatusListener delegate;
   private final Long2ObjectOpenHashMap<ChunkStatus> statuses;
   private ChunkPos spawnPos = new ChunkPos(0, 0);
   private final int fullDiameter;
   private final int radius;
   private final int diameter;
   private boolean started;

   public TrackingChunkStatusListener(int p_i50695_1_) {
      this.delegate = new LoggingChunkStatusListener(p_i50695_1_);
      this.fullDiameter = p_i50695_1_ * 2 + 1;
      this.radius = p_i50695_1_ + ChunkStatus.maxDistance();
      this.diameter = this.radius * 2 + 1;
      this.statuses = new Long2ObjectOpenHashMap<>();
   }

   public void updateSpawnPos(ChunkPos p_219509_1_) {
      if (this.started) {
         this.delegate.updateSpawnPos(p_219509_1_);
         this.spawnPos = p_219509_1_;
      }
   }

   public void onStatusChange(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_) {
      if (this.started) {
         this.delegate.onStatusChange(p_219508_1_, p_219508_2_);
         if (p_219508_2_ == null) {
            this.statuses.remove(p_219508_1_.toLong());
         } else {
            this.statuses.put(p_219508_1_.toLong(), p_219508_2_);
         }

      }
   }

   public void start() {
      this.started = true;
      this.statuses.clear();
   }

   public void stop() {
      this.started = false;
      this.delegate.stop();
   }

   public int getFullDiameter() {
      return this.fullDiameter;
   }

   public int getDiameter() {
      return this.diameter;
   }

   public int getProgress() {
      return this.delegate.getProgress();
   }

   @Nullable
   public ChunkStatus getStatus(int p_219525_1_, int p_219525_2_) {
      return this.statuses.get(ChunkPos.asLong(p_219525_1_ + this.spawnPos.x - this.radius, p_219525_2_ + this.spawnPos.z - this.radius));
   }
}
