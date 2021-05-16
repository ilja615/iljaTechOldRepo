package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BiomeGeneratorTypeScreens {
   public static final BiomeGeneratorTypeScreens NORMAL = new BiomeGeneratorTypeScreens("default") {
      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new NoiseChunkGenerator(new OverworldBiomeProvider(p_241869_3_, false, false, p_241869_1_), p_241869_3_, () -> {
            return p_241869_2_.getOrThrow(DimensionSettings.OVERWORLD);
         });
      }
   };
   private static final BiomeGeneratorTypeScreens FLAT = new BiomeGeneratorTypeScreens("flat") {
      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new FlatChunkGenerator(FlatGenerationSettings.getDefault(p_241869_1_));
      }
   };
   private static final BiomeGeneratorTypeScreens LARGE_BIOMES = new BiomeGeneratorTypeScreens("large_biomes") {
      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new NoiseChunkGenerator(new OverworldBiomeProvider(p_241869_3_, false, true, p_241869_1_), p_241869_3_, () -> {
            return p_241869_2_.getOrThrow(DimensionSettings.OVERWORLD);
         });
      }
   };
   public static final BiomeGeneratorTypeScreens AMPLIFIED = new BiomeGeneratorTypeScreens("amplified") {
      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new NoiseChunkGenerator(new OverworldBiomeProvider(p_241869_3_, false, false, p_241869_1_), p_241869_3_, () -> {
            return p_241869_2_.getOrThrow(DimensionSettings.AMPLIFIED);
         });
      }
   };
   private static final BiomeGeneratorTypeScreens SINGLE_BIOME_SURFACE = new BiomeGeneratorTypeScreens("single_biome_surface") {
      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new NoiseChunkGenerator(new SingleBiomeProvider(p_241869_1_.getOrThrow(Biomes.PLAINS)), p_241869_3_, () -> {
            return p_241869_2_.getOrThrow(DimensionSettings.OVERWORLD);
         });
      }
   };
   private static final BiomeGeneratorTypeScreens SINGLE_BIOME_CAVES = new BiomeGeneratorTypeScreens("single_biome_caves") {
      public DimensionGeneratorSettings create(DynamicRegistries.Impl p_241220_1_, long p_241220_2_, boolean p_241220_4_, boolean p_241220_5_) {
         Registry<Biome> registry = p_241220_1_.registryOrThrow(Registry.BIOME_REGISTRY);
         Registry<DimensionType> registry1 = p_241220_1_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
         Registry<DimensionSettings> registry2 = p_241220_1_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
         return new DimensionGeneratorSettings(p_241220_2_, p_241220_4_, p_241220_5_, DimensionGeneratorSettings.withOverworld(DimensionType.defaultDimensions(registry1, registry, registry2, p_241220_2_), () -> {
            return registry1.getOrThrow(DimensionType.OVERWORLD_CAVES_LOCATION);
         }, this.generator(registry, registry2, p_241220_2_)));
      }

      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new NoiseChunkGenerator(new SingleBiomeProvider(p_241869_1_.getOrThrow(Biomes.PLAINS)), p_241869_3_, () -> {
            return p_241869_2_.getOrThrow(DimensionSettings.CAVES);
         });
      }
   };
   private static final BiomeGeneratorTypeScreens SINGLE_BIOME_FLOATING_ISLANDS = new BiomeGeneratorTypeScreens("single_biome_floating_islands") {
      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new NoiseChunkGenerator(new SingleBiomeProvider(p_241869_1_.getOrThrow(Biomes.PLAINS)), p_241869_3_, () -> {
            return p_241869_2_.getOrThrow(DimensionSettings.FLOATING_ISLANDS);
         });
      }
   };
   private static final BiomeGeneratorTypeScreens DEBUG = new BiomeGeneratorTypeScreens("debug_all_block_states") {
      protected ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_) {
         return new DebugChunkGenerator(p_241869_1_);
      }
   };
   protected static final List<BiomeGeneratorTypeScreens> PRESETS = Lists.newArrayList(NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, SINGLE_BIOME_CAVES, SINGLE_BIOME_FLOATING_ISLANDS, DEBUG);
   protected static final Map<Optional<BiomeGeneratorTypeScreens>, BiomeGeneratorTypeScreens.IFactory> EDITORS = ImmutableMap.of(Optional.of(FLAT), (p_239089_0_, p_239089_1_) -> {
      ChunkGenerator chunkgenerator = p_239089_1_.overworld();
      return new CreateFlatWorldScreen(p_239089_0_, (p_239083_2_) -> {
         p_239089_0_.worldGenSettingsComponent.updateSettings(new DimensionGeneratorSettings(p_239089_1_.seed(), p_239089_1_.generateFeatures(), p_239089_1_.generateBonusChest(), DimensionGeneratorSettings.withOverworld(p_239089_0_.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), p_239089_1_.dimensions(), new FlatChunkGenerator(p_239083_2_))));
      }, chunkgenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkgenerator).settings() : FlatGenerationSettings.getDefault(p_239089_0_.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY)));
   }, Optional.of(SINGLE_BIOME_SURFACE), (p_239087_0_, p_239087_1_) -> {
      return new CreateBuffetWorldScreen(p_239087_0_, p_239087_0_.worldGenSettingsComponent.registryHolder(), (p_239088_2_) -> {
         p_239087_0_.worldGenSettingsComponent.updateSettings(fromBuffetSettings(p_239087_0_.worldGenSettingsComponent.registryHolder(), p_239087_1_, SINGLE_BIOME_SURFACE, p_239088_2_));
      }, parseBuffetSettings(p_239087_0_.worldGenSettingsComponent.registryHolder(), p_239087_1_));
   }, Optional.of(SINGLE_BIOME_CAVES), (p_239085_0_, p_239085_1_) -> {
      return new CreateBuffetWorldScreen(p_239085_0_, p_239085_0_.worldGenSettingsComponent.registryHolder(), (p_239086_2_) -> {
         p_239085_0_.worldGenSettingsComponent.updateSettings(fromBuffetSettings(p_239085_0_.worldGenSettingsComponent.registryHolder(), p_239085_1_, SINGLE_BIOME_CAVES, p_239086_2_));
      }, parseBuffetSettings(p_239085_0_.worldGenSettingsComponent.registryHolder(), p_239085_1_));
   }, Optional.of(SINGLE_BIOME_FLOATING_ISLANDS), (p_239081_0_, p_239081_1_) -> {
      return new CreateBuffetWorldScreen(p_239081_0_, p_239081_0_.worldGenSettingsComponent.registryHolder(), (p_239082_2_) -> {
         p_239081_0_.worldGenSettingsComponent.updateSettings(fromBuffetSettings(p_239081_0_.worldGenSettingsComponent.registryHolder(), p_239081_1_, SINGLE_BIOME_FLOATING_ISLANDS, p_239082_2_));
      }, parseBuffetSettings(p_239081_0_.worldGenSettingsComponent.registryHolder(), p_239081_1_));
   });
   private final ITextComponent description;

   private BiomeGeneratorTypeScreens(String p_i232324_1_) {
      this.description = new TranslationTextComponent("generator." + p_i232324_1_);
   }
   public BiomeGeneratorTypeScreens(ITextComponent displayName) {
      this.description = displayName;
   }

   private static DimensionGeneratorSettings fromBuffetSettings(DynamicRegistries p_243452_0_, DimensionGeneratorSettings p_243452_1_, BiomeGeneratorTypeScreens p_243452_2_, Biome p_243452_3_) {
      BiomeProvider biomeprovider = new SingleBiomeProvider(p_243452_3_);
      Registry<DimensionType> registry = p_243452_0_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<DimensionSettings> registry1 = p_243452_0_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Supplier<DimensionSettings> supplier;
      if (p_243452_2_ == SINGLE_BIOME_CAVES) {
         supplier = () -> {
            return registry1.getOrThrow(DimensionSettings.CAVES);
         };
      } else if (p_243452_2_ == SINGLE_BIOME_FLOATING_ISLANDS) {
         supplier = () -> {
            return registry1.getOrThrow(DimensionSettings.FLOATING_ISLANDS);
         };
      } else {
         supplier = () -> {
            return registry1.getOrThrow(DimensionSettings.OVERWORLD);
         };
      }

      return new DimensionGeneratorSettings(p_243452_1_.seed(), p_243452_1_.generateFeatures(), p_243452_1_.generateBonusChest(), DimensionGeneratorSettings.withOverworld(registry, p_243452_1_.dimensions(), new NoiseChunkGenerator(biomeprovider, p_243452_1_.seed(), supplier)));
   }

   private static Biome parseBuffetSettings(DynamicRegistries p_243451_0_, DimensionGeneratorSettings p_243451_1_) {
      return p_243451_1_.overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(p_243451_0_.registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS));
   }

   public static Optional<BiomeGeneratorTypeScreens> of(DimensionGeneratorSettings p_239079_0_) {
      ChunkGenerator chunkgenerator = p_239079_0_.overworld();
      if (chunkgenerator instanceof FlatChunkGenerator) {
         return Optional.of(FLAT);
      } else {
         return chunkgenerator instanceof DebugChunkGenerator ? Optional.of(DEBUG) : Optional.empty();
      }
   }

   public ITextComponent description() {
      return this.description;
   }

   public DimensionGeneratorSettings create(DynamicRegistries.Impl p_241220_1_, long p_241220_2_, boolean p_241220_4_, boolean p_241220_5_) {
      Registry<Biome> registry = p_241220_1_.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<DimensionType> registry1 = p_241220_1_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<DimensionSettings> registry2 = p_241220_1_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      return new DimensionGeneratorSettings(p_241220_2_, p_241220_4_, p_241220_5_, DimensionGeneratorSettings.withOverworld(registry1, DimensionType.defaultDimensions(registry1, registry, registry2, p_241220_2_), this.generator(registry, registry2, p_241220_2_)));
   }

   protected abstract ChunkGenerator generator(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_);

   @OnlyIn(Dist.CLIENT)
   public interface IFactory {
      Screen createEditScreen(CreateWorldScreen p_createEditScreen_1_, DimensionGeneratorSettings p_createEditScreen_2_);
   }

   // Forge start
   // For internal use only, automatically called for all ForgeWorldTypes. Register your ForgeWorldType in the forge registry!
   public static void registerGenerator(BiomeGeneratorTypeScreens gen) { PRESETS.add(gen); }
}
