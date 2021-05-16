package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;

public abstract class BiomeRegistry {
   private static final Int2ObjectMap<RegistryKey<Biome>> TO_NAME = new Int2ObjectArrayMap<>();
   public static final Biome PLAINS = register(1, Biomes.PLAINS, BiomeMaker.plainsBiome(false));
   public static final Biome THE_VOID = register(127, Biomes.THE_VOID, BiomeMaker.theVoidBiome());

   private static Biome register(int p_244204_0_, RegistryKey<Biome> p_244204_1_, Biome p_244204_2_) {
      TO_NAME.put(p_244204_0_, p_244204_1_);
      return WorldGenRegistries.registerMapping(WorldGenRegistries.BIOME, p_244204_0_, p_244204_1_, p_244204_2_);
   }

   public static RegistryKey<Biome> byId(int p_244203_0_) {
      return ((net.minecraftforge.registries.ForgeRegistry<Biome>)net.minecraftforge.registries.ForgeRegistries.BIOMES).getKey(p_244203_0_);
   }

   static {
      register(0, Biomes.OCEAN, BiomeMaker.oceanBiome(false));
      register(2, Biomes.DESERT, BiomeMaker.desertBiome(0.125F, 0.05F, true, true, true));
      register(3, Biomes.MOUNTAINS, BiomeMaker.mountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.MOUNTAIN, false));
      register(4, Biomes.FOREST, BiomeMaker.forestBiome(0.1F, 0.2F));
      register(5, Biomes.TAIGA, BiomeMaker.taigaBiome(0.2F, 0.2F, false, false, true, false));
      register(6, Biomes.SWAMP, BiomeMaker.swampBiome(-0.2F, 0.1F, false));
      register(7, Biomes.RIVER, BiomeMaker.riverBiome(-0.5F, 0.0F, 0.5F, 4159204, false));
      register(8, Biomes.NETHER_WASTES, BiomeMaker.netherWastesBiome());
      register(9, Biomes.THE_END, BiomeMaker.theEndBiome());
      register(10, Biomes.FROZEN_OCEAN, BiomeMaker.frozenOceanBiome(false));
      register(11, Biomes.FROZEN_RIVER, BiomeMaker.riverBiome(-0.5F, 0.0F, 0.0F, 3750089, true));
      register(12, Biomes.SNOWY_TUNDRA, BiomeMaker.tundraBiome(0.125F, 0.05F, false, false));
      register(13, Biomes.SNOWY_MOUNTAINS, BiomeMaker.tundraBiome(0.45F, 0.3F, false, true));
      register(14, Biomes.MUSHROOM_FIELDS, BiomeMaker.mushroomFieldsBiome(0.2F, 0.3F));
      register(15, Biomes.MUSHROOM_FIELD_SHORE, BiomeMaker.mushroomFieldsBiome(0.0F, 0.025F));
      register(16, Biomes.BEACH, BiomeMaker.beachBiome(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
      register(17, Biomes.DESERT_HILLS, BiomeMaker.desertBiome(0.45F, 0.3F, false, true, false));
      register(18, Biomes.WOODED_HILLS, BiomeMaker.forestBiome(0.45F, 0.3F));
      register(19, Biomes.TAIGA_HILLS, BiomeMaker.taigaBiome(0.45F, 0.3F, false, false, false, false));
      register(20, Biomes.MOUNTAIN_EDGE, BiomeMaker.mountainBiome(0.8F, 0.3F, ConfiguredSurfaceBuilders.GRASS, true));
      register(21, Biomes.JUNGLE, BiomeMaker.jungleBiome());
      register(22, Biomes.JUNGLE_HILLS, BiomeMaker.jungleHillsBiome());
      register(23, Biomes.JUNGLE_EDGE, BiomeMaker.jungleEdgeBiome());
      register(24, Biomes.DEEP_OCEAN, BiomeMaker.oceanBiome(true));
      register(25, Biomes.STONE_SHORE, BiomeMaker.beachBiome(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
      register(26, Biomes.SNOWY_BEACH, BiomeMaker.beachBiome(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
      register(27, Biomes.BIRCH_FOREST, BiomeMaker.birchForestBiome(0.1F, 0.2F, false));
      register(28, Biomes.BIRCH_FOREST_HILLS, BiomeMaker.birchForestBiome(0.45F, 0.3F, false));
      register(29, Biomes.DARK_FOREST, BiomeMaker.darkForestBiome(0.1F, 0.2F, false));
      register(30, Biomes.SNOWY_TAIGA, BiomeMaker.taigaBiome(0.2F, 0.2F, true, false, false, true));
      register(31, Biomes.SNOWY_TAIGA_HILLS, BiomeMaker.taigaBiome(0.45F, 0.3F, true, false, false, false));
      register(32, Biomes.GIANT_TREE_TAIGA, BiomeMaker.giantTreeTaiga(0.2F, 0.2F, 0.3F, false));
      register(33, Biomes.GIANT_TREE_TAIGA_HILLS, BiomeMaker.giantTreeTaiga(0.45F, 0.3F, 0.3F, false));
      register(34, Biomes.WOODED_MOUNTAINS, BiomeMaker.mountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRASS, true));
      register(35, Biomes.SAVANNA, BiomeMaker.savannaBiome(0.125F, 0.05F, 1.2F, false, false));
      register(36, Biomes.SAVANNA_PLATEAU, BiomeMaker.savanaPlateauBiome());
      register(37, Biomes.BADLANDS, BiomeMaker.badlandsBiome(0.1F, 0.2F, false));
      register(38, Biomes.WOODED_BADLANDS_PLATEAU, BiomeMaker.woodedBadlandsPlateauBiome(1.5F, 0.025F));
      register(39, Biomes.BADLANDS_PLATEAU, BiomeMaker.badlandsBiome(1.5F, 0.025F, true));
      register(40, Biomes.SMALL_END_ISLANDS, BiomeMaker.smallEndIslandsBiome());
      register(41, Biomes.END_MIDLANDS, BiomeMaker.endMidlandsBiome());
      register(42, Biomes.END_HIGHLANDS, BiomeMaker.endHighlandsBiome());
      register(43, Biomes.END_BARRENS, BiomeMaker.endBarrensBiome());
      register(44, Biomes.WARM_OCEAN, BiomeMaker.warmOceanBiome());
      register(45, Biomes.LUKEWARM_OCEAN, BiomeMaker.lukeWarmOceanBiome(false));
      register(46, Biomes.COLD_OCEAN, BiomeMaker.coldOceanBiome(false));
      register(47, Biomes.DEEP_WARM_OCEAN, BiomeMaker.deepWarmOceanBiome());
      register(48, Biomes.DEEP_LUKEWARM_OCEAN, BiomeMaker.lukeWarmOceanBiome(true));
      register(49, Biomes.DEEP_COLD_OCEAN, BiomeMaker.coldOceanBiome(true));
      register(50, Biomes.DEEP_FROZEN_OCEAN, BiomeMaker.frozenOceanBiome(true));
      register(129, Biomes.SUNFLOWER_PLAINS, BiomeMaker.plainsBiome(true));
      register(130, Biomes.DESERT_LAKES, BiomeMaker.desertBiome(0.225F, 0.25F, false, false, false));
      register(131, Biomes.GRAVELLY_MOUNTAINS, BiomeMaker.mountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
      register(132, Biomes.FLOWER_FOREST, BiomeMaker.flowerForestBiome());
      register(133, Biomes.TAIGA_MOUNTAINS, BiomeMaker.taigaBiome(0.3F, 0.4F, false, true, false, false));
      register(134, Biomes.SWAMP_HILLS, BiomeMaker.swampBiome(-0.1F, 0.3F, true));
      register(140, Biomes.ICE_SPIKES, BiomeMaker.tundraBiome(0.425F, 0.45000002F, true, false));
      register(149, Biomes.MODIFIED_JUNGLE, BiomeMaker.modifiedJungleBiome());
      register(151, Biomes.MODIFIED_JUNGLE_EDGE, BiomeMaker.modifiedJungleEdgeBiome());
      register(155, Biomes.TALL_BIRCH_FOREST, BiomeMaker.birchForestBiome(0.2F, 0.4F, true));
      register(156, Biomes.TALL_BIRCH_HILLS, BiomeMaker.birchForestBiome(0.55F, 0.5F, true));
      register(157, Biomes.DARK_FOREST_HILLS, BiomeMaker.darkForestBiome(0.2F, 0.4F, true));
      register(158, Biomes.SNOWY_TAIGA_MOUNTAINS, BiomeMaker.taigaBiome(0.3F, 0.4F, true, true, false, false));
      register(160, Biomes.GIANT_SPRUCE_TAIGA, BiomeMaker.giantTreeTaiga(0.2F, 0.2F, 0.25F, true));
      register(161, Biomes.GIANT_SPRUCE_TAIGA_HILLS, BiomeMaker.giantTreeTaiga(0.2F, 0.2F, 0.25F, true));
      register(162, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, BiomeMaker.mountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
      register(163, Biomes.SHATTERED_SAVANNA, BiomeMaker.savannaBiome(0.3625F, 1.225F, 1.1F, true, true));
      register(164, Biomes.SHATTERED_SAVANNA_PLATEAU, BiomeMaker.savannaBiome(1.05F, 1.2125001F, 1.0F, true, true));
      register(165, Biomes.ERODED_BADLANDS, BiomeMaker.erodedBadlandsBiome());
      register(166, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomeMaker.woodedBadlandsPlateauBiome(0.45F, 0.3F));
      register(167, Biomes.MODIFIED_BADLANDS_PLATEAU, BiomeMaker.badlandsBiome(0.45F, 0.3F, true));
      register(168, Biomes.BAMBOO_JUNGLE, BiomeMaker.bambooJungleBiome());
      register(169, Biomes.BAMBOO_JUNGLE_HILLS, BiomeMaker.bambooJungleHillsBiome());
      register(170, Biomes.SOUL_SAND_VALLEY, BiomeMaker.soulSandValleyBiome());
      register(171, Biomes.CRIMSON_FOREST, BiomeMaker.crimsonForestBiome());
      register(172, Biomes.WARPED_FOREST, BiomeMaker.warpedForestBiome());
      register(173, Biomes.BASALT_DELTAS, BiomeMaker.basaltDeltasBiome());
   }
}
