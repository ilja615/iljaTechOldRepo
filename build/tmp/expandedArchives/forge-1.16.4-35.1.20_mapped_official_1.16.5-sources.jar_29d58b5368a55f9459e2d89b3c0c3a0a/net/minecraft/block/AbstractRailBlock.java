package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block implements net.minecraftforge.common.extensions.IAbstractRailBlock {
   protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean isStraight;

   public static boolean isRail(World p_208488_0_, BlockPos p_208488_1_) {
      return isRail(p_208488_0_.getBlockState(p_208488_1_));
   }

   public static boolean isRail(BlockState p_208487_0_) {
      return p_208487_0_.is(BlockTags.RAILS) && p_208487_0_.getBlock() instanceof AbstractRailBlock;
   }

   protected AbstractRailBlock(boolean p_i48444_1_, AbstractBlock.Properties p_i48444_2_) {
      super(p_i48444_2_);
      this.isStraight = p_i48444_1_;
   }

   public boolean isStraight() {
      return this.isStraight;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      RailShape railshape = p_220053_1_.is(this) ? p_220053_1_.getValue(this.getShapeProperty()) : null;
      RailShape railShape2 = p_220053_1_.is(this) ? getRailDirection(p_220053_1_, p_220053_2_, p_220053_3_, null) : null;
      return railshape != null && railshape.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return canSupportRigidBlock(p_196260_2_, p_196260_3_.below());
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock())) {
         this.updateState(p_220082_1_, p_220082_2_, p_220082_3_, p_220082_5_);
      }
   }

   protected BlockState updateState(BlockState p_235327_1_, World p_235327_2_, BlockPos p_235327_3_, boolean p_235327_4_) {
      p_235327_1_ = this.updateDir(p_235327_2_, p_235327_3_, p_235327_1_, true);
      if (this.isStraight) {
         p_235327_1_.neighborChanged(p_235327_2_, p_235327_3_, this, p_235327_3_, p_235327_4_);
      }

      return p_235327_1_;
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide && p_220069_2_.getBlockState(p_220069_3_).is(this)) {
         RailShape railshape = getRailDirection(p_220069_1_, p_220069_2_, p_220069_3_, null);
         if (shouldBeRemoved(p_220069_3_, p_220069_2_, railshape)) {
            dropResources(p_220069_1_, p_220069_2_, p_220069_3_);
            p_220069_2_.removeBlock(p_220069_3_, p_220069_6_);
         } else {
            this.updateState(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_);
         }

      }
   }

   private static boolean shouldBeRemoved(BlockPos p_235328_0_, World p_235328_1_, RailShape p_235328_2_) {
      if (!canSupportRigidBlock(p_235328_1_, p_235328_0_.below())) {
         return true;
      } else {
         switch(p_235328_2_) {
         case ASCENDING_EAST:
            return !canSupportRigidBlock(p_235328_1_, p_235328_0_.east());
         case ASCENDING_WEST:
            return !canSupportRigidBlock(p_235328_1_, p_235328_0_.west());
         case ASCENDING_NORTH:
            return !canSupportRigidBlock(p_235328_1_, p_235328_0_.north());
         case ASCENDING_SOUTH:
            return !canSupportRigidBlock(p_235328_1_, p_235328_0_.south());
         default:
            return false;
         }
      }
   }

   protected void updateState(BlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_) {
   }

   protected BlockState updateDir(World p_208489_1_, BlockPos p_208489_2_, BlockState p_208489_3_, boolean p_208489_4_) {
      if (p_208489_1_.isClientSide) {
         return p_208489_3_;
      } else {
         RailShape railshape = p_208489_3_.getValue(this.getShapeProperty());
         return (new RailState(p_208489_1_, p_208489_2_, p_208489_3_)).place(p_208489_1_.hasNeighborSignal(p_208489_2_), p_208489_4_, railshape).getState();
      }
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.NORMAL;
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_) {
         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (getRailDirection(p_196243_1_, p_196243_2_, p_196243_3_, null).isAscending()) {
            p_196243_2_.updateNeighborsAt(p_196243_3_.above(), this);
         }

         if (this.isStraight) {
            p_196243_2_.updateNeighborsAt(p_196243_3_, this);
            p_196243_2_.updateNeighborsAt(p_196243_3_.below(), this);
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = super.defaultBlockState();
      Direction direction = p_196258_1_.getHorizontalDirection();
      boolean flag = direction == Direction.EAST || direction == Direction.WEST;
      return blockstate.setValue(this.getShapeProperty(), flag ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
   }

   @Deprecated //Forge: Use getRailDirection(IBlockAccess, BlockPos, IBlockState, EntityMinecart) for enhanced ability
   public abstract Property<RailShape> getShapeProperty();

   /* ======================================== FORGE START =====================================*/

   @Override
   public boolean isFlexibleRail(BlockState state, IBlockReader world, BlockPos pos)
   {
      return  !this.isStraight;
   }

   @Override
   public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @javax.annotation.Nullable net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) {
      return state.getValue(getShapeProperty());
   }
   /* ========================================= FORGE END ======================================*/
}
