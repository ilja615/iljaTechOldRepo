package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BiomeProvider implements BiomeManager.IBiomeReader {
   public static final Codec<BiomeProvider> CODEC = Registry.BIOME_SOURCE.dispatchStable(BiomeProvider::codec, Function.identity());
   protected final Map<Structure<?>, Boolean> supportedStructures = Maps.newHashMap();
   protected final Set<BlockState> surfaceBlocks = Sets.newHashSet();
   protected final List<Biome> possibleBiomes;

   protected BiomeProvider(Stream<Supplier<Biome>> p_i241937_1_) {
      this(p_i241937_1_.map(Supplier::get).collect(ImmutableList.toImmutableList()));
   }

   protected BiomeProvider(List<Biome> p_i231634_1_) {
      this.possibleBiomes = p_i231634_1_;
   }

   protected abstract Codec<? extends BiomeProvider> codec();

   @OnlyIn(Dist.CLIENT)
   public abstract BiomeProvider withSeed(long p_230320_1_);

   public List<Biome> possibleBiomes() {
      return this.possibleBiomes;
   }

   public Set<Biome> getBiomesWithin(int p_225530_1_, int p_225530_2_, int p_225530_3_, int p_225530_4_) {
      int i = p_225530_1_ - p_225530_4_ >> 2;
      int j = p_225530_2_ - p_225530_4_ >> 2;
      int k = p_225530_3_ - p_225530_4_ >> 2;
      int l = p_225530_1_ + p_225530_4_ >> 2;
      int i1 = p_225530_2_ + p_225530_4_ >> 2;
      int j1 = p_225530_3_ + p_225530_4_ >> 2;
      int k1 = l - i + 1;
      int l1 = i1 - j + 1;
      int i2 = j1 - k + 1;
      Set<Biome> set = Sets.newHashSet();

      for(int j2 = 0; j2 < i2; ++j2) {
         for(int k2 = 0; k2 < k1; ++k2) {
            for(int l2 = 0; l2 < l1; ++l2) {
               int i3 = i + k2;
               int j3 = j + l2;
               int k3 = k + j2;
               set.add(this.getNoiseBiome(i3, j3, k3));
            }
         }
      }

      return set;
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int p_225531_1_, int p_225531_2_, int p_225531_3_, int p_225531_4_, Predicate<Biome> p_225531_5_, Random p_225531_6_) {
      return this.findBiomeHorizontal(p_225531_1_, p_225531_2_, p_225531_3_, p_225531_4_, 1, p_225531_5_, p_225531_6_, false);
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int p_230321_1_, int p_230321_2_, int p_230321_3_, int p_230321_4_, int p_230321_5_, Predicate<Biome> p_230321_6_, Random p_230321_7_, boolean p_230321_8_) {
      int i = p_230321_1_ >> 2;
      int j = p_230321_3_ >> 2;
      int k = p_230321_4_ >> 2;
      int l = p_230321_2_ >> 2;
      BlockPos blockpos = null;
      int i1 = 0;
      int j1 = p_230321_8_ ? 0 : k;

      for(int k1 = j1; k1 <= k; k1 += p_230321_5_) {
         for(int l1 = -k1; l1 <= k1; l1 += p_230321_5_) {
            boolean flag = Math.abs(l1) == k1;

            for(int i2 = -k1; i2 <= k1; i2 += p_230321_5_) {
               if (p_230321_8_) {
                  boolean flag1 = Math.abs(i2) == k1;
                  if (!flag1 && !flag) {
                     continue;
                  }
               }

               int k2 = i + i2;
               int j2 = j + l1;
               if (p_230321_6_.test(this.getNoiseBiome(k2, l, j2))) {
                  if (blockpos == null || p_230321_7_.nextInt(i1 + 1) == 0) {
                     blockpos = new BlockPos(k2 << 2, p_230321_2_, j2 << 2);
                     if (p_230321_8_) {
                        return blockpos;
                     }
                  }

                  ++i1;
               }
            }
         }
      }

      return blockpos;
   }

   public boolean canGenerateStructure(Structure<?> p_205004_1_) {
      return this.supportedStructures.computeIfAbsent(p_205004_1_, (p_226839_1_) -> {
         return this.possibleBiomes.stream().anyMatch((p_226838_1_) -> {
            return p_226838_1_.getGenerationSettings().isValidStart(p_226839_1_);
         });
      });
   }

   public Set<BlockState> getSurfaceBlocks() {
      if (this.surfaceBlocks.isEmpty()) {
         for(Biome biome : this.possibleBiomes) {
            this.surfaceBlocks.add(biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial());
         }
      }

      return this.surfaceBlocks;
   }

   static {
      Registry.register(Registry.BIOME_SOURCE, "fixed", SingleBiomeProvider.CODEC);
      Registry.register(Registry.BIOME_SOURCE, "multi_noise", NetherBiomeProvider.CODEC);
      Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeProvider.CODEC);
      Registry.register(Registry.BIOME_SOURCE, "vanilla_layered", OverworldBiomeProvider.CODEC);
      Registry.register(Registry.BIOME_SOURCE, "the_end", EndBiomeProvider.CODEC);
   }
}
