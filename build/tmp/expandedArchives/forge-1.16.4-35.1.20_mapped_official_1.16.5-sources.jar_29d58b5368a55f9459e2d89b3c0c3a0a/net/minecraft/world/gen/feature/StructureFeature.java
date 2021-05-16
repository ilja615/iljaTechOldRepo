package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

public class StructureFeature<FC extends IFeatureConfig, F extends Structure<FC>> {
   public static final Codec<StructureFeature<?, ?>> DIRECT_CODEC = Registry.STRUCTURE_FEATURE.dispatch((p_236271_0_) -> {
      return p_236271_0_.feature;
   }, Structure::configuredStructureCodec);
   public static final Codec<Supplier<StructureFeature<?, ?>>> CODEC = RegistryKeyCodec.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, DIRECT_CODEC);
   public static final Codec<List<Supplier<StructureFeature<?, ?>>>> LIST_CODEC = RegistryKeyCodec.homogeneousList(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, DIRECT_CODEC);
   public final F feature;
   public final FC config;

   public StructureFeature(F p_i231937_1_, FC p_i231937_2_) {
      this.feature = p_i231937_1_;
      this.config = p_i231937_2_;
   }

   public StructureStart<?> generate(DynamicRegistries p_242771_1_, ChunkGenerator p_242771_2_, BiomeProvider p_242771_3_, TemplateManager p_242771_4_, long p_242771_5_, ChunkPos p_242771_7_, Biome p_242771_8_, int p_242771_9_, StructureSeparationSettings p_242771_10_) {
      return this.feature.generate(p_242771_1_, p_242771_2_, p_242771_3_, p_242771_4_, p_242771_5_, p_242771_7_, p_242771_8_, p_242771_9_, new SharedSeedRandom(), p_242771_10_, this.config);
   }
}
