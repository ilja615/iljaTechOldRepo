package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class IceAndSnowFeature extends Feature<NoFeatureConfig> {
   public IceAndSnowFeature(Codec<NoFeatureConfig> p_i231993_1_) {
      super(p_i231993_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = p_241855_4_.getX() + i;
            int l = p_241855_4_.getZ() + j;
            int i1 = p_241855_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, k, l);
            blockpos$mutable.set(k, i1, l);
            blockpos$mutable1.set(blockpos$mutable).move(Direction.DOWN, 1);
            Biome biome = p_241855_1_.getBiome(blockpos$mutable);
            if (biome.shouldFreeze(p_241855_1_, blockpos$mutable1, false)) {
               p_241855_1_.setBlock(blockpos$mutable1, Blocks.ICE.defaultBlockState(), 2);
            }

            if (biome.shouldSnow(p_241855_1_, blockpos$mutable)) {
               p_241855_1_.setBlock(blockpos$mutable, Blocks.SNOW.defaultBlockState(), 2);
               BlockState blockstate = p_241855_1_.getBlockState(blockpos$mutable1);
               if (blockstate.hasProperty(SnowyDirtBlock.SNOWY)) {
                  p_241855_1_.setBlock(blockpos$mutable1, blockstate.setValue(SnowyDirtBlock.SNOWY, Boolean.valueOf(true)), 2);
               }
            }
         }
      }

      return true;
   }
}
