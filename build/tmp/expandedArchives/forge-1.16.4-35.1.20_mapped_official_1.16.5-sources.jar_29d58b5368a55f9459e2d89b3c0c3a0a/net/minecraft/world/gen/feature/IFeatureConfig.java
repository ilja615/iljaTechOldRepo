package net.minecraft.world.gen.feature;

import java.util.stream.Stream;

public interface IFeatureConfig {
   NoFeatureConfig NONE = NoFeatureConfig.INSTANCE;

   default Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.empty();
   }
}
