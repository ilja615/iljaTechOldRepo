package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TopSolidRangeConfig implements IPlacementConfig {
   public static final Codec<TopSolidRangeConfig> CODEC = RecordCodecBuilder.create((builder) -> {
      return builder.group(Codec.INT.fieldOf("bottom_offset").orElse(0).forGetter((config) -> {
         return config.bottomOffset;
      }), Codec.INT.fieldOf("top_offset").orElse(0).forGetter((config) -> {
         return config.topOffset;
      }), Codec.INT.fieldOf("maximum").orElse(0).forGetter((config) -> {
         return config.maximum;
      })).apply(builder, TopSolidRangeConfig::new);
   });
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public TopSolidRangeConfig(int bottomOffset, int topOffset, int maximum) {
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.maximum = maximum;
   }
}
