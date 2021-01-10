package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;

public class ChanceConfig implements IPlacementConfig {
   public static final Codec<ChanceConfig> CODEC = Codec.INT.fieldOf("chance").xmap(ChanceConfig::new, (config) -> {
      return config.chance;
   }).codec();
   public final int chance;

   public ChanceConfig(int chance) {
      this.chance = chance;
   }
}
