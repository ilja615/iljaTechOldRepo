package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractTopPlantBlock extends AbstractPlantBlock implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
   private final double growPerTickProbability;

   protected AbstractTopPlantBlock(AbstractBlock.Properties p_i241180_1_, Direction p_i241180_2_, VoxelShape p_i241180_3_, boolean p_i241180_4_, double p_i241180_5_) {
      super(p_i241180_1_, p_i241180_2_, p_i241180_3_, p_i241180_4_);
      this.growPerTickProbability = p_i241180_5_;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public BlockState getStateForPlacement(IWorld p_235504_1_) {
      return this.defaultBlockState().setValue(AGE, Integer.valueOf(p_235504_1_.getRandom().nextInt(25)));
   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return p_149653_1_.getValue(AGE) < 25;
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (p_225542_1_.getValue(AGE) < 25 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225542_2_, p_225542_3_.relative(this.growthDirection), p_225542_2_.getBlockState(p_225542_3_.relative(this.growthDirection)),p_225542_4_.nextDouble() < this.growPerTickProbability)) {
         BlockPos blockpos = p_225542_3_.relative(this.growthDirection);
         if (this.canGrowInto(p_225542_2_.getBlockState(blockpos))) {
            p_225542_2_.setBlockAndUpdate(blockpos, p_225542_1_.cycle(AGE));
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_225542_2_, blockpos, p_225542_2_.getBlockState(blockpos));
         }
      }

   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == this.growthDirection.getOpposite() && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      if (p_196271_2_ != this.growthDirection || !p_196271_3_.is(this) && !p_196271_3_.is(this.getBodyBlock())) {
         if (this.scheduleFluidTicks) {
            p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
         }

         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         return this.getBodyBlock().defaultBlockState();
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return this.canGrowInto(p_176473_1_.getBlockState(p_176473_2_.relative(this.growthDirection)));
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      BlockPos blockpos = p_225535_3_.relative(this.growthDirection);
      int i = Math.min(p_225535_4_.getValue(AGE) + 1, 25);
      int j = this.getBlocksToGrowWhenBonemealed(p_225535_2_);

      for(int k = 0; k < j && this.canGrowInto(p_225535_1_.getBlockState(blockpos)); ++k) {
         p_225535_1_.setBlockAndUpdate(blockpos, p_225535_4_.setValue(AGE, Integer.valueOf(i)));
         blockpos = blockpos.relative(this.growthDirection);
         i = Math.min(i + 1, 25);
      }

   }

   protected abstract int getBlocksToGrowWhenBonemealed(Random p_230332_1_);

   protected abstract boolean canGrowInto(BlockState p_230334_1_);

   protected AbstractTopPlantBlock getHeadBlock() {
      return this;
   }
}
