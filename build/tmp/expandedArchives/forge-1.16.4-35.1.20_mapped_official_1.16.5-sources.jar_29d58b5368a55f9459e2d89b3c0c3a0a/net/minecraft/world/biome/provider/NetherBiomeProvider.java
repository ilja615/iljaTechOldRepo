package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.MaxMinNoiseMixer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NetherBiomeProvider extends BiomeProvider {
   private static final NetherBiomeProvider.Noise DEFAULT_NOISE_PARAMETERS = new NetherBiomeProvider.Noise(-7, ImmutableList.of(1.0D, 1.0D));
   public static final MapCodec<NetherBiomeProvider> DIRECT_CODEC = RecordCodecBuilder.mapCodec((p_242602_0_) -> {
      return p_242602_0_.group(Codec.LONG.fieldOf("seed").forGetter((p_235286_0_) -> {
         return p_235286_0_.seed;
      }), RecordCodecBuilder.<Pair<Biome.Attributes, Supplier<Biome>>>create((p_235282_0_) -> {
         return p_235282_0_.group(Biome.Attributes.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(p_235282_0_, Pair::of);
      }).listOf().fieldOf("biomes").forGetter((p_235284_0_) -> {
         return p_235284_0_.parameters;
      }), NetherBiomeProvider.Noise.CODEC.fieldOf("temperature_noise").forGetter((p_242608_0_) -> {
         return p_242608_0_.temperatureParams;
      }), NetherBiomeProvider.Noise.CODEC.fieldOf("humidity_noise").forGetter((p_242607_0_) -> {
         return p_242607_0_.humidityParams;
      }), NetherBiomeProvider.Noise.CODEC.fieldOf("altitude_noise").forGetter((p_242606_0_) -> {
         return p_242606_0_.altitudeParams;
      }), NetherBiomeProvider.Noise.CODEC.fieldOf("weirdness_noise").forGetter((p_242604_0_) -> {
         return p_242604_0_.weirdnessParams;
      })).apply(p_242602_0_, NetherBiomeProvider::new);
   });
   public static final Codec<NetherBiomeProvider> CODEC = Codec.mapEither(NetherBiomeProvider.DefaultBuilder.CODEC, DIRECT_CODEC).xmap((p_235277_0_) -> {
      return p_235277_0_.map(NetherBiomeProvider.DefaultBuilder::biomeSource, Function.identity());
   }, (p_235275_0_) -> {
      return p_235275_0_.preset().map(Either::<NetherBiomeProvider.DefaultBuilder, NetherBiomeProvider>left).orElseGet(() -> {
         return Either.right(p_235275_0_);
      });
   }).codec();
   private final NetherBiomeProvider.Noise temperatureParams;
   private final NetherBiomeProvider.Noise humidityParams;
   private final NetherBiomeProvider.Noise altitudeParams;
   private final NetherBiomeProvider.Noise weirdnessParams;
   private final MaxMinNoiseMixer temperatureNoise;
   private final MaxMinNoiseMixer humidityNoise;
   private final MaxMinNoiseMixer altitudeNoise;
   private final MaxMinNoiseMixer weirdnessNoise;
   private final List<Pair<Biome.Attributes, Supplier<Biome>>> parameters;
   private final boolean useY;
   private final long seed;
   private final Optional<Pair<Registry<Biome>, NetherBiomeProvider.Preset>> preset;

   private NetherBiomeProvider(long p_i231640_1_, List<Pair<Biome.Attributes, Supplier<Biome>>> p_i231640_3_, Optional<Pair<Registry<Biome>, NetherBiomeProvider.Preset>> p_i231640_4_) {
      this(p_i231640_1_, p_i231640_3_, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, p_i231640_4_);
   }

   private NetherBiomeProvider(long p_i241951_1_, List<Pair<Biome.Attributes, Supplier<Biome>>> p_i241951_3_, NetherBiomeProvider.Noise p_i241951_4_, NetherBiomeProvider.Noise p_i241951_5_, NetherBiomeProvider.Noise p_i241951_6_, NetherBiomeProvider.Noise p_i241951_7_) {
      this(p_i241951_1_, p_i241951_3_, p_i241951_4_, p_i241951_5_, p_i241951_6_, p_i241951_7_, Optional.empty());
   }

   private NetherBiomeProvider(long p_i241952_1_, List<Pair<Biome.Attributes, Supplier<Biome>>> p_i241952_3_, NetherBiomeProvider.Noise p_i241952_4_, NetherBiomeProvider.Noise p_i241952_5_, NetherBiomeProvider.Noise p_i241952_6_, NetherBiomeProvider.Noise p_i241952_7_, Optional<Pair<Registry<Biome>, NetherBiomeProvider.Preset>> p_i241952_8_) {
      super(p_i241952_3_.stream().map(Pair::getSecond));
      this.seed = p_i241952_1_;
      this.preset = p_i241952_8_;
      this.temperatureParams = p_i241952_4_;
      this.humidityParams = p_i241952_5_;
      this.altitudeParams = p_i241952_6_;
      this.weirdnessParams = p_i241952_7_;
      this.temperatureNoise = MaxMinNoiseMixer.create(new SharedSeedRandom(p_i241952_1_), p_i241952_4_.firstOctave(), p_i241952_4_.amplitudes());
      this.humidityNoise = MaxMinNoiseMixer.create(new SharedSeedRandom(p_i241952_1_ + 1L), p_i241952_5_.firstOctave(), p_i241952_5_.amplitudes());
      this.altitudeNoise = MaxMinNoiseMixer.create(new SharedSeedRandom(p_i241952_1_ + 2L), p_i241952_6_.firstOctave(), p_i241952_6_.amplitudes());
      this.weirdnessNoise = MaxMinNoiseMixer.create(new SharedSeedRandom(p_i241952_1_ + 3L), p_i241952_7_.firstOctave(), p_i241952_7_.amplitudes());
      this.parameters = p_i241952_3_;
      this.useY = false;
   }

   protected Codec<? extends BiomeProvider> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public BiomeProvider withSeed(long p_230320_1_) {
      return new NetherBiomeProvider(p_230320_1_, this.parameters, this.temperatureParams, this.humidityParams, this.altitudeParams, this.weirdnessParams, this.preset);
   }

   private Optional<NetherBiomeProvider.DefaultBuilder> preset() {
      return this.preset.map((p_242601_1_) -> {
         return new NetherBiomeProvider.DefaultBuilder(p_242601_1_.getSecond(), p_242601_1_.getFirst(), this.seed);
      });
   }

   public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      int i = this.useY ? p_225526_2_ : 0;
      Biome.Attributes biome$attributes = new Biome.Attributes((float)this.temperatureNoise.getValue((double)p_225526_1_, (double)i, (double)p_225526_3_), (float)this.humidityNoise.getValue((double)p_225526_1_, (double)i, (double)p_225526_3_), (float)this.altitudeNoise.getValue((double)p_225526_1_, (double)i, (double)p_225526_3_), (float)this.weirdnessNoise.getValue((double)p_225526_1_, (double)i, (double)p_225526_3_), 0.0F);
      return this.parameters.stream().min(Comparator.comparing((p_235272_1_) -> {
         return p_235272_1_.getFirst().fitness(biome$attributes);
      })).map(Pair::getSecond).map(Supplier::get).orElse(BiomeRegistry.THE_VOID);
   }

   public boolean stable(long p_235280_1_) {
      return this.seed == p_235280_1_ && this.preset.isPresent() && Objects.equals(this.preset.get().getSecond(), NetherBiomeProvider.Preset.NETHER);
   }

   static final class DefaultBuilder {
      public static final MapCodec<NetherBiomeProvider.DefaultBuilder> CODEC = RecordCodecBuilder.mapCodec((p_242630_0_) -> {
         return p_242630_0_.group(ResourceLocation.CODEC.flatXmap((p_242631_0_) -> {
            return Optional.ofNullable(NetherBiomeProvider.Preset.BY_NAME.get(p_242631_0_)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error("Unknown preset: " + p_242631_0_);
            });
         }, (p_242629_0_) -> {
            return DataResult.success(p_242629_0_.name);
         }).fieldOf("preset").stable().forGetter(NetherBiomeProvider.DefaultBuilder::preset), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(NetherBiomeProvider.DefaultBuilder::biomes), Codec.LONG.fieldOf("seed").stable().forGetter(NetherBiomeProvider.DefaultBuilder::seed)).apply(p_242630_0_, p_242630_0_.stable(NetherBiomeProvider.DefaultBuilder::new));
      });
      private final NetherBiomeProvider.Preset preset;
      private final Registry<Biome> biomes;
      private final long seed;

      private DefaultBuilder(NetherBiomeProvider.Preset p_i241956_1_, Registry<Biome> p_i241956_2_, long p_i241956_3_) {
         this.preset = p_i241956_1_;
         this.biomes = p_i241956_2_;
         this.seed = p_i241956_3_;
      }

      public NetherBiomeProvider.Preset preset() {
         return this.preset;
      }

      public Registry<Biome> biomes() {
         return this.biomes;
      }

      public long seed() {
         return this.seed;
      }

      public NetherBiomeProvider biomeSource() {
         return this.preset.biomeSource(this.biomes, this.seed);
      }
   }

   static class Noise {
      private final int firstOctave;
      private final DoubleList amplitudes;
      public static final Codec<NetherBiomeProvider.Noise> CODEC = RecordCodecBuilder.create((p_242613_0_) -> {
         return p_242613_0_.group(Codec.INT.fieldOf("firstOctave").forGetter(NetherBiomeProvider.Noise::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NetherBiomeProvider.Noise::amplitudes)).apply(p_242613_0_, NetherBiomeProvider.Noise::new);
      });

      public Noise(int p_i241954_1_, List<Double> p_i241954_2_) {
         this.firstOctave = p_i241954_1_;
         this.amplitudes = new DoubleArrayList(p_i241954_2_);
      }

      public int firstOctave() {
         return this.firstOctave;
      }

      public DoubleList amplitudes() {
         return this.amplitudes;
      }
   }

   public static class Preset {
      private static final Map<ResourceLocation, NetherBiomeProvider.Preset> BY_NAME = Maps.newHashMap();
      public static final NetherBiomeProvider.Preset NETHER = new NetherBiomeProvider.Preset(new ResourceLocation("nether"), (p_242617_0_, p_242617_1_, p_242617_2_) -> {
         return new NetherBiomeProvider(p_242617_2_, ImmutableList.of(Pair.of(new Biome.Attributes(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return p_242617_1_.getOrThrow(Biomes.NETHER_WASTES);
         }), Pair.of(new Biome.Attributes(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), () -> {
            return p_242617_1_.getOrThrow(Biomes.SOUL_SAND_VALLEY);
         }), Pair.of(new Biome.Attributes(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return p_242617_1_.getOrThrow(Biomes.CRIMSON_FOREST);
         }), Pair.of(new Biome.Attributes(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), () -> {
            return p_242617_1_.getOrThrow(Biomes.WARPED_FOREST);
         }), Pair.of(new Biome.Attributes(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
            return p_242617_1_.getOrThrow(Biomes.BASALT_DELTAS);
         })), Optional.of(Pair.of(p_242617_1_, p_242617_0_)));
      });
      private final ResourceLocation name;
      private final Function3<NetherBiomeProvider.Preset, Registry<Biome>, Long, NetherBiomeProvider> biomeSource;

      public Preset(ResourceLocation p_i241955_1_, Function3<NetherBiomeProvider.Preset, Registry<Biome>, Long, NetherBiomeProvider> p_i241955_2_) {
         this.name = p_i241955_1_;
         this.biomeSource = p_i241955_2_;
         BY_NAME.put(p_i241955_1_, this);
      }

      public NetherBiomeProvider biomeSource(Registry<Biome> p_242619_1_, long p_242619_2_) {
         return this.biomeSource.apply(this, p_242619_1_, p_242619_2_);
      }
   }
}
