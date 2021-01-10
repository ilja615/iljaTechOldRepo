package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TopSolidWithNoiseConfig implements IPlacementConfig {
   public static final Codec<TopSolidWithNoiseConfig> CODEC = RecordCodecBuilder.create((p_236980_0_) -> {
      return p_236980_0_.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((config) -> {
         return config.noiseToCountRatio;
      }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((config) -> {
         return config.noiseFactor;
      }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((config) -> {
         return config.noiseOffset;
      })).apply(p_236980_0_, TopSolidWithNoiseConfig::new);
   });
   public final int noiseToCountRatio;
   public final double noiseFactor;
   public final double noiseOffset;

   public TopSolidWithNoiseConfig(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
      this.noiseToCountRatio = noiseToCountRatio;
      this.noiseFactor = noiseFactor;
      this.noiseOffset = noiseOffset;
   }
}
