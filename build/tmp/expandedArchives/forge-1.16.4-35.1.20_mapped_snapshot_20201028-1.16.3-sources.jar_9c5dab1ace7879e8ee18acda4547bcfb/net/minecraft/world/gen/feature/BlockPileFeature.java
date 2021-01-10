package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class BlockPileFeature extends Feature<BlockStateProvidingFeatureConfig> {
   public BlockPileFeature(Codec<BlockStateProvidingFeatureConfig> p_i231932_1_) {
      super(p_i231932_1_);
   }

   public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateProvidingFeatureConfig config) {
      if (pos.getY() < 5) {
         return false;
      } else {
         int i = 2 + rand.nextInt(2);
         int j = 2 + rand.nextInt(2);

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-i, 0, -j), pos.add(i, 1, j))) {
            int k = pos.getX() - blockpos.getX();
            int l = pos.getZ() - blockpos.getZ();
            if ((float)(k * k + l * l) <= rand.nextFloat() * 10.0F - rand.nextFloat() * 6.0F) {
               this.func_227225_a_(reader, blockpos, rand, config);
            } else if ((double)rand.nextFloat() < 0.031D) {
               this.func_227225_a_(reader, blockpos, rand, config);
            }
         }

         return true;
      }
   }

   private boolean canPlaceOn(IWorld worldIn, BlockPos pos, Random random) {
      BlockPos blockpos = pos.down();
      BlockState blockstate = worldIn.getBlockState(blockpos);
      return blockstate.isIn(Blocks.GRASS_PATH) ? random.nextBoolean() : blockstate.isSolidSide(worldIn, blockpos, Direction.UP);
   }

   private void func_227225_a_(IWorld p_227225_1_, BlockPos p_227225_2_, Random p_227225_3_, BlockStateProvidingFeatureConfig p_227225_4_) {
      if (p_227225_1_.isAirBlock(p_227225_2_) && this.canPlaceOn(p_227225_1_, p_227225_2_, p_227225_3_)) {
         p_227225_1_.setBlockState(p_227225_2_, p_227225_4_.field_227268_a_.getBlockState(p_227225_3_, p_227225_2_), 4);
      }

   }
}
