package net.minecraft.world.chunk;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.lighting.WorldLightManager;

public abstract class AbstractChunkProvider implements IChunkLightProvider, AutoCloseable {
   @Nullable
   public Chunk getChunk(int p_217205_1_, int p_217205_2_, boolean p_217205_3_) {
      return (Chunk)this.getChunk(p_217205_1_, p_217205_2_, ChunkStatus.FULL, p_217205_3_);
   }

   @Nullable
   public Chunk getChunkNow(int p_225313_1_, int p_225313_2_) {
      return this.getChunk(p_225313_1_, p_225313_2_, false);
   }

   @Nullable
   public IBlockReader getChunkForLighting(int p_217202_1_, int p_217202_2_) {
      return this.getChunk(p_217202_1_, p_217202_2_, ChunkStatus.EMPTY, false);
   }

   public boolean hasChunk(int p_73149_1_, int p_73149_2_) {
      return this.getChunk(p_73149_1_, p_73149_2_, ChunkStatus.FULL, false) != null;
   }

   @Nullable
   public abstract IChunk getChunk(int p_212849_1_, int p_212849_2_, ChunkStatus p_212849_3_, boolean p_212849_4_);

   public abstract String gatherStats();

   public void close() throws IOException {
   }

   public abstract WorldLightManager getLightEngine();

   public void setSpawnSettings(boolean p_217203_1_, boolean p_217203_2_) {
   }

   public void updateChunkForced(ChunkPos p_217206_1_, boolean p_217206_2_) {
   }

   public boolean isEntityTickingChunk(Entity p_217204_1_) {
      return true;
   }

   public boolean isEntityTickingChunk(ChunkPos p_222865_1_) {
      return true;
   }

   public boolean isTickingChunk(BlockPos p_222866_1_) {
      return true;
   }
}
