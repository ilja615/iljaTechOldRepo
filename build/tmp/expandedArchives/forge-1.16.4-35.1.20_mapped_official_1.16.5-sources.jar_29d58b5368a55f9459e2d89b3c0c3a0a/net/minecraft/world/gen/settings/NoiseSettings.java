package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSettings {
   public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create((p_236170_0_) -> {
      return p_236170_0_.group(Codec.intRange(0, 256).fieldOf("height").forGetter(NoiseSettings::height), ScalingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::noiseSamplingSettings), SlideSettings.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::topSlideSettings), SlideSettings.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::bottomSlideSettings), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical), Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseSettings::densityFactor), Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseSettings::densityOffset), Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseSettings::useSimplexSurfaceNoise), Codec.BOOL.optionalFieldOf("random_density_offset", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseSettings::randomDensityOffset), Codec.BOOL.optionalFieldOf("island_noise_override", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseSettings::islandNoiseOverride), Codec.BOOL.optionalFieldOf("amplified", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseSettings::isAmplified)).apply(p_236170_0_, NoiseSettings::new);
   });
   private final int height;
   private final ScalingSettings noiseSamplingSettings;
   private final SlideSettings topSlideSettings;
   private final SlideSettings bottomSlideSettings;
   private final int noiseSizeHorizontal;
   private final int noiseSizeVertical;
   private final double densityFactor;
   private final double densityOffset;
   private final boolean useSimplexSurfaceNoise;
   private final boolean randomDensityOffset;
   private final boolean islandNoiseOverride;
   private final boolean isAmplified;

   public NoiseSettings(int p_i231910_1_, ScalingSettings p_i231910_2_, SlideSettings p_i231910_3_, SlideSettings p_i231910_4_, int p_i231910_5_, int p_i231910_6_, double p_i231910_7_, double p_i231910_9_, boolean p_i231910_11_, boolean p_i231910_12_, boolean p_i231910_13_, boolean p_i231910_14_) {
      this.height = p_i231910_1_;
      this.noiseSamplingSettings = p_i231910_2_;
      this.topSlideSettings = p_i231910_3_;
      this.bottomSlideSettings = p_i231910_4_;
      this.noiseSizeHorizontal = p_i231910_5_;
      this.noiseSizeVertical = p_i231910_6_;
      this.densityFactor = p_i231910_7_;
      this.densityOffset = p_i231910_9_;
      this.useSimplexSurfaceNoise = p_i231910_11_;
      this.randomDensityOffset = p_i231910_12_;
      this.islandNoiseOverride = p_i231910_13_;
      this.isAmplified = p_i231910_14_;
   }

   public int height() {
      return this.height;
   }

   public ScalingSettings noiseSamplingSettings() {
      return this.noiseSamplingSettings;
   }

   public SlideSettings topSlideSettings() {
      return this.topSlideSettings;
   }

   public SlideSettings bottomSlideSettings() {
      return this.bottomSlideSettings;
   }

   public int noiseSizeHorizontal() {
      return this.noiseSizeHorizontal;
   }

   public int noiseSizeVertical() {
      return this.noiseSizeVertical;
   }

   public double densityFactor() {
      return this.densityFactor;
   }

   public double densityOffset() {
      return this.densityOffset;
   }

   @Deprecated
   public boolean useSimplexSurfaceNoise() {
      return this.useSimplexSurfaceNoise;
   }

   @Deprecated
   public boolean randomDensityOffset() {
      return this.randomDensityOffset;
   }

   @Deprecated
   public boolean islandNoiseOverride() {
      return this.islandNoiseOverride;
   }

   @Deprecated
   public boolean isAmplified() {
      return this.isAmplified;
   }
}
