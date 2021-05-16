package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class FlyingNodeProcessor extends WalkNodeProcessor {
   public void prepare(Region p_225578_1_, MobEntity p_225578_2_) {
      super.prepare(p_225578_1_, p_225578_2_);
      this.oldWaterCost = p_225578_2_.getPathfindingMalus(PathNodeType.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
      super.done();
   }

   public PathPoint getStart() {
      int i;
      if (this.canFloat() && this.mob.isInWater()) {
         i = MathHelper.floor(this.mob.getY());
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.mob.getX(), (double)i, this.mob.getZ());

         for(Block block = this.level.getBlockState(blockpos$mutable).getBlock(); block == Blocks.WATER; block = this.level.getBlockState(blockpos$mutable).getBlock()) {
            ++i;
            blockpos$mutable.set(this.mob.getX(), (double)i, this.mob.getZ());
         }
      } else {
         i = MathHelper.floor(this.mob.getY() + 0.5D);
      }

      BlockPos blockpos1 = this.mob.blockPosition();
      PathNodeType pathnodetype1 = this.getBlockPathType(this.mob, blockpos1.getX(), i, blockpos1.getZ());
      if (this.mob.getPathfindingMalus(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.mob.getBoundingBox().minX, (double)i, this.mob.getBoundingBox().minZ));
         set.add(new BlockPos(this.mob.getBoundingBox().minX, (double)i, this.mob.getBoundingBox().maxZ));
         set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)i, this.mob.getBoundingBox().minZ));
         set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)i, this.mob.getBoundingBox().maxZ));

         for(BlockPos blockpos : set) {
            PathNodeType pathnodetype = this.getBlockPathType(this.mob, blockpos);
            if (this.mob.getPathfindingMalus(pathnodetype) >= 0.0F) {
               return super.getNode(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
         }
      }

      return super.getNode(blockpos1.getX(), i, blockpos1.getZ());
   }

   public FlaggedPathPoint getGoal(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(super.getNode(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_), MathHelper.floor(p_224768_5_)));
   }

   public int getNeighbors(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int i = 0;
      PathPoint pathpoint = this.getNode(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint)) {
         p_222859_1_[i++] = pathpoint;
      }

      PathPoint pathpoint1 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z);
      if (this.isOpen(pathpoint1)) {
         p_222859_1_[i++] = pathpoint1;
      }

      PathPoint pathpoint2 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z);
      if (this.isOpen(pathpoint2)) {
         p_222859_1_[i++] = pathpoint2;
      }

      PathPoint pathpoint3 = this.getNode(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint3)) {
         p_222859_1_[i++] = pathpoint3;
      }

      PathPoint pathpoint4 = this.getNode(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.isOpen(pathpoint4)) {
         p_222859_1_[i++] = pathpoint4;
      }

      PathPoint pathpoint5 = this.getNode(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.isOpen(pathpoint5)) {
         p_222859_1_[i++] = pathpoint5;
      }

      PathPoint pathpoint6 = this.getNode(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint6) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint4)) {
         p_222859_1_[i++] = pathpoint6;
      }

      PathPoint pathpoint7 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.isOpen(pathpoint7) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint4)) {
         p_222859_1_[i++] = pathpoint7;
      }

      PathPoint pathpoint8 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.isOpen(pathpoint8) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint4)) {
         p_222859_1_[i++] = pathpoint8;
      }

      PathPoint pathpoint9 = this.getNode(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint9) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint4)) {
         p_222859_1_[i++] = pathpoint9;
      }

      PathPoint pathpoint10 = this.getNode(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint10) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint5)) {
         p_222859_1_[i++] = pathpoint10;
      }

      PathPoint pathpoint11 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.isOpen(pathpoint11) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint5)) {
         p_222859_1_[i++] = pathpoint11;
      }

      PathPoint pathpoint12 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.isOpen(pathpoint12) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint5)) {
         p_222859_1_[i++] = pathpoint12;
      }

      PathPoint pathpoint13 = this.getNode(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint13) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint5)) {
         p_222859_1_[i++] = pathpoint13;
      }

      PathPoint pathpoint14 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint14) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint2)) {
         p_222859_1_[i++] = pathpoint14;
      }

      PathPoint pathpoint15 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint15) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint2)) {
         p_222859_1_[i++] = pathpoint15;
      }

      PathPoint pathpoint16 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint16) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint1)) {
         p_222859_1_[i++] = pathpoint16;
      }

      PathPoint pathpoint17 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint17) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint1)) {
         p_222859_1_[i++] = pathpoint17;
      }

      PathPoint pathpoint18 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint18) && this.hasMalus(pathpoint14) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint9) && this.hasMalus(pathpoint8)) {
         p_222859_1_[i++] = pathpoint18;
      }

      PathPoint pathpoint19 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint19) && this.hasMalus(pathpoint15) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint6) && this.hasMalus(pathpoint8)) {
         p_222859_1_[i++] = pathpoint19;
      }

      PathPoint pathpoint20 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint20) && this.hasMalus(pathpoint16) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint1) & this.hasMalus(pathpoint4) && this.hasMalus(pathpoint9) && this.hasMalus(pathpoint7)) {
         p_222859_1_[i++] = pathpoint20;
      }

      PathPoint pathpoint21 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint21) && this.hasMalus(pathpoint17) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint1) & this.hasMalus(pathpoint4) && this.hasMalus(pathpoint6) && this.hasMalus(pathpoint7)) {
         p_222859_1_[i++] = pathpoint21;
      }

      PathPoint pathpoint22 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint22) && this.hasMalus(pathpoint14) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint13) && this.hasMalus(pathpoint12)) {
         p_222859_1_[i++] = pathpoint22;
      }

      PathPoint pathpoint23 = this.getNode(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint23) && this.hasMalus(pathpoint15) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint10) && this.hasMalus(pathpoint12)) {
         p_222859_1_[i++] = pathpoint23;
      }

      PathPoint pathpoint24 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.isOpen(pathpoint24) && this.hasMalus(pathpoint16) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint13) && this.hasMalus(pathpoint11)) {
         p_222859_1_[i++] = pathpoint24;
      }

      PathPoint pathpoint25 = this.getNode(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.isOpen(pathpoint25) && this.hasMalus(pathpoint17) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint10) && this.hasMalus(pathpoint11)) {
         p_222859_1_[i++] = pathpoint25;
      }

      return i;
   }

   private boolean hasMalus(@Nullable PathPoint p_227476_1_) {
      return p_227476_1_ != null && p_227476_1_.costMalus >= 0.0F;
   }

   private boolean isOpen(@Nullable PathPoint p_227477_1_) {
      return p_227477_1_ != null && !p_227477_1_.closed;
   }

   @Nullable
   protected PathPoint getNode(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      PathPoint pathpoint = null;
      PathNodeType pathnodetype = this.getBlockPathType(this.mob, p_176159_1_, p_176159_2_, p_176159_3_);
      float f = this.mob.getPathfindingMalus(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.getNode(p_176159_1_, p_176159_2_, p_176159_3_);
         pathpoint.type = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (pathnodetype == PathNodeType.WALKABLE) {
            ++pathpoint.costMalus;
         }
      }

      return pathnodetype != PathNodeType.OPEN && pathnodetype != PathNodeType.WALKABLE ? pathpoint : pathpoint;
   }

   public PathNodeType getBlockPathType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, MobEntity p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
      BlockPos blockpos = p_186319_5_.blockPosition();
      pathnodetype = this.getBlockPathTypes(p_186319_1_, p_186319_2_, p_186319_3_, p_186319_4_, p_186319_6_, p_186319_7_, p_186319_8_, p_186319_9_, p_186319_10_, enumset, pathnodetype, blockpos);
      if (enumset.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType pathnodetype1 = PathNodeType.BLOCKED;

         for(PathNodeType pathnodetype2 : enumset) {
            if (p_186319_5_.getPathfindingMalus(pathnodetype2) < 0.0F) {
               return pathnodetype2;
            }

            if (p_186319_5_.getPathfindingMalus(pathnodetype2) >= p_186319_5_.getPathfindingMalus(pathnodetype1)) {
               pathnodetype1 = pathnodetype2;
            }
         }

         return pathnodetype == PathNodeType.OPEN && p_186319_5_.getPathfindingMalus(pathnodetype1) == 0.0F ? PathNodeType.OPEN : pathnodetype1;
      }
   }

   public PathNodeType getBlockPathType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      PathNodeType pathnodetype = getBlockPathTypeRaw(p_186330_1_, blockpos$mutable.set(p_186330_2_, p_186330_3_, p_186330_4_));
      if (pathnodetype == PathNodeType.OPEN && p_186330_3_ >= 1) {
         BlockState blockstate = p_186330_1_.getBlockState(blockpos$mutable.set(p_186330_2_, p_186330_3_ - 1, p_186330_4_));
         PathNodeType pathnodetype1 = getBlockPathTypeRaw(p_186330_1_, blockpos$mutable.set(p_186330_2_, p_186330_3_ - 1, p_186330_4_));
         if (pathnodetype1 != PathNodeType.DAMAGE_FIRE && !blockstate.is(Blocks.MAGMA_BLOCK) && pathnodetype1 != PathNodeType.LAVA && !blockstate.is(BlockTags.CAMPFIRES)) {
            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
               pathnodetype = PathNodeType.DAMAGE_CACTUS;
            } else if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
               pathnodetype = PathNodeType.DAMAGE_OTHER;
            } else if (pathnodetype1 == PathNodeType.COCOA) {
               pathnodetype = PathNodeType.COCOA;
            } else if (pathnodetype1 == PathNodeType.FENCE) {
               pathnodetype = PathNodeType.FENCE;
            } else {
               pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            }
         } else {
            pathnodetype = PathNodeType.DAMAGE_FIRE;
         }
      }

      if (pathnodetype == PathNodeType.WALKABLE || pathnodetype == PathNodeType.OPEN) {
         pathnodetype = checkNeighbourBlocks(p_186330_1_, blockpos$mutable.set(p_186330_2_, p_186330_3_, p_186330_4_), pathnodetype);
      }

      return pathnodetype;
   }

   private PathNodeType getBlockPathType(MobEntity p_192559_1_, BlockPos p_192559_2_) {
      return this.getBlockPathType(p_192559_1_, p_192559_2_.getX(), p_192559_2_.getY(), p_192559_2_.getZ());
   }

   private PathNodeType getBlockPathType(MobEntity p_192558_1_, int p_192558_2_, int p_192558_3_, int p_192558_4_) {
      return this.getBlockPathType(this.level, p_192558_2_, p_192558_3_, p_192558_4_, p_192558_1_, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
   }
}
