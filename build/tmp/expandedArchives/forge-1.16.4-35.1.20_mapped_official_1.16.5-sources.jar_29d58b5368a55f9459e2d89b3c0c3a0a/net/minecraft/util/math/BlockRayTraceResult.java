package net.minecraft.util.math;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public class BlockRayTraceResult extends RayTraceResult {
   private final Direction direction;
   private final BlockPos blockPos;
   private final boolean miss;
   private final boolean inside;

   public static BlockRayTraceResult miss(Vector3d p_216352_0_, Direction p_216352_1_, BlockPos p_216352_2_) {
      return new BlockRayTraceResult(true, p_216352_0_, p_216352_1_, p_216352_2_, false);
   }

   public BlockRayTraceResult(Vector3d p_i51186_1_, Direction p_i51186_2_, BlockPos p_i51186_3_, boolean p_i51186_4_) {
      this(false, p_i51186_1_, p_i51186_2_, p_i51186_3_, p_i51186_4_);
   }

   private BlockRayTraceResult(boolean p_i51187_1_, Vector3d p_i51187_2_, Direction p_i51187_3_, BlockPos p_i51187_4_, boolean p_i51187_5_) {
      super(p_i51187_2_);
      this.miss = p_i51187_1_;
      this.direction = p_i51187_3_;
      this.blockPos = p_i51187_4_;
      this.inside = p_i51187_5_;
   }

   public BlockRayTraceResult withDirection(Direction p_216351_1_) {
      return new BlockRayTraceResult(this.miss, this.location, p_216351_1_, this.blockPos, this.inside);
   }

   public BlockRayTraceResult withPosition(BlockPos p_237485_1_) {
      return new BlockRayTraceResult(this.miss, this.location, this.direction, p_237485_1_, this.inside);
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public RayTraceResult.Type getType() {
      return this.miss ? RayTraceResult.Type.MISS : RayTraceResult.Type.BLOCK;
   }

   public boolean isInside() {
      return this.inside;
   }
}
