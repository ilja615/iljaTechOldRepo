package net.minecraft.world.chunk;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.lighting.LevelBasedGraph;

public abstract class ChunkDistanceGraph extends LevelBasedGraph {
   protected ChunkDistanceGraph(int p_i50712_1_, int p_i50712_2_, int p_i50712_3_) {
      super(p_i50712_1_, p_i50712_2_, p_i50712_3_);
   }

   protected boolean isSource(long p_215485_1_) {
      return p_215485_1_ == ChunkPos.INVALID_CHUNK_POS;
   }

   protected void checkNeighborsAfterUpdate(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      ChunkPos chunkpos = new ChunkPos(p_215478_1_);
      int i = chunkpos.x;
      int j = chunkpos.z;

      for(int k = -1; k <= 1; ++k) {
         for(int l = -1; l <= 1; ++l) {
            long i1 = ChunkPos.asLong(i + k, j + l);
            if (i1 != p_215478_1_) {
               this.checkNeighbor(p_215478_1_, i1, p_215478_3_, p_215478_4_);
            }
         }
      }

   }

   protected int getComputedLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int i = p_215477_5_;
      ChunkPos chunkpos = new ChunkPos(p_215477_1_);
      int j = chunkpos.x;
      int k = chunkpos.z;

      for(int l = -1; l <= 1; ++l) {
         for(int i1 = -1; i1 <= 1; ++i1) {
            long j1 = ChunkPos.asLong(j + l, k + i1);
            if (j1 == p_215477_1_) {
               j1 = ChunkPos.INVALID_CHUNK_POS;
            }

            if (j1 != p_215477_3_) {
               int k1 = this.computeLevelFromNeighbor(j1, p_215477_1_, this.getLevel(j1));
               if (i > k1) {
                  i = k1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   protected int computeLevelFromNeighbor(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      return p_215480_1_ == ChunkPos.INVALID_CHUNK_POS ? this.getLevelFromSource(p_215480_3_) : p_215480_5_ + 1;
   }

   protected abstract int getLevelFromSource(long p_215492_1_);

   public void update(long p_215491_1_, int p_215491_3_, boolean p_215491_4_) {
      this.checkEdge(ChunkPos.INVALID_CHUNK_POS, p_215491_1_, p_215491_3_, p_215491_4_);
   }
}
