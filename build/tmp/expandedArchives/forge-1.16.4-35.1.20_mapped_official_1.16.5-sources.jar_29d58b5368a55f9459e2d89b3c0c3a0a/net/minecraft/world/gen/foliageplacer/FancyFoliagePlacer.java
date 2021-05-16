package net.minecraft.world.gen.foliageplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;

public class FancyFoliagePlacer extends BlobFoliagePlacer {
   public static final Codec<FancyFoliagePlacer> CODEC = RecordCodecBuilder.create((p_236748_0_) -> {
      return blobParts(p_236748_0_).apply(p_236748_0_, FancyFoliagePlacer::new);
   });

   public FancyFoliagePlacer(FeatureSpread p_i241998_1_, FeatureSpread p_i241998_2_, int p_i241998_3_) {
      super(p_i241998_1_, p_i241998_2_, p_i241998_3_);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.FANCY_FOLIAGE_PLACER;
   }

   protected void createFoliage(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_) {
      for(int i = p_230372_9_; i >= p_230372_9_ - p_230372_6_; --i) {
         int j = p_230372_7_ + (i != p_230372_9_ && i != p_230372_9_ - p_230372_6_ ? 1 : 0);
         this.placeLeavesRow(p_230372_1_, p_230372_2_, p_230372_3_, p_230372_5_.foliagePos(), j, p_230372_8_, i, p_230372_5_.doubleTrunk(), p_230372_10_);
      }

   }

   protected boolean shouldSkipLocation(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_) {
      return MathHelper.square((float)p_230373_2_ + 0.5F) + MathHelper.square((float)p_230373_4_ + 0.5F) > (float)(p_230373_5_ * p_230373_5_);
   }
}
