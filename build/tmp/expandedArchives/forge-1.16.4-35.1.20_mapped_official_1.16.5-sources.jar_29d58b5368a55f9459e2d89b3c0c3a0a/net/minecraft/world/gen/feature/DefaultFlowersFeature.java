package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DefaultFlowersFeature extends FlowersFeature<BlockClusterFeatureConfig> {
   public DefaultFlowersFeature(Codec<BlockClusterFeatureConfig> p_i231945_1_) {
      super(p_i231945_1_);
   }

   public boolean isValid(IWorld p_225559_1_, BlockPos p_225559_2_, BlockClusterFeatureConfig p_225559_3_) {
      return !p_225559_3_.blacklist.contains(p_225559_1_.getBlockState(p_225559_2_));
   }

   public int getCount(BlockClusterFeatureConfig p_225560_1_) {
      return p_225560_1_.tries;
   }

   public BlockPos getPos(Random p_225561_1_, BlockPos p_225561_2_, BlockClusterFeatureConfig p_225561_3_) {
      return p_225561_2_.offset(p_225561_1_.nextInt(p_225561_3_.xspread) - p_225561_1_.nextInt(p_225561_3_.xspread), p_225561_1_.nextInt(p_225561_3_.yspread) - p_225561_1_.nextInt(p_225561_3_.yspread), p_225561_1_.nextInt(p_225561_3_.zspread) - p_225561_1_.nextInt(p_225561_3_.zspread));
   }

   public BlockState getRandomFlower(Random p_225562_1_, BlockPos p_225562_2_, BlockClusterFeatureConfig p_225562_3_) {
      return p_225562_3_.stateProvider.getState(p_225562_1_, p_225562_2_);
   }
}
