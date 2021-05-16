package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TopSolidRangeConfig implements IPlacementConfig {
   public static final Codec<TopSolidRangeConfig> CODEC = RecordCodecBuilder.create((p_236986_0_) -> {
      return p_236986_0_.group(Codec.INT.fieldOf("bottom_offset").orElse(0).forGetter((p_236988_0_) -> {
         return p_236988_0_.bottomOffset;
      }), Codec.INT.fieldOf("top_offset").orElse(0).forGetter((p_236987_0_) -> {
         return p_236987_0_.topOffset;
      }), Codec.INT.fieldOf("maximum").orElse(0).forGetter((p_242816_0_) -> {
         return p_242816_0_.maximum;
      })).apply(p_236986_0_, TopSolidRangeConfig::new);
   });
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public TopSolidRangeConfig(int p_i241992_1_, int p_i241992_2_, int p_i241992_3_) {
      this.bottomOffset = p_i241992_1_;
      this.topOffset = p_i241992_2_;
      this.maximum = p_i241992_3_;
   }
}
