package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneWireBlock extends Block {
   public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
   public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
   public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
   public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
   private static final VoxelShape SHAPE_DOT = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
   private static final Map<Direction, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Direction.SOUTH, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST, Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST, Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
   private static final Map<Direction, VoxelShape> SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, VoxelShapes.or(SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), Direction.SOUTH, VoxelShapes.or(SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), Direction.EAST, VoxelShapes.or(SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), Direction.WEST, VoxelShapes.or(SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
   private final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();
   private static final Vector3f[] COLORS = new Vector3f[16];
   private final BlockState crossState;
   private boolean shouldSignal = true;

   public RedstoneWireBlock(AbstractBlock.Properties p_i48344_1_) {
      super(p_i48344_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, RedstoneSide.NONE).setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE).setValue(WEST, RedstoneSide.NONE).setValue(POWER, Integer.valueOf(0)));
      this.crossState = this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE).setValue(EAST, RedstoneSide.SIDE).setValue(SOUTH, RedstoneSide.SIDE).setValue(WEST, RedstoneSide.SIDE);

      for(BlockState blockstate : this.getStateDefinition().getPossibleStates()) {
         if (blockstate.getValue(POWER) == 0) {
            this.SHAPES_CACHE.put(blockstate, this.calculateShape(blockstate));
         }
      }

   }

   private VoxelShape calculateShape(BlockState p_235554_1_) {
      VoxelShape voxelshape = SHAPE_DOT;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         RedstoneSide redstoneside = p_235554_1_.getValue(PROPERTY_BY_DIRECTION.get(direction));
         if (redstoneside == RedstoneSide.SIDE) {
            voxelshape = VoxelShapes.or(voxelshape, SHAPES_FLOOR.get(direction));
         } else if (redstoneside == RedstoneSide.UP) {
            voxelshape = VoxelShapes.or(voxelshape, SHAPES_UP.get(direction));
         }
      }

      return voxelshape;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.SHAPES_CACHE.get(p_220053_1_.setValue(POWER, Integer.valueOf(0)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getConnectionState(p_196258_1_.getLevel(), this.crossState, p_196258_1_.getClickedPos());
   }

   private BlockState getConnectionState(IBlockReader p_235544_1_, BlockState p_235544_2_, BlockPos p_235544_3_) {
      boolean flag = isDot(p_235544_2_);
      p_235544_2_ = this.getMissingConnections(p_235544_1_, this.defaultBlockState().setValue(POWER, p_235544_2_.getValue(POWER)), p_235544_3_);
      if (flag && isDot(p_235544_2_)) {
         return p_235544_2_;
      } else {
         boolean flag1 = p_235544_2_.getValue(NORTH).isConnected();
         boolean flag2 = p_235544_2_.getValue(SOUTH).isConnected();
         boolean flag3 = p_235544_2_.getValue(EAST).isConnected();
         boolean flag4 = p_235544_2_.getValue(WEST).isConnected();
         boolean flag5 = !flag1 && !flag2;
         boolean flag6 = !flag3 && !flag4;
         if (!flag4 && flag5) {
            p_235544_2_ = p_235544_2_.setValue(WEST, RedstoneSide.SIDE);
         }

         if (!flag3 && flag5) {
            p_235544_2_ = p_235544_2_.setValue(EAST, RedstoneSide.SIDE);
         }

         if (!flag1 && flag6) {
            p_235544_2_ = p_235544_2_.setValue(NORTH, RedstoneSide.SIDE);
         }

         if (!flag2 && flag6) {
            p_235544_2_ = p_235544_2_.setValue(SOUTH, RedstoneSide.SIDE);
         }

         return p_235544_2_;
      }
   }

   private BlockState getMissingConnections(IBlockReader p_235551_1_, BlockState p_235551_2_, BlockPos p_235551_3_) {
      boolean flag = !p_235551_1_.getBlockState(p_235551_3_.above()).isRedstoneConductor(p_235551_1_, p_235551_3_);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (!p_235551_2_.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected()) {
            RedstoneSide redstoneside = this.getConnectingSide(p_235551_1_, p_235551_3_, direction, flag);
            p_235551_2_ = p_235551_2_.setValue(PROPERTY_BY_DIRECTION.get(direction), redstoneside);
         }
      }

      return p_235551_2_;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.DOWN) {
         return p_196271_1_;
      } else if (p_196271_2_ == Direction.UP) {
         return this.getConnectionState(p_196271_4_, p_196271_1_, p_196271_5_);
      } else {
         RedstoneSide redstoneside = this.getConnectingSide(p_196271_4_, p_196271_5_, p_196271_2_);
         return redstoneside.isConnected() == p_196271_1_.getValue(PROPERTY_BY_DIRECTION.get(p_196271_2_)).isConnected() && !isCross(p_196271_1_) ? p_196271_1_.setValue(PROPERTY_BY_DIRECTION.get(p_196271_2_), redstoneside) : this.getConnectionState(p_196271_4_, this.crossState.setValue(POWER, p_196271_1_.getValue(POWER)).setValue(PROPERTY_BY_DIRECTION.get(p_196271_2_), redstoneside), p_196271_5_);
      }
   }

   private static boolean isCross(BlockState p_235555_0_) {
      return p_235555_0_.getValue(NORTH).isConnected() && p_235555_0_.getValue(SOUTH).isConnected() && p_235555_0_.getValue(EAST).isConnected() && p_235555_0_.getValue(WEST).isConnected();
   }

   private static boolean isDot(BlockState p_235556_0_) {
      return !p_235556_0_.getValue(NORTH).isConnected() && !p_235556_0_.getValue(SOUTH).isConnected() && !p_235556_0_.getValue(EAST).isConnected() && !p_235556_0_.getValue(WEST).isConnected();
   }

   public void updateIndirectNeighbourShapes(BlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_, int p_196248_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         RedstoneSide redstoneside = p_196248_1_.getValue(PROPERTY_BY_DIRECTION.get(direction));
         if (redstoneside != RedstoneSide.NONE && !p_196248_2_.getBlockState(blockpos$mutable.setWithOffset(p_196248_3_, direction)).is(this)) {
            blockpos$mutable.move(Direction.DOWN);
            BlockState blockstate = p_196248_2_.getBlockState(blockpos$mutable);
            if (!blockstate.is(Blocks.OBSERVER)) {
               BlockPos blockpos = blockpos$mutable.relative(direction.getOpposite());
               BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), p_196248_2_.getBlockState(blockpos), p_196248_2_, blockpos$mutable, blockpos);
               updateOrDestroy(blockstate, blockstate1, p_196248_2_, blockpos$mutable, p_196248_4_, p_196248_5_);
            }

            blockpos$mutable.setWithOffset(p_196248_3_, direction).move(Direction.UP);
            BlockState blockstate3 = p_196248_2_.getBlockState(blockpos$mutable);
            if (!blockstate3.is(Blocks.OBSERVER)) {
               BlockPos blockpos1 = blockpos$mutable.relative(direction.getOpposite());
               BlockState blockstate2 = blockstate3.updateShape(direction.getOpposite(), p_196248_2_.getBlockState(blockpos1), p_196248_2_, blockpos$mutable, blockpos1);
               updateOrDestroy(blockstate3, blockstate2, p_196248_2_, blockpos$mutable, p_196248_4_, p_196248_5_);
            }
         }
      }

   }

   private RedstoneSide getConnectingSide(IBlockReader p_208074_1_, BlockPos p_208074_2_, Direction p_208074_3_) {
      return this.getConnectingSide(p_208074_1_, p_208074_2_, p_208074_3_, !p_208074_1_.getBlockState(p_208074_2_.above()).isRedstoneConductor(p_208074_1_, p_208074_2_));
   }

   private RedstoneSide getConnectingSide(IBlockReader p_235545_1_, BlockPos p_235545_2_, Direction p_235545_3_, boolean p_235545_4_) {
      BlockPos blockpos = p_235545_2_.relative(p_235545_3_);
      BlockState blockstate = p_235545_1_.getBlockState(blockpos);
      if (p_235545_4_) {
         boolean flag = this.canSurviveOn(p_235545_1_, blockpos, blockstate);
         if (flag && canConnectTo(p_235545_1_.getBlockState(blockpos.above()), p_235545_1_, blockpos.above(), null) ) {
            if (blockstate.isFaceSturdy(p_235545_1_, blockpos, p_235545_3_.getOpposite())) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !canConnectTo(blockstate, p_235545_1_, blockpos, p_235545_3_) && (blockstate.isRedstoneConductor(p_235545_1_, blockpos) || !canConnectTo(p_235545_1_.getBlockState(blockpos.below()), p_235545_1_, blockpos.below(), null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.below();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      return this.canSurviveOn(p_196260_2_, blockpos, blockstate);
   }

   private boolean canSurviveOn(IBlockReader p_235552_1_, BlockPos p_235552_2_, BlockState p_235552_3_) {
      return p_235552_3_.isFaceSturdy(p_235552_1_, p_235552_2_, Direction.UP) || p_235552_3_.is(Blocks.HOPPER);
   }

   private void updatePowerStrength(World p_235547_1_, BlockPos p_235547_2_, BlockState p_235547_3_) {
      int i = this.calculateTargetStrength(p_235547_1_, p_235547_2_);
      if (p_235547_3_.getValue(POWER) != i) {
         if (p_235547_1_.getBlockState(p_235547_2_) == p_235547_3_) {
            p_235547_1_.setBlock(p_235547_2_, p_235547_3_.setValue(POWER, Integer.valueOf(i)), 2);
         }

         Set<BlockPos> set = Sets.newHashSet();
         set.add(p_235547_2_);

         for(Direction direction : Direction.values()) {
            set.add(p_235547_2_.relative(direction));
         }

         for(BlockPos blockpos : set) {
            p_235547_1_.updateNeighborsAt(blockpos, this);
         }
      }

   }

   private int calculateTargetStrength(World p_235546_1_, BlockPos p_235546_2_) {
      this.shouldSignal = false;
      int i = p_235546_1_.getBestNeighborSignal(p_235546_2_);
      this.shouldSignal = true;
      int j = 0;
      if (i < 15) {
         for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = p_235546_2_.relative(direction);
            BlockState blockstate = p_235546_1_.getBlockState(blockpos);
            j = Math.max(j, this.getWireSignal(blockstate));
            BlockPos blockpos1 = p_235546_2_.above();
            if (blockstate.isRedstoneConductor(p_235546_1_, blockpos) && !p_235546_1_.getBlockState(blockpos1).isRedstoneConductor(p_235546_1_, blockpos1)) {
               j = Math.max(j, this.getWireSignal(p_235546_1_.getBlockState(blockpos.above())));
            } else if (!blockstate.isRedstoneConductor(p_235546_1_, blockpos)) {
               j = Math.max(j, this.getWireSignal(p_235546_1_.getBlockState(blockpos.below())));
            }
         }
      }

      return Math.max(i, j - 1);
   }

   private int getWireSignal(BlockState p_235557_1_) {
      return p_235557_1_.is(this) ? p_235557_1_.getValue(POWER) : 0;
   }

   private void checkCornerChangeAt(World p_176344_1_, BlockPos p_176344_2_) {
      if (p_176344_1_.getBlockState(p_176344_2_).is(this)) {
         p_176344_1_.updateNeighborsAt(p_176344_2_, this);

         for(Direction direction : Direction.values()) {
            p_176344_1_.updateNeighborsAt(p_176344_2_.relative(direction), this);
         }

      }
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock()) && !p_220082_2_.isClientSide) {
         this.updatePowerStrength(p_220082_2_, p_220082_3_, p_220082_1_);

         for(Direction direction : Direction.Plane.VERTICAL) {
            p_220082_2_.updateNeighborsAt(p_220082_3_.relative(direction), this);
         }

         this.updateNeighborsOfNeighboringWires(p_220082_2_, p_220082_3_);
      }
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && !p_196243_1_.is(p_196243_4_.getBlock())) {
         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (!p_196243_2_.isClientSide) {
            for(Direction direction : Direction.values()) {
               p_196243_2_.updateNeighborsAt(p_196243_3_.relative(direction), this);
            }

            this.updatePowerStrength(p_196243_2_, p_196243_3_, p_196243_1_);
            this.updateNeighborsOfNeighboringWires(p_196243_2_, p_196243_3_);
         }
      }
   }

   private void updateNeighborsOfNeighboringWires(World p_235553_1_, BlockPos p_235553_2_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         this.checkCornerChangeAt(p_235553_1_, p_235553_2_.relative(direction));
      }

      for(Direction direction1 : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_235553_2_.relative(direction1);
         if (p_235553_1_.getBlockState(blockpos).isRedstoneConductor(p_235553_1_, blockpos)) {
            this.checkCornerChangeAt(p_235553_1_, blockpos.above());
         } else {
            this.checkCornerChangeAt(p_235553_1_, blockpos.below());
         }
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         if (p_220069_1_.canSurvive(p_220069_2_, p_220069_3_)) {
            this.updatePowerStrength(p_220069_2_, p_220069_3_, p_220069_1_);
         } else {
            dropResources(p_220069_1_, p_220069_2_, p_220069_3_);
            p_220069_2_.removeBlock(p_220069_3_, false);
         }

      }
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return !this.shouldSignal ? 0 : p_176211_1_.getSignal(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      if (this.shouldSignal && p_180656_4_ != Direction.DOWN) {
         int i = p_180656_1_.getValue(POWER);
         if (i == 0) {
            return 0;
         } else {
            return p_180656_4_ != Direction.UP && !this.getConnectionState(p_180656_2_, p_180656_1_, p_180656_3_).getValue(PROPERTY_BY_DIRECTION.get(p_180656_4_.getOpposite())).isConnected() ? 0 : i;
         }
      } else {
         return 0;
      }
   }

   protected static boolean canConnectTo(BlockState p_176343_0_, IBlockReader world, BlockPos pos, @Nullable Direction p_176343_1_) {
      if (p_176343_0_.is(Blocks.REDSTONE_WIRE)) {
         return true;
      } else if (p_176343_0_.is(Blocks.REPEATER)) {
         Direction direction = p_176343_0_.getValue(RepeaterBlock.FACING);
         return direction == p_176343_1_ || direction.getOpposite() == p_176343_1_;
      } else if (p_176343_0_.is(Blocks.OBSERVER)) {
         return p_176343_1_ == p_176343_0_.getValue(ObserverBlock.FACING);
      } else {
         return p_176343_0_.canConnectRedstone(world, pos, p_176343_1_) && p_176343_1_ != null;
      }
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return this.shouldSignal;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getColorForPower(int p_235550_0_) {
      Vector3f vector3f = COLORS[p_235550_0_];
      return MathHelper.color(vector3f.x(), vector3f.y(), vector3f.z());
   }

   @OnlyIn(Dist.CLIENT)
   private void spawnParticlesAlongLine(World p_235549_1_, Random p_235549_2_, BlockPos p_235549_3_, Vector3f p_235549_4_, Direction p_235549_5_, Direction p_235549_6_, float p_235549_7_, float p_235549_8_) {
      float f = p_235549_8_ - p_235549_7_;
      if (!(p_235549_2_.nextFloat() >= 0.2F * f)) {
         float f1 = 0.4375F;
         float f2 = p_235549_7_ + f * p_235549_2_.nextFloat();
         double d0 = 0.5D + (double)(0.4375F * (float)p_235549_5_.getStepX()) + (double)(f2 * (float)p_235549_6_.getStepX());
         double d1 = 0.5D + (double)(0.4375F * (float)p_235549_5_.getStepY()) + (double)(f2 * (float)p_235549_6_.getStepY());
         double d2 = 0.5D + (double)(0.4375F * (float)p_235549_5_.getStepZ()) + (double)(f2 * (float)p_235549_6_.getStepZ());
         p_235549_1_.addParticle(new RedstoneParticleData(p_235549_4_.x(), p_235549_4_.y(), p_235549_4_.z(), 1.0F), (double)p_235549_3_.getX() + d0, (double)p_235549_3_.getY() + d1, (double)p_235549_3_.getZ() + d2, 0.0D, 0.0D, 0.0D);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      int i = p_180655_1_.getValue(POWER);
      if (i != 0) {
         for(Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = p_180655_1_.getValue(PROPERTY_BY_DIRECTION.get(direction));
            switch(redstoneside) {
            case UP:
               this.spawnParticlesAlongLine(p_180655_2_, p_180655_4_, p_180655_3_, COLORS[i], direction, Direction.UP, -0.5F, 0.5F);
            case SIDE:
               this.spawnParticlesAlongLine(p_180655_2_, p_180655_4_, p_180655_3_, COLORS[i], Direction.DOWN, direction, 0.0F, 0.5F);
               break;
            case NONE:
            default:
               this.spawnParticlesAlongLine(p_180655_2_, p_180655_4_, p_180655_3_, COLORS[i], Direction.DOWN, direction, 0.0F, 0.3F);
            }
         }

      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(SOUTH)).setValue(EAST, p_185499_1_.getValue(WEST)).setValue(SOUTH, p_185499_1_.getValue(NORTH)).setValue(WEST, p_185499_1_.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(EAST)).setValue(EAST, p_185499_1_.getValue(SOUTH)).setValue(SOUTH, p_185499_1_.getValue(WEST)).setValue(WEST, p_185499_1_.getValue(NORTH));
      case CLOCKWISE_90:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(WEST)).setValue(EAST, p_185499_1_.getValue(NORTH)).setValue(SOUTH, p_185499_1_.getValue(EAST)).setValue(WEST, p_185499_1_.getValue(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return p_185471_1_.setValue(NORTH, p_185471_1_.getValue(SOUTH)).setValue(SOUTH, p_185471_1_.getValue(NORTH));
      case FRONT_BACK:
         return p_185471_1_.setValue(EAST, p_185471_1_.getValue(WEST)).setValue(WEST, p_185471_1_.getValue(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (!p_225533_4_.abilities.mayBuild) {
         return ActionResultType.PASS;
      } else {
         if (isCross(p_225533_1_) || isDot(p_225533_1_)) {
            BlockState blockstate = isCross(p_225533_1_) ? this.defaultBlockState() : this.crossState;
            blockstate = blockstate.setValue(POWER, p_225533_1_.getValue(POWER));
            blockstate = this.getConnectionState(p_225533_2_, blockstate, p_225533_3_);
            if (blockstate != p_225533_1_) {
               p_225533_2_.setBlock(p_225533_3_, blockstate, 3);
               this.updatesOnShapeChange(p_225533_2_, p_225533_3_, p_225533_1_, blockstate);
               return ActionResultType.SUCCESS;
            }
         }

         return ActionResultType.PASS;
      }
   }

   private void updatesOnShapeChange(World p_235548_1_, BlockPos p_235548_2_, BlockState p_235548_3_, BlockState p_235548_4_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_235548_2_.relative(direction);
         if (p_235548_3_.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected() != p_235548_4_.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected() && p_235548_1_.getBlockState(blockpos).isRedstoneConductor(p_235548_1_, blockpos)) {
            p_235548_1_.updateNeighborsAtExceptFromFacing(blockpos, p_235548_4_.getBlock(), direction.getOpposite());
         }
      }

   }

   static {
      for(int i = 0; i <= 15; ++i) {
         float f = (float)i / 15.0F;
         float f1 = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
         float f2 = MathHelper.clamp(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
         float f3 = MathHelper.clamp(f * f * 0.6F - 0.7F, 0.0F, 1.0F);
         COLORS[i] = new Vector3f(f1, f2, f3);
      }

   }
}
