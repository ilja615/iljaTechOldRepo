package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.feature.structure.BasaltDeltasStructure;
import net.minecraft.world.gen.feature.structure.NetherackBlobReplacementStructure;

public abstract class Feature<FC extends IFeatureConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<Feature<?>> {
   public static final Feature<NoFeatureConfig> NO_OP = register("no_op", new NoOpFeature(NoFeatureConfig.CODEC));
   public static final Feature<BaseTreeFeatureConfig> TREE = register("tree", new TreeFeature(BaseTreeFeatureConfig.CODEC));
   public static final FlowersFeature<BlockClusterFeatureConfig> FLOWER = register("flower", new DefaultFlowersFeature(BlockClusterFeatureConfig.CODEC));
   public static final FlowersFeature<BlockClusterFeatureConfig> NO_BONEMEAL_FLOWER = register("no_bonemeal_flower", new DefaultFlowersFeature(BlockClusterFeatureConfig.CODEC));
   public static final Feature<BlockClusterFeatureConfig> RANDOM_PATCH = register("random_patch", new RandomPatchFeature(BlockClusterFeatureConfig.CODEC));
   public static final Feature<BlockStateProvidingFeatureConfig> BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockStateProvidingFeatureConfig.CODEC));
   public static final Feature<LiquidsConfig> SPRING = register("spring_feature", new SpringFeature(LiquidsConfig.CODEC));
   public static final Feature<NoFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoFeatureConfig.CODEC));
   public static final Feature<ReplaceBlockConfig> EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfig.CODEC));
   public static final Feature<NoFeatureConfig> VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellsFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> FOSSIL = register("fossil", new FossilsFeature(NoFeatureConfig.CODEC));
   public static final Feature<BigMushroomFeatureConfig> HUGE_RED_MUSHROOM = register("huge_red_mushroom", new BigRedMushroomFeature(BigMushroomFeatureConfig.CODEC));
   public static final Feature<BigMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new BigBrownMushroomFeature(BigMushroomFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> FREEZE_TOP_LAYER = register("freeze_top_layer", new IceAndSnowFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> VINES = register("vines", new VinesFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonsFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(NoFeatureConfig.CODEC));
   public static final Feature<BlockStateFeatureConfig> ICEBERG = register("iceberg", new IcebergFeature(BlockStateFeatureConfig.CODEC));
   public static final Feature<BlockStateFeatureConfig> FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockStateFeatureConfig.CODEC));
   public static final Feature<SphereReplaceConfig> DISK = register("disk", new SphereReplaceFeature(SphereReplaceConfig.CODEC));
   public static final Feature<SphereReplaceConfig> ICE_PATCH = register("ice_patch", new IcePathFeature(SphereReplaceConfig.CODEC));
   public static final Feature<BlockStateFeatureConfig> LAKE = register("lake", new LakesFeature(BlockStateFeatureConfig.CODEC));
   public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig.CODEC));
   public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> END_ISLAND = register("end_island", new EndIslandFeature(NoFeatureConfig.CODEC));
   public static final Feature<EndGatewayConfig> END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfig.CODEC));
   public static final SeaGrassFeature SEAGRASS = register("seagrass", new SeaGrassFeature(ProbabilityConfig.CODEC));
   public static final Feature<NoFeatureConfig> KELP = register("kelp", new KelpFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoFeatureConfig.CODEC));
   public static final Feature<FeatureSpreadConfig> SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(FeatureSpreadConfig.CODEC));
   public static final Feature<BlockWithContextConfig> SIMPLE_BLOCK = register("simple_block", new BlockWithContextFeature(BlockWithContextConfig.CODEC));
   public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig.CODEC));
   public static final Feature<HugeFungusConfig> HUGE_FUNGUS = register("huge_fungus", new HugeFungusFeature(HugeFungusConfig.CODEC));
   public static final Feature<BlockStateProvidingFeatureConfig> NETHER_FOREST_VEGETATION = register("nether_forest_vegetation", new NetherVegetationFeature(BlockStateProvidingFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> WEEPING_VINES = register("weeping_vines", new WeepingVineFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> TWISTING_VINES = register("twisting_vines", new TwistingVineFeature(NoFeatureConfig.CODEC));
   public static final Feature<ColumnConfig> BASALT_COLUMNS = register("basalt_columns", new BasaltColumnFeature(ColumnConfig.CODEC));
   public static final Feature<BasaltDeltasFeature> DELTA_FEATURE = register("delta_feature", new BasaltDeltasStructure(BasaltDeltasFeature.CODEC));
   public static final Feature<BlobReplacementConfig> REPLACE_BLOBS = register("netherrack_replace_blobs", new NetherackBlobReplacementStructure(BlobReplacementConfig.CODEC));
   public static final Feature<FillLayerConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerConfig.CODEC));
   public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(NoFeatureConfig.CODEC));
   public static final Feature<NoFeatureConfig> BASALT_PILLAR = register("basalt_pillar", new BasaltPillarFeature(NoFeatureConfig.CODEC));
   public static final Feature<OreFeatureConfig> NO_SURFACE_ORE = register("no_surface_ore", new NoExposedOreFeature(OreFeatureConfig.CODEC));
   public static final Feature<MultipleRandomFeatureConfig> RANDOM_SELECTOR = register("random_selector", new MultipleWithChanceRandomFeature(MultipleRandomFeatureConfig.CODEC));
   public static final Feature<SingleRandomFeature> SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SingleRandomFeatureConfig(SingleRandomFeature.CODEC));
   public static final Feature<TwoFeatureChoiceConfig> RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new TwoFeatureChoiceFeature(TwoFeatureChoiceConfig.CODEC));
   public static final Feature<DecoratedFeatureConfig> DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfig.CODEC));
   private final Codec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec;

   private static <C extends IFeatureConfig, F extends Feature<C>> F register(String p_214468_0_, F p_214468_1_) {
      return Registry.register(Registry.FEATURE, p_214468_0_, p_214468_1_);
   }

   public Feature(Codec<FC> p_i231953_1_) {
      this.configuredCodec = p_i231953_1_.fieldOf("config").xmap((p_236296_1_) -> {
         return new ConfiguredFeature<>(this, p_236296_1_);
      }, (p_236295_0_) -> {
         return p_236295_0_.config;
      }).codec();
   }

   public Codec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec() {
      return this.configuredCodec;
   }

   public ConfiguredFeature<FC, ?> configured(FC p_225566_1_) {
      return new ConfiguredFeature<>(this, p_225566_1_);
   }

   protected void setBlock(IWorldWriter p_230367_1_, BlockPos p_230367_2_, BlockState p_230367_3_) {
      p_230367_1_.setBlock(p_230367_2_, p_230367_3_, 3);
   }

   public abstract boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, FC p_241855_5_);

   protected static boolean isStone(Block p_227249_0_) {
      return net.minecraftforge.common.Tags.Blocks.STONE.contains(p_227249_0_);
   }

   public static boolean isDirt(Block p_227250_0_) {
      return net.minecraftforge.common.Tags.Blocks.DIRT.contains(p_227250_0_);
   }

   public static boolean isGrassOrDirt(IWorldGenerationBaseReader p_236293_0_, BlockPos p_236293_1_) {
      return p_236293_0_.isStateAtPosition(p_236293_1_, (p_236294_0_) -> {
         return isDirt(p_236294_0_.getBlock());
      });
   }

   public static boolean isAir(IWorldGenerationBaseReader p_236297_0_, BlockPos p_236297_1_) {
      return p_236297_0_.isStateAtPosition(p_236297_1_, AbstractBlock.AbstractBlockState::isAir);
   }
}
