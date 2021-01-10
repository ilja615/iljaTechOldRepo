package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;

public class BigMushroomFeatureConfig implements IFeatureConfig {
   public static final Codec<BigMushroomFeatureConfig> CODEC = RecordCodecBuilder.create((builder) -> {
      return builder.group(BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter((config) -> {
         return config.capProvider;
      }), BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter((config) -> {
         return config.stemProvider;
      }), Codec.INT.fieldOf("foliage_radius").orElse(2).forGetter((config) -> {
         return config.foliageRadius;
      })).apply(builder, BigMushroomFeatureConfig::new);
   });
   public final BlockStateProvider capProvider;
   public final BlockStateProvider stemProvider;
   public final int foliageRadius;

   public BigMushroomFeatureConfig(BlockStateProvider capProvider, BlockStateProvider stemProvider, int foliageRadius) {
      this.capProvider = capProvider;
      this.stemProvider = stemProvider;
      this.foliageRadius = foliageRadius;
   }
}
