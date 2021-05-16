package net.minecraft.world;

import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;

public class Region implements IBlockReader, ICollisionReader {
   protected final int centerX;
   protected final int centerZ;
   protected final IChunk[][] chunks;
   protected boolean allEmpty;
   protected final World level;

   public Region(World p_i50004_1_, BlockPos p_i50004_2_, BlockPos p_i50004_3_) {
      this.level = p_i50004_1_;
      this.centerX = p_i50004_2_.getX() >> 4;
      this.centerZ = p_i50004_2_.getZ() >> 4;
      int i = p_i50004_3_.getX() >> 4;
      int j = p_i50004_3_.getZ() >> 4;
      this.chunks = new IChunk[i - this.centerX + 1][j - this.centerZ + 1];
      AbstractChunkProvider abstractchunkprovider = p_i50004_1_.getChunkSource();
      this.allEmpty = true;

      for(int k = this.centerX; k <= i; ++k) {
         for(int l = this.centerZ; l <= j; ++l) {
            this.chunks[k - this.centerX][l - this.centerZ] = abstractchunkprovider.getChunkNow(k, l);
         }
      }

      for(int i1 = p_i50004_2_.getX() >> 4; i1 <= p_i50004_3_.getX() >> 4; ++i1) {
         for(int j1 = p_i50004_2_.getZ() >> 4; j1 <= p_i50004_3_.getZ() >> 4; ++j1) {
            IChunk ichunk = this.chunks[i1 - this.centerX][j1 - this.centerZ];
            if (ichunk != null && !ichunk.isYSpaceEmpty(p_i50004_2_.getY(), p_i50004_3_.getY())) {
               this.allEmpty = false;
               return;
            }
         }
      }

   }

   private IChunk getChunk(BlockPos p_226703_1_) {
      return this.getChunk(p_226703_1_.getX() >> 4, p_226703_1_.getZ() >> 4);
   }

   private IChunk getChunk(int p_226702_1_, int p_226702_2_) {
      int i = p_226702_1_ - this.centerX;
      int j = p_226702_2_ - this.centerZ;
      if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
         IChunk ichunk = this.chunks[i][j];
         return (IChunk)(ichunk != null ? ichunk : new EmptyChunk(this.level, new ChunkPos(p_226702_1_, p_226702_2_)));
      } else {
         return new EmptyChunk(this.level, new ChunkPos(p_226702_1_, p_226702_2_));
      }
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public IBlockReader getChunkForCollisions(int p_225522_1_, int p_225522_2_) {
      return this.getChunk(p_225522_1_, p_225522_2_);
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      IChunk ichunk = this.getChunk(p_175625_1_);
      return ichunk.getBlockEntity(p_175625_1_);
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      if (World.isOutsideBuildHeight(p_180495_1_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         IChunk ichunk = this.getChunk(p_180495_1_);
         return ichunk.getBlockState(p_180495_1_);
      }
   }

   public Stream<VoxelShape> getEntityCollisions(@Nullable Entity p_230318_1_, AxisAlignedBB p_230318_2_, Predicate<Entity> p_230318_3_) {
      return Stream.empty();
   }

   public Stream<VoxelShape> getCollisions(@Nullable Entity p_234867_1_, AxisAlignedBB p_234867_2_, Predicate<Entity> p_234867_3_) {
      return this.getBlockCollisions(p_234867_1_, p_234867_2_);
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      if (World.isOutsideBuildHeight(p_204610_1_)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         IChunk ichunk = this.getChunk(p_204610_1_);
         return ichunk.getFluidState(p_204610_1_);
      }
   }
}
