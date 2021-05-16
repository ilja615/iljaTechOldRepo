package net.minecraft.world.gen.carver;

import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class ConfiguredCarvers {
   public static final ConfiguredCarver<ProbabilityConfig> CAVE = register("cave", WorldCarver.CAVE.configured(new ProbabilityConfig(0.14285715F)));
   public static final ConfiguredCarver<ProbabilityConfig> CANYON = register("canyon", WorldCarver.CANYON.configured(new ProbabilityConfig(0.02F)));
   public static final ConfiguredCarver<ProbabilityConfig> OCEAN_CAVE = register("ocean_cave", WorldCarver.CAVE.configured(new ProbabilityConfig(0.06666667F)));
   public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CANYON = register("underwater_canyon", WorldCarver.UNDERWATER_CANYON.configured(new ProbabilityConfig(0.02F)));
   public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", WorldCarver.UNDERWATER_CAVE.configured(new ProbabilityConfig(0.06666667F)));
   public static final ConfiguredCarver<ProbabilityConfig> NETHER_CAVE = register("nether_cave", WorldCarver.NETHER_CAVE.configured(new ProbabilityConfig(0.2F)));

   private static <WC extends ICarverConfig> ConfiguredCarver<WC> register(String p_243773_0_, ConfiguredCarver<WC> p_243773_1_) {
      return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_CARVER, p_243773_0_, p_243773_1_);
   }
}
