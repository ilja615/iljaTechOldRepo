package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailState {
   private final World level;
   private final BlockPos pos;
   private final AbstractRailBlock block;
   private BlockState state;
   private final boolean isStraight;
   private final List<BlockPos> connections = Lists.newArrayList();
   private final boolean canMakeSlopes;

   public RailState(World p_i47755_1_, BlockPos p_i47755_2_, BlockState p_i47755_3_) {
      this.level = p_i47755_1_;
      this.pos = p_i47755_2_;
      this.state = p_i47755_3_;
      this.block = (AbstractRailBlock)p_i47755_3_.getBlock();
      RailShape railshape = this.block.getRailDirection(state, p_i47755_1_, p_i47755_2_, null);
      this.isStraight = !this.block.isFlexibleRail(state, p_i47755_1_, p_i47755_2_);
      this.canMakeSlopes = this.block.canMakeSlopes(state, p_i47755_1_, p_i47755_2_);
      this.updateConnections(railshape);
   }

   public List<BlockPos> getConnections() {
      return this.connections;
   }

   private void updateConnections(RailShape p_208509_1_) {
      this.connections.clear();
      switch(p_208509_1_) {
      case NORTH_SOUTH:
         this.connections.add(this.pos.north());
         this.connections.add(this.pos.south());
         break;
      case EAST_WEST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.east());
         break;
      case ASCENDING_EAST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.east().above());
         break;
      case ASCENDING_WEST:
         this.connections.add(this.pos.west().above());
         this.connections.add(this.pos.east());
         break;
      case ASCENDING_NORTH:
         this.connections.add(this.pos.north().above());
         this.connections.add(this.pos.south());
         break;
      case ASCENDING_SOUTH:
         this.connections.add(this.pos.north());
         this.connections.add(this.pos.south().above());
         break;
      case SOUTH_EAST:
         this.connections.add(this.pos.east());
         this.connections.add(this.pos.south());
         break;
      case SOUTH_WEST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.south());
         break;
      case NORTH_WEST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.north());
         break;
      case NORTH_EAST:
         this.connections.add(this.pos.east());
         this.connections.add(this.pos.north());
      }

   }

   private void removeSoftConnections() {
      for(int i = 0; i < this.connections.size(); ++i) {
         RailState railstate = this.getRail(this.connections.get(i));
         if (railstate != null && railstate.connectsTo(this)) {
            this.connections.set(i, railstate.pos);
         } else {
            this.connections.remove(i--);
         }
      }

   }

   private boolean hasRail(BlockPos p_196902_1_) {
      return AbstractRailBlock.isRail(this.level, p_196902_1_) || AbstractRailBlock.isRail(this.level, p_196902_1_.above()) || AbstractRailBlock.isRail(this.level, p_196902_1_.below());
   }

   @Nullable
   private RailState getRail(BlockPos p_196908_1_) {
      BlockState blockstate = this.level.getBlockState(p_196908_1_);
      if (AbstractRailBlock.isRail(blockstate)) {
         return new RailState(this.level, p_196908_1_, blockstate);
      } else {
         BlockPos lvt_2_1_ = p_196908_1_.above();
         blockstate = this.level.getBlockState(lvt_2_1_);
         if (AbstractRailBlock.isRail(blockstate)) {
            return new RailState(this.level, lvt_2_1_, blockstate);
         } else {
            lvt_2_1_ = p_196908_1_.below();
            blockstate = this.level.getBlockState(lvt_2_1_);
            return AbstractRailBlock.isRail(blockstate) ? new RailState(this.level, lvt_2_1_, blockstate) : null;
         }
      }
   }

   private boolean connectsTo(RailState p_196919_1_) {
      return this.hasConnection(p_196919_1_.pos);
   }

   private boolean hasConnection(BlockPos p_196904_1_) {
      for(int i = 0; i < this.connections.size(); ++i) {
         BlockPos blockpos = this.connections.get(i);
         if (blockpos.getX() == p_196904_1_.getX() && blockpos.getZ() == p_196904_1_.getZ()) {
            return true;
         }
      }

      return false;
   }

   protected int countPotentialConnections() {
      int i = 0;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (this.hasRail(this.pos.relative(direction))) {
            ++i;
         }
      }

      return i;
   }

   private boolean canConnectTo(RailState p_196905_1_) {
      return this.connectsTo(p_196905_1_) || this.connections.size() != 2;
   }

   private void connectTo(RailState p_208510_1_) {
      this.connections.add(p_208510_1_.pos);
      BlockPos blockpos = this.pos.north();
      BlockPos blockpos1 = this.pos.south();
      BlockPos blockpos2 = this.pos.west();
      BlockPos blockpos3 = this.pos.east();
      boolean flag = this.hasConnection(blockpos);
      boolean flag1 = this.hasConnection(blockpos1);
      boolean flag2 = this.hasConnection(blockpos2);
      boolean flag3 = this.hasConnection(blockpos3);
      RailShape railshape = null;
      if (flag || flag1) {
         railshape = RailShape.NORTH_SOUTH;
      }

      if (flag2 || flag3) {
         railshape = RailShape.EAST_WEST;
      }

      if (!this.isStraight) {
         if (flag1 && flag3 && !flag && !flag2) {
            railshape = RailShape.SOUTH_EAST;
         }

         if (flag1 && flag2 && !flag && !flag3) {
            railshape = RailShape.SOUTH_WEST;
         }

         if (flag && flag2 && !flag1 && !flag3) {
            railshape = RailShape.NORTH_WEST;
         }

         if (flag && flag3 && !flag1 && !flag2) {
            railshape = RailShape.NORTH_EAST;
         }
      }

      if (railshape == RailShape.NORTH_SOUTH && canMakeSlopes) {
         if (AbstractRailBlock.isRail(this.level, blockpos.above())) {
            railshape = RailShape.ASCENDING_NORTH;
         }

         if (AbstractRailBlock.isRail(this.level, blockpos1.above())) {
            railshape = RailShape.ASCENDING_SOUTH;
         }
      }

      if (railshape == RailShape.EAST_WEST && canMakeSlopes) {
         if (AbstractRailBlock.isRail(this.level, blockpos3.above())) {
            railshape = RailShape.ASCENDING_EAST;
         }

         if (AbstractRailBlock.isRail(this.level, blockpos2.above())) {
            railshape = RailShape.ASCENDING_WEST;
         }
      }

      if (railshape == null) {
         railshape = RailShape.NORTH_SOUTH;
      }

      this.state = this.state.setValue(this.block.getShapeProperty(), railshape);
      this.level.setBlock(this.pos, this.state, 3);
   }

   private boolean hasNeighborRail(BlockPos p_208512_1_) {
      RailState railstate = this.getRail(p_208512_1_);
      if (railstate == null) {
         return false;
      } else {
         railstate.removeSoftConnections();
         return railstate.canConnectTo(this);
      }
   }

   public RailState place(boolean p_226941_1_, boolean p_226941_2_, RailShape p_226941_3_) {
      BlockPos blockpos = this.pos.north();
      BlockPos blockpos1 = this.pos.south();
      BlockPos blockpos2 = this.pos.west();
      BlockPos blockpos3 = this.pos.east();
      boolean flag = this.hasNeighborRail(blockpos);
      boolean flag1 = this.hasNeighborRail(blockpos1);
      boolean flag2 = this.hasNeighborRail(blockpos2);
      boolean flag3 = this.hasNeighborRail(blockpos3);
      RailShape railshape = null;
      boolean flag4 = flag || flag1;
      boolean flag5 = flag2 || flag3;
      if (flag4 && !flag5) {
         railshape = RailShape.NORTH_SOUTH;
      }

      if (flag5 && !flag4) {
         railshape = RailShape.EAST_WEST;
      }

      boolean flag6 = flag1 && flag3;
      boolean flag7 = flag1 && flag2;
      boolean flag8 = flag && flag3;
      boolean flag9 = flag && flag2;
      if (!this.isStraight) {
         if (flag6 && !flag && !flag2) {
            railshape = RailShape.SOUTH_EAST;
         }

         if (flag7 && !flag && !flag3) {
            railshape = RailShape.SOUTH_WEST;
         }

         if (flag9 && !flag1 && !flag3) {
            railshape = RailShape.NORTH_WEST;
         }

         if (flag8 && !flag1 && !flag2) {
            railshape = RailShape.NORTH_EAST;
         }
      }

      if (railshape == null) {
         if (flag4 && flag5) {
            railshape = p_226941_3_;
         } else if (flag4) {
            railshape = RailShape.NORTH_SOUTH;
         } else if (flag5) {
            railshape = RailShape.EAST_WEST;
         }

         if (!this.isStraight) {
            if (p_226941_1_) {
               if (flag6) {
                  railshape = RailShape.SOUTH_EAST;
               }

               if (flag7) {
                  railshape = RailShape.SOUTH_WEST;
               }

               if (flag8) {
                  railshape = RailShape.NORTH_EAST;
               }

               if (flag9) {
                  railshape = RailShape.NORTH_WEST;
               }
            } else {
               if (flag9) {
                  railshape = RailShape.NORTH_WEST;
               }

               if (flag8) {
                  railshape = RailShape.NORTH_EAST;
               }

               if (flag7) {
                  railshape = RailShape.SOUTH_WEST;
               }

               if (flag6) {
                  railshape = RailShape.SOUTH_EAST;
               }
            }
         }
      }

      if (railshape == RailShape.NORTH_SOUTH && canMakeSlopes) {
         if (AbstractRailBlock.isRail(this.level, blockpos.above())) {
            railshape = RailShape.ASCENDING_NORTH;
         }

         if (AbstractRailBlock.isRail(this.level, blockpos1.above())) {
            railshape = RailShape.ASCENDING_SOUTH;
         }
      }

      if (railshape == RailShape.EAST_WEST && canMakeSlopes) {
         if (AbstractRailBlock.isRail(this.level, blockpos3.above())) {
            railshape = RailShape.ASCENDING_EAST;
         }

         if (AbstractRailBlock.isRail(this.level, blockpos2.above())) {
            railshape = RailShape.ASCENDING_WEST;
         }
      }

      if (railshape == null) {
         railshape = p_226941_3_;
      }

      this.updateConnections(railshape);
      this.state = this.state.setValue(this.block.getShapeProperty(), railshape);
      if (p_226941_2_ || this.level.getBlockState(this.pos) != this.state) {
         this.level.setBlock(this.pos, this.state, 3);

         for(int i = 0; i < this.connections.size(); ++i) {
            RailState railstate = this.getRail(this.connections.get(i));
            if (railstate != null) {
               railstate.removeSoftConnections();
               if (railstate.canConnectTo(this)) {
                  railstate.connectTo(this);
               }
            }
         }
      }

      return this;
   }

   public BlockState getState() {
      return this.state;
   }
}
