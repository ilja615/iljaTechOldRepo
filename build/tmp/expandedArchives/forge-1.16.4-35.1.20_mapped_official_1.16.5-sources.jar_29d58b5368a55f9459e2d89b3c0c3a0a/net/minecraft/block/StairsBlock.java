package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StairsBlock extends Block implements IWaterLoggable {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape TOP_AABB = SlabBlock.TOP_AABB;
   protected static final VoxelShape BOTTOM_AABB = SlabBlock.BOTTOM_AABB;
   protected static final VoxelShape OCTET_NNN = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
   protected static final VoxelShape OCTET_NNP = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
   protected static final VoxelShape OCTET_NPN = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
   protected static final VoxelShape OCTET_NPP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
   protected static final VoxelShape OCTET_PNN = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
   protected static final VoxelShape OCTET_PNP = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape OCTET_PPN = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
   protected static final VoxelShape OCTET_PPP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape[] TOP_SHAPES = makeShapes(TOP_AABB, OCTET_NNN, OCTET_PNN, OCTET_NNP, OCTET_PNP);
   protected static final VoxelShape[] BOTTOM_SHAPES = makeShapes(BOTTOM_AABB, OCTET_NPN, OCTET_PPN, OCTET_NPP, OCTET_PPP);
   private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   private final Block base;
   private final BlockState baseState;

   private static VoxelShape[] makeShapes(VoxelShape p_199779_0_, VoxelShape p_199779_1_, VoxelShape p_199779_2_, VoxelShape p_199779_3_, VoxelShape p_199779_4_) {
      return IntStream.range(0, 16).mapToObj((p_199780_5_) -> {
         return makeStairShape(p_199780_5_, p_199779_0_, p_199779_1_, p_199779_2_, p_199779_3_, p_199779_4_);
      }).toArray((p_199778_0_) -> {
         return new VoxelShape[p_199778_0_];
      });
   }

   private static VoxelShape makeStairShape(int p_199781_0_, VoxelShape p_199781_1_, VoxelShape p_199781_2_, VoxelShape p_199781_3_, VoxelShape p_199781_4_, VoxelShape p_199781_5_) {
      VoxelShape voxelshape = p_199781_1_;
      if ((p_199781_0_ & 1) != 0) {
         voxelshape = VoxelShapes.or(p_199781_1_, p_199781_2_);
      }

      if ((p_199781_0_ & 2) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, p_199781_3_);
      }

      if ((p_199781_0_ & 4) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, p_199781_4_);
      }

      if ((p_199781_0_ & 8) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, p_199781_5_);
      }

      return voxelshape;
   }

   @Deprecated // Forge: Use the other constructor that takes a Supplier
   public StairsBlock(BlockState p_i48321_1_, AbstractBlock.Properties p_i48321_2_) {
      super(p_i48321_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.base = p_i48321_1_.getBlock();
      this.baseState = p_i48321_1_;
      this.stateSupplier = () -> p_i48321_1_;
   }

   public StairsBlock(java.util.function.Supplier<BlockState> state, AbstractBlock.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.base = Blocks.AIR; // These are unused, fields are redirected
      this.baseState = Blocks.AIR.defaultBlockState();
      this.stateSupplier = state;
   }

   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (p_220053_1_.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(p_220053_1_)]];
   }

   private int getShapeIndex(BlockState p_196511_1_) {
      return p_196511_1_.getValue(SHAPE).ordinal() * 4 + p_196511_1_.getValue(FACING).get2DDataValue();
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      this.base.animateTick(p_180655_1_, p_180655_2_, p_180655_3_, p_180655_4_);
   }

   public void attack(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
      this.baseState.attack(p_196270_2_, p_196270_3_, p_196270_4_);
   }

   public void destroy(IWorld p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
      this.base.destroy(p_176206_1_, p_176206_2_, p_176206_3_);
   }

   public float getExplosionResistance() {
      return this.base.getExplosionResistance();
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_1_.is(p_220082_1_.getBlock())) {
         this.baseState.neighborChanged(p_220082_2_, p_220082_3_, Blocks.AIR, p_220082_3_, false);
         this.base.onPlace(this.baseState, p_220082_2_, p_220082_3_, p_220082_4_, false);
      }
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         this.baseState.onRemove(p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      this.base.stepOn(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return this.base.isRandomlyTicking(p_149653_1_);
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      this.base.randomTick(p_225542_1_, p_225542_2_, p_225542_3_, p_225542_4_);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      this.base.tick(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      return this.baseState.use(p_225533_2_, p_225533_4_, p_225533_5_, p_225533_6_);
   }

   public void wasExploded(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
      this.base.wasExploded(p_180652_1_, p_180652_2_, p_180652_3_);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction direction = p_196258_1_.getClickedFace();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      FluidState fluidstate = p_196258_1_.getLevel().getFluidState(blockpos);
      BlockState blockstate = this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(p_196258_1_.getClickLocation().y - (double)blockpos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
      return blockstate.setValue(SHAPE, getStairsShape(blockstate, p_196258_1_.getLevel(), blockpos));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getValue(WATERLOGGED)) {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
      }

      return p_196271_2_.getAxis().isHorizontal() ? p_196271_1_.setValue(SHAPE, getStairsShape(p_196271_1_, p_196271_4_, p_196271_5_)) : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   private static StairsShape getStairsShape(BlockState p_208064_0_, IBlockReader p_208064_1_, BlockPos p_208064_2_) {
      Direction direction = p_208064_0_.getValue(FACING);
      BlockState blockstate = p_208064_1_.getBlockState(p_208064_2_.relative(direction));
      if (isStairs(blockstate) && p_208064_0_.getValue(HALF) == blockstate.getValue(HALF)) {
         Direction direction1 = blockstate.getValue(FACING);
         if (direction1.getAxis() != p_208064_0_.getValue(FACING).getAxis() && canTakeShape(p_208064_0_, p_208064_1_, p_208064_2_, direction1.getOpposite())) {
            if (direction1 == direction.getCounterClockWise()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = p_208064_1_.getBlockState(p_208064_2_.relative(direction.getOpposite()));
      if (isStairs(blockstate1) && p_208064_0_.getValue(HALF) == blockstate1.getValue(HALF)) {
         Direction direction2 = blockstate1.getValue(FACING);
         if (direction2.getAxis() != p_208064_0_.getValue(FACING).getAxis() && canTakeShape(p_208064_0_, p_208064_1_, p_208064_2_, direction2)) {
            if (direction2 == direction.getCounterClockWise()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(BlockState p_185704_0_, IBlockReader p_185704_1_, BlockPos p_185704_2_, Direction p_185704_3_) {
      BlockState blockstate = p_185704_1_.getBlockState(p_185704_2_.relative(p_185704_3_));
      return !isStairs(blockstate) || blockstate.getValue(FACING) != p_185704_0_.getValue(FACING) || blockstate.getValue(HALF) != p_185704_0_.getValue(HALF);
   }

   public static boolean isStairs(BlockState p_185709_0_) {
      return p_185709_0_.getBlock() instanceof StairsBlock;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      Direction direction = p_185471_1_.getValue(FACING);
      StairsShape stairsshape = p_185471_1_.getValue(SHAPE);
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         if (direction.getAxis() == Direction.Axis.Z) {
            switch(stairsshape) {
            case INNER_LEFT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            default:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if (direction.getAxis() == Direction.Axis.X) {
            switch(stairsshape) {
            case INNER_LEFT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
               return p_185471_1_.rotate(Rotation.CLOCKWISE_180);
            }
         }
      }

      return super.mirror(p_185471_1_, p_185471_2_);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   // Forge Start
   private final java.util.function.Supplier<BlockState> stateSupplier;
   private Block getModelBlock() {
       return getModelState().getBlock();
   }
   private BlockState getModelState() {
       return stateSupplier.get();
   }
   // Forge end
}
