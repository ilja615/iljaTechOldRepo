package net.minecraft.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;

public class ShulkerAABBHelper {
   public static AxisAlignedBB openBoundingBox(BlockPos p_233539_0_, Direction p_233539_1_) {
      return VoxelShapes.block().bounds().expandTowards((double)(0.5F * (float)p_233539_1_.getStepX()), (double)(0.5F * (float)p_233539_1_.getStepY()), (double)(0.5F * (float)p_233539_1_.getStepZ())).contract((double)p_233539_1_.getStepX(), (double)p_233539_1_.getStepY(), (double)p_233539_1_.getStepZ()).move(p_233539_0_.relative(p_233539_1_));
   }
}
