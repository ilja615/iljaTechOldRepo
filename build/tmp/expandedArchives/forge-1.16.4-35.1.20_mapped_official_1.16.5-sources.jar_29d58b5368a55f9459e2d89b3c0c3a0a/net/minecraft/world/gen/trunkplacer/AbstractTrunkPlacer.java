package net.minecraft.world.gen.trunkplacer;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public abstract class AbstractTrunkPlacer {
   public static final Codec<AbstractTrunkPlacer> CODEC = Registry.TRUNK_PLACER_TYPES.dispatch(AbstractTrunkPlacer::type, TrunkPlacerType::codec);
   protected final int baseHeight;
   protected final int heightRandA;
   protected final int heightRandB;

   protected static <P extends AbstractTrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> trunkPlacerParts(Instance<P> p_236915_0_) {
      return p_236915_0_.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((p_236919_0_) -> {
         return p_236919_0_.baseHeight;
      }), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((p_236918_0_) -> {
         return p_236918_0_.heightRandA;
      }), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((p_236916_0_) -> {
         return p_236916_0_.heightRandB;
      }));
   }

   public AbstractTrunkPlacer(int p_i232060_1_, int p_i232060_2_, int p_i232060_3_) {
      this.baseHeight = p_i232060_1_;
      this.heightRandA = p_i232060_2_;
      this.heightRandB = p_i232060_3_;
   }

   protected abstract TrunkPlacerType<?> type();

   public abstract List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_);

   public int getTreeHeight(Random p_236917_1_) {
      return this.baseHeight + p_236917_1_.nextInt(this.heightRandA + 1) + p_236917_1_.nextInt(this.heightRandB + 1);
   }

   protected static void setBlock(IWorldWriter p_236913_0_, BlockPos p_236913_1_, BlockState p_236913_2_, MutableBoundingBox p_236913_3_) {
      TreeFeature.setBlockKnownShape(p_236913_0_, p_236913_1_, p_236913_2_);
      p_236913_3_.expand(new MutableBoundingBox(p_236913_1_, p_236913_1_));
   }

   private static boolean isDirt(IWorldGenerationBaseReader p_236912_0_, BlockPos p_236912_1_) {
      return p_236912_0_.isStateAtPosition(p_236912_1_, (p_236914_0_) -> {
         Block block = p_236914_0_.getBlock();
         return Feature.isDirt(block) && !p_236914_0_.is(Blocks.GRASS_BLOCK) && !p_236914_0_.is(Blocks.MYCELIUM);
      });
   }

   protected static void setDirtAt(IWorldGenerationReader p_236909_0_, BlockPos p_236909_1_) {
      if (!isDirt(p_236909_0_, p_236909_1_)) {
         TreeFeature.setBlockKnownShape(p_236909_0_, p_236909_1_, Blocks.DIRT.defaultBlockState());
      }

   }

   protected static boolean placeLog(IWorldGenerationReader p_236911_0_, Random p_236911_1_, BlockPos p_236911_2_, Set<BlockPos> p_236911_3_, MutableBoundingBox p_236911_4_, BaseTreeFeatureConfig p_236911_5_) {
      if (TreeFeature.validTreePos(p_236911_0_, p_236911_2_)) {
         setBlock(p_236911_0_, p_236911_2_, p_236911_5_.trunkProvider.getState(p_236911_1_, p_236911_2_), p_236911_4_);
         p_236911_3_.add(p_236911_2_.immutable());
         return true;
      } else {
         return false;
      }
   }

   protected static void placeLogIfFree(IWorldGenerationReader p_236910_0_, Random p_236910_1_, BlockPos.Mutable p_236910_2_, Set<BlockPos> p_236910_3_, MutableBoundingBox p_236910_4_, BaseTreeFeatureConfig p_236910_5_) {
      if (TreeFeature.isFree(p_236910_0_, p_236910_2_)) {
         placeLog(p_236910_0_, p_236910_1_, p_236910_2_, p_236910_3_, p_236910_4_, p_236910_5_);
      }

   }
}
