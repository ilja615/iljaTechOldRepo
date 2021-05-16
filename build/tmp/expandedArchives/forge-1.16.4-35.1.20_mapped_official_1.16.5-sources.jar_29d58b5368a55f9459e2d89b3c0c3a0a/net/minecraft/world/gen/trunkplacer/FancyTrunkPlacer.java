package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class FancyTrunkPlacer extends AbstractTrunkPlacer {
   public static final Codec<FancyTrunkPlacer> CODEC = RecordCodecBuilder.create((p_236891_0_) -> {
      return trunkPlacerParts(p_236891_0_).apply(p_236891_0_, FancyTrunkPlacer::new);
   });

   public FancyTrunkPlacer(int p_i232054_1_, int p_i232054_2_, int p_i232054_3_) {
      super(p_i232054_1_, p_i232054_2_, p_i232054_3_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.FANCY_TRUNK_PLACER;
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
      int i = 5;
      int j = p_230382_3_ + 2;
      int k = MathHelper.floor((double)j * 0.618D);
      if (!p_230382_7_.fromSapling) {
         setDirtAt(p_230382_1_, p_230382_4_.below());
      }

      double d0 = 1.0D;
      int l = Math.min(1, MathHelper.floor(1.382D + Math.pow(1.0D * (double)j / 13.0D, 2.0D)));
      int i1 = p_230382_4_.getY() + k;
      int j1 = j - 5;
      List<FancyTrunkPlacer.Foliage> list = Lists.newArrayList();
      list.add(new FancyTrunkPlacer.Foliage(p_230382_4_.above(j1), i1));

      for(; j1 >= 0; --j1) {
         float f = this.treeShape(j, j1);
         if (!(f < 0.0F)) {
            for(int k1 = 0; k1 < l; ++k1) {
               double d1 = 1.0D;
               double d2 = 1.0D * (double)f * ((double)p_230382_2_.nextFloat() + 0.328D);
               double d3 = (double)(p_230382_2_.nextFloat() * 2.0F) * Math.PI;
               double d4 = d2 * Math.sin(d3) + 0.5D;
               double d5 = d2 * Math.cos(d3) + 0.5D;
               BlockPos blockpos = p_230382_4_.offset(d4, (double)(j1 - 1), d5);
               BlockPos blockpos1 = blockpos.above(5);
               if (this.makeLimb(p_230382_1_, p_230382_2_, blockpos, blockpos1, false, p_230382_5_, p_230382_6_, p_230382_7_)) {
                  int l1 = p_230382_4_.getX() - blockpos.getX();
                  int i2 = p_230382_4_.getZ() - blockpos.getZ();
                  double d6 = (double)blockpos.getY() - Math.sqrt((double)(l1 * l1 + i2 * i2)) * 0.381D;
                  int j2 = d6 > (double)i1 ? i1 : (int)d6;
                  BlockPos blockpos2 = new BlockPos(p_230382_4_.getX(), j2, p_230382_4_.getZ());
                  if (this.makeLimb(p_230382_1_, p_230382_2_, blockpos2, blockpos, false, p_230382_5_, p_230382_6_, p_230382_7_)) {
                     list.add(new FancyTrunkPlacer.Foliage(blockpos, blockpos2.getY()));
                  }
               }
            }
         }
      }

      this.makeLimb(p_230382_1_, p_230382_2_, p_230382_4_, p_230382_4_.above(k), true, p_230382_5_, p_230382_6_, p_230382_7_);
      this.makeBranches(p_230382_1_, p_230382_2_, j, p_230382_4_, list, p_230382_5_, p_230382_6_, p_230382_7_);
      List<FoliagePlacer.Foliage> list1 = Lists.newArrayList();

      for(FancyTrunkPlacer.Foliage fancytrunkplacer$foliage : list) {
         if (this.trimBranches(j, fancytrunkplacer$foliage.getBranchBase() - p_230382_4_.getY())) {
            list1.add(fancytrunkplacer$foliage.attachment);
         }
      }

      return list1;
   }

   private boolean makeLimb(IWorldGenerationReader p_236887_1_, Random p_236887_2_, BlockPos p_236887_3_, BlockPos p_236887_4_, boolean p_236887_5_, Set<BlockPos> p_236887_6_, MutableBoundingBox p_236887_7_, BaseTreeFeatureConfig p_236887_8_) {
      if (!p_236887_5_ && Objects.equals(p_236887_3_, p_236887_4_)) {
         return true;
      } else {
         BlockPos blockpos = p_236887_4_.offset(-p_236887_3_.getX(), -p_236887_3_.getY(), -p_236887_3_.getZ());
         int i = this.getSteps(blockpos);
         float f = (float)blockpos.getX() / (float)i;
         float f1 = (float)blockpos.getY() / (float)i;
         float f2 = (float)blockpos.getZ() / (float)i;

         for(int j = 0; j <= i; ++j) {
            BlockPos blockpos1 = p_236887_3_.offset((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * f1), (double)(0.5F + (float)j * f2));
            if (p_236887_5_) {
               setBlock(p_236887_1_, blockpos1, p_236887_8_.trunkProvider.getState(p_236887_2_, blockpos1).setValue(RotatedPillarBlock.AXIS, this.getLogAxis(p_236887_3_, blockpos1)), p_236887_7_);
               p_236887_6_.add(blockpos1.immutable());
            } else if (!TreeFeature.isFree(p_236887_1_, blockpos1)) {
               return false;
            }
         }

         return true;
      }
   }

   private int getSteps(BlockPos p_236888_1_) {
      int i = MathHelper.abs(p_236888_1_.getX());
      int j = MathHelper.abs(p_236888_1_.getY());
      int k = MathHelper.abs(p_236888_1_.getZ());
      return Math.max(i, Math.max(j, k));
   }

   private Direction.Axis getLogAxis(BlockPos p_236889_1_, BlockPos p_236889_2_) {
      Direction.Axis direction$axis = Direction.Axis.Y;
      int i = Math.abs(p_236889_2_.getX() - p_236889_1_.getX());
      int j = Math.abs(p_236889_2_.getZ() - p_236889_1_.getZ());
      int k = Math.max(i, j);
      if (k > 0) {
         if (i == k) {
            direction$axis = Direction.Axis.X;
         } else {
            direction$axis = Direction.Axis.Z;
         }
      }

      return direction$axis;
   }

   private boolean trimBranches(int p_236885_1_, int p_236885_2_) {
      return (double)p_236885_2_ >= (double)p_236885_1_ * 0.2D;
   }

   private void makeBranches(IWorldGenerationReader p_236886_1_, Random p_236886_2_, int p_236886_3_, BlockPos p_236886_4_, List<FancyTrunkPlacer.Foliage> p_236886_5_, Set<BlockPos> p_236886_6_, MutableBoundingBox p_236886_7_, BaseTreeFeatureConfig p_236886_8_) {
      for(FancyTrunkPlacer.Foliage fancytrunkplacer$foliage : p_236886_5_) {
         int i = fancytrunkplacer$foliage.getBranchBase();
         BlockPos blockpos = new BlockPos(p_236886_4_.getX(), i, p_236886_4_.getZ());
         if (!blockpos.equals(fancytrunkplacer$foliage.attachment.foliagePos()) && this.trimBranches(p_236886_3_, i - p_236886_4_.getY())) {
            this.makeLimb(p_236886_1_, p_236886_2_, blockpos, fancytrunkplacer$foliage.attachment.foliagePos(), true, p_236886_6_, p_236886_7_, p_236886_8_);
         }
      }

   }

   private float treeShape(int p_236890_1_, int p_236890_2_) {
      if ((float)p_236890_2_ < (float)p_236890_1_ * 0.3F) {
         return -1.0F;
      } else {
         float f = (float)p_236890_1_ / 2.0F;
         float f1 = f - (float)p_236890_2_;
         float f2 = MathHelper.sqrt(f * f - f1 * f1);
         if (f1 == 0.0F) {
            f2 = f;
         } else if (Math.abs(f1) >= f) {
            return 0.0F;
         }

         return f2 * 0.5F;
      }
   }

   static class Foliage {
      private final FoliagePlacer.Foliage attachment;
      private final int branchBase;

      public Foliage(BlockPos p_i232055_1_, int p_i232055_2_) {
         this.attachment = new FoliagePlacer.Foliage(p_i232055_1_, 0, false);
         this.branchBase = p_i232055_2_;
      }

      public int getBranchBase() {
         return this.branchBase;
      }
   }
}
