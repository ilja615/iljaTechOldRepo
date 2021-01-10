package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseDependant implements IPlacementConfig {
   public static final Codec<NoiseDependant> CODEC = RecordCodecBuilder.create((p_236552_0_) -> {
      return p_236552_0_.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((config) -> {
         return config.noiseLevel;
      }), Codec.INT.fieldOf("below_noise").forGetter((config) -> {
         return config.belowNoise;
      }), Codec.INT.fieldOf("above_noise").forGetter((config) -> {
         return config.aboveNoise;
      })).apply(p_236552_0_, NoiseDependant::new);
   });
   public final double noiseLevel;
   public final int belowNoise;
   public final int aboveNoise;

   public NoiseDependant(double noiseLevel, int belowNoise, int aboveNoise) {
      this.noiseLevel = noiseLevel;
      this.belowNoise = belowNoise;
      this.aboveNoise = aboveNoise;
   }
}
