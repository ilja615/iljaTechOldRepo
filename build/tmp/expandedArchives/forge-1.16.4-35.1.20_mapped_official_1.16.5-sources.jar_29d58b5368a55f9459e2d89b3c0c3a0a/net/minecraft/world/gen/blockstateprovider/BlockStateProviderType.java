package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> extends net.minecraftforge.registries.ForgeRegistryEntry<BlockStateProviderType<?>> {
   public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleBlockStateProvider.CODEC);
   public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WeightedBlockStateProvider.CODEC);
   public static final BlockStateProviderType<PlainFlowerBlockStateProvider> PLAIN_FLOWER_PROVIDER = register("plain_flower_provider", PlainFlowerBlockStateProvider.CODEC);
   public static final BlockStateProviderType<ForestFlowerBlockStateProvider> FOREST_FLOWER_PROVIDER = register("forest_flower_provider", ForestFlowerBlockStateProvider.CODEC);
   public static final BlockStateProviderType<AxisRotatingBlockStateProvider> ROTATED_BLOCK_PROVIDER = register("rotated_block_provider", AxisRotatingBlockStateProvider.CODEC);
   private final Codec<P> codec;

   private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String p_236800_0_, Codec<P> p_236800_1_) {
      return Registry.register(Registry.BLOCKSTATE_PROVIDER_TYPES, p_236800_0_, new BlockStateProviderType<>(p_236800_1_));
   }

   public BlockStateProviderType(Codec<P> p_i232041_1_) {
      this.codec = p_i232041_1_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}
