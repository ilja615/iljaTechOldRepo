package net.minecraft.world.gen.foliageplacer;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.TreeFeature;

public abstract class FoliagePlacer {
   public static final Codec<FoliagePlacer> CODEC = Registry.FOLIAGE_PLACER_TYPES.dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
   protected final FeatureSpread radius;
   protected final FeatureSpread offset;

   protected static <P extends FoliagePlacer> P2<Mu<P>, FeatureSpread, FeatureSpread> foliagePlacerParts(Instance<P> p_242830_0_) {
      return p_242830_0_.group(FeatureSpread.codec(0, 8, 8).fieldOf("radius").forGetter((p_242829_0_) -> {
         return p_242829_0_.radius;
      }), FeatureSpread.codec(0, 8, 8).fieldOf("offset").forGetter((p_242828_0_) -> {
         return p_242828_0_.offset;
      }));
   }

   public FoliagePlacer(FeatureSpread p_i241999_1_, FeatureSpread p_i241999_2_) {
      this.radius = p_i241999_1_;
      this.offset = p_i241999_2_;
   }

   protected abstract FoliagePlacerType<?> type();

   public void createFoliage(IWorldGenerationReader p_236752_1_, Random p_236752_2_, BaseTreeFeatureConfig p_236752_3_, int p_236752_4_, FoliagePlacer.Foliage p_236752_5_, int p_236752_6_, int p_236752_7_, Set<BlockPos> p_236752_8_, MutableBoundingBox p_236752_9_) {
      this.createFoliage(p_236752_1_, p_236752_2_, p_236752_3_, p_236752_4_, p_236752_5_, p_236752_6_, p_236752_7_, p_236752_8_, this.offset(p_236752_2_), p_236752_9_);
   }

   protected abstract void createFoliage(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_);

   public abstract int foliageHeight(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_);

   public int foliageRadius(Random p_230376_1_, int p_230376_2_) {
      return this.radius.sample(p_230376_1_);
   }

   private int offset(Random p_236755_1_) {
      return this.offset.sample(p_236755_1_);
   }

   protected abstract boolean shouldSkipLocation(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_);

   protected boolean shouldSkipLocationSigned(Random p_230375_1_, int p_230375_2_, int p_230375_3_, int p_230375_4_, int p_230375_5_, boolean p_230375_6_) {
      int i;
      int j;
      if (p_230375_6_) {
         i = Math.min(Math.abs(p_230375_2_), Math.abs(p_230375_2_ - 1));
         j = Math.min(Math.abs(p_230375_4_), Math.abs(p_230375_4_ - 1));
      } else {
         i = Math.abs(p_230375_2_);
         j = Math.abs(p_230375_4_);
      }

      return this.shouldSkipLocation(p_230375_1_, i, p_230375_3_, j, p_230375_5_, p_230375_6_);
   }

   protected void placeLeavesRow(IWorldGenerationReader p_236753_1_, Random p_236753_2_, BaseTreeFeatureConfig p_236753_3_, BlockPos p_236753_4_, int p_236753_5_, Set<BlockPos> p_236753_6_, int p_236753_7_, boolean p_236753_8_, MutableBoundingBox p_236753_9_) {
      int i = p_236753_8_ ? 1 : 0;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j = -p_236753_5_; j <= p_236753_5_ + i; ++j) {
         for(int k = -p_236753_5_; k <= p_236753_5_ + i; ++k) {
            if (!this.shouldSkipLocationSigned(p_236753_2_, j, p_236753_7_, k, p_236753_5_, p_236753_8_)) {
               blockpos$mutable.setWithOffset(p_236753_4_, j, p_236753_7_, k);
               if (TreeFeature.validTreePos(p_236753_1_, blockpos$mutable)) {
                  p_236753_1_.setBlock(blockpos$mutable, p_236753_3_.leavesProvider.getState(p_236753_2_, blockpos$mutable), 19);
                  p_236753_9_.expand(new MutableBoundingBox(blockpos$mutable, blockpos$mutable));
                  p_236753_6_.add(blockpos$mutable.immutable());
               }
            }
         }
      }

   }

   public static final class Foliage {
      private final BlockPos foliagePos;
      private final int radiusOffset;
      private final boolean doubleTrunk;

      public Foliage(BlockPos p_i232035_1_, int p_i232035_2_, boolean p_i232035_3_) {
         this.foliagePos = p_i232035_1_;
         this.radiusOffset = p_i232035_2_;
         this.doubleTrunk = p_i232035_3_;
      }

      public BlockPos foliagePos() {
         return this.foliagePos;
      }

      public int radiusOffset() {
         return this.radiusOffset;
      }

      public boolean doubleTrunk() {
         return this.doubleTrunk;
      }
   }
}
