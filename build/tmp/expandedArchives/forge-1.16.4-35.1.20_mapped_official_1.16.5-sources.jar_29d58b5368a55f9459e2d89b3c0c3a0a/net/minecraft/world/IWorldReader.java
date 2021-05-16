package net.minecraft.world;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorldReader extends IBlockDisplayReader, ICollisionReader, BiomeManager.IBiomeReader {
   @Nullable
   IChunk getChunk(int p_217353_1_, int p_217353_2_, ChunkStatus p_217353_3_, boolean p_217353_4_);

   @Deprecated
   boolean hasChunk(int p_217354_1_, int p_217354_2_);

   int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_);

   int getSkyDarken();

   BiomeManager getBiomeManager();

   default Biome getBiome(BlockPos p_226691_1_) {
      return this.getBiomeManager().getBiome(p_226691_1_);
   }

   default Stream<BlockState> getBlockStatesIfLoaded(AxisAlignedBB p_234939_1_) {
      int i = MathHelper.floor(p_234939_1_.minX);
      int j = MathHelper.floor(p_234939_1_.maxX);
      int k = MathHelper.floor(p_234939_1_.minY);
      int l = MathHelper.floor(p_234939_1_.maxY);
      int i1 = MathHelper.floor(p_234939_1_.minZ);
      int j1 = MathHelper.floor(p_234939_1_.maxZ);
      return this.hasChunksAt(i, k, i1, j, l, j1) ? this.getBlockStates(p_234939_1_) : Stream.empty();
   }

   @OnlyIn(Dist.CLIENT)
   default int getBlockTint(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
      return p_225525_2_.getColor(this.getBiome(p_225525_1_), (double)p_225525_1_.getX(), (double)p_225525_1_.getZ());
   }

   default Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      IChunk ichunk = this.getChunk(p_225526_1_ >> 2, p_225526_3_ >> 2, ChunkStatus.BIOMES, false);
      return ichunk != null && ichunk.getBiomes() != null ? ichunk.getBiomes().getNoiseBiome(p_225526_1_, p_225526_2_, p_225526_3_) : this.getUncachedNoiseBiome(p_225526_1_, p_225526_2_, p_225526_3_);
   }

   Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_);

   boolean isClientSide();

   @Deprecated
   int getSeaLevel();

   DimensionType dimensionType();

   default BlockPos getHeightmapPos(Heightmap.Type p_205770_1_, BlockPos p_205770_2_) {
      return new BlockPos(p_205770_2_.getX(), this.getHeight(p_205770_1_, p_205770_2_.getX(), p_205770_2_.getZ()), p_205770_2_.getZ());
   }

   default boolean isEmptyBlock(BlockPos p_175623_1_) {
      return this.getBlockState(p_175623_1_).isAir(this, p_175623_1_);
   }

   default boolean canSeeSkyFromBelowWater(BlockPos p_175710_1_) {
      if (p_175710_1_.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(p_175710_1_);
      } else {
         BlockPos blockpos = new BlockPos(p_175710_1_.getX(), this.getSeaLevel(), p_175710_1_.getZ());
         if (!this.canSeeSky(blockpos)) {
            return false;
         } else {
            for(BlockPos blockpos1 = blockpos.below(); blockpos1.getY() > p_175710_1_.getY(); blockpos1 = blockpos1.below()) {
               BlockState blockstate = this.getBlockState(blockpos1);
               if (blockstate.getLightBlock(this, blockpos1) > 0 && !blockstate.getMaterial().isLiquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Deprecated
   default float getBrightness(BlockPos p_205052_1_) {
      return this.dimensionType().brightness(this.getMaxLocalRawBrightness(p_205052_1_));
   }

   default int getDirectSignal(BlockPos p_175627_1_, Direction p_175627_2_) {
      return this.getBlockState(p_175627_1_).getDirectSignal(this, p_175627_1_, p_175627_2_);
   }

   default IChunk getChunk(BlockPos p_217349_1_) {
      return this.getChunk(p_217349_1_.getX() >> 4, p_217349_1_.getZ() >> 4);
   }

   default IChunk getChunk(int p_212866_1_, int p_212866_2_) {
      return this.getChunk(p_212866_1_, p_212866_2_, ChunkStatus.FULL, true);
   }

   default IChunk getChunk(int p_217348_1_, int p_217348_2_, ChunkStatus p_217348_3_) {
      return this.getChunk(p_217348_1_, p_217348_2_, p_217348_3_, true);
   }

   @Nullable
   default IBlockReader getChunkForCollisions(int p_225522_1_, int p_225522_2_) {
      return this.getChunk(p_225522_1_, p_225522_2_, ChunkStatus.EMPTY, false);
   }

   default boolean isWaterAt(BlockPos p_201671_1_) {
      return this.getFluidState(p_201671_1_).is(FluidTags.WATER);
   }

   default boolean containsAnyLiquid(AxisAlignedBB p_72953_1_) {
      int i = MathHelper.floor(p_72953_1_.minX);
      int j = MathHelper.ceil(p_72953_1_.maxX);
      int k = MathHelper.floor(p_72953_1_.minY);
      int l = MathHelper.ceil(p_72953_1_.maxY);
      int i1 = MathHelper.floor(p_72953_1_.minZ);
      int j1 = MathHelper.ceil(p_72953_1_.maxZ);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int k1 = i; k1 < j; ++k1) {
         for(int l1 = k; l1 < l; ++l1) {
            for(int i2 = i1; i2 < j1; ++i2) {
               BlockState blockstate = this.getBlockState(blockpos$mutable.set(k1, l1, i2));
               if (!blockstate.getFluidState().isEmpty()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   default int getMaxLocalRawBrightness(BlockPos p_201696_1_) {
      return this.getMaxLocalRawBrightness(p_201696_1_, this.getSkyDarken());
   }

   default int getMaxLocalRawBrightness(BlockPos p_205049_1_, int p_205049_2_) {
      return p_205049_1_.getX() >= -30000000 && p_205049_1_.getZ() >= -30000000 && p_205049_1_.getX() < 30000000 && p_205049_1_.getZ() < 30000000 ? this.getRawBrightness(p_205049_1_, p_205049_2_) : 15;
   }

   @Deprecated
   default boolean hasChunkAt(BlockPos p_175667_1_) {
      return this.hasChunk(p_175667_1_.getX() >> 4, p_175667_1_.getZ() >> 4);
   }

   default boolean isAreaLoaded(BlockPos center, int range) {
      return this.hasChunksAt(center.offset(-range, -range, -range), center.offset(range, range, range));
   }

   @Deprecated
   default boolean hasChunksAt(BlockPos p_175707_1_, BlockPos p_175707_2_) {
      return this.hasChunksAt(p_175707_1_.getX(), p_175707_1_.getY(), p_175707_1_.getZ(), p_175707_2_.getX(), p_175707_2_.getY(), p_175707_2_.getZ());
   }

   @Deprecated
   default boolean hasChunksAt(int p_217344_1_, int p_217344_2_, int p_217344_3_, int p_217344_4_, int p_217344_5_, int p_217344_6_) {
      if (p_217344_5_ >= 0 && p_217344_2_ < 256) {
         p_217344_1_ = p_217344_1_ >> 4;
         p_217344_3_ = p_217344_3_ >> 4;
         p_217344_4_ = p_217344_4_ >> 4;
         p_217344_6_ = p_217344_6_ >> 4;

         for(int i = p_217344_1_; i <= p_217344_4_; ++i) {
            for(int j = p_217344_3_; j <= p_217344_6_; ++j) {
               if (!this.hasChunk(i, j)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
