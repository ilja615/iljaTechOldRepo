package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.NoOpFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;

public class ModFeatures
{
    public static ConfiguredFeature<?, ?> AZURITE_GEODE_CF;
    public static ConfiguredFeature<?, ?> CASSITERITE_GEODE_CF;
    public static ConfiguredFeature<?, ?> RUBY_GEODE_CF;

    public static PlacedFeature AZURITE_GEODE_PF;
    public static PlacedFeature CASSITERITE_GEODE_PF;
    public static PlacedFeature RUBY_GEODE_PF;

    public static void registerFeatures()
    {
        AZURITE_GEODE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(IljaTech.MOD_ID, "azurite_geode_cf"), Feature.GEODE.configured(new GeodeConfiguration(
                        new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
                        BlockStateProvider.simple(ModBlocks.AZURITE_BLOCK.get()),
                        BlockStateProvider.simple(ModBlocks.BUDDING_AZURITE_BLOCK.get()),
                        BlockStateProvider.simple(Blocks.CALCITE),
                        BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                        List.of(ModBlocks.AZURITE_SMALL_BUD.get().defaultBlockState(), ModBlocks.AZURITE_MEDIUM_BUD.get().defaultBlockState(), ModBlocks.AZURITE_LARGE_BUD.get().defaultBlockState(), ModBlocks.AZURITE_CLUSTER.get().defaultBlockState()),
                        BlockTags.FEATURES_CANNOT_REPLACE.getName(), BlockTags.GEODE_INVALID_BLOCKS.getName()), new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true, UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), -16, 16, 0.05D, 1)));
        CASSITERITE_GEODE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(IljaTech.MOD_ID, "cassiterite_geode_cf"), Feature.GEODE.configured(new GeodeConfiguration(
                new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
                        BlockStateProvider.simple(ModBlocks.CASSITERITE_BLOCK.get()),
                        BlockStateProvider.simple(ModBlocks.BUDDING_CASSITERITE_BLOCK.get()),
                        BlockStateProvider.simple(Blocks.CALCITE),
                        BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                        List.of(ModBlocks.CASSITERITE_SMALL_BUD.get().defaultBlockState(), ModBlocks.CASSITERITE_MEDIUM_BUD.get().defaultBlockState(), ModBlocks.CASSITERITE_LARGE_BUD.get().defaultBlockState(), ModBlocks.CASSITERITE_CLUSTER.get().defaultBlockState()),
                        BlockTags.FEATURES_CANNOT_REPLACE.getName(), BlockTags.GEODE_INVALID_BLOCKS.getName()), new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true, UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), -16, 16, 0.05D, 1)));
        RUBY_GEODE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(IljaTech.MOD_ID, "ruby_geode_cf"), Feature.GEODE.configured(new GeodeConfiguration(
                new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
                        BlockStateProvider.simple(ModBlocks.RUBY_BLOCK.get()),
                        BlockStateProvider.simple(ModBlocks.BUDDING_RUBY_BLOCK.get()),
                        BlockStateProvider.simple(Blocks.CALCITE),
                        BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                        List.of(ModBlocks.RUBY_SMALL_BUD.get().defaultBlockState(), ModBlocks.RUBY_MEDIUM_BUD.get().defaultBlockState(), ModBlocks.RUBY_LARGE_BUD.get().defaultBlockState(), ModBlocks.RUBY_CLUSTER.get().defaultBlockState()),
                        BlockTags.FEATURES_CANNOT_REPLACE.getName(), BlockTags.GEODE_INVALID_BLOCKS.getName()), new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true, UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), -16, 16, 0.05D, 1)));

        AZURITE_GEODE_PF = Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(IljaTech.MOD_ID, "azurite_geode_pf"), AZURITE_GEODE_CF.placed(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome()));
        CASSITERITE_GEODE_PF = Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(IljaTech.MOD_ID, "cassiterite_geode_pf"), CASSITERITE_GEODE_CF.placed(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome()));
        RUBY_GEODE_PF = Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(IljaTech.MOD_ID, "ruby_geode_pf"), RUBY_GEODE_CF.placed(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome()));
    }

    public static void addFeaturesToBiomes(final BiomeLoadingEvent event)
    {
        Biome.BiomeCategory category = event.getCategory();
        if(category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND && category != Biome.BiomeCategory.NONE) {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AZURITE_GEODE_PF);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, CASSITERITE_GEODE_PF);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RUBY_GEODE_PF);
        }
    }
}
