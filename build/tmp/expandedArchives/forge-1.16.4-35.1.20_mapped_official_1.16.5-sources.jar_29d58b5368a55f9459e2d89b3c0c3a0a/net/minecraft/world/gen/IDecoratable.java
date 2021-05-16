package net.minecraft.world.gen;

import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;

public interface IDecoratable<R> {
   R decorated(ConfiguredPlacement<?> p_227228_1_);

   default R chance(int p_242729_1_) {
      return this.decorated(Placement.CHANCE.configured(new ChanceConfig(p_242729_1_)));
   }

   default R count(FeatureSpread p_242730_1_) {
      return this.decorated(Placement.COUNT.configured(new FeatureSpreadConfig(p_242730_1_)));
   }

   default R count(int p_242731_1_) {
      return this.count(FeatureSpread.fixed(p_242731_1_));
   }

   default R countRandom(int p_242732_1_) {
      return this.count(FeatureSpread.of(0, p_242732_1_));
   }

   default R range(int p_242733_1_) {
      return this.decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, p_242733_1_)));
   }

   default R squared() {
      return this.decorated(Placement.SQUARE.configured(NoPlacementConfig.INSTANCE));
   }
}
