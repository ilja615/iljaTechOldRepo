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

public class GiantTrunkPlacer extends AbstractTrunkPlacer {
   public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create((p_236900_0_) -> {
      return trunkPlacerParts(p_236900_0_).apply(p_236900_0_, GiantTrunkPlacer::new);
   });

   public GiantTrunkPlacer(int p_i232057_1_, int p_i232057_2_, int p_i232057_3_) {
      super(p_i232057_1_, p_i232057_2_, p_i232057_3_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.GIANT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
      BlockPos blockpos = p_230382_4_.below();
      setDirtAt(p_230382_1_, blockpos);
      setDirtAt(p_230382_1_, blockpos.east());
      setDirtAt(p_230382_1_, blockpos.south());
      setDirtAt(p_230382_1_, blockpos.south().east());
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < p_230382_3_; ++i) {
         placeLogIfFreeWithOffset(p_230382_1_, p_230382_2_, blockpos$mutable, p_230382_5_, p_230382_6_, p_230382_7_, p_230382_4_, 0, i, 0);
         if (i < p_230382_3_ - 1) {
            placeLogIfFreeWithOffset(p_230382_1_, p_230382_2_, blockpos$mutable, p_230382_5_, p_230382_6_, p_230382_7_, p_230382_4_, 1, i, 0);
            placeLogIfFreeWithOffset(p_230382_1_, p_230382_2_, blockpos$mutable, p_230382_5_, p_230382_6_, p_230382_7_, p_230382_4_, 1, i, 1);
            placeLogIfFreeWithOffset(p_230382_1_, p_230382_2_, blockpos$mutable, p_230382_5_, p_230382_6_, p_230382_7_, p_230382_4_, 0, i, 1);
         }
      }

      return ImmutableList.of(new FoliagePlacer.Foliage(p_230382_4_.above(p_230382_3_), 0, true));
   }

   private static void placeLogIfFreeWithOffset(IWorldGenerationReader p_236899_0_, Random p_236899_1_, BlockPos.Mutable p_236899_2_, Set<BlockPos> p_236899_3_, MutableBoundingBox p_236899_4_, BaseTreeFeatureConfig p_236899_5_, BlockPos p_236899_6_, int p_236899_7_, int p_236899_8_, int p_236899_9_) {
      p_236899_2_.setWithOffset(p_236899_6_, p_236899_7_, p_236899_8_, p_236899_9_);
      placeLogIfFree(p_236899_0_, p_236899_1_, p_236899_2_, p_236899_3_, p_236899_4_, p_236899_5_);
   }
}
