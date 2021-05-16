package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ObserverBlock extends DirectionalBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public ObserverBlock(AbstractBlock.Properties p_i48358_1_) {
      super(p_i48358_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_1_.getValue(POWERED)) {
         p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(POWERED, Boolean.valueOf(false)), 2);
      } else {
         p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(POWERED, Boolean.valueOf(true)), 2);
         p_225534_2_.getBlockTicks().scheduleTick(p_225534_3_, this, 2);
      }

      this.updateNeighborsInFront(p_225534_2_, p_225534_3_, p_225534_1_);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getValue(FACING) == p_196271_2_ && !p_196271_1_.getValue(POWERED)) {
         this.startSignal(p_196271_4_, p_196271_5_);
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   private void startSignal(IWorld p_203420_1_, BlockPos p_203420_2_) {
      if (!p_203420_1_.isClientSide() && !p_203420_1_.getBlockTicks().hasScheduledTick(p_203420_2_, this)) {
         p_203420_1_.getBlockTicks().scheduleTick(p_203420_2_, this, 2);
      }

   }

   protected void updateNeighborsInFront(World p_190961_1_, BlockPos p_190961_2_, BlockState p_190961_3_) {
      Direction direction = p_190961_3_.getValue(FACING);
      BlockPos blockpos = p_190961_2_.relative(direction.getOpposite());
      p_190961_1_.neighborChanged(blockpos, this, p_190961_2_);
      p_190961_1_.updateNeighborsAtExceptFromFacing(blockpos, this, direction);
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_1_.getSignal(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(POWERED) && p_180656_1_.getValue(FACING) == p_180656_4_ ? 15 : 0;
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_1_.is(p_220082_4_.getBlock())) {
         if (!p_220082_2_.isClientSide() && p_220082_1_.getValue(POWERED) && !p_220082_2_.getBlockTicks().hasScheduledTick(p_220082_3_, this)) {
            BlockState blockstate = p_220082_1_.setValue(POWERED, Boolean.valueOf(false));
            p_220082_2_.setBlock(p_220082_3_, blockstate, 18);
            this.updateNeighborsInFront(p_220082_2_, p_220082_3_, blockstate);
         }

      }
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         if (!p_196243_2_.isClientSide && p_196243_1_.getValue(POWERED) && p_196243_2_.getBlockTicks().hasScheduledTick(p_196243_3_, this)) {
            this.updateNeighborsInFront(p_196243_2_, p_196243_3_, p_196243_1_.setValue(POWERED, Boolean.valueOf(false)));
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getNearestLookingDirection().getOpposite().getOpposite());
   }
}
