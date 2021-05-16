package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DecoratedPlacementConfig implements IPlacementConfig {
   public static final Codec<DecoratedPlacementConfig> CODEC = RecordCodecBuilder.create((p_242887_0_) -> {
      return p_242887_0_.group(ConfiguredPlacement.CODEC.fieldOf("outer").forGetter(DecoratedPlacementConfig::outer), ConfiguredPlacement.CODEC.fieldOf("inner").forGetter(DecoratedPlacementConfig::inner)).apply(p_242887_0_, DecoratedPlacementConfig::new);
   });
   private final ConfiguredPlacement<?> outer;
   private final ConfiguredPlacement<?> inner;

   public DecoratedPlacementConfig(ConfiguredPlacement<?> p_i242020_1_, ConfiguredPlacement<?> p_i242020_2_) {
      this.outer = p_i242020_1_;
      this.inner = p_i242020_2_;
   }

   public ConfiguredPlacement<?> outer() {
      return this.outer;
   }

   public ConfiguredPlacement<?> inner() {
      return this.inner;
   }
}
