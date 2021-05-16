package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BlockLightEngine extends LightEngine<BlockLightStorage.StorageMap, BlockLightStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockPos.Mutable pos = new BlockPos.Mutable();

   public BlockLightEngine(IChunkLightProvider p_i51301_1_) {
      super(p_i51301_1_, LightType.BLOCK, new BlockLightStorage(p_i51301_1_));
   }

   private int getLightEmission(long p_215635_1_) {
      int i = BlockPos.getX(p_215635_1_);
      int j = BlockPos.getY(p_215635_1_);
      int k = BlockPos.getZ(p_215635_1_);
      IBlockReader iblockreader = this.chunkSource.getChunkForLighting(i >> 4, k >> 4);
      return iblockreader != null ? iblockreader.getLightEmission(this.pos.set(i, j, k)) : 0;
   }

   protected int computeLevelFromNeighbor(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      if (p_215480_3_ == Long.MAX_VALUE) {
         return 15;
      } else if (p_215480_1_ == Long.MAX_VALUE) {
         return p_215480_5_ + 15 - this.getLightEmission(p_215480_3_);
      } else if (p_215480_5_ >= 15) {
         return p_215480_5_;
      } else {
         int i = Integer.signum(BlockPos.getX(p_215480_3_) - BlockPos.getX(p_215480_1_));
         int j = Integer.signum(BlockPos.getY(p_215480_3_) - BlockPos.getY(p_215480_1_));
         int k = Integer.signum(BlockPos.getZ(p_215480_3_) - BlockPos.getZ(p_215480_1_));
         Direction direction = Direction.fromNormal(i, j, k);
         if (direction == null) {
            return 15;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.getStateAndOpacity(p_215480_3_, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               BlockState blockstate1 = this.getStateAndOpacity(p_215480_1_, (MutableInt)null);
               VoxelShape voxelshape = this.getShape(blockstate1, p_215480_1_, direction);
               VoxelShape voxelshape1 = this.getShape(blockstate, p_215480_3_, direction.getOpposite());
               return VoxelShapes.faceShapeOccludes(voxelshape, voxelshape1) ? 15 : p_215480_5_ + Math.max(1, mutableint.getValue());
            }
         }
      }
   }

   protected void checkNeighborsAfterUpdate(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      long i = SectionPos.blockToSection(p_215478_1_);

      for(Direction direction : DIRECTIONS) {
         long j = BlockPos.offset(p_215478_1_, direction);
         long k = SectionPos.blockToSection(j);
         if (i == k || this.storage.storingLightForSection(k)) {
            this.checkNeighbor(p_215478_1_, j, p_215478_3_, p_215478_4_);
         }
      }

   }

   protected int getComputedLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int i = p_215477_5_;
      if (Long.MAX_VALUE != p_215477_3_) {
         int j = this.computeLevelFromNeighbor(Long.MAX_VALUE, p_215477_1_, 0);
         if (p_215477_5_ > j) {
            i = j;
         }

         if (i == 0) {
            return i;
         }
      }

      long j1 = SectionPos.blockToSection(p_215477_1_);
      NibbleArray nibblearray = this.storage.getDataLayer(j1, true);

      for(Direction direction : DIRECTIONS) {
         long k = BlockPos.offset(p_215477_1_, direction);
         if (k != p_215477_3_) {
            long l = SectionPos.blockToSection(k);
            NibbleArray nibblearray1;
            if (j1 == l) {
               nibblearray1 = nibblearray;
            } else {
               nibblearray1 = this.storage.getDataLayer(l, true);
            }

            if (nibblearray1 != null) {
               int i1 = this.computeLevelFromNeighbor(k, p_215477_1_, this.getLevel(nibblearray1, k));
               if (i > i1) {
                  i = i1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   public void onBlockEmissionIncrease(BlockPos p_215623_1_, int p_215623_2_) {
      this.storage.runAllUpdates();
      this.checkEdge(Long.MAX_VALUE, p_215623_1_.asLong(), 15 - p_215623_2_, true);
   }

   @Override
   public int queuedUpdateSize() {
      return storage.queuedUpdateSize();
   }
}
