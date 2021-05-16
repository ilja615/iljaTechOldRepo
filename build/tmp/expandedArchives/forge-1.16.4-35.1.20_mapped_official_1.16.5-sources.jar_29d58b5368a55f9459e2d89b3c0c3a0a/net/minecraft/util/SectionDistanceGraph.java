package net.minecraft.util;

import net.minecraft.util.math.SectionPos;
import net.minecraft.world.lighting.LevelBasedGraph;

public abstract class SectionDistanceGraph extends LevelBasedGraph {
   protected SectionDistanceGraph(int p_i50706_1_, int p_i50706_2_, int p_i50706_3_) {
      super(p_i50706_1_, p_i50706_2_, p_i50706_3_);
   }

   protected boolean isSource(long p_215485_1_) {
      return p_215485_1_ == Long.MAX_VALUE;
   }

   protected void checkNeighborsAfterUpdate(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               long l = SectionPos.offset(p_215478_1_, i, j, k);
               if (l != p_215478_1_) {
                  this.checkNeighbor(p_215478_1_, l, p_215478_3_, p_215478_4_);
               }
            }
         }
      }

   }

   protected int getComputedLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int i = p_215477_5_;

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            for(int l = -1; l <= 1; ++l) {
               long i1 = SectionPos.offset(p_215477_1_, j, k, l);
               if (i1 == p_215477_1_) {
                  i1 = Long.MAX_VALUE;
               }

               if (i1 != p_215477_3_) {
                  int j1 = this.computeLevelFromNeighbor(i1, p_215477_1_, this.getLevel(i1));
                  if (i > j1) {
                     i = j1;
                  }

                  if (i == 0) {
                     return i;
                  }
               }
            }
         }
      }

      return i;
   }

   protected int computeLevelFromNeighbor(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      return p_215480_1_ == Long.MAX_VALUE ? this.getLevelFromSource(p_215480_3_) : p_215480_5_ + 1;
   }

   protected abstract int getLevelFromSource(long p_215516_1_);

   public void update(long p_215515_1_, int p_215515_3_, boolean p_215515_4_) {
      this.checkEdge(Long.MAX_VALUE, p_215515_1_, p_215515_3_, p_215515_4_);
   }
}
