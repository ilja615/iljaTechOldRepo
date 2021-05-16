package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placement.ConfiguredPlacement;

public class DecoratedFeatureConfig implements IFeatureConfig {
   public static final Codec<DecoratedFeatureConfig> CODEC = RecordCodecBuilder.create((p_236493_0_) -> {
      return p_236493_0_.group(ConfiguredFeature.CODEC.fieldOf("feature").forGetter((p_236494_0_) -> {
         return p_236494_0_.feature;
      }), ConfiguredPlacement.CODEC.fieldOf("decorator").forGetter((p_236492_0_) -> {
         return p_236492_0_.decorator;
      })).apply(p_236493_0_, DecoratedFeatureConfig::new);
   });
   public final Supplier<ConfiguredFeature<?, ?>> feature;
   public final ConfiguredPlacement<?> decorator;

   public DecoratedFeatureConfig(Supplier<ConfiguredFeature<?, ?>> p_i241984_1_, ConfiguredPlacement<?> p_i241984_2_) {
      this.feature = p_i241984_1_;
      this.decorator = p_i241984_2_;
   }

   public String toString() {
      return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this.feature.get().feature()), this.decorator);
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return this.feature.get().getFeatures();
   }
}
