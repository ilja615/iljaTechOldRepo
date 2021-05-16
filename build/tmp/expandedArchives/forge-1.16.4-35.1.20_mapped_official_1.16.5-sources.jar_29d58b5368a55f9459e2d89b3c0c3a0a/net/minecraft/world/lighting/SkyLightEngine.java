package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SkyLightEngine extends LightEngine<SkyLightStorage.StorageMap, SkyLightStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

   public SkyLightEngine(IChunkLightProvider p_i51289_1_) {
      super(p_i51289_1_, LightType.SKY, new SkyLightStorage(p_i51289_1_));
   }

   protected int computeLevelFromNeighbor(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      if (p_215480_3_ == Long.MAX_VALUE) {
         return 15;
      } else {
         if (p_215480_1_ == Long.MAX_VALUE) {
            if (!this.storage.hasLightSource(p_215480_3_)) {
               return 15;
            }

            p_215480_5_ = 0;
         }

         if (p_215480_5_ >= 15) {
            return p_215480_5_;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.getStateAndOpacity(p_215480_3_, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               int i = BlockPos.getX(p_215480_1_);
               int j = BlockPos.getY(p_215480_1_);
               int k = BlockPos.getZ(p_215480_1_);
               int l = BlockPos.getX(p_215480_3_);
               int i1 = BlockPos.getY(p_215480_3_);
               int j1 = BlockPos.getZ(p_215480_3_);
               boolean flag = i == l && k == j1;
               int k1 = Integer.signum(l - i);
               int l1 = Integer.signum(i1 - j);
               int i2 = Integer.signum(j1 - k);
               Direction direction;
               if (p_215480_1_ == Long.MAX_VALUE) {
                  direction = Direction.DOWN;
               } else {
                  direction = Direction.fromNormal(k1, l1, i2);
               }

               BlockState blockstate1 = this.getStateAndOpacity(p_215480_1_, (MutableInt)null);
               if (direction != null) {
                  VoxelShape voxelshape = this.getShape(blockstate1, p_215480_1_, direction);
                  VoxelShape voxelshape1 = this.getShape(blockstate, p_215480_3_, direction.getOpposite());
                  if (VoxelShapes.faceShapeOccludes(voxelshape, voxelshape1)) {
                     return 15;
                  }
               } else {
                  VoxelShape voxelshape3 = this.getShape(blockstate1, p_215480_1_, Direction.DOWN);
                  if (VoxelShapes.faceShapeOccludes(voxelshape3, VoxelShapes.empty())) {
                     return 15;
                  }

                  int j2 = flag ? -1 : 0;
                  Direction direction1 = Direction.fromNormal(k1, j2, i2);
                  if (direction1 == null) {
                     return 15;
                  }

                  VoxelShape voxelshape2 = this.getShape(blockstate, p_215480_3_, direction1.getOpposite());
                  if (VoxelShapes.faceShapeOccludes(VoxelShapes.empty(), voxelshape2)) {
                     return 15;
                  }
               }

               boolean flag1 = p_215480_1_ == Long.MAX_VALUE || flag && j > i1;
               return flag1 && p_215480_5_ == 0 && mutableint.getValue() == 0 ? 0 : p_215480_5_ + Math.max(1, mutableint.getValue());
            }
         }
      }
   }

   protected void checkNeighborsAfterUpdate(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      long i = SectionPos.blockToSection(p_215478_1_);
      int j = BlockPos.getY(p_215478_1_);
      int k = SectionPos.sectionRelative(j);
      int l = SectionPos.blockToSectionCoord(j);
      int i1;
      if (k != 0) {
         i1 = 0;
      } else {
         int j1;
         for(j1 = 0; !this.storage.storingLightForSection(SectionPos.offset(i, 0, -j1 - 1, 0)) && this.storage.hasSectionsBelow(l - j1 - 1); ++j1) {
         }

         i1 = j1;
      }

      long i3 = BlockPos.offset(p_215478_1_, 0, -1 - i1 * 16, 0);
      long k1 = SectionPos.blockToSection(i3);
      if (i == k1 || this.storage.storingLightForSection(k1)) {
         this.checkNeighbor(p_215478_1_, i3, p_215478_3_, p_215478_4_);
      }

      long l1 = BlockPos.offset(p_215478_1_, Direction.UP);
      long i2 = SectionPos.blockToSection(l1);
      if (i == i2 || this.storage.storingLightForSection(i2)) {
         this.checkNeighbor(p_215478_1_, l1, p_215478_3_, p_215478_4_);
      }

      for(Direction direction : HORIZONTALS) {
         int j2 = 0;

         while(true) {
            long k2 = BlockPos.offset(p_215478_1_, direction.getStepX(), -j2, direction.getStepZ());
            long l2 = SectionPos.blockToSection(k2);
            if (i == l2) {
               this.checkNeighbor(p_215478_1_, k2, p_215478_3_, p_215478_4_);
               break;
            }

            if (this.storage.storingLightForSection(l2)) {
               this.checkNeighbor(p_215478_1_, k2, p_215478_3_, p_215478_4_);
            }

            ++j2;
            if (j2 > i1 * 16) {
               break;
            }
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
         long l = SectionPos.blockToSection(k);
         NibbleArray nibblearray1;
         if (j1 == l) {
            nibblearray1 = nibblearray;
         } else {
            nibblearray1 = this.storage.getDataLayer(l, true);
         }

         if (nibblearray1 != null) {
            if (k != p_215477_3_) {
               int k1 = this.computeLevelFromNeighbor(k, p_215477_1_, this.getLevel(nibblearray1, k));
               if (i > k1) {
                  i = k1;
               }

               if (i == 0) {
                  return i;
               }
            }
         } else if (direction != Direction.DOWN) {
            for(k = BlockPos.getFlatIndex(k); !this.storage.storingLightForSection(l) && !this.storage.isAboveData(l); k = BlockPos.offset(k, 0, 16, 0)) {
               l = SectionPos.offset(l, Direction.UP);
            }

            NibbleArray nibblearray2 = this.storage.getDataLayer(l, true);
            if (k != p_215477_3_) {
               int i1;
               if (nibblearray2 != null) {
                  i1 = this.computeLevelFromNeighbor(k, p_215477_1_, this.getLevel(nibblearray2, k));
               } else {
                  i1 = this.storage.lightOnInSection(l) ? 0 : 15;
               }

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

   protected void checkNode(long p_215473_1_) {
      this.storage.runAllUpdates();
      long i = SectionPos.blockToSection(p_215473_1_);
      if (this.storage.storingLightForSection(i)) {
         super.checkNode(p_215473_1_);
      } else {
         for(p_215473_1_ = BlockPos.getFlatIndex(p_215473_1_); !this.storage.storingLightForSection(i) && !this.storage.isAboveData(i); p_215473_1_ = BlockPos.offset(p_215473_1_, 0, 16, 0)) {
            i = SectionPos.offset(i, Direction.UP);
         }

         if (this.storage.storingLightForSection(i)) {
            super.checkNode(p_215473_1_);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugData(long p_215614_1_) {
      return super.getDebugData(p_215614_1_) + (this.storage.isAboveData(p_215614_1_) ? "*" : "");
   }

   @Override
   public int queuedUpdateSize() {
      return 0;
   }
}
