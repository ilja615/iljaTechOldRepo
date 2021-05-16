package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class FeatureSpreadConfig implements IPlacementConfig, IFeatureConfig {
   public static final Codec<FeatureSpreadConfig> CODEC = FeatureSpread.codec(-10, 128, 128).fieldOf("count").xmap(FeatureSpreadConfig::new, FeatureSpreadConfig::count).codec();
   private final FeatureSpread count;

   public FeatureSpreadConfig(int p_i241982_1_) {
      this.count = FeatureSpread.fixed(p_i241982_1_);
   }

   public FeatureSpreadConfig(FeatureSpread p_i241983_1_) {
      this.count = p_i241983_1_;
   }

   public FeatureSpread count() {
      return this.count;
   }
}
