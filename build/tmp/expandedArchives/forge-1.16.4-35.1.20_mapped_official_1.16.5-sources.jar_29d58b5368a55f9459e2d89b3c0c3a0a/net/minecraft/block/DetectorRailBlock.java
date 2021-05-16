package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DetectorRailBlock extends AbstractRailBlock {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public DetectorRailBlock(AbstractBlock.Properties p_i48417_1_) {
      super(true, p_i48417_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)).setValue(SHAPE, RailShape.NORTH_SOUTH));
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isClientSide) {
         if (!p_196262_1_.getValue(POWERED)) {
            this.checkPressed(p_196262_2_, p_196262_3_, p_196262_1_);
         }
      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_1_.getValue(POWERED)) {
         this.checkPressed(p_225534_2_, p_225534_3_, p_225534_1_);
      }
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      if (!p_176211_1_.getValue(POWERED)) {
         return 0;
      } else {
         return p_176211_4_ == Direction.UP ? 15 : 0;
      }
   }

   private void checkPressed(World p_176570_1_, BlockPos p_176570_2_, BlockState p_176570_3_) {
      if (this.canSurvive(p_176570_3_, p_176570_1_, p_176570_2_)) {
         boolean flag = p_176570_3_.getValue(POWERED);
         boolean flag1 = false;
         List<AbstractMinecartEntity> list = this.getInteractingMinecartOfType(p_176570_1_, p_176570_2_, AbstractMinecartEntity.class, (Predicate<Entity>)null);
         if (!list.isEmpty()) {
            flag1 = true;
         }

         if (flag1 && !flag) {
            BlockState blockstate = p_176570_3_.setValue(POWERED, Boolean.valueOf(true));
            p_176570_1_.setBlock(p_176570_2_, blockstate, 3);
            this.updatePowerToConnected(p_176570_1_, p_176570_2_, blockstate, true);
            p_176570_1_.updateNeighborsAt(p_176570_2_, this);
            p_176570_1_.updateNeighborsAt(p_176570_2_.below(), this);
            p_176570_1_.setBlocksDirty(p_176570_2_, p_176570_3_, blockstate);
         }

         if (!flag1 && flag) {
            BlockState blockstate1 = p_176570_3_.setValue(POWERED, Boolean.valueOf(false));
            p_176570_1_.setBlock(p_176570_2_, blockstate1, 3);
            this.updatePowerToConnected(p_176570_1_, p_176570_2_, blockstate1, false);
            p_176570_1_.updateNeighborsAt(p_176570_2_, this);
            p_176570_1_.updateNeighborsAt(p_176570_2_.below(), this);
            p_176570_1_.setBlocksDirty(p_176570_2_, p_176570_3_, blockstate1);
         }

         if (flag1) {
            p_176570_1_.getBlockTicks().scheduleTick(p_176570_2_, this, 20);
         }

         p_176570_1_.updateNeighbourForOutputSignal(p_176570_2_, this);
      }
   }

   protected void updatePowerToConnected(World p_185592_1_, BlockPos p_185592_2_, BlockState p_185592_3_, boolean p_185592_4_) {
      RailState railstate = new RailState(p_185592_1_, p_185592_2_, p_185592_3_);

      for(BlockPos blockpos : railstate.getConnections()) {
         BlockState blockstate = p_185592_1_.getBlockState(blockpos);
         blockstate.neighborChanged(p_185592_1_, blockpos, blockstate.getBlock(), p_185592_2_, false);
      }

   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock())) {
         this.checkPressed(p_220082_2_, p_220082_3_, this.updateState(p_220082_1_, p_220082_2_, p_220082_3_, p_220082_5_));
      }
   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      if (p_180641_1_.getValue(POWERED)) {
         List<CommandBlockMinecartEntity> list = this.getInteractingMinecartOfType(p_180641_2_, p_180641_3_, CommandBlockMinecartEntity.class, (Predicate<Entity>)null);
         if (!list.isEmpty()) {
            return list.get(0).getCommandBlock().getSuccessCount();
         }

         List<AbstractMinecartEntity> list1 = this.getInteractingMinecartOfType(p_180641_2_, p_180641_3_, AbstractMinecartEntity.class, EntityPredicates.CONTAINER_ENTITY_SELECTOR);
         List<AbstractMinecartEntity> carts = this.getInteractingMinecartOfType(p_180641_2_, p_180641_3_, AbstractMinecartEntity.class, null);
         if (!carts.isEmpty() && carts.get(0).getComparatorLevel() > -1) return carts.get(0).getComparatorLevel();
         if (!list1.isEmpty()) {
            return Container.getRedstoneSignalFromContainer((IInventory)list1.get(0));
         }
      }

      return 0;
   }

   protected <T extends AbstractMinecartEntity> List<T> getInteractingMinecartOfType(World p_200878_1_, BlockPos p_200878_2_, Class<T> p_200878_3_, @Nullable Predicate<Entity> p_200878_4_) {
      return p_200878_1_.getEntitiesOfClass(p_200878_3_, this.getSearchBB(p_200878_2_), p_200878_4_);
   }

   private AxisAlignedBB getSearchBB(BlockPos p_176572_1_) {
      double d0 = 0.2D;
      return new AxisAlignedBB((double)p_176572_1_.getX() + 0.2D, (double)p_176572_1_.getY(), (double)p_176572_1_.getZ() + 0.2D, (double)(p_176572_1_.getX() + 1) - 0.2D, (double)(p_176572_1_.getY() + 1) - 0.2D, (double)(p_176572_1_.getZ() + 1) - 0.2D);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         switch((RailShape)p_185499_1_.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)p_185499_1_.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return p_185499_1_.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)p_185499_1_.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return p_185499_1_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_185499_1_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return p_185499_1_.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      RailShape railshape = p_185471_1_.getValue(SHAPE);
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return p_185471_1_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_185471_1_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_185471_1_.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_185471_1_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_185471_1_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_185471_1_.setValue(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(p_185471_1_, p_185471_2_);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return p_185471_1_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_185471_1_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return p_185471_1_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_185471_1_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_185471_1_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_185471_1_.setValue(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(p_185471_1_, p_185471_2_);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(SHAPE, POWERED);
   }
}
