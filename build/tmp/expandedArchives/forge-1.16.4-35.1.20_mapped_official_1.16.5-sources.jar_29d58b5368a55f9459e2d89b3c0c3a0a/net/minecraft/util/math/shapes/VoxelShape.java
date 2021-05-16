package net.minecraft.util.math.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VoxelShape {
   protected final VoxelShapePart shape;
   @Nullable
   private VoxelShape[] faces;

   VoxelShape(VoxelShapePart p_i47680_1_) {
      this.shape = p_i47680_1_;
   }

   public double min(Direction.Axis p_197762_1_) {
      int i = this.shape.firstFull(p_197762_1_);
      return i >= this.shape.getSize(p_197762_1_) ? Double.POSITIVE_INFINITY : this.get(p_197762_1_, i);
   }

   public double max(Direction.Axis p_197758_1_) {
      int i = this.shape.lastFull(p_197758_1_);
      return i <= 0 ? Double.NEGATIVE_INFINITY : this.get(p_197758_1_, i);
   }

   public AxisAlignedBB bounds() {
      if (this.isEmpty()) {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("No bounds for empty shape."));
      } else {
         return new AxisAlignedBB(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
      }
   }

   protected double get(Direction.Axis p_197759_1_, int p_197759_2_) {
      return this.getCoords(p_197759_1_).getDouble(p_197759_2_);
   }

   protected abstract DoubleList getCoords(Direction.Axis p_197757_1_);

   public boolean isEmpty() {
      return this.shape.isEmpty();
   }

   public VoxelShape move(double p_197751_1_, double p_197751_3_, double p_197751_5_) {
      return (VoxelShape)(this.isEmpty() ? VoxelShapes.empty() : new VoxelShapeArray(this.shape, (DoubleList)(new OffsetDoubleList(this.getCoords(Direction.Axis.X), p_197751_1_)), (DoubleList)(new OffsetDoubleList(this.getCoords(Direction.Axis.Y), p_197751_3_)), (DoubleList)(new OffsetDoubleList(this.getCoords(Direction.Axis.Z), p_197751_5_))));
   }

   public VoxelShape optimize() {
      VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty()};
      this.forAllBoxes((p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_) -> {
         avoxelshape[0] = VoxelShapes.joinUnoptimized(avoxelshape[0], VoxelShapes.box(p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_), IBooleanFunction.OR);
      });
      return avoxelshape[0];
   }

   @OnlyIn(Dist.CLIENT)
   public void forAllEdges(VoxelShapes.ILineConsumer p_197754_1_) {
      this.shape.forAllEdges((p_197750_2_, p_197750_3_, p_197750_4_, p_197750_5_, p_197750_6_, p_197750_7_) -> {
         p_197754_1_.consume(this.get(Direction.Axis.X, p_197750_2_), this.get(Direction.Axis.Y, p_197750_3_), this.get(Direction.Axis.Z, p_197750_4_), this.get(Direction.Axis.X, p_197750_5_), this.get(Direction.Axis.Y, p_197750_6_), this.get(Direction.Axis.Z, p_197750_7_));
      }, true);
   }

   public void forAllBoxes(VoxelShapes.ILineConsumer p_197755_1_) {
      DoubleList doublelist = this.getCoords(Direction.Axis.X);
      DoubleList doublelist1 = this.getCoords(Direction.Axis.Y);
      DoubleList doublelist2 = this.getCoords(Direction.Axis.Z);
      this.shape.forAllBoxes((p_224789_4_, p_224789_5_, p_224789_6_, p_224789_7_, p_224789_8_, p_224789_9_) -> {
         p_197755_1_.consume(doublelist.getDouble(p_224789_4_), doublelist1.getDouble(p_224789_5_), doublelist2.getDouble(p_224789_6_), doublelist.getDouble(p_224789_7_), doublelist1.getDouble(p_224789_8_), doublelist2.getDouble(p_224789_9_));
      }, true);
   }

   public List<AxisAlignedBB> toAabbs() {
      List<AxisAlignedBB> list = Lists.newArrayList();
      this.forAllBoxes((p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_) -> {
         list.add(new AxisAlignedBB(p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_));
      });
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public double max(Direction.Axis p_197760_1_, double p_197760_2_, double p_197760_4_) {
      Direction.Axis direction$axis = AxisRotation.FORWARD.cycle(p_197760_1_);
      Direction.Axis direction$axis1 = AxisRotation.BACKWARD.cycle(p_197760_1_);
      int i = this.findIndex(direction$axis, p_197760_2_);
      int j = this.findIndex(direction$axis1, p_197760_4_);
      int k = this.shape.lastFull(p_197760_1_, i, j);
      return k <= 0 ? Double.NEGATIVE_INFINITY : this.get(p_197760_1_, k);
   }

   protected int findIndex(Direction.Axis p_197749_1_, double p_197749_2_) {
      return MathHelper.binarySearch(0, this.shape.getSize(p_197749_1_) + 1, (p_197761_4_) -> {
         if (p_197761_4_ < 0) {
            return false;
         } else if (p_197761_4_ > this.shape.getSize(p_197749_1_)) {
            return true;
         } else {
            return p_197749_2_ < this.get(p_197749_1_, p_197761_4_);
         }
      }) - 1;
   }

   protected boolean isFullWide(double p_211542_1_, double p_211542_3_, double p_211542_5_) {
      return this.shape.isFullWide(this.findIndex(Direction.Axis.X, p_211542_1_), this.findIndex(Direction.Axis.Y, p_211542_3_), this.findIndex(Direction.Axis.Z, p_211542_5_));
   }

   @Nullable
   public BlockRayTraceResult clip(Vector3d p_212433_1_, Vector3d p_212433_2_, BlockPos p_212433_3_) {
      if (this.isEmpty()) {
         return null;
      } else {
         Vector3d vector3d = p_212433_2_.subtract(p_212433_1_);
         if (vector3d.lengthSqr() < 1.0E-7D) {
            return null;
         } else {
            Vector3d vector3d1 = p_212433_1_.add(vector3d.scale(0.001D));
            return this.isFullWide(vector3d1.x - (double)p_212433_3_.getX(), vector3d1.y - (double)p_212433_3_.getY(), vector3d1.z - (double)p_212433_3_.getZ()) ? new BlockRayTraceResult(vector3d1, Direction.getNearest(vector3d.x, vector3d.y, vector3d.z).getOpposite(), p_212433_3_, true) : AxisAlignedBB.clip(this.toAabbs(), p_212433_1_, p_212433_2_, p_212433_3_);
         }
      }
   }

   public VoxelShape getFaceShape(Direction p_212434_1_) {
      if (!this.isEmpty() && this != VoxelShapes.block()) {
         if (this.faces != null) {
            VoxelShape voxelshape = this.faces[p_212434_1_.ordinal()];
            if (voxelshape != null) {
               return voxelshape;
            }
         } else {
            this.faces = new VoxelShape[6];
         }

         VoxelShape voxelshape1 = this.calculateFace(p_212434_1_);
         this.faces[p_212434_1_.ordinal()] = voxelshape1;
         return voxelshape1;
      } else {
         return this;
      }
   }

   private VoxelShape calculateFace(Direction p_222863_1_) {
      Direction.Axis direction$axis = p_222863_1_.getAxis();
      Direction.AxisDirection direction$axisdirection = p_222863_1_.getAxisDirection();
      DoubleList doublelist = this.getCoords(direction$axis);
      if (doublelist.size() == 2 && DoubleMath.fuzzyEquals(doublelist.getDouble(0), 0.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(doublelist.getDouble(1), 1.0D, 1.0E-7D)) {
         return this;
      } else {
         int i = this.findIndex(direction$axis, direction$axisdirection == Direction.AxisDirection.POSITIVE ? 0.9999999D : 1.0E-7D);
         return new SplitVoxelShape(this, direction$axis, i);
      }
   }

   public double collide(Direction.Axis p_212430_1_, AxisAlignedBB p_212430_2_, double p_212430_3_) {
      return this.collideX(AxisRotation.between(p_212430_1_, Direction.Axis.X), p_212430_2_, p_212430_3_);
   }

   protected double collideX(AxisRotation p_212431_1_, AxisAlignedBB p_212431_2_, double p_212431_3_) {
      if (this.isEmpty()) {
         return p_212431_3_;
      } else if (Math.abs(p_212431_3_) < 1.0E-7D) {
         return 0.0D;
      } else {
         AxisRotation axisrotation = p_212431_1_.inverse();
         Direction.Axis direction$axis = axisrotation.cycle(Direction.Axis.X);
         Direction.Axis direction$axis1 = axisrotation.cycle(Direction.Axis.Y);
         Direction.Axis direction$axis2 = axisrotation.cycle(Direction.Axis.Z);
         double d0 = p_212431_2_.max(direction$axis);
         double d1 = p_212431_2_.min(direction$axis);
         int i = this.findIndex(direction$axis, d1 + 1.0E-7D);
         int j = this.findIndex(direction$axis, d0 - 1.0E-7D);
         int k = Math.max(0, this.findIndex(direction$axis1, p_212431_2_.min(direction$axis1) + 1.0E-7D));
         int l = Math.min(this.shape.getSize(direction$axis1), this.findIndex(direction$axis1, p_212431_2_.max(direction$axis1) - 1.0E-7D) + 1);
         int i1 = Math.max(0, this.findIndex(direction$axis2, p_212431_2_.min(direction$axis2) + 1.0E-7D));
         int j1 = Math.min(this.shape.getSize(direction$axis2), this.findIndex(direction$axis2, p_212431_2_.max(direction$axis2) - 1.0E-7D) + 1);
         int k1 = this.shape.getSize(direction$axis);
         if (p_212431_3_ > 0.0D) {
            for(int l1 = j + 1; l1 < k1; ++l1) {
               for(int i2 = k; i2 < l; ++i2) {
                  for(int j2 = i1; j2 < j1; ++j2) {
                     if (this.shape.isFullWide(axisrotation, l1, i2, j2)) {
                        double d2 = this.get(direction$axis, l1) - d0;
                        if (d2 >= -1.0E-7D) {
                           p_212431_3_ = Math.min(p_212431_3_, d2);
                        }

                        return p_212431_3_;
                     }
                  }
               }
            }
         } else if (p_212431_3_ < 0.0D) {
            for(int k2 = i - 1; k2 >= 0; --k2) {
               for(int l2 = k; l2 < l; ++l2) {
                  for(int i3 = i1; i3 < j1; ++i3) {
                     if (this.shape.isFullWide(axisrotation, k2, l2, i3)) {
                        double d3 = this.get(direction$axis, k2 + 1) - d1;
                        if (d3 <= 1.0E-7D) {
                           p_212431_3_ = Math.max(p_212431_3_, d3);
                        }

                        return p_212431_3_;
                     }
                  }
               }
            }
         }

         return p_212431_3_;
      }
   }

   public String toString() {
      return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.bounds() + "]";
   }
}
