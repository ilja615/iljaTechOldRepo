package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class RandomPositionGenerator {
   @Nullable
   public static Vector3d getPos(CreatureEntity p_75463_0_, int p_75463_1_, int p_75463_2_) {
      return generateRandomPos(p_75463_0_, p_75463_1_, p_75463_2_, 0, (Vector3d)null, true, (double)((float)Math.PI / 2F), p_75463_0_::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vector3d getAirPos(CreatureEntity p_226338_0_, int p_226338_1_, int p_226338_2_, int p_226338_3_, @Nullable Vector3d p_226338_4_, double p_226338_5_) {
      return generateRandomPos(p_226338_0_, p_226338_1_, p_226338_2_, p_226338_3_, p_226338_4_, true, p_226338_5_, p_226338_0_::getWalkTargetValue, true, 0, 0, false);
   }

   @Nullable
   public static Vector3d getLandPos(CreatureEntity p_191377_0_, int p_191377_1_, int p_191377_2_) {
      return getLandPos(p_191377_0_, p_191377_1_, p_191377_2_, p_191377_0_::getWalkTargetValue);
   }

   @Nullable
   public static Vector3d getLandPos(CreatureEntity p_221024_0_, int p_221024_1_, int p_221024_2_, ToDoubleFunction<BlockPos> p_221024_3_) {
      return generateRandomPos(p_221024_0_, p_221024_1_, p_221024_2_, 0, (Vector3d)null, false, 0.0D, p_221024_3_, true, 0, 0, true);
   }

   @Nullable
   public static Vector3d getAboveLandPos(CreatureEntity p_226340_0_, int p_226340_1_, int p_226340_2_, Vector3d p_226340_3_, float p_226340_4_, int p_226340_5_, int p_226340_6_) {
      return generateRandomPos(p_226340_0_, p_226340_1_, p_226340_2_, 0, p_226340_3_, false, (double)p_226340_4_, p_226340_0_::getWalkTargetValue, true, p_226340_5_, p_226340_6_, true);
   }

   @Nullable
   public static Vector3d getLandPosTowards(CreatureEntity p_234133_0_, int p_234133_1_, int p_234133_2_, Vector3d p_234133_3_) {
      Vector3d vector3d = p_234133_3_.subtract(p_234133_0_.getX(), p_234133_0_.getY(), p_234133_0_.getZ());
      return generateRandomPos(p_234133_0_, p_234133_1_, p_234133_2_, 0, vector3d, false, (double)((float)Math.PI / 2F), p_234133_0_::getWalkTargetValue, true, 0, 0, true);
   }

   @Nullable
   public static Vector3d getPosTowards(CreatureEntity p_75464_0_, int p_75464_1_, int p_75464_2_, Vector3d p_75464_3_) {
      Vector3d vector3d = p_75464_3_.subtract(p_75464_0_.getX(), p_75464_0_.getY(), p_75464_0_.getZ());
      return generateRandomPos(p_75464_0_, p_75464_1_, p_75464_2_, 0, vector3d, true, (double)((float)Math.PI / 2F), p_75464_0_::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vector3d getPosTowards(CreatureEntity p_203155_0_, int p_203155_1_, int p_203155_2_, Vector3d p_203155_3_, double p_203155_4_) {
      Vector3d vector3d = p_203155_3_.subtract(p_203155_0_.getX(), p_203155_0_.getY(), p_203155_0_.getZ());
      return generateRandomPos(p_203155_0_, p_203155_1_, p_203155_2_, 0, vector3d, true, p_203155_4_, p_203155_0_::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vector3d getAirPosTowards(CreatureEntity p_226344_0_, int p_226344_1_, int p_226344_2_, int p_226344_3_, Vector3d p_226344_4_, double p_226344_5_) {
      Vector3d vector3d = p_226344_4_.subtract(p_226344_0_.getX(), p_226344_0_.getY(), p_226344_0_.getZ());
      return generateRandomPos(p_226344_0_, p_226344_1_, p_226344_2_, p_226344_3_, vector3d, false, p_226344_5_, p_226344_0_::getWalkTargetValue, true, 0, 0, false);
   }

   @Nullable
   public static Vector3d getPosAvoid(CreatureEntity p_75461_0_, int p_75461_1_, int p_75461_2_, Vector3d p_75461_3_) {
      Vector3d vector3d = p_75461_0_.position().subtract(p_75461_3_);
      return generateRandomPos(p_75461_0_, p_75461_1_, p_75461_2_, 0, vector3d, true, (double)((float)Math.PI / 2F), p_75461_0_::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vector3d getLandPosAvoid(CreatureEntity p_223548_0_, int p_223548_1_, int p_223548_2_, Vector3d p_223548_3_) {
      Vector3d vector3d = p_223548_0_.position().subtract(p_223548_3_);
      return generateRandomPos(p_223548_0_, p_223548_1_, p_223548_2_, 0, vector3d, false, (double)((float)Math.PI / 2F), p_223548_0_::getWalkTargetValue, true, 0, 0, true);
   }

   @Nullable
   private static Vector3d generateRandomPos(CreatureEntity p_226339_0_, int p_226339_1_, int p_226339_2_, int p_226339_3_, @Nullable Vector3d p_226339_4_, boolean p_226339_5_, double p_226339_6_, ToDoubleFunction<BlockPos> p_226339_8_, boolean p_226339_9_, int p_226339_10_, int p_226339_11_, boolean p_226339_12_) {
      PathNavigator pathnavigator = p_226339_0_.getNavigation();
      Random random = p_226339_0_.getRandom();
      boolean flag;
      if (p_226339_0_.hasRestriction()) {
         flag = p_226339_0_.getRestrictCenter().closerThan(p_226339_0_.position(), (double)(p_226339_0_.getRestrictRadius() + (float)p_226339_1_) + 1.0D);
      } else {
         flag = false;
      }

      boolean flag1 = false;
      double d0 = Double.NEGATIVE_INFINITY;
      BlockPos blockpos = p_226339_0_.blockPosition();

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos1 = getRandomDelta(random, p_226339_1_, p_226339_2_, p_226339_3_, p_226339_4_, p_226339_6_);
         if (blockpos1 != null) {
            int j = blockpos1.getX();
            int k = blockpos1.getY();
            int l = blockpos1.getZ();
            if (p_226339_0_.hasRestriction() && p_226339_1_ > 1) {
               BlockPos blockpos2 = p_226339_0_.getRestrictCenter();
               if (p_226339_0_.getX() > (double)blockpos2.getX()) {
                  j -= random.nextInt(p_226339_1_ / 2);
               } else {
                  j += random.nextInt(p_226339_1_ / 2);
               }

               if (p_226339_0_.getZ() > (double)blockpos2.getZ()) {
                  l -= random.nextInt(p_226339_1_ / 2);
               } else {
                  l += random.nextInt(p_226339_1_ / 2);
               }
            }

            BlockPos blockpos3 = new BlockPos((double)j + p_226339_0_.getX(), (double)k + p_226339_0_.getY(), (double)l + p_226339_0_.getZ());
            if (blockpos3.getY() >= 0 && blockpos3.getY() <= p_226339_0_.level.getMaxBuildHeight() && (!flag || p_226339_0_.isWithinRestriction(blockpos3)) && (!p_226339_12_ || pathnavigator.isStableDestination(blockpos3))) {
               if (p_226339_9_) {
                  blockpos3 = moveUpToAboveSolid(blockpos3, random.nextInt(p_226339_10_ + 1) + p_226339_11_, p_226339_0_.level.getMaxBuildHeight(), (p_226341_1_) -> {
                     return p_226339_0_.level.getBlockState(p_226341_1_).getMaterial().isSolid();
                  });
               }

               if (p_226339_5_ || !p_226339_0_.level.getFluidState(blockpos3).is(FluidTags.WATER)) {
                  PathNodeType pathnodetype = WalkNodeProcessor.getBlockPathTypeStatic(p_226339_0_.level, blockpos3.mutable());
                  if (p_226339_0_.getPathfindingMalus(pathnodetype) == 0.0F) {
                     double d1 = p_226339_8_.applyAsDouble(blockpos3);
                     if (d1 > d0) {
                        d0 = d1;
                        blockpos = blockpos3;
                        flag1 = true;
                     }
                  }
               }
            }
         }
      }

      return flag1 ? Vector3d.atBottomCenterOf(blockpos) : null;
   }

   @Nullable
   private static BlockPos getRandomDelta(Random p_226343_0_, int p_226343_1_, int p_226343_2_, int p_226343_3_, @Nullable Vector3d p_226343_4_, double p_226343_5_) {
      if (p_226343_4_ != null && !(p_226343_5_ >= Math.PI)) {
         double d3 = MathHelper.atan2(p_226343_4_.z, p_226343_4_.x) - (double)((float)Math.PI / 2F);
         double d4 = d3 + (double)(2.0F * p_226343_0_.nextFloat() - 1.0F) * p_226343_5_;
         double d0 = Math.sqrt(p_226343_0_.nextDouble()) * (double)MathHelper.SQRT_OF_TWO * (double)p_226343_1_;
         double d1 = -d0 * Math.sin(d4);
         double d2 = d0 * Math.cos(d4);
         if (!(Math.abs(d1) > (double)p_226343_1_) && !(Math.abs(d2) > (double)p_226343_1_)) {
            int l = p_226343_0_.nextInt(2 * p_226343_2_ + 1) - p_226343_2_ + p_226343_3_;
            return new BlockPos(d1, (double)l, d2);
         } else {
            return null;
         }
      } else {
         int i = p_226343_0_.nextInt(2 * p_226343_1_ + 1) - p_226343_1_;
         int j = p_226343_0_.nextInt(2 * p_226343_2_ + 1) - p_226343_2_ + p_226343_3_;
         int k = p_226343_0_.nextInt(2 * p_226343_1_ + 1) - p_226343_1_;
         return new BlockPos(i, j, k);
      }
   }

   static BlockPos moveUpToAboveSolid(BlockPos p_226342_0_, int p_226342_1_, int p_226342_2_, Predicate<BlockPos> p_226342_3_) {
      if (p_226342_1_ < 0) {
         throw new IllegalArgumentException("aboveSolidAmount was " + p_226342_1_ + ", expected >= 0");
      } else if (!p_226342_3_.test(p_226342_0_)) {
         return p_226342_0_;
      } else {
         BlockPos blockpos;
         for(blockpos = p_226342_0_.above(); blockpos.getY() < p_226342_2_ && p_226342_3_.test(blockpos); blockpos = blockpos.above()) {
         }

         BlockPos blockpos1;
         BlockPos blockpos2;
         for(blockpos1 = blockpos; blockpos1.getY() < p_226342_2_ && blockpos1.getY() - blockpos.getY() < p_226342_1_; blockpos1 = blockpos2) {
            blockpos2 = blockpos1.above();
            if (p_226342_3_.test(blockpos2)) {
               break;
            }
         }

         return blockpos1;
      }
   }
}
