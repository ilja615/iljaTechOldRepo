package net.minecraft.world.gen.placement;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;

public class FirePlacement extends SimplePlacement<FeatureSpreadConfig> {
   public FirePlacement(Codec<FeatureSpreadConfig> p_i232101_1_) {
      super(p_i232101_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, FeatureSpreadConfig p_212852_2_, BlockPos p_212852_3_) {
      List<BlockPos> list = Lists.newArrayList();

      for(int i = 0; i < p_212852_1_.nextInt(p_212852_1_.nextInt(p_212852_2_.count().sample(p_212852_1_)) + 1) + 1; ++i) {
         int j = p_212852_1_.nextInt(16) + p_212852_3_.getX();
         int k = p_212852_1_.nextInt(16) + p_212852_3_.getZ();
         int l = p_212852_1_.nextInt(120) + 4;
         list.add(new BlockPos(j, l, k));
      }

      return list.stream();
   }
}
