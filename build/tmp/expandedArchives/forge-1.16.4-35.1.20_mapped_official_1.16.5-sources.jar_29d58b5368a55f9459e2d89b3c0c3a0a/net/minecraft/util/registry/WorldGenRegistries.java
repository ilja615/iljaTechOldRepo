package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegistries {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   private static final MutableRegistry<MutableRegistry<?>> WRITABLE_REGISTRY = new SimpleRegistry<>(RegistryKey.createRegistryKey(new ResourceLocation("root")), Lifecycle.experimental());
   public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;
   public static final Registry<ConfiguredSurfaceBuilder<?>> CONFIGURED_SURFACE_BUILDER = registerSimple(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, () -> {
      return ConfiguredSurfaceBuilders.NOPE;
   });
   public static final Registry<ConfiguredCarver<?>> CONFIGURED_CARVER = registerSimple(Registry.CONFIGURED_CARVER_REGISTRY, () -> {
      return ConfiguredCarvers.CAVE;
   });
   public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = registerSimple(Registry.CONFIGURED_FEATURE_REGISTRY, () -> {
      return Features.OAK;
   });
   public static final Registry<StructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = registerSimple(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, () -> {
      return StructureFeatures.MINESHAFT;
   });
   public static final Registry<StructureProcessorList> PROCESSOR_LIST = registerSimple(Registry.PROCESSOR_LIST_REGISTRY, () -> {
      return ProcessorLists.ZOMBIE_PLAINS;
   });
   public static final Registry<JigsawPattern> TEMPLATE_POOL = registerSimple(Registry.TEMPLATE_POOL_REGISTRY, JigsawPatternRegistry::bootstrap);
   @Deprecated public static final Registry<Biome> BIOME = forge(Registry.BIOME_REGISTRY, () -> {
      return BiomeRegistry.PLAINS;
   });
   public static final Registry<DimensionSettings> NOISE_GENERATOR_SETTINGS = registerSimple(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, DimensionSettings::bootstrap);

   private static <T> Registry<T> registerSimple(RegistryKey<? extends Registry<T>> p_243667_0_, Supplier<T> p_243667_1_) {
      return registerSimple(p_243667_0_, Lifecycle.stable(), p_243667_1_);
   }

   private static <T extends net.minecraftforge.registries.IForgeRegistryEntry<T>> Registry<T> forge(RegistryKey<? extends Registry<T>> key, Supplier<T> def) {
      return internalRegister(key, net.minecraftforge.registries.GameData.getWrapper(key, Lifecycle.stable()), def, Lifecycle.stable());
   }

   private static <T> Registry<T> registerSimple(RegistryKey<? extends Registry<T>> p_243665_0_, Lifecycle p_243665_1_, Supplier<T> p_243665_2_) {
      return internalRegister(p_243665_0_, new SimpleRegistry<>(p_243665_0_, p_243665_1_), p_243665_2_, p_243665_1_);
   }

   private static <T, R extends MutableRegistry<T>> R internalRegister(RegistryKey<? extends Registry<T>> p_243666_0_, R p_243666_1_, Supplier<T> p_243666_2_, Lifecycle p_243666_3_) {
      ResourceLocation resourcelocation = p_243666_0_.location();
      LOADERS.put(resourcelocation, p_243666_2_);
      MutableRegistry<R> mutableregistry = (MutableRegistry<R>)WRITABLE_REGISTRY;
      return (R)mutableregistry.register((RegistryKey)p_243666_0_, p_243666_1_, p_243666_3_);
   }

   public static <T> T register(Registry<? super T> p_243663_0_, String p_243663_1_, T p_243663_2_) {
      return register(p_243663_0_, new ResourceLocation(p_243663_1_), p_243663_2_);
   }

   public static <V, T extends V> T register(Registry<V> p_243664_0_, ResourceLocation p_243664_1_, T p_243664_2_) {
      return ((MutableRegistry<V>)p_243664_0_).register(RegistryKey.create(p_243664_0_.key(), p_243664_1_), p_243664_2_, Lifecycle.stable());
   }

   public static <V, T extends V> T registerMapping(Registry<V> p_243662_0_, int p_243662_1_, RegistryKey<V> p_243662_2_, T p_243662_3_) {
      return ((MutableRegistry<V>)p_243662_0_).registerMapping(p_243662_1_, p_243662_2_, p_243662_3_, Lifecycle.stable());
   }

   public static void bootstrap() {
   }

   static {
      LOADERS.forEach((p_243668_0_, p_243668_1_) -> {
         if (p_243668_1_.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", (Object)p_243668_0_);
         }

      });
      Registry.checkRegistry(WRITABLE_REGISTRY);
   }
}
