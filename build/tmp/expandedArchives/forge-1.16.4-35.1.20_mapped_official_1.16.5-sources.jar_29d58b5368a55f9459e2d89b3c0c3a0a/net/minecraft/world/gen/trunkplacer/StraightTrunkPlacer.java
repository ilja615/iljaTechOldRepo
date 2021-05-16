package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class StraightTrunkPlacer extends AbstractTrunkPlacer {
   public static final Codec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.create((p_236904_0_) -> {
      return trunkPlacerParts(p_236904_0_).apply(p_236904_0_, StraightTrunkPlacer::new);
   });

   public StraightTrunkPlacer(int p_i232059_1_, int p_i232059_2_, int p_i232059_3_) {
      super(p_i232059_1_, p_i232059_2_, p_i232059_3_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
      setDirtAt(p_230382_1_, p_230382_4_.below());

      for(int i = 0; i < p_230382_3_; ++i) {
         placeLog(p_230382_1_, p_230382_2_, p_230382_4_.above(i), p_230382_5_, p_230382_6_, p_230382_7_);
      }

      return ImmutableList.of(new FoliagePlacer.Foliage(p_230382_4_.above(p_230382_3_), 0, false));
   }
}
