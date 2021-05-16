package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;

public class ChanceConfig implements IPlacementConfig {
   public static final Codec<ChanceConfig> CODEC = Codec.INT.fieldOf("chance").xmap(ChanceConfig::new, (p_236951_0_) -> {
      return p_236951_0_.chance;
   }).codec();
   public final int chance;

   public ChanceConfig(int p_i48665_1_) {
      this.chance = p_i48665_1_;
   }
}
