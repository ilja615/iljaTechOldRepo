package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class ForestFlowerBlockStateProvider extends BlockStateProvider {
   public static final Codec<ForestFlowerBlockStateProvider> CODEC;
   private static final BlockState[] FLOWERS = new BlockState[]{Blocks.DANDELION.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState()};
   public static final ForestFlowerBlockStateProvider INSTANCE = new ForestFlowerBlockStateProvider();

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.FOREST_FLOWER_PROVIDER;
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      double d0 = MathHelper.clamp((1.0D + Biome.BIOME_INFO_NOISE.getValue((double)p_225574_2_.getX() / 48.0D, (double)p_225574_2_.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
      return FLOWERS[(int)(d0 * (double)FLOWERS.length)];
   }

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
