package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class TreeFeature extends Feature<BaseTreeFeatureConfig> {
   public TreeFeature(Codec<BaseTreeFeatureConfig> p_i231999_1_) {
      super(p_i231999_1_);
   }

   public static boolean isFree(IWorldGenerationBaseReader p_236410_0_, BlockPos p_236410_1_) {
      return validTreePos(p_236410_0_, p_236410_1_) || p_236410_0_.isStateAtPosition(p_236410_1_, (p_236417_0_) -> {
         return p_236417_0_.is(BlockTags.LOGS);
      });
   }

   private static boolean isVine(IWorldGenerationBaseReader p_236414_0_, BlockPos p_236414_1_) {
      return p_236414_0_.isStateAtPosition(p_236414_1_, (p_236415_0_) -> {
         return p_236415_0_.is(Blocks.VINE);
      });
   }

   private static boolean isBlockWater(IWorldGenerationBaseReader p_236416_0_, BlockPos p_236416_1_) {
      return p_236416_0_.isStateAtPosition(p_236416_1_, (p_236413_0_) -> {
         return p_236413_0_.is(Blocks.WATER);
      });
   }

   public static boolean isAirOrLeaves(IWorldGenerationBaseReader p_236412_0_, BlockPos p_236412_1_) {
      return p_236412_0_.isStateAtPosition(p_236412_1_, (p_236411_0_) -> {
         return p_236411_0_.isAir() || p_236411_0_.is(BlockTags.LEAVES);
      });
   }

   private static boolean isGrassOrDirtOrFarmland(IWorldGenerationBaseReader p_236418_0_, BlockPos p_236418_1_) {
      return p_236418_0_.isStateAtPosition(p_236418_1_, (p_236409_0_) -> {
         Block block = p_236409_0_.getBlock();
         return isDirt(block) || block == Blocks.FARMLAND;
      });
   }

   private static boolean isReplaceablePlant(IWorldGenerationBaseReader p_236419_0_, BlockPos p_236419_1_) {
      return p_236419_0_.isStateAtPosition(p_236419_1_, (p_236406_0_) -> {
         Material material = p_236406_0_.getMaterial();
         return material == Material.REPLACEABLE_PLANT;
      });
   }

   public static void setBlockKnownShape(IWorldWriter p_236408_0_, BlockPos p_236408_1_, BlockState p_236408_2_) {
      p_236408_0_.setBlock(p_236408_1_, p_236408_2_, 19);
   }

   public static boolean validTreePos(IWorldGenerationBaseReader p_236404_0_, BlockPos p_236404_1_) {
      return isAirOrLeaves(p_236404_0_, p_236404_1_) || isReplaceablePlant(p_236404_0_, p_236404_1_) || isBlockWater(p_236404_0_, p_236404_1_);
   }

   private boolean doPlace(IWorldGenerationReader p_225557_1_, Random p_225557_2_, BlockPos p_225557_3_, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox p_225557_6_, BaseTreeFeatureConfig p_225557_7_) {
      int i = p_225557_7_.trunkPlacer.getTreeHeight(p_225557_2_);
      int j = p_225557_7_.foliagePlacer.foliageHeight(p_225557_2_, i, p_225557_7_);
      int k = i - j;
      int l = p_225557_7_.foliagePlacer.foliageRadius(p_225557_2_, k);
      BlockPos blockpos;
      if (!p_225557_7_.fromSapling) {
         int i1 = p_225557_1_.getHeightmapPos(Heightmap.Type.OCEAN_FLOOR, p_225557_3_).getY();
         int j1 = p_225557_1_.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, p_225557_3_).getY();
         if (j1 - i1 > p_225557_7_.maxWaterDepth) {
            return false;
         }

         int k1;
         if (p_225557_7_.heightmap == Heightmap.Type.OCEAN_FLOOR) {
            k1 = i1;
         } else if (p_225557_7_.heightmap == Heightmap.Type.WORLD_SURFACE) {
            k1 = j1;
         } else {
            k1 = p_225557_1_.getHeightmapPos(p_225557_7_.heightmap, p_225557_3_).getY();
         }

         blockpos = new BlockPos(p_225557_3_.getX(), k1, p_225557_3_.getZ());
      } else {
         blockpos = p_225557_3_;
      }

      if (blockpos.getY() >= 1 && blockpos.getY() + i + 1 <= 256) {
         if (!isGrassOrDirtOrFarmland(p_225557_1_, blockpos.below())) {
            return false;
         } else {
            OptionalInt optionalint = p_225557_7_.minimumSize.minClippedHeight();
            int l1 = this.getMaxFreeTreeHeight(p_225557_1_, i, blockpos, p_225557_7_);
            if (l1 >= i || optionalint.isPresent() && l1 >= optionalint.getAsInt()) {
               List<FoliagePlacer.Foliage> list = p_225557_7_.trunkPlacer.placeTrunk(p_225557_1_, p_225557_2_, l1, blockpos, p_225557_4_, p_225557_6_, p_225557_7_);
               list.forEach((p_236407_8_) -> {
                  p_225557_7_.foliagePlacer.createFoliage(p_225557_1_, p_225557_2_, p_225557_7_, l1, p_236407_8_, j, l, p_225557_5_, p_225557_6_);
               });
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private int getMaxFreeTreeHeight(IWorldGenerationBaseReader p_241521_1_, int p_241521_2_, BlockPos p_241521_3_, BaseTreeFeatureConfig p_241521_4_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i <= p_241521_2_ + 1; ++i) {
         int j = p_241521_4_.minimumSize.getSizeAtHeight(p_241521_2_, i);

         for(int k = -j; k <= j; ++k) {
            for(int l = -j; l <= j; ++l) {
               blockpos$mutable.setWithOffset(p_241521_3_, k, i, l);
               if (!isFree(p_241521_1_, blockpos$mutable) || !p_241521_4_.ignoreVines && isVine(p_241521_1_, blockpos$mutable)) {
                  return i - 2;
               }
            }
         }
      }

      return p_241521_2_;
   }

   protected void setBlock(IWorldWriter p_230367_1_, BlockPos p_230367_2_, BlockState p_230367_3_) {
      setBlockKnownShape(p_230367_1_, p_230367_2_, p_230367_3_);
   }

   public final boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BaseTreeFeatureConfig p_241855_5_) {
      Set<BlockPos> set = Sets.newHashSet();
      Set<BlockPos> set1 = Sets.newHashSet();
      Set<BlockPos> set2 = Sets.newHashSet();
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.getUnknownBox();
      boolean flag = this.doPlace(p_241855_1_, p_241855_3_, p_241855_4_, set, set1, mutableboundingbox, p_241855_5_);
      if (mutableboundingbox.x0 <= mutableboundingbox.x1 && flag && !set.isEmpty()) {
         if (!p_241855_5_.decorators.isEmpty()) {
            List<BlockPos> list = Lists.newArrayList(set);
            List<BlockPos> list1 = Lists.newArrayList(set1);
            list.sort(Comparator.comparingInt(Vector3i::getY));
            list1.sort(Comparator.comparingInt(Vector3i::getY));
            p_241855_5_.decorators.forEach((p_236405_6_) -> {
               p_236405_6_.place(p_241855_1_, p_241855_3_, list, list1, set2, mutableboundingbox);
            });
         }

         VoxelShapePart voxelshapepart = this.updateLeaves(p_241855_1_, mutableboundingbox, set, set2);
         Template.updateShapeAtEdge(p_241855_1_, 3, voxelshapepart, mutableboundingbox.x0, mutableboundingbox.y0, mutableboundingbox.z0);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShapePart updateLeaves(IWorld p_236403_1_, MutableBoundingBox p_236403_2_, Set<BlockPos> p_236403_3_, Set<BlockPos> p_236403_4_) {
      List<Set<BlockPos>> list = Lists.newArrayList();
      VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(p_236403_2_.getXSpan(), p_236403_2_.getYSpan(), p_236403_2_.getZSpan());
      int i = 6;

      for(int j = 0; j < 6; ++j) {
         list.add(Sets.newHashSet());
      }

      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(BlockPos blockpos : Lists.newArrayList(p_236403_4_)) {
         if (p_236403_2_.isInside(blockpos)) {
            voxelshapepart.setFull(blockpos.getX() - p_236403_2_.x0, blockpos.getY() - p_236403_2_.y0, blockpos.getZ() - p_236403_2_.z0, true, true);
         }
      }

      for(BlockPos blockpos1 : Lists.newArrayList(p_236403_3_)) {
         if (p_236403_2_.isInside(blockpos1)) {
            voxelshapepart.setFull(blockpos1.getX() - p_236403_2_.x0, blockpos1.getY() - p_236403_2_.y0, blockpos1.getZ() - p_236403_2_.z0, true, true);
         }

         for(Direction direction : Direction.values()) {
            blockpos$mutable.setWithOffset(blockpos1, direction);
            if (!p_236403_3_.contains(blockpos$mutable)) {
               BlockState blockstate = p_236403_1_.getBlockState(blockpos$mutable);
               if (blockstate.hasProperty(BlockStateProperties.DISTANCE)) {
                  list.get(0).add(blockpos$mutable.immutable());
                  setBlockKnownShape(p_236403_1_, blockpos$mutable, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(1)));
                  if (p_236403_2_.isInside(blockpos$mutable)) {
                     voxelshapepart.setFull(blockpos$mutable.getX() - p_236403_2_.x0, blockpos$mutable.getY() - p_236403_2_.y0, blockpos$mutable.getZ() - p_236403_2_.z0, true, true);
                  }
               }
            }
         }
      }

      for(int l = 1; l < 6; ++l) {
         Set<BlockPos> set = list.get(l - 1);
         Set<BlockPos> set1 = list.get(l);

         for(BlockPos blockpos2 : set) {
            if (p_236403_2_.isInside(blockpos2)) {
               voxelshapepart.setFull(blockpos2.getX() - p_236403_2_.x0, blockpos2.getY() - p_236403_2_.y0, blockpos2.getZ() - p_236403_2_.z0, true, true);
            }

            for(Direction direction1 : Direction.values()) {
               blockpos$mutable.setWithOffset(blockpos2, direction1);
               if (!set.contains(blockpos$mutable) && !set1.contains(blockpos$mutable)) {
                  BlockState blockstate1 = p_236403_1_.getBlockState(blockpos$mutable);
                  if (blockstate1.hasProperty(BlockStateProperties.DISTANCE)) {
                     int k = blockstate1.getValue(BlockStateProperties.DISTANCE);
                     if (k > l + 1) {
                        BlockState blockstate2 = blockstate1.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(l + 1));
                        setBlockKnownShape(p_236403_1_, blockpos$mutable, blockstate2);
                        if (p_236403_2_.isInside(blockpos$mutable)) {
                           voxelshapepart.setFull(blockpos$mutable.getX() - p_236403_2_.x0, blockpos$mutable.getY() - p_236403_2_.y0, blockpos$mutable.getZ() - p_236403_2_.z0, true, true);
                        }

                        set1.add(blockpos$mutable.immutable());
                     }
                  }
               }
            }
         }
      }

      return voxelshapepart;
   }
}
