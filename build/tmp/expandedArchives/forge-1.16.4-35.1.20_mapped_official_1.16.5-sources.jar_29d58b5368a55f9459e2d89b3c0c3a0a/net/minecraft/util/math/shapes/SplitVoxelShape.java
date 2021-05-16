package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;

public class SplitVoxelShape extends VoxelShape {
   private final VoxelShape delegate;
   private final Direction.Axis axis;
   private static final DoubleList SLICE_COORDS = new DoubleRangeList(1);

   public SplitVoxelShape(VoxelShape p_i47682_1_, Direction.Axis p_i47682_2_, int p_i47682_3_) {
      super(makeSlice(p_i47682_1_.shape, p_i47682_2_, p_i47682_3_));
      this.delegate = p_i47682_1_;
      this.axis = p_i47682_2_;
   }

   private static VoxelShapePart makeSlice(VoxelShapePart p_197775_0_, Direction.Axis p_197775_1_, int p_197775_2_) {
      return new PartSplitVoxelShape(p_197775_0_, p_197775_1_.choose(p_197775_2_, 0, 0), p_197775_1_.choose(0, p_197775_2_, 0), p_197775_1_.choose(0, 0, p_197775_2_), p_197775_1_.choose(p_197775_2_ + 1, p_197775_0_.xSize, p_197775_0_.xSize), p_197775_1_.choose(p_197775_0_.ySize, p_197775_2_ + 1, p_197775_0_.ySize), p_197775_1_.choose(p_197775_0_.zSize, p_197775_0_.zSize, p_197775_2_ + 1));
   }

   protected DoubleList getCoords(Direction.Axis p_197757_1_) {
      return p_197757_1_ == this.axis ? SLICE_COORDS : this.delegate.getCoords(p_197757_1_);
   }
}
