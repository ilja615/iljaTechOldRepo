package net.minecraft.world.gen.foliageplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;

public class PineFoliagePlacer extends FoliagePlacer {
   public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create((p_242834_0_) -> {
      return foliagePlacerParts(p_242834_0_).and(FeatureSpread.codec(0, 16, 8).fieldOf("height").forGetter((p_242833_0_) -> {
         return p_242833_0_.height;
      })).apply(p_242834_0_, PineFoliagePlacer::new);
   });
   private final FeatureSpread height;

   public PineFoliagePlacer(FeatureSpread p_i242002_1_, FeatureSpread p_i242002_2_, FeatureSpread p_i242002_3_) {
      super(p_i242002_1_, p_i242002_2_);
      this.height = p_i242002_3_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.PINE_FOLIAGE_PLACER;
   }

   protected void createFoliage(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_) {
      int i = 0;

      for(int j = p_230372_9_; j >= p_230372_9_ - p_230372_6_; --j) {
         this.placeLeavesRow(p_230372_1_, p_230372_2_, p_230372_3_, p_230372_5_.foliagePos(), i, p_230372_8_, j, p_230372_5_.doubleTrunk(), p_230372_10_);
         if (i >= 1 && j == p_230372_9_ - p_230372_6_ + 1) {
            --i;
         } else if (i < p_230372_7_ + p_230372_5_.radiusOffset()) {
            ++i;
         }
      }

   }

   public int foliageRadius(Random p_230376_1_, int p_230376_2_) {
      return super.foliageRadius(p_230376_1_, p_230376_2_) + p_230376_1_.nextInt(p_230376_2_ + 1);
   }

   public int foliageHeight(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_) {
      return this.height.sample(p_230374_1_);
   }

   protected boolean shouldSkipLocation(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_) {
      return p_230373_2_ == p_230373_5_ && p_230373_4_ == p_230373_5_ && p_230373_5_ > 0;
   }
}
