package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.structure.StructureFeatures;

public class DefaultBiomeFeatures {
   public static void addDefaultOverworldLandMesaStructures(BiomeGenerationSettings.Builder p_243713_0_) {
      p_243713_0_.addStructureStart(StructureFeatures.MINESHAFT_MESA);
      p_243713_0_.addStructureStart(StructureFeatures.STRONGHOLD);
   }

   public static void addDefaultOverworldLandStructures(BiomeGenerationSettings.Builder p_243733_0_) {
      p_243733_0_.addStructureStart(StructureFeatures.MINESHAFT);
      p_243733_0_.addStructureStart(StructureFeatures.STRONGHOLD);
   }

   public static void addDefaultOverworldOceanStructures(BiomeGenerationSettings.Builder p_243736_0_) {
      p_243736_0_.addStructureStart(StructureFeatures.MINESHAFT);
      p_243736_0_.addStructureStart(StructureFeatures.SHIPWRECK);
   }

   public static void addDefaultCarvers(BiomeGenerationSettings.Builder p_243738_0_) {
      p_243738_0_.addCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.CAVE);
      p_243738_0_.addCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.CANYON);
   }

   public static void addOceanCarvers(BiomeGenerationSettings.Builder p_243740_0_) {
      p_243740_0_.addCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.OCEAN_CAVE);
      p_243740_0_.addCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.CANYON);
      p_243740_0_.addCarver(GenerationStage.Carving.LIQUID, ConfiguredCarvers.UNDERWATER_CANYON);
      p_243740_0_.addCarver(GenerationStage.Carving.LIQUID, ConfiguredCarvers.UNDERWATER_CAVE);
   }

   public static void addDefaultLakes(BiomeGenerationSettings.Builder p_243742_0_) {
      p_243742_0_.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_WATER);
      p_243742_0_.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
   }

   public static void addDesertLakes(BiomeGenerationSettings.Builder p_243744_0_) {
      p_243744_0_.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
   }

   public static void addDefaultMonsterRoom(BiomeGenerationSettings.Builder p_243746_0_) {
      p_243746_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Features.MONSTER_ROOM);
   }

   public static void addDefaultUndergroundVariety(BiomeGenerationSettings.Builder p_243748_0_) {
      p_243748_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIRT);
      p_243748_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GRAVEL);
      p_243748_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GRANITE);
      p_243748_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIORITE);
      p_243748_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_ANDESITE);
   }

   public static void addDefaultOres(BiomeGenerationSettings.Builder p_243750_0_) {
      p_243750_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL);
      p_243750_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON);
      p_243750_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD);
      p_243750_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_REDSTONE);
      p_243750_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND);
      p_243750_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_LAPIS);
   }

   public static void addExtraGold(BiomeGenerationSettings.Builder p_243751_0_) {
      p_243751_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD_EXTRA);
   }

   public static void addExtraEmeralds(BiomeGenerationSettings.Builder p_243752_0_) {
      p_243752_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_EMERALD);
   }

   public static void addInfestedStone(BiomeGenerationSettings.Builder p_243753_0_) {
      p_243753_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_INFESTED);
   }

   public static void addDefaultSoftDisks(BiomeGenerationSettings.Builder p_243754_0_) {
      p_243754_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_SAND);
      p_243754_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
      p_243754_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_GRAVEL);
   }

   public static void addSwampClayDisk(BiomeGenerationSettings.Builder p_243755_0_) {
      p_243755_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
   }

   public static void addMossyStoneBlock(BiomeGenerationSettings.Builder p_243756_0_) {
      p_243756_0_.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.FOREST_ROCK);
   }

   public static void addFerns(BiomeGenerationSettings.Builder p_243757_0_) {
      p_243757_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_LARGE_FERN);
   }

   public static void addBerryBushes(BiomeGenerationSettings.Builder p_243758_0_) {
      p_243758_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_DECORATED);
   }

   public static void addSparseBerryBushes(BiomeGenerationSettings.Builder p_243759_0_) {
      p_243759_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_SPARSE);
   }

   public static void addLightBambooVegetation(BiomeGenerationSettings.Builder p_243760_0_) {
      p_243760_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BAMBOO_LIGHT);
   }

   public static void addBambooVegetation(BiomeGenerationSettings.Builder p_243761_0_) {
      p_243761_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BAMBOO);
      p_243761_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BAMBOO_VEGETATION);
   }

   public static void addTaigaTrees(BiomeGenerationSettings.Builder p_243762_0_) {
      p_243762_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TAIGA_VEGETATION);
   }

   public static void addWaterTrees(BiomeGenerationSettings.Builder p_243763_0_) {
      p_243763_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_WATER);
   }

   public static void addBirchTrees(BiomeGenerationSettings.Builder p_243764_0_) {
      p_243764_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_BIRCH);
   }

   public static void addOtherBirchTrees(BiomeGenerationSettings.Builder p_243765_0_) {
      p_243765_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BIRCH_OTHER);
   }

   public static void addTallBirchTrees(BiomeGenerationSettings.Builder p_243766_0_) {
      p_243766_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BIRCH_TALL);
   }

   public static void addSavannaTrees(BiomeGenerationSettings.Builder p_243687_0_) {
      p_243687_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_SAVANNA);
   }

   public static void addShatteredSavannaTrees(BiomeGenerationSettings.Builder p_243688_0_) {
      p_243688_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_SHATTERED_SAVANNA);
   }

   public static void addMountainTrees(BiomeGenerationSettings.Builder p_243689_0_) {
      p_243689_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN);
   }

   public static void addMountainEdgeTrees(BiomeGenerationSettings.Builder p_243690_0_) {
      p_243690_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN_EDGE);
   }

   public static void addJungleTrees(BiomeGenerationSettings.Builder p_243691_0_) {
      p_243691_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE);
   }

   public static void addJungleEdgeTrees(BiomeGenerationSettings.Builder p_243692_0_) {
      p_243692_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE_EDGE);
   }

   public static void addBadlandsTrees(BiomeGenerationSettings.Builder p_243693_0_) {
      p_243693_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.OAK_BADLANDS);
   }

   public static void addSnowyTrees(BiomeGenerationSettings.Builder p_243694_0_) {
      p_243694_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRUCE_SNOWY);
   }

   public static void addJungleGrass(BiomeGenerationSettings.Builder p_243695_0_) {
      p_243695_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_JUNGLE);
   }

   public static void addSavannaGrass(BiomeGenerationSettings.Builder p_243696_0_) {
      p_243696_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS);
   }

   public static void addShatteredSavannaGrass(BiomeGenerationSettings.Builder p_243697_0_) {
      p_243697_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
   }

   public static void addSavannaExtraGrass(BiomeGenerationSettings.Builder p_243698_0_) {
      p_243698_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_SAVANNA);
   }

   public static void addBadlandGrass(BiomeGenerationSettings.Builder p_243699_0_) {
      p_243699_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
      p_243699_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_BADLANDS);
   }

   public static void addForestFlowers(BiomeGenerationSettings.Builder p_243700_0_) {
      p_243700_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_VEGETATION);
   }

   public static void addForestGrass(BiomeGenerationSettings.Builder p_243701_0_) {
      p_243701_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_FOREST);
   }

   public static void addSwampVegetation(BiomeGenerationSettings.Builder p_243702_0_) {
      p_243702_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SWAMP_TREE);
      p_243702_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_SWAMP);
      p_243702_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
      p_243702_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
      p_243702_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_WATERLILLY);
      p_243702_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_SWAMP);
      p_243702_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_SWAMP);
   }

   public static void addMushroomFieldVegetation(BiomeGenerationSettings.Builder p_243703_0_) {
      p_243703_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.MUSHROOM_FIELD_VEGETATION);
      p_243703_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
      p_243703_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
   }

   public static void addPlainVegetation(BiomeGenerationSettings.Builder p_243704_0_) {
      p_243704_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PLAIN_VEGETATION);
      p_243704_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_PLAIN_DECORATED);
      p_243704_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_PLAIN);
   }

   public static void addDesertVegetation(BiomeGenerationSettings.Builder p_243705_0_) {
      p_243705_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_2);
   }

   public static void addGiantTaigaVegetation(BiomeGenerationSettings.Builder p_243706_0_) {
      p_243706_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA);
      p_243706_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
      p_243706_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_GIANT);
      p_243706_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_GIANT);
   }

   public static void addDefaultFlowers(BiomeGenerationSettings.Builder p_243707_0_) {
      p_243707_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_DEFAULT);
   }

   public static void addWarmFlowers(BiomeGenerationSettings.Builder p_243708_0_) {
      p_243708_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_WARM);
   }

   public static void addDefaultGrass(BiomeGenerationSettings.Builder p_243709_0_) {
      p_243709_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
   }

   public static void addTaigaGrass(BiomeGenerationSettings.Builder p_243710_0_) {
      p_243710_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA_2);
      p_243710_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
      p_243710_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
   }

   public static void addPlainGrass(BiomeGenerationSettings.Builder p_243711_0_) {
      p_243711_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS_2);
   }

   public static void addDefaultMushrooms(BiomeGenerationSettings.Builder p_243712_0_) {
      p_243712_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_NORMAL);
      p_243712_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_NORMAL);
   }

   public static void addDefaultExtraVegetation(BiomeGenerationSettings.Builder p_243717_0_) {
      p_243717_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE);
      p_243717_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
   }

   public static void addBadlandExtraVegetation(BiomeGenerationSettings.Builder p_243718_0_) {
      p_243718_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_BADLANDS);
      p_243718_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
      p_243718_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DECORATED);
   }

   public static void addJungleExtraVegetation(BiomeGenerationSettings.Builder p_243719_0_) {
      p_243719_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_MELON);
      p_243719_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.VINES);
   }

   public static void addDesertExtraVegetation(BiomeGenerationSettings.Builder p_243720_0_) {
      p_243720_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_DESERT);
      p_243720_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
      p_243720_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DESERT);
   }

   public static void addSwampExtraVegetation(BiomeGenerationSettings.Builder p_243721_0_) {
      p_243721_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_SWAMP);
      p_243721_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
   }

   public static void addDesertExtraDecoration(BiomeGenerationSettings.Builder p_243722_0_) {
      p_243722_0_.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.WELL);
   }

   public static void addFossilDecoration(BiomeGenerationSettings.Builder p_243723_0_) {
      p_243723_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Features.FOSSIL);
   }

   public static void addColdOceanExtraVegetation(BiomeGenerationSettings.Builder p_243724_0_) {
      p_243724_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.KELP_COLD);
   }

   public static void addDefaultSeagrass(BiomeGenerationSettings.Builder p_243725_0_) {
      p_243725_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SIMPLE);
   }

   public static void addLukeWarmKelp(BiomeGenerationSettings.Builder p_243726_0_) {
      p_243726_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.KELP_WARM);
   }

   public static void addDefaultSprings(BiomeGenerationSettings.Builder p_243727_0_) {
      p_243727_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
      p_243727_0_.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
   }

   public static void addIcebergs(BiomeGenerationSettings.Builder p_243728_0_) {
      p_243728_0_.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_PACKED);
      p_243728_0_.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_BLUE);
   }

   public static void addBlueIce(BiomeGenerationSettings.Builder p_243729_0_) {
      p_243729_0_.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.BLUE_ICE);
   }

   public static void addSurfaceFreezing(BiomeGenerationSettings.Builder p_243730_0_) {
      p_243730_0_.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Features.FREEZE_TOP_LAYER);
   }

   public static void addNetherDefaultOres(BiomeGenerationSettings.Builder p_243731_0_) {
      p_243731_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_GRAVEL_NETHER);
      p_243731_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_BLACKSTONE);
      p_243731_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_GOLD_NETHER);
      p_243731_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_QUARTZ_NETHER);
      addAncientDebris(p_243731_0_);
   }

   public static void addAncientDebris(BiomeGenerationSettings.Builder p_243732_0_) {
      p_243732_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_LARGE);
      p_243732_0_.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_SMALL);
   }

   public static void farmAnimals(MobSpawnInfo.Builder p_243714_0_) {
      p_243714_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.SHEEP, 12, 4, 4));
      p_243714_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PIG, 10, 4, 4));
      p_243714_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.CHICKEN, 10, 4, 4));
      p_243714_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.COW, 8, 4, 4));
   }

   public static void ambientSpawns(MobSpawnInfo.Builder p_243734_0_) {
      p_243734_0_.addSpawn(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(EntityType.BAT, 10, 8, 8));
   }

   public static void commonSpawns(MobSpawnInfo.Builder p_243737_0_) {
      ambientSpawns(p_243737_0_);
      monsters(p_243737_0_, 95, 5, 100);
   }

   public static void oceanSpawns(MobSpawnInfo.Builder p_243716_0_, int p_243716_1_, int p_243716_2_, int p_243716_3_) {
      p_243716_0_.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SQUID, p_243716_1_, 1, p_243716_2_));
      p_243716_0_.addSpawn(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.COD, p_243716_3_, 3, 6));
      commonSpawns(p_243716_0_);
      p_243716_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.DROWNED, 5, 1, 1));
   }

   public static void warmOceanSpawns(MobSpawnInfo.Builder p_243715_0_, int p_243715_1_, int p_243715_2_) {
      p_243715_0_.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SQUID, p_243715_1_, p_243715_2_, 4));
      p_243715_0_.addSpawn(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.TROPICAL_FISH, 25, 8, 8));
      p_243715_0_.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.DOLPHIN, 2, 1, 2));
      commonSpawns(p_243715_0_);
   }

   public static void plainsSpawns(MobSpawnInfo.Builder p_243739_0_) {
      farmAnimals(p_243739_0_);
      p_243739_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.HORSE, 5, 2, 6));
      p_243739_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.DONKEY, 1, 1, 3));
      commonSpawns(p_243739_0_);
   }

   public static void snowySpawns(MobSpawnInfo.Builder p_243741_0_) {
      p_243741_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 10, 2, 3));
      p_243741_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.POLAR_BEAR, 1, 1, 2));
      ambientSpawns(p_243741_0_);
      monsters(p_243741_0_, 95, 5, 20);
      p_243741_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.STRAY, 80, 4, 4));
   }

   public static void desertSpawns(MobSpawnInfo.Builder p_243743_0_) {
      p_243743_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3));
      ambientSpawns(p_243743_0_);
      monsters(p_243743_0_, 19, 1, 100);
      p_243743_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.HUSK, 80, 4, 4));
   }

   public static void monsters(MobSpawnInfo.Builder p_243735_0_, int p_243735_1_, int p_243735_2_, int p_243735_3_) {
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SPIDER, 100, 4, 4));
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIE, p_243735_1_, 4, 4));
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIE_VILLAGER, p_243735_2_, 1, 1));
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SKELETON, p_243735_3_, 4, 4));
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.CREEPER, 100, 4, 4));
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SLIME, 100, 4, 4));
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 10, 1, 4));
      p_243735_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.WITCH, 5, 1, 1));
   }

   public static void mooshroomSpawns(MobSpawnInfo.Builder p_243745_0_) {
      p_243745_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.MOOSHROOM, 8, 4, 8));
      ambientSpawns(p_243745_0_);
   }

   public static void baseJungleSpawns(MobSpawnInfo.Builder p_243747_0_) {
      farmAnimals(p_243747_0_);
      p_243747_0_.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.CHICKEN, 10, 4, 4));
      commonSpawns(p_243747_0_);
   }

   public static void endSpawns(MobSpawnInfo.Builder p_243749_0_) {
      p_243749_0_.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 10, 4, 4));
   }
}
