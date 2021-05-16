package net.minecraft.util;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ICollisionReader;

public class TransportationHelper {
   public static int[][] offsetsForDirection(Direction p_234632_0_) {
      Direction direction = p_234632_0_.getClockWise();
      Direction direction1 = direction.getOpposite();
      Direction direction2 = p_234632_0_.getOpposite();
      return new int[][]{{direction.getStepX(), direction.getStepZ()}, {direction1.getStepX(), direction1.getStepZ()}, {direction2.getStepX() + direction.getStepX(), direction2.getStepZ() + direction.getStepZ()}, {direction2.getStepX() + direction1.getStepX(), direction2.getStepZ() + direction1.getStepZ()}, {p_234632_0_.getStepX() + direction.getStepX(), p_234632_0_.getStepZ() + direction.getStepZ()}, {p_234632_0_.getStepX() + direction1.getStepX(), p_234632_0_.getStepZ() + direction1.getStepZ()}, {direction2.getStepX(), direction2.getStepZ()}, {p_234632_0_.getStepX(), p_234632_0_.getStepZ()}};
   }

   public static boolean isBlockFloorValid(double p_234630_0_) {
      return !Double.isInfinite(p_234630_0_) && p_234630_0_ < 1.0D;
   }

   public static boolean canDismountTo(ICollisionReader p_234631_0_, LivingEntity p_234631_1_, AxisAlignedBB p_234631_2_) {
      return p_234631_0_.getBlockCollisions(p_234631_1_, p_234631_2_).allMatch(VoxelShape::isEmpty);
   }

   @Nullable
   public static Vector3d findDismountLocation(ICollisionReader p_242381_0_, double p_242381_1_, double p_242381_3_, double p_242381_5_, LivingEntity p_242381_7_, Pose p_242381_8_) {
      if (isBlockFloorValid(p_242381_3_)) {
         Vector3d vector3d = new Vector3d(p_242381_1_, p_242381_3_, p_242381_5_);
         if (canDismountTo(p_242381_0_, p_242381_7_, p_242381_7_.getLocalBoundsForPose(p_242381_8_).move(vector3d))) {
            return vector3d;
         }
      }

      return null;
   }

   public static VoxelShape nonClimbableShape(IBlockReader p_242380_0_, BlockPos p_242380_1_) {
      BlockState blockstate = p_242380_0_.getBlockState(p_242380_1_);
      return !blockstate.is(BlockTags.CLIMBABLE) && (!(blockstate.getBlock() instanceof TrapDoorBlock) || !blockstate.getValue(TrapDoorBlock.OPEN)) ? blockstate.getCollisionShape(p_242380_0_, p_242380_1_) : VoxelShapes.empty();
   }

   public static double findCeilingFrom(BlockPos p_242383_0_, int p_242383_1_, Function<BlockPos, VoxelShape> p_242383_2_) {
      BlockPos.Mutable blockpos$mutable = p_242383_0_.mutable();
      int i = 0;

      while(i < p_242383_1_) {
         VoxelShape voxelshape = p_242383_2_.apply(blockpos$mutable);
         if (!voxelshape.isEmpty()) {
            return (double)(p_242383_0_.getY() + i) + voxelshape.min(Direction.Axis.Y);
         }

         ++i;
         blockpos$mutable.move(Direction.UP);
      }

      return Double.POSITIVE_INFINITY;
   }

   @Nullable
   public static Vector3d findSafeDismountLocation(EntityType<?> p_242379_0_, ICollisionReader p_242379_1_, BlockPos p_242379_2_, boolean p_242379_3_) {
      if (p_242379_3_ && p_242379_0_.isBlockDangerous(p_242379_1_.getBlockState(p_242379_2_))) {
         return null;
      } else {
         double d0 = p_242379_1_.getBlockFloorHeight(nonClimbableShape(p_242379_1_, p_242379_2_), () -> {
            return nonClimbableShape(p_242379_1_, p_242379_2_.below());
         });
         if (!isBlockFloorValid(d0)) {
            return null;
         } else if (p_242379_3_ && d0 <= 0.0D && p_242379_0_.isBlockDangerous(p_242379_1_.getBlockState(p_242379_2_.below()))) {
            return null;
         } else {
            Vector3d vector3d = Vector3d.upFromBottomCenterOf(p_242379_2_, d0);
            return p_242379_1_.getBlockCollisions((Entity)null, p_242379_0_.getDimensions().makeBoundingBox(vector3d)).allMatch(VoxelShape::isEmpty) ? vector3d : null;
         }
      }
   }
}
