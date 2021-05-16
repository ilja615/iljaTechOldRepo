package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeverBlock extends HorizontalFaceBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
   protected static final VoxelShape WEST_AABB = Block.box(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
   protected static final VoxelShape UP_AABB_Z = Block.box(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
   protected static final VoxelShape UP_AABB_X = Block.box(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
   protected static final VoxelShape DOWN_AABB_Z = Block.box(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
   protected static final VoxelShape DOWN_AABB_X = Block.box(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

   public LeverBlock(AbstractBlock.Properties p_i48369_1_) {
      super(p_i48369_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(FACE, AttachFace.WALL));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((AttachFace)p_220053_1_.getValue(FACE)) {
      case FLOOR:
         switch(p_220053_1_.getValue(FACING).getAxis()) {
         case X:
            return UP_AABB_X;
         case Z:
         default:
            return UP_AABB_Z;
         }
      case WALL:
         switch((Direction)p_220053_1_.getValue(FACING)) {
         case EAST:
            return EAST_AABB;
         case WEST:
            return WEST_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case NORTH:
         default:
            return NORTH_AABB;
         }
      case CEILING:
      default:
         switch(p_220053_1_.getValue(FACING).getAxis()) {
         case X:
            return DOWN_AABB_X;
         case Z:
         default:
            return DOWN_AABB_Z;
         }
      }
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         BlockState blockstate1 = p_225533_1_.cycle(POWERED);
         if (blockstate1.getValue(POWERED)) {
            makeParticle(blockstate1, p_225533_2_, p_225533_3_, 1.0F);
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockState blockstate = this.pull(p_225533_1_, p_225533_2_, p_225533_3_);
         float f = blockstate.getValue(POWERED) ? 0.6F : 0.5F;
         p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         return ActionResultType.CONSUME;
      }
   }

   public BlockState pull(BlockState p_226939_1_, World p_226939_2_, BlockPos p_226939_3_) {
      p_226939_1_ = p_226939_1_.cycle(POWERED);
      p_226939_2_.setBlock(p_226939_3_, p_226939_1_, 3);
      this.updateNeighbours(p_226939_1_, p_226939_2_, p_226939_3_);
      return p_226939_1_;
   }

   private static void makeParticle(BlockState p_196379_0_, IWorld p_196379_1_, BlockPos p_196379_2_, float p_196379_3_) {
      Direction direction = p_196379_0_.getValue(FACING).getOpposite();
      Direction direction1 = getConnectedDirection(p_196379_0_).getOpposite();
      double d0 = (double)p_196379_2_.getX() + 0.5D + 0.1D * (double)direction.getStepX() + 0.2D * (double)direction1.getStepX();
      double d1 = (double)p_196379_2_.getY() + 0.5D + 0.1D * (double)direction.getStepY() + 0.2D * (double)direction1.getStepY();
      double d2 = (double)p_196379_2_.getZ() + 0.5D + 0.1D * (double)direction.getStepZ() + 0.2D * (double)direction1.getStepZ();
      p_196379_1_.addParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, p_196379_3_), d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.getValue(POWERED) && p_180655_4_.nextFloat() < 0.25F) {
         makeParticle(p_180655_1_, p_180655_2_, p_180655_3_, 0.5F);
      }

   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && !p_196243_1_.is(p_196243_4_.getBlock())) {
         if (p_196243_1_.getValue(POWERED)) {
            this.updateNeighbours(p_196243_1_, p_196243_2_, p_196243_3_);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_1_.getValue(POWERED) && getConnectedDirection(p_176211_1_) == p_176211_4_ ? 15 : 0;
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   private void updateNeighbours(BlockState p_196378_1_, World p_196378_2_, BlockPos p_196378_3_) {
      p_196378_2_.updateNeighborsAt(p_196378_3_, this);
      p_196378_2_.updateNeighborsAt(p_196378_3_.relative(getConnectedDirection(p_196378_1_).getOpposite()), this);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACE, FACING, POWERED);
   }
}
