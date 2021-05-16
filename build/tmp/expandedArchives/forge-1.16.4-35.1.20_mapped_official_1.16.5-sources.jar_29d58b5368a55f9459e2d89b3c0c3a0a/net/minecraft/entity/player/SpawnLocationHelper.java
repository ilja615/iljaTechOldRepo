package net.minecraft.entity.player;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class SpawnLocationHelper {
   @Nullable
   protected static BlockPos getOverworldRespawnPos(ServerWorld p_241092_0_, int p_241092_1_, int p_241092_2_, boolean p_241092_3_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_241092_1_, 0, p_241092_2_);
      Biome biome = p_241092_0_.getBiome(blockpos$mutable);
      boolean flag = p_241092_0_.dimensionType().hasCeiling();
      BlockState blockstate = biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
      if (p_241092_3_ && !blockstate.getBlock().is(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         Chunk chunk = p_241092_0_.getChunk(p_241092_1_ >> 4, p_241092_2_ >> 4);
         int i = flag ? p_241092_0_.getChunkSource().getGenerator().getSpawnHeight() : chunk.getHeight(Heightmap.Type.MOTION_BLOCKING, p_241092_1_ & 15, p_241092_2_ & 15);
         if (i < 0) {
            return null;
         } else {
            int j = chunk.getHeight(Heightmap.Type.WORLD_SURFACE, p_241092_1_ & 15, p_241092_2_ & 15);
            if (j <= i && j > chunk.getHeight(Heightmap.Type.OCEAN_FLOOR, p_241092_1_ & 15, p_241092_2_ & 15)) {
               return null;
            } else {
               for(int k = i + 1; k >= 0; --k) {
                  blockpos$mutable.set(p_241092_1_, k, p_241092_2_);
                  BlockState blockstate1 = p_241092_0_.getBlockState(blockpos$mutable);
                  if (!blockstate1.getFluidState().isEmpty()) {
                     break;
                  }

                  if (blockstate1.equals(blockstate)) {
                     return blockpos$mutable.above().immutable();
                  }
               }

               return null;
            }
         }
      }
   }

   @Nullable
   public static BlockPos getSpawnPosInChunk(ServerWorld p_241094_0_, ChunkPos p_241094_1_, boolean p_241094_2_) {
      for(int i = p_241094_1_.getMinBlockX(); i <= p_241094_1_.getMaxBlockX(); ++i) {
         for(int j = p_241094_1_.getMinBlockZ(); j <= p_241094_1_.getMaxBlockZ(); ++j) {
            BlockPos blockpos = getOverworldRespawnPos(p_241094_0_, i, j, p_241094_2_);
            if (blockpos != null) {
               return blockpos;
            }
         }
      }

      return null;
   }
}
