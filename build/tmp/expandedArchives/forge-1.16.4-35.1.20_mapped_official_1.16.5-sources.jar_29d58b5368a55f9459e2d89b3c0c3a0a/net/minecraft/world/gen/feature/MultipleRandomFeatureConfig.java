package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MultipleRandomFeatureConfig implements IFeatureConfig {
   public static final Codec<MultipleRandomFeatureConfig> CODEC = RecordCodecBuilder.create((p_236585_0_) -> {
      return p_236585_0_.apply2(MultipleRandomFeatureConfig::new, ConfiguredRandomFeatureList.CODEC.listOf().fieldOf("features").forGetter((p_236586_0_) -> {
         return p_236586_0_.features;
      }), ConfiguredFeature.CODEC.fieldOf("default").forGetter((p_236584_0_) -> {
         return p_236584_0_.defaultFeature;
      }));
   });
   public final List<ConfiguredRandomFeatureList> features;
   public final Supplier<ConfiguredFeature<?, ?>> defaultFeature;

   public MultipleRandomFeatureConfig(List<ConfiguredRandomFeatureList> p_i51455_1_, ConfiguredFeature<?, ?> p_i51455_2_) {
      this(p_i51455_1_, () -> {
         return p_i51455_2_;
      });
   }

   private MultipleRandomFeatureConfig(List<ConfiguredRandomFeatureList> p_i241991_1_, Supplier<ConfiguredFeature<?, ?>> p_i241991_2_) {
      this.features = p_i241991_1_;
      this.defaultFeature = p_i241991_2_;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(this.features.stream().flatMap((p_242812_0_) -> {
         return p_242812_0_.feature.get().getFeatures();
      }), this.defaultFeature.get().getFeatures());
   }
}
