package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.NetherVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVineFeature;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public class NyliumBlock extends Block implements IGrowable {
   public NyliumBlock(AbstractBlock.Properties p_i241184_1_) {
      super(p_i241184_1_);
   }

   private static boolean canBeNylium(BlockState p_235516_0_, IWorldReader p_235516_1_, BlockPos p_235516_2_) {
      BlockPos blockpos = p_235516_2_.above();
      BlockState blockstate = p_235516_1_.getBlockState(blockpos);
      int i = LightEngine.getLightBlockInto(p_235516_1_, p_235516_0_, p_235516_2_, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(p_235516_1_, blockpos));
      return i < p_235516_1_.getMaxLightLevel();
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (!canBeNylium(p_225542_1_, p_225542_2_, p_225542_3_)) {
         p_225542_2_.setBlockAndUpdate(p_225542_3_, Blocks.NETHERRACK.defaultBlockState());
      }

   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_1_.getBlockState(p_176473_2_.above()).isAir();
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      BlockState blockstate = p_225535_1_.getBlockState(p_225535_3_);
      BlockPos blockpos = p_225535_3_.above();
      if (blockstate.is(Blocks.CRIMSON_NYLIUM)) {
         NetherVegetationFeature.place(p_225535_1_, p_225535_2_, blockpos, Features.Configs.CRIMSON_FOREST_CONFIG, 3, 1);
      } else if (blockstate.is(Blocks.WARPED_NYLIUM)) {
         NetherVegetationFeature.place(p_225535_1_, p_225535_2_, blockpos, Features.Configs.WARPED_FOREST_CONFIG, 3, 1);
         NetherVegetationFeature.place(p_225535_1_, p_225535_2_, blockpos, Features.Configs.NETHER_SPROUTS_CONFIG, 3, 1);
         if (p_225535_2_.nextInt(8) == 0) {
            TwistingVineFeature.place(p_225535_1_, p_225535_2_, blockpos, 3, 1, 2);
         }
      }

   }
}
