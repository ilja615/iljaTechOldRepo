package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class BlockPosWrapper implements IPosWrapper {
   private final BlockPos blockPos;
   private final Vector3d centerPosition;

   public BlockPosWrapper(BlockPos p_i50371_1_) {
      this.blockPos = p_i50371_1_;
      this.centerPosition = Vector3d.atCenterOf(p_i50371_1_);
   }

   public Vector3d currentPosition() {
      return this.centerPosition;
   }

   public BlockPos currentBlockPosition() {
      return this.blockPos;
   }

   public boolean isVisibleBy(LivingEntity p_220610_1_) {
      return true;
   }

   public String toString() {
      return "BlockPosTracker{blockPos=" + this.blockPos + ", centerPosition=" + this.centerPosition + '}';
   }
}
