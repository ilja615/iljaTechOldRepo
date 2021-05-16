package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.WorldGenRegistries;

public class ConfiguredSurfaceBuilders {
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> BADLANDS = register("badlands", SurfaceBuilder.BADLANDS.configured(SurfaceBuilder.CONFIG_BADLANDS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> BASALT_DELTAS = register("basalt_deltas", SurfaceBuilder.BASALT_DELTAS.configured(SurfaceBuilder.CONFIG_BASALT_DELTAS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CRIMSON_FOREST = register("crimson_forest", SurfaceBuilder.NETHER_FOREST.configured(SurfaceBuilder.CONFIG_CRIMSON_FOREST));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> DESERT = register("desert", SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_DESERT));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> END = register("end", SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_THEEND));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> ERODED_BADLANDS = register("eroded_badlands", SurfaceBuilder.ERODED_BADLANDS.configured(SurfaceBuilder.CONFIG_BADLANDS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> FROZEN_OCEAN = register("frozen_ocean", SurfaceBuilder.FROZEN_OCEAN.configured(SurfaceBuilder.CONFIG_GRASS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> FULL_SAND = register("full_sand", SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_FULL_SAND));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> GIANT_TREE_TAIGA = register("giant_tree_taiga", SurfaceBuilder.GIANT_TREE_TAIGA.configured(SurfaceBuilder.CONFIG_GRASS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> GRASS = register("grass", SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_GRASS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> GRAVELLY_MOUNTAIN = register("gravelly_mountain", SurfaceBuilder.GRAVELLY_MOUNTAIN.configured(SurfaceBuilder.CONFIG_GRASS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> ICE_SPIKES = register("ice_spikes", SurfaceBuilder.DEFAULT.configured(new SurfaceBuilderConfig(Blocks.SNOW_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.GRAVEL.defaultBlockState())));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> MOUNTAIN = register("mountain", SurfaceBuilder.MOUNTAIN.configured(SurfaceBuilder.CONFIG_GRASS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> MYCELIUM = register("mycelium", SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_MYCELIUM));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> NETHER = register("nether", SurfaceBuilder.NETHER.configured(SurfaceBuilder.CONFIG_HELL));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> NOPE = register("nope", SurfaceBuilder.NOPE.configured(SurfaceBuilder.CONFIG_STONE));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> OCEAN_SAND = register("ocean_sand", SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_OCEAN_SAND));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SHATTERED_SAVANNA = register("shattered_savanna", SurfaceBuilder.SHATTERED_SAVANNA.configured(SurfaceBuilder.CONFIG_GRASS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SOUL_SAND_VALLEY = register("soul_sand_valley", SurfaceBuilder.SOUL_SAND_VALLEY.configured(SurfaceBuilder.CONFIG_SOUL_SAND_VALLEY));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> STONE = register("stone", SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_STONE));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SWAMP = register("swamp", SurfaceBuilder.SWAMP.configured(SurfaceBuilder.CONFIG_GRASS));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> WARPED_FOREST = register("warped_forest", SurfaceBuilder.NETHER_FOREST.configured(SurfaceBuilder.CONFIG_WARPED_FOREST));
   public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> WOODED_BADLANDS = register("wooded_badlands", SurfaceBuilder.WOODED_BADLANDS.configured(SurfaceBuilder.CONFIG_BADLANDS));

   private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> register(String p_244192_0_, ConfiguredSurfaceBuilder<SC> p_244192_1_) {
      return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, p_244192_0_, p_244192_1_);
   }
}
