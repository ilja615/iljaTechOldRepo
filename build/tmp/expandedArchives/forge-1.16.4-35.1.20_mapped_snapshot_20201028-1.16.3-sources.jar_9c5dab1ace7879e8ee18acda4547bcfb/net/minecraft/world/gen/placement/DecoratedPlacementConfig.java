package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DecoratedPlacementConfig implements IPlacementConfig {
   public static final Codec<DecoratedPlacementConfig> CODEC = RecordCodecBuilder.create((builder) -> {
      return builder.group(ConfiguredPlacement.CODEC.fieldOf("outer").forGetter(DecoratedPlacementConfig::getOuter), ConfiguredPlacement.CODEC.fieldOf("inner").forGetter(DecoratedPlacementConfig::getInner)).apply(builder, DecoratedPlacementConfig::new);
   });
   private final ConfiguredPlacement<?> outer;
   private final ConfiguredPlacement<?> inner;

   public DecoratedPlacementConfig(ConfiguredPlacement<?> outer, ConfiguredPlacement<?> p_i242020_2_) {
      this.outer = outer;
      this.inner = p_i242020_2_;
   }

   public ConfiguredPlacement<?> getOuter() {
      return this.outer;
   }

   public ConfiguredPlacement<?> getInner() {
      return this.inner;
   }
}
