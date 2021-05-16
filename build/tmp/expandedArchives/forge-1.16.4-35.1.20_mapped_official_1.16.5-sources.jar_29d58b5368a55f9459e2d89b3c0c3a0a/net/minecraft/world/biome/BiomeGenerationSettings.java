package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeGenerationSettings {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(() -> {
      return ConfiguredSurfaceBuilders.NOPE;
   }, ImmutableMap.of(), ImmutableList.of(), ImmutableList.of());
   public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec((p_242495_0_) -> {
      return p_242495_0_.group(ConfiguredSurfaceBuilder.CODEC.fieldOf("surface_builder").forGetter((p_242501_0_) -> {
         return p_242501_0_.surfaceBuilder;
      }), Codec.simpleMap(GenerationStage.Carving.CODEC, ConfiguredCarver.LIST_CODEC.promotePartial(Util.prefix("Carver: ", LOGGER::error)), IStringSerializable.keys(GenerationStage.Carving.values())).fieldOf("carvers").forGetter((p_242499_0_) -> {
         return p_242499_0_.carvers;
      }), ConfiguredFeature.LIST_CODEC.promotePartial(Util.prefix("Feature: ", LOGGER::error)).listOf().fieldOf("features").forGetter((p_242497_0_) -> {
         return p_242497_0_.features;
      }), StructureFeature.LIST_CODEC.promotePartial(Util.prefix("Structure start: ", LOGGER::error)).fieldOf("starts").forGetter((p_242488_0_) -> {
         return p_242488_0_.structureStarts;
      })).apply(p_242495_0_, BiomeGenerationSettings::new);
   });
   private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
   private final Map<GenerationStage.Carving, List<Supplier<ConfiguredCarver<?>>>> carvers;
   private final java.util.Set<GenerationStage.Carving> carversView;
   private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
   private final List<Supplier<StructureFeature<?, ?>>> structureStarts;
   private final List<ConfiguredFeature<?, ?>> flowerFeatures;

   private BiomeGenerationSettings(Supplier<ConfiguredSurfaceBuilder<?>> p_i241935_1_, Map<GenerationStage.Carving, List<Supplier<ConfiguredCarver<?>>>> p_i241935_2_, List<List<Supplier<ConfiguredFeature<?, ?>>>> p_i241935_3_, List<Supplier<StructureFeature<?, ?>>> p_i241935_4_) {
      this.surfaceBuilder = p_i241935_1_;
      this.carvers = p_i241935_2_;
      this.features = p_i241935_3_;
      this.structureStarts = p_i241935_4_;
      this.flowerFeatures = p_i241935_3_.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(ConfiguredFeature::getFeatures).filter((p_242490_0_) -> {
         return p_242490_0_.feature == Feature.FLOWER;
      }).collect(ImmutableList.toImmutableList());
      this.carversView = java.util.Collections.unmodifiableSet(carvers.keySet());
   }

   public List<Supplier<ConfiguredCarver<?>>> getCarvers(GenerationStage.Carving p_242489_1_) {
      return this.carvers.getOrDefault(p_242489_1_, ImmutableList.of());
   }

   public java.util.Set<GenerationStage.Carving> getCarvingStages() {
       return this.carversView;
   }

   public boolean isValidStart(Structure<?> p_242493_1_) {
      return this.structureStarts.stream().anyMatch((p_242494_1_) -> {
         return (p_242494_1_.get()).feature == p_242493_1_;
      });
   }

   public Collection<Supplier<StructureFeature<?, ?>>> structures() {
      return this.structureStarts;
   }

   public StructureFeature<?, ?> withBiomeConfig(StructureFeature<?, ?> p_242491_1_) {
      return DataFixUtils.orElse(this.structureStarts.stream().map(Supplier::get).filter((p_242492_1_) -> {
         return p_242492_1_.feature == p_242491_1_.feature;
      }).findAny(), p_242491_1_);
   }

   public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
      return this.flowerFeatures;
   }

   public List<List<Supplier<ConfiguredFeature<?, ?>>>> features() {
      return this.features;
   }

   public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
      return this.surfaceBuilder;
   }

   public ISurfaceBuilderConfig getSurfaceBuilderConfig() {
      return this.surfaceBuilder.get().config();
   }

   public static class Builder {
      protected Optional<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder = Optional.empty();
      protected final Map<GenerationStage.Carving, List<Supplier<ConfiguredCarver<?>>>> carvers = Maps.newLinkedHashMap();
      protected final List<List<Supplier<ConfiguredFeature<?, ?>>>> features = Lists.newArrayList();
      protected final List<Supplier<StructureFeature<?, ?>>> structureStarts = Lists.newArrayList();

      public BiomeGenerationSettings.Builder surfaceBuilder(ConfiguredSurfaceBuilder<?> p_242517_1_) {
         return this.surfaceBuilder(() -> {
            return p_242517_1_;
         });
      }

      public BiomeGenerationSettings.Builder surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> p_242519_1_) {
         this.surfaceBuilder = Optional.of(p_242519_1_);
         return this;
      }

      public BiomeGenerationSettings.Builder addFeature(GenerationStage.Decoration p_242513_1_, ConfiguredFeature<?, ?> p_242513_2_) {
         return this.addFeature(p_242513_1_.ordinal(), () -> {
            return p_242513_2_;
         });
      }

      public BiomeGenerationSettings.Builder addFeature(int p_242510_1_, Supplier<ConfiguredFeature<?, ?>> p_242510_2_) {
         this.addFeatureStepsUpTo(p_242510_1_);
         this.features.get(p_242510_1_).add(p_242510_2_);
         return this;
      }

      public <C extends ICarverConfig> BiomeGenerationSettings.Builder addCarver(GenerationStage.Carving p_242512_1_, ConfiguredCarver<C> p_242512_2_) {
         this.carvers.computeIfAbsent(p_242512_1_, (p_242511_0_) -> {
            return Lists.newArrayList();
         }).add(() -> {
            return p_242512_2_;
         });
         return this;
      }

      public BiomeGenerationSettings.Builder addStructureStart(StructureFeature<?, ?> p_242516_1_) {
         this.structureStarts.add(() -> {
            return p_242516_1_;
         });
         return this;
      }

      protected void addFeatureStepsUpTo(int p_242509_1_) {
         while(this.features.size() <= p_242509_1_) {
            this.features.add(Lists.newArrayList());
         }

      }

      public BiomeGenerationSettings build() {
         return new BiomeGenerationSettings(this.surfaceBuilder.orElseThrow(() -> {
            return new IllegalStateException("Missing surface builder");
         }), this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_242518_0_) -> {
            return ImmutableList.copyOf((Collection)p_242518_0_.getValue());
         })), this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()), ImmutableList.copyOf(this.structureStarts));
      }
   }
}
