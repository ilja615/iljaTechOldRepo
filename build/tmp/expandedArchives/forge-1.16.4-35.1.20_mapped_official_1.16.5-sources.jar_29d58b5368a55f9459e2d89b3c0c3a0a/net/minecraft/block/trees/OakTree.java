package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

public class OakTree extends Tree {
   @Nullable
   protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_) {
      if (p_225546_1_.nextInt(10) == 0) {
         return p_225546_2_ ? Features.FANCY_OAK_BEES_005 : Features.FANCY_OAK;
      } else {
         return p_225546_2_ ? Features.OAK_BEES_005 : Features.OAK;
      }
   }
}
