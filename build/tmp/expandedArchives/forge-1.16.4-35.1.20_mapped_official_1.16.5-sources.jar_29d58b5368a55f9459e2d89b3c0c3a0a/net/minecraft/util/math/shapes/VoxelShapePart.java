package net.minecraft.util.math.shapes;

import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VoxelShapePart {
   private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;

   protected VoxelShapePart(int p_i47686_1_, int p_i47686_2_, int p_i47686_3_) {
      this.xSize = p_i47686_1_;
      this.ySize = p_i47686_2_;
      this.zSize = p_i47686_3_;
   }

   public boolean isFullWide(AxisRotation p_197824_1_, int p_197824_2_, int p_197824_3_, int p_197824_4_) {
      return this.isFullWide(p_197824_1_.cycle(p_197824_2_, p_197824_3_, p_197824_4_, Direction.Axis.X), p_197824_1_.cycle(p_197824_2_, p_197824_3_, p_197824_4_, Direction.Axis.Y), p_197824_1_.cycle(p_197824_2_, p_197824_3_, p_197824_4_, Direction.Axis.Z));
   }

   public boolean isFullWide(int p_197818_1_, int p_197818_2_, int p_197818_3_) {
      if (p_197818_1_ >= 0 && p_197818_2_ >= 0 && p_197818_3_ >= 0) {
         return p_197818_1_ < this.xSize && p_197818_2_ < this.ySize && p_197818_3_ < this.zSize ? this.isFull(p_197818_1_, p_197818_2_, p_197818_3_) : false;
      } else {
         return false;
      }
   }

   public boolean isFull(AxisRotation p_197829_1_, int p_197829_2_, int p_197829_3_, int p_197829_4_) {
      return this.isFull(p_197829_1_.cycle(p_197829_2_, p_197829_3_, p_197829_4_, Direction.Axis.X), p_197829_1_.cycle(p_197829_2_, p_197829_3_, p_197829_4_, Direction.Axis.Y), p_197829_1_.cycle(p_197829_2_, p_197829_3_, p_197829_4_, Direction.Axis.Z));
   }

   public abstract boolean isFull(int p_197835_1_, int p_197835_2_, int p_197835_3_);

   public abstract void setFull(int p_199625_1_, int p_199625_2_, int p_199625_3_, boolean p_199625_4_, boolean p_199625_5_);

   public boolean isEmpty() {
      for(Direction.Axis direction$axis : AXIS_VALUES) {
         if (this.firstFull(direction$axis) >= this.lastFull(direction$axis)) {
            return true;
         }
      }

      return false;
   }

   public abstract int firstFull(Direction.Axis p_199623_1_);

   public abstract int lastFull(Direction.Axis p_199624_1_);

   @OnlyIn(Dist.CLIENT)
   public int lastFull(Direction.Axis p_197836_1_, int p_197836_2_, int p_197836_3_) {
      if (p_197836_2_ >= 0 && p_197836_3_ >= 0) {
         Direction.Axis direction$axis = AxisRotation.FORWARD.cycle(p_197836_1_);
         Direction.Axis direction$axis1 = AxisRotation.BACKWARD.cycle(p_197836_1_);
         if (p_197836_2_ < this.getSize(direction$axis) && p_197836_3_ < this.getSize(direction$axis1)) {
            int i = this.getSize(p_197836_1_);
            AxisRotation axisrotation = AxisRotation.between(Direction.Axis.X, p_197836_1_);

            for(int j = i - 1; j >= 0; --j) {
               if (this.isFull(axisrotation, j, p_197836_2_, p_197836_3_)) {
                  return j + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int getSize(Direction.Axis p_197819_1_) {
      return p_197819_1_.choose(this.xSize, this.ySize, this.zSize);
   }

   public int getXSize() {
      return this.getSize(Direction.Axis.X);
   }

   public int getYSize() {
      return this.getSize(Direction.Axis.Y);
   }

   public int getZSize() {
      return this.getSize(Direction.Axis.Z);
   }

   @OnlyIn(Dist.CLIENT)
   public void forAllEdges(VoxelShapePart.ILineConsumer p_197828_1_, boolean p_197828_2_) {
      this.forAllAxisEdges(p_197828_1_, AxisRotation.NONE, p_197828_2_);
      this.forAllAxisEdges(p_197828_1_, AxisRotation.FORWARD, p_197828_2_);
      this.forAllAxisEdges(p_197828_1_, AxisRotation.BACKWARD, p_197828_2_);
   }

   @OnlyIn(Dist.CLIENT)
   private void forAllAxisEdges(VoxelShapePart.ILineConsumer p_197832_1_, AxisRotation p_197832_2_, boolean p_197832_3_) {
      AxisRotation axisrotation = p_197832_2_.inverse();
      int j = this.getSize(axisrotation.cycle(Direction.Axis.X));
      int k = this.getSize(axisrotation.cycle(Direction.Axis.Y));
      int l = this.getSize(axisrotation.cycle(Direction.Axis.Z));

      for(int i1 = 0; i1 <= j; ++i1) {
         for(int j1 = 0; j1 <= k; ++j1) {
            int i = -1;

            for(int k1 = 0; k1 <= l; ++k1) {
               int l1 = 0;
               int i2 = 0;

               for(int j2 = 0; j2 <= 1; ++j2) {
                  for(int k2 = 0; k2 <= 1; ++k2) {
                     if (this.isFullWide(axisrotation, i1 + j2 - 1, j1 + k2 - 1, k1)) {
                        ++l1;
                        i2 ^= j2 ^ k2;
                     }
                  }
               }

               if (l1 == 1 || l1 == 3 || l1 == 2 && (i2 & 1) == 0) {
                  if (p_197832_3_) {
                     if (i == -1) {
                        i = k1;
                     }
                  } else {
                     p_197832_1_.consume(axisrotation.cycle(i1, j1, k1, Direction.Axis.X), axisrotation.cycle(i1, j1, k1, Direction.Axis.Y), axisrotation.cycle(i1, j1, k1, Direction.Axis.Z), axisrotation.cycle(i1, j1, k1 + 1, Direction.Axis.X), axisrotation.cycle(i1, j1, k1 + 1, Direction.Axis.Y), axisrotation.cycle(i1, j1, k1 + 1, Direction.Axis.Z));
                  }
               } else if (i != -1) {
                  p_197832_1_.consume(axisrotation.cycle(i1, j1, i, Direction.Axis.X), axisrotation.cycle(i1, j1, i, Direction.Axis.Y), axisrotation.cycle(i1, j1, i, Direction.Axis.Z), axisrotation.cycle(i1, j1, k1, Direction.Axis.X), axisrotation.cycle(i1, j1, k1, Direction.Axis.Y), axisrotation.cycle(i1, j1, k1, Direction.Axis.Z));
                  i = -1;
               }
            }
         }
      }

   }

   protected boolean isZStripFull(int p_197833_1_, int p_197833_2_, int p_197833_3_, int p_197833_4_) {
      for(int i = p_197833_1_; i < p_197833_2_; ++i) {
         if (!this.isFullWide(p_197833_3_, p_197833_4_, i)) {
            return false;
         }
      }

      return true;
   }

   protected void setZStrip(int p_197834_1_, int p_197834_2_, int p_197834_3_, int p_197834_4_, boolean p_197834_5_) {
      for(int i = p_197834_1_; i < p_197834_2_; ++i) {
         this.setFull(p_197834_3_, p_197834_4_, i, false, p_197834_5_);
      }

   }

   protected boolean isXZRectangleFull(int p_197827_1_, int p_197827_2_, int p_197827_3_, int p_197827_4_, int p_197827_5_) {
      for(int i = p_197827_1_; i < p_197827_2_; ++i) {
         if (!this.isZStripFull(p_197827_3_, p_197827_4_, i, p_197827_5_)) {
            return false;
         }
      }

      return true;
   }

   public void forAllBoxes(VoxelShapePart.ILineConsumer p_197831_1_, boolean p_197831_2_) {
      VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(this);

      for(int i = 0; i <= this.xSize; ++i) {
         for(int j = 0; j <= this.ySize; ++j) {
            int k = -1;

            for(int l = 0; l <= this.zSize; ++l) {
               if (voxelshapepart.isFullWide(i, j, l)) {
                  if (p_197831_2_) {
                     if (k == -1) {
                        k = l;
                     }
                  } else {
                     p_197831_1_.consume(i, j, l, i + 1, j + 1, l + 1);
                  }
               } else if (k != -1) {
                  int i1 = i;
                  int j1 = i;
                  int k1 = j;
                  int l1 = j;
                  voxelshapepart.setZStrip(k, l, i, j, false);

                  while(voxelshapepart.isZStripFull(k, l, i1 - 1, k1)) {
                     voxelshapepart.setZStrip(k, l, i1 - 1, k1, false);
                     --i1;
                  }

                  while(voxelshapepart.isZStripFull(k, l, j1 + 1, k1)) {
                     voxelshapepart.setZStrip(k, l, j1 + 1, k1, false);
                     ++j1;
                  }

                  while(voxelshapepart.isXZRectangleFull(i1, j1 + 1, k, l, k1 - 1)) {
                     for(int i2 = i1; i2 <= j1; ++i2) {
                        voxelshapepart.setZStrip(k, l, i2, k1 - 1, false);
                     }

                     --k1;
                  }

                  while(voxelshapepart.isXZRectangleFull(i1, j1 + 1, k, l, l1 + 1)) {
                     for(int j2 = i1; j2 <= j1; ++j2) {
                        voxelshapepart.setZStrip(k, l, j2, l1 + 1, false);
                     }

                     ++l1;
                  }

                  p_197831_1_.consume(i1, k1, k, j1 + 1, l1 + 1, l);
                  k = -1;
               }
            }
         }
      }

   }

   public void forAllFaces(VoxelShapePart.IFaceConsumer p_211540_1_) {
      this.forAllAxisFaces(p_211540_1_, AxisRotation.NONE);
      this.forAllAxisFaces(p_211540_1_, AxisRotation.FORWARD);
      this.forAllAxisFaces(p_211540_1_, AxisRotation.BACKWARD);
   }

   private void forAllAxisFaces(VoxelShapePart.IFaceConsumer p_211541_1_, AxisRotation p_211541_2_) {
      AxisRotation axisrotation = p_211541_2_.inverse();
      Direction.Axis direction$axis = axisrotation.cycle(Direction.Axis.Z);
      int i = this.getSize(axisrotation.cycle(Direction.Axis.X));
      int j = this.getSize(axisrotation.cycle(Direction.Axis.Y));
      int k = this.getSize(direction$axis);
      Direction direction = Direction.fromAxisAndDirection(direction$axis, Direction.AxisDirection.NEGATIVE);
      Direction direction1 = Direction.fromAxisAndDirection(direction$axis, Direction.AxisDirection.POSITIVE);

      for(int l = 0; l < i; ++l) {
         for(int i1 = 0; i1 < j; ++i1) {
            boolean flag = false;

            for(int j1 = 0; j1 <= k; ++j1) {
               boolean flag1 = j1 != k && this.isFull(axisrotation, l, i1, j1);
               if (!flag && flag1) {
                  p_211541_1_.consume(direction, axisrotation.cycle(l, i1, j1, Direction.Axis.X), axisrotation.cycle(l, i1, j1, Direction.Axis.Y), axisrotation.cycle(l, i1, j1, Direction.Axis.Z));
               }

               if (flag && !flag1) {
                  p_211541_1_.consume(direction1, axisrotation.cycle(l, i1, j1 - 1, Direction.Axis.X), axisrotation.cycle(l, i1, j1 - 1, Direction.Axis.Y), axisrotation.cycle(l, i1, j1 - 1, Direction.Axis.Z));
               }

               flag = flag1;
            }
         }
      }

   }

   public interface IFaceConsumer {
      void consume(Direction p_consume_1_, int p_consume_2_, int p_consume_3_, int p_consume_4_);
   }

   public interface ILineConsumer {
      void consume(int p_consume_1_, int p_consume_2_, int p_consume_3_, int p_consume_4_, int p_consume_5_, int p_consume_6_);
   }
}
