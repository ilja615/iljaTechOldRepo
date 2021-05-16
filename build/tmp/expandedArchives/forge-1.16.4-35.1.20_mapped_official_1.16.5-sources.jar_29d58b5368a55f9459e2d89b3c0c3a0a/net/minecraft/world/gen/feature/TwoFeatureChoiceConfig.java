package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public static final Codec<TwoFeatureChoiceConfig> CODEC = RecordCodecBuilder.create((p_236581_0_) -> {
      return p_236581_0_.group(ConfiguredFeature.CODEC.fieldOf("feature_true").forGetter((p_236582_0_) -> {
         return p_236582_0_.featureTrue;
      }), ConfiguredFeature.CODEC.fieldOf("feature_false").forGetter((p_236580_0_) -> {
         return p_236580_0_.featureFalse;
      })).apply(p_236581_0_, TwoFeatureChoiceConfig::new);
   });
   public final Supplier<ConfiguredFeature<?, ?>> featureTrue;
   public final Supplier<ConfiguredFeature<?, ?>> featureFalse;

   public TwoFeatureChoiceConfig(Supplier<ConfiguredFeature<?, ?>> p_i241990_1_, Supplier<ConfiguredFeature<?, ?>> p_i241990_2_) {
      this.featureTrue = p_i241990_1_;
      this.featureFalse = p_i241990_2_;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(this.featureTrue.get().getFeatures(), this.featureFalse.get().getFeatures());
   }
}
