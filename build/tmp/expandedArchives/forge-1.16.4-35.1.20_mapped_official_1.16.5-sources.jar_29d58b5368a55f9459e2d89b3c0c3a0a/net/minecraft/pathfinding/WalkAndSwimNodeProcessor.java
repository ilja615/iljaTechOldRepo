package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class WalkAndSwimNodeProcessor extends WalkNodeProcessor {
   private float oldWalkableCost;
   private float oldWaterBorderCost;

   public void prepare(Region p_225578_1_, MobEntity p_225578_2_) {
      super.prepare(p_225578_1_, p_225578_2_);
      p_225578_2_.setPathfindingMalus(PathNodeType.WATER, 0.0F);
      this.oldWalkableCost = p_225578_2_.getPathfindingMalus(PathNodeType.WALKABLE);
      p_225578_2_.setPathfindingMalus(PathNodeType.WALKABLE, 6.0F);
      this.oldWaterBorderCost = p_225578_2_.getPathfindingMalus(PathNodeType.WATER_BORDER);
      p_225578_2_.setPathfindingMalus(PathNodeType.WATER_BORDER, 4.0F);
   }

   public void done() {
      this.mob.setPathfindingMalus(PathNodeType.WALKABLE, this.oldWalkableCost);
      this.mob.setPathfindingMalus(PathNodeType.WATER_BORDER, this.oldWaterBorderCost);
      super.done();
   }

   public PathPoint getStart() {
      return this.getNode(MathHelper.floor(this.mob.getBoundingBox().minX), MathHelper.floor(this.mob.getBoundingBox().minY + 0.5D), MathHelper.floor(this.mob.getBoundingBox().minZ));
   }

   public FlaggedPathPoint getGoal(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(this.getNode(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_ + 0.5D), MathHelper.floor(p_224768_5_)));
   }

   public int getNeighbors(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int i = 0;
      int j = 1;
      BlockPos blockpos = new BlockPos(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z);
      double d0 = this.inWaterDependentPosHeight(blockpos);
      PathPoint pathpoint = this.getAcceptedNode(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1, 1, d0);
      PathPoint pathpoint1 = this.getAcceptedNode(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z, 1, d0);
      PathPoint pathpoint2 = this.getAcceptedNode(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z, 1, d0);
      PathPoint pathpoint3 = this.getAcceptedNode(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1, 1, d0);
      PathPoint pathpoint4 = this.getAcceptedNode(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z, 0, d0);
      PathPoint pathpoint5 = this.getAcceptedNode(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z, 1, d0);
      if (pathpoint != null && !pathpoint.closed) {
         p_222859_1_[i++] = pathpoint;
      }

      if (pathpoint1 != null && !pathpoint1.closed) {
         p_222859_1_[i++] = pathpoint1;
      }

      if (pathpoint2 != null && !pathpoint2.closed) {
         p_222859_1_[i++] = pathpoint2;
      }

      if (pathpoint3 != null && !pathpoint3.closed) {
         p_222859_1_[i++] = pathpoint3;
      }

      if (pathpoint4 != null && !pathpoint4.closed) {
         p_222859_1_[i++] = pathpoint4;
      }

      if (pathpoint5 != null && !pathpoint5.closed) {
         p_222859_1_[i++] = pathpoint5;
      }

      boolean flag = pathpoint3 == null || pathpoint3.type == PathNodeType.OPEN || pathpoint3.costMalus != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.type == PathNodeType.OPEN || pathpoint.costMalus != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.type == PathNodeType.OPEN || pathpoint2.costMalus != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.type == PathNodeType.OPEN || pathpoint1.costMalus != 0.0F;
      if (flag && flag3) {
         PathPoint pathpoint6 = this.getAcceptedNode(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1, 1, d0);
         if (pathpoint6 != null && !pathpoint6.closed) {
            p_222859_1_[i++] = pathpoint6;
         }
      }

      if (flag && flag2) {
         PathPoint pathpoint7 = this.getAcceptedNode(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1, 1, d0);
         if (pathpoint7 != null && !pathpoint7.closed) {
            p_222859_1_[i++] = pathpoint7;
         }
      }

      if (flag1 && flag3) {
         PathPoint pathpoint8 = this.getAcceptedNode(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1, 1, d0);
         if (pathpoint8 != null && !pathpoint8.closed) {
            p_222859_1_[i++] = pathpoint8;
         }
      }

      if (flag1 && flag2) {
         PathPoint pathpoint9 = this.getAcceptedNode(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1, 1, d0);
         if (pathpoint9 != null && !pathpoint9.closed) {
            p_222859_1_[i++] = pathpoint9;
         }
      }

      return i;
   }

   private double inWaterDependentPosHeight(BlockPos p_203246_1_) {
      if (!this.mob.isInWater()) {
         BlockPos blockpos = p_203246_1_.below();
         VoxelShape voxelshape = this.level.getBlockState(blockpos).getCollisionShape(this.level, blockpos);
         return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.max(Direction.Axis.Y));
      } else {
         return (double)p_203246_1_.getY() + 0.5D;
      }
   }

   @Nullable
   private PathPoint getAcceptedNode(int p_203245_1_, int p_203245_2_, int p_203245_3_, int p_203245_4_, double p_203245_5_) {
      PathPoint pathpoint = null;
      BlockPos blockpos = new BlockPos(p_203245_1_, p_203245_2_, p_203245_3_);
      double d0 = this.inWaterDependentPosHeight(blockpos);
      if (d0 - p_203245_5_ > 1.125D) {
         return null;
      } else {
         PathNodeType pathnodetype = this.getBlockPathType(this.level, p_203245_1_, p_203245_2_, p_203245_3_, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
         float f = this.mob.getPathfindingMalus(pathnodetype);
         double d1 = (double)this.mob.getBbWidth() / 2.0D;
         if (f >= 0.0F) {
            pathpoint = this.getNode(p_203245_1_, p_203245_2_, p_203245_3_);
            pathpoint.type = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         }

         if (pathnodetype != PathNodeType.WATER && pathnodetype != PathNodeType.WALKABLE) {
            if (pathpoint == null && p_203245_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.UNPASSABLE_RAIL && pathnodetype != PathNodeType.TRAPDOOR) {
               pathpoint = this.getAcceptedNode(p_203245_1_, p_203245_2_ + 1, p_203245_3_, p_203245_4_ - 1, p_203245_5_);
            }

            if (pathnodetype == PathNodeType.OPEN) {
               AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)p_203245_1_ - d1 + 0.5D, (double)p_203245_2_ + 0.001D, (double)p_203245_3_ - d1 + 0.5D, (double)p_203245_1_ + d1 + 0.5D, (double)((float)p_203245_2_ + this.mob.getBbHeight()), (double)p_203245_3_ + d1 + 0.5D);
               if (!this.mob.level.noCollision(this.mob, axisalignedbb)) {
                  return null;
               }

               PathNodeType pathnodetype1 = this.getBlockPathType(this.level, p_203245_1_, p_203245_2_ - 1, p_203245_3_, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
               if (pathnodetype1 == PathNodeType.BLOCKED) {
                  pathpoint = this.getNode(p_203245_1_, p_203245_2_, p_203245_3_);
                  pathpoint.type = PathNodeType.WALKABLE;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                  return pathpoint;
               }

               if (pathnodetype1 == PathNodeType.WATER) {
                  pathpoint = this.getNode(p_203245_1_, p_203245_2_, p_203245_3_);
                  pathpoint.type = PathNodeType.WATER;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                  return pathpoint;
               }

               int i = 0;

               while(p_203245_2_ > 0 && pathnodetype == PathNodeType.OPEN) {
                  --p_203245_2_;
                  if (i++ >= this.mob.getMaxFallDistance()) {
                     return null;
                  }

                  pathnodetype = this.getBlockPathType(this.level, p_203245_1_, p_203245_2_, p_203245_3_, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
                  f = this.mob.getPathfindingMalus(pathnodetype);
                  if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                     pathpoint = this.getNode(p_203245_1_, p_203245_2_, p_203245_3_);
                     pathpoint.type = pathnodetype;
                     pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                     break;
                  }

                  if (f < 0.0F) {
                     return null;
                  }
               }
            }

            return pathpoint;
         } else {
            if (p_203245_2_ < this.mob.level.getSeaLevel() - 10 && pathpoint != null) {
               ++pathpoint.costMalus;
            }

            return pathpoint;
         }
      }
   }

   protected PathNodeType evaluateBlockPathType(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_) {
      if (p_215744_5_ == PathNodeType.RAIL && !(p_215744_1_.getBlockState(p_215744_4_).getBlock() instanceof AbstractRailBlock) && !(p_215744_1_.getBlockState(p_215744_4_.below()).getBlock() instanceof AbstractRailBlock)) {
         p_215744_5_ = PathNodeType.UNPASSABLE_RAIL;
      }

      if (p_215744_5_ == PathNodeType.DOOR_OPEN || p_215744_5_ == PathNodeType.DOOR_WOOD_CLOSED || p_215744_5_ == PathNodeType.DOOR_IRON_CLOSED) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      if (p_215744_5_ == PathNodeType.LEAVES) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      return p_215744_5_;
   }

   public PathNodeType getBlockPathType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      PathNodeType pathnodetype = getBlockPathTypeRaw(p_186330_1_, blockpos$mutable.set(p_186330_2_, p_186330_3_, p_186330_4_));
      if (pathnodetype == PathNodeType.WATER) {
         for(Direction direction : Direction.values()) {
            PathNodeType pathnodetype2 = getBlockPathTypeRaw(p_186330_1_, blockpos$mutable.set(p_186330_2_, p_186330_3_, p_186330_4_).move(direction));
            if (pathnodetype2 == PathNodeType.BLOCKED) {
               return PathNodeType.WATER_BORDER;
            }
         }

         return PathNodeType.WATER;
      } else {
         if (pathnodetype == PathNodeType.OPEN && p_186330_3_ >= 1) {
            BlockState blockstate = p_186330_1_.getBlockState(new BlockPos(p_186330_2_, p_186330_3_ - 1, p_186330_4_));
            PathNodeType pathnodetype1 = getBlockPathTypeRaw(p_186330_1_, blockpos$mutable.set(p_186330_2_, p_186330_3_ - 1, p_186330_4_));
            if (pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.LAVA) {
               pathnodetype = PathNodeType.WALKABLE;
            } else {
               pathnodetype = PathNodeType.OPEN;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || blockstate.is(Blocks.MAGMA_BLOCK) || blockstate.is(BlockTags.CAMPFIRES)) {
               pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
               pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
               pathnodetype = PathNodeType.DAMAGE_OTHER;
            }
         }

         if (pathnodetype == PathNodeType.WALKABLE) {
            pathnodetype = checkNeighbourBlocks(p_186330_1_, blockpos$mutable.set(p_186330_2_, p_186330_3_, p_186330_4_), pathnodetype);
         }

         return pathnodetype;
      }
   }
}
