package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ColumnConfig implements IFeatureConfig {
   public static final Codec<ColumnConfig> CODEC = RecordCodecBuilder.create((p_242793_0_) -> {
      return p_242793_0_.group(FeatureSpread.codec(0, 2, 1).fieldOf("reach").forGetter((p_242796_0_) -> {
         return p_242796_0_.reach;
      }), FeatureSpread.codec(1, 5, 5).fieldOf("height").forGetter((p_242792_0_) -> {
         return p_242792_0_.height;
      })).apply(p_242793_0_, ColumnConfig::new);
   });
   private final FeatureSpread reach;
   private final FeatureSpread height;

   public ColumnConfig(FeatureSpread p_i241981_1_, FeatureSpread p_i241981_2_) {
      this.reach = p_i241981_1_;
      this.height = p_i241981_2_;
   }

   public FeatureSpread reach() {
      return this.reach;
   }

   public FeatureSpread height() {
      return this.height;
   }
}
