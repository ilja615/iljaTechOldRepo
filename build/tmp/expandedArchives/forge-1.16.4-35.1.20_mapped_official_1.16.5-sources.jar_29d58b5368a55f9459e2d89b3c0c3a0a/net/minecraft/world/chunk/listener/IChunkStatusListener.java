package net.minecraft.world.chunk.listener;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface IChunkStatusListener {
   void updateSpawnPos(ChunkPos p_219509_1_);

   void onStatusChange(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_);

   void stop();
}
