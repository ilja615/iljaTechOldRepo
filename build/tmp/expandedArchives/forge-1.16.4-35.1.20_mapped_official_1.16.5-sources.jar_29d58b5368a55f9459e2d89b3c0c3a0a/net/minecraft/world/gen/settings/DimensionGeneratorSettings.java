package net.minecraft.world.gen.settings;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionGeneratorSettings {
   public static final Codec<DimensionGeneratorSettings> CODEC = RecordCodecBuilder.<DimensionGeneratorSettings>create((p_236214_0_) -> {
      return p_236214_0_.group(Codec.LONG.fieldOf("seed").stable().forGetter(DimensionGeneratorSettings::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(DimensionGeneratorSettings::generateFeatures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(DimensionGeneratorSettings::generateBonusChest), SimpleRegistry.dataPackCodec(Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), Dimension.CODEC).xmap(Dimension::sortMap, Function.identity()).fieldOf("dimensions").forGetter(DimensionGeneratorSettings::dimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((p_236213_0_) -> {
         return p_236213_0_.legacyCustomOptions;
      })).apply(p_236214_0_, p_236214_0_.stable(DimensionGeneratorSettings::new));
   }).comapFlatMap(DimensionGeneratorSettings::guardExperimental, Function.identity());
   private static final Logger LOGGER = LogManager.getLogger();
   private final long seed;
   private final boolean generateFeatures;
   private final boolean generateBonusChest;
   private final SimpleRegistry<Dimension> dimensions;
   private final Optional<String> legacyCustomOptions;

   private DataResult<DimensionGeneratorSettings> guardExperimental() {
      Dimension dimension = this.dimensions.get(Dimension.OVERWORLD);
      if (dimension == null) {
         return DataResult.error("Overworld settings missing");
      } else {
         return this.stable() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this);
      }
   }

   private boolean stable() {
      return Dimension.stable(this.seed, this.dimensions);
   }

   public DimensionGeneratorSettings(long p_i231914_1_, boolean p_i231914_3_, boolean p_i231914_4_, SimpleRegistry<Dimension> p_i231914_5_) {
      this(p_i231914_1_, p_i231914_3_, p_i231914_4_, p_i231914_5_, Optional.empty());
      Dimension dimension = p_i231914_5_.get(Dimension.OVERWORLD);
      if (dimension == null) {
         throw new IllegalStateException("Overworld settings missing");
      }
   }

   private DimensionGeneratorSettings(long p_i231915_1_, boolean p_i231915_3_, boolean p_i231915_4_, SimpleRegistry<Dimension> p_i231915_5_, Optional<String> p_i231915_6_) {
      this.seed = p_i231915_1_;
      this.generateFeatures = p_i231915_3_;
      this.generateBonusChest = p_i231915_4_;
      this.dimensions = p_i231915_5_;
      this.legacyCustomOptions = p_i231915_6_;
   }

   public static DimensionGeneratorSettings demoSettings(DynamicRegistries p_242752_0_) {
      Registry<Biome> registry = p_242752_0_.registryOrThrow(Registry.BIOME_REGISTRY);
      int i = "North Carolina".hashCode();
      Registry<DimensionType> registry1 = p_242752_0_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<DimensionSettings> registry2 = p_242752_0_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      return new DimensionGeneratorSettings((long)i, true, true, withOverworld(registry1, DimensionType.defaultDimensions(registry1, registry, registry2, (long)i), makeDefaultOverworld(registry, registry2, (long)i)));
   }

   public static DimensionGeneratorSettings makeDefault(Registry<DimensionType> p_242751_0_, Registry<Biome> p_242751_1_, Registry<DimensionSettings> p_242751_2_) {
      long i = (new Random()).nextLong();
      return new DimensionGeneratorSettings(i, true, false, withOverworld(p_242751_0_, DimensionType.defaultDimensions(p_242751_0_, p_242751_1_, p_242751_2_, i), makeDefaultOverworld(p_242751_1_, p_242751_2_, i)));
   }

   public static NoiseChunkGenerator makeDefaultOverworld(Registry<Biome> p_242750_0_, Registry<DimensionSettings> p_242750_1_, long p_242750_2_) {
      return new NoiseChunkGenerator(new OverworldBiomeProvider(p_242750_2_, false, false, p_242750_0_), p_242750_2_, () -> {
         return p_242750_1_.getOrThrow(DimensionSettings.OVERWORLD);
      });
   }

   public long seed() {
      return this.seed;
   }

   public boolean generateFeatures() {
      return this.generateFeatures;
   }

   public boolean generateBonusChest() {
      return this.generateBonusChest;
   }

   public static SimpleRegistry<Dimension> withOverworld(Registry<DimensionType> p_242749_0_, SimpleRegistry<Dimension> p_242749_1_, ChunkGenerator p_242749_2_) {
      Dimension dimension = p_242749_1_.get(Dimension.OVERWORLD);
      Supplier<DimensionType> supplier = () -> {
         return dimension == null ? p_242749_0_.getOrThrow(DimensionType.OVERWORLD_LOCATION) : dimension.type();
      };
      return withOverworld(p_242749_1_, supplier, p_242749_2_);
   }

   public static SimpleRegistry<Dimension> withOverworld(SimpleRegistry<Dimension> p_241520_0_, Supplier<DimensionType> p_241520_1_, ChunkGenerator p_241520_2_) {
      SimpleRegistry<Dimension> simpleregistry = new SimpleRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
      simpleregistry.register(Dimension.OVERWORLD, new Dimension(p_241520_1_, p_241520_2_), Lifecycle.stable());

      for(Entry<RegistryKey<Dimension>, Dimension> entry : p_241520_0_.entrySet()) {
         RegistryKey<Dimension> registrykey = entry.getKey();
         if (registrykey != Dimension.OVERWORLD) {
            simpleregistry.register(registrykey, entry.getValue(), p_241520_0_.lifecycle(entry.getValue()));
         }
      }

      return simpleregistry;
   }

   public SimpleRegistry<Dimension> dimensions() {
      return this.dimensions;
   }

   public ChunkGenerator overworld() {
      Dimension dimension = this.dimensions.get(Dimension.OVERWORLD);
      if (dimension == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return dimension.generator();
      }
   }

   public ImmutableSet<RegistryKey<World>> levels() {
      return this.dimensions().entrySet().stream().map((p_236218_0_) -> {
         return RegistryKey.create(Registry.DIMENSION_REGISTRY, p_236218_0_.getKey().location());
      }).collect(ImmutableSet.toImmutableSet());
   }

   public boolean isDebug() {
      return this.overworld() instanceof DebugChunkGenerator;
   }

   public boolean isFlatWorld() {
      return this.overworld() instanceof FlatChunkGenerator;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOldCustomizedWorld() {
      return this.legacyCustomOptions.isPresent();
   }

   public DimensionGeneratorSettings withBonusChest() {
      return new DimensionGeneratorSettings(this.seed, this.generateFeatures, true, this.dimensions, this.legacyCustomOptions);
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionGeneratorSettings withFeaturesToggled() {
      return new DimensionGeneratorSettings(this.seed, !this.generateFeatures, this.generateBonusChest, this.dimensions);
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionGeneratorSettings withBonusChestToggled() {
      return new DimensionGeneratorSettings(this.seed, this.generateFeatures, !this.generateBonusChest, this.dimensions);
   }

   public static DimensionGeneratorSettings create(DynamicRegistries p_242753_0_, Properties p_242753_1_) {
      String s = MoreObjects.firstNonNull((String)p_242753_1_.get("generator-settings"), "");
      p_242753_1_.put("generator-settings", s);
      String s1 = MoreObjects.firstNonNull((String)p_242753_1_.get("level-seed"), "");
      p_242753_1_.put("level-seed", s1);
      String s2 = (String)p_242753_1_.get("generate-structures");
      boolean flag = s2 == null || Boolean.parseBoolean(s2);
      p_242753_1_.put("generate-structures", Objects.toString(flag));
      String s3 = (String)p_242753_1_.get("level-type");
      String s4 = Optional.ofNullable(s3).map((p_236217_0_) -> {
         return p_236217_0_.toLowerCase(Locale.ROOT);
      }).orElseGet(net.minecraftforge.common.ForgeHooks::getDefaultWorldType);
      p_242753_1_.put("level-type", s4);
      long i = (new Random()).nextLong();
      if (!s1.isEmpty()) {
         try {
            long j = Long.parseLong(s1);
            if (j != 0L) {
               i = j;
            }
         } catch (NumberFormatException numberformatexception) {
            i = (long)s1.hashCode();
         }
      }

      Registry<DimensionType> registry2 = p_242753_0_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<Biome> registry = p_242753_0_.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<DimensionSettings> registry1 = p_242753_0_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      SimpleRegistry<Dimension> simpleregistry = DimensionType.defaultDimensions(registry2, registry, registry1, i);
      net.minecraftforge.common.world.ForgeWorldType type = net.minecraftforge.registries.ForgeRegistries.WORLD_TYPES.getValue(new net.minecraft.util.ResourceLocation(s4));
      if (type != null) return type.createSettings(p_242753_0_, i, flag, false, s);
      switch(s4) {
      case "flat":
         JsonObject jsonobject = !s.isEmpty() ? JSONUtils.parse(s) : new JsonObject();
         Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, jsonobject);
         return new DimensionGeneratorSettings(i, flag, false, withOverworld(registry2, simpleregistry, new FlatChunkGenerator(FlatGenerationSettings.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElseGet(() -> {
            return FlatGenerationSettings.getDefault(registry);
         }))));
      case "debug_all_block_states":
         return new DimensionGeneratorSettings(i, flag, false, withOverworld(registry2, simpleregistry, new DebugChunkGenerator(registry)));
      case "amplified":
         return new DimensionGeneratorSettings(i, flag, false, withOverworld(registry2, simpleregistry, new NoiseChunkGenerator(new OverworldBiomeProvider(i, false, false, registry), i, () -> {
            return registry1.getOrThrow(DimensionSettings.AMPLIFIED);
         })));
      case "largebiomes":
         return new DimensionGeneratorSettings(i, flag, false, withOverworld(registry2, simpleregistry, new NoiseChunkGenerator(new OverworldBiomeProvider(i, false, true, registry), i, () -> {
            return registry1.getOrThrow(DimensionSettings.OVERWORLD);
         })));
      default:
         return new DimensionGeneratorSettings(i, flag, false, withOverworld(registry2, simpleregistry, makeDefaultOverworld(registry, registry1, i)));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionGeneratorSettings withSeed(boolean p_236220_1_, OptionalLong p_236220_2_) {
      long i = p_236220_2_.orElse(this.seed);
      SimpleRegistry<Dimension> simpleregistry;
      if (p_236220_2_.isPresent()) {
         simpleregistry = new SimpleRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
         long j = p_236220_2_.getAsLong();

         for(Entry<RegistryKey<Dimension>, Dimension> entry : this.dimensions.entrySet()) {
            RegistryKey<Dimension> registrykey = entry.getKey();
            simpleregistry.register(registrykey, new Dimension(entry.getValue().typeSupplier(), entry.getValue().generator().withSeed(j)), this.dimensions.lifecycle(entry.getValue()));
         }
      } else {
         simpleregistry = this.dimensions;
      }

      DimensionGeneratorSettings dimensiongeneratorsettings;
      if (this.isDebug()) {
         dimensiongeneratorsettings = new DimensionGeneratorSettings(i, false, false, simpleregistry);
      } else {
         dimensiongeneratorsettings = new DimensionGeneratorSettings(i, this.generateFeatures(), this.generateBonusChest() && !p_236220_1_, simpleregistry);
      }

      return dimensiongeneratorsettings;
   }
}
