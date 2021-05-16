package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public final class VoxelShapeCube extends VoxelShape {
   protected VoxelShapeCube(VoxelShapePart p_i48182_1_) {
      super(p_i48182_1_);
   }

   protected DoubleList getCoords(Direction.Axis p_197757_1_) {
      return new DoubleRangeList(this.shape.getSize(p_197757_1_));
   }

   protected int findIndex(Direction.Axis p_197749_1_, double p_197749_2_) {
      int i = this.shape.getSize(p_197749_1_);
      return MathHelper.clamp(MathHelper.floor(p_197749_2_ * (double)i), -1, i);
   }
}
