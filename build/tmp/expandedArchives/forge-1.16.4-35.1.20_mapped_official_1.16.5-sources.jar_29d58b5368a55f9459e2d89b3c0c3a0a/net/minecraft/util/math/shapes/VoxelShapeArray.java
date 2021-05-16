package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;

public final class VoxelShapeArray extends VoxelShape {
   private final DoubleList xs;
   private final DoubleList ys;
   private final DoubleList zs;

   protected VoxelShapeArray(VoxelShapePart p_i47693_1_, double[] p_i47693_2_, double[] p_i47693_3_, double[] p_i47693_4_) {
      this(p_i47693_1_, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_i47693_2_, p_i47693_1_.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_i47693_3_, p_i47693_1_.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_i47693_4_, p_i47693_1_.getZSize() + 1)));
   }

   VoxelShapeArray(VoxelShapePart p_i47694_1_, DoubleList p_i47694_2_, DoubleList p_i47694_3_, DoubleList p_i47694_4_) {
      super(p_i47694_1_);
      int i = p_i47694_1_.getXSize() + 1;
      int j = p_i47694_1_.getYSize() + 1;
      int k = p_i47694_1_.getZSize() + 1;
      if (i == p_i47694_2_.size() && j == p_i47694_3_.size() && k == p_i47694_4_.size()) {
         this.xs = p_i47694_2_;
         this.ys = p_i47694_3_;
         this.zs = p_i47694_4_;
      } else {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
      }
   }

   protected DoubleList getCoords(Direction.Axis p_197757_1_) {
      switch(p_197757_1_) {
      case X:
         return this.xs;
      case Y:
         return this.ys;
      case Z:
         return this.zs;
      default:
         throw new IllegalArgumentException();
      }
   }
}
