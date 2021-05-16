package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class NoExposedOreFeature extends Feature<OreFeatureConfig> {
   NoExposedOreFeature(Codec<OreFeatureConfig> p_i231974_1_) {
      super(p_i231974_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, OreFeatureConfig p_241855_5_) {
      int i = p_241855_3_.nextInt(p_241855_5_.size + 1);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j = 0; j < i; ++j) {
         this.offsetTargetPos(blockpos$mutable, p_241855_3_, p_241855_4_, Math.min(j, 7));
         if (p_241855_5_.target.test(p_241855_1_.getBlockState(blockpos$mutable), p_241855_3_) && !this.isFacingAir(p_241855_1_, blockpos$mutable)) {
            p_241855_1_.setBlock(blockpos$mutable, p_241855_5_.state, 2);
         }
      }

      return true;
   }

   private void offsetTargetPos(BlockPos.Mutable p_236327_1_, Random p_236327_2_, BlockPos p_236327_3_, int p_236327_4_) {
      int i = this.getRandomPlacementInOneAxisRelativeToOrigin(p_236327_2_, p_236327_4_);
      int j = this.getRandomPlacementInOneAxisRelativeToOrigin(p_236327_2_, p_236327_4_);
      int k = this.getRandomPlacementInOneAxisRelativeToOrigin(p_236327_2_, p_236327_4_);
      p_236327_1_.setWithOffset(p_236327_3_, i, j, k);
   }

   private int getRandomPlacementInOneAxisRelativeToOrigin(Random p_236328_1_, int p_236328_2_) {
      return Math.round((p_236328_1_.nextFloat() - p_236328_1_.nextFloat()) * (float)p_236328_2_);
   }

   private boolean isFacingAir(IWorld p_236326_1_, BlockPos p_236326_2_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Direction direction : Direction.values()) {
         blockpos$mutable.setWithOffset(p_236326_2_, direction);
         if (p_236326_1_.getBlockState(blockpos$mutable).isAir()) {
            return true;
         }
      }

      return false;
   }
}
