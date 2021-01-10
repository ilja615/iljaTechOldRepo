package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.carver.ICarverConfig;

public class ProbabilityConfig implements ICarverConfig, IFeatureConfig {
   public static final Codec<ProbabilityConfig> CODEC = RecordCodecBuilder.create((builder) -> {
      return builder.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((config) -> {
         return config.probability;
      })).apply(builder, ProbabilityConfig::new);
   });
   public final float probability;

   public ProbabilityConfig(float probability) {
      this.probability = probability;
   }
}
