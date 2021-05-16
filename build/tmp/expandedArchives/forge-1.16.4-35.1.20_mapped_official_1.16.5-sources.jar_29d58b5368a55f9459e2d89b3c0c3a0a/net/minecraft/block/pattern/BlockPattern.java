package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorldReader;

public class BlockPattern {
   private final Predicate<CachedBlockInfo>[][][] pattern;
   private final int depth;
   private final int height;
   private final int width;

   public BlockPattern(Predicate<CachedBlockInfo>[][][] p_i48279_1_) {
      this.pattern = p_i48279_1_;
      this.depth = p_i48279_1_.length;
      if (this.depth > 0) {
         this.height = p_i48279_1_[0].length;
         if (this.height > 0) {
            this.width = p_i48279_1_[0][0].length;
         } else {
            this.width = 0;
         }
      } else {
         this.height = 0;
         this.width = 0;
      }

   }

   public int getDepth() {
      return this.depth;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   @Nullable
   private BlockPattern.PatternHelper matches(BlockPos p_177682_1_, Direction p_177682_2_, Direction p_177682_3_, LoadingCache<BlockPos, CachedBlockInfo> p_177682_4_) {
      for(int i = 0; i < this.width; ++i) {
         for(int j = 0; j < this.height; ++j) {
            for(int k = 0; k < this.depth; ++k) {
               if (!this.pattern[k][j][i].test(p_177682_4_.getUnchecked(translateAndRotate(p_177682_1_, p_177682_2_, p_177682_3_, i, j, k)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(p_177682_1_, p_177682_2_, p_177682_3_, p_177682_4_, this.width, this.height, this.depth);
   }

   @Nullable
   public BlockPattern.PatternHelper find(IWorldReader p_177681_1_, BlockPos p_177681_2_) {
      LoadingCache<BlockPos, CachedBlockInfo> loadingcache = createLevelCache(p_177681_1_, false);
      int i = Math.max(Math.max(this.width, this.height), this.depth);

      for(BlockPos blockpos : BlockPos.betweenClosed(p_177681_2_, p_177681_2_.offset(i - 1, i - 1, i - 1))) {
         for(Direction direction : Direction.values()) {
            for(Direction direction1 : Direction.values()) {
               if (direction1 != direction && direction1 != direction.getOpposite()) {
                  BlockPattern.PatternHelper blockpattern$patternhelper = this.matches(blockpos, direction, direction1, loadingcache);
                  if (blockpattern$patternhelper != null) {
                     return blockpattern$patternhelper;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, CachedBlockInfo> createLevelCache(IWorldReader p_181627_0_, boolean p_181627_1_) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(p_181627_0_, p_181627_1_));
   }

   protected static BlockPos translateAndRotate(BlockPos p_177683_0_, Direction p_177683_1_, Direction p_177683_2_, int p_177683_3_, int p_177683_4_, int p_177683_5_) {
      if (p_177683_1_ != p_177683_2_ && p_177683_1_ != p_177683_2_.getOpposite()) {
         Vector3i vector3i = new Vector3i(p_177683_1_.getStepX(), p_177683_1_.getStepY(), p_177683_1_.getStepZ());
         Vector3i vector3i1 = new Vector3i(p_177683_2_.getStepX(), p_177683_2_.getStepY(), p_177683_2_.getStepZ());
         Vector3i vector3i2 = vector3i.cross(vector3i1);
         return p_177683_0_.offset(vector3i1.getX() * -p_177683_4_ + vector3i2.getX() * p_177683_3_ + vector3i.getX() * p_177683_5_, vector3i1.getY() * -p_177683_4_ + vector3i2.getY() * p_177683_3_ + vector3i.getY() * p_177683_5_, vector3i1.getZ() * -p_177683_4_ + vector3i2.getZ() * p_177683_3_ + vector3i.getZ() * p_177683_5_);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, CachedBlockInfo> {
      private final IWorldReader level;
      private final boolean loadChunks;

      public CacheLoader(IWorldReader p_i48983_1_, boolean p_i48983_2_) {
         this.level = p_i48983_1_;
         this.loadChunks = p_i48983_2_;
      }

      public CachedBlockInfo load(BlockPos p_load_1_) throws Exception {
         return new CachedBlockInfo(this.level, p_load_1_, this.loadChunks);
      }
   }

   public static class PatternHelper {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache<BlockPos, CachedBlockInfo> cache;
      private final int width;
      private final int height;
      private final int depth;

      public PatternHelper(BlockPos p_i46378_1_, Direction p_i46378_2_, Direction p_i46378_3_, LoadingCache<BlockPos, CachedBlockInfo> p_i46378_4_, int p_i46378_5_, int p_i46378_6_, int p_i46378_7_) {
         this.frontTopLeft = p_i46378_1_;
         this.forwards = p_i46378_2_;
         this.up = p_i46378_3_;
         this.cache = p_i46378_4_;
         this.width = p_i46378_5_;
         this.height = p_i46378_6_;
         this.depth = p_i46378_7_;
      }

      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public Direction getForwards() {
         return this.forwards;
      }

      public Direction getUp() {
         return this.up;
      }

      public CachedBlockInfo getBlock(int p_177670_1_, int p_177670_2_, int p_177670_3_) {
         return this.cache.getUnchecked(BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), p_177670_1_, p_177670_2_, p_177670_3_));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }
   }
}
