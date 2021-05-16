package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class ChunkPos {
   public static final long INVALID_CHUNK_POS = asLong(1875016, 1875016);
   public final int x;
   public final int z;

   public ChunkPos(int p_i1947_1_, int p_i1947_2_) {
      this.x = p_i1947_1_;
      this.z = p_i1947_2_;
   }

   public ChunkPos(BlockPos p_i46717_1_) {
      this.x = p_i46717_1_.getX() >> 4;
      this.z = p_i46717_1_.getZ() >> 4;
   }

   public ChunkPos(long p_i48713_1_) {
      this.x = (int)p_i48713_1_;
      this.z = (int)(p_i48713_1_ >> 32);
   }

   public long toLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int p_77272_0_, int p_77272_1_) {
      return (long)p_77272_0_ & 4294967295L | ((long)p_77272_1_ & 4294967295L) << 32;
   }

   public static int getX(long p_212578_0_) {
      return (int)(p_212578_0_ & 4294967295L);
   }

   public static int getZ(long p_212579_0_) {
      return (int)(p_212579_0_ >>> 32 & 4294967295L);
   }

   public int hashCode() {
      int i = 1664525 * this.x + 1013904223;
      int j = 1664525 * (this.z ^ -559038737) + 1013904223;
      return i ^ j;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos chunkpos = (ChunkPos)p_equals_1_;
         return this.x == chunkpos.x && this.z == chunkpos.z;
      }
   }

   public int getMinBlockX() {
      return this.x << 4;
   }

   public int getMinBlockZ() {
      return this.z << 4;
   }

   public int getMaxBlockX() {
      return (this.x << 4) + 15;
   }

   public int getMaxBlockZ() {
      return (this.z << 4) + 15;
   }

   public int getRegionX() {
      return this.x >> 5;
   }

   public int getRegionZ() {
      return this.z >> 5;
   }

   public int getRegionLocalX() {
      return this.x & 31;
   }

   public int getRegionLocalZ() {
      return this.z & 31;
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockPos getWorldPosition() {
      return new BlockPos(this.getMinBlockX(), 0, this.getMinBlockZ());
   }

   public int getChessboardDistance(ChunkPos p_226661_1_) {
      return Math.max(Math.abs(this.x - p_226661_1_.x), Math.abs(this.z - p_226661_1_.z));
   }

   public static Stream<ChunkPos> rangeClosed(ChunkPos p_222243_0_, int p_222243_1_) {
      return rangeClosed(new ChunkPos(p_222243_0_.x - p_222243_1_, p_222243_0_.z - p_222243_1_), new ChunkPos(p_222243_0_.x + p_222243_1_, p_222243_0_.z + p_222243_1_));
   }

   public static Stream<ChunkPos> rangeClosed(final ChunkPos p_222239_0_, final ChunkPos p_222239_1_) {
      int i = Math.abs(p_222239_0_.x - p_222239_1_.x) + 1;
      int j = Math.abs(p_222239_0_.z - p_222239_1_.z) + 1;
      final int k = p_222239_0_.x < p_222239_1_.x ? 1 : -1;
      final int l = p_222239_0_.z < p_222239_1_.z ? 1 : -1;
      return StreamSupport.stream(new AbstractSpliterator<ChunkPos>((long)(i * j), 64) {
         @Nullable
         private ChunkPos pos;

         public boolean tryAdvance(Consumer<? super ChunkPos> p_tryAdvance_1_) {
            if (this.pos == null) {
               this.pos = p_222239_0_;
            } else {
               int i1 = this.pos.x;
               int j1 = this.pos.z;
               if (i1 == p_222239_1_.x) {
                  if (j1 == p_222239_1_.z) {
                     return false;
                  }

                  this.pos = new ChunkPos(p_222239_0_.x, j1 + l);
               } else {
                  this.pos = new ChunkPos(i1 + k, j1);
               }
            }

            p_tryAdvance_1_.accept(this.pos);
            return true;
         }
      }, false);
   }
}
