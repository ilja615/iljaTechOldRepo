package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

public interface IBlockReader {
   @Nullable
   TileEntity getBlockEntity(BlockPos p_175625_1_);

   BlockState getBlockState(BlockPos p_180495_1_);

   FluidState getFluidState(BlockPos p_204610_1_);

   default int getLightEmission(BlockPos p_217298_1_) {
      return this.getBlockState(p_217298_1_).getLightValue(this, p_217298_1_);
   }

   default int getMaxLightLevel() {
      return 15;
   }

   default int getMaxBuildHeight() {
      return 256;
   }

   default Stream<BlockState> getBlockStates(AxisAlignedBB p_234853_1_) {
      return BlockPos.betweenClosedStream(p_234853_1_).map(this::getBlockState);
   }

   default BlockRayTraceResult clip(RayTraceContext p_217299_1_) {
      return traverseBlocks(p_217299_1_, (p_217297_1_, p_217297_2_) -> {
         BlockState blockstate = this.getBlockState(p_217297_2_);
         FluidState fluidstate = this.getFluidState(p_217297_2_);
         Vector3d vector3d = p_217297_1_.getFrom();
         Vector3d vector3d1 = p_217297_1_.getTo();
         VoxelShape voxelshape = p_217297_1_.getBlockShape(blockstate, this, p_217297_2_);
         BlockRayTraceResult blockraytraceresult = this.clipWithInteractionOverride(vector3d, vector3d1, p_217297_2_, voxelshape, blockstate);
         VoxelShape voxelshape1 = p_217297_1_.getFluidShape(fluidstate, this, p_217297_2_);
         BlockRayTraceResult blockraytraceresult1 = voxelshape1.clip(vector3d, vector3d1, p_217297_2_);
         double d0 = blockraytraceresult == null ? Double.MAX_VALUE : p_217297_1_.getFrom().distanceToSqr(blockraytraceresult.getLocation());
         double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : p_217297_1_.getFrom().distanceToSqr(blockraytraceresult1.getLocation());
         return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
      }, (p_217302_0_) -> {
         Vector3d vector3d = p_217302_0_.getFrom().subtract(p_217302_0_.getTo());
         return BlockRayTraceResult.miss(p_217302_0_.getTo(), Direction.getNearest(vector3d.x, vector3d.y, vector3d.z), new BlockPos(p_217302_0_.getTo()));
      });
   }

   @Nullable
   default BlockRayTraceResult clipWithInteractionOverride(Vector3d p_217296_1_, Vector3d p_217296_2_, BlockPos p_217296_3_, VoxelShape p_217296_4_, BlockState p_217296_5_) {
      BlockRayTraceResult blockraytraceresult = p_217296_4_.clip(p_217296_1_, p_217296_2_, p_217296_3_);
      if (blockraytraceresult != null) {
         BlockRayTraceResult blockraytraceresult1 = p_217296_5_.getInteractionShape(this, p_217296_3_).clip(p_217296_1_, p_217296_2_, p_217296_3_);
         if (blockraytraceresult1 != null && blockraytraceresult1.getLocation().subtract(p_217296_1_).lengthSqr() < blockraytraceresult.getLocation().subtract(p_217296_1_).lengthSqr()) {
            return blockraytraceresult.withDirection(blockraytraceresult1.getDirection());
         }
      }

      return blockraytraceresult;
   }

   default double getBlockFloorHeight(VoxelShape p_242402_1_, Supplier<VoxelShape> p_242402_2_) {
      if (!p_242402_1_.isEmpty()) {
         return p_242402_1_.max(Direction.Axis.Y);
      } else {
         double d0 = p_242402_2_.get().max(Direction.Axis.Y);
         return d0 >= 1.0D ? d0 - 1.0D : Double.NEGATIVE_INFINITY;
      }
   }

   default double getBlockFloorHeight(BlockPos p_242403_1_) {
      return this.getBlockFloorHeight(this.getBlockState(p_242403_1_).getCollisionShape(this, p_242403_1_), () -> {
         BlockPos blockpos = p_242403_1_.below();
         return this.getBlockState(blockpos).getCollisionShape(this, blockpos);
      });
   }

   static <T> T traverseBlocks(RayTraceContext p_217300_0_, BiFunction<RayTraceContext, BlockPos, T> p_217300_1_, Function<RayTraceContext, T> p_217300_2_) {
      Vector3d vector3d = p_217300_0_.getFrom();
      Vector3d vector3d1 = p_217300_0_.getTo();
      if (vector3d.equals(vector3d1)) {
         return p_217300_2_.apply(p_217300_0_);
      } else {
         double d0 = MathHelper.lerp(-1.0E-7D, vector3d1.x, vector3d.x);
         double d1 = MathHelper.lerp(-1.0E-7D, vector3d1.y, vector3d.y);
         double d2 = MathHelper.lerp(-1.0E-7D, vector3d1.z, vector3d.z);
         double d3 = MathHelper.lerp(-1.0E-7D, vector3d.x, vector3d1.x);
         double d4 = MathHelper.lerp(-1.0E-7D, vector3d.y, vector3d1.y);
         double d5 = MathHelper.lerp(-1.0E-7D, vector3d.z, vector3d1.z);
         int i = MathHelper.floor(d3);
         int j = MathHelper.floor(d4);
         int k = MathHelper.floor(d5);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(i, j, k);
         T t = p_217300_1_.apply(p_217300_0_, blockpos$mutable);
         if (t != null) {
            return t;
         } else {
            double d6 = d0 - d3;
            double d7 = d1 - d4;
            double d8 = d2 - d5;
            int l = MathHelper.sign(d6);
            int i1 = MathHelper.sign(d7);
            int j1 = MathHelper.sign(d8);
            double d9 = l == 0 ? Double.MAX_VALUE : (double)l / d6;
            double d10 = i1 == 0 ? Double.MAX_VALUE : (double)i1 / d7;
            double d11 = j1 == 0 ? Double.MAX_VALUE : (double)j1 / d8;
            double d12 = d9 * (l > 0 ? 1.0D - MathHelper.frac(d3) : MathHelper.frac(d3));
            double d13 = d10 * (i1 > 0 ? 1.0D - MathHelper.frac(d4) : MathHelper.frac(d4));
            double d14 = d11 * (j1 > 0 ? 1.0D - MathHelper.frac(d5) : MathHelper.frac(d5));

            while(d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
               if (d12 < d13) {
                  if (d12 < d14) {
                     i += l;
                     d12 += d9;
                  } else {
                     k += j1;
                     d14 += d11;
                  }
               } else if (d13 < d14) {
                  j += i1;
                  d13 += d10;
               } else {
                  k += j1;
                  d14 += d11;
               }

               T t1 = p_217300_1_.apply(p_217300_0_, blockpos$mutable.set(i, j, k));
               if (t1 != null) {
                  return t1;
               }
            }

            return p_217300_2_.apply(p_217300_0_);
         }
      }
   }
}
