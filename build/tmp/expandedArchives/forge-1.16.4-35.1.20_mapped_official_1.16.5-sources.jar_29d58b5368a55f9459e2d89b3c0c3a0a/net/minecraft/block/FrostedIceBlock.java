package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FrostedIceBlock extends IceBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

   public FrostedIceBlock(AbstractBlock.Properties p_i48394_1_) {
      super(p_i48394_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      this.tick(p_225542_1_, p_225542_2_, p_225542_3_, p_225542_4_);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((p_225534_4_.nextInt(3) == 0 || this.fewerNeigboursThan(p_225534_2_, p_225534_3_, 4)) && p_225534_2_.getMaxLocalRawBrightness(p_225534_3_) > 11 - p_225534_1_.getValue(AGE) - p_225534_1_.getLightBlock(p_225534_2_, p_225534_3_) && this.slightlyMelt(p_225534_1_, p_225534_2_, p_225534_3_)) {
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(Direction direction : Direction.values()) {
            blockpos$mutable.setWithOffset(p_225534_3_, direction);
            BlockState blockstate = p_225534_2_.getBlockState(blockpos$mutable);
            if (blockstate.is(this) && !this.slightlyMelt(blockstate, p_225534_2_, blockpos$mutable)) {
               p_225534_2_.getBlockTicks().scheduleTick(blockpos$mutable, this, MathHelper.nextInt(p_225534_4_, 20, 40));
            }
         }

      } else {
         p_225534_2_.getBlockTicks().scheduleTick(p_225534_3_, this, MathHelper.nextInt(p_225534_4_, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState p_196455_1_, World p_196455_2_, BlockPos p_196455_3_) {
      int i = p_196455_1_.getValue(AGE);
      if (i < 3) {
         p_196455_2_.setBlock(p_196455_3_, p_196455_1_.setValue(AGE, Integer.valueOf(i + 1)), 2);
         return false;
      } else {
         this.melt(p_196455_1_, p_196455_2_, p_196455_3_);
         return true;
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_4_ == this && this.fewerNeigboursThan(p_220069_2_, p_220069_3_, 2)) {
         this.melt(p_220069_1_, p_220069_2_, p_220069_3_);
      }

      super.neighborChanged(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
   }

   private boolean fewerNeigboursThan(IBlockReader p_196456_1_, BlockPos p_196456_2_, int p_196456_3_) {
      int i = 0;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Direction direction : Direction.values()) {
         blockpos$mutable.setWithOffset(p_196456_2_, direction);
         if (p_196456_1_.getBlockState(blockpos$mutable).is(this)) {
            ++i;
            if (i >= p_196456_3_) {
               return false;
            }
         }
      }

      return true;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }
}
