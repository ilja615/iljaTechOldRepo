package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SingleRandomFeature implements IFeatureConfig {
   public static final Codec<SingleRandomFeature> CODEC = ConfiguredFeature.LIST_CODEC.fieldOf("features").xmap(SingleRandomFeature::new, (p_236643_0_) -> {
      return p_236643_0_.features;
   }).codec();
   public final List<Supplier<ConfiguredFeature<?, ?>>> features;

   public SingleRandomFeature(List<Supplier<ConfiguredFeature<?, ?>>> p_i51437_1_) {
      this.features = p_i51437_1_;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return this.features.stream().flatMap((p_242826_0_) -> {
         return p_242826_0_.get().getFeatures();
      });
   }
}
