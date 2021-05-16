package net.minecraft.world.lighting;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class LightEngine<M extends LightDataMap<M>, S extends SectionLightStorage<M>> extends LevelBasedGraph implements IWorldLightListener {
   private static final Direction[] DIRECTIONS = Direction.values();
   protected final IChunkLightProvider chunkSource;
   protected final LightType layer;
   protected final S storage;
   private boolean runningLightUpdates;
   protected final BlockPos.Mutable pos = new BlockPos.Mutable();
   private final long[] lastChunkPos = new long[2];
   private final IBlockReader[] lastChunk = new IBlockReader[2];

   public LightEngine(IChunkLightProvider p_i51296_1_, LightType p_i51296_2_, S p_i51296_3_) {
      super(16, 256, 8192);
      this.chunkSource = p_i51296_1_;
      this.layer = p_i51296_2_;
      this.storage = p_i51296_3_;
      this.clearCache();
   }

   protected void checkNode(long p_215473_1_) {
      this.storage.runAllUpdates();
      if (this.storage.storingLightForSection(SectionPos.blockToSection(p_215473_1_))) {
         super.checkNode(p_215473_1_);
      }

   }

   @Nullable
   private IBlockReader getChunk(int p_215615_1_, int p_215615_2_) {
      long i = ChunkPos.asLong(p_215615_1_, p_215615_2_);

      for(int j = 0; j < 2; ++j) {
         if (i == this.lastChunkPos[j]) {
            return this.lastChunk[j];
         }
      }

      IBlockReader iblockreader = this.chunkSource.getChunkForLighting(p_215615_1_, p_215615_2_);

      for(int k = 1; k > 0; --k) {
         this.lastChunkPos[k] = this.lastChunkPos[k - 1];
         this.lastChunk[k] = this.lastChunk[k - 1];
      }

      this.lastChunkPos[0] = i;
      this.lastChunk[0] = iblockreader;
      return iblockreader;
   }

   private void clearCache() {
      Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
      Arrays.fill(this.lastChunk, (Object)null);
   }

   protected BlockState getStateAndOpacity(long p_227468_1_, @Nullable MutableInt p_227468_3_) {
      if (p_227468_1_ == Long.MAX_VALUE) {
         if (p_227468_3_ != null) {
            p_227468_3_.setValue(0);
         }

         return Blocks.AIR.defaultBlockState();
      } else {
         int i = SectionPos.blockToSectionCoord(BlockPos.getX(p_227468_1_));
         int j = SectionPos.blockToSectionCoord(BlockPos.getZ(p_227468_1_));
         IBlockReader iblockreader = this.getChunk(i, j);
         if (iblockreader == null) {
            if (p_227468_3_ != null) {
               p_227468_3_.setValue(16);
            }

            return Blocks.BEDROCK.defaultBlockState();
         } else {
            this.pos.set(p_227468_1_);
            BlockState blockstate = iblockreader.getBlockState(this.pos);
            boolean flag = blockstate.canOcclude() && blockstate.useShapeForLightOcclusion();
            if (p_227468_3_ != null) {
               p_227468_3_.setValue(blockstate.getLightBlock(this.chunkSource.getLevel(), this.pos));
            }

            return flag ? blockstate : Blocks.AIR.defaultBlockState();
         }
      }
   }

   protected VoxelShape getShape(BlockState p_223405_1_, long p_223405_2_, Direction p_223405_4_) {
      return p_223405_1_.canOcclude() ? p_223405_1_.getFaceOcclusionShape(this.chunkSource.getLevel(), this.pos.set(p_223405_2_), p_223405_4_) : VoxelShapes.empty();
   }

   public static int getLightBlockInto(IBlockReader p_215613_0_, BlockState p_215613_1_, BlockPos p_215613_2_, BlockState p_215613_3_, BlockPos p_215613_4_, Direction p_215613_5_, int p_215613_6_) {
      boolean flag = p_215613_1_.canOcclude() && p_215613_1_.useShapeForLightOcclusion();
      boolean flag1 = p_215613_3_.canOcclude() && p_215613_3_.useShapeForLightOcclusion();
      if (!flag && !flag1) {
         return p_215613_6_;
      } else {
         VoxelShape voxelshape = flag ? p_215613_1_.getOcclusionShape(p_215613_0_, p_215613_2_) : VoxelShapes.empty();
         VoxelShape voxelshape1 = flag1 ? p_215613_3_.getOcclusionShape(p_215613_0_, p_215613_4_) : VoxelShapes.empty();
         return VoxelShapes.mergedFaceOccludes(voxelshape, voxelshape1, p_215613_5_) ? 16 : p_215613_6_;
      }
   }

   protected boolean isSource(long p_215485_1_) {
      return p_215485_1_ == Long.MAX_VALUE;
   }

   protected int getComputedLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      return 0;
   }

   protected int getLevel(long p_215471_1_) {
      return p_215471_1_ == Long.MAX_VALUE ? 0 : 15 - this.storage.getStoredLevel(p_215471_1_);
   }

   protected int getLevel(NibbleArray p_215622_1_, long p_215622_2_) {
      return 15 - p_215622_1_.get(SectionPos.sectionRelative(BlockPos.getX(p_215622_2_)), SectionPos.sectionRelative(BlockPos.getY(p_215622_2_)), SectionPos.sectionRelative(BlockPos.getZ(p_215622_2_)));
   }

   protected void setLevel(long p_215476_1_, int p_215476_3_) {
      this.storage.setStoredLevel(p_215476_1_, Math.min(15, 15 - p_215476_3_));
   }

   protected int computeLevelFromNeighbor(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      return 0;
   }

   public boolean hasLightWork() {
      return this.hasWork() || this.storage.hasWork() || this.storage.hasInconsistencies();
   }

   public int runUpdates(int p_215616_1_, boolean p_215616_2_, boolean p_215616_3_) {
      if (!this.runningLightUpdates) {
         if (this.storage.hasWork()) {
            p_215616_1_ = this.storage.runUpdates(p_215616_1_);
            if (p_215616_1_ == 0) {
               return p_215616_1_;
            }
         }

         this.storage.markNewInconsistencies(this, p_215616_2_, p_215616_3_);
      }

      this.runningLightUpdates = true;
      if (this.hasWork()) {
         p_215616_1_ = this.runUpdates(p_215616_1_);
         this.clearCache();
         if (p_215616_1_ == 0) {
            return p_215616_1_;
         }
      }

      this.runningLightUpdates = false;
      this.storage.swapSectionMap();
      return p_215616_1_;
   }

   protected void queueSectionData(long p_215621_1_, @Nullable NibbleArray p_215621_3_, boolean p_215621_4_) {
      this.storage.queueSectionData(p_215621_1_, p_215621_3_, p_215621_4_);
   }

   @Nullable
   public NibbleArray getDataLayerData(SectionPos p_215612_1_) {
      return this.storage.getDataLayerData(p_215612_1_.asLong());
   }

   public int getLightValue(BlockPos p_215611_1_) {
      return this.storage.getLightValue(p_215611_1_.asLong());
   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugData(long p_215614_1_) {
      return "" + this.storage.getLevel(p_215614_1_);
   }

   public void checkBlock(BlockPos p_215617_1_) {
      long i = p_215617_1_.asLong();
      this.checkNode(i);

      for(Direction direction : DIRECTIONS) {
         this.checkNode(BlockPos.offset(i, direction));
      }

   }

   public void onBlockEmissionIncrease(BlockPos p_215623_1_, int p_215623_2_) {
   }

   public void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_) {
      this.storage.updateSectionStatus(p_215566_1_.asLong(), p_215566_2_);
   }

   public void enableLightSources(ChunkPos p_215620_1_, boolean p_215620_2_) {
      long i = SectionPos.getZeroNode(SectionPos.asLong(p_215620_1_.x, 0, p_215620_1_.z));
      this.storage.enableLightSources(i, p_215620_2_);
   }

   public void retainData(ChunkPos p_223129_1_, boolean p_223129_2_) {
      long i = SectionPos.getZeroNode(SectionPos.asLong(p_223129_1_.x, 0, p_223129_1_.z));
      this.storage.retainData(i, p_223129_2_);
   }

   public abstract int queuedUpdateSize();
}
