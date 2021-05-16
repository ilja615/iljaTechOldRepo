package net.minecraft.entity.ai.brain.memory;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.vector.Vector3d;

public class WalkTarget {
   private final IPosWrapper target;
   private final float speedModifier;
   private final int closeEnoughDist;

   public WalkTarget(BlockPos p_i50302_1_, float p_i50302_2_, int p_i50302_3_) {
      this(new BlockPosWrapper(p_i50302_1_), p_i50302_2_, p_i50302_3_);
   }

   public WalkTarget(Vector3d p_i50303_1_, float p_i50303_2_, int p_i50303_3_) {
      this(new BlockPosWrapper(new BlockPos(p_i50303_1_)), p_i50303_2_, p_i50303_3_);
   }

   public WalkTarget(IPosWrapper p_i50304_1_, float p_i50304_2_, int p_i50304_3_) {
      this.target = p_i50304_1_;
      this.speedModifier = p_i50304_2_;
      this.closeEnoughDist = p_i50304_3_;
   }

   public IPosWrapper getTarget() {
      return this.target;
   }

   public float getSpeedModifier() {
      return this.speedModifier;
   }

   public int getCloseEnoughDist() {
      return this.closeEnoughDist;
   }
}
