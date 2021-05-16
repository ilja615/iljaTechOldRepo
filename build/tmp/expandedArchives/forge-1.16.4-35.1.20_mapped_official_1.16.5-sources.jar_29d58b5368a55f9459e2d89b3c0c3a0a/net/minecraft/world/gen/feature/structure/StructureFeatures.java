package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureFeatures {
   public static final StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> PILLAGER_OUTPOST = register("pillager_outpost", Structure.PILLAGER_OUTPOST.configured(new VillageConfig(() -> {
      return PillagerOutpostPools.START;
   }, 7)));
   public static final StructureFeature<MineshaftConfig, ? extends Structure<MineshaftConfig>> MINESHAFT = register("mineshaft", Structure.MINESHAFT.configured(new MineshaftConfig(0.004F, MineshaftStructure.Type.NORMAL)));
   public static final StructureFeature<MineshaftConfig, ? extends Structure<MineshaftConfig>> MINESHAFT_MESA = register("mineshaft_mesa", Structure.MINESHAFT.configured(new MineshaftConfig(0.004F, MineshaftStructure.Type.MESA)));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> WOODLAND_MANSION = register("mansion", Structure.WOODLAND_MANSION.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> JUNGLE_TEMPLE = register("jungle_pyramid", Structure.JUNGLE_TEMPLE.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> DESERT_PYRAMID = register("desert_pyramid", Structure.DESERT_PYRAMID.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> IGLOO = register("igloo", Structure.IGLOO.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<ShipwreckConfig, ? extends Structure<ShipwreckConfig>> SHIPWRECK = register("shipwreck", Structure.SHIPWRECK.configured(new ShipwreckConfig(false)));
   public static final StructureFeature<ShipwreckConfig, ? extends Structure<ShipwreckConfig>> SHIPWRECH_BEACHED = register("shipwreck_beached", Structure.SHIPWRECK.configured(new ShipwreckConfig(true)));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> SWAMP_HUT = register("swamp_hut", Structure.SWAMP_HUT.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> STRONGHOLD = register("stronghold", Structure.STRONGHOLD.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> OCEAN_MONUMENT = register("monument", Structure.OCEAN_MONUMENT.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<OceanRuinConfig, ? extends Structure<OceanRuinConfig>> OCEAN_RUIN_COLD = register("ocean_ruin_cold", Structure.OCEAN_RUIN.configured(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F)));
   public static final StructureFeature<OceanRuinConfig, ? extends Structure<OceanRuinConfig>> OCEAN_RUIN_WARM = register("ocean_ruin_warm", Structure.OCEAN_RUIN.configured(new OceanRuinConfig(OceanRuinStructure.Type.WARM, 0.3F, 0.9F)));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> NETHER_BRIDGE = register("fortress", Structure.NETHER_BRIDGE.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> NETHER_FOSSIL = register("nether_fossil", Structure.NETHER_FOSSIL.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> END_CITY = register("end_city", Structure.END_CITY.configured(NoFeatureConfig.INSTANCE));
   public static final StructureFeature<ProbabilityConfig, ? extends Structure<ProbabilityConfig>> BURIED_TREASURE = register("buried_treasure", Structure.BURIED_TREASURE.configured(new ProbabilityConfig(0.01F)));
   public static final StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> BASTION_REMNANT = register("bastion_remnant", Structure.BASTION_REMNANT.configured(new VillageConfig(() -> {
      return BastionRemnantsPieces.START;
   }, 6)));
   public static final StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> VILLAGE_PLAINS = register("village_plains", Structure.VILLAGE.configured(new VillageConfig(() -> {
      return PlainsVillagePools.START;
   }, 6)));
   public static final StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> VILLAGE_DESERT = register("village_desert", Structure.VILLAGE.configured(new VillageConfig(() -> {
      return DesertVillagePools.START;
   }, 6)));
   public static final StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> VILLAGE_SAVANNA = register("village_savanna", Structure.VILLAGE.configured(new VillageConfig(() -> {
      return SavannaVillagePools.START;
   }, 6)));
   public static final StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> VILLAGE_SNOWY = register("village_snowy", Structure.VILLAGE.configured(new VillageConfig(() -> {
      return SnowyVillagePools.START;
   }, 6)));
   public static final StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> VILLAGE_TAIGA = register("village_taiga", Structure.VILLAGE.configured(new VillageConfig(() -> {
      return TaigaVillagePools.START;
   }, 6)));
   public static final StructureFeature<RuinedPortalFeature, ? extends Structure<RuinedPortalFeature>> RUINED_PORTAL_STANDARD = register("ruined_portal", Structure.RUINED_PORTAL.configured(new RuinedPortalFeature(RuinedPortalStructure.Location.STANDARD)));
   public static final StructureFeature<RuinedPortalFeature, ? extends Structure<RuinedPortalFeature>> RUINED_PORTAL_DESERT = register("ruined_portal_desert", Structure.RUINED_PORTAL.configured(new RuinedPortalFeature(RuinedPortalStructure.Location.DESERT)));
   public static final StructureFeature<RuinedPortalFeature, ? extends Structure<RuinedPortalFeature>> RUINED_PORTAL_JUNGLE = register("ruined_portal_jungle", Structure.RUINED_PORTAL.configured(new RuinedPortalFeature(RuinedPortalStructure.Location.JUNGLE)));
   public static final StructureFeature<RuinedPortalFeature, ? extends Structure<RuinedPortalFeature>> RUINED_PORTAL_SWAMP = register("ruined_portal_swamp", Structure.RUINED_PORTAL.configured(new RuinedPortalFeature(RuinedPortalStructure.Location.SWAMP)));
   public static final StructureFeature<RuinedPortalFeature, ? extends Structure<RuinedPortalFeature>> RUINED_PORTAL_MOUNTAIN = register("ruined_portal_mountain", Structure.RUINED_PORTAL.configured(new RuinedPortalFeature(RuinedPortalStructure.Location.MOUNTAIN)));
   public static final StructureFeature<RuinedPortalFeature, ? extends Structure<RuinedPortalFeature>> RUINED_PORTAL_OCEAN = register("ruined_portal_ocean", Structure.RUINED_PORTAL.configured(new RuinedPortalFeature(RuinedPortalStructure.Location.OCEAN)));
   public static final StructureFeature<RuinedPortalFeature, ? extends Structure<RuinedPortalFeature>> RUINED_PORTAL_NETHER = register("ruined_portal_nether", Structure.RUINED_PORTAL.configured(new RuinedPortalFeature(RuinedPortalStructure.Location.NETHER)));

   private static <FC extends IFeatureConfig, F extends Structure<FC>> StructureFeature<FC, F> register(String p_244162_0_, StructureFeature<FC, F> p_244162_1_) {
      return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, p_244162_0_, p_244162_1_);
   }
}
