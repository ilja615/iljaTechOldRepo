package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class DirectionalPlaceContext extends BlockItemUseContext {
   private final Direction direction;

   public DirectionalPlaceContext(World p_i50051_1_, BlockPos p_i50051_2_, Direction p_i50051_3_, ItemStack p_i50051_4_, Direction p_i50051_5_) {
      super(p_i50051_1_, (PlayerEntity)null, Hand.MAIN_HAND, p_i50051_4_, new BlockRayTraceResult(Vector3d.atBottomCenterOf(p_i50051_2_), p_i50051_5_, p_i50051_2_, false));
      this.direction = p_i50051_3_;
   }

   public BlockPos getClickedPos() {
      return this.getHitResult().getBlockPos();
   }

   public boolean canPlace() {
      return this.getLevel().getBlockState(this.getHitResult().getBlockPos()).canBeReplaced(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.canPlace();
   }

   public Direction getNearestLookingDirection() {
      return Direction.DOWN;
   }

   public Direction[] getNearestLookingDirections() {
      switch(this.direction) {
      case DOWN:
      default:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
      case UP:
         return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
      case NORTH:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
      case SOUTH:
         return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
      case WEST:
         return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
      case EAST:
         return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
      }
   }

   public Direction getHorizontalDirection() {
      return this.direction.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.direction;
   }

   public boolean isSecondaryUseActive() {
      return false;
   }

   public float getRotation() {
      return (float)(this.direction.get2DDataValue() * 90);
   }
}
