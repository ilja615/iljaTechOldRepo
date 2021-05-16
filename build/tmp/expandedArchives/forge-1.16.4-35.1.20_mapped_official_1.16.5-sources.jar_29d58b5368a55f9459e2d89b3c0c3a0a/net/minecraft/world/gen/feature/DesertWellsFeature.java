package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class DesertWellsFeature extends Feature<NoFeatureConfig> {
   private static final BlockStateMatcher IS_SAND = BlockStateMatcher.forBlock(Blocks.SAND);
   private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
   private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
   private final BlockState water = Blocks.WATER.defaultBlockState();

   public DesertWellsFeature(Codec<NoFeatureConfig> p_i231948_1_) {
      super(p_i231948_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
      for(p_241855_4_ = p_241855_4_.above(); p_241855_1_.isEmptyBlock(p_241855_4_) && p_241855_4_.getY() > 2; p_241855_4_ = p_241855_4_.below()) {
      }

      if (!IS_SAND.test(p_241855_1_.getBlockState(p_241855_4_))) {
         return false;
      } else {
         for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
               if (p_241855_1_.isEmptyBlock(p_241855_4_.offset(i, -1, j)) && p_241855_1_.isEmptyBlock(p_241855_4_.offset(i, -2, j))) {
                  return false;
               }
            }
         }

         for(int l = -1; l <= 0; ++l) {
            for(int l1 = -2; l1 <= 2; ++l1) {
               for(int k = -2; k <= 2; ++k) {
                  p_241855_1_.setBlock(p_241855_4_.offset(l1, l, k), this.sandstone, 2);
               }
            }
         }

         p_241855_1_.setBlock(p_241855_4_, this.water, 2);

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            p_241855_1_.setBlock(p_241855_4_.relative(direction), this.water, 2);
         }

         for(int i1 = -2; i1 <= 2; ++i1) {
            for(int i2 = -2; i2 <= 2; ++i2) {
               if (i1 == -2 || i1 == 2 || i2 == -2 || i2 == 2) {
                  p_241855_1_.setBlock(p_241855_4_.offset(i1, 1, i2), this.sandstone, 2);
               }
            }
         }

         p_241855_1_.setBlock(p_241855_4_.offset(2, 1, 0), this.sandSlab, 2);
         p_241855_1_.setBlock(p_241855_4_.offset(-2, 1, 0), this.sandSlab, 2);
         p_241855_1_.setBlock(p_241855_4_.offset(0, 1, 2), this.sandSlab, 2);
         p_241855_1_.setBlock(p_241855_4_.offset(0, 1, -2), this.sandSlab, 2);

         for(int j1 = -1; j1 <= 1; ++j1) {
            for(int j2 = -1; j2 <= 1; ++j2) {
               if (j1 == 0 && j2 == 0) {
                  p_241855_1_.setBlock(p_241855_4_.offset(j1, 4, j2), this.sandstone, 2);
               } else {
                  p_241855_1_.setBlock(p_241855_4_.offset(j1, 4, j2), this.sandSlab, 2);
               }
            }
         }

         for(int k1 = 1; k1 <= 3; ++k1) {
            p_241855_1_.setBlock(p_241855_4_.offset(-1, k1, -1), this.sandstone, 2);
            p_241855_1_.setBlock(p_241855_4_.offset(-1, k1, 1), this.sandstone, 2);
            p_241855_1_.setBlock(p_241855_4_.offset(1, k1, -1), this.sandstone, 2);
            p_241855_1_.setBlock(p_241855_4_.offset(1, k1, 1), this.sandstone, 2);
         }

         return true;
      }
   }
}
