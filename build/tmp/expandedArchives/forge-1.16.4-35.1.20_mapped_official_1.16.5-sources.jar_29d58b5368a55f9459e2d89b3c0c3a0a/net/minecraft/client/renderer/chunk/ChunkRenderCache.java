package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderCache implements IBlockDisplayReader {
   protected final int centerX;
   protected final int centerZ;
   protected final BlockPos start;
   protected final int xLength;
   protected final int yLength;
   protected final int zLength;
   protected final Chunk[][] chunks;
   protected final BlockState[] blockStates;
   protected final FluidState[] fluidStates;
   protected final World level;

   @Nullable
   public static ChunkRenderCache createIfNotEmpty(World p_212397_0_, BlockPos p_212397_1_, BlockPos p_212397_2_, int p_212397_3_) {
      int i = p_212397_1_.getX() - p_212397_3_ >> 4;
      int j = p_212397_1_.getZ() - p_212397_3_ >> 4;
      int k = p_212397_2_.getX() + p_212397_3_ >> 4;
      int l = p_212397_2_.getZ() + p_212397_3_ >> 4;
      Chunk[][] achunk = new Chunk[k - i + 1][l - j + 1];

      for(int i1 = i; i1 <= k; ++i1) {
         for(int j1 = j; j1 <= l; ++j1) {
            achunk[i1 - i][j1 - j] = p_212397_0_.getChunk(i1, j1);
         }
      }

      if (isAllEmpty(p_212397_1_, p_212397_2_, i, j, achunk)) {
         return null;
      } else {
         int k1 = 1;
         BlockPos blockpos1 = p_212397_1_.offset(-1, -1, -1);
         BlockPos blockpos = p_212397_2_.offset(1, 1, 1);
         return new ChunkRenderCache(p_212397_0_, i, j, achunk, blockpos1, blockpos);
      }
   }

   public static boolean isAllEmpty(BlockPos p_241718_0_, BlockPos p_241718_1_, int p_241718_2_, int p_241718_3_, Chunk[][] p_241718_4_) {
      for(int i = p_241718_0_.getX() >> 4; i <= p_241718_1_.getX() >> 4; ++i) {
         for(int j = p_241718_0_.getZ() >> 4; j <= p_241718_1_.getZ() >> 4; ++j) {
            Chunk chunk = p_241718_4_[i - p_241718_2_][j - p_241718_3_];
            if (!chunk.isYSpaceEmpty(p_241718_0_.getY(), p_241718_1_.getY())) {
               return false;
            }
         }
      }

      return true;
   }

   public ChunkRenderCache(World p_i49840_1_, int p_i49840_2_, int p_i49840_3_, Chunk[][] p_i49840_4_, BlockPos p_i49840_5_, BlockPos p_i49840_6_) {
      this.level = p_i49840_1_;
      this.centerX = p_i49840_2_;
      this.centerZ = p_i49840_3_;
      this.chunks = p_i49840_4_;
      this.start = p_i49840_5_;
      this.xLength = p_i49840_6_.getX() - p_i49840_5_.getX() + 1;
      this.yLength = p_i49840_6_.getY() - p_i49840_5_.getY() + 1;
      this.zLength = p_i49840_6_.getZ() - p_i49840_5_.getZ() + 1;
      this.blockStates = new BlockState[this.xLength * this.yLength * this.zLength];
      this.fluidStates = new FluidState[this.xLength * this.yLength * this.zLength];

      for(BlockPos blockpos : BlockPos.betweenClosed(p_i49840_5_, p_i49840_6_)) {
         int i = (blockpos.getX() >> 4) - p_i49840_2_;
         int j = (blockpos.getZ() >> 4) - p_i49840_3_;
         Chunk chunk = p_i49840_4_[i][j];
         int k = this.index(blockpos);
         this.blockStates[k] = chunk.getBlockState(blockpos);
         this.fluidStates[k] = chunk.getFluidState(blockpos);
      }

   }

   protected final int index(BlockPos p_212398_1_) {
      return this.index(p_212398_1_.getX(), p_212398_1_.getY(), p_212398_1_.getZ());
   }

   protected int index(int p_217339_1_, int p_217339_2_, int p_217339_3_) {
      int i = p_217339_1_ - this.start.getX();
      int j = p_217339_2_ - this.start.getY();
      int k = p_217339_3_ - this.start.getZ();
      return k * this.xLength * this.yLength + j * this.xLength + i;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      return this.blockStates[this.index(p_180495_1_)];
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      return this.fluidStates[this.index(p_204610_1_)];
   }

   public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
      return this.level.getShade(p_230487_1_, p_230487_2_);
   }

   public WorldLightManager getLightEngine() {
      return this.level.getLightEngine();
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      return this.getBlockEntity(p_175625_1_, Chunk.CreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_212399_1_, Chunk.CreateEntityType p_212399_2_) {
      int i = (p_212399_1_.getX() >> 4) - this.centerX;
      int j = (p_212399_1_.getZ() >> 4) - this.centerZ;
      return this.chunks[i][j].getBlockEntity(p_212399_1_, p_212399_2_);
   }

   public int getBlockTint(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
      return this.level.getBlockTint(p_225525_1_, p_225525_2_);
   }
}
