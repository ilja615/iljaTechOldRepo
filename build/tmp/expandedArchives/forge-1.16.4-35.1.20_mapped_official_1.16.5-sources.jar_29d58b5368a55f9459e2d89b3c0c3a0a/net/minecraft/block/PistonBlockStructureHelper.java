package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonBlockStructureHelper {
   private final World level;
   private final BlockPos pistonPos;
   private final boolean extending;
   private final BlockPos startPos;
   private final Direction pushDirection;
   private final List<BlockPos> toPush = Lists.newArrayList();
   private final List<BlockPos> toDestroy = Lists.newArrayList();
   private final Direction pistonDirection;

   public PistonBlockStructureHelper(World p_i45664_1_, BlockPos p_i45664_2_, Direction p_i45664_3_, boolean p_i45664_4_) {
      this.level = p_i45664_1_;
      this.pistonPos = p_i45664_2_;
      this.pistonDirection = p_i45664_3_;
      this.extending = p_i45664_4_;
      if (p_i45664_4_) {
         this.pushDirection = p_i45664_3_;
         this.startPos = p_i45664_2_.relative(p_i45664_3_);
      } else {
         this.pushDirection = p_i45664_3_.getOpposite();
         this.startPos = p_i45664_2_.relative(p_i45664_3_, 2);
      }

   }

   public boolean resolve() {
      this.toPush.clear();
      this.toDestroy.clear();
      BlockState blockstate = this.level.getBlockState(this.startPos);
      if (!PistonBlock.isPushable(blockstate, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
         if (this.extending && blockstate.getPistonPushReaction() == PushReaction.DESTROY) {
            this.toDestroy.add(this.startPos);
            return true;
         } else {
            return false;
         }
      } else if (!this.addBlockLine(this.startPos, this.pushDirection)) {
         return false;
      } else {
         for(int i = 0; i < this.toPush.size(); ++i) {
            BlockPos blockpos = this.toPush.get(i);
            if (this.level.getBlockState(blockpos).isStickyBlock() && !this.addBranchingBlocks(blockpos)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean addBlockLine(BlockPos p_177251_1_, Direction p_177251_2_) {
      BlockState blockstate = this.level.getBlockState(p_177251_1_);
      if (level.isEmptyBlock(p_177251_1_)) {
         return true;
      } else if (!PistonBlock.isPushable(blockstate, this.level, p_177251_1_, this.pushDirection, false, p_177251_2_)) {
         return true;
      } else if (p_177251_1_.equals(this.pistonPos)) {
         return true;
      } else if (this.toPush.contains(p_177251_1_)) {
         return true;
      } else {
         int i = 1;
         if (i + this.toPush.size() > 12) {
            return false;
         } else {
            BlockState oldState;
            while(blockstate.isStickyBlock()) {
               BlockPos blockpos = p_177251_1_.relative(this.pushDirection.getOpposite(), i);
               oldState = blockstate;
               blockstate = this.level.getBlockState(blockpos);
               if (blockstate.isAir(this.level, blockpos) || !oldState.canStickTo(blockstate) || !PistonBlock.isPushable(blockstate, this.level, blockpos, this.pushDirection, false, this.pushDirection.getOpposite()) || blockpos.equals(this.pistonPos)) {
                  break;
               }

               ++i;
               if (i + this.toPush.size() > 12) {
                  return false;
               }
            }

            int l = 0;

            for(int i1 = i - 1; i1 >= 0; --i1) {
               this.toPush.add(p_177251_1_.relative(this.pushDirection.getOpposite(), i1));
               ++l;
            }

            int j1 = 1;

            while(true) {
               BlockPos blockpos1 = p_177251_1_.relative(this.pushDirection, j1);
               int j = this.toPush.indexOf(blockpos1);
               if (j > -1) {
                  this.reorderListAtCollision(l, j);

                  for(int k = 0; k <= j + l; ++k) {
                     BlockPos blockpos2 = this.toPush.get(k);
                     if (this.level.getBlockState(blockpos2).isStickyBlock() && !this.addBranchingBlocks(blockpos2)) {
                        return false;
                     }
                  }

                  return true;
               }

               blockstate = this.level.getBlockState(blockpos1);
               if (blockstate.isAir(level, blockpos1)) {
                  return true;
               }

               if (!PistonBlock.isPushable(blockstate, this.level, blockpos1, this.pushDirection, true, this.pushDirection) || blockpos1.equals(this.pistonPos)) {
                  return false;
               }

               if (blockstate.getPistonPushReaction() == PushReaction.DESTROY) {
                  this.toDestroy.add(blockpos1);
                  return true;
               }

               if (this.toPush.size() >= 12) {
                  return false;
               }

               this.toPush.add(blockpos1);
               ++l;
               ++j1;
            }
         }
      }
   }

   private void reorderListAtCollision(int p_177255_1_, int p_177255_2_) {
      List<BlockPos> list = Lists.newArrayList();
      List<BlockPos> list1 = Lists.newArrayList();
      List<BlockPos> list2 = Lists.newArrayList();
      list.addAll(this.toPush.subList(0, p_177255_2_));
      list1.addAll(this.toPush.subList(this.toPush.size() - p_177255_1_, this.toPush.size()));
      list2.addAll(this.toPush.subList(p_177255_2_, this.toPush.size() - p_177255_1_));
      this.toPush.clear();
      this.toPush.addAll(list);
      this.toPush.addAll(list1);
      this.toPush.addAll(list2);
   }

   private boolean addBranchingBlocks(BlockPos p_177250_1_) {
      BlockState blockstate = this.level.getBlockState(p_177250_1_);

      for(Direction direction : Direction.values()) {
         if (direction.getAxis() != this.pushDirection.getAxis()) {
            BlockPos blockpos = p_177250_1_.relative(direction);
            BlockState blockstate1 = this.level.getBlockState(blockpos);
            if (blockstate1.canStickTo(blockstate) && !this.addBlockLine(blockpos, direction)) {
               return false;
            }
         }
      }

      return true;
   }

   public List<BlockPos> getToPush() {
      return this.toPush;
   }

   public List<BlockPos> getToDestroy() {
      return this.toDestroy;
   }
}
