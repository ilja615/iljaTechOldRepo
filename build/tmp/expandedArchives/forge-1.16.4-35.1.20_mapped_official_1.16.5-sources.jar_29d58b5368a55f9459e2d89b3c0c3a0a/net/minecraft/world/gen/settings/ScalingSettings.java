package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ScalingSettings {
   private static final Codec<Double> SCALE_RANGE = Codec.doubleRange(0.001D, 1000.0D);
   public static final Codec<ScalingSettings> CODEC = RecordCodecBuilder.create((p_236152_0_) -> {
      return p_236152_0_.group(SCALE_RANGE.fieldOf("xz_scale").forGetter(ScalingSettings::xzScale), SCALE_RANGE.fieldOf("y_scale").forGetter(ScalingSettings::yScale), SCALE_RANGE.fieldOf("xz_factor").forGetter(ScalingSettings::xzFactor), SCALE_RANGE.fieldOf("y_factor").forGetter(ScalingSettings::yFactor)).apply(p_236152_0_, ScalingSettings::new);
   });
   private final double xzScale;
   private final double yScale;
   private final double xzFactor;
   private final double yFactor;

   public ScalingSettings(double p_i231909_1_, double p_i231909_3_, double p_i231909_5_, double p_i231909_7_) {
      this.xzScale = p_i231909_1_;
      this.yScale = p_i231909_3_;
      this.xzFactor = p_i231909_5_;
      this.yFactor = p_i231909_7_;
   }

   public double xzScale() {
      return this.xzScale;
   }

   public double yScale() {
      return this.yScale;
   }

   public double xzFactor() {
      return this.xzFactor;
   }

   public double yFactor() {
      return this.yFactor;
   }
}
