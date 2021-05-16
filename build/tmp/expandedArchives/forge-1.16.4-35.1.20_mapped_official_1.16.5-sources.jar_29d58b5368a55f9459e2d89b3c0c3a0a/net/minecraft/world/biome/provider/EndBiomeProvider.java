package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndBiomeProvider extends BiomeProvider {
   public static final Codec<EndBiomeProvider> CODEC = RecordCodecBuilder.create((p_242647_0_) -> {
      return p_242647_0_.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((p_242648_0_) -> {
         return p_242648_0_.biomes;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((p_242646_0_) -> {
         return p_242646_0_.seed;
      })).apply(p_242647_0_, p_242647_0_.stable(EndBiomeProvider::new));
   });
   private final SimplexNoiseGenerator islandNoise;
   private final Registry<Biome> biomes;
   private final long seed;
   private final Biome end;
   private final Biome highlands;
   private final Biome midlands;
   private final Biome islands;
   private final Biome barrens;

   public EndBiomeProvider(Registry<Biome> p_i241959_1_, long p_i241959_2_) {
      this(p_i241959_1_, p_i241959_2_, p_i241959_1_.getOrThrow(Biomes.THE_END), p_i241959_1_.getOrThrow(Biomes.END_HIGHLANDS), p_i241959_1_.getOrThrow(Biomes.END_MIDLANDS), p_i241959_1_.getOrThrow(Biomes.SMALL_END_ISLANDS), p_i241959_1_.getOrThrow(Biomes.END_BARRENS));
   }

   private EndBiomeProvider(Registry<Biome> p_i241960_1_, long p_i241960_2_, Biome p_i241960_4_, Biome p_i241960_5_, Biome p_i241960_6_, Biome p_i241960_7_, Biome p_i241960_8_) {
      super(ImmutableList.of(p_i241960_4_, p_i241960_5_, p_i241960_6_, p_i241960_7_, p_i241960_8_));
      this.biomes = p_i241960_1_;
      this.seed = p_i241960_2_;
      this.end = p_i241960_4_;
      this.highlands = p_i241960_5_;
      this.midlands = p_i241960_6_;
      this.islands = p_i241960_7_;
      this.barrens = p_i241960_8_;
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom(p_i241960_2_);
      sharedseedrandom.consumeCount(17292);
      this.islandNoise = new SimplexNoiseGenerator(sharedseedrandom);
   }

   protected Codec<? extends BiomeProvider> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public BiomeProvider withSeed(long p_230320_1_) {
      return new EndBiomeProvider(this.biomes, p_230320_1_, this.end, this.highlands, this.midlands, this.islands, this.barrens);
   }

   public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      int i = p_225526_1_ >> 2;
      int j = p_225526_3_ >> 2;
      if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
         return this.end;
      } else {
         float f = getHeightValue(this.islandNoise, i * 2 + 1, j * 2 + 1);
         if (f > 40.0F) {
            return this.highlands;
         } else if (f >= 0.0F) {
            return this.midlands;
         } else {
            return f < -20.0F ? this.islands : this.barrens;
         }
      }
   }

   public boolean stable(long p_235318_1_) {
      return this.seed == p_235318_1_;
   }

   public static float getHeightValue(SimplexNoiseGenerator p_235317_0_, int p_235317_1_, int p_235317_2_) {
      int i = p_235317_1_ / 2;
      int j = p_235317_2_ / 2;
      int k = p_235317_1_ % 2;
      int l = p_235317_2_ % 2;
      float f = 100.0F - MathHelper.sqrt((float)(p_235317_1_ * p_235317_1_ + p_235317_2_ * p_235317_2_)) * 8.0F;
      f = MathHelper.clamp(f, -100.0F, 80.0F);

      for(int i1 = -12; i1 <= 12; ++i1) {
         for(int j1 = -12; j1 <= 12; ++j1) {
            long k1 = (long)(i + i1);
            long l1 = (long)(j + j1);
            if (k1 * k1 + l1 * l1 > 4096L && p_235317_0_.getValue((double)k1, (double)l1) < (double)-0.9F) {
               float f1 = (MathHelper.abs((float)k1) * 3439.0F + MathHelper.abs((float)l1) * 147.0F) % 13.0F + 9.0F;
               float f2 = (float)(k - i1 * 2);
               float f3 = (float)(l - j1 * 2);
               float f4 = 100.0F - MathHelper.sqrt(f2 * f2 + f3 * f3) * f1;
               f4 = MathHelper.clamp(f4, -100.0F, 80.0F);
               f = Math.max(f, f4);
            }
         }
      }

      return f;
   }
}
